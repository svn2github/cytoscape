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
package cytoscape.plugin.util;

import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.lang.StringBuffer;

import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author skillcoy
 *
 */
public class HttpUtils {
	/**
	 *
	 */
	public static boolean STOP = false;

	private static HttpURLConnection buildConnection(URL url) throws java.io.IOException {
		HttpURLConnection Connect = (HttpURLConnection) url.openConnection();
		Connect.setDoInput(true);
		Connect.setDoOutput(true);
		Connect.setUseCaches(false);
		Connect.setAllowUserInteraction(false);
		Connect.setRequestMethod("POST");

		return Connect;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param url DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 */
	public static InputStream getInputStream(String url) throws java.io.IOException {
		HttpURLConnection Connection = buildConnection(new URL(url));

		return Connection.getInputStream();
	}

	//	public static String download(String url) throws java.io.IOException
	/**
	 *  DOCUMENT ME!
	 *
	 * @param url DOCUMENT ME!
	 * @param fileName DOCUMENT ME!
	 * @param Dir DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 */
	public static File downloadFile(String url, String fileName, String Dir)
	    throws java.io.IOException {
		InputStream is = getInputStream(url);

		// er..not sure how to do this
		java.util.List<Byte> FileBytes = new java.util.ArrayList<Byte>();

		byte[] buffer = new byte[1];

		while (((is.read(buffer)) != -1) && !STOP) {
			FileBytes.add(buffer[0]);
		}

		System.out.println("total bytes: " + FileBytes.size());

		File Download = new File(Dir + fileName);
		FileOutputStream os = new FileOutputStream(Download);

		for (int i = 0; i < FileBytes.size(); i++) {
			if (!STOP)
				os.write(new byte[] { FileBytes.get(i) });
			else

				break;
		}

		os.flush();
		os.close();

		if (STOP)
			Download.delete();

		return Download;
	}
}
