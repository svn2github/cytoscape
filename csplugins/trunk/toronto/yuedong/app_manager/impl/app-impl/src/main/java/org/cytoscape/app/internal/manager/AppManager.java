package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.exception.AppCopyException;
import org.cytoscape.app.internal.manager.App.AppStatus;
import org.cytoscape.application.CyApplicationConfiguration;

/**
 * This class represents an AppManager, which is capable of maintaining a list of all currently installed and available apps. The class
 * also provides functionalities for installing and uninstalling apps.
 */
public class AppManager {
	/** Installed apps are copied to this subdirectory under the local app storage directory. */
	private static final String INSTALLED_APPS_DIRECTORY_NAME = "Installed";
	
	/** Uninstalled apps are copied to this subdirectory under the local app storage directory. */
	private static final String UNINSTALLED_APPS_DIRECTORY_NAME = "Uninstalled";
		
	private Set<App> installedApps;
	private Set<App> toBeUninstalledApps;
	private Set<App> uninstalledApps;
	
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
	}
	
	/**
	 * Attempts to install an app. Makes a copy of the app file and places it in the directory 
	 * used to hold all installed and uninstalled apps. Then, the app is created by instancing 
	 * its class that extends {@link AbstractCyApp}.
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
			// Uses Apache Commons library; overwrites files with the same name.
			FileUtils.copyFileToDirectory(app.getAppFile(), new File(getInstalledAppsPath()));
			
			// Update the app's path
			String fileName = app.getAppFile().getName();
			app.setAppFile(new File(getInstalledAppsPath() + File.separator + fileName));
			
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
		// TODO: At time of writing, CyApplicationConfiguration always returns the home directory for directory location.
		return applicationConfiguration.getConfigurationDirectoryLocation();
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
}
