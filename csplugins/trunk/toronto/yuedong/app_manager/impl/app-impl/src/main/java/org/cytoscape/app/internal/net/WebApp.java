package org.cytoscape.app.internal.net;

/**
 * This class is intended to be a container for information obtained about an app from the app store website.
 */
public class WebApp {
	
	/** Name of the app used by the app store site as a unique app identifier. */
	private String name;
	
	/** The name of the app that is displayed to the user. */
	private String fullName;
	
	/** A short description of the app. */
	private String description;
	
	/** Detailed information about the app, formatted in Markdown */
	private String details;
	
	/** The URL to the icon used to represent the app */
	private String iconUrl;

	/** The URL to the app's page on the app store website */
	private String appStoreUrl;
	
	/** The number of downloads recorded for this app */
	private int downloadCount;
	
	/** 
	 * Obtain the app name that is used as a unique identifier on the app store website 
	 * @return The unique representative name used by the app store website
	 */
	public String getName() {
		return name;
	}

	/** 
	 * Obtain the name of the app that is displayed to the user 
	 * @return The app name displayed to the user
	 */
	public String getFullName() {
		return fullName;
	}

	/** 
	 * Obtain a short description of the app 
	 * @return A short description of the app obtained from the app store website
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get detailed information about the app, formatted in Markdown
	 * @return Detailed app information, formatted in Markdown
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * Obtain the URL to the icon image for the app
	 * @return The URL of the app icon image
	 */
	public String getIconUrl() {
		return iconUrl;
	}
	
	/**
	 * Obtain the URL of the app's page on the app store website
	 * @return The URL to the app store page for this app
	 */
	public String getAppStoreUrl() {
		return appStoreUrl;
	}
	
	/**
	 * Obtain the download count for this app that was obtained from the app store website
	 * @return The download count for this app
	 */
	public int getDownloadCount() {
		return downloadCount;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	public void setAppStoreUrl(String appStoreUrl) {
		this.appStoreUrl = appStoreUrl;
	}
	
	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}
	
	@Override
	public String toString() {
		return fullName;
	}	
}
