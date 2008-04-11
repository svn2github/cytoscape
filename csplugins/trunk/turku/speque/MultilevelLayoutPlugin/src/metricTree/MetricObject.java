package metricTree;

public interface MetricObject{
	
	public Object getValue();
	
	public Float getDistance();
	
	public MetricObject getNearest();
	
	public float distance(MetricObject object);
	
	public void setDistance(float distance);
	
	public void setNearest(MetricObject nearest);
	
}
