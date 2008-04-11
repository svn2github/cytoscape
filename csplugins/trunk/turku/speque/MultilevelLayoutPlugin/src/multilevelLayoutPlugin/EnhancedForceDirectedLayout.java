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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import metricTree.MetricNode;
import metricTree.MetricNodeTree;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import giny.model.Edge;
import giny.model.Node;

/**
 * Class used to calculate a nice layout for a graph using 
 * a heavily tuned force-directed placement algorithm by
 * C. Walshaw (2003). 
 * 
 * @author Pekka Salmela
 *
 */
public class EnhancedForceDirectedLayout {
	
	/**
	 * Natural Spring Length for this level.
	 */
	private double k;
	
	/**
	 * Grid size used to find out the local neighborhood
	 * 
	 * of a node.
	 */
	private int R;
	
	/**
	 * The graph the algorithm is applied to.
	 */
	private CyNetwork network;
	
	/**
	 * Position manager used to store node locations.
	 */
	private NodePositionManager posManager;
	
	/**
	 * Node attributes.
	 */
	private CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
	
	/**
	 * Points used during calculation.
	 */
	private Point2D.Double DispVector, DiffVector, vOldPosition ;
	
	//just some variables used in performance testing
	//private long start=0L, gridTime=0L, attTime=0L, repTime=0L;
	
	/**
	 * Class constructor.
	 * @param previousNaturalSpringLength Ideal length of the edges on the 
	 * previous graph level. 
	 * @param level The graph level the layout calculation is done for. 
	 * @param network The graph view that the layout is done for.
	 * @param posManager The position manager used to store node positions.   
	 */
	public EnhancedForceDirectedLayout(double previousNaturalSpringLength, double level, CyNetwork network, NodePositionManager posManager){
		this.k = (Math.sqrt(4.0/7.0))*previousNaturalSpringLength;
		this.R = (int)(2.0*(level + 1.0)*k);
		this.network = network;
		this.posManager = posManager;
		this.CTimesKSquared = MultilevelConfig.C*k*k;
	}
	
	/**
	 * Calculates a nice layout for the graph given in the constructor
	 * using an tuned version of force-directed placement layout.
	 */
	@SuppressWarnings("unchecked")
	public void doLayout() {
		boolean converged= false;
		double t = k;
		
		MetricNodeTree metricNodeTree;
		MetricNode.posman = posManager;
		
		HashSet<Node> neighboringNodesOfV = new HashSet<Node>();
		HashSet<Edge> edgesConnectedToV = new HashSet<Edge>();
		ArrayList<MetricNode> closeNodes = new ArrayList<MetricNode>();	
		
		ClusteringCoefficientManager ccm = null;
		if(MultilevelConfig.clusteringEnabled){
			ccm = new ClusteringCoefficientManager(network);
			ccm.setAllCCs();
		}
		
		//Repeat as long as there is enough movement
		while(!converged){
			converged = true;
			
			Iterator<Node> iterator = network.nodesIterator();
			
			metricNodeTree = new MetricNodeTree();
			
			HashSet<MetricNode> mNodeSet = new HashSet<MetricNode>();
			while(iterator.hasNext()){
				MetricNode mn = new MetricNode(iterator.next());
				metricNodeTree.insert(mn);
				mNodeSet.add(mn);
			}
			
			//iterate over all metric nodes
			Iterator<MetricNode> mIterator = mNodeSet.iterator();
			
			while(mIterator.hasNext()) {
				MetricNode mnode = mIterator.next();
				
				//FIND NODE IN A RANGE OF R
				closeNodes = metricNodeTree.getRange(mnode, R);
				
				Node v = mnode.getValue();
				
				//FIND CONNECTED NODES
				edgesConnectedToV.clear();
				int[] edgeIndices = network.getAdjacentEdgeIndicesArray(v.getRootGraphIndex(), true, true, true);
				for(int i = 0; i < edgeIndices.length; i++){
					edgesConnectedToV.add(network.getEdge(edgeIndices[i]));
				}
				neighboringNodesOfV.clear();
				for(Edge e : edgesConnectedToV){
					if(!e.getSource().equals(v)) neighboringNodesOfV.add(e.getSource());
					if(!e.getTarget().equals(v)) neighboringNodesOfV.add(e.getTarget());
				}
				
				//start = System.currentTimeMillis();
				
				//CALCULATE REPULSIVE FORCES
				
				DispVector = new Point2D.Double(0, 0);
				double diffLength, uWeight, fRep, newX, newY;
				//iterate over found nodes
				for (MetricNode temp : closeNodes){
					Node u = temp.getValue();
					if (u != v){
						DiffVector = getDifferenceVector(u, v, posManager);
						diffLength = getVectorLength(DiffVector);
						//is node inside radius R?
						if (getVectorLength(DiffVector)<=R) {
							if (diffLength == 0.0){
								posManager.setPosition(
										u.getRootGraphIndex(), 
										posManager.getX(u.getRootGraphIndex()) + plusOrMinusOne()*0.001*k,
										posManager.getY(u.getRootGraphIndex()) + plusOrMinusOne()*0.001*k);
								DiffVector = getDifferenceVector(u, v, posManager);
								diffLength = getVectorLength(DiffVector);
							}
							
							uWeight = 1.0;
							if (nodeAttributes.getIntegerAttribute(u.getIdentifier(), "ml_weight") != null){
								uWeight = nodeAttributes.getIntegerAttribute(u.getIdentifier(), "ml_weight");
							}
							//calculate actual repulsive force
							fRep = forceRepulsive(diffLength, uWeight);
							
							if(MultilevelConfig.clusteringEnabled){
								if(neighboringNodesOfV.contains(u)){
									if(ccm.getCC(u.getRootGraphIndex()) >= MultilevelConfig.minimumCC){
										fRep = fRep / (1.0 + ccm.getCC(u.getRootGraphIndex()) * MultilevelConfig.ccPower);
									}
								}
							}
							
							newX = DispVector.getX() + (DiffVector.getX() / diffLength)	* fRep;
							newY = DispVector.getY() + (DiffVector.getY() / diffLength)	* fRep;
							DispVector.setLocation(newX, newY);
						}						
					}						
				 }
				
				//repTime += System.currentTimeMillis() -start;
				
				//start = System.currentTimeMillis();
				
				//CALCULATE ATTRACTIVE FORCES BETWEEN CONNECTED NODES	

				double fAttr;
				//iterate over nodes connected to v
				for(Node n : neighboringNodesOfV){
					DiffVector = getDifferenceVector(n, v, posManager);
					diffLength = getVectorLength(DiffVector);
					if (diffLength == 0.0) {
						posManager.setPosition(
								n.getRootGraphIndex(), 
								posManager.getX(n.getRootGraphIndex()) + plusOrMinusOne()*0.001*k,
								posManager.getY(n.getRootGraphIndex()) + plusOrMinusOne()*0.001*k);
						DiffVector = getDifferenceVector(n, v, posManager);
						diffLength = getVectorLength(DiffVector);
					}
					//calculate actual attractive force
					fAttr = forceAttractive(diffLength);
					
					if(MultilevelConfig.clusteringEnabled){
						if(ccm.getCC(n.getRootGraphIndex()) >= MultilevelConfig.minimumCC){
							fAttr = fAttr * (1.0 + ccm.getCC(n.getRootGraphIndex()) * MultilevelConfig.ccPower);
						}
					}
									
					newX = (DispVector.getX() + (DiffVector.getX() / diffLength) * fAttr);
					newY = (DispVector.getY() + (DiffVector.getY() / diffLength)	* fAttr);
					DispVector.setLocation(newX, newY);
				}
				
				//attTime += System.currentTimeMillis() -start;
				 
				//REPOSITION v
				//first, save old position
				vOldPosition = new Point2D.Double(posManager.getX(v.getRootGraphIndex()), posManager.getY(v.getRootGraphIndex()));
				double dispLength = getVectorLength(DispVector);
				if (dispLength != 0.0) {
					posManager.setX(v.getRootGraphIndex(), 
							posManager.getX(v.getRootGraphIndex())
							+ (DispVector.getX() / dispLength)
							* Math.min(t, dispLength));
					posManager.setY(v.getRootGraphIndex(), 
							posManager.getY(v.getRootGraphIndex())
							+ (DispVector.getY() / dispLength)
							* Math.min(t, dispLength));
				}				
				//calculate the amount of movement 
				DiffVector = new Point2D.Double((vOldPosition.getX() - posManager.getX(v.getRootGraphIndex())), 
						(vOldPosition.getY() - posManager.getY(v.getRootGraphIndex())));
				//if all movement has dropped below treshold => converged 
				if(getVectorLength(DiffVector) > this.k*MultilevelConfig.tolerance) converged = false;
			}
			
			//reduce the temperature to limit the maximum movement
			t = cool(t);
		}
		
		//System.out.println("grid: " + gridTime + " rep: " + repTime + " attr: " + attTime);
	}
	
