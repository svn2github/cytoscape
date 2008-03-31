/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package clusterViz.ui;

// System imports
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

// clusterViz imports
import clusterViz.ui.ClusterVizView;

// TreeView imports
import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.core.PluginManager;
import edu.stanford.genetics.treeview.core.MenuHelpPluginsFrame;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterVizView extends TreeViewApp {
	private URL codeBase = null;
	private ViewFrame viewFrame = null;

	public ClusterVizView() {
		super();
		scanForPlugins();
		setExitOnWindowsClosed(false);
	}

	public ClusterVizView(XmlConfig xmlConfig) {
		super(xmlConfig);
		scanForPlugins();
		// setExitOnWindowsClosed(false);
	}

	public void setVisible(boolean visibility) {
		if (viewFrame != null)
			viewFrame.setVisible(visibility);
	}

	public void startup() {
		// XXX HACK XXX
		String sFilePath = "/tmp/input.cdt";
		File file = new File(sFilePath);
		FileSet fileSet = new FileSet(file.getName(), file.getParent()+File.separator);
		try {
			viewFrame = openNewNW(fileSet);
		} catch (LoadException e) {
			System.err.println(e.getMessage());
		}
	}


	private void setCodeBase(URL url) {
		codeBase = url;
	}

	private void scanForPlugins() {
    URL fileURL = getCodeBase();
    String dir = Util.URLtoFilePath(fileURL.getPath()+"/treeView/");
		System.out.println("plugin path: "+dir);
    File[] files = PluginManager.getPluginManager().readdir(dir);
    if (files == null) {
      LogBuffer.println("Directory "+dir+" returned null");
      File f_currdir = new File(".");
      try {
        dir = f_currdir.getCanonicalPath() + File.separator +"treeView" + File.separator;
        LogBuffer.println("failing over to "+dir);
        files = PluginManager.getPluginManager().readdir(dir);
        if (files != null) {
          setCodeBase(f_currdir.toURL());
        }
      } catch (IOException e1) {
        // this might happen when the dir is bad.
        e1.printStackTrace();
      }
    }
    if (files == null || files.length == 0) {
    	LogBuffer.println("Directory "+dir+" contains no plugins");
    } else {
      PluginManager.getPluginManager().loadPlugins(files, false);
    }
    PluginManager.getPluginManager().pluginAssignConfigNodes(getGlobalConfig().getNode("Plugins"));
  }

	public ViewFrame openNew() {
		LinkedViewFrame tvFrame = new LinkedViewFrame((TreeViewApp)this, "clusterViz");
		tvFrame.addWindowListener(this);
		return tvFrame;
	}

	public ViewFrame openNew (FileSet fileSet) throws LoadException {
    LinkedViewFrame tvFrame  = new LinkedViewFrame((TreeViewApp)this, "clusterViz");
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

  public ViewFrame openNewNW(FileSet fileSet) throws LoadException {
    LinkedViewFrame tvFrame  = new LinkedViewFrame((TreeViewApp)this, "clusterViz");
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
    if (codeBase != null) {
      return codeBase;
    }
    try {
      URL location;
      String classLocation = ClusterVizView.class.getName().replace('.', '/') + ".class";
      ClassLoader loader = ClusterVizView.class.getClassLoader();
      if (loader == null) {
        location = ClassLoader.getSystemResource(classLocation);
      } else {
        location = loader.getResource(classLocation);
      }
      String token = null;
      if (location != null && "jar".equals(location.getProtocol())) {
        String urlString = location.toString();
        if (urlString != null) {
          final int lastBangIndex = urlString.lastIndexOf("!");
          if (lastBangIndex >= 0) {
            urlString = urlString.substring("jar:".length(), lastBangIndex);
            if (urlString != null) {
              int lastSlashIndex = urlString.lastIndexOf("/");
              if (lastSlashIndex >= 0) {
                token = urlString.substring(0, lastSlashIndex);
              }
            }
          }
        }
      }
      if (token == null) {
        return (new File(".")).toURL();
      } else {
        return new URL(token);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, e);
      return null;
    }
  }
}
