package fileloader;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.readers.*;
import cytoscape.plugin.*;
import cytoscape.data.*;

import giny.model.*;

public class LoaderPlugin extends CytoscapePlugin {

  public LoaderPlugin () {
    initialize();
  }

  protected void initialize () {

    String[] args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    for ( int i = 0; i < args.length; ++i ) {
      if ( args[i].startsWith( "-ss" ) ) {
        i++;
        FileLoader.loadFileToAttributes( args[i], true, true, "\t" );
      } else if ( args[i].startsWith( "-edge" ) ) {
        i++;
        FileLoader.loadFileToAttributes( args[i], false, true, "\t" );
      }
    }




    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Load").add( new JMenuItem ( new AbstractAction( "Load Spread Sheet" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                FileLoaderUI ui = new FileLoaderUI();
              }
            } ); } } ) );


    
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Load").add( new JMenuItem ( new AbstractAction( "Load Cytoscape Project" ) {
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
                FileLoader.loadCytoscape( file.toString() );
              }
            } ); } } ) );



    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Save").add( new JMenuItem ( new AbstractAction( "Save Cytoscape Project" ) {
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
                FileLoader.saveCytoscape( file.toString() );
              }
            } ); } } ) );


  }


}
