package org.genmapp.golayout;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import ding.view.DEdgeView;
import ding.view.DGraphView;

public class EdgeBundlerPlugin extends CytoscapePlugin {
	public EdgeBundlerPlugin () {
//	CyLayouts.addLayout(new EdgeBundler(), "Bundle Edges");
	EdgeBundlerAction mainAction = new EdgeBundlerAction ();
	Cytoscape.getDesktop().getCyMenus().addAction(mainAction);

}


// ~ Inner Classes
// //////////////////////////////////////////////////////////

public class EdgeBundlerAction extends CytoscapeAction {


	public EdgeBundlerAction () {
		super("Bundle Edges ...");
		setPreferredMenu("Layout");

	}


	public void actionPerformed(ActionEvent e) {
	
		EdgeBundler eb = new EdgeBundler();
		CyLayouts.addLayout(eb, "Bundle Edges ...");
		CyLayoutAlgorithm layout = CyLayouts.getLayout("edge-bundler");
		layout.doLayout(Cytoscape.getCurrentNetworkView());
	}
}
	
	
	/**
	 * adpted from Cerebral (Barsky, Munzner, Gardy, Kincaid, University of British Columbia/Agilent)
	 * http://www.cs.ubc.ca/nest/imager/tr/2008/cerebral/ 
	 * 
	 * @author ajk
	 *
	 */
	public class EdgeBundler extends AbstractLayout 
	{
		LayoutProperties layoutProperties = null;
		CyNetwork network = null;
		CyNetworkView networkView = null;
		private int scale = 60;
		


		private List<Node> sortedNodes = new ArrayList();
		private float beta = 1.0f;
		
		
		// a hashmap of different lists of control points, indexed by Edge
		HashMap<Edge, List> splineDataAdjustedControlPoints = new HashMap();
		HashMap<Edge, List> reverseControlPoints = new HashMap();
		HashMap<Node, List<EdgeCluster>> edgeClustersMap = new HashMap(); // edge clusters for a given focus node 
		
	    private Point2D p0 = new Point2D.Float();

	    private Point2D pNminus1 = new Point2D.Float();

		public CyNetworkView getNetworkView() {
			return networkView;
		}

		public void setNetworkView(CyNetworkView networkView) {
			this.networkView = networkView;
		}

		public CyNetwork getNetwork() {
			return network;
		}

		public void setNetwork(CyNetwork net) {
			this.network = net;
		}

		public EdgeBundler ()
		{   
			
			super();
			
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.add(new Tunable("beta", 
					"some sort of constant for adjusting control points", Tunable.DOUBLE, new Double(1.0)));
			layoutProperties.add(new Tunable("scale", 
					"scale factor for iterating distances of edge lengths", Tunable.INTEGER, new Integer(60)));
		
			// We've now set all of our tunables, so we can read the property
			// file now and adjust as appropriate
			layoutProperties.initializeProperties();

			// Finally, update everything. We need to do this to update
			// any of our values based on what we read from the property file
			updateSettings(true);
			this.setNetwork(Cytoscape.getCurrentNetwork());
			this.setNetworkView(Cytoscape.getCurrentNetworkView());

		}

		/**
		 * External interface to update our settings
		 */
		public void updateSettings() {
			updateSettings(true);
		}

		/**
		 * Signals that we want to update our internal settings
		 * 
		 * @param force
		 *            force the settings to be updated, if true
		 */
		public void updateSettings(boolean force) {
			layoutProperties.updateValues();
			Tunable t = layoutProperties.get("beta");
			if ((t != null) && (t.valueChanged() || force))
				beta = ((Double) t.getValue()).floatValue();
			Tunable t2 = layoutProperties.get("scale");
			if ((t2 != null) && (t2.valueChanged() || force))
				scale = ((Integer) t2.getValue()).intValue();

		}

		/**
		 * Reverts our settings back to the original.
		 */
		public void revertSettings() {
			layoutProperties.revertProperties();
		}

		public LayoutProperties getSettings() {
			return layoutProperties;
		}

		/**
		 * Returns the short-hand name of this algorithm
		 * 
		 * @return short-hand name
		 */
		public String getName() {
			return "edge-bundler";
		}

		/**
		 * Returns the user-visible name of this layout
		 * 
		 * @return user visible name
		 */
		public String toString() {
			return "Edge Bundler";
		}

