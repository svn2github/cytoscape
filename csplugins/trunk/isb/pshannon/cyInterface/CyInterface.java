package csplugins.isb.pshannon.cyInterface;

import java.util.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.Action;
/**
 * This interface provides methods used by the DataCube plugin that are implemented
 * by Cytoscape classes. This interface insulates the plugin from changes
 * in the Cytoscape API.
 */
public interface CyInterface {
    
    public JFrame getMainFrame();
    public String[] getArguments();
    public void addPluginMenuAction(Action action);
    public void addFileLoadMenuAction(Action action);
    
    public String[] getNodeAttributeNames();
    public Object getNodeAttributeValue(String attributeName, String nodeName);
    public void setNodeAttributeValue(String attributeName, String nodeName, Object value);
    public void setNodeAttributeValue(String attributeName, String nodeName, double value);
    
    public List getSelectedNodeNames();
    public void selectNodesByName(String[] names);
    public void selectNodesByName(List names);
    public void redrawGraph(boolean doLayout, boolean applyAppearances);
    public void setInteractivity(boolean newState);
    
}

