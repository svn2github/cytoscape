package metricTree;

import java.util.*;

/**
 * Metrinen puu.
 */
public class MetricIntegerTree {
	
	/***************************************
	 * JƒSENMUUTTUJAT.
	 ***************************************/
	 
	/**
	 * Puun juurisolmun sis‰lt‰m‰ objekti.
	 */
	private MetricInteger object;
	
	/**
	 * Puun sis‰lt‰mien objektien maksimiet‰isyys juuren objektista (peittos‰de).
	 */
	private float radius;
	
	/**
	 * Juurisolmun vasen alipuu.
	 */
	private MetricIntegerTree left;
	
	/**
	 * Juurisolmun oikea alipuu.
	 */
	private MetricIntegerTree right;	 
	
	/***************************************
	 * KONSTRUKTORIT.
	 ***************************************/
	 
	/**
	 * Luo tyhj‰n puun.
	 */
	public MetricIntegerTree() {
		
	}
	
	/**
	 * Luo puun, joka sis‰lt‰‰ objektin 'object'.
	 */
	public MetricIntegerTree(MetricInteger object) {
		this.object = object;
	}
	
	/***************************************
	 * FUNKTIOT.
	 ***************************************/
	 
	/**
	 * Palauttaa puun juuren sis‰lt‰m‰n objektin.
	 */
	public MetricInteger getObject() {
		return object;
	}
	
	/**
	 * Palauttaa puun peittos‰teen.
	 */
	public float getRadius() {
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
	public ArrayList<MetricInteger> getNearest(MetricInteger object, int n) {
		ArrayList<MetricInteger> result = new ArrayList<MetricInteger>();
		if (!isEmpty()) {
			getNearest(result, object, getObject().distance(object), n);
		}
		return result;
	}
	
	/**
	 * Palauttaa objektista 'object' korkeintaan et‰isyydell‰ 'distance' sijaitsevat alkiot.
	 */
	public ArrayList<MetricInteger> getRange(MetricInteger object, float distance) {
		ArrayList<MetricInteger> result = new ArrayList<MetricInteger>();
		if (!isEmpty()) {
			getRange(result, object, distance, getObject().distance(object));
		}
		return result;
	}
	
	/**
	 * Palauttaa kaikki puun objektit.
	 */
	public ArrayList<MetricInteger> getObjects() {
		ArrayList<MetricInteger> result = new ArrayList<MetricInteger>();
		getObjects(result);
		return result;
	}
	
	/**
	 * Palauttaa kaikki puun objektit.
	 */
	public MetricIntegerTree getLeft() {
		return left;
	}
	
	
	/**
	 * Palauttaa kaikki puun objektit.
	 */
	public MetricIntegerTree getRight() {
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
	public void insert(MetricInteger object) {
		if (isEmpty()) {
			this.object = object;
		}
		else {
			float leftDistance = getObject().distance(object);
			insert(object, leftDistance);
		}
	}
	
	/**
	 * Asettaa puun peittos‰teen.
	 */
	public void setRadius(float radius) {
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
	private void getNearest(ArrayList<MetricInteger> result, MetricInteger object, float leftDistance, int n) {
		if (isLeaf()) {
			result.add(getObject());
			Collections.sort(result);
			if (result.size() > n) {
				result.remove(result.size() - 1);
			}
			return;
		}
		else {
			float range = Float.MAX_VALUE;
			if (result.size() == n) {
				range = result.get(result.size() - 1).getDistance();
			}
			// System.out.println("Peitto: " + radius);
			left.getObject().setDistance(leftDistance);
			float rightDistance = object.distance(right.getObject());
			right.getObject().setDistance(rightDistance);
			if (left.getRadius() + range >= leftDistance) {
				left.getNearest(result, object, leftDistance, n);
			}	
			if (right.getRadius() + range >= rightDistance) {
				right.getNearest(result, object, rightDistance, n);
			}
		}
	}
	
	/**
	 * Lis‰‰ objektista 'object' korkeintaan et‰isyydell‰ 'range' sijaitsevat alkiot listaan 'result'.
	 */
	private void getRange(ArrayList<MetricInteger> result, MetricInteger object, float range, float leftDistance) {
		if (isLeaf()) {
			result.add(getObject());
			result.get(result.size() - 1);
			return;
		}
		else {
			float rightDistance = object.distance(right.getObject());
			if (left.getRadius() + range >= leftDistance) {
				left.getRange(result, object, range, leftDistance);
			}	
			if (right.getRadius() + range >= rightDistance) {
				right.getRange(result, object, range, rightDistance);
			}
		}
	}
	
	/**
	 * Lis‰‰ puuhun alkion 'object'.
	 */
	private void insert(MetricInteger object, float leftDistance) {
		setRadius(leftDistance);
		if (isLeaf()) {
			left = new MetricIntegerTree(getObject());
			right = new MetricIntegerTree(object);
		}
		else {
			float rightDistance = right.getObject().distance(object);
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
	private void getObjects(ArrayList<MetricInteger> result) {
		if (isLeaf()) {
			result.add(getObject());
			return;
		}
		left.getObjects(result);
		right.getObjects(result);
	}
	
}