		/**
		 * Return true if we support performing our layout on a limited set of nodes
		 * 
		 * @return true if we support selected-only layout
		 */
		public boolean supportsSelectedOnly() {
			return false;
		}

		/**
		 * Returns the types of node attributes supported by this algorithm.
		 * 
		 * @return the list of supported attribute types, or null if node attributes
		 *         are not supported
		 */
		public byte[] supportsNodeAttributes() {
			return null;
		}

		/**
		 * Returns the types of edge attributes supported by this algorithm.
		 * 
		 * @return the list of supported attribute types, or null if edge attributes
		 *         are not supported
		 */
		public byte[] supportsEdgeAttributes() {
			return null;
		}

		/**
		 * Returns a JPanel to be used as part of the Settings dialog for this
		 * layout algorithm.
		 * 
		 */
		public JPanel getSettingsPanel() {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(layoutProperties.getTunablePanel());

			return panel;
		}


		/**
		 * The layout protocol...
		 */
		public void construct() {
			
			taskMonitor.setStatus("Bundling edges");
			taskMonitor.setPercentCompleted(1);
			
		    Iterator<Node> nodesIt = this.network.nodesIterator();
		    while (nodesIt.hasNext()) {
		         Node node = nodesIt.next();
		         sortedNodes.add(node);
		     }
		    
		    // go through edges and initialize beta adjusted control points
		    // also set Line type to curved
		    Iterator<Edge> edgesIt = this.network.edgesIterator();
	        while (edgesIt.hasNext()) {
	            Edge edge = (Edge) edgesIt.next();
                List<Point2D> betaPoints = splineDataAdjustedControlPoints.get(edge);
                if (betaPoints == null)
                {
                	betaPoints = new ArrayList<Point2D>();
                	splineDataAdjustedControlPoints.put(edge, betaPoints);
                }
                EdgeView ev = networkView.getEdgeView(edge);
                DEdgeView dev = (DEdgeView) ev;
                dev.setLineType(EdgeView.CURVED_LINES);
	        }

		    Collections.sort(sortedNodes, new NodeSorter(this.network));
			
	        recomputeLocations();
			Tunable t = layoutProperties.get("beta");
			if (t != null) {
	        setBeta(((Double) t.getValue()).floatValue());
			}

		}
		
		   public void setBeta(float beta) {
		        this.beta = beta;
		        // recomputeLocations();

		        Iterator<Edge> edgesIt = this.network.edgesIterator();

		        while (edgesIt.hasNext()) {
		            Edge edge = (Edge) edgesIt.next();
		            Node source = edge.getSource();
		            Node target = edge.getTarget();
		            if (source != target) {
		            	EdgeView ev = this.networkView.getEdgeView(edge);
		            	DGraphView dview = (DGraphView) this.networkView;
		                // why is DEdgeView not a public class?
		            	DEdgeView dev = (DEdgeView) ev;
		                List<Point2D> controlPoints = (List) dev.getHandles();
		                List<Point2D> betaPoints = splineDataAdjustedControlPoints.get(edge);
		                if (betaPoints == null)
		                {
		                	betaPoints = new ArrayList<Point2D>();
		                	splineDataAdjustedControlPoints.put(edge, betaPoints);
		                }
		                betaAdjustControlPoints(controlPoints, 
		                		 splineDataAdjustedControlPoints.get(edge), controlPoints
		                        .size());
		                assignEdgeFromControlPoints(edge);
		            } else {
		                assignEdgeFromControlPoints(edge);
		            }
		        }

		    }	

		   private float getSegmentLength(float x1, float y1, float x2, float y2) {
		        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		    }
		   
