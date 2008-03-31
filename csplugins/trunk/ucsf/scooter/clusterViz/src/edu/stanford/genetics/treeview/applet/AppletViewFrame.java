/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: AppletViewFrame.java,v $
 * $Revision: 1.10 $
 * $Date: 2006/10/03 06:19:12 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular,
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER
 */
package edu.stanford.genetics.treeview.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observer;

import javax.swing.JFrame;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.core.MenuHelpPluginsFrame;

/**
 *  A subclass of ViewFrame designed to run in a browser. Very similar to TreeViewFrame.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version    @version $Revision: 1.10 $ $Date: 2006/10/03 06:19:12 $
 */
public class AppletViewFrame extends LinkedViewFrame implements Observer {
	/**  Description of the Field */
	TreeViewApp treeView;

	private static String appName = "Java TreeView (Applet)";
	public String getAppName() {
		return appName;
	}
	/**
	 *  Sets up widgets.
	 *
	 * @param  treeview application which spawned this window.
	 */
	public AppletViewFrame(TreeViewApp treeview, Applet applet) 
		{
		super(treeview, appName);
		browserControl= new AppletBrowserControl(applet);
		
	}
	protected void displayPluginInfo() {
		MenuHelpPluginsFrame frame = new MenuHelpPluginsFrame(
				"Current Plugins", this);
		try {
			URL f_currdir;
			f_currdir = new URL(".");
			frame.setSourceText(f_currdir.getPath() + "/plugins");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			frame.setSourceText("Unable to read default plugins directory.");
		}
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	/**
	 * subclass for applets
	 */
	class AppletBrowserControl extends BrowserControl {
		private AppletContext appletContext;
		
		public AppletBrowserControl(Applet applet) {
			appletContext = applet.getAppletContext();
		}

		public void displayURL(String url) throws IOException {
			appletContext.showDocument(new URL(url), "_blank");
		}
		
	}
}

