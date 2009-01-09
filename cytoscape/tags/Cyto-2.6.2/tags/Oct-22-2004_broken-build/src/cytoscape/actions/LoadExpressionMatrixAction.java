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
import javax.swing.JOptionPane;
import java.io.File;

import cytoscape.data.ExpressionData;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.*;
import cytoscape.data.Semantics;

//-------------------------------------------------------------------------
public class LoadExpressionMatrixAction extends CytoscapeAction {
  
    
  public LoadExpressionMatrixAction () {
    super("Expression Matrix File...");
    setPreferredMenu( "File.Load" );
    setAcceleratorCombo( KeyEvent.VK_E, ActionEvent.CTRL_MASK );
  }
    
  public void actionPerformed(ActionEvent e)  {
 
    CyFileFilter filter = new CyFileFilter();
    filter.addExtension("mrna");
    filter.addExtension("mRNA");
    filter.addExtension("pvals");
    filter.setDescription("Expression Matrix files");

    // get the file name
    final String name;
    try {
      name = FileUtil.getFile( "Load Expression Matrix File",
                               FileUtil.LOAD,
                               new CyFileFilter[] { filter } ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return;
    } 
    Cytoscape.loadExpressionData( name, true );

  } // actionPerformed
    
} // inner class LoadExpressionMatrix

