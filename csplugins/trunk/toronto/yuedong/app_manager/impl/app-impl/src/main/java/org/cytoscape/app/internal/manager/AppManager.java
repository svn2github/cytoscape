package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.exception.AppCopyException;
import org.cytoscape.application.CyApplicationConfiguration;

/**
 * This class represents an AppManager, which is capable of maintaining a list of all currently installed and available apps. The class
 * also provides functionalities for installing and uninstalling apps.
 */
public class AppManager {
	private static final String APP_CLASS_TAG = "Cytoscape-App";
	
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
	 * Installs an app; first makes a copy of the app file and places it in the directory used to hold all installed and uninstalled apps.
	 * Then, the app is created by instancing its {@link AbstractCyApp} class that implements.
	 * @param app The {@link App} object representing and providing information about the app to install
	 * @throws AppCopyException If there was an IO-related error during the copy operation that prevents the app from 
	 * being successfully installed.
	 */
	public void installApp(App app) throws AppCopyException {
		
		File appFile = app.getAppFile();
		
		// Copy app to local storage directory using utilities provided by the Apache Commons library.
		try {
			// Overwrites files with the same name.
			FileUtils.copyFileToDirectory(appFile, getAppPath());
		} catch (IOException e) {
			
			throw new AppCopyException();
		}
		
		// TODO: Currently uses the CyAppAdapter's loader to load apps' classes. Is there reason to use a different one?
		ClassLoader appClassLoader = appAdapter.getClass().getClassLoader();
		
		Class<?> appEntryClass = null;
		try {
			 appEntryClass = appClassLoader.loadClass(app.getEntryClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Constructor<?> constructor = null;
		try {
			constructor = appEntryClass.getConstructor(CyAppAdapter.class);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Object object = constructor.newInstance(appAdapter);
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		installedApps.add(app);
	}
	
	public Set<App> getInstalledApps() {
		return installedApps;
	}
	
	/**
	 * Return the path of the directory used to contain all apps.
	 * @return The path of the root directory containing all installed and uninstalled apps.
	 */
	private File getAppPath() {
		// TODO: At time of writing, CyApplicationConfiguration always returns the home directory for directory location.
		return applicationConfiguration.getConfigurationDirectoryLocation();
	}
}
