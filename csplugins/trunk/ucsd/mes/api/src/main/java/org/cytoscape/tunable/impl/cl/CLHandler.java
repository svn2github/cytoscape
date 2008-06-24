
package org.cytoscape.tunable.impl.cl;

import java.lang.reflect.*;
import javax.swing.JPanel;
import org.apache.commons.cli.*;
import org.cytoscape.tunable.*;

public interface CLHandler extends Handler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
