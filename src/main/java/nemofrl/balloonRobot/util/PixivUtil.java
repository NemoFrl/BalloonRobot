package nemofrl.balloonRobot.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import nemofrl.balloonRobot.config.BalloonConfig;

public class PixivUtil {

	public static String getPixivUrl() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(BalloonConfig.pixivUrl); 
		HttpResponse resp=client.execute(get);
		if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			String pictureUrl = EntityUtils.toString(resp.getEntity(), "UTF-8");
			return BalloonConfig.pixivUrl.substring(0,BalloonConfig.pixivUrl.lastIndexOf("/"))+pictureUrl;
		}
	    return null;
	}
}
