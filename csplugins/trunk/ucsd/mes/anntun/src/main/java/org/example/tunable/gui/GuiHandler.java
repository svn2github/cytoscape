
package org.example.tunable.gui;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.example.tunable.*;

public interface GuiHandler {
	public JPanel getJPanel();
	public void handle();
}
