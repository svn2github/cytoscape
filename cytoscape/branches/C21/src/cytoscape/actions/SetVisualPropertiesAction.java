//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.actions;
//------------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.CyWindow;
//------------------------------------------------------------------------------
public class SetVisualPropertiesAction extends AbstractAction   {
    CyWindow cyWindow;
    /** The constructor that takes no arguments shows the
     *  label "Set Visual Properties" - this makes it appropriate
     *  for the pulldown menu system, and inappropriate for an icon. */
    public SetVisualPropertiesAction(CyWindow cyWindow) {
        super("Set Visual Properties");
        this.cyWindow = cyWindow;
    }
    /** The constructor that takes a boolean shows no label,
     *  no matter what the value of the boolean actually is.
     *  This makes is appropriate for an icon, but inappropriate
     *  for the pulldown menu system. */
    public SetVisualPropertiesAction(CyWindow cyWindow,
                                     boolean showLabel) {
	super();
        this.cyWindow = cyWindow;
    }
    public void actionPerformed (ActionEvent e) {
	cyWindow.getVizMapUI().refreshUI();
	cyWindow.getVizMapUI().getStyleSelector().show();
	//cyWindow.show();
    }
}

