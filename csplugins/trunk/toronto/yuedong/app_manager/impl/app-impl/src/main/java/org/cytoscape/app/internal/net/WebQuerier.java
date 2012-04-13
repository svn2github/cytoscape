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
	
	private StreamUtil streamUtil;
	
	public WebQuerier(StreamUtil streamUtil) {
		this.streamUtil = streamUtil;
		
		Set<String> appNames = getAllAppNames();
		
		for (String name : appNames) {
			System.out.println(name);
		}
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
		connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
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
		Set<String> tagNames = new HashSet<String>();
		
		try {
			String jsonResult = query(APP_STORE_URL + "apps/");
			
			JSONArray jsonArray = new JSONArray(jsonResult);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				
				tagNames.add(jsonObject.getString("name"));
//				System.out.println("Found tag url identifier: " + jsonObject.get("name"));
			}
			
			/*
			System.out.println("Parsed: " + jsonArray.toString());
			System.out.println("Index 1: " + jsonArray.get(1));
			System.out.println("Index 2: " + jsonArray.get(2));
			*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing JSON: " + e.getMessage());
			e.printStackTrace();
		}
		
		return tagNames;
	}
	
	/**
	 * Return the set of all unique app names available on the app store. These
	 * names can be used to query for further app information about each app.
	 * 
	 * @return The set of all unique app names available on the app store website.
	 */
	public Set<String> getAllAppNames() {
		
		Set<String> tagNames = getAllTags();
		Set<String> appNames = new HashSet<String>();

		try {
			String jsonResult = null;
			
			for (String tagName : tagNames) {
				jsonResult = query(APP_STORE_URL + "apps/with_tag/" + tagName);
				
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
		
		return appNames;
	}
}
