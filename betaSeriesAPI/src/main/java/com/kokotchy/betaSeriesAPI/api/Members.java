package com.kokotchy.betaSeriesAPI.api;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;

import com.kokotchy.betaSeriesAPI.Utils;

/**
 * Members API
 * 
 * @author kokotchy
 * 
 */
public class Members {

	/**
	 * API Key
	 */
	private String apiKey;

	/**
	 * Token of logged user
	 */
	private String token;

	/**
	 * Create new members api with the given key
	 * 
	 * @param apiKey
	 *            API Key
	 */
	public Members(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Auth the user with his login and password
	 * 
	 * @param login
	 *            Login of the user
	 * @param password
	 *            Password of the user
	 */
	public boolean auth(String login, String password) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("login", login);
		params.put("password", Utils.getMD5(password));
		Document document = Utils.executeQuery("members/auth.xml", apiKey,
				params);
		if (document.selectSingleNode("/root/code").getText().equals("1")) {
			Node tokenNode = document.selectSingleNode("/root/member/token");
			token = tokenNode.getText();
			return true;
		}
		return false;
	}

	/**
	 * Destroy the token of the user
	 */
	public void destroy() {

	}

	/**
	 * Return the token of the user
	 * 
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * TODO Fill it
	 */
	public void infos() {

	}

	/**
	 * TODO Fill it
	 * 
	 * @param user
	 */
	public void infos(String user) {

	}
}
