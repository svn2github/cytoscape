/*
 * Created on Sep 22, 2006
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.applet;

import java.applet.Applet;
import java.io.File;
import java.net.URL;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.core.PluginManager;

public class AppletApp extends TreeViewApp {
	private Applet applet;
	public AppletApp(Applet ap) {
		super();
		applet = ap;
	}

	public AppletApp(Applet ap, XmlConfig xmlConfig) {
		super(xmlConfig);
		applet = ap;
	}

	/* inherit description */
	public ViewFrame openNew() {
		// setup toplevel
		LinkedViewFrame tvFrame  =
				new AppletViewFrame(this, applet);
		tvFrame.addWindowListener(this);
		return tvFrame;
	}


	/* inherit description */
	public ViewFrame openNew(FileSet fileSet) throws LoadException {
		// setup toplevel
		LinkedViewFrame tvFrame  =
				new AppletViewFrame(this, applet);
		try {
			tvFrame.loadFileSet(fileSet);
			tvFrame.setLoaded(true);
		} catch (LoadException e) {
			tvFrame.dispose();
			throw e;
		}

		tvFrame.addWindowListener(this);
		return tvFrame;
	}

	/**
	* same as above, but doesn't open a loading window (damn deadlocks!)
	*/
	public ViewFrame openNewNW(FileSet fileSet) throws LoadException {
		// setup toplevel
		LinkedViewFrame tvFrame  = new AppletViewFrame(this, applet);
		if (fileSet != null) {
			try {
				tvFrame.loadFileSetNW(fileSet);
				tvFrame.setLoaded(true);
			} catch (LoadException e) {
				tvFrame.dispose();
				throw e;
			}
		}
		tvFrame.addWindowListener(this);
		return tvFrame;
	}

	public URL getCodeBase() {
		// TODO Auto-generated method stub
		return applet.getCodeBase();
	}
}
