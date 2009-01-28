package org.cytoscape.work;

public interface ValuedTask<V>
{
	public V run(TaskMonitor taskMonitor) throws Exception;

	public void cancel();
}