		    private void betaAdjustControlPoints(List points, List betaPoints,
		            int pointCount) {
		        betaPoints.clear();
		        if (pointCount >= 2) {
		            p0.setLocation((Point2D) points.get(0));
		            pNminus1.setLocation((Point2D) points.get(pointCount - 1));

		            for (int i = 0; i < pointCount; i++) {
		                Point2D pi = (Point2D) points.get(i);
		                float thisXContribution = (float) (pi.getX() * beta);
		                float oneminusbeta = 1 - beta;
		                float distanceAlongSpline = i / (pointCount - 1);
		                float deltaX = (float) (pNminus1.getX() - p0.getX());

		                float pix = thisXContribution;
		                pix += oneminusbeta
		                        * ((p0.getX() + distanceAlongSpline * deltaX));

		                float thisYContribution = (float) (pi.getY() * beta);
		                float deltaY = (float) (pNminus1.getY() - p0.getY());

		                float piy = thisYContribution;
		                piy += oneminusbeta
		                        * ((p0.getY() + distanceAlongSpline * deltaY));

		                betaPoints.add(new Point2D.Float(pix, piy));
		            }
		        }
		    }


		    // TODO: need to rewrite this to just reassign betaAdjustedControlPoints to edges
		    private void assignEdgeFromControlPoints(Edge edge) {
		        List<Point2D> controlPoints = splineDataAdjustedControlPoints.get(edge);

		        EdgeView ev = this.networkView.getEdgeView(edge);
		        DEdgeView dev = (DEdgeView) ev;
		        dev.removeAllHandles();
		        
		        for (int i = 0; i < controlPoints.size(); i++)
		        {

		            Point2D p1 = (Point2D) controlPoints.get(i);
		            dev.addHandle(p1);

		        }
		        // do we need to validate edges?
//		        edge.setValidated(false);
		    }


		    
		

		    public void recomputeLocations() {
		        clearEdges();
		        highDegreeNodeCluster();
		    }

		    
		    private void clearEdges() {
		        Iterator<Edge> edgeIt = this.network.edgesIterator();
		        
		        while (edgeIt.hasNext()) {
		            Edge edge = (Edge) edgeIt.next();
		            EdgeView eview = this.networkView.getEdgeView(edge);
		            ((DEdgeView) eview).removeAllHandles();
		
//		            edge.setInt(Cerebral.BUNDLE_MAX_SIZE, 0);
		        }
		        Iterator nodesIt = sortedNodes.iterator();
		        while (nodesIt.hasNext()) {
		            Node focusNode = (Node) nodesIt.next();
		            // TODO: maybe remove focus node?
		            edgeClustersMap.put(focusNode, null);
		        }
		    }
		    
		   
		    double getThisEdgeLength(Edge edge)
		    {
	            Node source = edge.getSource();
	            Node target = edge.getTarget();	
	            NodeView ns = this.networkView.getNodeView(source);
	            NodeView nt = this.networkView.getNodeView(target);
	            
	            double width = ns.getWidth();
	            double height = nt.getHeight();
	            
	            return Math.sqrt((width * width) + (height * height));
	            
		    }
		    
		    Node getAdjacentNode (Edge edge, Node node)
		    {
		    	Node source = edge.getSource();
	            Node target = edge.getTarget();	
	           if (node == source) { return target; }
	           else if (node == target) { return source; }
	           else 
	           {
	        	   System.out.println ("getAdjacentNode: boo hoo, we have a problem with: " + node + " and " + edge);
	        	   return null;
	           }
		    }
		    
