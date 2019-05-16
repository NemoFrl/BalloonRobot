package nemofrl.balloonRobot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class DstLog implements Runnable{

	private String type;
	private String name;
	
	public DstLog(String type,String name) {
		this.type=type;
		this.name=name;
	}
	
	public void run() {
		Jedis jedis=new Jedis("www.fornemo.club");
		boolean exist=jedis.exists(type);
		if(!exist) {
			Map<String,String> map=new HashMap<String,String>();
			map.put(name, "1");
			jedis.hmset(type, map);
		} else {
			String beforeValue=jedis.hget(type, name);
			if(beforeValue!=null) {
				String afterValue=String.valueOf(Integer.parseInt(beforeValue)+1);
				jedis.hset(type, name, afterValue);
			} else jedis.hset(type, name, "1");
		}
		jedis.close();
	}
	
	public void start() {
		new Thread(this).start();
	}

}
