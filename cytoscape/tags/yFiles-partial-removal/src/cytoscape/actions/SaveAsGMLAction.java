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
import javax.swing.JOptionPane;
import java.io.File;

import cytoscape.view.NetworkView;
//import cytoscape.data.GraphProps;
//import cytoscape.data.readers.GMLWriter;
//-------------------------------------------------------------------------
public class SaveAsGMLAction extends AbstractAction {
    NetworkView networkView;
    
    public SaveAsGMLAction (NetworkView networkView) {
        super("Graph as GML...");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        //save as GML isn't supported yet, so for now we put up an
        //error message. Eventually we'll adapt the code preserved
        //below to use the new GML parser.
        String title = "Operation not supported";
        String message = "The 'Save As GML' operation is not yet supported.";
        JOptionPane.showMessageDialog(networkView.getMainFrame(), message,
                                      title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void oldSavedCode() {
        /*
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        if (chooser.showSaveDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
            String name = chooser.getSelectedFile().toString();
            if (!name.endsWith(".gml")) name = name + ".gml";
            GraphProps props = new GraphProps(networkView.getNetwork().getGraph(),
                                              networkView.getNetwork().getNodeAttributes(),
                                              networkView.getNetwork().getEdgeAttributes());
            GMLWriter writer = new GMLWriter(props);
            writer.write(name);
        } // if
        */
    }
} // SaveAsGMLAction