		    private void highDegreeNodeCluster() {
		        Iterator edgeIt;
		        Iterator nodesIt;

		        Set groupedEdges = new HashSet();

		        // Pass 1
		        int maxClusterSeparation = 3;
		        for (int i = 1; i <= maxClusterSeparation; i++) {
		            int maxSepThisIteration = i * scale; /// was SearchGridLayout.SCALE;
		            nodesIt = sortedNodes.iterator();
		            while (nodesIt.hasNext()) {
		                Node focusNode = (Node) nodesIt.next();
		                List<EdgeCluster> edgeClusters = edgeClustersMap.get(focusNode);

		                Set focusNodeEdges = new HashSet();
		                List<Edge> edges = network.getAdjacentEdgesList(focusNode, true, true, true);
		                for (int j = 0; j < edges.size(); j++){
		                    Edge edge = edges.get(j);
		                    if (edge.getSource() != edge.getTarget()) {
		                    	
		                        if (!groupedEdges.contains(edge)) {
		                            if (getThisEdgeLength(edge) > (i * scale * 1.0))
		                                focusNodeEdges.add(edge);
		                            }
		                        }
		                   
		                }
		                while (!focusNodeEdges.isEmpty()) {
		                    EdgeCluster edgeCluster = new EdgeCluster(focusNode);

		                    Edge edge = (Edge) focusNodeEdges.iterator().next();
		                    focusNodeEdges.remove(edge);
		                    edgeCluster.addEdge(edge);

		                    List neighboursToCheck = new ArrayList();
		                    Node neighbor = getAdjacentNode (edge, focusNode);
		                    if (neighbor != null)
		                    {
		                        neighboursToCheck.add(neighbor);
		                    }
		                    
		                    while (!neighboursToCheck.isEmpty()) {
		                        Node neighbourNode = (Node) neighboursToCheck
		                                .remove(0);
		                        Iterator edgesIt = focusNodeEdges.iterator();
		                        while (edgesIt.hasNext()) {
		                            Edge nextEdge = (Edge) edgesIt.next();
		                            Node potentialNeighbour = getAdjacentNode(nextEdge, focusNode);
		                            NodeView neighbourView = this.networkView.getNodeView(neighbourNode);
		                            NodeView potentialView = this.networkView.getNodeView(potentialNeighbour);
		                            double xSep = Math.abs(neighbourView.getXPosition()
		                                    - potentialView.getXPosition());
		                            double ySep = Math.abs(neighbourView.getYPosition()
		                                    - potentialView.getYPosition());
		                            if ((xSep <= maxSepThisIteration && ySep <= maxSepThisIteration)) {
		                                edgeCluster.addEdge(nextEdge);
		                                neighboursToCheck.add(potentialNeighbour);
		                                edgesIt.remove();
		                            }
		                        }
		                    }

		                    System.out.println ("edge clusters = " + edgeClusters);
		                    System.out.println ("edge clusters size = " + edgeClusters.size());
		                    System.out.println ("edge cluster = " + edgeCluster);
		                    System.out.println ("edge cluster size = " + edgeCluster.size());
		                    if (edgeCluster.size() > 1 || i == maxClusterSeparation) {
		                        edgeClusters.add(edgeCluster);
		                        Iterator clusterEdgesIt = edgeCluster.iterator();
		                        while (clusterEdgesIt.hasNext()) {
		                            Edge e = (Edge) clusterEdgesIt.next();
		                            groupedEdges.add(e);
		                        }
		                    }
		                }
		            }
		        }

		        // add the single edges into their own cluster.
		        nodesIt = sortedNodes.iterator();
		        while (nodesIt.hasNext()) {
		            Node focusNode = (Node) nodesIt.next();
		            List edgeClusters = this.edgeClustersMap.get(focusNode); 
		            if (edgeClusters == null)
		            {
		            	
		            	edgeClusters = new ArrayList<EdgeCluster>();  
		            }
		            	
		            Iterator edgesIt = this.network.getAdjacentEdgesList(focusNode, true, true, true).iterator();
		            while (edgesIt.hasNext()) {
		                Edge edge = (Edge) edgesIt.next();
		                if (!groupedEdges.contains(edge)) {
		                    EdgeCluster single = new EdgeCluster(focusNode);
		                    single.addEdge(edge);
		                    edgeClusters.add(single);
		                    groupedEdges.add(edge);
		                }
		            }
		            this.edgeClustersMap.put(focusNode, edgeClusters);
		        }

		        // Pass 2
		        // Now that the edges are clumped into clusters, try joining
		        // neighbouring
		        // clusters.

		        NeighbourSorter neighbourSorter = new NeighbourSorter();

		        float angleLimit = (float) (Math.PI / 8);
		        nodesIt = sortedNodes.iterator();
		        while (nodesIt.hasNext()) {
		            Node focusNode = (Node) nodesIt.next();
		            

		            List edgeClusters = this.edgeClustersMap.get(focusNode);
//		            (List) focusNode.get(Cerebral.EDGE_CLUSTERS);

		            // System.out.println(focusNode.getString(CytoPrefuse.LABEL) + ": "
		            // + edgeClusters.size());

		            if (edgeClusters.size() > 0) {
		                Collections.sort(edgeClusters, neighbourSorter);

		                // Start with each cluster in its own list
		                List allClusters = new ArrayList();
		                for (int i = 0; i < edgeClusters.size(); i++) {
		                    List single = new ArrayList();
		                    single.add(edgeClusters.get(i));
		                    allClusters.add(single);
		                }

		                // and merge neighbouring lists until a limit is reached
		                List newClusterList = new ArrayList();

		                boolean changed = false;
		                do {
		                    newClusterList.clear();
		                    changed = false;
		                    for (int i = 0; i < allClusters.size() - 1; i += 2) {
		                        List group1 = (List) allClusters.get(i);
		                        List group2 = (List) allClusters.get(i + 1);

		                        float g1angle = getAverageAngle(group1, focusNode);
		                        float g2angle = getAverageAngle(group2, focusNode);

		                        if (Math.abs(g1angle - g2angle) < angleLimit) {
		                            group2.addAll(group1);
		                            newClusterList.add(group2);
		                            changed = true;
		                        } else {
		                            newClusterList.add(group1);
		                            newClusterList.add(group2);
		                        }
		                    }
		                    if (allClusters.size() % 2 == 1) {
		                        newClusterList.add(allClusters
		                                .get(allClusters.size() - 1));
		                    }
		                    if (!changed && newClusterList.size() > 1) {
		                        // see if the first and last can merge
		                        List group1 = (List) newClusterList.get(0);
		                        List group2 = (List) newClusterList.get(newClusterList
		                                .size() - 1);

		                        float g1angle = getAverageAngle(group1, focusNode);
		                        float g2angle = getAverageAngle(group2, focusNode);

		                        if (Math.abs(g1angle - g2angle + (2 * Math.PI)) < angleLimit) {
		                            group2.addAll(group1);
		                            newClusterList.remove(group1);
		                            changed = true;
		                        }
		                    }
		                    if (changed) {

		                        List temp = allClusters;
		                        allClusters = newClusterList;
		                        newClusterList = temp;
		                    }
		                } while (changed);

		                for (int i = 0; i < allClusters.size(); i++) {
		                    setGroupControlPoints((List) allClusters.get(i), focusNode);
		                }
		            }
		        }

		        // AJK: don't worry about orienting edges, this is about color interpolation when rendering spline and
		        //      we are not directly rendering spline
//		        orientEdges();

		        edgeIt = this.network.edgesIterator();
		        while (edgeIt.hasNext()) {
		            Edge edge = (Edge) edgeIt.next();
		            // AJK: don't worry about fixing up self-edges, Cytoscape handles that well already (I believe)
//		            if (edge.getBoolean(Cerebral.SELF_EDGE)) {
//		                fixupSelfEdge(edge, (List) edge
//		                        .get(Cerebral.SPLINE_BETA_ADJUSTED));
//		            } else {
	            	EdgeView ev = this.networkView.getEdgeView(edge);
	            	DGraphView dview = (DGraphView) this.networkView;
	                // why is DEdgeView not a public class?
	            	DEdgeView dev = (DEdgeView) ev;
	                List<Point2D> controlPoints = (List) dev.getHandles();
	                betaAdjustControlPoints(controlPoints, 
	                		 splineDataAdjustedControlPoints.get(edge), controlPoints
	                        .size());
//		            }
		            assignEdgeFromControlPoints(edge);

		        }
		    }


