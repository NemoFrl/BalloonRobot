package nemofrl.balloonRobot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
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

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.service.Robot;
import nemofrl.balloonRobot.util.MessageUtil;

public class App {
	
	private static final Logger logger = LogManager.getLogger(App.class);
	
	public static WebSocketClient wsc;
	
	public static Map<String,User> userMap=new ConcurrentHashMap<String,User>();

	public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
		
		wsc = new BalloonWebSocketClient(new URI("ws://www.fornemo.club:25303"));
		
		wsc.connect();
		
		
		while (true) {
			try {
			   Thread.sleep(5000);
	    	   wsc.send("test");
			} catch(Exception e) {
				logger.info("retry connect");
				try {
					wsc = new BalloonWebSocketClient(new URI("ws://127.0.0.1:25303"));
					wsc.connect();
				} catch (Exception e1) {
					logger.error("retry connect error",e1);
				}
			}
		}

	}

	
}
