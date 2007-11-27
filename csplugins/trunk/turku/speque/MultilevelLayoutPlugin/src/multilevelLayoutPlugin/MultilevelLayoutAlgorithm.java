/*
	
	MultiLevelLayoutPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package multilevelLayoutPlugin;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

/**
 * Class executing the multilevel layout algorithm originally
 * presented by C. Walshaw (2003). The algorithm is slightly 
 * tuned by Pekka Salmela (2007). The algorithm contains two
 * main phases:
 * 1) Construct a hierarchy of continuously coarser graphs by 
 * finding maximal independent sets of edges on each level until
 * there are only two nodes and one edge left.
 * 2) Starting from the coarsest graph consisting, place the two 
 * nodes randomly and use the positions to place nodes of the next 
 * graph in the hierarchy. Then adjust the positions using a 
 * heavily tuned version of force-directed placement algorithm.
 *    
 * @author Pekka Salmela
 */

public class MultilevelLayoutAlgorithm extends AbstractLayout{
	
	private NodePositionManager posManager;
	private double level;
	protected boolean cancel = false;
	private LayoutProperties layoutProperties = null;

	/**
	 * Constructor.
	 */
	public MultilevelLayoutAlgorithm(){
		super();
		layoutProperties = new LayoutProperties(getName());
		layoutProperties.add(new Tunable("repForceMultiplier",
				"<html>Constant multiplier used in calculation of repulsive forces. <p>Default value 0.2, suggested value 0.1 - 0.9.</html>",
				Tunable.DOUBLE, new Double(0.2)));
		layoutProperties.add(new Tunable("tolMultiplier",
                "<html>Parameter used to control the tolerance below which algorithm is concidered to be converged. <p>Default value 0.01, suggested value 0.01 - 0.09.</html>",
                Tunable.DOUBLE, new Double(0.01)));
		layoutProperties.add(new Tunable("clusteringOption",
                "Flag indicating if clustering option should be used during layout calculation.",
                Tunable.BOOLEAN, new Boolean(false)));

		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}
	
	/**
	 * External interface to update our settings
	 */
	public void updateSettings() {
		updateSettings(true);
	}

