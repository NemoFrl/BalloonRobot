package nemofrl.balloonRobot.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import nemofrl.balloonRobot.App;
import nemofrl.balloonRobot.entity.User;
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
		Map<String, User> map=App.userMap;
		Gson gson = new Gson();
		Map<String, String> msgMap = new HashMap<String, String>();
		
		for(String admin:map.keySet()) {
			msgMap.put("act", "106");
			msgMap.put("QQID", admin);
			msgMap.put("msg", contentBody);
			String sendMsgJson = gson.toJson(msgMap);
			App.wsc.send(sendMsgJson);
		}
		for(Entry<String, User> en:map.entrySet()) {
			List<String> chatList=en.getValue().getChatList();
			for(String chat:chatList) {
				msgMap.put("act", "101");
				msgMap.put("groupid", chat);
				String sendMsgJson = gson.toJson(msgMap);
				App.wsc.send(sendMsgJson);
			}
		}
	}
}
