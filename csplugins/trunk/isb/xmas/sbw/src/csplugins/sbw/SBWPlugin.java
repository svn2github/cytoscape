package csplugins.sbw;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import javax.swing.*;

import edu.caltech.sbw.*;

/**
 * The Cytoscape Plugin for interacting with
 * SBW modules
 */
public class SBWPlugin extends AbstractPlugin {
  
  static CyWindow window;
  SBWConnector connector;
  ModuleImpl moduleImpl;

  public SBWPlugin ( CyWindow window ) {
    this.window = window;
    initialize();
  }

  public SBWPlugin () {
  }
  

  public void initialize () {

   //   ImageIcon tool =  new ImageIcon( getClass().getResource("/sbw36.gif") );
//      ImageIcon menu =  new ImageIcon( getClass().getResource("/sbw16.gif") );

//      ToolSBW toolsbw = new ToolSBW( this, tool );
//      MenuSBW menusbw = new MenuSBW( this, menu );
    
//      window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )toolsbw );
//      window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )menusbw );
    
    
    //  try {

//        moduleImpl = new ModuleImpl("Cytoscape SBW Module");
      
//        // we will be "csplugins_sbw.cytoscape" in Python.
//        // Category "Network Analysis"
//        moduleImpl.addService( "Cytoscape", "Network Analysis", csplugins.sbw.SBWProvider.class );
       
//        String[] args = new String[1];
//        args[0] = "-sbwmodule";
//        moduleImpl.run(args);
       
//        System.out.println( "Mod IMpl: "+moduleImpl );
     
//      } 

//     catch (SBWException sbwe) {
//       sbwe.handleWithDialog();
//       System.out.println( "Error Caught\n"+sbwe.getDetailedMessage() );
//     }

  
    try {


      moduleImpl = new ModuleImpl("csplugins.sbw",
                                  "Cytoscape SBW Module",
                                  ModuleImpl.SELF_MANAGED,
                                  CyMain.class,
                                  "Cytoscape SBW Connection Module");

      // we will be "csplugins_sbw.cytoscape" in Python.
      // Category "Network Analysis"
      
      moduleImpl.addService( "Cytoscape",
                             "Cytoscape",
                             "Network Analysis", 
                             new SBWProvider( window ),
                             "Provides a General API to Cytoscape netowork and data." );


      moduleImpl.addService( "Network",
                             "Network",
                             "Network Analysis", 
                             window.getNetwork(),
                             "Provides a General API to Cytoscape netowork and data." );

      moduleImpl.addService( "Window",
                             "Window",
                             "Network Analysis", 
                             window,
                             "Provides a General API to Cytoscape netowork and data." );


      moduleImpl.addService( "GraphView",
                             "GraphView",
                             "Network Analysis", 
                             window.getView(),
                             "Provides a General API to Cytoscape netowork and data." );

      String[] args = new String[1];
      args[0] = "-sbwmodule";
      //args[0] = "-sbwregister";
      moduleImpl.run(args);
     } 

    catch (SBWException sbwe) {
      sbwe.handleWithDialog();
      System.out.println( "Error Caught\n"+sbwe.getDetailedMessage() );
    }



  }

  public SBWConnector getConnector () {
    if ( connector == null ) {
       connector = new SBWConnector();
    } 
    return connector;
  }


  public static CyWindow getCyWindow () {
    return window;
  }

  public String describe () {
    return "SBW Connection";
  }

}
