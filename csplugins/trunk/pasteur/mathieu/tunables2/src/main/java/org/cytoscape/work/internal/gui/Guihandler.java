package org.cytoscape.work.internal.gui;


import javax.swing.*;

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
	
	void handleDependents();
	
	//added method to reset the value after handling to check the TunableValidator method
	void resetValue();
}
