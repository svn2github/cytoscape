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

import cytoscape.data.ExpressionData;
import cytoscape.view.NetworkView;
import cytoscape.util.CyFileFilter;
//-------------------------------------------------------------------------
public class LoadExpressionMatrixAction extends AbstractAction {
    NetworkView networkView;
    
    public LoadExpressionMatrixAction (NetworkView networkView) {
        super("Expression Matrix File...");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e)  {
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
	ExpFileChooser chooser = new ExpFileChooser(currentDirectory);
	CyFileFilter filter = new CyFileFilter();
	filter.addExtension("mrna");
	filter.addExtension("mRNA");
	filter.addExtension("pvals");
	filter.setDescription("Expression Matrix files");
	chooser.setFileFilter(filter);
	chooser.addChoosableFileFilter(filter);
	if (chooser.showOpenDialog (networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
	    currentDirectory = chooser.getCurrentDirectory();
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	    String expDataFilename = chooser.getSelectedFile().toString();
            ExpressionData newData = new ExpressionData();
	    boolean validLoad = newData.loadData(expDataFilename);
	    
            if (validLoad) {
                String callerID = "LoadExpressionMatrixAction.actionPerformed";
                networkView.getNetwork().beginActivity(callerID);
                networkView.getNetwork().setExpressionData(newData);
                // rather than depend on the configuration file,
                // depend on the ExpFileChooser's checkbox.
                //if(config.getWhetherToCopyExpToAttribs()) {
                if(chooser.getWhetherToCopyExpToAttribs()) {
                    newData.copyToAttribs(networkView.getNetwork().getNodeAttributes());
                    //graph appearances may depend on expression data attributes
                    networkView.redrawGraph(false, true);
                }
                networkView.getNetwork().endActivity(callerID);
                //display a description of the data in a dialog
                String expDescript = newData.getDescription();
                String title = "Load Expression Data";
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              expDescript, title,
                                              JOptionPane.PLAIN_MESSAGE);
            } else {
                //show an error message in a dialog
                String errString = "Unable to load expression data from "
                    + expDataFilename;
                String title = "Load Expression Data";
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              errString, title,
                                              JOptionPane.ERROR_MESSAGE);
            }
	} // if
    } // actionPerformed
    
} // inner class LoadExpressionMatrix

