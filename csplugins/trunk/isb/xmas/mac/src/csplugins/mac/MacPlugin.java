package csplugins.mac;

import com.apple.eawt.*;
import com.apple.eio.*;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.actions.HelpAboutAction;
import cytoscape.plugin.CytoscapePlugin;

import java.awt.event.ActionEvent;

public class MacPlugin extends CytoscapePlugin implements ApplicationListener {

    Application app;

    public MacPlugin() {

        this.app = Application.getApplication();
        app.addApplicationListener(this);

    }

    public void handleAbout(ApplicationEvent event) {
        new HelpAboutAction().actionPerformed(new ActionEvent(this, 0, "About"));
        event.setHandled(true);
    }

    public void handleOpenApplication(ApplicationEvent event) {
    }

    public void handleOpenFile(ApplicationEvent event) {
        String file = event.getFilename();

        CyNetwork newNetwork = Cytoscape.createNetworkFromFile(file);

        if (newNetwork.getNodeCount() < CytoscapeInit.getViewThreshold()) {
            Cytoscape.createNetworkView(newNetwork);
        }
        event.setHandled(true);
    }

    public void handlePreferences(ApplicationEvent event) {
    }

    public void handlePrintFile(ApplicationEvent event) {
    }

    public void handleQuit(ApplicationEvent event) {
        Cytoscape.exit();
        event.setHandled(true);
    }

    public void handleReOpenApplication(ApplicationEvent event) {
    }
}
