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
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
public class SBMLPlugin extends CytoscapePlugin {

  public SBMLPlugin () {
    initialize();
  }

  protected void initialize () {


    String[] cliargs = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    


   
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
  }


}
