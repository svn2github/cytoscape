//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.actions;
//------------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.CytoscapeWindow;
//------------------------------------------------------------------------------
public class SetVisualPropertiesAction extends AbstractAction   {
    CytoscapeWindow cytoscapeWindow;
    /** The constructor that takes no arguments shows the
     *  label "Set Visual Properties" - this makes it appropriate
     *  for the pulldown menu system, and inappropriate for an icon. */
    public SetVisualPropertiesAction(CytoscapeWindow cytoscapeWindow) {
        super("Set Visual Properties");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    /** The constructor that takes a boolean shows no label,
     *  no matter what the value of the boolean actually is.
     *  This makes is appropriate for an icon, but inappropriate
     *  for the pulldown menu system. */
    public SetVisualPropertiesAction(CytoscapeWindow cytoscapeWindow,
                                     boolean showLabel) {
	super();
        this.cytoscapeWindow = cytoscapeWindow;
    }
    public void actionPerformed (ActionEvent e) {
	cytoscapeWindow.getVizMapUI().refreshUI();
	cytoscapeWindow.getVizMapUI().getStyleSelector().show();
	//vizMapUI.show();
    }
}

