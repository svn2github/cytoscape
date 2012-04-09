package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.event.AppEvent;
import org.cytoscape.app.internal.event.AppListener;
import org.cytoscape.app.internal.exception.AppCopyException;
import org.cytoscape.app.internal.exception.AppParsingException;
import org.cytoscape.app.internal.manager.App.AppStatus;
import org.cytoscape.application.CyApplicationConfiguration;

/**
 * This class represents an AppManager, which is capable of maintaining a list of all currently installed and available apps. The class
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
	private static final String APPS_DIRECTORY_NAME = "apps";
	
	private Set<App> installedApps;
	private Set<App> toBeUninstalledApps;
	private Set<App> uninstalledApps;
	
	private Set<AppListener> appListeners;
	
	/** An {@link AppParser} object used to parse File objects and possibly URLs into {@link App} objects
	 * into a format we can more easily work with
	 */
	private AppParser appParser;
	
	/**
	 * {@link CyApplicationConfiguration} service used to obtain the directories used to store the apps.
	 */
	private CyApplicationConfiguration applicationConfiguration;
	
	/**
	 * The {@link CyAppAdapter} service reference provided to the constructor of the app's {@link AbstractCyApp}-implementing class.
	 */
	private CyAppAdapter appAdapter;
	
	public AppManager(CyAppAdapter appAdapter, CyApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
		this.appAdapter = appAdapter;
		
		installedApps = new HashSet<App>();
		toBeUninstalledApps = new HashSet<App>();
		uninstalledApps = new HashSet<App>();
		
		appParser = new AppParser();
		
		initializeAppsDirectories();
		
		System.out.println("Installed apps path: " + getInstalledAppsPath());
		System.out.println("Uninstalled apps path: " + getUninstalledAppsPath());
		
		/*
		try {
			App app = appParser.parseApp(new File(getBaseAppPath().getCanonicalPath() + File.separator + "CytoscapeTestSimpleApp.jar"));
			installApp(app);
		} catch (AppParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AppCopyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		this.appListeners = new HashSet<AppListener>();
		
		installAppsInDirectory(new File(getInstalledAppsPath()));
	}
	
	public AppParser getAppParser() {
		return appParser;
	}
	
	/**
	 * Attempts to install an app. Makes a copy of the app file and places it in the directory 
	 * used to hold all installed and uninstalled apps, if it was not already present there. Then, the 
	 * app is created by instancing its class that extends {@link AbstractCyApp}.
	 * 
	 * Before the app is installed, it is checked if it contains valid packaging by its isAppValidated() method.
	 * Apps that have not been validated are ignored.
	 * 
	 * @param app The {@link App} object representing and providing information about the app to install
	 * @throws AppCopyException If there was an IO-related error during the copy operation that prevents the app from 
	 * being successfully installed.
	 */
	public void installApp(App app) throws AppCopyException {
		
		// Check if the app has been verified to contain proper packaging.
		if (!app.isAppValidated()) {
			
			// If the app is not packaged properly or is missing fields in its manifest file, do not install the app
			// as the install operation will fail.
			return;
		}
		
		// Attempt to copy the app to the directory for installed apps.
		try {
			File installedAppsDirectory = new File(getInstalledAppsPath());
			File appFile = app.getAppFile();
			
			// Only perform the copy if the app was not already in the target directory
			if (!FileUtils.directoryContains(installedAppsDirectory, appFile)) {
				
				// Uses Apache Commons library; overwrites files with the same name.
				FileUtils.copyFileToDirectory(appFile, installedAppsDirectory);
				
				// Update the app's path
				String fileName = app.getAppFile().getName();
				app.setAppFile(new File(getInstalledAppsPath() + File.separator + fileName));
			}
		} catch (IOException e) {
			throw new AppCopyException("Unable to copy file: " + e.getMessage());
		}
		
		// TODO: Currently uses the CyAppAdapter's loader to load apps' classes. Is there reason to use a different one?
		ClassLoader appClassLoader = new URLClassLoader(
				new URL[]{app.getJarURL()}, appAdapter.getClass().getClassLoader());
		
		String entryClassName = app.getEntryClassName();
		
		// Attempt to load the class
		Class<?> appEntryClass = null;
		try {
			 appEntryClass = appClassLoader.loadClass(entryClassName);
		} catch (ClassNotFoundException e) {
			
			throw new IllegalStateException("Class " + entryClassName + " not found in URL: " + app.getJarURL());
		}
		
		// Attempt to obtain the constructor
		Constructor<?> constructor = null;
		try {
			constructor = appEntryClass.getConstructor(CyAppAdapter.class);
		} catch (SecurityException e) {
			throw new IllegalStateException("Access to the constructor for " + appEntryClass + " denied.");
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Unable to find a constructor for " + appEntryClass + " that takes a CyAppAdapter as its argument.");
		}
		
		// Attempt to instantiate the app's class that extends AbstractCyActivator.
		Object appInstance = null;
		try {
			appInstance = constructor.newInstance(appAdapter);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Illegal arguments passed to the constructor for the app's entry class: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new RuntimeException("Error instantiating the class " + appEntryClass + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		// Keep a reference to the newly created instance
		app.setAppInstance((AbstractCyApp) appInstance);
		app.setStatus(AppStatus.INSTALLED);
		
		installedApps.add(app);
		
		// Let the listeners know that an app has been installed
		for (AppListener appListener : appListeners) {
			AppEvent appEvent = new AppEvent(this, app);
			appListener.appInstalled(appEvent);
		}
	}
	
	/**
	 * Uninstalls an app. If it was located in the subdirectory containing currently installed apps in the
	 * local storage directory, it will be moved to the subdirectory containing currently uninstalled apps.
	 * 
	 * The app will only be uninstalled if it is currently installed.
	 * 
	 * @param app The app to be uninstalled.
	 * @throws AppCopyException If there was an error while moving the app from the installed apps subdirectory
	 * to the subdirectory containing currently uninstalled apps.
	 */
	public void uninstallApp(App app) throws AppCopyException {
		// Check if the app is installed before attempting to uninstall.
		if (app.getStatus() != AppStatus.INSTALLED) {
			// If it is not installed, do not attempt to uninstall it.
			return;
		}
		
		// Check if the app is inside the directory containing currently installed apps.
		// If so, prepare to move it to the uninstalled directory.
		File appParentDirectory = app.getAppFile().getParentFile();
		try {
			// Obtain the path of the "installed apps" subdirectory.
			String uninstalledAppsPath = getUninstalledAppsPath();
			
			if (appParentDirectory.getCanonicalPath().equals(
					getInstalledAppsPath())) {
				
				// Use the Apache commons library to copy over the file, overwriting an existing file if needed.
				try {
					FileUtils.copyFileToDirectory(app.getAppFile(), new File(uninstalledAppsPath));
				} catch (IOException e) {
					throw new AppCopyException("Unable to copy file: " + e.getMessage());
				}
				
				// Delete the source file after the copy operation
				String fileName = app.getAppFile().getName();
				app.getAppFile().delete();
				app.setAppFile(new File(uninstalledAppsPath + File.separator + fileName));
				
				// Simple apps require a Cytoscape restart to be uninstalled
				app.setStatus(AppStatus.TO_BE_UNINSTALLED);
				
				installedApps.remove(app);
				toBeUninstalledApps.add(app);
				
				// Let the listeners know that an app has been uninstalled
				for (AppListener appListener : appListeners) {
					AppEvent appEvent = new AppEvent(this, app);
					appListener.appUninstalled(appEvent);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to obtain path: " + e.getMessage());
		}
	}
	
	/**
	 * Return the set of all currently installed apps.
	 * @return
	 */
	public Set<App> getInstalledApps() {
		return installedApps;
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
	 * @return The canonical path of the subdirectory in the local storage directory containing currently installed apps.
	 */
	private String getInstalledAppsPath() {
		try {
			return getBaseAppPath().getCanonicalPath() + File.separator + INSTALLED_APPS_DIRECTORY_NAME;
		} catch (IOException e) {
			throw new RuntimeException("Unable to obtain canonical path for installed apps directory: " + e.getMessage());
		}
	}
	
	/**
	 * Return the canonical path of the subdirectory in the local storage directory containing uninstalled apps.
	 * @return The canonical path of the subdirectory in the local storage directory containing uninstalled apps.
	 */
	private String getUninstalledAppsPath() {
		try {
			return getBaseAppPath().getCanonicalPath() + File.separator + UNINSTALLED_APPS_DIRECTORY_NAME;
		} catch (IOException e) {
			throw new RuntimeException("Unable to obtain canonical path for uninstalled apps directory: " + e.getMessage());
		}
	}
	
	private void installAppsInDirectory(File directory) {

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
		
		for (App parsedApp : parsedApps) {
			try {
				installApp(parsedApp);
			} catch (AppCopyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.installedApps.addAll(parsedApps);
		
		System.out.println("Number of apps installed from directory: " + parsedApps.size());
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
	
	public void addAppListener(AppListener appListener) {
		appListeners.add(appListener);
	}
	
	public  void removeAppListener(AppListener appListener) {
		appListeners.remove(appListener);
	}
	
	public void installAppsFromDirectory() {
		installAppsInDirectory(new File(getInstalledAppsPath()));
	}
}
