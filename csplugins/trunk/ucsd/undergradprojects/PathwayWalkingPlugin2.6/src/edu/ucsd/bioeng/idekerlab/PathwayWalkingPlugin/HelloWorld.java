package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class HelloWorld {

    public HelloWorld(CyAttributes cna) {
        String[] message = cna.getAttributeNames();
        for(int i=0; i<message.length; i++){
        	System.out.println(cna.getAttributeDescription(message[i]));
            // use the CytoscapeDesktop as parent for a Swing dialog
            JOptionPane.showMessageDialog( Cytoscape.getDesktop(), cna.getAttributeDescription(message[i]));
        }
    }
}