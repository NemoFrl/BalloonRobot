package nemofrl.balloonRobot.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import nemofrl.balloonRobot.service.Robot;

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
			HttpGet get = new HttpGet("http://translate.google.cn/translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=auto&tl="+to+"&q="+source);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String json = EntityUtils.toString(response.getEntity(), "UTF-8");
				Gson gson = new Gson();
				Map<String,Object> map=gson.fromJson(json, Map.class);
				ArrayList<LinkedTreeMap<String, Object>> sentences=(ArrayList<LinkedTreeMap<String, Object>>) map.get("sentences");
				String result="";
				for(int i=0;i<sentences.size();i++) {
					result+=sentences.get(i).get("trans")+",";
				}
				result = result.substring(0,result.length()-1);
				return result; 
			}

		} catch (Exception e) {
			logger.info("error", e);
		}
		return null;

	}
}
