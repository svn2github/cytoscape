
/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.layout.algorithms.force;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.layout.*;
import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.algorithms.graphPartition.*;

import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;

import java.awt.GridLayout;

import java.util.*;

import javax.swing.JPanel;

import prefuse.util.force.*;

/**
 * This class wraps the Prefuse force-directed layout algorithm.
 * See {@link http://prefuse.org} for more detail.
 */
public class ForceDirectedLayout extends AbstractGraphPartition
{
	private ForceSimulator m_fsim;

	private int numIterations = 100;
	double defaultSpringCoefficient = -1.0;
	double defaultSpringLength = -1.0;
	double defaultNodeMass = 3.0;

	/**
	 * Value to set for doing unweighted layouts
	 */
	public static final String UNWEIGHTEDATTRIBUTE = "(unweighted)";

	/**
	 * Integrators
	 */
	String[] integratorArray = {"Runge-Kutta", "Euler"};

	/**
	 * Minimum and maximum weights.  This is used to
	 * provide a bounds on the weights.
	 */
	protected double minWeightCutoff = 0;
	protected double maxWeightCutoff = Double.MAX_VALUE;

	private boolean supportWeights = true;
	private LayoutProperties layoutProperties;
	Map<LayoutNode,ForceItem> forceItems;

	private Integrator integrator = null;
	
	public ForceDirectedLayout() {
		super();

		m_fsim = new ForceSimulator();
		m_fsim.addForce(new NBodyForce());
		m_fsim.addForce(new SpringForce());
		m_fsim.addForce(new DragForce());

		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
		forceItems = new HashMap<LayoutNode,ForceItem>();
	}
	
	public String getName() {
		return "force-directed";
	}

	public String toString() {
		return "Force-Directed Layout";
	}

	protected void initialize_local() {
		LayoutPartition.setWeightCutoffs(minWeightCutoff, maxWeightCutoff);
		// Depending on whether we are partitioned or not,
		// we use different initialization.  Note that if the user only wants
		// to lay out selected nodes, partitioning becomes a very bad idea!
		if (selectedOnly) {
			// We still use the partition abstraction, even if we're
			// not partitioning.  This makes the code further down
			// much cleaner
			LayoutPartition partition = new LayoutPartition(network, networkView, selectedOnly,
			                                                edgeAttribute);
			partitionList = new ArrayList(1);
			partitionList.add(partition);
		} else {
			partitionList = LayoutPartition.partition(network, networkView, selectedOnly,
			                                          edgeAttribute);
		}
	}


	public void layoutPartion(LayoutPartition part) {
		// Calculate our edge weights
		part.calculateEdgeWeights();

        m_fsim.clear();

	   	// initialize nodes
		Iterator iter = part.nodeIterator();
        while ( iter.hasNext() ) {
			LayoutNode ln = (LayoutNode)iter.next();
			if ( !forceItems.containsKey(ln) )
				forceItems.put(ln, new ForceItem());
            ForceItem fitem = forceItems.get(ln); 
            fitem.mass = getMassValue(ln);
            fitem.location[0] = 0f; 
            fitem.location[1] = 0f; 
            m_fsim.addItem(fitem);
        }
		
		// initialize edges
		iter =  part.edgeIterator(); 
		while ( iter.hasNext() ) {
			LayoutEdge e  = (LayoutEdge)iter.next();
			LayoutNode n1 = e.getSource();
			ForceItem f1 = forceItems.get(n1); 
			LayoutNode n2 = e.getTarget();
			ForceItem f2 = forceItems.get(n2); 
			if ( f1 == null || f2 == null )
				continue;
			m_fsim.addSpring(f1, f2, getSpringCoefficient(e), getSpringLength(e)); 
		}

		// setTaskStatus(5); // This is a rough approximation, but probably good enough
		if (taskMonitor != null) {
			taskMonitor.setStatus("Initializing partition "+part.getPartitionNumber());
		}

		// perform layout
		long timestep = 1000L;
		for ( int i = 0; i < numIterations && !canceled; i++ ) {
			timestep *= (1.0 - i/(double)numIterations);
			long step = timestep+50;
			m_fsim.runSimulator(step);
			setTaskStatus((int)(((double)i/(double)numIterations)*90.+5));
		}
		
        // update positions
        iter = part.nodeIterator(); 
        while ( iter.hasNext() ) {
			LayoutNode ln = (LayoutNode)iter.next();
			if (!ln.isLocked()) {
            	ForceItem fitem = forceItems.get(ln); 
				ln.setX(fitem.location[0]);
				ln.setY(fitem.location[1]);
				part.moveNodeToLocation(ln);
			}
        }
    }
    
