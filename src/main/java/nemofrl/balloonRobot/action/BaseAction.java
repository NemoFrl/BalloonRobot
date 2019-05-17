package nemofrl.balloonRobot.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.exception.QQException;
import nemofrl.balloonRobot.service.Robot;
import nemofrl.balloonRobot.util.MessageUtil;
import nemofrl.balloonRobot.util.ServerUtil;

public class BaseAction {
	private static final Logger logger = LogManager.getLogger(BaseAction.class);
	
	protected String source;
	protected User user;
	protected String contentBody;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getContentBody() {
		return contentBody;
	}
	public void setContentBody(String contentBody) {
		this.contentBody = contentBody;
	}
	public static void shCommand(String source, String command, User user) {
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
	
	public boolean checkBody() {
		if(StringUtils.isBlank(this.contentBody)) {
			MessageUtil.sendMessage(source,"参数不能为空");
			return false;
		}
		else return true;
	}
}
