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

    String[] args = CytoscapeInit.getArgs();
    for ( int i = 0; i < args.length; ++i ) {
      if ( args[i].startsWith( "-ss" ) ) {
        i++;
        FileLoader.loadFileToNetwork( args[i], "\t" );
        Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );
      } 
    }


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
                 FileLoader.saveNetworkToFile( Cytoscape.getCurrentNetwork(), name );
                 Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED,null, null );
              }
            } ); } } ) );
    

  }


}
