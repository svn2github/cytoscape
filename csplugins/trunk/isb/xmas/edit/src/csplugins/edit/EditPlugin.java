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

  private static UndoManager undo;
  
  private static UndoAction undoAction;
  private static RedoAction redoAction;

  private static Cut cut;
  private static Copy copy;
  private static Paste paste;
  


  public EditPlugin () {
    initialize();
  }

  protected void initialize () {
    
    undo = new UndoManager();
    undoAction = new UndoAction();
    redoAction = new RedoAction();
    
    JMenuItem undoItem = new JMenuItem( undoAction );
    JMenuItem redoItem = new JMenuItem( redoAction );

    undoItem.setAccelerator(  javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Z,
                                                                  ActionEvent.CTRL_MASK ) );
    redoItem.setAccelerator(  javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Y,
                                                                  ActionEvent.CTRL_MASK ) );

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Edit" ).add( undoItem );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Edit" ).add( redoItem );

    cut = new Cut(null);
    Cytoscape.getDesktop().getCyMenus().addAction( cut );
    copy = new Copy(null);
    Cytoscape.getDesktop().getCyMenus().addAction( copy );
    paste = new Paste(null);
    Cytoscape.getDesktop().getCyMenus().addAction( paste );

  }

  public static void addEdit ( UndoableEdit edit ) {
    undo.addEdit( edit );
    undoAction.update();
    redoAction.update();
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

  protected class UndoAction extends AbstractAction {
    public UndoAction() {
	    super("Undo");
	    setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
	    try {
        undo.undo();
	    } catch (CannotUndoException ex) {
        System.out.println("Unable to undo: " + ex);
        ex.printStackTrace();
	    }
	    update();
	    redoAction.update();
    }

    protected void update() {
	    if(undo.canUndo()) {
        setEnabled(true);
        putValue(Action.NAME, undo.getUndoPresentationName());
	    }
	    else {
        setEnabled(false);
        putValue(Action.NAME, "Undo");
	    }
    }
  }

  protected class RedoAction extends AbstractAction {
    public RedoAction() {
	    super("Redo");
	    setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
	    try {
        undo.redo();
	    } catch (CannotRedoException ex) {
        System.out.println("Unable to redo: " + ex);
        ex.printStackTrace();
	    }
	    update();
	    undoAction.update();
    }

    protected void update() {

      System.out.println( "REDO: "+undo.canRedo() );

	    if(undo.canRedo()) {
        setEnabled(true);
        putValue(Action.NAME, undo.getRedoPresentationName());
	    }
	    else {
        setEnabled(false);
        putValue(Action.NAME, "Redo");
	    }
    }
  }
  
  
}
