package org.cytoscape.work;

public interface TaskMonitor
{
	public void	setTitle(String title);
	public boolean	needsToCancel();
	public void	setProgress(double progress);
	public void	setStatusMessage(String statusMessage);
	public void	setException(Throwable exception);
}
