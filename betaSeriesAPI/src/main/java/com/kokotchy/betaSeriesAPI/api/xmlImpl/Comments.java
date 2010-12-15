package com.kokotchy.betaSeriesAPI.api.xmlImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Node;

import com.kokotchy.betaSeriesAPI.UtilsXml;
import com.kokotchy.betaSeriesAPI.api.IComments;
import com.kokotchy.betaSeriesAPI.api.factories.CommentFactory;
import com.kokotchy.betaSeriesAPI.model.Comment;

/**
 * Comments api
 * 
 * @author kokotchy
 */
public class Comments implements IComments {

	/**
	 * Api key
	 */
	private String apiKey;

	/**
	 * Create new comments api with the given key
	 * 
	 * @param apiKey
	 *            API key
	 */
	public Comments(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Return the comments of the document
	 * 
	 * @param document
	 *            Document
	 * @return Comments
	 */
	@SuppressWarnings("unchecked")
	private Set<Comment> getComments(Document document) {
		Set<Comment> comments = new HashSet<Comment>();
		if (!UtilsXml.hasErrors(document)) {
			List<Node> nodes = document.selectNodes("/root/comments/comment");
			for (Node showNode : nodes) {
				Comment comment = CommentFactory.createComment(showNode);
				comments.add(comment);
			}
		}
		return comments;
	}

	@Override
	public Set<Comment> getComments(String url) {
		Document document = UtilsXml.executeQuery("comments/show/" + url,
				apiKey);
		return getComments(document);
	}

	@Override
	public Set<Comment> getComments(String url, int season, int episode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("season", "" + season);
		params.put("episode", "" + episode);
		Document document = UtilsXml.executeQuery("comments/episode/" + url,
				apiKey, params);
		return getComments(document);
	}

	@Override
	public Set<Comment> getUserComments(String login) {
		Document document = UtilsXml.executeQuery("comments/member/" + login,
				apiKey);
		return getComments(document);
	}

	/**
	 * Post a comment on the profil of a member that can be a response to
	 * another comment
	 * 
	 * @param token
	 *            Token
	 * @param login
	 *            Login
	 * @param text
	 *            Content
	 * @param responseTo
	 *            Id of the comment
	 */
	private boolean postAUserComment(String token, String login, String text,
			int responseTo) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("member", login);
		String encode = text;
		try {
			encode = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.put("text", encode);
		params.put("token", token);
		if (responseTo >= 0) {
			params.put("in_reply_to", "" + responseTo);
		}
		UtilsXml.executeQuery("comments/post/member", apiKey, params);
		// TODO Check for error
		return true;
	}

	@Override
	public boolean postComment(String token, String url, String text) {
		return postGeneralComment(token, url, text, -1, -1, -1);
	}

	@Override
	public boolean postComment(String token, String url, String text,
			int responseTo) {
		return postGeneralComment(token, url, text, -1, -1, responseTo);
	}

	@Override
	public boolean postComment(String token, String url, String text,
			int season, int episode) {
		return postGeneralComment(token, url, text, season, episode, -1);
	}

	@Override
	public boolean postComment(String token, String url, String text,
			int responseTo, int season, int episode) {
		return postGeneralComment(token, url, text, season, episode, responseTo);
	}

	/**
	 * Post a comment on the website.
	 * 
	 * @param token
	 *            Token
	 * @param url
	 *            Url
	 * @param text
	 *            Text
	 * @param season
	 *            Season
	 * @param episode
	 *            Episode
	 * @param responseTo
	 *            Id of the comment
	 */
	private boolean postGeneralComment(String token, String url, String text,
			int season, int episode, int responseTo) {
		Map<String, String> params = new HashMap<String, String>();
		String action = null;
		params.put("text", text);
		params.put("show", url);
		if (season < 0 && episode < 0) {
			action = "comments/post/show";
			if (responseTo >= 0) {
				params.put("in_reply_to", "" + responseTo);
			}
		} else if (season > 0 && episode > 0) {
			action = "comments/post/episode";
			params.put("season", "" + season);
			params.put("episode", "" + episode);
			if (responseTo >= 0) {
				params.put("in_reply_to", "" + responseTo);
			}
		}
		if (token != null) {
			params.put("token", token);
		}
		// TODO Check for error
		if (action != null) {
			UtilsXml.executeQuery(action, apiKey, params);
			return true;
		}
		return false;
	}

	@Override
	public boolean postUserComment(String token, String login, String text) {
		return postAUserComment(token, login, text, -1);
	}

	@Override
	public boolean postUserComment(String token, String login, String text,
			int responseTo) {
		return postAUserComment(token, login, text, responseTo);
	}
}
