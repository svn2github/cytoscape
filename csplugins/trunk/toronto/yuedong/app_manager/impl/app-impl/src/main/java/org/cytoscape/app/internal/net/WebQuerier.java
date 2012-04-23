package org.cytoscape.app.internal.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.cytoscape.io.util.StreamUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for querying the Cytoscape App Store web service to obtain
 * information about available apps and app updates.
 */
public class WebQuerier {
	
	private static final String APP_STORE_URL = "http://nrnb.org/cyappstore/";
	
	private static final String REQUEST_JSON_HEADER_KEY = "X-Requested-With";
	private static final String REQUEST_JSON_HEADER_VALUE = "XMLHttpRequest";
	
	private StreamUtil streamUtil;
	
	/** A reference to the result obtained by the last successful query for all available app tags. */
	private Set<AppTag> appTags;
	
	/** A reference to the result obtained by the last successful query for all available apps. */
	private Set<WebApp> apps;
	
	/**
	 * A class that represents a tag used for apps, containing information about the tag
	 * such as its unique name used on the app store website as well as its human-readable name.
	 */
	public class AppTag {
		
		/** A unique name of the tag used by the app store website as a tag identifier */
		private String name;
		
		/** The name of the tag that is shown to the user */
		private String fullName;
		
		/** The number of apps associated with this tag */
		private int count;
		
		public AppTag() {
		}
		
		/** Obtain the name of the tag, which is a unique name used by the app store website as an identifier */
		public String getName() {
			return name;
		}
		
		/** Obtain the name of the tag that is shown to the user */
		public String getFullName() {
			return fullName;
		}
		
		/** Obtain the number of apps known by the web store to be associated with this tag */
		public int getCount() {
			return count;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		
		public void setCount(int count) {
			this.count = count;
		}
	}
	
	public WebQuerier(StreamUtil streamUtil) {
		this.streamUtil = streamUtil;
		
		appTags = null;
		apps = null;
		
		/*
		Set<WebApp> webApps = getAllApps();
		
		System.out.println("Apps found: " + webApps.size());
		*/
	}
	
	/**
	 * Makes a HTTP query using the given URL and returns the response as a string.
	 * @param url The URL used to make the HTTP request
	 * @return The response, as a string
	 * @throws IOException If there was an error while attempting to make a connection
	 * to the given URL
	 */
	private String query(String url) throws IOException {
		// Convert the string url to a URL object
		URL parsedUrl = null;
		try {
			parsedUrl = new URL(url);
		} catch (MalformedURLException e) {
			throw new IOException("Malformed url, " + e.getMessage());
		}
		
		String result = null;
	
		HttpURLConnection connection = (HttpURLConnection) streamUtil.getURLConnection(parsedUrl);
		connection.setRequestProperty(REQUEST_JSON_HEADER_KEY, REQUEST_JSON_HEADER_VALUE);
		connection.connect();
		
		InputStream inputStream = connection.getInputStream();
		result = IOUtils.toString(inputStream, "UTF-8");
		
		connection.disconnect();
		
		return result;
	}
	
	/**
	 * Return the set of all tag names found on the app store. 
	 * @return The set of all available tag names
	 */
	public Set<AppTag> getAllTags() {
		// If we have a cached result from the previous query, use that one
		if (appTags != null) {
			return appTags;
		}
		
		Set<AppTag> tags = new HashSet<AppTag>();
		
		try {
			String jsonResult = query(APP_STORE_URL + "apps/tags");

			JSONArray jsonArray = new JSONArray(jsonResult);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				
				AppTag appTag = new AppTag();
				appTag.setName(jsonObject.get("name").toString());
				appTag.setFullName(jsonObject.get("fullname").toString());
				appTag.setCount(jsonObject.getInt("count"));
				tags.add(appTag);
//				System.out.println("Found tag url identifier: " + jsonObject.get("name"));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing JSON: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Cache the result of this query
		appTags = tags;
		return tags;
	}
	
	public Set<WebApp> getAllApps() {
		// If we have a cached result from the previous query, use that one
		if (apps != null) {
			return apps;
		}
		
		System.out.println("Obtaining apps from app store..");
		
		Set<WebApp> result = new HashSet<WebApp>();
		
		String jsonResult = null;
		try {
			// Obtain information about the app from the website
			jsonResult = query(APP_STORE_URL + "apps/");
			
			// Parse the JSON result
			JSONArray jsonArray = new JSONArray(jsonResult);
			JSONObject jsonObject = null;
			
			for (int index = 0; index < jsonArray.length(); index++) {
				jsonObject = jsonArray.getJSONObject(index);
				
				WebApp webApp = new WebApp();
				webApp.setName(jsonObject.get("name").toString());
				webApp.setFullName(jsonObject.get("fullname").toString());
				webApp.setDescription(jsonObject.get("description").toString());
				webApp.setIconUrl(jsonObject.get("icon_url").toString());
				webApp.setDownloadCount(jsonObject.getInt("downloads"));
				webApp.setAppStoreUrl(APP_STORE_URL + "apps/" + webApp.getName());
				result.add(webApp);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing JSON: " + e.getMessage());
			e.printStackTrace();
		}
	
		
		System.out.println(result.size() + " apps found from web store.");
		
		// Cache the result of this query
		this.apps = result;
		return result;
	}
	
	public String getAppDescription(String appName) {
		// Obtain information about the app from the website
		String jsonResult = null;
		JSONObject jsonObject = null;
		
		try {
			jsonResult = query(APP_STORE_URL + "apps/" + appName);
			
			// Parse the JSON result
			jsonObject = new JSONObject(jsonResult);
			return jsonObject.get("description").toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing JSON: " + e.getMessage());
			e.printStackTrace();
		}
		
		return "";
	}
	
	public Set<WebApp> getAppsByTag(String tagName) {
		Set<WebApp> webApps = getAllApps();
		
		// Construct a map used to quickly obtain references to WebApp objects given the app's name.
		// The app's name is guaranteed to be unique by the app store website.
		Map<String, WebApp> appMap = new HashMap<String, WebApp>();
		for (WebApp webApp : webApps) {
			appMap.put(webApp.getName(), webApp);
		}
		
		Set<WebApp> result = new HashSet<WebApp>();
		
		// Query for apps that match the given tag
		String jsonResult = null;
		try {
			// Obtain information about the app from the website
			jsonResult = query(APP_STORE_URL + "apps/with_tag/" + tagName);
			
			// Parse the JSON result
			JSONArray jsonArray = new JSONArray(jsonResult);
			JSONObject jsonObject = null;
			
			for (int index = 0; index < jsonArray.length(); index++) {
				jsonObject = jsonArray.getJSONObject(index);
				
				String appName = jsonObject.get("name").toString();

				// Assume any app obtained by querying with the tag was
				// already obtained by the query for all available apps
				result.add(appMap.get(appName));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing JSON: " + e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
}
