//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.plugin;

import java.lang.reflect.Constructor;



/**
 * A CytoscapePlugin is the new "Global" plugin. A CytoscapePlugin constructor
 * does not have any arguments, since it is Network agnostic.  Instead all
 * access to the Cytoscape Data Structures is handled throught the static
 * methods provided by cytoscape.Cytoscape.
 *
 * It is encouraged, but not mandatory, for plugins to override the
 * {@link#describe describe} method to state what the plugin does and how it
 * should be used.
 */
public abstract class CytoscapePlugin {

  /**
   * There are no arguments required or allowed in a CytoscapePlugin
   * constructor.
   */
  public CytoscapePlugin () { 
  }


  /**
   * method returning a String description of the plugin.
   */
  public String describe () { 
    return new String("No description."); 
  }

  /**
   * Attempts to instantiate a plugin of the class defined by the
   * first argument. The other arguments to this method are used as
   * possible arguments for the plugin constructor. This method searches
   * for a constructor of a known type in the plugin class and then
   * attempts to use that constructor to create an instance of the plugin.
   *
   *
   * @return true if the plugin was successfulyl constructed, false otherwise
   */
  public static boolean loadPlugin ( Class pluginClass ) {
       
    //System.out.print( " Loading plugin from class: "+pluginClass +" ...");


    if ( pluginClass == null ) {
      return false;
    }
    //System.out.println( " which wasn't null " );
    
    Object object = null;
      try {
        object = pluginClass.newInstance();
        //System.out.println( "Plugin Class: "+object.getClass() );
      } catch (InstantiationException e) {
        System.out.println( "InstantiationException");
          System.out.println(e);
          e.printStackTrace();
      } catch (IllegalAccessException e) {
        System.out.println( "IllegalAccessException");
          System.out.println(e);
          e.printStackTrace();
      } // catch (ClassNotFoundException e) {
//           System.out.println(e);
//       }

      if ( object == null ) {
        System.out.println( "Instantiation seems to have failed" );
      }

      return true;


   //  // there are no argruments allowed in the constructor
//     // so we just request the no argrument Constructor.
//     Constructor ctor = null;
//     try {
//       Class[] argClasses = new Class[] {};
//       ctor = pluginClass.getConstructor( argClasses );
//     } catch ( SecurityException se ) {
//       System.err.println("In CytoscapePlugin.loadPlugin: a Constructor could not be found");
//       System.err.println( se.getMessage() );
//       se.printStackTrace();
//       return false;
//     } catch ( NoSuchMethodException nsme ) {
//       //ignore, there are other constructors to look for
//     }
//     if (ctor != null) {
//       try {
//         Object[] args = new Object[] {};
//         return ctor.newInstance( args ) != null;
//       } catch (Exception e) {
//         System.err.println("In CytoscapePlugin.loadPlugin:");
//         System.err.println("Exception while constructing plugin instance:");
//         System.err.println(e.getMessage());
//         e.printStackTrace();
//         return false;
//       }
//     }
//     return false;
  }
}