    /**
     * Get the mass value associated with the given node. Subclasses should
     * override this method to perform custom mass assignment.
     * @param n the node for which to compute the mass value
     * @return the mass value for the node. By default, all items are given
     * a mass value of 1.0.
     */
    protected float getMassValue(LayoutNode n) {
        return (float)defaultNodeMass;
    }
    
    /**
     * Get the spring length for the given edge. Subclasses should
     * override this method to perform custom spring length assignment.
     * @param e the edge for which to compute the spring length
     * @return the spring length for the edge. A return value of
     * -1 means to ignore this method and use the global default.
     */
    protected float getSpringLength(LayoutEdge e) {
		double weight = e.getWeight();
		return (float)(defaultSpringLength/weight);
    }

    /**
     * Get the spring coefficient for the given edge, which controls the
     * tension or strength of the spring. Subclasses should
     * override this method to perform custom spring tension assignment.
     * @param e the edge for which to compute the spring coefficient.
     * @return the spring coefficient for the edge. A return value of
     * -1 means to ignore this method and use the global default.
     */
    protected float getSpringCoefficient(LayoutEdge e) {
        return (float)defaultSpringCoefficient;
    }

	/**
	 * Return information about our algorithm
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	public byte[] supportsEdgeAttributes() {
		if (!supportWeights)
			return null;
		byte[] attrs = { CyAttributes.TYPE_INTEGER, CyAttributes.TYPE_FLOATING };

		return attrs;
	}

	public List getInitialAttributeList() {
		ArrayList list = new ArrayList();
		list.add(UNWEIGHTEDATTRIBUTE);

		return list;
	}

	
	protected void initialize_properties() {
		layoutProperties.add(new Tunable("defaultSpringCoefficient", "Default Spring Coefficient",
		                                 Tunable.DOUBLE, new Double(defaultSpringCoefficient)));

		layoutProperties.add(new Tunable("defaultSpringLength", "Default Spring Length",
		                                 Tunable.DOUBLE, new Double(defaultSpringLength)));

		layoutProperties.add(new Tunable("defaultNodeMass", "Default Node Mass",
		                                 Tunable.DOUBLE, new Double(defaultNodeMass)));

		layoutProperties.add(new Tunable("numIterations", "Number of Iterations",
		                                 Tunable.INTEGER, new Integer(numIterations)));

		layoutProperties.add(new Tunable("min_weight", "The minimum edge weight to consider",
       		                              Tunable.DOUBLE, new Double(0)));

		layoutProperties.add(new Tunable("max_weight", "The maximum edge weight to consider",
           		                          Tunable.DOUBLE, new Double(Double.MAX_VALUE)));

		layoutProperties.add(new Tunable("integrator", "Integration algorithm to use",
		                                 Tunable.LIST, new Integer(0), 
		                                 (Object) integratorArray, (Object) null, 0));

		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));

		layoutProperties.add(new Tunable("edge_attribute", 
		                                 "The edge attribute that contains the weights",
		                                 Tunable.EDGEATTRIBUTE, "weight",
		                                 (Object) getInitialAttributeList(), (Object) null,
		                                 Tunable.NUMERICATTRIBUTE));

		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("defaultSpringCoefficient");
		if ((t != null) && (t.valueChanged() || force))
			defaultSpringCoefficient = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("defaultSpringLength");
		if ((t != null) && (t.valueChanged() || force))
			defaultSpringLength = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("defaultNodeMass");
		if ((t != null) && (t.valueChanged() || force))
			defaultNodeMass = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("numIterations");
		if ((t != null) && (t.valueChanged() || force))
			numIterations = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("selected_only");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

		t = layoutProperties.get("min_weight");
		if ((t != null) && (t.valueChanged() || force))
			minWeightCutoff = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("max_weight");
		if ((t != null) && (t.valueChanged() || force))
			maxWeightCutoff = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("edge_attribute");
		if ((t != null) && (t.valueChanged() || force))
			edgeAttribute = (t.getValue().toString());

		t = layoutProperties.get("integrator");
		if ((t != null) && (t.valueChanged() || force)) {
			if (((Integer) t.getValue()).intValue() == 0)
				integrator = new RungeKuttaIntegrator();
			else if (((Integer) t.getValue()).intValue() == 1)
				integrator = new EulerIntegrator();
			else
				return;

			m_fsim.setIntegrator(integrator);
		}
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}
}
