//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
/**
 * This class implements two menu items that allow enabling and disabling
 * the visual mapper attached the the CyWindow argument.
 */
public class ToggleVisualMapperAction extends AbstractAction {
    
    CyWindow cyWindow;
    boolean enable;
    
    public ToggleVisualMapperAction(CyWindow cyWindow, boolean enable) {
        super(getLabel(enable));
        this.cyWindow = cyWindow;
        this.enable = enable;
    }
    
    private static String getLabel(boolean enable) {
        return (enable == true) ? "Enable Visual Mapper" : "Disable Visual Mapper";
    }
    
    public void actionPerformed(ActionEvent e) {
        cyWindow.setVisualMapperEnabled(enable);
    }
}