	/**
	 * Signal that we want to update our internal settings
	 *
	 * @param force force the settings to be updated, if true
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();
		Tunable t1 = layoutProperties.get("repForceMultiplier");
		if ((t1 != null) && (t1.valueChanged() || force))
			MultilevelConfig.C = ((Double) t1.getValue()).doubleValue();
		Tunable t2 = layoutProperties.get("tolMultiplier");
		if ((t2 != null) && (t2.valueChanged() || force))
			MultilevelConfig.tolerance = ((Double) t2.getValue()).doubleValue();
		Tunable t3 = layoutProperties.get("clusteringOption");
		if ((t3 != null) && (t3.valueChanged() || force))
			MultilevelConfig.clusteringEnabled = ((Boolean) t3.getValue()).booleanValue();
	}

	/**
	 * Revert our settings back to the original.
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}
	  
	/**
	 * Main entry point for AbstractLayout classes. Initializes and
	 * runs the algorithm.
	 */
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize();  // Calls initialize_local
		executeLayout();
		networkView.fitContent();
		networkView.updateView();
	}
	
	/**
	 * Tells the algorithm it should be aborted.
	 *  
	 */
	public void setCancel() {
		this.cancel = true;
	}
		
	/**
	 * Call all of the initialization code.  Called from
	 * <code>AbstractLayout.initialize()</code>.
	 * 
	 */
	protected void initialize_local() {
		posManager = new NodePositionManager(networkView.getNetwork().getNodeCount());
		level = 0;
	}
		
	/**
	 * Execute layout algorithm for the graph given when 
	 * calling the constructor method.  
	 *
	 */
	@SuppressWarnings("unchecked")
	private void executeLayout(){
		long start = System.currentTimeMillis();
		
		CyNetwork origNetwork = networkView.getNetwork();
		double originalSize = origNetwork.getNodeCount();

		if(cancel == true) return;
		
		Vector<CyNetwork> components = quickSortNetworks(GraphConnectivityChecker.getGraphComponents(origNetwork));
		
		taskMonitor.setStatus("Finding independent sets...");
		taskMonitor.setPercentCompleted(5);
		double nodesSoFar = 0.0; 
		
		for(CyNetwork cn : components){
			Vector<CyNetwork> networkSet = new Vector<CyNetwork>((int)Math.sqrt(cn.getNodeCount()), 5);
			networkSet.add(cn);
			boolean goOn = true;
			//construct a set of continuously coarser graphs by calculating maximum independent sets
			int nodeCount = cn.getNodeCount();
			if(nodeCount > 2){
				while(goOn){
					CyNetwork next = MaximalIndependentSetFinder.findMaximalIndependentSet(networkSet.lastElement(), level);
					if(nodeCount != next.getNodeCount()){
						nodeCount = next.getNodeCount();
						if(nodeCount == 2) goOn = false;
						networkSet.add(next);
						level++;
					}
					else goOn = false;
				}
			}
			if(cancel == true) {
				for(int i = 1; i<networkSet.size(); i++){Cytoscape.destroyNetwork(networkSet.elementAt(i));}
				return;
			}
	
			taskMonitor.setStatus("Calculating the layout...");
			taskMonitor.setPercentCompleted(10);
			
			if(networkSet.size() == 1){
				Iterator<Node> iter = networkSet.elementAt(0).nodesIterator();
				double i = 1.0;
				while(iter.hasNext()){
					Node n = iter.next();
					posManager.addNode(n.getRootGraphIndex(), i*30.0, i*30.0);
					i++;
				}
			}
			
			CyNetwork currentNetwork = null;
			double previousNaturalSpringLength = 0.0;
			nodesSoFar += cn.getNodeCount();
			
			//starting from the coarsest graph, calculate enhanced force-directed layout for each level
			while(networkSet.size()>1){
				if(cancel == true) {
					for(int i = 1; i<networkSet.size(); i++){Cytoscape.destroyNetwork(networkSet.elementAt(i));}
					return;
				}
				level--;
				//if currentNetwork is not set, then this is the first level
				if(currentNetwork == null){
					currentNetwork = networkSet.lastElement();
					double max = 10.0/(Math.pow(Math.sqrt(4.0/7.0), level+1.0));
					Iterator<CyNode> iter = currentNetwork.nodesIterator();
					CyNode n = iter.next();
					posManager.addNode(n.getRootGraphIndex(), max, 0.0);
					n = iter.next();
					posManager.addNode(n.getRootGraphIndex(), 0.0, max);
					previousNaturalSpringLength = Math.sqrt(2.0) * max;
				}
				
				taskMonitor.setStatus("Calculating the layout on level " + (int)level + " for component " + (components.indexOf(cn)+1)+ "/" + (components.size()));
				taskMonitor.setPercentCompleted((int)(80.0*(nodesSoFar/originalSize)/(level+2.0) + 10.0));
				
				//take the network before the last one from the list
				CyNetwork nextNetwork = networkSet.elementAt(networkSet.size()-2);
				//use nextNetwork to place nodes in currentNetwork
				doOneLevelPlacement(currentNetwork, nextNetwork, previousNaturalSpringLength);
				//use EFDL to nextNetwork
				EnhancedForceDirectedLayout e = new EnhancedForceDirectedLayout(previousNaturalSpringLength, level, nextNetwork, posManager);
				e.doLayout();
				//store the natural spring length
				previousNaturalSpringLength = e.getK();
				//remove used network from the list
				networkSet.remove(networkSet.size()-1);
				
				//clean up (we can not use Cytoscape.destroyNetwork(currentNetwork), because
				//currentNetwork has been created by Cytoscape.getRootGraph.createNetwork(...)
				//and destroying raises an exception)
				Iterator<CyNode> i1 = currentNetwork.nodesIterator();
				while(i1.hasNext()){
					CyNode n = i1.next();
					posManager.removeNode(n.getRootGraphIndex());
					Cytoscape.getRootGraph().removeNode(n);
				}
				Iterator<CyEdge> i2 = currentNetwork.edgesIterator();
				while(i2.hasNext()){Cytoscape.getRootGraph().removeEdge(i2.next());}
				
				//change the network to be used during the next iteration
				currentNetwork = nextNetwork;
			}
		}
		
		//iterate over node positions and apply them to the NetworkView
		taskMonitor.setStatus("Updating node positions...");
		taskMonitor.setPercentCompleted(95);
		
		//first scale the network to look good, or at least tolerable
		
		for(CyNetwork cn : components){
			double minX = Double.POSITIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			double maxX = Double.NEGATIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;
			Iterator<Node> iter = cn.nodesIterator();
			while(iter.hasNext()){
				Node n = iter.next();
				//NodeView nv  = finalView.getNodeView(iter.next());
				if(posManager.getX(n.getRootGraphIndex()) < minX) minX = posManager.getX(n.getRootGraphIndex());
				if(posManager.getY(n.getRootGraphIndex()) < minY) minY = posManager.getY(n.getRootGraphIndex());
				if(posManager.getX(n.getRootGraphIndex()) > maxX) maxX = posManager.getX(n.getRootGraphIndex());
				if(posManager.getY(n.getRootGraphIndex()) > maxY) maxY = posManager.getY(n.getRootGraphIndex());
			}
			double xDifference = maxX -minX;
			if(xDifference == 0.0) xDifference = 1.0;
			double yDifference = maxY -minY;
			if(yDifference == 0.0) yDifference = 1.0;
			iter = cn.nodesIterator();
			while(iter.hasNext()){
				Node n = iter.next();
				posManager.setX(n.getRootGraphIndex(), ((300.0 * Math.sqrt((double)cn.getNodeCount()) * (posManager.getX(n.getRootGraphIndex())-minX))/(xDifference)));
				posManager.setY(n.getRootGraphIndex(), ((300.0 * Math.sqrt((double)cn.getNodeCount()) * (posManager.getY(n.getRootGraphIndex())-minY))/(yDifference)));
			}
		}
		
		taskMonitor.setStatus("Laying out the graph...");
		taskMonitor.setPercentCompleted(99);
		
		CyNetworkView finalView = Cytoscape.getCurrentNetworkView();
		
		Iterator<CyNetwork> iter = components.iterator();
		CyNetwork first = iter.next();
		Iterator<Node> nodesIter = first.nodesIterator();
		double maxX = 0; double maxY = 0;
		while(nodesIter.hasNext()){
			Node n = nodesIter.next();
			NodeView nv  = finalView.getNodeView(n);
			nv.setXPosition(posManager.getX(n.getRootGraphIndex()));
			nv.setYPosition(posManager.getY(n.getRootGraphIndex()));
			if(nv.getXPosition() > maxX) maxX = nv.getXPosition();
			if(nv.getYPosition() > maxY) maxY = nv.getYPosition();
		}
		
		double rowMaxX = -100.0;
		double rowMaxY = maxY;
		while(iter.hasNext()){
			CyNetwork next = iter.next();
			nodesIter = next.nodesIterator();
			double tempYLimit = maxY;
			double tempXLimit = rowMaxX;
			while(nodesIter.hasNext()){
				Node n = nodesIter.next();
				NodeView nv  = finalView.getNodeView(n);
				nv.setXPosition(posManager.getX(n.getRootGraphIndex()) + tempXLimit + 100.0);
				nv.setYPosition(posManager.getY(n.getRootGraphIndex()) + tempYLimit + 100.0);
				if(nv.getXPosition() > rowMaxX) rowMaxX = nv.getXPosition();
				if(nv.getYPosition() > rowMaxY) rowMaxY = nv.getYPosition();
			}
			if(rowMaxX > maxX){
				rowMaxX = -100.0;
				maxY = rowMaxY;
			}
		}
		
		System.out.println("Calculating the layout took " + ((System.currentTimeMillis() -start)/1000.0) + " seconds.");
		
		CyAttributes nodesAttributes = Cytoscape.getNodeAttributes();
		nodesAttributes.deleteAttribute("ml_previous");
		nodesAttributes.deleteAttribute("ml_ancestor1");
		nodesAttributes.deleteAttribute("ml_ancestor2");
		nodesAttributes.deleteAttribute("ml_weight");
		nodesAttributes.deleteAttribute("mllp_partition");
        
		taskMonitor.setPercentCompleted(100);	
		taskMonitor.setStatus("Layout complete");
		System.gc();
		System.out.println("Stop MultiLevelPlugin");
	}
	
	/**
	 * Places the nodes of a more detailed graph using the positions of
	 * a coarser graph. A position manager is used to store node 
	 * positions and node attributes are used to denote connections between
	 * nodes in different graph levels.
	 * @param coarser The graph determining the node positions.
	 * @param finer The graph the placement is applied to. 
	 */
	@SuppressWarnings("unchecked")
	private void doOneLevelPlacement (CyNetwork coarser, CyNetwork finer, double k){
		//iterate over the nodes of the previous graph
		Iterator<CyNode> nodesIterator = coarser.nodesIterator();
		CyAttributes nodesAttributes = Cytoscape.getNodeAttributes();

		while(nodesIterator.hasNext()){
			CyNode n = nodesIterator.next();
			double nX = posManager.getX(n.getRootGraphIndex());
			double nY = posManager.getY(n.getRootGraphIndex());
			if(nodesAttributes.getIntegerAttribute(n.getIdentifier(), "ml_previous") != null){
				//place only one node
				Integer pre = nodesAttributes.getIntegerAttribute(n.getIdentifier(), "ml_previous");
				posManager.addNode(pre, nX, nY);
			}
			else{
				//place two nodes
				Integer anc1 = nodesAttributes.getIntegerAttribute(n.getIdentifier(), "ml_ancestor1");
				Integer anc2 = nodesAttributes.getIntegerAttribute(n.getIdentifier(), "ml_ancestor2");
				posManager.addNode(anc1, nX, nY);
				posManager.addNode(anc2, nX + plusOrMinusOne()*0.001*k, nY + plusOrMinusOne()*0.001*k);
			}
		}
	}
	
	/**
	 * Sorts a vector of networks in ascending order 
	 * according to network sizes.
	 * @param networks Vector of networks to be sorted
	 * @return A new vector containing all the original networks 
	 * in sorted order.
	 */
	private Vector<CyNetwork> quickSortNetworks(Vector<CyNetwork> networks){
		if(networks.size() <= 1) return networks;
	     Vector<CyNetwork> less = new Vector<CyNetwork>(); 
	     Vector<CyNetwork> pivotList = new Vector<CyNetwork>();
	     Vector<CyNetwork> greater = new Vector<CyNetwork>();
	     int pivot = networks.elementAt((int)(Math.random()*networks.size())).getNodeCount();
	     for(CyNetwork cn : networks){
	         if(cn.getNodeCount() < pivot) less.add(cn);
	         if(cn.getNodeCount() == pivot) pivotList.add(cn);
	         if(cn.getNodeCount() > pivot) greater.add(cn);
	     }
	     Vector<CyNetwork> result = new Vector<CyNetwork>();
	     result.addAll(quickSortNetworks(greater));
	     result.addAll(pivotList);
	     result.addAll(quickSortNetworks(less));
	     return result;
	}
	
	/**
	 * Returns a JPanel to be used as part of the Settings dialog for this layout
	 * algorithm.
	 *
	 */
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0,1));
		panel.add(layoutProperties.getTunablePanel());
		return panel;
	}
	
	/**
	 * Returns 1 or -1 with equal probability.
	 * @return 1 or -1, toss a coin to guess which one.
	 */
	private double plusOrMinusOne(){
		if(Math.random()<0.5) return 1.0;
		else return -1.0;
	}

	/**
	 * Return the short-hand name of this algorithm
	 *
	 * @return  short-hand name
	 */
	public String getName() {
		return "multilevel-layout";
	}

	/**
	 *  Return the user-visible name of this layout
	 *
	 * @return  user visible name
	 */
	public String toString() {
		return "Multilevel Layout";
	}

	/**
	 *  Return true if we support performing our layout on a 
	 * limited set of nodes
	 *
	 * @return  true if we support selected-only layout
	 */
	public boolean supportsSelectedOnly() {
		return false;
	}	
	
	public byte[] supportsNodeAttributes() {
		return null;
	}
	
	public byte[] supportsEdgeAttributes() {
		return null;
	}
}
