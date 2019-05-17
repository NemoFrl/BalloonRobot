package nemofrl.balloonRobot.action;

import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.util.Action;
import nemofrl.balloonRobot.util.MessageUtil;
import nemofrl.balloonRobot.util.PermissionUtil;
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
		MessageUtil.sendMessage(source, "服务器内存及cpu状态如下：");
		String command = "ps aux | sort -k3r |head -n 2";
		shCommand(source, command, user);
		return;
	}
	@Action("ps-sar")
	public void PsSar() {
		MessageUtil.sendMessage(source, "服务器网络状态如下：");
		String command = "sar -n DEV 1 1";
		shCommand(source, command, user);
		return;
	}
	@Action("ps-logout")
	public void psLogout() {
		if (PermissionUtil.getQQ(source).equals(user.getAdminQQ())) {
			if (user.getTimer() != null)
				user.getTimer().cancel();
			ServerUtil.exitServer(PermissionUtil.getQQ(source));
			MessageUtil.sendMessage(source, "注销成功");
		} else
			MessageUtil.sendMessage(source, "gay missing this permission");
		return;
	}
	@Action("ps-info")
	public void psInfo() {
		MessageUtil.sendMessage(source, "服务器信息如下：");
		MessageUtil.sendMessage(source, "服务器IP："+user.getServerIp()+"，存档："+user.getCluster());
		return;
	}
	@Action("sh")
	public void sh() {
		if(!checkBody()) return;
		String adminQQ = user.getAdminQQ();
		if (PermissionUtil.getQQ(source).equals(adminQQ)) {
			shCommand(source, contentBody, user);
		} else
			MessageUtil.sendMessage(source, "gay missing this permission");
		return;
	}
	
}
