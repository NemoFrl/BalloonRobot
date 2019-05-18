package nemofrl.balloonRobot.service;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class BalloonWebSocketClient extends WebSocketClient{
	private static final Logger logger = LogManager.getLogger(BalloonWebSocketClient.class);
	
	public BalloonWebSocketClient(URI serverURI) {
		super(serverURI);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		logger.info("connect success to "+this.getURI());
	}

	@Override
	public void onMessage(String message) {
		Thread thread = new Thread(new Core(message));
		thread.start();
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		logger.info("connect close");
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		logger.error("websocket error");
	}

}
