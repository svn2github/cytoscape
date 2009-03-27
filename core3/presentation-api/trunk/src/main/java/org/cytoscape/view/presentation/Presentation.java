
package org.cytoscape.view.presentation;

import java.awt.print.Printable;
import java.util.Properties;

public interface Presentation {

	Printable getPrintable();
	void setProperties(Properties props);
	Properties getProperties();
}

