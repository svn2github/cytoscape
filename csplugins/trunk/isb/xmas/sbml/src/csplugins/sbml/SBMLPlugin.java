package csplugins.sbml;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.List;
import giny.view.*;
import edu.umd.cs.piccolo.PNode;
import javax.swing.*;
import phoebe.*;

import csplugins.metabolic.Reaction;

public class SBMLPlugin extends CytoscapePlugin {

  public SBMLPlugin () {
    initialize();
  }

  protected void initialize () {

    ArrayList sbml_files = new ArrayList();


    String[] args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();

    boolean in_sbml = false;
    for ( int i = 0; i < args.length; ++i ) {
      if ( in_sbml && !args[i].startsWith("-") ) {
        sbml_files.add( args[i] );
      } else if ( in_sbml && args[i].startsWith("-") ) {
        in_sbml = false;
        i = args.length;
      } else if ( !in_sbml && ( args[i].startsWith("--SBML") || args[i].startsWith("--sbml") 
                                || args[i].startsWith("-SBML") || args[i].startsWith("-sbml") ) ) {
        in_sbml = true;
      }
    }
      


    for ( Iterator i = sbml_files.iterator(); i.hasNext(); ) {
      String file = ( String )i.next();
      System.out.println( "Loading: "+ file );
       LibSBML.loadSBML( file );
    }

   
    JMenuItem load = new JMenuItem ( new AbstractAction( "Load SBML File" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
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
                LibSBML.loadSBML( file.toString() );
              }
            } ); } } );
  
  Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Load").add( load );
    

  JMenuItem save = new JMenuItem ( new AbstractAction( "Save SBML File" ) {
      public void actionPerformed ( java.awt.event.ActionEvent e ) {
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
                //LibSBML.saveSBML( file.toString() );
              }
            } ); } } );
  
  Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Save").add( save );

   JMenuItem layout = new JMenuItem ( new AbstractAction( "Layout kegg2sbml" ) {
      public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                CyNetwork network = Cytoscape.getCurrentNetwork();
                CyNetworkView view = Cytoscape.getCurrentNetworkView();

                Iterator nodes_i = network.nodesIterator();
                while ( nodes_i.hasNext() ) {
                  CyNode node = ( CyNode )nodes_i.next();
                  if ( node instanceof Reaction ) {

                    Iterator r_i = ( ( Reaction )node ).getReactantList().iterator();
                    Iterator p_i = ( ( Reaction )node ).getProductList().iterator();

                    int count = 0;
                    double x = 0;
                    double y = 0;

                    while ( r_i.hasNext() ) {
                      NodeView nv = view.getNodeView( ( CyNode )r_i.next() );
                      count++;
                      x+= nv.getXPosition();
                      y+= nv.getYPosition();
                    }

                    while ( p_i.hasNext() ) {
                      NodeView nv = view.getNodeView( ( CyNode )p_i.next() );
                      count++;
                      x+= nv.getXPosition();
                      y+= nv.getYPosition();
                    }
                    
                    NodeView nv = view.getNodeView( node );
                    nv.setXPosition( x/count );
                    nv.setYPosition( y/count );
                    nv.setNodePosition( true );

                  } else {
                    Double x = new Double( Cytoscape.getNodeAttributeValue( node, "cd:x" ).toString() );
                    Double y = new Double( Cytoscape.getNodeAttributeValue( node, "cd:y" ).toString() );
                    Double w = new Double( Cytoscape.getNodeAttributeValue( node, "cd:w" ).toString() );
                    Double h = new Double( Cytoscape.getNodeAttributeValue( node, "cd:h" ).toString() );
                    
                    NodeView nv = view.getNodeView( node );
                    nv.setXPosition( x.doubleValue() );
                    nv.setYPosition( y.doubleValue() );
                    nv.setWidth( w.doubleValue() );
                    nv.setHeight( h.doubleValue() );
                    nv.setNodePosition( true );

                  }
                }
              }//run
            } ); } } );
  
  Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add( layout );

  }


}
