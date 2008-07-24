
package org.cytoscape.work.tunable.impl.gui;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.cytoscape.work.tunable.*;

public interface GuiHandler extends Handler {
	public JPanel getJPanel();
	public void handle();
}
