package org.cytoscape.app.internal.manager;

import java.io.File;
import java.net.URL;

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
	
	/**
	 * The URL to the jar file containing the app.
	 */
	private URL jarURL;
	
	/**
	 * A reference to the instance of the app's class that extends {@link AbstractCyApp}.
	 */
	private AbstractCyApp appInstance;
	
	/**
	 * Whether this App object represents an app that has been checked to have valid packaging (such as containing
	 * necessary tags in its manifest file) and contains valid fields, making it loadable by the {@link AppManager} service.
	 */
	private boolean appValidated;
	
	private AppStatus status;
	
	/**
	 * An enumeration that indicates the status of a given app, such as whether it is installed or uninstalled.
	 */
	public enum AppStatus{
		INSTALLED,
		TO_BE_UNINSTALLED
	}
	
	public App(String appName, String version, String authors, String description, File appFile, String entryClassName) {
		this.appName = appName;
		this.version = version;
		this.authors = authors;
		this.description = description;
		this.appFile = appFile;
		this.entryClassName = entryClassName;
		
		this.setAppValidated(false);
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
	
	public URL getJarURL() {
		return jarURL;
	}

	public AbstractCyApp getAppInstance() {
		return appInstance;
	}

	public boolean isAppValidated() {
		return appValidated;
	}
	
	public AppStatus getStatus() {
		return status;
	}
	
	public void setAppFile(File appFile) {
		this.appFile = appFile;
	}
	
	public void setAppInstance(AbstractCyApp appInstance) {
		this.appInstance = appInstance;
	}

	public void setAppValidated(boolean appValidated) {
		this.appValidated = appValidated;
	}

	public void setStatus(AppStatus status) {
		this.status = status;
	}



}
