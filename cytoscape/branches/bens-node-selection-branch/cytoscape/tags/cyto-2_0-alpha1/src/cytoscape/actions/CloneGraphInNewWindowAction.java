//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.model.GraphPerspective;

import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
public class CloneGraphInNewWindowAction extends AbstractAction {
    CyWindow cyWindow;

    public CloneGraphInNewWindowAction(CyWindow cyWindow) {
        super("Whole graph");
        this.cyWindow = cyWindow;
    }

     public void actionPerformed(ActionEvent e) {
        //save the vizmapper catalog
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        CyNetwork oldNetwork = cyWindow.getNetwork();
        String callerID = "CloneGraphInNewWindowAction.actionPerformed";
        oldNetwork.beginActivity(callerID);
      	GraphPerspective subGraph =
                (GraphPerspective)oldNetwork.getGraphPerspective().clone();

        CyNetwork newNetwork = new CyNetwork(subGraph,
                                             oldNetwork.getNodeAttributes(),
                                             oldNetwork.getEdgeAttributes(),
                                             oldNetwork.getExpressionData() );
        newNetwork.setNeedsLayout(false);
        oldNetwork.endActivity(callerID);

        String title = " cloned whole graph";
        try {
            //this call creates a WindowOpened event, which is caught by
            //cytoscape.java, enabling that class to manage the set of windows
            //and quit when the last window is closed
            CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),
                                              newNetwork, title);
            newWindow.showWindow();
        } catch (Exception e00) {
            System.err.println("exception when creating new window");
            e00.printStackTrace();
        }
        cyWindow.getView().addGraphViewChangeListener(cyWindow.getCyMenus());
        cyWindow.getCyMenus().setNodesRequiredItemsEnabled();
    }//end performinginyMode()

}

