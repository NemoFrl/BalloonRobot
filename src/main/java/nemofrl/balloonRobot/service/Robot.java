package nemofrl.balloonRobot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.google.gson.Gson;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import nemofrl.balloonRobot.App;
import nemofrl.balloonRobot.action.BaseAction;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.exception.QQException;
import nemofrl.balloonRobot.util.Action;
import nemofrl.balloonRobot.util.HttpApiUtil;
import nemofrl.balloonRobot.util.MessageUtil;
import nemofrl.balloonRobot.util.PermissionUtil;
import nemofrl.balloonRobot.util.PixivUtil;
import nemofrl.balloonRobot.util.ServerUtil;
import redis.clients.jedis.Jedis;

public class Robot implements Runnable{

	private static final Logger logger = LogManager.getLogger(Robot.class);
	
	private String source;
	
	public Robot(String source) {
		this.source=source;
	}
	
	public  void run() {
		
		String getMsg = MessageUtil.getMsg(source);
		if (StringUtils.isBlank(getMsg)) {
			// 空消息不做任何处理
			return;
		}
		// 尝试分割消息
		int firstBlank = getMsg.indexOf(" ");
		String commandHead = null;
		String contentBody = null;
		if (firstBlank != -1) {
			commandHead = getMsg.substring(0, firstBlank);
			contentBody = getMsg.substring(firstBlank + 1);
		}
		if (getMsg.equals("login-help")) {
			String commandList = "login {\r\n" + "	\"serverIp\": \"服务器ip地址\",\r\n" + "	\"port\": \"服务器端口\",\r\n"
					+ "	\"serverUserName\": \"服务器用户名\",\r\n" + "	\"serverPassword\": \"服务器密码\",\r\n"
					+ "	\"cluster\": \"存档名称\",\r\n" + "	\"robotQQ\": \"机器人qq\",\r\n"
					+ "	\"chatList\": [\"可使用本服务的群\"]\r\n" + "}";
			MessageUtil.sendMessage(source, commandList);
		}
		// 查询是否已经登录
		User user = PermissionUtil.checkChat(source);
		// 未登录
		if (user == null) {
			// 为登录命令时登录
			if (firstBlank != -1 && commandHead.equals("login")) {
				try {
					ServerUtil.initServer(source, contentBody);
					MessageUtil.sendMessage(source, "服务器初始化成功");
				} catch (QQException e) {
					if (e.getE() != null)
						logger.error(e.getMsg(), e.getE());
					else
						logger.error(e.getMsg());
					MessageUtil.sendMessage(source, e.getMsg());
				}
			} else
				MessageUtil.sendMessage(source, "服务器尚未初始化，请输入初始化配置");

			return;
		}
		// 复读开启时无视一切命令，除了 取消复读
		if (user.isFudu() && !getMsg.equals("fd cancel")) {
			if(!getMsg.contains("CQ:image")) {
				String screenName = "Master Server " + user.getCluster();
				String dstCommand = "TheNet:SystemMessage(\\\"" + PermissionUtil.getNick(source) + "：" + getMsg + "\\\")";
				String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"" + dstCommand + "$(printf \\\\r)\"\r\n";
				BaseAction.shCommand(source, command, user);
			}
			return;
		}

		Reflections reflections=new Reflections("nemofrl.balloonRobot.action.*",new MethodAnnotationsScanner());
		Set<Method> methods=reflections.getMethodsAnnotatedWith(Action.class);
		try {
			for(Method method:methods) {
				Action action=method.getAnnotation(Action.class);
				BaseAction baseAction=(BaseAction) (method.getDeclaringClass().getDeclaredMethod("getInstance").invoke(null));
				if(firstBlank == -1&&action.value().equals(getMsg)) {
					baseAction.setContentBody(null);
					baseAction.setSource(source);
					baseAction.setUser(user);
					logger.info("调用Action方法为:"+method.getName());
					method.invoke(baseAction);
					logger.info("调用成功");
					return;
				} else if(action.value().equals(commandHead)) {
					baseAction.setContentBody(contentBody);
					baseAction.setSource(source);
					baseAction.setUser(user);
					logger.info("调用Action方法为:"+method.getName());
					method.invoke(baseAction);
					logger.info("调用成功");
					return;
				}
					
			}
		}catch(Exception e) {
			logger.error("Action调用失败",e);
			MessageUtil.sendMessage(source, "Action调用失败");
		}
		if (firstBlank != -1&&commandHead.equals("[CQ:at,qq=" + user.getRobotQQ() + "]")) {
			MessageUtil.sendMessage(source, contentBody);
			return;
		}
	}

	
}
