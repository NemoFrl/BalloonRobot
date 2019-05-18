package nemofrl.balloonRobot.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;

import com.google.gson.Gson;

import nemofrl.balloonRobot.App;

public class MessageService {
	private static final Logger logger = LogManager.getLogger(MessageService.class);
	public static String getMsg(String jsonMsg) {
		
		Gson gson = new Gson();
		Map<String, String> map = gson.fromJson(jsonMsg, Map.class);
		if (StringUtils.isNotBlank(map.get("msg"))) {
			return map.get("msg");
		} else
			return null;
	}
   
	public static boolean sendMessage(String source,String msg) {
		Gson sourceGson = new Gson();
		Map<String, String> sourceMap = sourceGson.fromJson(source, Map.class);
		
		Gson gson = new Gson();
		Map<String, String> msgMap = new HashMap<String, String>();
		
		if(sourceMap.get("act").equals("21")) {
			msgMap.put("act", "106");
			msgMap.put("QQID", sourceMap.get("fromQQ"));
		} else if(sourceMap.get("act").equals("2")) {
			msgMap.put("act", "101");
			msgMap.put("groupid", sourceMap.get("fromGroup"));
		}else {
			logger.warn("unsupport from");
			return false;
		}
		
		msgMap.put("msg", msg);
		String sendMsgJson = gson.toJson(msgMap);
		App.wsc.send(sendMsgJson);
		return true;
	}
	public static boolean ban(String source,String time) {
		Gson sourceGson = new Gson();
		Map<String, String> sourceMap = sourceGson.fromJson(source, Map.class);
		
		Gson gson = new Gson();
		Map<String, String> msgMap = new HashMap<String, String>();
		
		msgMap.put("act", "121");
		msgMap.put("QQID", sourceMap.get("fromQQ"));
		msgMap.put("groupid", sourceMap.get("fromGroup"));
		msgMap.put("duration", time);
		
		String sendMsgJson = gson.toJson(msgMap);
		App.wsc.send(sendMsgJson);
		return true;
	}

	
}
