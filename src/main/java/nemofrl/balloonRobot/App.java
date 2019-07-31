package nemofrl.balloonRobot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nemofrl.balloonRobot.config.BalloonConfig;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.service.BalloonWebSocketClient;

public class App {
	
	private static final Logger logger = LogManager.getLogger(App.class);
	
	public static WebSocketClient wsc;
	
	public static Map<String,User> userMap=new ConcurrentHashMap<String,User>();

	public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
		
		if(initConfig()) {
		
			wsc = new BalloonWebSocketClient(new URI(BalloonConfig.websocketUrl));
			
			wsc.connect();
			
			while (true) {
				try {
				   Thread.sleep(5000);
		    	   wsc.send("test");
				} catch(Exception e) {
					logger.info("retry connect");
					try {
						wsc = new BalloonWebSocketClient(new URI(BalloonConfig.websocketUrl));
						wsc.connect();
					} catch (Exception e1) {
						logger.error("retry connect error",e1);
					}
				}
			}
		}
	}

	private static boolean initConfig() {
		logger.info("初始化气球仔配置...");
		File conf=new File("src/main/resources/config.json");
		if(!conf.exists())
			conf=new File("config.json");
		String config=null;
		try {
			InputStream in=new FileInputStream(conf);
			byte[] bytes=new byte[in.available()];
			in.read(bytes);
			in.close();
			config=new String(bytes,StandardCharsets.UTF_8);
			Gson gson=new Gson();
			Type type = new TypeToken<Map<String, Object>>() {}.getType();
			Map<String,Object> map= gson.fromJson(config,type);
			BalloonConfig.websocketUrl = (String) map.get("websocketUrl");
			BalloonConfig.jedisUrl = (String) map.get("jedisUrl");
			BalloonConfig.clientLimit = Integer.parseInt((String) map.get("clientLimit"));
			BalloonConfig.superAdmin = (String) map.get("superAdmin");
			BalloonConfig.rebotInfo = (String) map.get("rebotInfo");
			BalloonConfig.adminClient = (String) map.get("adminClient");
			BalloonConfig.pixivUrl = (String) map.get("pixivUrl");
			BalloonConfig.youtubeUrl = (String) map.get("youtubeUrl");
			logger.info("气球仔配置初始化成功！");
			logger.info(BalloonConfig.rebotInfo);
			return true;
		} catch(Exception e) {
			logger.error("read config error,can't init",e);
			return false;
		}
	}
}
