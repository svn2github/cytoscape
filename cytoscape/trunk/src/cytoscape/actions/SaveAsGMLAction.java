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
import cytoscape.data.GraphProps;
import cytoscape.data.readers.GMLWriter;
//-------------------------------------------------------------------------
public class SaveAsGMLAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public SaveAsGMLAction (CytoscapeWindow cytoscapeWindow) {
        super("As GML...");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        File currentDirectory = cytoscapeWindow.getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        if (chooser.showSaveDialog(cytoscapeWindow) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            cytoscapeWindow.setCurrentDirectory(currentDirectory);
            String name = chooser.getSelectedFile().toString();
            if (!name.endsWith(".gml")) name = name + ".gml";
            GraphProps props = cytoscapeWindow.getProps();
            GMLWriter writer = new GMLWriter(props);
            writer.write(name);
        } // if
    }
} // SaveAsGMLAction

