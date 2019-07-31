package nemofrl.balloonRobot.service;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import nemofrl.balloonRobot.App;
import nemofrl.balloonRobot.action.BaseAction;
import nemofrl.balloonRobot.config.BalloonConfig;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.exception.QQException;
import nemofrl.balloonRobot.util.RouteUtil;
import nemofrl.balloonRobot.util.ServerUtil;

public class Core implements Runnable{

	private static final Logger logger = LogManager.getLogger(Core.class);
	
	private String source;
	
	public Core(String source) {
		this.source=source;
	}
	
	public  void run() {
		
		String getMsg = MessageService.getMsg(source);
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
			MessageService.sendMessage(source, commandList);
		}
		
		
		// 查询是否已经登录
		User user = RouteUtil.checkChat(source);
		// 未登录
		if (user == null) {
			logger.info("该请求发起方尚未登录，校验是否为登录命令");
			// 为登录命令时登录
			if (firstBlank != -1 && commandHead.equals("login")) {
				if(App.userMap.size()<=BalloonConfig.clientLimit) {
					try {
						ServerUtil.initServer(source, contentBody);
						MessageService.sendMessage(source, "服务器初始化成功");
						logger.info("登录成功");
					} catch (QQException e) {
						if (e.getE() != null)
							logger.error(e.getMsg(), e.getE());
						else
							logger.error(e.getMsg());
						MessageService.sendMessage(source, e.getMsg());
					}
				} else MessageService.sendMessage(source, "气球仔已经顶不住啦（服务器调用方已满）");
			} else
				MessageService.sendMessage(source, "服务器尚未初始化，请输入初始化配置");

			return;
		}
		logger.info("校验登录信息成功，msg:"+source);
		// 复读开启时无视一切命令，除了 取消复读
		if (user.isFudu() && !getMsg.equals("fd cancel")) {
			if(!getMsg.contains("CQ:image")) {
				String screenName = "Master Server " + user.getCluster();
				String dstCommand = "TheNet:SystemMessage(\\\"" + RouteUtil.getNick(source) + "：" + getMsg + "\\\")";
				String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"" + dstCommand + "$(printf \\\\r)\"\r\n";
				BaseAction.shCommand(source, command, user);
			}
			return;
		}
		logger.info("非复读状态，开始查找Action...");
		Reflections reflections=new Reflections("nemofrl.balloonRobot.action.*",new MethodAnnotationsScanner());
		Set<Method> methods=reflections.getMethodsAnnotatedWith(Action.class);
		try {
			for(Method method:methods) {
				Action action=method.getAnnotation(Action.class);
				BaseAction baseAction=(BaseAction) (method.getDeclaringClass().getDeclaredMethod("getInstance").invoke(null));
				if(firstBlank == -1&&action.value().equals(getMsg)) {
					if(checkPermission(action.permission(),user,source)) {
						baseAction.setContentBody(null);
						baseAction.setSource(source);
						baseAction.setUser(user);
						logger.info("调用Action方法为:"+method.getName());
						method.invoke(baseAction);
						logger.info("调用成功");
					} else {
						logger.info("权限不足，无法调用Action方法:"+method.getName());
						MessageService.sendMessage(source, "gay missing this permission");
					}
					return;
				} else if(action.value().equals(commandHead)) {
					if(checkPermission(action.permission(),user,source)) {
						baseAction.setContentBody(contentBody);
						baseAction.setSource(source);
						baseAction.setUser(user);
						logger.info("调用Action方法为:"+method.getName());
						method.invoke(baseAction);
						logger.info("调用成功");
					} else {
						logger.info("权限不足，无法调用Action方法:"+method.getName());
						MessageService.sendMessage(source, "gay missing this permission");
					}
					return;
				}
					
			}
		}catch(Exception e) {
			logger.error("Action调用失败",e);
			MessageService.sendMessage(source, "Action调用失败");
		}
		if (firstBlank != -1&&commandHead.equals("[CQ:at,qq=" + user.getRobotQQ() + "]")) {
			MessageService.sendMessage(source, contentBody);
			return;
		}
		logger.info("无法找到相应Action，为普通消息，忽略...");
	}

	public boolean checkPermission(String actionPerm,User user,String source) {
		if(actionPerm.equals("superAdmin")) {
			if(RouteUtil.getQQ(source).equals(BalloonConfig.superAdmin))
				return true;
			else 
				return false;
		} else if(actionPerm.equals("admin")) {
			if(RouteUtil.getQQ(source).equals(user.getAdminQQ()))
				return true;
			else 
				return false;
		}
		return true;
	}
}
