// $Revision$
// $Date$
// $Author$


package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.util.*;
import cytoscape.view.CyNetworkView;
import cytoscape.data.GraphObjAttributes;

/**
 *  write out the current graph to the specified file, using the standard
 *  interactions format:  nodeA edgeType nodeB.
 *  for example: <code>
 *
 *     YMR056C pp YLL013C
 *     YCR107W pp YBR265W
 *
 *  </code>  
 */
public class SaveAsInteractionsAction extends CytoscapeAction {

    
  public SaveAsInteractionsAction () {
    super("Graph as Interactions...");
    setPreferredMenu( "File.Save" );
  }

  public void actionPerformed(ActionEvent e) {

    // get the file name
    String name;
    try {
      name = FileUtil.getFile( "Save Graph as Interactions",
                               FileUtil.SAVE,
                               new CyFileFilter[] {} ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return;
    }
    
    if ( !name.endsWith (".sif") ) 
      name = name + ".sif";
      
    GraphObjAttributes nodeAttributes = Cytoscape.getNodeNetworkData();
    GraphObjAttributes edgeAttributes = Cytoscape.getEdgeNetworkData();
    
    try {
      FileWriter fileWriter = new FileWriter( name );
      String lineSep = System.getProperty("line.separator");
      CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
      List nodeList = networkView.getNetwork().nodesList();
      giny.model.Node[] nodes = ( giny.model.Node[] ) nodeList.toArray ( new giny.model.Node [0] );
      for (int i=0; i < nodes.length; i++) {
        StringBuffer sb = new StringBuffer ();
        giny.model.Node node = nodes[i];
        String canonicalName = nodeAttributes.getCanonicalName(node);
        List edges = networkView.getNetwork().getAdjacentEdgesList(node, true, true, true); 
        
        if (edges.size() == 0) {
          sb.append(canonicalName + lineSep);
        } else {
          Iterator it = edges.iterator();
          while ( it.hasNext() ) {
            giny.model.Edge edge = (giny.model.Edge)it.next();
            if (node == edge.getSource()){ //do only for outgoing edges
              giny.model.Node target = edge.getTarget();
              String canonicalTargetName = nodeAttributes.getCanonicalName(target);
              String edgeName = edgeAttributes.getCanonicalName(edge);
              String interactionName =
                (String)(edgeAttributes.getValue("interaction", edgeName));
              if (interactionName == null) {interactionName = "xx";}
              sb.append(canonicalName);
              sb.append("\t");
              sb.append(interactionName);
              sb.append("\t");
              sb.append(canonicalTargetName);
              sb.append(lineSep);
            }
          } // while
        } // else: this node has edges, write out one line for every out edge (if any) */
        fileWriter.write(sb.toString());
        //System.out.println(" WRITE: "+ sb.toString() );
      }  // for i
      fileWriter.close();
    } catch (IOException ioe) {
      System.err.println("Error while writing " + name);
      ioe.printStackTrace();
    } // catch
 
  }  // actionPerformed
  
} // SaveAsInteractionsAction

