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
import cytoscape.data.servers.BioDataServer;
//-------------------------------------------------------------------------
/**
 * Action allows the loading of a BioDataServer from the gui.
 *
 * added by dramage 2002-08-20
 */
public class LoadBioDataServerAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public LoadBioDataServerAction(CytoscapeWindow cytoscapeWindow) {
        super("Bio Data Server...");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed(ActionEvent e) {
        File currentDirectory = cytoscapeWindow.getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog (cytoscapeWindow) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            cytoscapeWindow.setCurrentDirectory(currentDirectory);
            String bioDataDirectory = chooser.getSelectedFile().toString();
            BioDataServer bioDataServer = null;
            //bioDataServer = BioDataServerFactory.create (bioDataDirectory);
            try {
                bioDataServer = new BioDataServer (bioDataDirectory);
                cytoscapeWindow.setBioDataServer(bioDataServer);
            } catch (Exception e0) {
                String es = "cannot create new biodata server at " + bioDataDirectory;
                cytoscapeWindow.getLogger().warning(es);
            }
            /* this really shouldn't be here; labels should be controlled
             * via the vizmapper */
            cytoscapeWindow.displayCommonNodeNames();
            cytoscapeWindow.redrawGraph(false, true);
        }
    }
}