		    private float getAverageAngle(List group, Node focusNode) {
		        Iterator ecIt = group.iterator();
		        float groupX = 0.0f;
		        float groupY = 0.0f;
		        float count = 0;
		        while (ecIt.hasNext()) {
		            EdgeCluster ec = (EdgeCluster) ecIt.next();
		            Point2D p = ec.getEndControlPoint();
		            groupX += p.getX() * ec.size();
		            groupY += p.getY() * ec.size();
		            count += ec.size();
		        }
		        NodeView focusView = this.networkView.getNodeView(focusNode);
		        return (float) Math.atan2(groupY / count - focusView.getYPosition(), groupX
		                / count - focusView.getXPosition());
		    }

		    private void setGroupControlPoints(List group, Node focusNode) {

		        if (group.size() == 0)
		            return;
		        Collections.sort(group, new ClusterEdgeLengthSorter());

		        Iterator groupIt = group.iterator();
		        while (groupIt.hasNext()) {
		            EdgeCluster cluster = (EdgeCluster) groupIt.next();
		            cluster.assignSourceControlPoint();
		        }

		        // Assign a starting control point to show strong clustering leaving the
		        // node
		        float groupAngle = getAverageAngle(group, focusNode);
		        NodeView focusNodeView = networkView.getNodeView(focusNode);
		        Point2D start = new Point2D.Float(
		                (float) ((focusNodeView.getXPosition() + (45) * Math.cos(groupAngle))),
		                (float) (focusNodeView.getYPosition() + (45) * Math.sin(groupAngle)));
		        for (int i = 0; i < group.size(); i++) {
		            EdgeCluster cluster = (EdgeCluster) group.get(i);
		            cluster.addControlPoint(start);
		        }

		        for (int i = 0; i < group.size(); i++) {
		            EdgeCluster cluster = (EdgeCluster) group.get(i);
		            Point2D p = cluster.getEndControlPoint();
		            for (int j = i; j < group.size(); j++) {
		                EdgeCluster remainingCluster = (EdgeCluster) group.get(j);
		                remainingCluster.addControlPoint(p);
		            }
		        }

		        groupIt = group.iterator();
		        while (groupIt.hasNext()) {
		            EdgeCluster cluster = (EdgeCluster) groupIt.next();
		            cluster.assignEndControlPoint();
		        }
		    }
		    

		    
		   private class EdgeCluster {

