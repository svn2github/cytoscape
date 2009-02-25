package org.cytoscape.work.internal.tunables;


import javax.swing.JPanel;

import org.cytoscape.work.Handler;

public interface Guihandler extends Handler{
	public void handle();
	JPanel getJPanel();
	String getState();
	String getName();

	void notifyDependents();
	void addDependent(Guihandler gh);

	void checkDependency(String name, String state);
	String getDependency();
}
