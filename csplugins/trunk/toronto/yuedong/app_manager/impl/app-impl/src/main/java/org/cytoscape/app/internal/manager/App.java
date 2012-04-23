package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
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

	private URL appStoreUrl;
	
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
	
	/**
	 * Default app installation method that can be used by classes extending this class.
	 * 
	 * Attempts to install an app by copying it to the installed apps directory,
	 * creating an instance of the app's class that extends the {@link AbstractCyApp} class,
	 * and registering it with the given {@link AppManager} object. The app is instanced by
	 * calling its createAppInstance() method.
	 * 
	 * @param appManager The AppManager used to register this app with.
	 * @throws AppInstallException If there was an error while attempting to install the app such
	 * as improper app packaging, failure to copy the file to the installed apps directory, 
	 * or failure to create an instance of the app.
	 */
	protected void defaultInstall(AppManager appManager) throws AppInstallException {
		// Check if the app has been verified to contain proper packaging.
		if (!this.isAppValidated()) {
			
			// If the app is not packaged properly or is missing fields in its manifest file, do not install the app
			// as the install operation will fail.
			throw new AppInstallException("Cannot install app; app file has not been checked to comply with app specifications");
		}
		
		// Check if the app has already been installed.
		if (this.getStatus() == AppStatus.INSTALLED) {
			
			// Do nothing if it is already installed
			throw new AppInstallException("This app has already been installed.");
		}
		
		// Obtain the paths to the local storage directories for holding installed and uninstalled apps.
		String installedAppsPath = appManager.getInstalledAppsPath();
		String uninstalledAppsPath = appManager.getUninstalledAppsPath();
		
		// Attempt to copy the app to the directory for installed apps.
		try {
			File appFile = this.getAppFile();
			
			// Only perform the copy if the app was not already in the target directory
			if (!appFile.getParentFile().getCanonicalPath().equals(installedAppsPath)) {
				
				// Uses Apache Commons library; overwrites files with the same name.
				FileUtils.copyFileToDirectory(appFile, new File(installedAppsPath));
				
				// If we copied it from the uninstalled apps directory, remove it from that directory
				if (appFile.getParentFile().getCanonicalPath().equals(uninstalledAppsPath)) {
					appFile.delete();
				}
				
				// Update the app's path
				String fileName = this.getAppFile().getName();
				this.setAppFile(new File(installedAppsPath + File.separator + fileName));
			}
		} catch (IOException e) {
			throw new AppInstallException("Unable to copy app file to installed apps directory: " + e.getMessage());
		}
	
		// Create an app instance only if one was not already created
		if (this.getAppInstance() == null) {
			Object appInstance;
			try {
				appInstance = createAppInstance(appManager.getAppAdapter());
			} catch (AppInstanceException e) {
				throw new AppInstallException("Unable to create app instance: " + e.getMessage());
			}
			
			// Keep a reference to the newly created instance
			this.setAppInstance((AbstractCyApp) appInstance);
		}
		
		this.setStatus(AppStatus.INSTALLED);
		appManager.addApp(this);
	}
	
	/**
	 * Default app uninstallation method that can be used by classes extending this class.
	 * 
	 * The default app uninstallation procedure consists of simply moving the app to the uninstalled apps
	 * directory.
	 * 
	 * @param appManager The app manager responsible for managing apps, which is used to obtain
	 * the path of the storage directories containing the installed and uninstalled apps
	 * @throws AppUninstallException If there was an error while uninstalling the app, such as
	 * attempting to uninstall an app that is not installed, or failure to move the app to
	 * the uninstalled apps directory
	 */
	protected void defaultUninstall(AppManager appManager) throws AppUninstallException {
		// Check if the app is installed before attempting to uninstall.
		if (this.getStatus() != AppStatus.INSTALLED) {
			// If it is not installed, do not attempt to uninstall it.
			throw new AppUninstallException("App is not installed; cannot uninstall.");
		}
		
		// Check if the app is inside the directory containing currently installed apps.
		// If so, prepare to move it to the uninstalled directory.
		File appParentDirectory = this.getAppFile().getParentFile();
		try {
			// Obtain the path of the "uninstalled apps" subdirectory.
			String uninstalledAppsPath = appManager.getUninstalledAppsPath();
			
			if (appParentDirectory.getCanonicalPath().equals(
					appManager.getInstalledAppsPath())) {
				
				// Use the Apache commons library to copy over the file, overwriting existing files.
				try {
					FileUtils.copyFileToDirectory(this.getAppFile(), new File(uninstalledAppsPath));
				} catch (IOException e) {
					throw new AppUninstallException("Unable to move file: " + e.getMessage());
				}
				
				// Delete the source file after the copy operation
				String fileName = this.getAppFile().getName();
				this.getAppFile().delete();
				this.setAppFile(new File(uninstalledAppsPath + File.separator + fileName));				
			}
		} catch (IOException e) {
			throw new AppUninstallException("Unable to obtain path: " + e.getMessage());
		}
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
	
	public URL getAppStoreUrl() {
		return appStoreUrl;
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
	
	public void setAppStoreUrl(URL appStoreURL) {
		this.appStoreUrl = appStoreURL;
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
