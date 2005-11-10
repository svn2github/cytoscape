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
import cytoscape.util.*;
import giny.model.*;

public class LoaderPlugin extends CytoscapePlugin {

  public LoaderPlugin () {
    initialize();
  }

  protected void initialize () {


    // get command line files
    String[] args = CytoscapeInit.getArgs();
    boolean inFiles = false;
    ArrayList files = new ArrayList(); 
    for ( int i = 0; i < args.length; ++i ) {
      if ( args[i].startsWith( "-ss" ) ) {
        inFiles = true;
      }  else if ( args[i].startsWith("-") && inFiles ) {
        inFiles = false;
      } else if ( inFiles ) {
        files.add( args[i] );
      }
    }
    // load files
    for ( Iterator i = files.iterator(); i.hasNext(); ) {
      String file = (String)i.next();
      System.out.println( "Laoding Spreadsheet File: "+file );
      Import.loadFileToNetwork( file, "\t" );
    }
    Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );



    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Load").add( new JMenuItem ( new AbstractAction( "Load Network" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                FileLoaderUI ui = new FileLoaderUI();
                Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );
              }
            } ); } } ) );

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Save").add( new JMenuItem ( new AbstractAction( "Save Network" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                final String name;
                 try {
                   name = FileUtil.getFile( "Save Network",
                                            FileUtil.SAVE,
                                            new CyFileFilter[] {} ).toString();
                 } catch ( Exception exp ) {
                   // this is because the selection was canceled
                   return;
                 }
                 Export.saveNetworkToFile( Cytoscape.getCurrentNetwork(), name );
                 Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED,null, null );
              }
            } ); } } ) );
    

  }


}
