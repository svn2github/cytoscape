package org.cytoscape.app.internal.manager;

import java.io.File;

import org.cytoscape.app.AbstractCyApp;

/**
 * This class represents an app, and contains all needed information about the app such as its name, version, 
 * authors list, description, and file path.
 */
public class App {
	
	private String appName;
	private String version;
	private String authors;
	private String description;
	private File appFile;
	
	/**
	 * The name of the app's class that extends {@link AbstractCyApp} to be instantiated when the app is loaded.
	 */
	private String entryClassName;
	
	public App(String appName, String version, String authors, String description, File appFile, String entryClassName) {
		this.appName = appName;
		this.version = version;
		this.authors = authors;
		this.description = description;
		this.appFile = appFile;
		this.entryClassName = entryClassName;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getAuthors() {
		return authors;
	}
	
	public String getDescription() {
		return description;
	}
	
	public File getAppFile() {
		return appFile;
	}
	
	public String getEntryClassName() {
		return entryClassName;
	}
}
