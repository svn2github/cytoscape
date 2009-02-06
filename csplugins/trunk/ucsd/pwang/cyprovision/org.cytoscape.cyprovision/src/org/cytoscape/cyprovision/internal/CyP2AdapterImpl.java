package org.cytoscape.cyprovision.internal;

import java.util.Properties;
import javax.swing.JFrame;
import org.cytoscape.cyprovision.CyP2Adapter;

public class CyP2AdapterImpl implements CyP2Adapter{
	private JFrame cyDesktop;
	private Properties props;

	public JFrame getCyDesktop(){
		if (cyDesktop == null) {
			cyDesktop = new JFrame("Cytoscape Desktop frame"); //Cytoscape.getDesktop();
			cyDesktop.setSize(600, 300);
			cyDesktop.setVisible(true);
		}
		return cyDesktop;
	
	}
	
	// We should get the properties from Cytoscape preferences
	// For now we provide some test values 
	public Properties getCyProperties(){
		if (props == null) {
			props = new Properties();
			//props.setProperty("key", "value");
			props.setProperty(CyP2Adapter.p2_AUTO_UPDATE_ENABLED, "true");
		}
		return props; //Cytoscape.getPreferences();
	}
}
