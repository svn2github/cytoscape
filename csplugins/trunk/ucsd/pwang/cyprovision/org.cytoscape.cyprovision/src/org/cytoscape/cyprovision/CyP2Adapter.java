package org.cytoscape.cyprovision;

import java.util.Properties;

import javax.swing.JFrame;

public interface CyP2Adapter {

	public JFrame getCyDesktop();
	
	public Properties getCyProperties();
	
	public static String PROVISION_SERVICE_NAME = "org.cytoscape.cyprovision.CyP2Adapter";
}
