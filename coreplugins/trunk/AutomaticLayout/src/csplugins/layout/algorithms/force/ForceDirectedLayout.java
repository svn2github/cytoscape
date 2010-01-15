
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

import cytoscape.layout.Tunable;
import cytoscape.layout.LayoutProperties;
import cytoscape.logger.CyLogger;
import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.EdgeWeighter;
import csplugins.layout.algorithms.graphPartition.*;

import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;

import java.awt.GridLayout;
import java.awt.Dimension;

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
	double defaultSpringCoefficient = 1e-4f;
	double defaultSpringLength = 50;
	double defaultNodeMass = 3.0;

	/**
	 * Value to set for doing unweighted layouts
	 */
	public static final String UNWEIGHTEDATTRIBUTE = "(unweighted)";

	/**
	 * Integrators
	 */
	String[] integratorArray = {"Runge-Kutta", "Euler", "Backward Euler"};

	private boolean supportWeights = true;
	private LayoutProperties layoutProperties;
	Map<LayoutNode,ForceItem> forceItems;

	private Integrator integrator = null;
	
	public ForceDirectedLayout() {
		super();

		logger = CyLogger.getLogger(ForceDirectedLayout.class);
		// logger.setDebug(true);

		if (edgeWeighter == null)
			edgeWeighter = new EdgeWeighter();

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
	}


	public void layoutPartion(LayoutPartition part) {
		Dimension initialLocation = null;
		// logger.debug("layoutPartion: "+part.getEdgeList().size()+" edges");
		// Calculate our edge weights
		part.calculateEdgeWeights();
		// logger.debug("layoutPartion: "+part.getEdgeList().size()+" edges after calculateEdgeWeights");

		m_fsim.clear();

		// initialize nodes
		for (LayoutNode ln: part.getNodeList()) {
			if ( !forceItems.containsKey(ln) )
				forceItems.put(ln, new ForceItem());
			ForceItem fitem = forceItems.get(ln); 
			fitem.mass = getMassValue(ln);
			fitem.location[0] = 0f; 
			fitem.location[1] = 0f; 
			m_fsim.addItem(fitem);
		}
		
		// initialize edges
		for (LayoutEdge e: part.getEdgeList()) {
			LayoutNode n1 = e.getSource();
			ForceItem f1 = forceItems.get(n1); 
			LayoutNode n2 = e.getTarget();
			ForceItem f2 = forceItems.get(n2); 
			if ( f1 == null || f2 == null )
				continue;
			// logger.debug("Adding edge "+e+" with spring coeffficient = "+getSpringCoefficient(e)+" and length "+getSpringLength(e));
			m_fsim.addSpring(f1, f2, getSpringCoefficient(e), getSpringLength(e)); 
		}

		// setTaskStatus(5); // This is a rough approximation, but probably good enough
		if (taskMonitor != null) {
			taskMonitor.setStatus("Initializing partition "+part.getPartitionNumber());
		}

		// Figure out our starting point
		if (selectedOnly) {
			initialLocation = part.getAverageLocation();
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
		part.resetNodes(); // reset the nodes so we get the new average location
		for (LayoutNode ln: part.getNodeList()) {
			if (!ln.isLocked()) {
				ForceItem fitem = forceItems.get(ln); 
				ln.setX(fitem.location[0]);
				ln.setY(fitem.location[1]);
				part.moveNodeToLocation(ln);
			}
		}
		// Not quite done, yet.  If we're only laying out selected nodes, we need
		// to migrate the selected nodes back to their starting position
		if (selectedOnly) {
			double xDelta = 0.0;
			double yDelta = 0.0;
			Dimension finalLocation = part.getAverageLocation();
			xDelta = finalLocation.getWidth() - initialLocation.getWidth();
			yDelta = finalLocation.getHeight() - initialLocation.getHeight();
			for (LayoutNode v: part.getNodeList()) {
				if (!v.isLocked()) {
					v.decrement(xDelta, yDelta);
					part.moveNodeToLocation(v);
				}
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

		layoutProperties.add(new Tunable("standard", "Standard settings",
		                                 Tunable.GROUP, new Integer(2)));

		layoutProperties.add(new Tunable("partition", "Partition graph before layout",
		                                 Tunable.BOOLEAN, new Boolean(true)));

		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));

		edgeWeighter.getWeightTunables(layoutProperties, getInitialAttributeList());

		layoutProperties.add(new Tunable("force_alg_settings", "Algorithm settings",
		                                 Tunable.GROUP, new Integer(5)));

		layoutProperties.add(new Tunable("defaultSpringCoefficient", "Default Spring Coefficient",
		                                 Tunable.DOUBLE, new Double(defaultSpringCoefficient)));

		layoutProperties.add(new Tunable("defaultSpringLength", "Default Spring Length",
		                                 Tunable.DOUBLE, new Double(defaultSpringLength)));

		layoutProperties.add(new Tunable("defaultNodeMass", "Default Node Mass",
		                                 Tunable.DOUBLE, new Double(defaultNodeMass)));

		layoutProperties.add(new Tunable("numIterations", "Number of Iterations",
		                                 Tunable.INTEGER, new Integer(numIterations)));

		layoutProperties.add(new Tunable("integrator", "Integration algorithm to use",
		                                 Tunable.LIST, new Integer(0), 
		                                 (Object) integratorArray, (Object) null, 0));

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

		Tunable t = layoutProperties.get("selected_only");
		if ((t != null) && (t.valueChanged() || force)) {
			selectedOnly = ((Boolean) t.getValue()).booleanValue();
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("partition");
		if ((t != null) && (t.valueChanged() || force)) {
			setPartition(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("defaultSpringCoefficient");
		if ((t != null) && (t.valueChanged() || force)) {
			defaultSpringCoefficient = ((Double) t.getValue()).doubleValue();
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("defaultSpringLength");
		if ((t != null) && (t.valueChanged() || force)) {
			defaultSpringLength = ((Double) t.getValue()).doubleValue();
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("defaultNodeMass");
		if ((t != null) && (t.valueChanged() || force)) {
			defaultNodeMass = ((Double) t.getValue()).doubleValue();
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("numIterations");
		if ((t != null) && (t.valueChanged() || force)) {
			numIterations = ((Integer) t.getValue()).intValue();
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("integrator");
		if ((t != null) && (t.valueChanged() || force)) {
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
			if (((Integer) t.getValue()).intValue() == 0)
				integrator = new RungeKuttaIntegrator();
			else if (((Integer) t.getValue()).intValue() == 1)
				integrator = new EulerIntegrator();
			else if (((Integer) t.getValue()).intValue() == 2)
				integrator = new BackwardEulerIntegrator();
			else
				return;
			m_fsim.setIntegrator(integrator);
		}

		edgeWeighter.updateSettings(layoutProperties, force);
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	public JPanel getSettingsPanel() {
		// JPanel panel = new JPanel(new GridLayout(0, 1));
		// panel.add(layoutProperties.getTunablePanel());
		// return panel;
		return layoutProperties.getTunablePanel();

	}
}
