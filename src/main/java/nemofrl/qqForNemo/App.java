package nemofrl.qqForNemo;

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
import nemofrl.qqForNemo.entity.User;
import nemofrl.qqForNemo.service.Robot;
import nemofrl.qqForNemo.util.MessageUtil;

public class App {
	
	private static final Logger logger = LogManager.getLogger(App.class);
	
	public static WebSocketClient wsc;
	
	public static Map<String,User> userMap=new ConcurrentHashMap<String,User>();

	public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
		
		
		wsc = new WebSocketClient(new URI("ws://www.fornemo.club:25303"), (Draft) new Draft_17()) {

			@Override
			public void onClose(int arg0, String arg1, boolean arg2) {
				logger.info("connect close,retry connect");
			}

			@Override
			public void onError(Exception arg0) {
				logger.error("websocket error",arg0);
			}

			@Override
			public void onMessage(String msg) {
				Thread thread = new Thread(new Robot(msg));
				thread.start();
			}

			@Override
			public void onOpen(ServerHandshake arg0) {
				logger.info("connect success");

			}

		};
		wsc.connect();
		
	
		while (true) {
		}

	}

	
}
