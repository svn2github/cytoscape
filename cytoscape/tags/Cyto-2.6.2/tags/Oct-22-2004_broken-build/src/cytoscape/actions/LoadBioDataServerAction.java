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
import cytoscape.util.*;
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
   
    // get the file name
    final String name;
    try {
      name = FileUtil.getFile( "Load BioDataServer ",
                               FileUtil.LOAD,
                               new CyFileFilter[] {} ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return;
    } 
    Cytoscape.loadBioDataServer( name );
    
  }
}


