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

import y.base.Node;
import y.view.Graph2D;

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class ListFromFileSelectionAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public  ListFromFileSelectionAction (CytoscapeWindow cytoscapeWindow) {
        super("From File...");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        boolean cancelSelectionAction = !useSelectionFile();
    }

    private boolean useSelectionFile() {
        File currentDirectory = cytoscapeWindow.getCurrentDirectory();
        JFileChooser fChooser = new JFileChooser(currentDirectory);     
        fChooser.setDialogTitle("Load Gene Selection File");
        switch (fChooser.showOpenDialog(null)) {
                
        case JFileChooser.APPROVE_OPTION:
            File file = fChooser.getSelectedFile();
            currentDirectory = fChooser.getCurrentDirectory();
            cytoscapeWindow.setCurrentDirectory(currentDirectory);
            String s;

            try {
                FileReader fin = new FileReader(file);
                BufferedReader bin = new BufferedReader(fin);
                // create a hash of all the nodes in the file
                Hashtable fileNodes = new Hashtable();
                while ((s = bin.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(s);
                    String name = st.nextToken();
                    String trimname = name.trim();
                    if(trimname.length() > 0) {
                        String canonicalName =
                                cytoscapeWindow.findCanonicalName(trimname);
                        fileNodes.put(canonicalName, Boolean.TRUE);
                    }
                }
                fin.close();

                // loop through all the node of the graph
                // selecting those in the file
                Graph2D graph = cytoscapeWindow.getGraph();
                Node [] nodes = graph.getNodeArray();
                for (int i=0; i < nodes.length; i++) {
                    Node node = nodes[i];
                    String canonicalName =
                        cytoscapeWindow.getNodeAttributes().getCanonicalName(node);
                    if (canonicalName == null) {
                        // use node label as canonical name
                        canonicalName = graph.getLabelText(node);
                    }
                    Boolean select = (Boolean) fileNodes.get(canonicalName);
                    if (select != null) {
                        graph.getRealizer(node).setSelected(true);
                    }
                }
                cytoscapeWindow.redrawGraph(false, false);
                  
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.toString(),
                                 "Error Reading \"" + file.getName()+"\"",
                                               JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;

        default:
            // cancel or error
            return false;
        }
    }
    
}

