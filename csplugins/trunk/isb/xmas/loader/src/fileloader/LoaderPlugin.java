package fileloader;

import javax.swing.*;
import cytoscape.*;
import cytoscape.plugin.*;


public class LoaderPlugin extends CytoscapePlugin {

  public LoaderPlugin () {
    initialize();
  }

  protected void initialize () {

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Load").add( new JMenuItem ( new AbstractAction( "Load Spread Sheet" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                FileLoaderUI ui = new FileLoaderUI();
              }
            } ); } } ) );


  }


}
