//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.hierarchic.HierarchicLayouter;

import cytoscape.CytoscapeWindow;
import cytoscape.layout.HierarchicalLayoutDialog;
//-------------------------------------------------------------------------
public class HierarchicalLayoutAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    HierarchicalLayoutDialog hDialog;
    
    public HierarchicalLayoutAction(CytoscapeWindow cytoscapeWindow) {
        super("Hierarchical");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (hDialog == null)
            hDialog = new HierarchicalLayoutDialog(cytoscapeWindow.getMainFrame());
        hDialog.pack();
        hDialog.setLocationRelativeTo(cytoscapeWindow.getMainFrame());
        hDialog.setVisible(true);
        /* This looks suspicious. Won't the dialog run in a different thread,
         * and thus the following line end up setting a default layouter?
         * I think an explicit block needs to go here to wait until the
         * dialog is closed to set the layouter, but we'll worry about
         * that later. -AM 2003/06/11
         */
        cytoscapeWindow.setLayouter( hDialog.getLayouter() );
        /* The following line wasn't commented out before; this is bizarre
         * since the effect is to discard the layouter that was constructed
         * through the dialog above. I'm assuming this is a bug. -AM 2003/06/11
         */
      //layouter = new HierarchicLayouter ();
    }
}

