package com.kokotchy.betaSeriesAPI.api.jsonImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kokotchy.betaSeriesAPI.Utils;
import com.kokotchy.betaSeriesAPI.UtilsJson;
import com.kokotchy.betaSeriesAPI.api.IMembers;
import com.kokotchy.betaSeriesAPI.api.NotImplementedException;
import com.kokotchy.betaSeriesAPI.api.factories.EpisodeFactory;
import com.kokotchy.betaSeriesAPI.api.factories.MemberFactory;
import com.kokotchy.betaSeriesAPI.api.factories.NotificationFactory;
import com.kokotchy.betaSeriesAPI.model.Episode;
import com.kokotchy.betaSeriesAPI.model.Member;
import com.kokotchy.betaSeriesAPI.model.Notification;
import com.kokotchy.betaSeriesAPI.model.SortType;
import com.kokotchy.betaSeriesAPI.model.SubtitleLanguage;

/**
 * Members API
 * 
 * @author kokotchy
 */
public class Members implements IMembers {

	/**
	 * API Key
	 */
	private String apiKey;

	/**
	 * Create new members api with the given key
	 * 
	 * @param apiKey
	 *            API key
	 */
	public Members(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public String auth(String login, String password) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("login", login);
		params.put("password", Utils.getMD5(password));
		JSONObject object = UtilsJson.executeQuery("members/auth", apiKey,
				params);
		if (!UtilsJson.hasErrors(object)) {
			return UtilsJson
					.getJSONStringFromPath(object, "/root/member/token");
		}
		return null;
	}

	@Override
	public boolean destroy(String token) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		UtilsJson.executeQuery("members/destroy", apiKey, params);
		// TODO Check for error
		return true;
	}

	@Override
	public int getDateCache(String token, boolean identifieduser) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Episode> getEpisodes(String token,
			SubtitleLanguage subtitleLanguage) {
		String lang = null;
		switch (subtitleLanguage) {
		case VF:
			lang = "vf";
			break;
		case VOVF:
			lang = "vovf";
			break;
		case ALL:
			lang = "all";
			break;
		}
		List<Episode> result = new LinkedList<Episode>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		JSONObject jsonObject = UtilsJson.executeQuery("members/episodes/"
				+ lang, apiKey, params);
		try {
			JSONArray episodes = UtilsJson.getJSONArrayFromPath(jsonObject,
					"/root/episodes");
			int length = episodes.length();
			for (int i = 0; i < length; i++) {
				JSONObject episode = episodes.getJSONObject(i);
				result.add(EpisodeFactory.createEpisode(episode));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Return the information about the user. If it is the identified user,
	 * identifiedUser has to be true and user has to be the token. If
	 * identifiedUser is false, then the user is the login of the user to
	 * retrieve.
	 * 
	 * @param user
	 *            User or token to retrieve
	 * @param identifiedUser
	 *            If user if the user or the token
	 * @return Member informations
	 */
	private Member getInfosForUser(String user, boolean identifiedUser) {
		JSONObject jsonObject;
		if (!identifiedUser) {
			jsonObject = UtilsJson
					.executeQuery("members/infos/" + user, apiKey);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", user);
			jsonObject = UtilsJson
					.executeQuery("members/infos", apiKey, params);
		}
		return MemberFactory.createMember(UtilsJson.getJSONObjectFromPath(
				jsonObject, "/root/member"));
	}

	@Override
	public List<Notification> getNotifications(String token, boolean seen,
			int nb, int lastId, SortType sort) {
		return getNotificationsWithParameters(token, seen, nb, lastId, sort);
	}

	@Override
	public List<Notification> getNotifications(String token, boolean seen,
			int nb, SortType sort) {
		return getNotificationsWithParameters(token, seen, nb, -1, sort);
	}

	@Override
	public List<Notification> getNotifications(String token, boolean seen, SortType sort) {
		return getNotificationsWithParameters(token, seen, -1, -1, sort);
	}

	@Override
	public List<Notification> getNotifications(String token, int nb, SortType sort) {
		return getNotificationsWithParameters(token, null, nb, -1, sort);
	}

	/**
	 * Return the notifications with the given parameter. Conditions for the
	 * parameters to be used:
	 * <ul>
	 * <li>seen has not to be null</li>
	 * <li>nb greater than 0</li>
	 * <li>lastId greater than 0</li>
	 * </ul>
	 * 
	 * @param seen
	 *            If the notification has to be already seen or not
	 * @param nb
	 *            Number of notification
	 * @param lastId
	 *            Start of notification
	 * @param sort
	 * @return List of notification
	 */
	private List<Notification> getNotificationsWithParameters(String token,
			Boolean seen, int nb, int lastId, SortType sort) {
		Map<String, String> params = new HashMap<String, String>();
		if (seen != null) {
			params.put("seen", seen ? "yes" : "no");
		}

		switch (sort) {
		case ASC:
			params.put("sort", "asc");
			break;
		case DESC:
			params.put("sort", "desc");
		default:
		}

		if (nb > 0) {
			params.put("number", "" + nb);
		}
		if (lastId > 0) {
			params.put("last_id", "" + lastId);
		}
		params.put("token", token);
		List<Notification> notifications = new LinkedList<Notification>();
		JSONObject jsonObject = UtilsJson.executeQuery("members/notifications",
				apiKey, params);
		JSONArray notificationsArray = UtilsJson.getJSONArrayFromPath(
				jsonObject, "/root/notifications");
		try {
			int length = notificationsArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject notification = notificationsArray.getJSONObject(i);
				notifications.add(NotificationFactory
						.createNotification(notification));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return notifications;
	}

	@Override
	public Member infos(String token) {
		return getInfosForUser(token, true);
	}

	@Override
	public Member infos(String token, int lastCache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Member infosOfUser(String user) {
		return getInfosForUser(user, false);
	}

	@Override
	public Member infosOfUser(String user, int lastCache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActive(String token) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		JSONObject jsonObject = UtilsJson.executeQuery("members/is_active",
				apiKey, params);
		return !UtilsJson.hasErrors(jsonObject);
	}

	@Override
	public boolean resetViewedShow(String token, String url) {
		return setWatched(token, url, 0, 0);
	}

	@Override
	public boolean setDownloaded(String token, String url, int season, int episode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("season", "" + season);
		params.put("episode", "" + episode);
		params.put("token", token);
		UtilsJson.executeQuery("members/downloaded/" + url, apiKey, params);
		return true;
	}

	@Override
	public boolean setWatched(String token, String url, int season, int episode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("season", "" + season);
		params.put("episode", "" + episode);
		params.put("token", token);
		UtilsJson.executeQuery("members/watched/" + url, apiKey, params);
		// TODO Check for error
		return true;
	}

	@Override
	public boolean signup(String login, String password, String email) {
		throw new NotImplementedException();
	}

}
