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
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CyFileFilter;
import cytoscape.data.Semantics;

//-------------------------------------------------------------------------
public class LoadExpressionMatrixAction extends CytoscapeAction {
  
    
  public LoadExpressionMatrixAction () {
    super("Expression Matrix File...");
    setPreferredMenu( "File.Load" );
    setAcceleratorCombo( KeyEvent.VK_E, ActionEvent.CTRL_MASK );
  }
    
  public void actionPerformed(ActionEvent e)  {
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    ExpFileChooser chooser = new ExpFileChooser(currentDirectory);
    CyFileFilter filter = new CyFileFilter();
    filter.addExtension("mrna");
    filter.addExtension("mRNA");
    filter.addExtension("pvals");
    filter.setDescription("Expression Matrix files");
    chooser.setFileFilter(filter);
    chooser.addChoosableFileFilter(filter);
    if (chooser.showOpenDialog( Cytoscape.getDesktop() ) == chooser.APPROVE_OPTION) {
	    currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	    String expDataFilename = chooser.getSelectedFile().toString();
      Cytoscape.loadExpressionData( expDataFilename, chooser.getWhetherToCopyExpToAttribs() );
    } // if
  } // actionPerformed
    
} // inner class LoadExpressionMatrix

