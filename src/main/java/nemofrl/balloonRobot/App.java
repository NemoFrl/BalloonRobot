package nemofrl.balloonRobot;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;

import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import nemofrl.balloonRobot.config.BalloonConfig;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.service.MessageService;
import nemofrl.balloonRobot.service.BalloonWebSocketClient;
import nemofrl.balloonRobot.service.Core;

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
			logger.info("气球仔配置初始化成功！");
			logger.info(BalloonConfig.rebotInfo);
			return true;
		} catch(Exception e) {
			logger.error("read config error,can't init",e);
			return false;
		}
	}
}
