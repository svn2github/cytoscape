package metricTree;

/**
 * Metrinen objekti.
 */
public class MetricInteger implements MetricObject, Comparable {
	
	/***************************************
	 * JÄSENMUUTTUJAT.
	 ***************************************/
	
	/**
	 * Arvo.
	 */
	private int value;
	
	private MetricObject nearest;
	
	private Float distance;
	
	/***************************************
	 * KONSTRUKTORIT.
	 ***************************************/
	
	/**
	 * Luo objektin.
	 */
	public MetricInteger(int value) {
		this.value = value;
	}
	
	/***************************************
	 * FUNKTIOT.
	 ***************************************/
	 
	public Integer getValue() {
		return value;
	}
	 
	public float distance(MetricObject object) {
		return Math.abs(getValue() - ((MetricInteger)object).getValue());
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
	
	public MetricObject getNearest() {
		return nearest;
	}
	
	public Float getDistance() {
		return distance;
	}
	
	public int compareTo(Object object) {
		// if (object == null) return -1;
		return getDistance().compareTo(((MetricObject)object).getDistance());
	}
	
	/***************************************
	 * PROSEDUURIT.
	 ***************************************/
	
	public void setNearest(MetricObject nearest){
		this.nearest = nearest;
	}
	
	public void setDistance(float distance){
		this.distance = new Float(distance);
	}
	
}