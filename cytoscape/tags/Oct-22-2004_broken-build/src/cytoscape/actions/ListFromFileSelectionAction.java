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

import java.util.*;
import java.io.*;

import giny.view.*;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.Semantics;
import cytoscape.util.*;
//-------------------------------------------------------------------------
public class ListFromFileSelectionAction extends CytoscapeAction {
    
  public  ListFromFileSelectionAction () {
    super("From File...");
    setPreferredMenu( "Select.Nodes" );
  }

  public void actionPerformed (ActionEvent e) {
    boolean cancelSelectionAction = !selectFromFile();
  }
    
  private boolean selectFromFile() {
  
    // get the file name
    final String name;
    try {
      name = FileUtil.getFile( "Load Gene Selection File",
                               FileUtil.LOAD ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return false;
    }

    CyNetwork network = Cytoscape.getCurrentNetwork();     
            
    try {
      FileReader fin = new FileReader( name );
      BufferedReader bin = new BufferedReader(fin);
      List fileNodes = new ArrayList();
      String s;
      while ((s = bin.readLine()) != null) {
        String trimName = s.trim();
        if (trimName.length() > 0) {fileNodes.add(trimName);}
      }
      fin.close();

      // loop through all the node of the graph
      // selecting those in the file

                
      List nodeList = network.nodesList();
      giny.model.Node [] nodes = (giny.model.Node [])nodeList.toArray(new giny.model.Node[0]);
      for (int i=0; i < nodes.length; i++) {
        giny.model.Node node = nodes[i];
        boolean select = false;
        String canonicalName =
          network.getNodeAttributes().getCanonicalName(node);
        List synonyms =
          Semantics.getAllSynonyms(canonicalName, network );
        for (Iterator synI=synonyms.iterator(); synI.hasNext(); ) {
          if ( fileNodes.contains( (String)synI.next() ) ) {
            select = true;
            break;
          }
        }
        if (select) {
          //CyNetworkView view = Cytoscape.getCurrentNetworkView();
          //NodeView nv = view.getNodeView(node.getRootGraphIndex());
          //nv.setSelected(true);
          network.setFlagged(node,true);
        }
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.toString(),
                                    "Error Reading \"" +name+"\"",
                                    JOptionPane.ERROR_MESSAGE);
               
      return false;
    }

           
    return true;
  }
  
}  


