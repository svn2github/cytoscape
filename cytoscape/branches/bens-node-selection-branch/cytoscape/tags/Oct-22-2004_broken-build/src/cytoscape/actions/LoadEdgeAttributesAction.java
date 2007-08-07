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
import javax.swing.JOptionPane;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.*;
import cytoscape.data.Semantics;

//-------------------------------------------------------------------------
/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of edge / edge attributes from the GUI
 */
public class LoadEdgeAttributesAction extends CytoscapeAction {
  
    
  public LoadEdgeAttributesAction () {
    super("Edge Attributes...");
    setPreferredMenu( "File.Load" );
  }
    
  public void actionPerformed(ActionEvent e)  {
    
    CyFileFilter nf = new CyFileFilter();
    nf.addExtension("na");
    nf.setDescription("Edge Attribute files");

    // get the file name
    final String name;
    try {
      name = FileUtil.getFile( "Load Edge Attributes",
                               FileUtil.LOAD,
                               new CyFileFilter[] { nf } ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return;
    } 
             
    Cytoscape.loadAttributes( new String[] {},
                              new String[] { name },
                              !CytoscapeInit.noCanonicalization(),
                              Cytoscape.getBioDataServer(),
                              CytoscapeInit.getDefaultSpeciesName() ) ;
  } // actionPerformed
}

