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

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

// Giny imports
import giny.model.Node;
import giny.view.GraphViewChangeListener;
import giny.view.GraphViewChangeEvent;

// clusterViz imports
import clusterViz.ui.ClusterVizView;
import clusterViz.model.ClusterVizModel;

// TreeView imports
import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.app.LinkedViewApp;
// import edu.stanford.genetics.treeview.core.PluginManager;
import edu.stanford.genetics.treeview.core.MenuHelpPluginsFrame;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterVizView extends TreeViewApp implements Observer, GraphViewChangeListener {
	private URL codeBase = null;
	private ViewFrame viewFrame = null;
	private TreeSelectionI geneSelection = null;
	private TreeSelectionI arraySelection = null;
	private ClusterVizModel dataModel = null;
	private CyNetworkView myView = null;
	private CyNetwork myNetwork = null;
	private	List<CyNode>selectedNodes;
	private	List<CyNode>selectedArrays;
	private CyLogger myLogger;

	private static String appName = "ClusterViz";

	public ClusterVizView(CyLogger logger) {
		super();
		setExitOnWindowsClosed(false);
		selectedNodes = new ArrayList();
		selectedArrays = new ArrayList();
		myLogger = logger;
	}

	public ClusterVizView(XmlConfig xmlConfig, CyLogger logger) {
		super(xmlConfig);
		selectedNodes = new ArrayList();
		selectedArrays = new ArrayList();
		// setExitOnWindowsClosed(false);
		myLogger = logger;
	}

	public void setVisible(boolean visibility) {
		if (viewFrame != null)
			viewFrame.setVisible(visibility);
	}

	public String getAppName() {
		return appName;
	}

	public void startup(CyLogger logger) {
		// Get our data model
		dataModel = new ClusterVizModel(myLogger);

		// Get our logger
		this.myLogger = logger;

		// Set up our configuration
		XmlConfig documentConfig = new XmlConfig("<DocumentConfig><Views><View type=\"Dendrogram\" dock=\"1\"><ColorExtractor contrast=\"3.0\"><ColorSet up=\"#FFFF00\" down=\"#0000FF\"/></ColorExtractor></View></Views></DocumentConfig>","DocumentConfig");
		dataModel.setDocumentConfig(documentConfig);

		// Create our view frame
		TreeViewFrame frame = new TreeViewFrame(this);

		// Set the data model
		frame.setDataModel(dataModel);
		frame.setLoaded(true);
		frame.addWindowListener(this);
		frame.setVisible(true);
		geneSelection = frame.getGeneSelection();
		geneSelection.addObserver(this);
		arraySelection = frame.getArraySelection();
		arraySelection.addObserver(this);

		// Now set up to receive selection events
		myView = Cytoscape.getCurrentNetworkView();
		myNetwork = Cytoscape.getCurrentNetwork();
		// myView.addGraphViewChangeListener(this);
	}


	private void setCodeBase(URL url) {
		codeBase = url;
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

	public void update(Observable o, Object arg) {
		if (o == geneSelection) {
			selectedNodes = new ArrayList();
			int[] selections = geneSelection.getSelectedIndexes();
			HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
			String [] names = geneInfo.getNames();
			for (int i = 0; i < selections.length; i++) {
				String nodeName = geneInfo.getHeader(selections[i])[0];
				CyNode node = Cytoscape.getCyNode(nodeName, false);
				if (node != null) selectedNodes.add(node);
			}
			// myView.removeGraphViewChangeListener(this);
			// System.out.println("Selecting "+nodes.size()+" nodes");
			if (!dataModel.isSymmetrical() || selectedArrays.size() == 0) {
				myNetwork.unselectAllNodes();
				myNetwork.setSelectedNodeState(selectedNodes, true);
				return;
			}
			// myView.addGraphViewChangeListener(this);
		} else if (o == arraySelection) {
			// We only care about array selection for symmetrical models
			if (!dataModel.isSymmetrical())
				return;
			selectedArrays = new ArrayList();
			int[] selections = arraySelection.getSelectedIndexes();
			if (selections.length == dataModel.nExpr())
				return;
			HeaderInfo arrayInfo = dataModel.getArrayHeaderInfo();
			String [] names = arrayInfo.getNames();
			for (int i = 0; i < selections.length; i++) {
				String nodeName = arrayInfo.getHeader(selections[i])[0];
				CyNode node = Cytoscape.getCyNode(nodeName, false);
				if (node != null) selectedArrays.add(node);
			}
		}

		// If we've gotten here, we want to select edges
		myNetwork.unselectAllEdges();
		myNetwork.unselectAllNodes();
		List<CyEdge>edgesToSelect = new ArrayList();
		for (CyNode node1: selectedNodes) {
			int [] nodes = new int[2];
			nodes[0] = node1.getRootGraphIndex();
			for (CyNode node2: selectedArrays) {
				nodes[1] = node2.getRootGraphIndex();
				int edges[] = myNetwork.getConnectingEdgeIndicesArray(nodes);
				for (int i = 0; i < edges.length; i++)
					edgesToSelect.add((CyEdge)myNetwork.getEdge(edges[i]));
			}
		}
		myNetwork.setSelectedEdgeState(edgesToSelect, true);
	}

	public void graphViewChanged(GraphViewChangeEvent event) {
		// System.out.println("graphViewChanged");
		if (event.isNodesSelectedType()) {
			Node[] nodeArray = event.getSelectedNodes();
			// setSelection(nodeArray, true);
		} else if (event.isNodesUnselectedType()) {
			Node[] nodeArray = event.getUnselectedNodes();
			// setSelection(nodeArray, false);
		}
	}

	private void setSelection(Node[] nodeArray, boolean select) {
		HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
		geneSelection.deleteObserver(this);
		geneSelection.setSelectedNode(null);
		for (int index = 0; index < nodeArray.length; index++) {
			CyNode cyNode = (CyNode) nodeArray[index];
			// System.out.println("setting "+cyNode.getIdentifier()+" to "+select);
			int geneIndex = geneInfo.getIndex(cyNode.getIdentifier());
			geneSelection.setIndex(geneIndex, select);
		}
		geneSelection.notifyObservers();
		geneSelection.addObserver(this);
		arraySelection.setSelectedNode(null);
		arraySelection.selectAllIndexes();
		arraySelection.notifyObservers();
	}
}
