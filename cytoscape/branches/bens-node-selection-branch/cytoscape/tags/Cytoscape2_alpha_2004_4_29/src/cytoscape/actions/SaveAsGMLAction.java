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
import java.io.FileWriter;
import java.io.IOException;

import cytoscape.view.NetworkView;
//import cytoscape.data.GraphProps;
import cytoscape.data.readers.GMLTree;
//-------------------------------------------------------------------------
public class SaveAsGMLAction extends AbstractAction {
    NetworkView networkView;
    
    public SaveAsGMLAction (NetworkView networkView) {
        super("Graph as GML...");
        this.networkView = networkView;
    }

    public SaveAsGMLAction (NetworkView networkView, String text ) {
        super();
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        //save as GML isn't supported yet, so for now we put up an
        //error message. Eventually we'll adapt the code preserved
        //below to use the new GML parser.
	File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
      	JFileChooser chooser = new JFileChooser (currentDirectory);
      	if (chooser.showSaveDialog (networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
          String name = chooser.getSelectedFile ().toString ();
          currentDirectory = chooser.getCurrentDirectory();
          networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
          if (!name.endsWith (".gml")) name = name + ".gml";
          try {
              FileWriter fileWriter = new FileWriter(name);
	      GMLTree result = new GMLTree(networkView);
	      fileWriter.write(result.toString());
	      fileWriter.close();
	  }
	  catch (IOException ioe) {
              System.err.println("Error while writing " + name);
              ioe.printStackTrace();
          } 
	}
    }
} // SaveAsGMLAction

