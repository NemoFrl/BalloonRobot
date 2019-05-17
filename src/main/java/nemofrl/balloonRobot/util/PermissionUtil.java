package nemofrl.balloonRobot.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import nemofrl.balloonRobot.App;
import nemofrl.balloonRobot.entity.User;

public class PermissionUtil {
	private static final Logger logger = LogManager.getLogger(PermissionUtil.class);
	
	public static String getGroup(String jsonMsg) {
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonMsg, Map.class);
		return map.get("fromGroup");
	}
	public static String getNick(String jsonMsg) {
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonMsg, Map.class);
		return map.get("nick");
	}
	
	public static String getQQ(String jsonMsg) {
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonMsg, Map.class);
		return map.get("fromQQ");
	}
	public static User checkChat(String source) {
		String fromGroup=PermissionUtil.getGroup(source);
		if(StringUtils.isBlank(fromGroup)) {
			String fromQQ=PermissionUtil.getQQ(source);
			Map<String, User> map=App.userMap;
			for(String admin:map.keySet()) {
				if(fromQQ.equals(admin)) {
					logger.info("收到私聊-服务器管理为："+admin+"的请求");
					return map.get(admin);
				}
			}
			return null;
		}
		Map<String, User> map=App.userMap;
		for(Entry<String, User> en:map.entrySet()) {
			List<String> chatList=en.getValue().getChatList();
			for(String chat:chatList) {
				if(fromGroup.equals(chat)) {
					logger.info("收到群聊-服务器管理为："+en.getKey()+"的请求");
					return en.getValue();
				}
			}
		}
		return null;
	}
}
