package com.kokotchy.betaSeriesAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO Fill it
 * 
 * @author kokotchy
 * 
 */
public class UtilsJson {

	/**
	 * TODO Fill it
	 * 
	 * @param action
	 * @param apiKey
	 * @param params
	 * @return
	 */
	public static JSONObject executeQuery(String action, String apiKey,
			Map<String, String> params) {
		params.put("key", apiKey);
		try {
			// String urlPattern = "http://%s/%s?%s";
			// String host = "api.betaseries.com";
			// URL url = new URL(String.format(urlPattern, host, action, Utils
			// .getParamAsString(params)));
			File file = new File("/home/kokotchy/Desktop/betaseriejson/"
					+ action);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			StringBuffer json = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			JSONObject result = new JSONObject(json.toString());
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * TODO Fill it
	 * 
	 * @param jsonObject
	 * @param path
	 * @return
	 */
	public static JSONArray getJSONArrayFromPath(JSONObject jsonObject,
			String path) {
		String[] split = path.split("/");
		JSONObject object = jsonObject;
		try {
			for (int i = 0; i < split.length - 1; i++) {
				String part = split[i].trim();
				if (!part.isEmpty()) {
					object = jsonObject.getJSONObject(part);
				}
			}
			return object.getJSONArray(split[split.length - 1]);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}