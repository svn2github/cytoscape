package cytoscape.view;

import cytoscape.Cytoscape;
import javax.help.*;
import java.net.*;

/**
 * This class creates the Cytoscape Help Broker for managing the JavaHelp
 * system and help set access
 */
public class CyHelpBroker {

  HelpBroker hb;
  HelpSet hs;

  public CyHelpBroker() {

    hb = null;
    hs = null;
    URL hsURL = getClass().getResource("/cytoscape/help/Cytoscape.hs");

    ClassLoader cl = Cytoscape.class.getClassLoader();
    try {
	hs = new HelpSet(null, hsURL);
	hb = hs.createHelpBroker();
    } catch (Exception e) {
	System.out.println("HelpSet " + e.getMessage());
	System.out.println("HelpSet " + hs + " not found.");
    }
  }

  public HelpBroker getHelpBroker() { return hb; }
  public HelpSet getHelpSet() { return hs; }
  
}
