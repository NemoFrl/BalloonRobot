package nemofrl.balloonRobot.action;

import nemofrl.balloonRobot.service.Action;
import nemofrl.balloonRobot.service.MessageService;
import nemofrl.balloonRobot.util.HttpApiUtil;

public class AttachAction extends BaseAction{
	private static class AttachActionInstance {
		private static final AttachAction INSTANCE = new AttachAction();
	}
	public static AttachAction getInstance(){
	    return AttachActionInstance.INSTANCE;
	}
	@Action("gkd")
	public void gkd() {
//		String result = PixivUtil.getPixivUrl();
		MessageService.sendMessage(source, "死变态");
		//MessageUtil.ban(source,"1");
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
}
