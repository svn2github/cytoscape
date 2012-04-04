package org.cytoscape.app.internal.manager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.cytoscape.app.internal.exception.AppParsingException;

/**
 * This class represents an app parser that is capable of parsing given {@link File}
 * objects to {@link App} objects, as well as reporting problems found while attempting
 * to verify the app.
 */
public class AppParser {
	/** The name of the key in the app jar's manifest file that indicates the fully-qualified name 
	 * of the class to instantiate upon app installation. */
	private static final String APP_CLASS_TAG = "Cytoscape-App";
	
	public App parseApp(File file) throws AppParsingException {
		App parsedApp = new App();
		
		if (!file.isFile()) {
			throw new AppParsingException("The given file, " + file + ", is not a file.");
		}
		
		// Attempt to parse the file as a jar file
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
		} catch (IOException e) {
			throw new AppParsingException("Error parsing given file as a jar file: " + e.getMessage());
		}
		
		// Obtain the URL of the jar file
		URL jarURL = null;
		try {
			jarURL = file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new AppParsingException("Failed to obtain jar URL: " + e.getMessage());
		}
		
		// Attempt to obtain manifest file from jar
		Manifest manifest = null;
		try {
			manifest = jarFile.getManifest();
		} catch (IOException e) {
			throw new AppParsingException("Error obtaining manifest from app jar: " + e.getMessage());
		}
		
		// Obtain the fully-qualified name of the class to instantiate upon app installation
		String entryClassName = manifest.getMainAttributes().getValue(APP_CLASS_TAG);
		if (entryClassName == null || entryClassName.trim().length() == 0) {
			throw new AppParsingException("Jar is missing value for entry " + APP_CLASS_TAG + " in its manifest file.");
		}
		
		// Attempt to guess the app's name
		parsedApp.setAppName(file.getName()); // Use filename for now
		
		parsedApp.setAppFile(file);
		parsedApp.setEntryClassName(entryClassName);
		parsedApp.setJarURL(jarURL);
		parsedApp.setAppValidated(true);
		
		return parsedApp;
	}
}
