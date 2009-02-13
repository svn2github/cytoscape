package org.cytoscape.work.internal.gui;


import javax.swing.*;

import org.cytoscape.work.Handler;

public interface Guihandler extends Handler{
	public void handle();
//	public JPanel getInputPanel();
//	public Tunable getTunable();
//	public Field getField();
//	public Object getObject();
//	public JPanel getOutputPanel();
	
	
	void returnPanel();
	JPanel getJPanel();
	String getState();
	String getName();

	void notifyDependents();
	void addDependent(Guihandler gh);

	void checkDependency(String name, String state);
	String getDependency();
}