	/**
	 * Calculates the difference vector of vectors u and v, namely vector u-v.
	 * @param u First vector.
	 * @param v Second vector.
	 * @return Vector u-v.
	 */
	private static Point2D.Double getDifferenceVector(Node u, Node v, NodePositionManager pm){
		return new Point2D.Double(
				(pm.getX(u.getRootGraphIndex()) - pm.getX(v.getRootGraphIndex())),
				(pm.getY(u.getRootGraphIndex()) - pm.getY(v.getRootGraphIndex()))
				);
	}
	
	/**
	 * Calculates the distance from origo to point p; that is, the distance of
	 * vector (p.x, p.y).
	 * @param p
	 * @return
	 */
	private static double getVectorLength(Point2D.Double p){
		return p.distance(0.0, 0.0);
	}
	
	/**
	 * Calculates the next step of the cooling schedule.
	 * @param previous
	 * @return The next step of the cooling schedule.
	 */
	private double cool(double previous){
		return MultilevelConfig.T*previous;
	}
	
	private double CTimesKSquared;
	
	/**
	 * Calculates the amount of repulsive force between two nodes. 
	 * @param x Distance between the two nodes.
	 * @param w Weight of the node the new position is calculated for.
	 * @return Amount of repulsive force. 
	 */
	private double forceRepulsive(double x, double w) {
		if(Double.isNaN(x)) throw new IllegalStateException("Parameter x was NaN");
		if(x == 0.0) throw new IllegalStateException("Parameter x was 0");
		return -1.0*(w*CTimesKSquared/x);
	}
	
	/**
	 * Calculates the amount of attractive force between two nodes.
	 * @param x Distance between the two nodes.
	 * @return Amount of attractive force.
	 */
	private double forceAttractive(double x) throws IllegalStateException {
		if(Double.isNaN(x)) throw new IllegalStateException("Parameter x was NaN");
		return MultilevelConfig.A*(x*x)/k;
	}
	
	/**
	 * Returns the ideal edge length used in calculation.
	 * @return Ideal edge length.
	 */
	public double getK() {
		return k;
	}
	
	/**
	 * Returns 1 or -1 with equal probability.
	 * @return 1 or -1, toss a coin to guess which one.
	 */
	private double plusOrMinusOne(){
		if(Math.random()<0.5) return 1.0;
		else return -1.0;
	}
}
	