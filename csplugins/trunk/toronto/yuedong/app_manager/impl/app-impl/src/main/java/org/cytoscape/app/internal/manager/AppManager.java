package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.event.AppsChangedEvent;
import org.cytoscape.app.internal.event.AppsChangedListener;
import org.cytoscape.app.internal.exception.AppInstallException;
import org.cytoscape.app.internal.exception.AppParsingException;
import org.cytoscape.app.internal.exception.AppUninstallException;
import org.cytoscape.app.internal.net.WebQuerier;
import org.cytoscape.application.CyApplicationConfiguration;

/**
 * This class represents an App Manager, which is capable of maintaining a list of all currently installed and available apps. The class
 * also provides functionalities for installing and uninstalling apps.
 */
public class AppManager {
	/** Only files with these extensions are checked when looking for apps in a given subdirectory.
	 */
	private static final String[] APP_EXTENSIONS = {"jar"};
	
	/** Installed apps are copied to this subdirectory under the local app storage directory. */
	private static final String INSTALLED_APPS_DIRECTORY_NAME = "installed";
	
	/** Uninstalled apps are copied to this subdirectory under the local app storage directory. */
	private static final String UNINSTALLED_APPS_DIRECTORY_NAME = "uninstalled";
	
	/** This subdirectory in the local Cytoscape storage directory is used to store app data, as 
	 * well as installed and uninstalled apps. */
	private static final String APPS_DIRECTORY_NAME = "3.0/apps";
	
	/** The set of all apps, represented by {@link App} objects, registered to this App Manager. */
	private Set<App> apps;
	
	private Set<AppsChangedListener> appListeners;
	
	/** An {@link AppParser} object used to parse File objects and possibly URLs into {@link App} objects
	 * into a format we can more easily work with
	 */
	private AppParser appParser;
	
	/**
	 * A reference to the {@link WebQuerier} object used to make queries to the app store website.
	 */
	private WebQuerier webQuerier;
	
	/**
	 * {@link CyApplicationConfiguration} service used to obtain the directories used to store the apps.
	 */
	private CyApplicationConfiguration applicationConfiguration;
	
	/**
	 * The {@link CyAppAdapter} service reference provided to the constructor of the app's {@link AbstractCyApp}-implementing class.
	 */
	private CyAppAdapter appAdapter;
	
	public AppManager(CyAppAdapter appAdapter, CyApplicationConfiguration applicationConfiguration, final WebQuerier webQuerier) {
		this.applicationConfiguration = applicationConfiguration;
		this.appAdapter = appAdapter;
		this.webQuerier = webQuerier;
		
		apps = new HashSet<App>();
		
		appParser = new AppParser();
		
		initializeAppsDirectories();
		
		this.appListeners = new HashSet<AppsChangedListener>();

		// Install previously enabled apps
		installAppsInDirectory(new File(getInstalledAppsPath()));
		
		// Load apps from the "uninstalled apps" directory
		Set<App> uninstalledApps = obtainAppsFromDirectory(new File(getUninstalledAppsPath()));
		apps.addAll(uninstalledApps);
	}
	
	public CyAppAdapter getAppAdapter() {
		return appAdapter;
	}
	
	public AppParser getAppParser() {
		return appParser;
	}
	
	public WebQuerier getWebQuerier() {
		return webQuerier;
	}
	
	/**
	 * Registers an app to this app manager.
	 * @param app The app to register to this manager.
	 */
	public void addApp(App app) {
		apps.add(app);
	}
	
	/**
	 * Removes an app from this app manager.
	 * @param app The app to remove
	 */
	public void removeApp(App app) {
		apps.remove(app);
	}
	
