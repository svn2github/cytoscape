
package org.example.tunable.internal.gui;

import javax.swing.JPanel;
import org.example.tunable.Handler;
import java.util.Map;

public interface GuiHandler extends Handler {
	JPanel getJPanel();
	void handle();

	String getState();
	String getName();

	void notifyDependents();
	void addDependent(GuiHandler gh);

	void checkDependency(String name, String state);
	String getDependency();
}
