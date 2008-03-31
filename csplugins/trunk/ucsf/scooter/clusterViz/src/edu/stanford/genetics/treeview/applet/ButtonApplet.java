/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ButtonApplet.java,v $
 * $Revision: 1.13 $
 * $Date: 2007/06/17 18:23:11 $
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
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.app.LinkedViewApp;
import edu.stanford.genetics.treeview.core.PluginManager;
import edu.stanford.genetics.treeview.reg.RegEngine;

public class ButtonApplet extends Applet {
	private TreeViewApp app;
	/**  start method for running as applet */
	public void start() {
		super.start();
		loadPlugins();
		final String cdtFile = getParameter("cdtFile");
		String cdtName = getParameter("cdtName");
		String styleName = getParameter("style");
		final int styleCode;
		if (styleName == null) {
			styleCode = FileSet.AUTO_STYLE;
		} else {
			styleCode = FileSet.getStyleByName(styleName);
		}
		if (cdtName == null) cdtName = cdtFile;
		if (cdtFile == null) {
			JOptionPane.showMessageDialog(this, "Must Provide cdtFile parameter in applet tag.");
			add(new JLabel("Must Provide cdtFile parameter in applet tag."));
		} else {
			app = new AppletApp(this, generateGlobalConfig());
			JButton openButton = new JButton("View " + cdtName);
			openButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					try {
						if (cdtFile == null) {
							app.openNew();
						} else {
							FileSet fileSet = new FileSet(makeWellFormedURL(cdtFile),"");
							fileSet.setStyle(styleCode);
							app.openNew(fileSet).setVisible(true);
						}
					} catch (LoadException ex) {
						JPanel temp = new JPanel();
						temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
						temp.add(new JLabel("Problems opening url " + cdtFile));
						temp.add(new JLabel("" + ex));
						JOptionPane.showMessageDialog(ButtonApplet.this, temp);
					}
				}
			});
			add(openButton);
			
		}
		repaint();
	}

	/**
	 * unfortunately, I cannot instantiate classloader within applet
	 * so instead we assume that the plugin jar is in the classpath
	 * and just ask the system classloader to instantiate.
	 */
	private void loadPlugins() {
		ClassLoader cl = getClass().getClassLoader();
		String plugins[] = getParameter("plugins").split(",");
		int loadStatus[] = new int[plugins.length];
		boolean showPopup = false;
		for (int i = 0; i < plugins.length;i++) {
			try {
				Class c = cl.loadClass(plugins[i]);
				PluginFactory pp = (PluginFactory) c.newInstance();
				loadStatus[i] = 0;
			} catch (ClassNotFoundException e) {
				loadStatus[i] = 1;
				showPopup = true;
				e.printStackTrace();
			} catch (InstantiationException e) {
				loadStatus[i] = 2;
				showPopup = true;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				loadStatus[i] = 3;
				showPopup = true;
				e.printStackTrace();
			}
		}
		if (showPopup == true) {
			String message = "";
			for (int j = 0; j < loadStatus.length; j++) {
				message += plugins[j];
				switch (loadStatus[j]) {
				case 0:
					message += " Loaded properly";
					break;
				case 1:
					message += " ClassNotFoundException";
					break;
				case 2:
					message += " InstantiationException";
					break;
				case 3:
					message += " IllegalAccessException";
					break;
				}
				message += "\n";
			}
			JOptionPane.showMessageDialog(this, message);
		}
	}

	/**
	* This subroutine generates a globalconfig to be used in applet mode.
	* <p>
	* It first tries to load getCodeBase() + "globalConfig.xml", and if that falls through for
	* any reason it defaults to a generic XmlConfig.
	*/
	private XmlConfig generateGlobalConfig() {
		XmlConfig globalConfig = null;
		String url          = getParameter("globalConfig");
		if (url != null) {
			try {
				globalConfig = new XmlConfig(new java.net.URL(url), "ProgramConfig");
				RegEngine.addBogusComplete(globalConfig.getNode("Registration"));
			} catch (java.net.MalformedURLException e) {
			} catch (java.security.AccessControlException sec) {
				urlAccessViolation(url + "\n" + sec);
			} catch (Exception ex) {
				urlAccessViolation(ex.toString());
			}

		}
		if (globalConfig == null) {
			globalConfig = new XmlConfig((java.net.URL) null, "ProgramConfig");
		}
		return globalConfig;
	}
	
	/**
	 *  Called when a url is improperly accessed. Pops up a window the hard way.
	 *
	 * @param  url  url which we do not have authorization for.
	 */
	public void urlAccessViolation(String url) {
		TextArea mp      = new TextArea("Bad URL\n" +
				"There was a security exception accessing the url\n" + url +
				"\nremember, applets can only load urls from the same server");
		final Frame top  = new Frame("Bad URL");
		top.addWindowListener(
			new WindowAdapter() {

				public void windowClosing(WindowEvent windowEvent) {
					top.dispose();
				}
			});

		top.add(mp);
		top.pack();
		top.show();
	}

	/**
	 *  Pops up a window with the html source of a url.
	 *
	 * @param  url  url to show.
	 */
	public void showText(java.net.URL url) {
		try {
			Reader st        = new InputStreamReader(url.openStream());
			int ch;
			TextArea mp      = new TextArea();
			ch = st.read();
			while (ch != -1) {
				char[] cbuf  = new char[1];
				cbuf[0] = (char) ch;
				mp.append(new String(cbuf));
				ch = st.read();
			}
			final Frame top  = new Frame("Show URL");
			top.addWindowListener(
				new WindowAdapter() {

					public void windowClosing(WindowEvent windowEvent) {
						top.dispose();
					}
				});

			top.add(mp);
			top.pack();
			top.show();

		} catch (java.io.IOException e) {
		}
	}

	/* (non-Javadoc)
	 * @see java.applet.Applet#getParameterInfo()
	 */
	public String[][] getParameterInfo() {
		
		 String pinfo[][] = {
				 {"plugins",    "urls",    "comma separated list of plugin urls"},
				 {"cdtFile", "url", "cdt file to display"},
				 {"cdtName",   "String",     "name to display on button"},
				 {"style", "String", "name of style with which to display this file"},
				 {"globalConfig", "url", "url of global config"}
			 };
		return pinfo;
	}
	
	
	/* (non-Javadoc)
	 * @see java.applet.Applet#getAppletInfo()
	 */
	public String getAppletInfo() {
		return "Java Treeview Applet (" +TreeViewApp.getVersionTag() +")";
	}

	private String makeWellFormedURL(String rawURL) {
		if (rawURL.toLowerCase().startsWith("http")) {
			return rawURL;
		} else if (rawURL.toLowerCase().startsWith("file")) {
			return rawURL;
		} else {
			return getCodeBase().toString() + rawURL;
		}
	}

}