		        private float groupX = 0;

		        private float groupY = 0;

		        private List edges = new ArrayList();

		        private Node clusterNode;

		        private float minEdgeLength;

		        public EdgeCluster(Node clusterNode) {
		            this.clusterNode = clusterNode;
		            minEdgeLength = Float.MAX_VALUE;
		        }

		        public void assignEndControlPoint() {
		            Iterator<Edge> edgeIt = edges.iterator();
		            while (edgeIt.hasNext()) {
		                Edge edge = edgeIt.next();
		                Node endNode = getAdjacentNode(edge, clusterNode);
		                NodeView endNodeView = networkView.getNodeView(endNode);
		                EdgeView ev = networkView.getEdgeView(edge);
		                DEdgeView dev = (DEdgeView) ev;
		                dev.addHandle(new Point2D.Float((float) endNodeView.getXPosition(),
		                        (float) endNodeView.getYPosition()));
		                /*
		                 * Line2D l = new Line2D.Float
		                 * ((float)clusterNode.getEndX(),(float) clusterNode.getEndY(),
		                 * (float)endNode.getEndX(), (float) endNode.getEndY()); Point2D
		                 * point = getIntersectionPoint (l, endNode); double theta =
		                 * Math.atan2(clusterNode.getEndY() -
		                 * endNode.getEndY(),clusterNode.getEndX() - endNode.getEndX());
		                 * point.setLocation(point.getX() +
		                 * 12*Math.cos(theta),point.getY() + 12*Math.sin(theta));
		                 * 
		                 * controlPoints.add(point.clone());
		                 */
		            }
		        }

		        public void addControlPoint(Point2D p) {
		            Iterator<Edge> edgeIt = edges.iterator();
		            while (edgeIt.hasNext()) {
		                Edge edge = edgeIt.next();
		                EdgeView ev = networkView.getEdgeView(edge);
		                DEdgeView dev = (DEdgeView) ev;
		                dev.addHandle(p);
		            }

		        }

		        public Point2D getEndControlPoint() {
		            int controlDistance;
		            if (minEdgeLength > 100) {
		                controlDistance = 45;
		            } else {
		                controlDistance = 15;
		            }

		            float groupAngle = getClusterAngle();
		            NodeView clusterNodeView = networkView.getNodeView(clusterNode);
		            return new Point2D.Float(
		                    (float) (clusterNodeView.getXPosition() + (minEdgeLength - controlDistance)
		                            * Math.cos(groupAngle)), (float) ((clusterNodeView.getYPosition()
		                             + (minEdgeLength - controlDistance)
		                            * Math.sin(groupAngle))));
		        }

