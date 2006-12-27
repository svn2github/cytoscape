package csplugins.layout;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cytoscape.layout.*;
import cytoscape.view.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import cytoscape.layout.CyLayouts;
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

		// Add the Cytoscape layouts
		CyLayouts.addLayout(new HierarchicalLayoutAlgorithm(), "Cytoscape Layouts");
		CyLayouts.addLayout(new AttributeCircleLayout(), "Cytoscape Layouts");
		CyLayouts.addLayout(new DegreeSortedCircleLayout(), "Cytoscape Layouts");
		CyLayouts.addLayout(new ISOMLayout(), "Cytoscape Layouts");
		CyLayouts.addLayout(new GroupAttributesLayout(), "Cytoscape Layouts");
		CyLayouts.addLayout(new BioLayoutKKAlgorithm(false), "Cytoscape Layouts");
		CyLayouts.addLayout(new BioLayoutKKAlgorithm(true), "Cytoscape Layouts");
		CyLayouts.addLayout(new BioLayoutFRAlgorithm(true), "Cytoscape Layouts");

		// Add the JGraph layouts
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.ANNEALING), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.MOEN), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.CIRCLE_GRAPH), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.RADIAL_TREE), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.GEM), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.SPRING_EMBEDDED), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.SUGIYAMA), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.TREE), "JGraph Layouts");
  }
}
