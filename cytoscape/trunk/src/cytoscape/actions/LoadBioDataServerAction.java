//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CyFileFilter;
import cytoscape.data.Semantics;
import cytoscape.data.servers.BioDataServer;
import cytoscape.data.Semantics;
//-------------------------------------------------------------------------
/**
 * Action allows the loading of a BioDataServer from the gui.
 *
 * added by dramage 2002-08-20
 */
public class LoadBioDataServerAction extends CytoscapeAction {
  
    
  public LoadBioDataServerAction () {
    super("Bio Data Server...");
    setPreferredMenu( "File.Load" );
  }

  public void actionPerformed(ActionEvent e) {
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser(currentDirectory);
    //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    if (chooser.showOpenDialog ( Cytoscape.getDesktop() ) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      String bioDataDirectory = chooser.getSelectedFile().toString();
      Cytoscape.loadBioDataServer( bioDataDirectory );
    }
  }
}


