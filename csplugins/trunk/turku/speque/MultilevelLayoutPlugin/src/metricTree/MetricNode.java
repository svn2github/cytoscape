package metricTree;

import giny.model.Node;
import multilevelLayoutPlugin.NodePositionManager;

/**
 * Metric node.
 */
public class MetricNode implements Comparable {
	
	/***************************************
	 * JÄSENMUUTTUJAT.
	 ***************************************/
	
	/**
	 * Arvo.
	 */
	private Node value;
	
	public static NodePositionManager posman;
	
	private MetricNode nearest;
	
	private Double distance;
	
	/***************************************
	 * KONSTRUKTORIT.
	 ***************************************/
	
	/**
	 * Luo objektin.
	 */
	public MetricNode(Node value) {
		this.value = value;
	}
	
	/***************************************
	 * FUNKTIOT.
	 ***************************************/
	 
	public Node getValue() {
		return value;
	}
	 
	public double distance(MetricNode object) {
		double x1 = posman.getX(value.getRootGraphIndex()); 
		double x2 = posman.getX(object.getValue().getRootGraphIndex());
		double y1 = posman.getY(value.getRootGraphIndex());
		double y2 = posman.getY(object.getValue().getRootGraphIndex());
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
	
	public MetricNode getNearest() {
		return nearest;
	}
	
	public Double getDistance() {
		return distance;
	}
	
	public int compareTo(Object object) {
		// if (object == null) return -1;
		return getDistance().compareTo(((MetricNode)object).getDistance());
	}
	
	/***************************************
	 * PROSEDUURIT.
	 ***************************************/
	
	public void setNearest(MetricNode nearest){
		this.nearest = nearest;
	}
	
	public void setDistance(double distance){
		this.distance = new Double(distance);
	}
	
}