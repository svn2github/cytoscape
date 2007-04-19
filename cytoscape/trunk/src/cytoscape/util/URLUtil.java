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
package cytoscape.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import java.net.Proxy;
import java.net.URL;

import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTask;


/**
 *
 */
public class URLUtil {
	private static final String GZIP = ".gz";
	private static final String ZIP = ".zip";
	private static final String JAR = ".jar";
	
	public static boolean STOP = false;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public static InputStream getInputStream(URL source) throws IOException {
		final InputStream newIs;
		final InputStream proxyIs;
		Proxy CytoProxy = ProxyHandler.getProxyServer();
		
		if (CytoProxy == null) {
			proxyIs = source.openStream();
		} else {
			proxyIs = source.openConnection(CytoProxy).getInputStream();
		}
		
		if (source.toString().endsWith(GZIP)) {
			newIs = new GZIPInputStream(proxyIs);
		} else if (source.toString().endsWith(ZIP)) {
			System.err.println(source.toString() + " ZIP ");
			newIs = new ZipInputStream(proxyIs);
		} else if (source.toString().endsWith(JAR)) {
			newIs = new JarInputStream(proxyIs);
		} else {
			newIs = proxyIs;
		}

		return newIs;
	}
	
	
	/**
	 * Download the file specified by the url string to the given File object
	 * @param urlString
	 * @param downloadFile
	 * @param taskMonitor
	 * @return
	 * @throws IOException
	 */
	public static void download(String urlString, File downloadFile, TaskMonitor taskMonitor) throws IOException {
		URL url = new URL(urlString);

		InputStream is = null;
		Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
		
		int maxCount = 0; // -1 if unknown
		int progressCount = 0;
		
		if (CytoProxyHandler == null) {
			java.net.URLConnection conn = url.openConnection();
			maxCount = conn.getContentLength();
			is = conn.getInputStream();
		} else {
			java.net.URLConnection conn = url.openConnection(CytoProxyHandler);
			maxCount = conn.getContentLength();
			is = conn.getInputStream();
		}
		
		FileOutputStream os = new FileOutputStream(downloadFile);

		double percent = 0.0d;
		byte[] buffer = new byte[1];
		while (((is.read(buffer)) != -1) && !STOP) {
			progressCount += buffer.length;

			//  Report on Progress
			if (taskMonitor != null) {
				percent = ((double) progressCount / maxCount) * 100.0;

				if (maxCount == -1) { // file size unknown
					percent = -1;
				}

				JTask jTask = (JTask) taskMonitor;

				if (jTask.haltRequested()) { //abort
					downloadFile = null;
					taskMonitor.setStatus("Canceling the download task ...");
					taskMonitor.setPercentCompleted(100);
					break;
				}

				taskMonitor.setPercentCompleted((int) percent);
			}
			os.write(buffer);
		}


		os.flush();
		os.close();
		is.close(); 
		
		if (STOP) {
			downloadFile.delete();
		}
	}

	
	/**
	 * Get the the contents of the given URL as a string.
	 * @param source
	 * @return String
	 * @throws IOException
	 */
	public static String download(URL source) throws IOException {
		InputStream is = getInputStream(source);
//		Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
//		
//		if (CytoProxyHandler == null) {
//			is = getInputStream(source);
//		} else {
//			source.openConnection(CytoProxyHandler);
//		}
		
		StringBuffer buffer = new StringBuffer();
		int c;
		while( (c = is.read() ) != -1) {
			buffer.append( (char)c );
		}
		is.close();
		return buffer.toString();
	}
	
	
	
}
