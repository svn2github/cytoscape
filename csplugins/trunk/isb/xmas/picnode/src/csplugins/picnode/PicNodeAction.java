package csplugins.picnode;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.beans.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import phoebe.*;

import giny.model.*;
import giny.view.*;

public class PicNodeAction {

  public PicNodeAction () {}

  public static JMenuItem setImage ( Object[] args, PNode node ) {
    final CyNetworkView net_view = ( CyNetworkView )args[0];
    final NodeView nv = ( NodeView )node; 

     return  new JMenuItem( new AbstractAction( "Set Image" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {

                File file = null;
                File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
                JFileChooser chooser = new JFileChooser(currentDirectory);
                if ( chooser.showOpenDialog( Cytoscape.getDesktop() ) == 
                     chooser.APPROVE_OPTION) {
                  currentDirectory = chooser.getCurrentDirectory();
                  Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
                  file = chooser.getSelectedFile();
                }

                if ( nv instanceof PicNode ) {
                  ( ( PicNode )nv ).setImage( file.getAbsolutePath() );
                } else if ( nv instanceof PNodeView ) {
                   PicNode pic = new PicNode(  nv.getRootGraphIndex(),
                                                ( PGraphView )net_view );
                  
                   pic.setImage( file.getAbsolutePath() );
                }
             
              
              } } ); } } );
  }


}

