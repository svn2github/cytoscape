package csplugins.common.samplePlugins;

import javax.swing.JOptionPane;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

public class HelloWorldPlugin extends CytoscapePlugin {

    public HelloWorldPlugin() {
        String message = "Hello, world!";
        System.out.println(message);
        // use the CytoscapeDesktop as parent for a Swing dialog
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
    }
}

