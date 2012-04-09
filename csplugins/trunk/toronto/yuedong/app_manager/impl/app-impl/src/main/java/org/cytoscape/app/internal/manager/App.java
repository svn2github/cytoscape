package org.cytoscape.app.internal.manager;

import java.io.File;
import java.net.URL;

import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.internal.AppVersionUtils;

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
	 * The fully-qualified name of the app's class that extends {@link AbstractCyApp} to be instantiated when the app is loaded.
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
	
	/**
	 * Whether we've found the official name of the app as opposed to using an inferred name.
	 */
	private boolean officialNameObtained;
	
	/**
	 * Whether or not the app is a simple app as opposed to an OSGi bundle-based app.
	 */
	private boolean isSimpleApp;
	
	private AppStatus status;
	
	/**
	 * An enumeration that indicates the status of a given app, such as whether it is installed or uninstalled.
	 */
	public enum AppStatus{
		INSTALLED("Installed"),
		TO_BE_UNINSTALLED("Uninstall on Restart"),
		UNINSTALLED("Uninstalled");
		
		String readableStatus;
		
		private AppStatus(String readableStatus) {
			this.readableStatus = readableStatus;
		}
		
		@Override
		public String toString() {
			return readableStatus;
		}
	}
	
	public App() {
		this("", "", "", "", null);
	}
	
	public App(String appName, String version, String authors, String description, File appFile) {
		this.appName = appName;
		this.version = version;
		this.authors = authors;
		this.description = description;
		this.appFile = appFile;
		
		appValidated = false;
		officialNameObtained = false;
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
	
	public boolean isOfficialNameObtained() {
		return officialNameObtained;
	}
	
	public boolean isSimpleApp() {
		return isSimpleApp;
	}
	
	public AppStatus getStatus() {
		return status;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setAppFile(File appFile) {
		this.appFile = appFile;
	}
	
	public void setEntryClassName(String entryClassName) {
		this.entryClassName = entryClassName;
	}
	
	public void setJarURL(URL jarURL) {
		this.jarURL = jarURL;
	}
	
	public void setAppInstance(AbstractCyApp appInstance) {
		this.appInstance = appInstance;
	}

	public void setAppValidated(boolean appValidated) {
		this.appValidated = appValidated;
	}

	public void setOfficialNameObtained(boolean officialNameObtained) {
		this.officialNameObtained = officialNameObtained;
	}
	
	public void setSimpleApp(boolean isSimpleApp) {
		this.isSimpleApp = isSimpleApp;
	}
	
	public void setStatus(AppStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String result;
		
		result = "App: {name:" + appName + ", authors:" + authors + ", version:" + version + "}";
		
		return result;
	}







}
