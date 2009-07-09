
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
import csplugins.layout.algorithms.circularLayout.CircularLayoutAlgorithm;
import csplugins.layout.algorithms.force.ForceDirectedLayout;
import csplugins.layout.algorithms.graphPartition.AttributeCircleLayout;
import csplugins.layout.algorithms.graphPartition.DegreeSortedCircleLayout;
import csplugins.layout.algorithms.graphPartition.ISOMLayout;
import csplugins.layout.algorithms.hierarchicalLayout.HierarchicalLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class LayoutActivator implements BundleActivator {

	private Set<ServiceRegistration> regSet;

	public void start(BundleContext bc) {
		System.out.println("csplugins.layout.LayoutActivator start");	
		// initialize the set of registrations
		regSet = new HashSet<ServiceRegistration>();

		// Add the Cytoscape layouts
		registerLayout(bc, new ForceDirectedLayout(), "Cytoscape Layouts");
		registerLayout(bc, new CircularLayoutAlgorithm(), "Cytoscape Layouts");
		registerLayout(bc, new HierarchicalLayoutAlgorithm(), "Cytoscape Layouts");
		registerLayout(bc, new AttributeCircleLayout(), "Cytoscape Layouts");
		registerLayout(bc, new DegreeSortedCircleLayout(), "Cytoscape Layouts");
		registerLayout(bc, new ISOMLayout(), "Cytoscape Layouts");
		registerLayout(bc, new GroupAttributesLayout(), "Cytoscape Layouts");
		registerLayout(bc, new BioLayoutKKAlgorithm(false), "Cytoscape Layouts");
		registerLayout(bc, new BioLayoutKKAlgorithm(true), "Cytoscape Layouts");
		registerLayout(bc, new BioLayoutFRAlgorithm(true), "Cytoscape Layouts");

		// Add the JGraph layouts
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.ANNEALING), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.MOEN), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.CIRCLE_GRAPH), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.RADIAL_TREE), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.GEM), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.SPRING_EMBEDDED), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.SUGIYAMA), "JGraph Layouts");
		registerLayout(bc, new JGraphLayoutWrapper(JGraphLayoutWrapper.TREE), "JGraph Layouts");
	}

	public void stop(BundleContext bc) {
		for ( ServiceRegistration reg : regSet )
			reg.unregister();
	}

	private void registerLayout(BundleContext bc, CyLayoutAlgorithm cla, String prefMenu) {
		Hashtable props = new Hashtable();
		props.put("preferredMenu", prefMenu);
		props.put("name", cla.getName());
		ServiceRegistration reg = bc.registerService(CyLayoutAlgorithm.class.getName(),cla,props);
		regSet.add( reg );	
	}
}
