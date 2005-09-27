package csplugins.edit;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cern.colt.list.*;

import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;
public class EditPlugin extends CytoscapePlugin {

  private static IntArrayList nodeClipBoard;
  private static IntArrayList edgeClipBoard;
  private static String networkClipBoard;

  private static Cut cut;
  private static Copy copy;
  private static Paste paste;
  


  public EditPlugin () {
    initialize();
  }

  protected void initialize () {
    
    ImageIcon cuticon = new ImageIcon( getClass().getResource( "/editcut.png" ) );
    ImageIcon copyicon = new ImageIcon( getClass().getResource( "/editcopy.png" ) );
    ImageIcon pasteicon = new ImageIcon( getClass().getResource( "/editpaste.png" ) );
 
    cut = new Cut(cuticon);
    Cytoscape.getDesktop().getCyMenus().addAction( cut );
    copy = new Copy(copyicon);
    Cytoscape.getDesktop().getCyMenus().addAction( copy );
    paste = new Paste(pasteicon);
    Cytoscape.getDesktop().getCyMenus().addAction( paste );

  }

 
  public static String getNetworkClipBoard () {
    return networkClipBoard;
  }

  public static void setNetworkClipBoard ( String id) {
    networkClipBoard = id;
  }

  public static IntArrayList getNodeClipBoard () {
    if ( nodeClipBoard == null )
      nodeClipBoard = new IntArrayList();
    return nodeClipBoard;
  }
  
  public static IntArrayList getEdgeClipBoard () {
    if ( edgeClipBoard == null )
      edgeClipBoard = new IntArrayList();
    return edgeClipBoard;
  }

  
}
