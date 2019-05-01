package nemofrl.balloonRobot.entity;

import java.util.List;
import java.util.Timer;

import org.java_websocket.client.WebSocketClient;

import ch.ethz.ssh2.Connection;

public class User {

	private String serverIp;
	private String serverUserName;
	private String serverPassword;
	private String cluster;
	private String adminQQ;
	private String robotQQ;
	private Timer timer;
	private List<String> chatList;
	private String lastLog;
	public boolean fudu = false;

	public List<String> getChatList() {
		return chatList;
	}

	public void setChatList(List<String> chatList) {
		this.chatList = chatList;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getAdminQQ() {
		return adminQQ;
	}

	public void setAdminQQ(String adminQQ) {
		this.adminQQ = adminQQ;
	}

	public String getRobotQQ() {
		return robotQQ;
	}

	public void setRobotQQ(String robotQQ) {
		this.robotQQ = robotQQ;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	public boolean isFudu() {
		return fudu;
	}

	public void setFudu(boolean fudu) {
		this.fudu = fudu;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerUserName() {
		return serverUserName;
	}

	public void setServerUserName(String serverUserName) {
		this.serverUserName = serverUserName;
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	public String getLastLog() {
		return lastLog;
	}

	public void setLastLog(String lastLog) {
		this.lastLog = lastLog;
	}

}
