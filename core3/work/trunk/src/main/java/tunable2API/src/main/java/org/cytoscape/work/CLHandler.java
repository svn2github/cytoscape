package org.cytoscape.work;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;


public interface CLHandler extends Handler {
	public Option getOption();
	public void handleLine( CommandLine line );
}
