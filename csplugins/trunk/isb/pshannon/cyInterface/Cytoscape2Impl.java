package csplugins.isb.pshannon.cyInterface;

import java.util.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.Action;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.Semantics;
/**
 * This class implements the interface between the DataCube plugin and Cytoscape
 * assuming Cytoscape version 2.0.
 *
 * Selections to and from Cytoscape are with respect to the currently focused network.
 */
public class Cytoscape2Impl implements CyInterface {
    
    public Cytoscape2Impl() {}
    
    public JFrame getMainFrame() {return Cytoscape.getDesktop();}
    public String[] getArguments() {
        return Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    }
    public void addPluginMenuAction(Action action) {
        Cytoscape.getDesktop().getCyMenus().getMenuBar().addAction("Plugins", action);
    }
    public void addFileLoadMenuAction(Action action) {
        Cytoscape.getDesktop().getCyMenus().getMenuBar().addAction("File.Load", action);
    }
    
    public String[] getNodeAttributeNames() {
        return Cytoscape.getCurrentNetwork().getNodeAttributesList();
    }
    public Object getNodeAttributeValue(String attributeName, String nodeName) {
        CyNode node = Cytoscape.getCyNode(nodeName, false);
        if (node == null) {return null;}
        //note swap in argument order; this is intentional
        return Cytoscape.getCurrentNetwork().getNodeAttributeValue(node, attributeName);
    }
    public void setNodeAttributeValue(String attributeName, String nodeName, Object value) {
        CyNode node = Cytoscape.getCyNode(nodeName, false);
        if (node == null) {return;}
        //note swap in argument order; this is intentional
        Cytoscape.getCurrentNetwork().setNodeAttributeValue(node, attributeName, value);
    }
    public void setNodeAttributeValue(String attributeName, String nodeName, double value) {
        CyNode node = Cytoscape.getCyNode(nodeName, false);
        if (node == null) {return;}
        //note swap in argument order; this is intentional
        Cytoscape.getCurrentNetwork().setNodeAttributeValue(node, attributeName, new Double(value));
    }
    
    public List getSelectedNodeNames() {
        Set nodeSet = Cytoscape.getCurrentNetwork().getFlagger().getFlaggedNodes();
        List nameList = new ArrayList();
        for (Iterator iter = nodeSet.iterator(); iter.hasNext(); ) {
            CyNode node = (CyNode)iter.next();
            String name = (String)Cytoscape.getCurrentNetwork().getNodeAttributeValue (
                                        node, Semantics.CANONICAL_NAME);
            nameList.add(name);
        }
        return nameList;
    }
    public void selectNodesByName(String[] nameArray) {
        selectNodesByName(Arrays.asList(nameArray));
    }
    public void selectNodesByName(List nameList) {
        for (Iterator iter = nameList.iterator(); iter.hasNext(); ) {
            String name = (String)iter.next();
            CyNode node = Cytoscape.getCyNode(name, false);
            if (node != null) {
                Cytoscape.getCurrentNetwork().getFlagger().setFlagged(node, true);
            }
        }
    }
    public void redrawGraph(boolean doLayout, boolean applyAppearances) {
        Cytoscape.getCurrentNetworkView().redrawGraph(doLayout, applyAppearances);
    }
    public void setInteractivity(boolean newState) {}
    
}

