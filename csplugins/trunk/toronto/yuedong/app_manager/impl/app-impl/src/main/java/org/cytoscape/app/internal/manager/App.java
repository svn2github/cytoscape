package org.cytoscape.app.internal.manager;

import java.io.File;
import java.net.URL;

import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.exception.AppInstallException;
import org.cytoscape.app.internal.exception.AppInstanceException;
import org.cytoscape.app.internal.exception.AppUninstallException;

/**
 * This class represents an app, and contains all needed information about the app such as its name, version, 
 * authors list, description, and file path (if present).
 */
public abstract class App {
	
	private String appName;
	private String version;
	private String authors;
	private String description;
	private File appFile;
	
	/**
	 * The fully-qualified name of the app's class that extends {@link AbstractCyApp} to be instantiated when the app is loaded.
	 */
	private String entryClassName;

	private URL appStoreURL;
	
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
		this.appName = "";
		this.version = "";
		this.authors = "";
		this.description = "";
		this.appFile = null;
		
		appValidated = false;
		officialNameObtained = false;
		this.status = AppStatus.UNINSTALLED;
	}
	
	/**
	 * Creates an instance of this app, such as by instancing the app's class that extends AbstractCyApp,
	 * and returns an instance to it.
	 * @param appAdapter A reference to the {@link CyAppAdapter} service used to provide the newly
	 * created app instance with access to the Cytoscape API
	 * @return A reference to the instance of the app's class that extends AbstractCyApp.
	 * @throws AppInstanceException If there was an error while instancing the app, such as not being able to
	 * locate the class to be instanced.
	 */
	public abstract Object createAppInstance(CyAppAdapter appAdapter) throws AppInstanceException;
	
	/**
	 * Installs this app by creating an instance of its class that extends AbstractCyApp, copying itself
	 * over to the local Cytoscape app storage directory using the directory path obtained from the given 
	 * {@link AppManager} if needed, and registering it to the {@link AppManager}.
	 * @param appManager The AppManager used to register this app.
	 * @throws AppInstallException If there was an error while installing the app such as being unable to copy
	 * over the app file.
	 */
	public abstract void install(AppManager appManager) throws AppInstallException;
	
	/**
	 * Uninstalls this app by unloading its classes if possible, and copying itself over to
	 * the local Cytoscape app storage directory for uninstalled apps using the path obtained from the
	 * given {@link AppManager}.
	 * @param appManager The AppManager used to register this app.
	 * @throws AppUninstallException If there was an error while uninstalling the app, such as attemping
	 * to uninstall an app that isn't installed, or being unable to move the app file to the uninstalled
	 * apps directory.
	 */
	public abstract void uninstall(AppManager appManager) throws AppUninstallException;
	
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
	
	public URL getAppStoreURL() {
		return appStoreURL;
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
	
	public void setAppStoreURL(URL appStoreURL) {
		this.appStoreURL = appStoreURL;
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
	
	public void setStatus(AppStatus status) {
		this.status = status;
	}
}
