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
import java.io.FileWriter;
import java.io.IOException;

import y.base.*;
import y.view.Graph2D;

import cytoscape.CytoscapeWindow;
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
    CytoscapeWindow cytoscapeWindow;
    
  public SaveAsInteractionsAction(CytoscapeWindow cytoscapeWindow) {
      super("As Interactions...");
      this.cytoscapeWindow = cytoscapeWindow;
  }

  public void actionPerformed (ActionEvent e) {
    File currentDirectory = cytoscapeWindow.getCurrentDirectory();
    JFileChooser chooser = new JFileChooser (currentDirectory);
    if (chooser.showSaveDialog (cytoscapeWindow) == chooser.APPROVE_OPTION) {
      String name = chooser.getSelectedFile ().toString ();
      currentDirectory = chooser.getCurrentDirectory();
      cytoscapeWindow.setCurrentDirectory(currentDirectory);
      if (!name.endsWith (".sif")) name = name + ".sif";
      try {
        FileWriter fileWriter = new FileWriter (name);
        Node [] nodes = cytoscapeWindow.getGraph().getNodeArray();
        GraphObjAttributes nodeAttributes = cytoscapeWindow.getNodeAttributes();
        GraphObjAttributes edgeAttributes = cytoscapeWindow.getEdgeAttributes();
        for (int i=0; i < nodes.length; i++) {
          StringBuffer sb = new StringBuffer ();
          Node node = nodes [i];
          String canonicalName = nodeAttributes.getCanonicalName (node);
          if (node.edges().size() == 0)
            sb.append (canonicalName + "\n");
          else {
            EdgeCursor ec = node.outEdges ();
            for (ec.toFirst (); ec.ok (); ec.next ()) {
              Edge edge = ec.edge ();
              Node target = edge.target ();
              String canonicalTargetName = nodeAttributes.getCanonicalName (target);
              String edgeName = edgeAttributes.getCanonicalName(edge);
              String interactionName =
                 (String)(edgeAttributes.getValue("interaction", edgeName));
              if (interactionName == null) {interactionName = "xx";}
              sb.append (canonicalName);
              sb.append (" ");
              sb.append (interactionName);
              sb.append (" ");
              sb.append (canonicalTargetName);
              sb.append ("\n");
              } // for ec
             } // else: this node has edges, write out one line for every out edge (if any)
           fileWriter.write (sb.toString ());
          }  // for i
          fileWriter.close ();
        } 
      catch (IOException ioe) {
        System.err.println ("Error while writing " + name);
        ioe.printStackTrace ();
        } // catch
      } // if
    }  // actionPerformed

} // SaveAsInteractionsAction

