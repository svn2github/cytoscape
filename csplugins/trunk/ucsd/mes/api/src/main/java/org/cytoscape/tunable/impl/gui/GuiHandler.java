
package org.cytoscape.tunable.impl.gui;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.cytoscape.tunable.*;

public interface GuiHandler extends Handler {
	public JPanel getJPanel();
	public void handle();
}
