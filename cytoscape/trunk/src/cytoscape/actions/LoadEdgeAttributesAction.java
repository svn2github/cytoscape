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
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CyFileFilter;
import cytoscape.data.Semantics;

//-------------------------------------------------------------------------
/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of node / edge attributes from the GUI
 */

public class LoadEdgeAttributesAction extends CytoscapeAction {
     
     
  public LoadEdgeAttributesAction () {
    super("Edge Attributes...");
    setPreferredMenu( "File.Load" );
    setAcceleratorCombo( KeyEvent.VK_E, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
  }
    
  public void actionPerformed(ActionEvent e)  {
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser(currentDirectory);
    String dialogTitle = "Load Edge Attributes";
    chooser.setDialogTitle(dialogTitle);
    if (chooser.showOpenDialog( Cytoscape.getDesktop() ) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      String attrFilename = chooser.getSelectedFile().toString();
            
      Cytoscape.loadAttributes( new String[] {},
                                new String[] { attrFilename },
                                Semantics.getCanonicalize( Cytoscape.getCytoscapeObj() ),
                                Cytoscape.getCytoscapeObj().getBioDataServer(),
                                Semantics.getDefaultSpecies( Cytoscape.getCurrentNetwork(),
                                                             Cytoscape.getCytoscapeObj() )
                                );
    } // if
  } // actionPerformed
}