		        public void assignSourceControlPoint() {

		            Iterator<Edge> edgeIt = edges.iterator();
		            while (edgeIt.hasNext()) {

		                Edge edge = edgeIt.next();
		                EdgeView ev = networkView.getEdgeView(edge);
		                DEdgeView dev = (DEdgeView) ev;
//		                List controlPoints = (List) edge.get(Cerebral.SPLINE_CONTROL);

//		                if (edge.getSourceNode() != clusterNode) {
//		                    edge.setBoolean(Cerebral.REORIENT, true);
//		                }
		                NodeView clusterNodeView = networkView.getNodeView(clusterNode);
		                dev.addHandle(new Point2D.Float((float) clusterNodeView.getXPosition(), 
		                       (float) clusterNodeView.getYPosition()));
		                /*
		                 * Node endNode = edge.getAdjacentItem(clusterNode); Line2D
		                 * l = new Line2D.Float ( (float)endNode.getEndX(), (float)
		                 * endNode.getEndY(),(float)clusterNode.getEndX(),(float)
		                 * clusterNode.getEndY()); Point2D point = getIntersectionPoint
		                 * (l,clusterNode);
		                 * 
		                 * double theta = Math.atan2(endNode.getEndY() -
		                 * clusterNode.getEndY(), endNode.getEndX() -
		                 * clusterNode.getEndX()); point.setLocation(point.getX() +
		                 * 12*Math.cos(theta),point.getY() + 12*Math.sin(theta));
		                 * 
		                 * controlPoints.add( point.clone());
		                 */

		            }
		        }

		        void addEdge(Edge e) {
		            edges.add(e);
		            Node n1 = getAdjacentNode(e, clusterNode);
		            NodeView clusterNodeView = networkView.getNodeView(clusterNode);
		            NodeView n1View = networkView.getNodeView(n1);
		            groupX += n1View.getXPosition() - clusterNodeView.getXPosition();
		            groupY += n1View.getYPosition() - clusterNodeView.getYPosition();
		            float thisEdgeLength = getSegmentLength((float) clusterNodeView.getXPosition(),
		                    (float) clusterNodeView.getYPosition(), (float) n1View.getXPosition(),
		                    (float) n1View.getYPosition());
		            if (thisEdgeLength < minEdgeLength) {
		                minEdgeLength = thisEdgeLength;
		            }

		        }

		        void clear() {
		            edges.clear();
		            groupX = 0;
		            groupY = 0;
		        }

		        int size() {
		            return edges.size();
		        }

		        Iterator iterator() {
		            return edges.iterator();
		        }

		        public float getClusterAngle() {
		            float theta = (float) Math.atan2(groupY / edges.size(), groupX
		                    / edges.size());
		            // if (theta < 0) {
		            // theta += Math.PI * 2;
		            // }
		            return theta;
		        }

		        public float getAverageEdgeLength() {
		            return (float) Math.sqrt((groupX / edges.size())
		                    * (groupX / edges.size()) + (groupY / edges.size())
		                    * (groupY / edges.size()));
		        }
		    }

		    private class ClusterEdgeLengthSorter implements Comparator {
		        public int compare(Object arg0, Object arg1) {
		            EdgeCluster ec1 = (EdgeCluster) arg0;
		            EdgeCluster ec2 = (EdgeCluster) arg1;
		            if (ec1.getAverageEdgeLength() < ec2.getAverageEdgeLength()) {
		                return -1;
		            } else {
		                return 1;
		            }
		        }
		    }

			
		   private class NodeSorter implements Comparator {

			   CyNetwork net;
			   
			   public NodeSorter (CyNetwork net)
			   {
				   this.net = net;
			   }
			   
		        public int compare(Object arg0, Object arg1) {
		            Node n1 = (Node) arg0;
		            Node n2 = (Node) arg1;
		           
		            int numEdges1 = net.getAdjacentEdgesList(n1, true, true, true).size();
		            int numEdges2 = net.getAdjacentEdgesList(n2, true, true, true).size();
		            
		            if (numEdges1 < numEdges2)
		                return 1;
		            if (numEdges1 == numEdges2)
		                return 0;
		            else
		                return -1;
		        }
		    }

		    private class NeighbourSorter implements Comparator {

		        public int compare(Object arg0, Object arg1) {
		            if (arg1 == null)
		                return -1;
		            if (arg0 == null)
		                return 1;
		            EdgeCluster edgeCluster = (EdgeCluster) arg0;
		            float theta1 = edgeCluster.getClusterAngle();

		            edgeCluster = (EdgeCluster) arg1;
		            float theta2 = edgeCluster.getClusterAngle();

		            if (theta1 < theta2) {
		                return -1;
		            } else
		                return 1;
		        }
		    }
			
			
			
	}	

}
