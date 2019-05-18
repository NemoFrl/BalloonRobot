package nemofrl.balloonRobot.action;

import nemofrl.balloonRobot.service.Action;

public class PublicAction extends BaseAction{

	private static class PublicActionInstance {
		private static final PublicAction INSTANCE = new PublicAction();
	}
	public static PublicAction getInstance(){
	    return PublicActionInstance.INSTANCE;
	}
	
	@Action(value="announce",permission="superAdmin")
	public void announce() {
		if(!checkBody()) return;
		System.out.println("test");
	}
}
