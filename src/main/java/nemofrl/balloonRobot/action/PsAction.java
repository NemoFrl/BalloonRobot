package nemofrl.balloonRobot.action;

import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.service.Action;
import nemofrl.balloonRobot.service.MessageService;
import nemofrl.balloonRobot.util.RouteUtil;
import nemofrl.balloonRobot.util.ServerUtil;

public class PsAction extends BaseAction{
	private static class PsActionInstance {
		private static final PsAction INSTANCE = new PsAction();
	}
	public static PsAction getInstance(){
	    return PsActionInstance.INSTANCE;
	}
	@Action("ps-aux")
	public void PsAux() {
		MessageService.sendMessage(source, "服务器内存及cpu状态如下：");
		String command = "ps aux | sort -k3r |head -n 2";
		shCommand(source, command, user);
		return;
	}
	@Action("ps-sar")
	public void PsSar() {
		MessageService.sendMessage(source, "服务器网络状态如下：");
		String command = "sar -n DEV 1 1";
		shCommand(source, command, user);
		return;
	}
	@Action(value="logout",permission="admin")
	public void psLogout() {
		if (user.getTimer() != null)
			user.getTimer().cancel();
		ServerUtil.exitServer(RouteUtil.getQQ(source));
		MessageService.sendMessage(source, "注销成功");
		return;
	}
	@Action("ps-info")
	public void psInfo() {
		MessageService.sendMessage(source, "服务器信息如下：");
		MessageService.sendMessage(source, "服务器IP："+user.getServerIp()+"，存档："+user.getCluster());
		return;
	}
	@Action(value="sh",permission="admin")
	public void sh() {
		if(!checkBody()) return;
		shCommand(source, contentBody, user);
		return;
	}
	
}
