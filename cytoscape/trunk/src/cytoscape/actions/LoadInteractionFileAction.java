//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import java.io.File;

import cytoscape.CytoscapeWindow;
import cytoscape.util.CyFileFilter;
//-------------------------------------------------------------------------
public class LoadInteractionFileAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public LoadInteractionFileAction(CytoscapeWindow cytoscapeWindow) {
        super("Interaction...");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e)  {
        File currentDirectory = cytoscapeWindow.getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        CyFileFilter filter = new CyFileFilter();
        filter.addExtension("sif");
        filter.setDescription("Interaction files");
        chooser.setFileFilter(filter);
        chooser.addChoosableFileFilter(filter);
        if (chooser.showOpenDialog(cytoscapeWindow) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            cytoscapeWindow.setCurrentDirectory(currentDirectory);
            String name = chooser.getSelectedFile().toString();
            cytoscapeWindow.loadInteraction(name);
        } // if
    } // actionPerformed
    
}

