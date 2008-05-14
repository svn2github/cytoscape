package mTree;

import multilevelLayoutPlugin.NodePositionManager;
import giny.model.Node;

public class MTreeNode {
	
	public static NodePositionManager posman;
	
	private double parentDistance = 0;
	
	private double coveringRadius = 0;
	
	private MTree subtree;
	
	private MTree container;
	
	private Node graphNode;
	
	public MTreeNode(Node n){
		graphNode = n;
	}
	
	public double getCoveringRadius(){
		return coveringRadius;
	}
	
	public double getParentDistance(){
		return parentDistance;
	}
	
	public MTree getSubtree(){
		return subtree;
	}
	
	public Node getGraphNode(){
		return graphNode;
	}
	
	public double distance(MTreeNode n){
		double x1 = posman.getX(graphNode.getRootGraphIndex()); 
		double x2 = posman.getX(n.getGraphNode().getRootGraphIndex());
		double y1 = posman.getY(graphNode.getRootGraphIndex());
		double y2 = posman.getY(n.getGraphNode().getRootGraphIndex());
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	public MTree getContainer(){
		return container;
	}
	
	public void setCoveringRadius(double r){
		coveringRadius = r;
	}
	
	public void setParentDistance(double d){
		parentDistance = d;
	}
	
	public void setContainer(MTree m){
		container = m;
	}
	
	public void setSubtree(MTree m){
		subtree = m;
	}
}
