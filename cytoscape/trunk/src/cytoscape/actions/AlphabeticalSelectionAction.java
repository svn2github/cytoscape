//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class AlphabeticalSelectionAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public AlphabeticalSelectionAction(CytoscapeWindow cytoscapeWindow) {
        super("By Name...");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        String answer = 
        (String) JOptionPane.showInputDialog(cytoscapeWindow.getMainFrame(), 
        "Select nodes whose name (or synonym) starts with");
        if (answer != null && answer.length() > 0) {
            cytoscapeWindow.selectNodesStartingWith(answer.trim());
        }
    }
}

