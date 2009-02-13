
package org.cytoscape.work.internal.cl;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.apache.commons.cli.*;
import org.cytoscape.work.Handler;

public interface CLHandler extends Handler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
