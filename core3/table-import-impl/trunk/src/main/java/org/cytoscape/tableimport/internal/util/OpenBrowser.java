/*
  File: OpenBrowser.java

  Copyright (c) 2006-2010, The Cytoscape Consortium (www.cytoscape.org)

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

//-------------------------------------------------------------------------
// $Revision: 19546 $
// $Date: 2010-03-09 13:25:15 -0800 (Tue, 09 Mar 2010) $
// $Author: mes $
//-------------------------------------------------------------------------
package org.cytoscape.tableimport.internal.util;

//import cytoscape.CytoscapeInit;
//import cytoscape.logger.CyLogger;

import java.io.IOException;
import java.util.Properties;

import org.cytoscape.tableimport.internal.reader.ExcelAttributeSheetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class OpenBrowser {
	static String[] LINUX_BROWSERS =
	        { "xdg-open", "htmlview", "firefox", "mozilla", "konqueror", "chrome", "chromium" };

	static String MAC_PATH = "open";

	private static final String WIN_PATH = "rundll32";
	private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
	private static final Logger logger = LoggerFactory.getLogger(OpenBrowser.class);
	/**
	 *  DOCUMENT ME!
	 *
	 * @param url DOCUMENT ME!
	 */
	public static void openURL(final String url) {
		final Properties prop = null;//CytoscapeInit.getProperties();
		String defBrowser = prop.getProperty("defaultWebBrowser");
		if (defBrowser != null && defBrowser.equals(""))
			defBrowser = null;
		final String osName = System.getProperty("os.name");

		boolean succeeded;
		if (osName.startsWith("Windows"))
			succeeded = openURLOnWindows(url, defBrowser);
		else if (osName.startsWith("Mac"))
			succeeded = openURLOnMac(url, defBrowser);
		else // Assume Linux
			succeeded = openURLOnLinux(url, defBrowser);

		if (!succeeded)
			logger.error("failed to launch browser!");
	}

	private static boolean openURLOnWindows(final String url, final String defBrowser) {
		final String cmd = (defBrowser != null) ? defBrowser + " " + url
		                                        : WIN_PATH + " " + WIN_FLAG + " " + url;
		logger.info("Opening URL by command \"" + cmd + "\"");
		return tryExecute(cmd) == 0;
	}

	private static boolean openURLOnMac(final String url, final String defBrowser) {
		final String cmd = (defBrowser != null) ? defBrowser + " " + url
		                                        : MAC_PATH + " " + " " + url;
		logger.info("Opening URL by command \"" + cmd + "\"");
		return tryExecute(cmd) == 0;
	}

	private static boolean openURLOnLinux(final String url, final String defBrowser)
	{
		String cmd;
		if (defBrowser != null) {
			cmd = defBrowser + " " + url;
			logger.info("Opening URL by command \"" + cmd + "\"");
			if (tryExecute(cmd) == 0)
				return true;
		}

		for (final String browser : LINUX_BROWSERS) {
			cmd = browser + " " + url;
			logger.info("Opening URL by command \"" + cmd + "\"");
			if (tryExecute(cmd) == 0)
				return true;
		}

		return false;
	}
	
	/**
	 * @return the command's exit code
	 */
	private static int tryExecute(final String cmd) {
		try {
			final Process p = Runtime.getRuntime().exec(cmd);
			return p.waitFor();
		} catch (final InterruptedException e) {
			logger.error("failed to execute browser command: '" + cmd + "'", e );
			return -1;
		} catch (final IOException e) {
			logger.error("failed to execute browser command: '" + cmd + "'", e );
			return -1;
		}
	}
}
