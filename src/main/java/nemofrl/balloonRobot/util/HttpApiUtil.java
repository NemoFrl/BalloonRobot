package nemofrl.balloonRobot.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import nemofrl.balloonRobot.service.Core;


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
		HttpGet httpGet=new HttpGet("https://prod-api.ishuhui.com/ver/89352976/anime/detail?id=1&type=comics&.json");
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
			int startNum=maxNum/showLen*showLen;
			int endNum=startNum+showLen;
			String numKey=(startNum+1)+"-"+endNum;
			map=(Map<String, Object>) map.get("nums");
			map=(Map<String, Object>) map.get(numKey+"");
			int num=maxNum;
			String returnStr="";
			
			for(int i=0;i<5;i++) {
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

}
	
