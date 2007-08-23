/* vim: set ts=2: */
/*
 * This is based on the ISOMLayout from the JUNG project.
 */
package csplugins.layout.algorithms.graphPartition;

import cern.colt.list.IntArrayList;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;

import cytoscape.CyNetwork;

import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import giny.model.*;

import java.awt.GridLayout;

import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;


/**
 *
 */
public class ISOMLayout extends AbstractGraphPartition {
	private int maxEpoch = 5000;
	private int epoch;
	private int radiusConstantTime = 100;
	private int radius = 5;
	private int minRadius = 1;
	private double adaption;
	private double initialAdaptation = 90.0D / 100.0D;
	private double minAdaptation = 0;
	private double sizeFactor = 100;
	private double factor;
	private double coolingFactor = 2;
	private boolean trace;
	private boolean done;
	private LayoutProperties layoutProperties;
	private LayoutPartition partition;

	//Queue, First In First Out, use add() and get(0)/remove(0)
	private IntArrayList q;
	private String status = null;
	OpenIntObjectHashMap nodeIndexToDataMap;
	OpenIntIntHashMap nodeIndexToLayoutIndex;
	double globalX;
	double globalY;
	double squared_size;

	/**
	 * Creates a new ISOMLayout object.
	 */
	public ISOMLayout() {
		super();

		q = new IntArrayList();
		trace = false;
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Inverted Self-Organizing Map Layout";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return "isom";
	}

	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}

	protected void initialize_properties() {
		layoutProperties.add(new Tunable("maxEpoch", "Number of iterations", Tunable.INTEGER,
		                                 new Integer(5000)));
		layoutProperties.add(new Tunable("sizeFactor", "Size factor", Tunable.INTEGER,
		                                 new Integer(10)));
		layoutProperties.add(new Tunable("radiusConstantTime", "Radius constant", Tunable.INTEGER,
		                                 new Integer(20)));
		layoutProperties.add(new Tunable("radius", "Radius", Tunable.INTEGER, new Integer(5)));
		layoutProperties.add(new Tunable("minRadius", "Minimum radius", Tunable.INTEGER,
		                                 new Integer(1)));
		layoutProperties.add(new Tunable("initialAdaptation", "Initial adaptation", Tunable.DOUBLE,
		                                 new Double(0.9)));
		layoutProperties.add(new Tunable("minAdaptation", "Minimum adaptation value",
		                                 Tunable.DOUBLE, new Double(0)));
		layoutProperties.add(new Tunable("coolingFactor", "Cooling factor", Tunable.DOUBLE,
		                                 new Double(2)));

		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("maxEpoch");

		if ((t != null) && (t.valueChanged() || force))
			maxEpoch = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("sizeFactor");

		if ((t != null) && (t.valueChanged() || force))
			sizeFactor = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("radiusConstantTime");

		if ((t != null) && (t.valueChanged() || force))
			radiusConstantTime = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("radius");

		if ((t != null) && (t.valueChanged() || force))
			radius = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("minRadius");

		if ((t != null) && (t.valueChanged() || force))
			minRadius = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("initialAdaptation");

		if ((t != null) && (t.valueChanged() || force))
			initialAdaptation = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("minAdaptation");

		if ((t != null) && (t.valueChanged() || force))
			minAdaptation = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("coolingFactor");

		if ((t != null) && (t.valueChanged() || force))
			coolingFactor = ((Double) t.getValue()).doubleValue();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param partition DOCUMENT ME!
	 */
	public void layoutPartion(LayoutPartition partition) {
		this.partition = partition;

		int nodeCount = partition.nodeCount();
		nodeIndexToDataMap = new OpenIntObjectHashMap(PrimeFinder.nextPrime(nodeCount));
		nodeIndexToLayoutIndex = new OpenIntIntHashMap(PrimeFinder.nextPrime(nodeCount));
		squared_size = network.getNodeCount() * sizeFactor;

		epoch = 1;

		adaption = initialAdaptation;

		System.out.println("Epoch: " + epoch + " maxEpoch: " + maxEpoch);

		while (epoch < maxEpoch) {
			partition.resetNodes();
			adjust();
			updateParameters();

			if (canceled)
				break;
		}
	}

	/**
	 * @return the closest NodeView to these coords.
	 */
	public int getClosestPosition(double x, double y) {
		double minDistance = Double.MAX_VALUE;
		int closest = 0;
		Iterator nodeIter = partition.nodeIterator();

		while (nodeIter.hasNext()) {
			LayoutNode node = (LayoutNode) nodeIter.next();
			int rootGraphIndex = node.getNode().getRootGraphIndex();

			nodeIndexToLayoutIndex.put(rootGraphIndex, node.getIndex());

			double dx = node.getX();
			double dy = node.getY();
			double dist = (dx * dx) + (dy * dy);

			if (dist < minDistance) {
				minDistance = dist;
				closest = rootGraphIndex;
			}
		}

		return closest;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void adjust() {
		//Generate random position in graph space
		ISOMVertexData tempISOM = new ISOMVertexData();

		// creates a new XY data location
		globalX = 10 + (Math.random() * squared_size);
		globalY = 10 + (Math.random() * squared_size);

		//Get closest vertex to random position
		int winner = getClosestPosition(globalX, globalY);

		Iterator nodeIter = partition.nodeIterator();

		while (nodeIter.hasNext()) {
			int nodeIndex = ((LayoutNode) nodeIter.next()).getNode().getRootGraphIndex();
			ISOMVertexData ivd = getISOMVertexData(nodeIndex);
			ivd.distance = 0;
			ivd.visited = false;
		}

		adjustVertex(winner);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateParameters() {
		epoch++;

		double factor = Math.exp(-1 * coolingFactor * ((1.0 * epoch) / maxEpoch));
		adaption = Math.max(minAdaptation, factor * initialAdaptation);

		if ((radius > minRadius) && ((epoch % radiusConstantTime) == 0)) {
			radius--;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void adjustVertex(int v) {
		q.clear();

		ISOMVertexData ivd = getISOMVertexData(v);
		ivd.distance = 0;
		ivd.visited = true;
		q.add(v);

		int current;
		List<LayoutNode> nodeList = partition.getNodeList();

		while (!q.isEmpty()) {
			current = q.get(0);
			q.remove(0);

			int layoutIndex = nodeIndexToLayoutIndex.get(current);
			LayoutNode currentNode = (LayoutNode) nodeList.get(layoutIndex);

			ISOMVertexData currData = getISOMVertexData(current);

			double current_x = currentNode.getX();
			double current_y = currentNode.getY();

			double dx = globalX - current_x;
			double dy = globalY - current_y;

			// possible mod
			double factor = adaption / Math.pow(2, currData.distance);

			currentNode.setX(current_x + (factor * dx));
			currentNode.setY(current_y + (factor * dy));
			partition.moveNodeToLocation(currentNode);

			if (currData.distance < radius) {
				int[] neighbors = neighborsArray(network, current);

				for (int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index) {
					ISOMVertexData childData = getISOMVertexData(neighbors[neighbor_index]);

					if (!childData.visited) {
						childData.visited = true;
						childData.distance = currData.distance + 1;
						q.add(neighbors[neighbor_index]);
					}
				}
			}
		}

		// Add check to make sure we don't put nodes on top of each other
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ISOMVertexData getISOMVertexData(int v) {
		ISOMVertexData vd = (ISOMVertexData) nodeIndexToDataMap.get(v);

		if (vd == null) {
			vd = new ISOMVertexData();
			nodeIndexToDataMap.put(v, vd);
		}

		return vd;
	}

	public static class ISOMVertexData {
		public DoubleMatrix1D disp;
		int distance;
		boolean visited;

		public ISOMVertexData() {
			initialize();
		}

		public void initialize() {
			disp = new DenseDoubleMatrix1D(2);

			distance = 0;
			visited = false;
		}

		public double getXDisp() {
			return disp.get(0);
		}

		public double getYDisp() {
			return disp.get(1);
		}

		public void setDisp(double x, double y) {
			disp.set(0, x);
			disp.set(1, y);
		}

		public void incrementDisp(double x, double y) {
			disp.set(0, disp.get(0) + x);
			disp.set(1, disp.get(1) + y);
		}

		public void decrementDisp(double x, double y) {
			disp.set(0, disp.get(0) - x);
			disp.set(1, disp.get(1) - y);
		}
	}

	// This is here to replace the deprecated neighborsArray function
	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 * @param nodeIndex DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] neighborsArray(CyNetwork network, int nodeIndex) {
		// Get a list of edges
		int[] edges = network.getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);
		int[] neighbors = new int[edges.length];
		int offset = 0;

		for (int edge = 0; edge < edges.length; edge++) {
			int source = network.getEdgeSourceIndex(edges[edge]);
			int target = network.getEdgeTargetIndex(edges[edge]);

			if (source != nodeIndex) {
				neighbors[offset++] = source;
			} else {
				neighbors[offset++] = target;
			}
		}

		return neighbors;
	}
}
