/**
 *
 */
package org.systemsbiology.cytoscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.systemsbiology.cytoscape.dialog.*;

import org.systemsbiology.gaggle.core.datatypes.*;
import org.systemsbiology.gaggle.core.Boss;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * @author skillcoy
 */
public class CyBroadcast {
    private GooseDialog gDialog;
    private Boss gaggleBoss;

    private String broadcastID = "ID";

    // text strings for popup dialog boxes
    private String broadcastStr = "The following checked attributes will be captured for broadcast.\n"
            + "Click \"OK\" to proceed or \"Cancel\" to cancel transaction.";

    private static void print(String S) {
        System.out.println(S);
    }

    /**
     *
     */
    public CyBroadcast(GooseDialog Dialog, Boss boss) {
        this.gDialog = Dialog;
        this.gaggleBoss = boss;
    }

    // very basically for the moment we will only broadcast by ID
    public void broadcastNameList(CyGoose Goose, String TargetGoose) {
        print("broadcastNameList");
        if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0) {
            GagglePlugin.showDialogBox("No nodes were selected for list broadcast.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId())
                .getSelectedNodes();
        Iterator<CyNode> NodesIter = SelectedNodes.iterator();

        Set<CyEdge> SelectedEdges = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedEdges();
        Iterator<CyEdge> EdgesIter = SelectedEdges.iterator();
        
        // warning: no nodes are selected for broadcast
        if (SelectedNodes.size() == 0 && SelectedEdges.size() == 0) {
            GagglePlugin.showDialogBox("No nodes or edges selected for broadcast.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: get species XXX
        String Species = null;

        Species = gDialog.getSpecies();

        ArrayList<String> SelectedIds = new ArrayList<String>();
        while (NodesIter.hasNext()) 
        	SelectedIds.add(NodesIter.next().getIdentifier());
        while (EdgesIter.hasNext()) 
        	SelectedIds.add(EdgesIter.next().getIdentifier());
        
        String Ids[] = new String[SelectedIds.size()];
        Namelist namelist = new Namelist();
        namelist.setSpecies(Species);
        namelist.setNames(SelectedIds.toArray(Ids));

        try {
            gaggleBoss.broadcastNamelist(Goose.getName(), TargetGoose, namelist);
        }
        catch (Exception E) {
            GagglePlugin.showDialogBox("Failed to broadcast list of names to "
                    + TargetGoose, "Error", JOptionPane.ERROR_MESSAGE);
            E.printStackTrace();
        }
    }

    // TODO broadcast edge attributes as well
    // broadcasts hash of selected attributes
    public void broadcastTuple(CyGoose Goose, String TargetGoose) {
        print("broadcastTuple");

        Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId())
                .getSelectedNodes();

        // warning: no nodes are selected for broadcast
        if (SelectedNodes.size() == 0) {
            GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // pass string of attribute names
        String[] AllAttrNames = Cytoscape.getNodeAttributes().getAttributeNames();

        final CyGoose goose = Goose;
        final String target = TargetGoose;

        // confirmAttrSelection(AllAttrNames, "HashMap");
        AttrSelectAction okAction = new AttrSelectAction() {
            public void takeAction(String[] selectAttr) {
            	broadcastTuple(selectAttr, goose, target);
            }
        };

        CyAttrDialog dialog = new CyAttrDialog(AllAttrNames, okAction,
                CyAttrDialog.MULTIPLE_SELECT);
        dialog.setDialogText(broadcastStr);
        dialog.preSelectCheckBox(AllAttrNames);
        dialog.buildDialogWin();
    }

    
    private void broadcastTuple(String[] attrNames, CyGoose goose,
                                  String targetGoose) {
        if (Cytoscape.getNetwork(goose.getNetworkId()).getSelectedNodes().size() <= 0) {
            GagglePlugin.showDialogBox("No nodes were selected for tuple broadcast.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GaggleTuple gaggleTuple = new GaggleTuple();
        //gaggleTuple.setSpecies(Cytoscape.get); // todo add a way to specify species  - possibly:
        /*
        add a dropdown or ui item that allows you to either type in a species
        or pick from all of the values of attributes named 'species' in the current network
        (or in all networks?)

        the existing way might be ok for now (see getSpecies())
         */

        Set<CyNode> selectedNodes = Cytoscape.getNetwork(goose.getNetworkId()).getSelectedNodes();
        Iterator<CyNode> nodeIter = selectedNodes.iterator();

        // create a string array of node names
        String[] nodeArr = new String[selectedNodes.size()];
        for (int i = 0; (i < selectedNodes.size()) && nodeIter.hasNext(); i++) {
            CyNode currentNode = (CyNode) nodeIter.next();
            nodeArr[i] = currentNode.getIdentifier();
        }

        Tuple dataTuple = new Tuple();
        Tuple metadata = new Tuple();
        metadata.addSingle(new Single("condition", "static"));
        gaggleTuple.setMetadata(metadata);

        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        
        for (String attr : attrNames) {
            switch (nodeAtts.getType(attr)) {
                case CyAttributes.TYPE_INTEGER:
                    for (String nodeName : nodeArr) {
                        Tuple row = new Tuple();
                        row.addSingle(new Single(nodeName));
                        row.addSingle(new Single(attr));
                        row.addSingle(new Single(nodeAtts.getIntegerAttribute(nodeName, attr)));
                        dataTuple.addSingle(new Single(row));
                    }
                    break;
                case CyAttributes.TYPE_FLOATING:
                    for (String nodeName : nodeArr) {
                        Tuple row = new Tuple();
                        row.addSingle(new Single(nodeName));
                        row.addSingle(new Single(attr));
                        row.addSingle(new Single(nodeAtts.getDoubleAttribute(nodeName, attr)));
                        dataTuple.addSingle(new Single(row));
                    }
                    break;
                case CyAttributes.TYPE_STRING:
                    for (String nodeName : nodeArr) {
                    	if (nodeAtts.getStringAttribute(nodeName, attr) != null) {
	                        Tuple row = new Tuple();
	                        row.addSingle(new Single(nodeName));
	                        row.addSingle(new Single(attr));
	                        row.addSingle(new Single(nodeAtts.getStringAttribute(nodeName, attr)));
	                        dataTuple.addSingle(new Single(row));
                    	}
                    }
                    break;
            }
        }

        gaggleTuple.setData(dataTuple);
        gaggleTuple.setSpecies( this.gDialog.getSpecies() );
        //gaggleTuple.setSpecies(getSpecies(nodeArr[0])); // todo, find a better way (see above)
        gaggleTuple.setName(""); //why?

        try {
            broadcastTuple(goose, targetGoose); // bogus call
            gaggleBoss.broadcastTuple(goose.getName(), targetGoose, gaggleTuple);
        }
        catch (Exception E) {
            GagglePlugin.showDialogBox("Failed to broadcast map to " + targetGoose,
                    "Error", JOptionPane.ERROR_MESSAGE);
            E.printStackTrace();
        }
    }


    // TODO handle edges as well
    public void broadcastDataMatrix(CyGoose Goose, String TargetGoose) {
        print("broadcastDataMatrix");

        if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0) {
            GagglePlugin.showDialogBox(
                    "No nodes were selected for Data Matrix broadcast.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<CyNode> SelectedNodes = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes();

        // warning: no nodes are selected for broadcast
        if (SelectedNodes.size() == 0) {
            GagglePlugin.showDialogBox("No nodes selected for broadcast.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // create an array of experiment conditions (columnTitles in DataMatrix)
        ArrayList<String> condNamesArrList = new ArrayList<String>();
        String[] attributeNames = Cytoscape.getNodeAttributes().getAttributeNames();

        for (String CurrentAttr : attributeNames) {
            // assume all DOUBLE type attributes are expression data
            if (Cytoscape.getNodeAttributes().getType(CurrentAttr) == CyAttributes.TYPE_FLOATING)
                condNamesArrList.add(CurrentAttr);
        }

        // move everything from ArrayList to a String array
        String[] condNames = new String[condNamesArrList.size()];
        condNamesArrList.toArray(condNames);

        final CyGoose goose = Goose;
        final String target = TargetGoose;

        // dialog for user to select attributes for broadcast
        AttrSelectAction okAction = new AttrSelectAction() {
            public void takeAction(String[] selectAttr) {
                broadcastDataMatrix(selectAttr, goose, target);
            }
        };

        if (condNames.length > 0) {
            CyAttrDialog dialog = new CyAttrDialog(condNames, okAction,
                    CyAttrDialog.MULTIPLE_SELECT);
            dialog.setDialogText(broadcastStr);
            dialog.preSelectCheckBox(condNames);
            dialog.buildDialogWin();
        } else {
            GagglePlugin.showDialogBox(
                    "The selected nodes do not have numerical attributes for a matrix",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void broadcastDataMatrix(String[] condNames, CyGoose Goose,
                                     String TargetGoose) {
        if (Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().size() <= 0) {
            GagglePlugin.showDialogBox(
                    "No nodes were selected for Data Matrix broadcast.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        // initialize DataMatrix
        DataMatrix matrix = new DataMatrix();
        matrix.setColumnTitles(condNames);

        // loop through all flagged nodes and construct a DataMatrix with
        // row=columnNames & column=condNames
        Iterator<CyNode> selectedNodesIter = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().iterator();
        while (selectedNodesIter.hasNext()) {
            double[] condVals = new double[condNames.length];
            CyNode CurrentSelectedNode = selectedNodesIter.next();
            String NodeId = CurrentSelectedNode.getIdentifier();
            for (int i = 0; i < condNames.length; i++) {
                try {
                    Double val = Cytoscape.getNodeAttributes().getDoubleAttribute(NodeId,
                            condNames[i]);
                    if (val != null) condVals[i] = val.doubleValue();
                }
                catch (Exception ex) {
                    System.out
                            .println("broadcastDataMatrix() error: incompatible data type for "
                                    + condNames[i]);
                }
            }
            // use other attribute to identify node if selected by user

            if (!broadcastID.equals(CyAttrDialog.DEFAULT_NODE_ID)) // again..."ID" ==
            // "ID" WHY??
            {
                NodeId = Cytoscape.getNodeAttributes().getStringAttribute(NodeId,
                        broadcastID);

                // skip any node that is null // shouldn't this happen before?
                if (NodeId == null) continue;
            }
            // add new row to DataMatrix
            matrix.addRow(NodeId, condVals);
            matrix.setSpecies( this.gDialog.getSpecies() );
            //matrix.setSpecies(getSpecies(NodeId)); // XXX er...does this set the species
            // every time 'round? what if the
            // species is different from one
            // node to another?
        }

        try {
            this.gaggleBoss.broadcastMatrix(Goose.getName(), TargetGoose, matrix);
        }
        catch (Exception E) {
            GagglePlugin.showDialogBox(
                    "Failed to broadcast matrix to " + TargetGoose, "Error",
                    JOptionPane.ERROR_MESSAGE);
            E.printStackTrace();
        }
    }

    public void broadcastNetwork(CyGoose Goose, String TargetGoose) {
        print("broadcastNetwork " + Cytoscape.getNetwork(Goose.getNetworkId()).getIdentifier());

        Network GaggleNetwork = new Network();
        String Species = "";

        Iterator<CyNode> NodesIter = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedNodes().iterator();
        while (NodesIter.hasNext()) {
            CyNode CurrentSelectedNode = NodesIter.next();
            GaggleNetwork.add(CurrentSelectedNode.getIdentifier());
        }

        Iterator<CyEdge> EdgesIter = Cytoscape.getNetwork(Goose.getNetworkId()).getSelectedEdges().iterator();
        CyAttributes EdgeAtts = Cytoscape.getEdgeAttributes();
        while (EdgesIter.hasNext()) {
            CyEdge CurrentSelectedEdge = EdgesIter.next();

            CyNode SourceNode = (CyNode) CurrentSelectedEdge.getSource();
            CyNode TargetNode = (CyNode) CurrentSelectedEdge.getTarget();

            // create a new GaggleInteraction for broadcast
            String InteractionType = EdgeAtts.getStringAttribute(CurrentSelectedEdge.getIdentifier(), Semantics.INTERACTION);
            Interaction GaggleInteraction = new Interaction(
                    SourceNode.getIdentifier(), TargetNode.getIdentifier(),
                    InteractionType, CurrentSelectedEdge.isDirected());
            GaggleNetwork.add(GaggleInteraction);

            // again if there's more than one species we'll only get the last one!!!
            Species = this.gDialog.getSpecies();
            //Species = getSpecies(SourceNode.getIdentifier());
        }

        if (Species.equals("")) {
            Species = gDialog.getSpecies();
        }

        GaggleNetwork = addAttributes(GaggleNetwork);
        GaggleNetwork.setSpecies(Species);
        System.out.println("in broadcastnetwork, species is " + GaggleNetwork.getSpecies());
        try {
            this.gaggleBoss.broadcastNetwork(Goose.getName(), TargetGoose,
                    GaggleNetwork);
        }
        catch (Exception E) {
            E.printStackTrace();
        }
    }


    private Network addAttributes(Network gaggleNet) {
        for (String id : gaggleNet.getNodes()) {
            gaggleNet = addAttributes(id, Cytoscape.getNodeAttributes(), gaggleNet, NetworkObject.NODE);
        }

        for (Interaction interaction : gaggleNet.getInteractions())
            gaggleNet = addAttributes(interaction.toString(), Cytoscape.getEdgeAttributes(), gaggleNet, NetworkObject.EDGE);

        return gaggleNet;
    }


    // try the "species" attribute first; if not found, use DefaultSpeciesName
//    private String getSpecies(String NodeId) {
//        String Species = "";
//
//        Species = Cytoscape.getNodeAttributes().getStringAttribute(NodeId,
//                Semantics.SPECIES);
//        if (Species == null)
//            Species = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
//
//        return Species;
//    }


    // add attributes to the node/edge
    private Network addAttributes(String Identifier, CyAttributes cyAtts, Network gaggleNet, NetworkObject obj) {

        for (String AttributeName : cyAtts.getAttributeNames()) {
            Object Value = "";
            if (!cyAtts.getUserVisible(AttributeName)) {
                continue; // don't think we should pass on hidden attributes, they aren't useful to the user
            }

            switch (cyAtts.getType(AttributeName)) {
                case CyAttributes.TYPE_BOOLEAN:
                    Value = cyAtts.getBooleanAttribute(Identifier, AttributeName);
                    break;

                case CyAttributes.TYPE_INTEGER:
                    Value = cyAtts.getIntegerAttribute(Identifier, AttributeName);
                    break;

                case CyAttributes.TYPE_STRING:
                    Value = cyAtts.getStringAttribute(Identifier, AttributeName);
                    break;

                case CyAttributes.TYPE_FLOATING:
                    Value = cyAtts.getDoubleAttribute(Identifier, AttributeName);
                    break;
            }
            ;
            if (Value == null) Value = "";
            switch (obj) {
                case NODE:
                    gaggleNet.addNodeAttribute(Identifier, AttributeName, Value);
                    break;

                case EDGE:
                    gaggleNet.addEdgeAttribute(Identifier, AttributeName, Value);
                    break;
            }
        }

        return gaggleNet;
    }


}
