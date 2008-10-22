/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.io.read;

import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTask;

import org.cytoscape.io.CyFileFilter;

import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Central registry for all Cytoscape import classes.
 *
 * @author Cytoscape Development Team.
 */
public class CyReaderManager<R extends CyReader> {

	protected Set<R> readers;

	/**
	 * Constructor.
	 */
	public CyReaderManager() {
		readers = new HashSet<R>();	
		// get R services and add to list
	}


	/**
	 * Gets the GraphReader that is capable of reading the specified file.
	 *
	 * @param fileName File name or null if no reader is capable of reading the file.
	 * @return GraphReader capable of reading the specified file.
	 */
	public R getReader(String fileName) {
		for ( R read : readers ) {
			CyFileFilter cff = new CyFileFilter(read.getExtensions(),read.getContentTypes(),read.getExtensionDescription());
			if ( cff.accept( fileName ) )
				return read;
		}

		return null;
	}

	/**
	 * Gets the GraphReader that is capable of reading URL.
	 *
	 * @param url -- the URL string
	 * @return GraphReader capable of reading the specified URL.
	 */
	public R getReader(URL url) {
		// Open up the connection
		Proxy pProxyServer = null; //ProxyHandler.getProxyServer();
		URLConnection conn = null;

		try {
			if (pProxyServer == null)
				conn = url.openConnection();
			else
				conn = url.openConnection(pProxyServer);
		} catch (IOException ioe) {
			System.out.println("Unable to open "+url);
			return null;
		}
		// Ensure we are reading the real content from url,
		// and not some out-of-date cached content:
		conn.setUseCaches(false);

		// Get the content-type
		String contentType = conn.getContentType();
		if (contentType == null)
			contentType = "";

		// System.out.println("Content-type: "+contentType);

		int cend = contentType.indexOf(';');
		if (cend >= 0)
			contentType = contentType.substring(0, cend);

		for ( R read : readers ) {
			CyFileFilter cff = new CyFileFilter(read.getExtensions(),read.getContentTypes(),read.getExtensionDescription());
			if ( cff.accept(url, contentType) ) 
				return read;
		}

		// Couldn't get a reader based on URL so download it and 
		// try it as a file
		
		// If the content type is text/plain or text/html or text/xml
		// then write a temp file and handle things that way
		if (contentType.contains("text/html") ||
		    contentType.contains("text/plain") ||
		    contentType.contains("text/xml") ||
		    contentType.contains("application/rdf+xml")) {
			File tmpFile = null;

			try {
				tmpFile = downloadFromURL(url, null);
			} catch (Exception e) {
				System.out.println("Failed to download from URL: "+url);
			}

			if (tmpFile != null) {
				return getReader(tmpFile.getAbsolutePath());
			}
		}

		return null;
	}


	/**
	 * Gets a list of all registered filters plus a catch-all super set filter.
	 *
	 * @return List of CyFileFilter Objects.
	 */
	public CyFileFilter[] getFileFilters() {
		int size = readers.size();
		if ( size > 1 )
			size++;

		CyFileFilter[] ans = new CyFileFilter[size];

		int i = 0;
		for ( R read : readers ) {
			ans[i++] = new CyFileFilter( read.getExtensions(), read.getContentTypes(), read.getExtensionDescription() );
		}

		if (readers.size() > 1) {
			String[] allTypes = concatAllExtensions(readers);
			ans[i] = new CyFileFilter(allTypes, new String[]{}, "All Types");
		}

		return ans;
	}



	/**
	 * Creates a String array of all extensions
	 */
	private String[] concatAllExtensions(Set<R> cffs) {
		Set<String> stringAns = new HashSet<String>();

		for ( R cff : cffs ) 
			for ( String s : cff.getExtensions() )
				stringAns.add( s );

		String[] ret = new String[stringAns.size()];
		int i = 0;
		for ( String s : stringAns )
			ret[i++] = s;
			
		
		return ret; 
	}

	private String extractExtensionFromContentType(String ct) {
		Pattern p = Pattern.compile("^\\w+/([\\w|-]+);*.*");
		Matcher m = p.matcher(ct);

		if (m.matches())
			return m.group(1);
		else

			return "txt";
	}

	// Create a temp file for URL download
	private File createTempFile(URLConnection conn, URL url) throws IOException {
		File tmpFile = null;
		String tmpDir = System.getProperty("java.io.tmpdir");
		String pURLstr = url.toString();

		// Try if we can determine the network type from URLstr
		// test the URL against the various file extensions,
		// if one matches, then extract the basename
		String[] theExts = concatAllExtensions(readers);

		for ( String theExt : theExts ) {
			if (pURLstr.endsWith(theExt)) {
				tmpFile = new File(tmpDir + System.getProperty("file.separator")
				                   + pURLstr.substring(pURLstr.lastIndexOf("/") + 1));

				break;
			}
		}

		// if none of the extensions match, then use the the content type as
		// a suffix and create a temp file.
		if (tmpFile == null) {
			String ct = "." + extractExtensionFromContentType(conn.getContentType());
			tmpFile = File.createTempFile("url.download.", ct, new File(tmpDir));
		}

		if (tmpFile == null)
			return null;
		else
			tmpFile.deleteOnExit();

		return tmpFile;
	}

	/**
	 * Download a temporary file from the given URL. The file will be saved in the
	 * temporary directory and will be deleted after Cytoscape exits.
	 * @param u -- the URL string, TaskMonitor if any
	 * @return -- a temporary file downloaded from the given URL.
	 */
	private File downloadFromURL(URL url, TaskMonitor taskMonitor)
	    throws IOException, FileNotFoundException {
		Proxy pProxyServer = null; // ProxyHandler.getProxyServer();
		URLConnection conn = null;

		if (pProxyServer == null)
			conn = url.openConnection();
		else
			conn = url.openConnection(pProxyServer);
		// Ensure we are reading the real content from url,
		// and not some out-of-date cached content:
		conn.setUseCaches(false);
		// create the temp file
		File tmpFile = createTempFile(conn, url);

		// This is needed for the progress monitor 
		int maxCount = conn.getContentLength(); // -1 if unknown
		int progressCount = 0;

		// now write the temp file
		BufferedWriter out = null;
		BufferedReader in = null;

		out = new BufferedWriter(new FileWriter(tmpFile));
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String inputLine = null;
		double percent = 0.0d;

		while ((inputLine = in.readLine()) != null) {
			progressCount += inputLine.length();

			//  Report on Progress
			if (taskMonitor != null) {
				percent = ((double) progressCount / maxCount) * 100.0;

				if (maxCount == -1) { // file size unknown
					percent = -1;
				}

				JTask jTask = (JTask) taskMonitor;

				if (jTask.haltRequested()) { //abort
					tmpFile = null;
					taskMonitor.setStatus("Canceling the download task ...");
					taskMonitor.setPercentCompleted(100);

					break;
				}

				taskMonitor.setPercentCompleted((int) percent);
			}

			out.write(inputLine);
			out.newLine();
		}

		in.close();
		out.close();

		return tmpFile;
	} 
}
