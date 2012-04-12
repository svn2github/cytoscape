package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
import org.cytoscape.app.AbstractCyApp;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.internal.event.AppsChangedEvent;
import org.cytoscape.app.internal.event.AppsChangedListener;
import org.cytoscape.app.internal.exception.AppInstallException;
import org.cytoscape.app.internal.exception.AppInstanceException;
import org.cytoscape.app.internal.exception.AppMoveException;
import org.cytoscape.app.internal.exception.AppUninstallException;
import org.cytoscape.app.internal.manager.App.AppStatus;

public class SimpleApp extends App {

	@Override
	public Object createAppInstance(CyAppAdapter appAdapter) throws AppInstanceException {
		
		File appFile = this.getAppFile();
		URL appURL = null;
		try {
			appURL = appFile.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new AppInstanceException("Unable to obtain URL for file: " 
					+ appFile + ". Reason: " + e.getMessage());
		}
		
		// TODO: Currently uses the CyAppAdapter's loader to load apps' classes. Is there reason to use a different one?
		ClassLoader appClassLoader = new URLClassLoader(
				new URL[]{appURL}, appAdapter.getClass().getClassLoader());
		
		// Attempt to load the class
		Class<?> appEntryClass = null;
		try {
			 appEntryClass = appClassLoader.loadClass(this.getEntryClassName());
		} catch (ClassNotFoundException e) {
			
			throw new AppInstanceException("Class " + this.getEntryClassName() + " not found in URL: " + appURL);
		}
		
		// Attempt to obtain the constructor
		Constructor<?> constructor = null;
		try {
			constructor = appEntryClass.getConstructor(CyAppAdapter.class);
		} catch (SecurityException e) {
			throw new AppInstanceException("Access to the constructor for " + appEntryClass + " denied.");
		} catch (NoSuchMethodException e) {
			throw new AppInstanceException("Unable to find a constructor for " + appEntryClass + " that takes a CyAppAdapter as its argument.");
		}
		
		// Attempt to instantiate the app's class that extends AbstractCyActivator.
		Object appInstance = null;
		try {
			appInstance = constructor.newInstance(appAdapter);
		} catch (IllegalArgumentException e) {
			throw new AppInstanceException("Illegal arguments passed to the constructor for the app's entry class: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new AppInstanceException("Error instantiating the class " + appEntryClass + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new AppInstanceException("Access to constructor denied: " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new AppInstanceException(e.getMessage());
		}
		
		return appInstance;
	}

	@Override
	public void install(AppManager appManager) throws AppInstallException {
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

	@Override
	public void uninstall(AppManager appManager) throws AppUninstallException {
		
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
				
				// Simple apps require a Cytoscape restart to be uninstalled
				this.setStatus(AppStatus.TO_BE_UNINSTALLED);
				
			}
		} catch (IOException e) {
			throw new AppUninstallException("Unable to obtain path: " + e.getMessage());
		}
	}

}
