package nemofrl.balloonRobot.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nemofrl.balloonRobot.config.BalloonConfig;
import nemofrl.balloonRobot.entity.User;
import nemofrl.balloonRobot.exception.QQException;
import nemofrl.balloonRobot.service.Action;
import nemofrl.balloonRobot.service.MessageService;
import nemofrl.balloonRobot.util.ServerUtil;
import redis.clients.jedis.Jedis;

public class DstAction extends BaseAction{
	private static final Logger logger = LogManager.getLogger(BaseAction.class);
	private static class DstActionInstance {
		private static final DstAction INSTANCE = new DstAction();
	}
	public static DstAction getInstance(){
	    return DstActionInstance.INSTANCE;
	}
	@Action("dst")
	public void dst() {
		if(!checkBody()) return;
		String screenName = "Master Server " + user.getCluster();
		String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"" + contentBody
				+ "$(printf \\\\r)\"\r\n";
		shCommand(source, command, user);
		return;
	}

	@Action("dst-initsteamcmd")
	public void initSteamCmd() {
		MessageService.sendMessage(source, "开始初始化steamcmd");
		shCommand(source, "apt-get update", user);
		shCommand(source, "apt-get -y install lib32gcc1", user);
		shCommand(source, "apt-get -y install lib32stdc++6", user);
		shCommand(source, "apt-get -y install libcurl4-gnutls-dev:i386", user);
		shCommand(source, "apt-get -y install htop", user);
		shCommand(source, "apt-get -y install screen", user);
		shCommand(source, "mkdir steamcmd", user);
		shCommand(source, "cd steamcmd;wget https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz",
				user);
		shCommand(source, "cd steamcmd;tar -xvzf steamcmd_linux.tar.gz", user);
		shCommand(source, "cd steamcmd;rm -f steamcmd_linux.tar.gz", user);
		shCommand(source, "cd steamcmd/linux32;./steamcmd", user);
		shCommand(source, "mkdir -p .klei/DoNotStarveTogether", user);
		MessageService.sendMessage(source, "steamcmd初始化完成，请更新饥荒服务器");
	}
	@Action("dst-stopmaster")
	public void stopMaster() {
		MessageService.sendMessage(source, "开始关闭地上饥荒服务器");
		String screenName = "Master Server " + user.getCluster();
		String stop = "pkill -f '" + screenName + "'";
		shCommand(source, stop, user);
		MessageService.sendMessage(source, "地上饥荒服务器关闭成功");
	}
	@Action("dst-restartmaster")
	public void restartMaster() {
		stopMaster();
		MessageService.sendMessage(source, "开始启动地上饥荒服务器");
		String screenName = "Master Server " + user.getCluster();
		String start = "cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/bin;screen -dmS \""
				+ screenName + "\" ./dontstarve_dedicated_server_nullrenderer -console -cluster "
				+ user.getCluster() + " -monitor_parent_process $ -shard Master";
		shCommand(source, start, user);
		MessageService.sendMessage(source, "地上饥荒服务器启动成功");
	}
	@Action("dst-stopcaves")
	public void stopCaves() {
		MessageService.sendMessage(source, "开始关闭地下饥荒服务器");
		String screenName = "Caves Server " + user.getCluster();
		String stop = "pkill -f '" + screenName + "'";
		shCommand(source, stop, user);
		MessageService.sendMessage(source, "地下饥荒服务器关闭成功");
	}
	@Action("dst-restartcaves")
	public void restartCaves() {
		stopCaves();
		MessageService.sendMessage(source, "开始启动地下饥荒服务器");
		String screenName = "Caves Server " + user.getCluster();
		String start = "cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/bin;screen -dmS \""
				+ screenName + "\" ./dontstarve_dedicated_server_nullrenderer -console -cluster "
				+ user.getCluster() + " -monitor_parent_process $ -shard Caves";
		shCommand(source, start, user);
		MessageService.sendMessage(source, "地下饥荒服务器成功");
		return;
	}
	@Action("dst-update")
	public void dstUpdate() {
		MessageService.sendMessage(source, "开始更新饥荒服务器");
		String command = "cd steamcmd;./steamcmd.sh +login anonymous +app_update 343050 validate +quit";
		shCommand(source, command, user);
		MessageService.sendMessage(source, "饥荒服务器更新完成");
		return;
	}
	@Action("dst-updatebeta")
	public void dstUpdateBeta() {
		MessageService.sendMessage(source, "开始更新测试版饥荒服务器");
		String command = "cd steamcmd;./steamcmd.sh +login anonymous +app_update 343050 -beta returnofthembeta +quit";
		shCommand(source, command, user);
		MessageService.sendMessage(source, "测试版饥荒服务器更新完成");
		return;
	}
	@Action("dst-mod")
	public void dstMod() {
		if(!checkBody()) return;
		String getMods="cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/mods/;cat dedicated_server_mods_setup.lua";
		String result=null;
		try {
			result = ServerUtil.shServer(source, getMods, user);
		} catch (QQException e) {
			if (e.getE() != null)
				logger.error(e.getMsg(), e.getE());
			else
				logger.error(e.getMsg());
		}
		if(result==null) {
			MessageService.sendMessage(source, "读取mod配置文件失败，请检查是否已安装饥荒服务器");
			return;
		}
		if(!result.contains(contentBody)) {
			String command = "cd Steam/steamapps/common/Don\\'t\\ Starve\\ Together\\ Dedicated\\ Server/mods/;echo \"ServerModSetup(\\\""+contentBody+"\\\")\" >> dedicated_server_mods_setup.lua";
			shCommand(source, command, user);
			MessageService.sendMessage(source, "mod添加完成，请重启饥荒服务器");
		}else MessageService.sendMessage(source, "mod添加失败，该mod已存在");
		return;
	}
	@Action("dst-kick")
	public void dstKick() {
		if(!checkBody()) return;
		String screenName = "Master Server " + user.getCluster();
		String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"TheNet:Kick(\\\"" + contentBody
				+ "\\\")$(printf \\\\r)\"\r\n";
		shCommand(source, command, user);
		return;
	}
	@Action("dst-ban")
	public void dstBan() {
		if(!checkBody()) return;
		String screenName = "Master Server " + user.getCluster();
		String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"TheNet:BanForTime(\\\"" + contentBody
				+ "\\\",120)$(printf \\\\r)\"\r\n";
		shCommand(source, command, user);
		return;
	}
	@Action("dst-msg")
	public void dstMsg() {
		if(!checkBody()) return;
		String screenName = "Master Server " + user.getCluster();
		String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"TheNet:SystemMessage(\\\"" + contentBody
				+ "\\\")$(printf \\\\r)\"\r\n";
		shCommand(source, command, user);
		return;
	}
	@Action("dst-back")
	public void dstBack() {
		if(!checkBody()) return;
		String screenName = "Master Server " + user.getCluster();
		String command = "screen -S \"" + screenName + "\" -p 0 -X stuff \"c_rollback(" + contentBody
				+ ")$(printf \\\\r)\"\r\n";
		shCommand(source, command, user);
		return;
	}
	@Action("dst-list")
	public void dstList() {
		if(!checkBody()) return;
		String path = ".klei/DoNotStarveTogether/" + user.getCluster() + "/Master";
		String screenName = "Master Server " + user.getCluster();
		String printList = "screen -S \"" + screenName
				+ "\" -p 0 -X stuff \"c_listplayers()$(printf \\\\r)\"\r\n sleep 1";
		String getList = "grep -A " + contentBody + " 'c_listplayers()" + "' " + path + "/server_log.txt"
				+ " | tail -n " + contentBody;
		try {
			String result = ServerUtil.shServer(source, printList + "&&" + getList, user);
			if (StringUtils.isNotBlank(result)) {
				int index=result.lastIndexOf("c_listplayers()");
				if(index!=-1)
					result=result.substring(index+16);
				if(result.length()>0) {
					result=result.substring(1);
					MessageService.sendMessage(source, result);
				} else MessageService.sendMessage(source, "没人在玩气球仔哦");
			} else MessageService.sendMessage(source, "没人在玩气球仔哦");
		} catch (QQException e) {
			if (e.getE() != null)
				logger.error(e.getMsg(), e.getE());
			else
				logger.error(e.getMsg());
			MessageService.sendMessage(source, e.getMsg());
		}
		return;
	}
	@Action("fd")
	public void fd() {
		if(!checkBody()) return;
		if (contentBody.equals("start")) {
			MessageService.sendMessage(source, "开始复读，复读状态下，其余命令无效");
			user.setFudu(true);

		}
		if (contentBody.equals("cancel")) {
			MessageService.sendMessage(source, "取消复读，其余命令生效");
			user.setFudu(false);
		}
		return;
	}
	@Action("dst-death")
	public void dstDeath() {
		MessageService.sendMessage(source, "气球仔死亡记录如下：");
		Jedis jedis=new Jedis(BalloonConfig.websocketUrl);
		Map<String,String> deadth=jedis.hgetAll("death");
		Set<Entry<String,String>> keyValues=deadth.entrySet();
		Iterator<Entry<String,String>> iter=keyValues.iterator();
		Map<Integer,String> result=new HashMap<Integer,String>();
		int max=0;
		while(iter.hasNext()) {
			Entry<String,String> keyValue=iter.next();
			String name=keyValue.getKey();
			Integer num=Integer.parseInt(keyValue.getValue());
			if(result.get(num)==null)
				result.put(num, name);
			else result.put(num,result.get(num)+"，"+name);
			if(num>max)
				max=num;
		}
		jedis.close();
		
		for(int i=0;i<=max;i++) {
			if(result.get(i)!=null)
				MessageService.sendMessage(source, i+"次："+result.get(i));
		}
		return;
	}
	@Action("dst-join")
	public void dstJoin() {
		MessageService.sendMessage(source, "气球仔游戏记录如下：");
		Jedis jedis=new Jedis("www.fornemo.club");
		Map<String,String> deadth=jedis.hgetAll("join");
		Set<Entry<String,String>> keyValues=deadth.entrySet();
		Iterator<Entry<String,String>> iter=keyValues.iterator();
		Map<Integer,String> result=new HashMap<Integer,String>();
		int max=0;
		while(iter.hasNext()) {
			Entry<String,String> keyValue=iter.next();
			String name=keyValue.getKey();
			Integer num=Integer.parseInt(keyValue.getValue());
			if(result.get(num)==null)
				result.put(num, name);
			else result.put(num,result.get(num)+"，"+name);
			if(num>max)
				max=num;
		}
		jedis.close();
		
		for(int i=0;i<=max;i++) {
			if(result.get(i)!=null)
				MessageService.sendMessage(source, i+"次："+result.get(i));
		}
		return;
	}
	@Action("sj")
	public void sj() {
		if(!checkBody()) return;
		Timer timer = user.getTimer();
		String path = ".klei/DoNotStarveTogether/" + user.getCluster() + "/Master";
		if (contentBody.equals("start")) {
			MessageService.sendMessage(source, "开始视奸");
			ServerUtil.sjServer(timer, path, source, user);
		}
		if (contentBody.equals("cancel")) {
			MessageService.sendMessage(source, "取消视奸");
			timer.cancel();
		}
		return;
	}
	
}
