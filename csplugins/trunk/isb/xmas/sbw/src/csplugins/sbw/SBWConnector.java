package csplugins.sbw;

import edu.caltech.sbw.*; 
import java.util.Hashtable;

public class SBWConnector {

  ModuleImpl moduleImpl;

  public SBWConnector () {
   
  }


  public ModuleImpl getModuleImpl () {
    if ( moduleImpl == null ) {
       // name the Module
      try {
        moduleImpl = new ModuleImpl("Cytoscape SBW Module");
      }  catch (SBWException sbwe) {
        System.out.println( "Error Initializing Module" );
        sbwe.handleWithDialog();
      }
    }
    return moduleImpl;
  }

  /**
   * Register with the Broker
   */
  public void register () {
    try {
      moduleImpl = getModuleImpl();

      // we will be "csplugins_sbw.cytoscape" in Python.
      // Category "Network Analysis"
      moduleImpl.addService( "cytoscape", "Network Analysis", SBWProvider.class );

      String[] args = new String[1];
      args[0] = "-sbwregister";
      moduleImpl.run(args);
  
    } 

    catch (SBWException sbwe) {
      sbwe.handleWithDialog();
    }

  }

  /**
   * Initialize the connection to the SBW Broker.
   */
  public void connect () {

    try {
      Class classs = Class.forName( "csplugins.sbw.SBWConnector" );
      System.out.println( "Class Made: "+classs );
    } catch ( Exception e ) {
      e.printStackTrace();
    }


    try {

      // if ( moduleImpl == null ) {
      moduleImpl = new ModuleImpl("Cytoscape SBW Module");
      // }
      // we will be "csplugins_sbw.cytoscape" in Python.
      // Category "Network Analysis"
      moduleImpl.addService( "Cytoscape", "Network Analysis", csplugins.sbw.SBWProvider.class );

      String[] args = new String[1];
      args[0] = "-sbwmodule";
      moduleImpl.run(args);
  
      System.out.println( "Mod IMpl: "+moduleImpl );


    } 

    catch (SBWException sbwe) {
      sbwe.handleWithDialog();
      System.out.println( "Error Caught\n"+sbwe.getDetailedMessage() );
    }

  }

}


