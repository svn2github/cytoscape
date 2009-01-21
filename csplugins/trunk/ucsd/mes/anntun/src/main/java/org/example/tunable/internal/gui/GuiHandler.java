
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.example.tunable.*;

public interface GuiHandler extends Handler {
	public JPanel getJPanel();
	public void handle();
}
