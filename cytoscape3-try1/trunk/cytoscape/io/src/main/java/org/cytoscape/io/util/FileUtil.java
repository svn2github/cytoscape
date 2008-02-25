/**
 * 
 */
package org.cytoscape.io.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import cytoscape.task.TaskMonitor;

/**
 * @author skillcoy
 *
 */
public class FileUtil {
	/**
	 * A string that defines a simplified java regular expression for a URL.
	 * This may need to be updated to be more precise.
	 */
	public static final String urlPattern = "^(jar\\:)?(\\w+\\:\\/+\\S+)(\\!\\/\\S*)?$";

	/**
	 *
	 * @param filename 
	 *		File to read in
	 *
	 * @return  The contents of the given file as a string.
	 */
	public static String getInputString(String filename) {
		try {
			InputStream stream = getInputStream(filename);
			return getInputString(stream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		System.out.println("couldn't create string from '" + filename + "'");

		return null;
	}

	/**
	 *
	 * @param inputStream 
	 *		An InputStream
	 *
	 * @return  The contents of the given file as a string.
	 */
	public static String getInputString(InputStream inputStream) throws IOException {
		String lineSep = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

		while ((line = br.readLine()) != null)
			sb.append(line + lineSep);

		return sb.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static InputStream getInputStream(String name) {
		return getInputStream(name, null);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 * @param monitor DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static InputStream getInputStream(String name, TaskMonitor monitor) {
		InputStream in = null;

		try {
			if (name.matches(urlPattern)) {
				URL u = new URL(name);
				// in = u.openStream();
                // Use URLUtil to get the InputStream since we might be using a proxy server 
				// and because pages may be cached:
				in = URLUtil.getBasicInputStream(u);
			} else
				in = new FileInputStream(name);
		} catch (IOException ioe) {
			ioe.printStackTrace();

			if (monitor != null)
				monitor.setException(ioe, ioe.getMessage());
		}

		return in;
	}

	
}
