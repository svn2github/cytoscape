package csplugins.mac;

import com.apple.eawt.*;
import com.apple.eio.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import javax.swing.*;

public class MacPlugin extends CytoscapePlugin implements ApplicationListener {


  Application app;
  
  public MacPlugin () {

    this.app = Application.getApplication();
    app.addApplicationListener( this );

  }


  public void handleAbout ( ApplicationEvent event ) {
   //  JFrame frame = new JFrame( "About Cytoscape" );
//     frame.getContentPane().add( new JLabel( "<HTML><BIG>Cytoscape 2.0</big><br><br>Macing done by Rowan</HTML>" ) );
//     frame.pack();
//     frame.setVisible( true );
  }

  public void handleOpenApplication ( ApplicationEvent event ) {
  }
 

  public void handleOpenFile ( ApplicationEvent event ) {
    String file = event.getFilename();
   
    CyNetwork newNetwork = Cytoscape.createNetworkFromFile( file );
    
  
    
    if ( newNetwork.getNodeCount() < 500 ) {
      Cytoscape.createNetworkView( newNetwork );
    }
        
  } 
 

  public void handlePreferences ( ApplicationEvent event ) {
  }
 

  public void handlePrintFile ( ApplicationEvent event ) {
  }
 
  public void handleQuit ( ApplicationEvent event ) {
    Cytoscape.getCytoscapeObj().getParentApp().exit(0);
  }

  public void handleReOpenApplication ( ApplicationEvent event ){
  }

    


}
