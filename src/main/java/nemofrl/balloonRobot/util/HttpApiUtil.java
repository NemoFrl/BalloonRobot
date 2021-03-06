package nemofrl.balloonRobot.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import nemofrl.balloonRobot.config.BalloonConfig;


public class HttpApiUtil {
	private static final Logger logger = LogManager.getLogger(HttpApiUtil.class);

	public static String[] search(String search) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet("https://www.baidu.com/sugrec?prod=pc&wd=" + search);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String encodeJson = EntityUtils.toString(response.getEntity(), "UTF-8");
				Gson gson = new Gson();
				Map<String, Object> json = gson.fromJson(encodeJson, Map.class);
				ArrayList<LinkedTreeMap<String, String>> qlist = (ArrayList<LinkedTreeMap<String, String>>) json
						.get("g");
				if (qlist == null)
					return null;
				String[] result = new String[qlist.size()];
				for (int i = 0; i < qlist.size(); i++) {
					LinkedTreeMap<String, String> qMap = qlist.get(i);

					result[i] = qMap.get("q");
				}
				return result;
			}

		} catch (Exception e) {
			logger.info("error", e);
		}
		return null;
	}

	public static String translation(String to, String source) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost("http://translate.google.cn/translate_a/single");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("ie","UTF-8"));
			nvps.add(new BasicNameValuePair("client","gtx"));
			nvps.add(new BasicNameValuePair("sl","auto"));
			nvps.add(new BasicNameValuePair("tl",to));
			nvps.add(new BasicNameValuePair("dt","t"));
			nvps.add(new BasicNameValuePair("dj","1"));
			nvps.add(new BasicNameValuePair("q",source));
	        //设置参数到请求对象中
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String json = EntityUtils.toString(response.getEntity(), "UTF-8");
				Gson gson = new Gson();
				Map<String,Object> map=gson.fromJson(json, Map.class);
				ArrayList<LinkedTreeMap<String, Object>> sentences=(ArrayList<LinkedTreeMap<String, Object>>) map.get("sentences");
				String result="";
				for(int i=0;i<sentences.size();i++) {
					result+=sentences.get(i).get("trans");
				}
				
				return result; 
			}

		} catch (Exception e) {
			logger.info("error", e);
		}
		return null;

	}
	
	public static String getNewHaizei(String h) {
		
		HttpClient client = HttpClientBuilder.create().build();
		try {
		String version=getHaizeiVersion();
		if(StringUtils.isBlank(version))
				return null;
		HttpGet httpGet=new HttpGet("https://prod-api.ishuhui.com/ver/"+version+"/anime/detail?id=1&type=comics&.json");
		HttpResponse response = client.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			Gson gson = new Gson();
			Map<String,Object> map=gson.fromJson(json, Map.class);
			map=(Map<String, Object>) map.get("data");
			map=(Map<String, Object>) map.get("comicsIndexes");
			map=(Map<String, Object>) map.get("1");
			int maxNum;
			if(h==null)
				maxNum=((Double)map.get("maxNum")).intValue();
			else maxNum=Integer.valueOf(h);
			int showLen=((Double) map.get("showLen")).intValue();
			int startNum=maxNum/showLen==(maxNum*1.0/showLen)?maxNum-showLen:maxNum/showLen*showLen;
			int endNum=startNum+showLen;
			String numKey=(startNum+1)+"-"+endNum;
			map=(Map<String, Object>) map.get("nums");
			map=(Map<String, Object>) map.get(numKey+"");
			int num=maxNum;
			String returnStr="";
			
			for(int i=0;i<map.size();i++) {
				ArrayList<LinkedTreeMap<String, Object>> allpoint=(ArrayList<LinkedTreeMap<String, Object>>) map.get(num+"");
				Map<String, Object> point=allpoint.get(0);
				int id=((Double) point.get("id")).intValue();
				String title=(String) point.get("title");
				returnStr+="[海贼王] "+num+"话 "+title+" http://www.hanhuazu.cc/comics/detail/"+id+"\n";
				num--;
			}
			return returnStr.substring(0,returnStr.length()-1);
		} 
		} catch (Exception e) {
			logger.info("error", e);
		}
		return null;
	}

	public static String getHaizeiVersion() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet verGet=new HttpGet("https://prod-u.ishuhui.com/ver");
		HttpResponse response = client.execute(verGet);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			Gson gson = new Gson();
			Map<String,Object> map=gson.fromJson(json, Map.class);
			map=(Map<String, Object>) map.get("data");
			return (String) map.get("comics");
		}
		return null;
	}
	
	public static String getPixivUrl(String id) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(BalloonConfig.pixivUrl+"?id="+id); 
		HttpResponse resp=client.execute(get);
		if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			String pictureUrl = EntityUtils.toString(resp.getEntity(), "UTF-8");
			return BalloonConfig.pixivUrl.substring(0,BalloonConfig.pixivUrl.lastIndexOf("/"))+pictureUrl;
		}
	    return null;
	}
	
	public static String getYoutubeUrl(String search) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		String encodeSearch=URLEncoder.encode(search,StandardCharsets.UTF_8.name());
		HttpGet get = new HttpGet(BalloonConfig.youtubeUrl+"/getVideoList?search="+encodeSearch); 
		HttpResponse resp=client.execute(get);
		if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			String json = EntityUtils.toString(resp.getEntity(), "UTF-8");
			Gson gson=new Gson();
			String resultStr="";
			List<LinkedTreeMap> list=gson.fromJson(json, List.class);
			for(int i=0;i<list.size();i++) {
				LinkedTreeMap map=list.get(i);
				resultStr+="[youtube] "+map.get("videoId")+" "+map.get("title")+"\n";
			}
			return resultStr.substring(0,resultStr.length()-1);
		}
	    return null;
	}
	
	public static String getYoutubeVideo(String videoId) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(BalloonConfig.youtubeUrl+"/downloadVideo?videoId="+videoId); 
		HttpResponse resp=client.execute(get);
		if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			String fileUrl = EntityUtils.toString(resp.getEntity(), "UTF-8");
			return BalloonConfig.youtubeUrl+fileUrl;
		}
	    return null;
	}
	
	public static String getYoutubeStatus() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(BalloonConfig.youtubeUrl+"/getDownloadStatus"); 
		HttpResponse resp=client.execute(get);
		if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			String respStr = EntityUtils.toString(resp.getEntity(), "UTF-8");
			return respStr;
		}
	    return null;
	}

	public static String[] getGoogleUrl(String search) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		String encodeSearch=URLEncoder.encode(search,StandardCharsets.UTF_8.name());
		HttpGet get = new HttpGet(BalloonConfig.youtubeUrl+"/googleSearch?search="+encodeSearch); 
		HttpResponse resp=client.execute(get);
		if(resp.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			String json = EntityUtils.toString(resp.getEntity(), "UTF-8");
			Gson gson=new Gson();
			List<LinkedTreeMap> list=gson.fromJson(json, List.class);
			String[] resultStr=new String[list.size()];
			
			for(int i=0;i<list.size();i++) {
				LinkedTreeMap map=list.get(i);
				resultStr[i]="[google] "+map.get("title")+"\n"+map.get("link");
			}
			return resultStr;
		}
	    return null;
	}
}
	
