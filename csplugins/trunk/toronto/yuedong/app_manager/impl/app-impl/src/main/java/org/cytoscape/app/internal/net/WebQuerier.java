package org.cytoscape.app.internal.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
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
	private Set<String> appTags;
	
	/** A reference to the result obtained by the last successful query for all app identifier names. */
	private Set<String> appNames;
	
	/** 
	 * A reference to the result obtained by the last successful query for information about all
	 * available apps. 
	 */
	private Set<WebApp> apps;
	
	public WebQuerier(StreamUtil streamUtil) {
		this.streamUtil = streamUtil;
		
		appTags = null;
		appNames = null;
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
	public Set<String> getAllTags() {
		// If we have a cached result from the previous query, use that one
		if (appTags != null) {
			return appTags;
		}
		
		Set<String> tagNames = new HashSet<String>();
		
		try {
			String jsonResult = query(APP_STORE_URL + "apps/");
			
			JSONArray jsonArray = new JSONArray(jsonResult);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				
				tagNames.add(jsonObject.getString("name"));
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
		appTags = tagNames;
		return tagNames;
	}
	
	/**
	 * Return the set of all unique app names available on the app store. These
	 * names can be used to query for further app information about each app.
	 * 
	 * @return The set of all unique app names available on the app store website.
	 */
	public Set<String> getAllAppNames() {
		// If we have a cached result from the previous query, use that one
		if (appNames != null) {
			return appNames;
		}
		
		Set<String> tagNames = getAllTags();
		Set<String> appNames = new HashSet<String>();

		try {
			String jsonResult = null;
			
			for (String tagName : tagNames) {
				// Obtain app names from website
				jsonResult = query(APP_STORE_URL + "apps/with_tag/" + tagName);
				
				// Parse JSON result
				JSONArray jsonArray = new JSONArray(jsonResult);
				
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					
					appNames.add(jsonObject.getString("name"));
				}
				
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
		this.appNames = appNames;
		return appNames;
	}
	
	public Set<WebApp> getAllApps() {
		// If we have a cached result from the previous query, use that one
		if (apps != null) {
			return apps;
		}
		
		System.out.println("Obtaining apps from app store..");
		
		Set<WebApp> result = new HashSet<WebApp>();
		Set<String> appNames = getAllAppNames();
		
		String jsonResult = null;
		for (String appName : appNames) {
			try {
				// Obtain information about the app from the website
				jsonResult = query(APP_STORE_URL + "apps/" + appName);
				
				// Parse the JSON result
				JSONObject jsonObject = new JSONObject(jsonResult);
				
				WebApp webApp = new WebApp();
				webApp.setName(jsonObject.get("name").toString());
				webApp.setFullName(jsonObject.get("fullname").toString());
				webApp.setDescription(jsonObject.get("description").toString());
				webApp.setDetails(jsonObject.get("details").toString());
				webApp.setIconUrl(jsonObject.get("icon_url").toString());
				webApp.setAppUrl(APP_STORE_URL + "apps/" + appName);
				result.add(webApp);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("Error parsing JSON: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		System.out.println(result.size() + " apps found from web store.");
		
		// Cache the result of this query
		this.apps = result;
		return result;
	}
}
