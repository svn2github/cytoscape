
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

package csplugins.layout;

import csplugins.layout.algorithms.GroupAttributesLayout;
import csplugins.layout.algorithms.bioLayout.BioLayoutFRAlgorithm;
import csplugins.layout.algorithms.bioLayout.BioLayoutKKAlgorithm;
import csplugins.layout.algorithms.graphPartition.AttributeCircleLayout;
import csplugins.layout.algorithms.graphPartition.DegreeSortedCircleLayout;
import csplugins.layout.algorithms.graphPartition.ISOMLayout;
import csplugins.layout.algorithms.hierarchicalLayout.HierarchicalLayoutAlgorithm;
import csplugins.layout.algorithms.circularLayout.CircularLayoutAlgorithm;
import csplugins.layout.algorithms.force.ForceDirectedLayout;

import cytoscape.*;

import cytoscape.layout.*;
import cytoscape.layout.CyLayouts;

import cytoscape.plugin.*;

import cytoscape.util.*;

import cytoscape.view.*;

import giny.view.NodeView;

import java.awt.event.*;

import java.util.*;

import javax.swing.*;


/**
 *
 */
public class LayoutPlugin extends CytoscapePlugin {
	/**
	 * Creates a new LayoutPlugin object.
	 */
	public LayoutPlugin() {
		// Add the Cytoscape layouts
		CyLayouts.addLayout(new ForceDirectedLayout(), "Cytoscape Layouts");
		CyLayouts.addLayout(new CircularLayoutAlgorithm(), "Cytoscape Layouts");
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
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.CIRCLE_GRAPH),
		                    "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.RADIAL_TREE),
		                    "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.GEM), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.SPRING_EMBEDDED),
		                    "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.SUGIYAMA), "JGraph Layouts");
		CyLayouts.addLayout(new JGraphLayoutWrapper(JGraphLayoutWrapper.TREE), "JGraph Layouts");
	}

	public PluginInfo getPluginInfoObject() {
		PluginInfo info = new PluginInfo();
		info.setName("Automatic Layouts Plugin");
		info.setDescription("This plugin includes the core layouts provided by Cytoscape.  It populates the 'Layout->Cytoscape Layouts' and 'Layout->JGraph Layouts' menues");
		info.setCategory("Core");
		info.setPluginVersion(1.0);
		info.setProjectUrl("http://cytoscape.org/plugins_page/plugin_document.xml");
		info.addAuthor("Scooter Morris", "UCSF");
		return info;
	}
}
