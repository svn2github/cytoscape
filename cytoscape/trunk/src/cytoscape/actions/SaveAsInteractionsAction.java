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
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import giny.model.RootGraph;
import giny.model.GraphPerspective;
import y.base.*;
import y.view.Graph2D;

import cytoscape.view.NetworkView;
import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
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
public class SaveAsInteractionsAction extends AbstractAction {
    NetworkView networkView;
    
  public SaveAsInteractionsAction(NetworkView networkView) {
      super("As Interactions...");
      this.networkView = networkView;
  }

  public void actionPerformed(ActionEvent e) {
    File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser (currentDirectory);
    if (chooser.showSaveDialog (networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
      String name = chooser.getSelectedFile ().toString ();
      currentDirectory = chooser.getCurrentDirectory();
      networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      if (!name.endsWith (".sif")) name = name + ".sif";
      GraphObjAttributes nodeAttributes = networkView.getNetwork().getNodeAttributes();
      GraphObjAttributes edgeAttributes = networkView.getNetwork().getEdgeAttributes();
      try {
        FileWriter fileWriter = new FileWriter(name);
	if ( networkView.getCytoscapeObj().getConfiguration().isYFiles()) {
		Node [] nodes = networkView.getNetwork().getGraph().getNodeArray();
		for (int i=0; i < nodes.length; i++) {
			StringBuffer sb = new StringBuffer ();
			Node node = nodes[i];
			String canonicalName = nodeAttributes.getCanonicalName(node);
			if (node.edges().size() == 0)
				sb.append(canonicalName + "\n");
				else {
					EdgeCursor ec = node.outEdges();
					for (ec.toFirst(); ec.ok(); ec.next()) {
						Edge edge = ec.edge();
						Node target = edge.target();
						String canonicalTargetName = nodeAttributes.getCanonicalName(target);
						String edgeName = edgeAttributes.getCanonicalName(edge);
						String interactionName =
						(String)(edgeAttributes.getValue("interaction", edgeName));
						if (interactionName == null) {interactionName = "xx";}
						sb.append(canonicalName);
						sb.append(" ");
						sb.append(interactionName);
						sb.append(" ");
						sb.append(canonicalTargetName);
						sb.append("\n");
					} // for ec
				} // else: this node has edges, write out one line for every out edge (if any)
				fileWriter.write(sb.toString());
		}  // for i
          fileWriter.close();
	}//isYFiles
	else {// for giny
		List nodeList = networkView.getNetwork().getRootGraph().nodesList();
		giny.model.Node [] nodes = (giny.model.Node []) nodeList.toArray (new giny.model.Node [0]);
		for (int i=0; i < nodes.length; i++) {
			StringBuffer sb = new StringBuffer ();
			giny.model.Node node = nodes[i];
			String canonicalName = nodeAttributes.getCanonicalName(node);
			RootGraph rgraph = networkView.getNetwork().getRootGraph();
			GraphPerspective gp = rgraph.createGraphPerspective(rgraph.getNodeIndicesArray(), rgraph.getEdgeIndicesArray());
			List edges = gp.getAdjacentEdgesList(node, true, true, true); 
			if (edges.size() == 0) {
				sb.append(canonicalName + "\n");
      } else {
					List outedges = gp.getAdjacentEdgesList(node, true, false, true);
          if ( outedges == null || outedges.size() == 0 ) {
            sb.append(canonicalName + "\n");
          } else {

            Iterator it = outedges.iterator();
            while ( it.hasNext() ) {
              giny.model.Edge edge = (giny.model.Edge)it.next();
              giny.model.Node target = edge.getTarget();
              String canonicalTargetName = nodeAttributes.getCanonicalName(target);
              String edgeName = edgeAttributes.getCanonicalName(edge);
              String interactionName =
                (String)(edgeAttributes.getValue("interaction", edgeName));
              if (interactionName == null) {interactionName = "xx";}
              sb.append(canonicalName);
              sb.append(" ");
              sb.append(interactionName);
              sb.append(" ");
              sb.append(canonicalTargetName);
              sb.append("\n");
            } // while
          }
				} // else: this node has edges, write out one line for every out edge (if any) */
				fileWriter.write(sb.toString());
        //System.out.println(" WRITE: "+ sb.toString() );
		}  // for i
	 fileWriter.close();
  }
        } 
      catch (IOException ioe) {
        System.err.println("Error while writing " + name);
        ioe.printStackTrace();
        } // catch
      } // if
    }  // actionPerformed

} // SaveAsInteractionsAction

