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

import java.net.URL;

import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;


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

		if (source.toString().endsWith(GZIP)) {
			newIs = new GZIPInputStream(source.openStream());
		} else if (source.toString().endsWith(ZIP)) {
			System.err.println(source.toString() + " ZIP ");
			newIs = new ZipInputStream(source.openStream());
		} else if (source.toString().endsWith(JAR)) {
			newIs = new JarInputStream(source.openStream());
		} else {
			newIs = source.openStream();
		}

		return newIs;
	}
	
	
	/**
	 * Download the file specified by the url string to the given File object
	 * @param urlString
	 * @param downloadFile
	 * @return
	 * @throws IOException
	 */
	public static void download(String urlString, File downloadFile) throws IOException {
		URL url = new URL(urlString);
		// using getInputStream method above never returned an input stream that I could read from why??
		InputStream is = url.openStream();
		
		java.util.List<Byte> FileBytes = new java.util.ArrayList<Byte>();

		byte[] buffer = new byte[1];
		while (((is.read(buffer)) != -1) && !STOP) {
			FileBytes.add(buffer[0]);
		}

		System.out.println("total bytes: " + FileBytes.size());
		FileOutputStream os = new FileOutputStream(downloadFile);

		for (int i = 0; i < FileBytes.size(); i++) {
			if (!STOP) {
				os.write(new byte[] { FileBytes.get(i) });
			} else {
				break;
			}
		}

		os.flush();
		os.close();

		if (STOP) {
			downloadFile.delete();
		}

//		return downloadFile;
	}
	
	
}
