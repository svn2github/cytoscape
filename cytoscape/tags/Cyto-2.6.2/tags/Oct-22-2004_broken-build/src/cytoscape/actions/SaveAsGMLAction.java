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

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.data.readers.GMLTree;
import cytoscape.util.*;

//-------------------------------------------------------------------------
public class SaveAsGMLAction extends CytoscapeAction {
    
  public SaveAsGMLAction () {
    super("Graph as GML...");
    setPreferredMenu( "File.Save" );
  }

  public SaveAsGMLAction ( boolean label) {
    super();
  }
    
  public void actionPerformed(ActionEvent e) {
    // get the file name
    String name;
    try {
      name = FileUtil.getFile( "Save Graph as GML",
                               FileUtil.SAVE,
                               new CyFileFilter[] {} ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return;
    }
   
    if (!name.endsWith (".gml") ) 
      name = name + ".gml";
    
    try {
      FileWriter fileWriter = new FileWriter(name);
      GMLTree result = new GMLTree( Cytoscape.getCurrentNetworkView() );
      fileWriter.write(result.toString());
      fileWriter.close();
    }
    catch (IOException ioe) {
      System.err.println("Error while writing " + name);
      ioe.printStackTrace();
    } 
  }
  
} // SaveAsGMLAction

