package csplugins.layout;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cytoscape.layout.*;
import cytoscape.view.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import cytoscape.layout.LayoutManager;
import csplugins.layout.algorithms.hierarchicalLayout.HierarchicalLayoutAlgorithm;
import csplugins.layout.algorithms.graphPartition.AttributeCircleLayout;
import csplugins.layout.algorithms.graphPartition.DegreeSortedCircleLayout;
import csplugins.layout.algorithms.graphPartition.ISOMLayout;
import csplugins.layout.algorithms.bioLayout.BioLayoutKKAlgorithm;
import csplugins.layout.algorithms.bioLayout.BioLayoutFRAlgorithm;
import csplugins.layout.algorithms.GroupAttributesLayout;

import giny.view.NodeView;

public class LayoutPlugin extends CytoscapePlugin
{
  public LayoutPlugin()
  { 
    LayoutManager layoutManager = Cytoscape.getLayoutManager();

		// Add the Cytoscape layouts
		layoutManager.addLayout(new HierarchicalLayoutAlgorithm(), "Cytoscape Layouts");
		layoutManager.addLayout(new AttributeCircleLayout(), "Cytoscape Layouts");
		layoutManager.addLayout(new DegreeSortedCircleLayout(), "Cytoscape Layouts");
		layoutManager.addLayout(new ISOMLayout(), "Cytoscape Layouts");
		layoutManager.addLayout(new GroupAttributesLayout(), "Cytoscape Layouts");
		layoutManager.addLayout(new BioLayoutKKAlgorithm(false), "Cytoscape Layouts");
		layoutManager.addLayout(new BioLayoutKKAlgorithm(true), "Cytoscape Layouts");
		layoutManager.addLayout(new BioLayoutFRAlgorithm(true), "Cytoscape Layouts");

		// Add the JGraph layouts
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.ANNEALING), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.MOEN), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.CIRCLE_GRAPH), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.RADIAL_TREE), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.GEM), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.SPRING_EMBEDDED), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.SUGIYAMA), "JGraph Layouts");
		layoutManager.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.TREE), "JGraph Layouts");
  }
}
