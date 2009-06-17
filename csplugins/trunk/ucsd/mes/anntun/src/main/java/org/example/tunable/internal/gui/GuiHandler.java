
package org.example.tunable.internal.gui;

import javax.swing.JPanel;

import org.example.tunable.Handler;

public interface GuiHandler extends Handler {
	JPanel getJPanel();
	void handle();

	String getState();
	String getName();

	void notifyDependents();

	String getDependency();
}
