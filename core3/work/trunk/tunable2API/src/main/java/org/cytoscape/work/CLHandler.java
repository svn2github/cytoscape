package org.cytoscape.work;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.cytoscape.work.Handler;

public interface CLHandler extends Handler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
