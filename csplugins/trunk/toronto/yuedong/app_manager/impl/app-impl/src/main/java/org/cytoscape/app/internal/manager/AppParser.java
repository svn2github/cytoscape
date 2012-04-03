package org.cytoscape.app.internal.manager;

import java.io.File;

import org.cytoscape.app.internal.exception.AppParsingException;

/**
 * This class represents an app parser that is capable of parsing given {@link File}
 * objects to {@link App} objects, as well as reporting problems found while attempting
 * to verify the app.
 */
public class AppParser {
	public App parseApp(File file) throws AppParsingException {
		if (!file.isFile()) {
			throw new AppParsingException("Given file is not a file.");
		}
		
		
		App parsedApp = new App(null, null, null, null, file, null);
		
		return parsedApp;
	}
}
