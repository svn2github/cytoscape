// $Revision$
// $Date$
// $Author$

// $Revision$
// $Date$
// $Author$

package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNetwork;
import cytoscape.data.readers.GMLWriter;
import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLReader2;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.*;


public class SaveAsGMLAction extends CytoscapeAction {
    
  public SaveAsGMLAction () {
    super("Graph as GML...");
    setPreferredMenu( "File.Save" );
  }

  public SaveAsGMLAction ( boolean label) {
    super();
  }
    
  public void actionPerformed(ActionEvent e) {
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
      //could also get the assoicate data here
      CyNetwork network = Cytoscape.getCurrentNetwork();
	CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());;
	List list = null;
	GMLReader2 reader = (GMLReader2)network.getClientData("GML");
	if(reader != null){
	  list = reader.getList();
	  System.err.println("Using loaded gml data");
	}
	else{
	  System.err.println("Not using loaded data");
	  list = new Vector();
	}
	GMLWriter gmlWriter = new GMLWriter();
	gmlWriter.writeGML(network,view,list);
	GMLParser.printList(list,fileWriter);
        fileWriter.close();
    }
    catch (IOException ioe) {
      System.err.println("Error while writing " + name);
      ioe.printStackTrace();
    } 
  }
  
} // SaveAsGMLAction

