package nemofrl.balloonRobot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.NotYetConnectedException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import nemofrl.balloonRobot.App;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.exception.QQException;
import nemofrl.balloonRobot.service.DstLog;
import nemofrl.balloonRobot.service.Robot;

public class ServerUtil {
	private static final Logger logger = LogManager.getLogger(ServerUtil.class);


	public static String shServer(String source, String command,User user) throws QQException {

		logger.info("exc shell command:" + command);
		try {
			Connection conn=loginLinuxServer(user);
			Session session = conn.openSession();// 打开一个会话
			session.execCommand(command);
			String result = processStdout(session.getStdout(), "UTF-8");
			if(StringUtils.isBlank(result))
				result = processStdout(session.getStderr(), "UTF-8");
			if (StringUtils.isNotBlank(result))
				result = result.substring(0, result.length() - 1);
			session.close();
			conn.close();
			return result;
		} catch (IOException e) {
			throw new QQException("执行服务器命令失败", e);
		} 

	}

	public static void sjServer(Timer timer,final String path,final String source,final User user) {
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		user.setTimer(timer);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					String sendLog = null;
					Connection conn=loginLinuxServer(user);
					Session session = conn.openSession();// 打开一个会话
					session.execCommand("cd " + path + "&&tail -n 1 server_chat_log.txt");// 执行命令
					String log = processStdout(session.getStdout(), "UTF-8");
					session.close();
					conn.close();
					if(StringUtils.isBlank(log)) {
						return;
					}
					log = log.substring(0, log.length() - 1);
					String lastLog=user.getLastLog();
					if (lastLog == null || !lastLog.equals(log)) {
						sendLog = log;
						user.setLastLog(log);
					}
					if (StringUtils.isNotBlank(sendLog)) {
						MessageUtil.sendMessage(source, sendLog);
						String name=sendLog.split(" ")[3];
						if(sendLog.contains("[Death Announcement]")) {
							new DstLog("death",name).start();
						}
						if(sendLog.contains("[Join Announcement]")) {
							new DstLog("join",name).start();
						}
					}
				} catch (NotYetConnectedException e) {
					logger.error("NotYetConnectedException", e);
					MessageUtil.sendMessage(source, e.getMessage());
				} catch (IOException e) {
					logger.error("命令执行失败", e);
					MessageUtil.sendMessage(source, "命令执行失败");
					this.cancel();
				} catch (QQException e) {
					if(e.getE()!=null)
						logger.error(e.getMsg(), e.getE());
					else logger.error(e.getMsg());
					MessageUtil.sendMessage(source, e.getMsg());
				}
			}

		}, 0, 1000);

	}

	public static String processStdout(InputStream in, String charset) {
		InputStream stdout = new StreamGobbler(in);
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\n");
			}
			br.close();
			stdout.close();
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return buffer.toString();
	}

	public static Connection loginLinuxServer(User user) throws  QQException {
		
		String ip=user.getServerIp();
		String passwd=user.getServerPassword();
		String userName=user.getServerUserName();
		int port=user.getPort();
		Connection conn = new Connection(ip,port);
		
		boolean flag=false;
		try {
			conn.connect();// 连接
			flag = conn.authenticateWithPassword(userName, passwd);// 认证
		} catch (Exception e) {
			throw new QQException("连接服务器失败", e);
		}
		if (!flag) {
			throw new QQException("认证服务器失败");
		}
		return conn;
	}
	public static void initServer(String source,String config) throws QQException {
		String qq=PermissionUtil.getQQ(source);
		User user=App.userMap.get(qq);
		if(user==null) {
			Gson gson=new Gson();
			try {
				config=config.replace("&#91;", "[");
				config=config.replace("&#93;", "]");
				user=gson.fromJson(config, User.class);
				user.setAdminQQ(qq);
				if(StringUtils.isAnyBlank(user.getAdminQQ(),user.getCluster(),user.getRobotQQ(),
						user.getServerIp(),user.getServerPassword(),user.getServerUserName())||user.getChatList()==null)
					throw new QQException("服务器初始化失败，配置不完整");
				if(user.getPort()==null)
					user.setPort(22);
			} catch(JsonSyntaxException e) {
				throw new QQException("服务器初始化失败，请输入正确的配置",e);
			}
			App.userMap.put(qq, user);
		}else throw new QQException("服务器已经初始化");
	}
	public static void exitServer(String userId) {
		App.userMap.remove(userId);
	}
	
}
