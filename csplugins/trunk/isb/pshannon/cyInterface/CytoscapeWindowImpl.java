package csplugins.isb.pshannon.cyInterface

import java.util.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import y.base.*;

import cytoscape.CytoscapeWindow;

/**
 * This class implements the interface between the DataCube plugin and Cytoscape
 * assuming Cytoscape version 1.1.1, with CytoscapeWindow as the main class.
 */
public class CytoscapeWindowImpl implements CyDataCubeInterface {
    
    CytoscapeWindow cytoscapeWindow;
    
    public CytoscapeWindowImpl(CytoscapeWindow cytoscapeWindow) {
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public JFrame getMainFrame() {return cytoscapeWindow.getMainFrame();}
    public String[] getArguments() {return cytoscapeWindow.getConfiguration().getArgs();}
    public void addPluginMenuAction(Action action) {
        cytoscapeWindow.getOperationsMenu().add(action);
    }
    
    public void addFileLoadMenuAction(Action action) {
          JMenuBar menubar = cytoscapeWindow.getMenuBar ();

          boolean foundLoadMenu = false;
          for (int i=0; i < menubar.getMenuCount (); i++) {
              JMenu menu = menubar.getMenu (i);
              String menuName = menu.getText ();
              if (menuName.equalsIgnoreCase ("File")) {
                  for (int j=0; j < menu.getItemCount (); j++) {
                      JMenuItem item = menu.getItem (j);
                      if (item.isEnabled () && item.getText().equalsIgnoreCase ("Load")) {
                          JMenu loadMenu = (JMenu) item;
                          loadMenu.add (action);
                          foundLoadMenu = true;
                      }
                  } // for j
              }
          } // for i
          
          if (!foundLoadMenu) {
              String msg = "Could not find 'File->Load' menu, to add 'Load Data Matrix...' menu";
              JOptionPane.showMessageDialog (null, msg, "Error!", JOptionPane.ERROR_MESSAGE);
          }
    }

    public String[] getNodeAttributeNames() {
        return cytoscapeWindow.getNodeAttributes().getAttributeNames();
    }
    public Object getNodeAttributeValue(String attributeName, String nodeName) {
        return cytoscapeWindow.getNodeAttributes().get(attributeName, nodeName);
    }
    public void setNodeAttributeValue(String attributeName, String nodeName, Object value) {
        cytoscapeWindow.getNodeAttributes().set(attributeName, nodeName, value);
    }
    public void setNodeAttributeValue(String attributeName, String nodeName, double value) {
        cytoscapeWindow.getNodeAttributes().set(attributeName, nodeName, new Double(value));
    }
    
    
    public List getSelectedNodeNames() {
        ArrayList canonicalNameList = new ArrayList ();
        NodeCursor nc = cytoscapeWindow.getGraph().selectedNodes();
        while (nc.ok ()) {
            Node node = nc.node ();
            String nodeName = cytoscapeWindow.getNodeAttributes().getCanonicalName (node);
            canonicalNameList.add (nodeName);
            nc.next ();
        } // while
        return canonicalNameList;
    }
    
    public void selectNodesByName(String[] names) {
        cytoscapeWindow.selectNodesByName(names);
    }
    public void selectNodesByName(List names) {
        String[] nameArray = (String[])names.toArray(new String[0]);
        selectNodesByName(nameArray);
    }
            
    public void redrawGraph(boolean doLayout, boolean applyAppearances) {
        cytoscapeWindow.redrawGraph(doLayout, applyAppearances);
    }
    public void setInteractivity(boolean newState) {
        cytoscapeWindow.setInteractivity(newState);
    }
    
}

