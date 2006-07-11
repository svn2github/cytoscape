package nct.visualization.cytoscape;

import cytoscape.task.TaskMonitor;

public interface Monitorable
{
  void setMonitor(TaskMonitor taskMonitor);
  TaskMonitor getMonitor();
}
