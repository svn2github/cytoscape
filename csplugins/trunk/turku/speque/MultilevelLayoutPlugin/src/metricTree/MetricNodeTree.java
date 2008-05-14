package metricTree;

import java.util.*;

/**
 * Metrinen puu.
 */
public class MetricNodeTree {
	
	/***************************************
	 * JƒSENMUUTTUJAT.
	 ***************************************/
	 
	/**
	 * Puun juurisolmun sis‰lt‰m‰ objekti.
	 */
	private MetricNode object;
	
	/**
	 * Puun sis‰lt‰mien objektien maksimiet‰isyys juuren objektista (peittos‰de).
	 */
	private double radius;
	
	/**
	 * Juurisolmun vasen alipuu.
	 */
	private MetricNodeTree left;
	
	/**
	 * Juurisolmun oikea alipuu.
	 */
	private MetricNodeTree right;	 
	
	/***************************************
	 * KONSTRUKTORIT.
	 ***************************************/
	 
	/**
	 * Luo tyhj‰n puun.
	 */
	public MetricNodeTree() {
		
	}
	
	/**
	 * Luo puun, joka sis‰lt‰‰ objektin 'object'.
	 */
	public MetricNodeTree(MetricNode object) {
		this.object = object;
	}
	
	/***************************************
	 * FUNKTIOT.
	 ***************************************/
	 
	/**
	 * Palauttaa puun juuren sis‰lt‰m‰n objektin.
	 */
	public MetricNode getObject() {
		return object;
	}
	
	/**
	 * Palauttaa puun peittos‰teen.
	 */
	public double getRadius() {
		return radius;
	}
	 
	/**
	 * Onko puu tyhj‰?
	 */
	public boolean isEmpty() {
		return object == null;
	}
	
	/**
	 * Onko puu lehti, eli onko puu yksialkioinen?
	 */
	public boolean isLeaf() {
		return left == null;
	}
	
	/**
	 * Palauttaa objektin 'object' 'n' l‰hint‰ naapuria.
	 */
	public ArrayList<MetricNode> getNearest(MetricNode object, int n) {
		ArrayList<MetricNode> result = new ArrayList<MetricNode>();
		if (!isEmpty()) {
			getNearest(result, object, getObject().distance(object), n);
		}
		return result;
	}
	
	/**
	 * Palauttaa objektista 'object' korkeintaan et‰isyydell‰ 'distance' sijaitsevat alkiot.
	 */
	public ArrayList<MetricNode> getRange(MetricNode object, double distance) {
		ArrayList<MetricNode> result = new ArrayList<MetricNode>();
		if (!isEmpty()) {
			getRange(result, object, distance, getObject().distance(object));
		}
		return result;
	}
	
	/**
	 * Palauttaa kaikki puun objektit.
	 */
	public ArrayList<MetricNode> getObjects() {
		ArrayList<MetricNode> result = new ArrayList<MetricNode>();
		getObjects(result);
		return result;
	}
	
	/**
	 * Palauttaa vasemman alipuun.
	 */
	public MetricNodeTree getLeft() {
		return left;
	}
	
	
	/**
	 * Palauttaa oikean alipuun.
	 */
	public MetricNodeTree getRight() {
		return right;
	}
	
	/**
	 * Palauttaa puun merkkijonoesityksen.
	 */
	@Override
	public String toString() {
		return object + "#" + radius + "(" + left + "," + right + ")";
	}
	
	/***************************************
	 * PROSEDUURIT.
	 ***************************************/
	 
	/**
	 * Lis‰‰ puuhun alkion 'object'.
	 */
	public void insert(MetricNode object) {
		if (isEmpty()) {
			this.object = object;
		}
		else {
			double leftDistance = getObject().distance(object);
			insert(object, leftDistance);
		}
	}
	
	/**
	 * Asettaa puun peittos‰teen.
	 */
	public void setRadius(double radius) {
		if (radius > this.radius) {
			this.radius = radius;
		}
	}
	
	/***************************************
	 * APUMETODIT
	 ***************************************/
	
	/**
	 * Lis‰‰ objektin 'object' 'n' l‰hint‰ naapuria listaan 'result'.
	 */
	private void getNearest(ArrayList<MetricNode> result, MetricNode object, double leftDistance, int n) {
		if (isLeaf()) {
			result.add(getObject());
			Collections.sort(result);
			if (result.size() > n) {
				result.remove(result.size() - 1);
			}
			return;
		}
		else {
			double range = Double.MAX_VALUE;
			if (result.size() == n) {
				range = result.get(result.size() - 1).getDistance();
			}
			// System.out.println("Peitto: " + radius);
			left.getObject().setDistance(leftDistance);
			double rightDistance = object.distance(right.getObject());
			right.getObject().setDistance(rightDistance);
			if (left.getRadius() + range >= leftDistance) {
				left.getNearest(result, object, leftDistance, n);
			}	
			if (right.getRadius() + range >= rightDistance) {
				right.getNearest(result, object, rightDistance, n);
			}
		}
	}
	
//	/**
//	 * Lis‰‰ objektista 'object' korkeintaan et‰isyydell‰ 'range' sijaitsevat alkiot listaan 'result'.
//	 */
//	private void getRange(ArrayList<MetricNode> result, MetricNode object, double range, double leftDistance) {
//		if (isLeaf()) {
//			result.add(getObject());
//			result.get(result.size() - 1);
//			return;
//		}
//		else {
//			double rightDistance = object.distance(right.getObject());
//			if (left.getRadius() + range >= leftDistance) {
//				left.getRange(result, object, range, leftDistance);
//			}	
//			if (right.getRadius() + range >= rightDistance) {
//				right.getRange(result, object, range, rightDistance);
//			}
//		}
//	}
	
	/**
	 * Lis‰‰ objektista 'object' korkeintaan et‰isyydell‰ 'range' sijaitsevat alkiot listaan 'result'.
	 */
	private void getRange(ArrayList<MetricNode> result, MetricNode object, double range, double parentDistance) {
		if (isLeaf()) {
			result.add(getObject());
			result.get(result.size() - 1);
			return;
		}
		else {
			if(Math.abs(parentDistance - left.getObject().getDistance()) <= range + radius){
			double leftDistance = object.distance(left.getObject());
			if (leftDistance <= range + radius) {
				left.getRange(result, object, range, leftDistance);
			}
			}
			if(Math.abs(parentDistance - right.getObject().getDistance()) <= range + radius){
			double rightDistance = object.distance(right.getObject());
			if (rightDistance <= range + radius) {
				right.getRange(result, object, range, rightDistance);
			}
			}
		}
	}
	
	/**
	 * Lis‰‰ puuhun alkion 'object'.
	 */
	private void insert(MetricNode object, double leftDistance) {
		setRadius(leftDistance);
		if (isLeaf()) {
			left = new MetricNodeTree(getObject());
			right = new MetricNodeTree(object);
		}
		else {
			double rightDistance = right.getObject().distance(object);
			if (leftDistance < rightDistance) {
				left.insert(object, leftDistance);
			}
			else {
				right.insert(object, rightDistance);
			}
		}
	}
	
	/**
	 * Palauttaa kaikki puun objektit.
	 */
	private void getObjects(ArrayList<MetricNode> result) {
		if (isLeaf()) {
			result.add(getObject());
			return;
		}
		left.getObjects(result);
		right.getObjects(result);
	}
	
}