//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
/**
 * This class implements two menu items that allow enabling and disabling
 * the visual mapper attached the the CyWindow argument.
 */
public class ToggleVisualMapperAction extends AbstractAction {
    private CyWindow cyWindow;

    public ToggleVisualMapperAction(CyWindow cyWindow) {
        super("Disable Visual Mapper");
        this.cyWindow = cyWindow;
    }

    public void actionPerformed(ActionEvent e) {
        cyWindow.toggleVisualMapperEnabled();
    }
}

