
package org.cytoscape.work.tunable.impl.cl;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.cytoscape.work.tunable.Handler;

public interface CLHandler extends Handler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
