package nemofrl.balloonRobot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;

import com.google.gson.Gson;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import nemofrl.balloonRobot.App;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.exception.QQException;
import nemofrl.balloonRobot.util.HttpApiUtil;
import nemofrl.balloonRobot.util.MessageUtil;
import nemofrl.balloonRobot.util.PermissionUtil;
import nemofrl.balloonRobot.util.PixivUtil;
import nemofrl.balloonRobot.util.ServerUtil;

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
			String commandList = "login {\r\n" + "	\"serverIp\": \"服务器ip地址\",\r\n"
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
				shCommand(source, command, user);
			}
			return;
		}
		if (firstBlank == -1)
			notBlankMethod(source, user);
		else
			blankMethod(commandHead, contentBody, source, user);

	}

	public static void notBlankMethod(String source, User user) {
		String getMsg = MessageUtil.getMsg(source);
		if (getMsg.equals("dst-initsteamcmd")) {
			MessageUtil.sendMessage(source, "开始初始化steamcmd");
			shCommand(source, "apt-get update", user);
			shCommand(source, "apt-get -y install lib32gcc1", user);
			shCommand(source, "apt-get -y install lib32stdc++6", user);
			shCommand(source, "apt-get -y install libcurl4-gnutls-dev:i386", user);
			shCommand(source, "apt-get -y install htop", user);
			shCommand(source, "apt-get -y install screen", user);
			shCommand(source, "mkdir steamcmd", user);
			shCommand(source, "cd steamcmd;wget https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz",
					user);
			shCommand(source, "cd steamcmd;tar -xvzf steamcmd_linux.tar.gz", user);
			shCommand(source, "cd steamcmd;rm -f steamcmd_linux.tar.gz", user);
			shCommand(source, "cd steamcmd/linux32;./steamcmd", user);
			shCommand(source, "mkdir -p .klei/DoNotStarveTogether", user);
			MessageUtil.sendMessage(source, "steamcmd初始化完成，请更新饥荒服务器");
		}
		if (getMsg.equals("dst-restartmaster")) {
			MessageUtil.sendMessage(source, "开始重启地上饥荒服务器");
			String screenName = "Master Server " + user.getCluster();
			String stop = "pkill -f '" + screenName + "'";
			String start = "cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/bin;screen -dmS \""
					+ screenName + "\" ./dontstarve_dedicated_server_nullrenderer -console -cluster "
					+ user.getCluster() + " -monitor_parent_process $ -shard Master";
			shCommand(source, stop, user);
			shCommand(source, start, user);
			MessageUtil.sendMessage(source, "地上饥荒服务器重启成功");
			return;
		}
		if (getMsg.equals("dst-restartcaves")) {
			MessageUtil.sendMessage(source, "开始重启地下饥荒服务器");
			String screenName = "Caves Server " + user.getCluster();
			String stop = "pkill -f '" + screenName + "'";
			String start = "cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/bin;screen -dmS \""
					+ screenName + "\" ./dontstarve_dedicated_server_nullrenderer -console -cluster "
					+ user.getCluster() + " -monitor_parent_process $ -shard Caves";
			shCommand(source, stop, user);
			shCommand(source, start, user);
			MessageUtil.sendMessage(source, "地下饥荒服务器成功");
			return;
		}
		if (getMsg.equals("logout")) {
			if (PermissionUtil.getQQ(source).equals(user.getAdminQQ())) {
				if (user.getTimer() != null)
					user.getTimer().cancel();
				ServerUtil.exitServer(PermissionUtil.getQQ(source));
				MessageUtil.sendMessage(source, "注销成功");
			} else
				MessageUtil.sendMessage(source, "gay missing this permission");
			return;
		}
		if (getMsg.equals("dst-update")) {
			MessageUtil.sendMessage(source, "开始更新饥荒服务器");
			String command = "cd steamcmd;./steamcmd.sh +login anonymous +app_update 343050 validate +quit";
			shCommand(source, command, user);
			MessageUtil.sendMessage(source, "饥荒服务器更新完成");
			return;
		}
		if (getMsg.equals("ps-aux")) {
			MessageUtil.sendMessage(source, "服务器内存及cpu状态如下：");
			String command = "ps aux | sort -k3r |head -n 2";
			shCommand(source, command, user);
			return;
		}
		if (getMsg.equals("ps-sar")) {
			MessageUtil.sendMessage(source, "服务器网络状态如下：");
			String command = "sar -n DEV 1 1";
			shCommand(source, command, user);
			return;
		}
		if (getMsg.contains("色图") || getMsg.contains("gkd")) {
			String result = PixivUtil.getPixivUrl();
			MessageUtil.sendMessage(source, result);
			return;
		}

		// MessageUtil.sendMessage(source,"command not found");
	}

	public static void blankMethod(String commandHead, String contentBody, String source, User user) {
		Timer timer = user.getTimer();
		String path = ".klei/DoNotStarveTogether/" + user.getCluster() + "/Master";
		String screenName = "Master Server " + user.getCluster();
		String adminQQ = user.getAdminQQ();
		String robotQQ = user.getRobotQQ();

		if (commandHead.equals("baidu")) {
			String[] result = HttpApiUtil.search(contentBody);
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					MessageUtil.sendMessage(source, result[i]);
				}
			} else
				MessageUtil.sendMessage(source, "气球仔看不懂");
			return;
		}
		if (commandHead.startsWith("tran")) {
			int index=commandHead.indexOf("-");
			
			if(index!=-1) {
				String tran=commandHead.substring(index+1, commandHead.length());
			String result = HttpApiUtil.translation(tran, contentBody);
			if (result != null) {
				MessageUtil.sendMessage(source, result);
			} else
				MessageUtil.sendMessage(source, "气球仔看不懂");
			return;
			}
		}

		if (commandHead.equals("sj")) {
			if (contentBody.equals("start")) {
				MessageUtil.sendMessage(source, "开始视奸");
				ServerUtil.sjServer(timer, path, source, user);
			}
			if (contentBody.equals("cancel")) {
				MessageUtil.sendMessage(source, "取消视奸");
				timer.cancel();
			}
			return;
		}

		if (commandHead.equals("sh")) {
			if (PermissionUtil.getQQ(source).equals(adminQQ)) {
				shCommand(source, contentBody, user);
			} else
				MessageUtil.sendMessage(source, "gay missing this permission");
			return;
		}
		if (commandHead.equals("[CQ:at,qq=" + robotQQ + "]")) {
			MessageUtil.sendMessage(source, contentBody);
			return;
		}
		if (commandHead.equals("dst")) {
			String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"" + contentBody
					+ "$(printf \\\\r)\"\r\n";
			shCommand(source, command, user);
			return;
		}
		if (commandHead.equals("dst-mod")) {
			String getMods="cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/mods/;cat dedicated_server_mods_setup.lua";
			String result=null;
			try {
				result = ServerUtil.shServer(source, getMods, user);
			} catch (QQException e) {
				if (e.getE() != null)
					logger.error(e.getMsg(), e.getE());
				else
					logger.error(e.getMsg());
			}
			if(result==null) {
				MessageUtil.sendMessage(source, "读取mod配置文件失败，请检查是否已安装饥荒服务器");
				return;
			}
			if(!result.contains(contentBody)) {
				String command = "cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/mods/;echo \"ServerModSetup(\\\""+contentBody+"\\\")\" >> dedicated_server_mods_setup.lua";
				shCommand(source, command, user);
				MessageUtil.sendMessage(source, "mod添加完成，请重启饥荒服务器");
			}else MessageUtil.sendMessage(source, "mod添加失败，该mod已存在");
			return;
		}
		if (commandHead.equals("dst-kick")) {
			String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"TheNet:Kick(\\\"" + contentBody
					+ "\\\")$(printf \\\\r)\"\r\n";
			shCommand(source, command, user);
			return;
		}
		if (commandHead.equals("dst-ban")) {
			String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"TheNet:BanForTime(\\\"" + contentBody
					+ "\\\",120)$(printf \\\\r)\"\r\n";
			shCommand(source, command, user);
			return;
		}
		if (commandHead.equals("dst-msg")) {
			String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"TheNet:SystemMessage(\\\"" + contentBody
					+ "\\\")$(printf \\\\r)\"\r\n";
			shCommand(source, command, user);
			return;
		}
		if (commandHead.equals("dst-back")) {
			String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"c_rollback(" + contentBody
					+ ")$(printf \\\\r)\"\r\n";
			shCommand(source, command, user);
			return;
		}
		if (commandHead.equals("dst-list")) {
			String printList = "screen -S \"" + screenName
					+ "\" -p 0 -X stuff \"c_listplayers()$(printf \\\\r)\"\r\n sleep 1";
			String getList = "grep -A " + contentBody + " 'c_listplayers()" + "' " + path + "/server_log.txt"
					+ " | tail -n " + contentBody;
			try {
				String result = ServerUtil.shServer(source, printList + "&&" + getList, user);
				if (StringUtils.isNotBlank(result)) {
					int index=result.lastIndexOf("c_listplayers()");
					if(index!=-1)
					result=result.substring(index+17);
				}
				if(StringUtils.isNotBlank(result))
					MessageUtil.sendMessage(source, result);
				else MessageUtil.sendMessage(source, "没人在玩气球仔哦");
			} catch (QQException e) {
				if (e.getE() != null)
					logger.error(e.getMsg(), e.getE());
				else
					logger.error(e.getMsg());
				MessageUtil.sendMessage(source, e.getMsg());
			}
			return;
		}
		
		if (commandHead.equals("fd")) {
			if (contentBody.equals("start")) {
				MessageUtil.sendMessage(source, "开始复读，复读状态下，其余命令无效");
				user.setFudu(true);

			}
			if (contentBody.equals("cancel")) {
				MessageUtil.sendMessage(source, "取消复读，其余命令生效");
				user.setFudu(false);
			}
			return;
		}
		// MessageUtil.sendMessage(source,"command not found");
	}

	private static void shCommand(String source, String command, User user) {
		try {
			String result = ServerUtil.shServer(source, command, user);
			if (StringUtils.isNotBlank(result))
				MessageUtil.sendMessage(source, result);
		} catch (QQException e) {
			if (e.getE() != null)
				logger.error(e.getMsg(), e.getE());
			else
				logger.error(e.getMsg());
			MessageUtil.sendMessage(source, e.getMsg());
		}
	}
	
}
