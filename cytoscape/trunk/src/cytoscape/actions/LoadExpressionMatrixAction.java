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
import java.io.File;

import cytoscape.CytoscapeWindow;
import cytoscape.util.CyFileFilter;
//-------------------------------------------------------------------------
public class LoadExpressionMatrixAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public LoadExpressionMatrixAction (CytoscapeWindow cytoscapeWindow) {
        super("Expression Matrix File...");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e)  {
        File currentDirectory = cytoscapeWindow.getCurrentDirectory();
	ExpFileChooser chooser = new ExpFileChooser(currentDirectory);
	CyFileFilter filter = new CyFileFilter();
	filter.addExtension("mrna");
	filter.addExtension("mRNA");
	filter.addExtension("pvals");
	filter.setDescription("Expression Matrix files");
	chooser.setFileFilter(filter);
	chooser.addChoosableFileFilter(filter);
	if (chooser.showOpenDialog (cytoscapeWindow) == chooser.APPROVE_OPTION) {
	    currentDirectory = chooser.getCurrentDirectory();
            cytoscapeWindow.setCurrentDirectory(currentDirectory);
	    String expDataFilename = chooser.getSelectedFile().toString();
	    boolean validLoad = cytoscapeWindow.loadExpressionData(expDataFilename);
	    
            if (validLoad) {
                // rather than depend on the configuration file,
                // depend on the ExpFileChooser's checkbox.
                //if(config.getWhetherToCopyExpToAttribs()) {
                if(chooser.getWhetherToCopyExpToAttribs()) {
                    cytoscapeWindow.getExpressionData().copyToAttribs(
                                            cytoscapeWindow.getNodeAttributes());
                }
                //display a description of the data in a dialog
                String expDescript =
                        cytoscapeWindow.getExpressionData().getDescription();
                String title = "Load Expression Data";
                JOptionPane.showMessageDialog(cytoscapeWindow.getMainFrame(),
                                              expDescript, title,
                                              JOptionPane.PLAIN_MESSAGE);
            } else {
                //show an error message in a dialog
                String errString = "Unable to load expression data from "
                    + expDataFilename;
                String title = "Load Expression Data";
                JOptionPane.showMessageDialog(cytoscapeWindow.getMainFrame(),
                                              errString, title,
                                              JOptionPane.ERROR_MESSAGE);
            }
	} // if
    } // actionPerformed
    
} // inner class LoadExpressionMatrix