	/**
	 * Attempts to install an app. Makes a copy of the app file and places it in the directory 
	 * used to hold all installed and uninstalled apps, if it was not already present there. Then, the 
	 * app is created by instancing its class that extends {@link AbstractCyApp}.
	 * 
	 * Before the app is installed, it is checked if it contains valid packaging by its isAppValidated() method.
	 * Apps that have not been validated are ignored. Also, apps that are already installed are left alone.
	 * 
	 * @param app The {@link App} object representing and providing information about the app to install
	 * @throws AppMoveException If there was an IO-related error during the copy operation that prevents the app from 
	 * being successfully installed.
	 */
	public void installApp(App app) {
		
		try {
			app.install(this);
		} catch (AppInstallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Let the listeners know that an app has been installed
		for (AppsChangedListener appListener : appListeners) {
			AppsChangedEvent appEvent = new AppsChangedEvent(this);
			appListener.appsChanged(appEvent);
		}
	}
	
	/**
	 * Uninstalls an app. If it was located in the subdirectory containing currently installed apps in the
	 * local storage directory, it will be moved to the subdirectory containing currently uninstalled apps.
	 * 
	 * The app will only be uninstalled if it is currently installed.
	 * 
	 * @param app The app to be uninstalled.
	 * @throws AppMoveException If there was an error while moving the app from the installed apps subdirectory
	 * to the subdirectory containing currently uninstalled apps.
	 */
	public void uninstallApp(App app) {
		
		try {
			app.uninstall(this);
		} catch (AppUninstallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Let the listeners know that an app has been uninstalled
		for (AppsChangedListener appListener : appListeners) {
			AppsChangedEvent appEvent = new AppsChangedEvent(this);
			appListener.appsChanged(appEvent);
		}
	}
		
	/**
	 * Return the set of all apps registered to this app manager.
	 * @return The set of all apps registered to this app manager.
	 */
	public Set<App> getApps() {
		return apps;
	}
	
	/**
	 * Return the path of the directory used to contain all apps.
	 * @return The path of the root directory containing all installed and uninstalled apps.
	 */
	private File getBaseAppPath() {
		File baseAppPath = null;
		
		// TODO: At time of writing, CyApplicationConfiguration always returns the home directory for directory location.
		try {
			baseAppPath = new File(applicationConfiguration.getConfigurationDirectoryLocation().getCanonicalPath() 
					+ File.separator + APPS_DIRECTORY_NAME);
		} catch (IOException e) {
			throw new RuntimeException("Unabled to obtain canonical path for Cytoscape local storage directory: " + e.getMessage());
		}
		
		return baseAppPath;
	}
	
	/**
	 * Return the canonical path of the subdirectory in the local storage directory containing installed apps.
	 * @return The canonical path of the subdirectory in the local storage directory containing currently installed apps,
	 * or <code>null</code> if there was an error obtaining the canonical path.
	 */
	public String getInstalledAppsPath() {
		try {
			return getBaseAppPath().getCanonicalPath() + File.separator + INSTALLED_APPS_DIRECTORY_NAME;
		} catch (IOException e) {
			// TODO: Record error in logger
			return null;
		}
	}
	
	/**
	 * Return the canonical path of the subdirectory in the local storage directory containing uninstalled apps.
	 * @return The canonical path of the subdirectory in the local storage directory containing uninstalled apps,
	 * or <code>null</code> if there was an error obtaining the canonical path.
	 */
	public String getUninstalledAppsPath() {
		try {
			return getBaseAppPath().getCanonicalPath() + File.separator + UNINSTALLED_APPS_DIRECTORY_NAME;
		} catch (IOException e) {
			// TODO: Record error in logger
			return null;
		}
	}
	
	private void installAppsInDirectory(File directory) {

		// Parse App objects from the given directory
		Set<App> parsedApps = obtainAppsFromDirectory(directory);
		
		// Install each app
		for (App parsedApp : parsedApps) {
			installApp(parsedApp);
		}
		
		System.out.println("Number of apps installed from directory: " + parsedApps.size());
	}
	
	/**
	 * Obtain a set of {@link App} objects through attempting to parse files found in the first level of the given directory.
	 * @param directory The directory used to parse {@link App} objects
	 * @return A set of all {@link App} objects that were successfully parsed from files in the given directory
	 */
	private Set<App> obtainAppsFromDirectory(File directory) {
		// Obtain all files in the given directory with supported extensions, perform a non-recursive search
		Collection<File> files = FileUtils.listFiles(directory, APP_EXTENSIONS, false); 
		
		Set<App> parsedApps = new HashSet<App>();
		
		App app;
		for (File potentialApp : files) {
			app = null;
			try {
				app = appParser.parseApp(potentialApp);
			} catch (AppParsingException e) {
				System.out.println("Failed to parse " + potentialApp + ", error: " + e.getMessage());
			} finally {
				if (app != null) {
					parsedApps.add(app);
					
					System.out.println("App parsed: " + app);
				}
			}
		}
		
		return parsedApps;
	}
	
	/**
	 * Create app storage directories if they don't already exist.
	 */
	private void initializeAppsDirectories() {
		boolean created = true;
		
		File appDirectory = getBaseAppPath();
		if (!appDirectory.exists()) {
			created = created && appDirectory.mkdir();
		}
		
		File installedDirectory = new File(getInstalledAppsPath());
		if (!installedDirectory.exists()) {
			created = created && installedDirectory.mkdir();
		}
		
		File uninstalledDirectory = new File(getUninstalledAppsPath());
		if (!uninstalledDirectory.exists()) {
			created = created && uninstalledDirectory.mkdir();
		}
		
		if (!created) {
			throw new RuntimeException("Failed to create local app storage directories.");
		}
	}
	
	public void addAppListener(AppsChangedListener appListener) {
		appListeners.add(appListener);
	}
	
	public  void removeAppListener(AppsChangedListener appListener) {
		appListeners.remove(appListener);
	}
	
	public void installAppsFromDirectory() {
		installAppsInDirectory(new File(getInstalledAppsPath()));
	}
}
