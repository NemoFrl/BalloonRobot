package nemofrl.balloonRobot.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nemofrl.balloonRobot.config.BalloonConfig;
import nemofrl.balloonRobot.service.Action;
import nemofrl.balloonRobot.service.MessageService;
import nemofrl.balloonRobot.util.HttpApiUtil;
import nemofrl.balloonRobot.util.PixivUtil;

public class AttachAction extends BaseAction{
	private static final Logger logger = LogManager.getLogger(AttachAction.class);
	private static class AttachActionInstance {
		private static final AttachAction INSTANCE = new AttachAction();
	}
	public static AttachAction getInstance(){
	    return AttachActionInstance.INSTANCE;
	}
	@Action("gkd")
	public void gkd() {
		String result="搞慢点，顶不住了";
		try {
			result = PixivUtil.getPixivUrl(contentBody);
		} catch (Exception e) {
			logger.error("get pixivUrl error",e);
		}
		MessageService.sendMessage(source, result);
//		MessageService.ban(source,"1");
		return;
	}
	@Action("baidu")
	public void baidu() {
		String[] result = HttpApiUtil.search(contentBody);
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				MessageService.sendMessage(source, result[i]);
			}
		} else
			MessageService.sendMessage(source, "气球仔看不懂");
		return;
	}
	@Action("tran")
	public void tran() {
		int index=contentBody.indexOf(" ");
		if(index==-1) {
			MessageService.sendMessage(source, "参数错误");
			return;
		}
		String lan=contentBody.substring(0,index);
		String tran=contentBody.substring(index+1);
		String result = HttpApiUtil.translation(lan, tran);
		if (result != null) {
			MessageService.sendMessage(source, result);
		} else
			MessageService.sendMessage(source, "气球仔看不懂");
		return;
	}
	
	@Action(value="startclient",permission="superAdmin")
	public void startclient() {
		shCommand(source, "wakeonlan "+BalloonConfig.adminClient, user);
		MessageService.sendMessage(source, "远程启动气球仔电脑成功");
	}
	
	@Action(value="haizei")
	public void getNewHaizei() {
		String newHaizeiString=HttpApiUtil.getNewHaizei(contentBody);
		MessageService.sendMessage(source, newHaizeiString);
	}
	
}
