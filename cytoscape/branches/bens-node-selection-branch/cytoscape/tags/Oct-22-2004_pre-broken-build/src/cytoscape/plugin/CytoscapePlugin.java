//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.plugin;

/**
 * A CytoscapePlugin is the new "Global" plugin. A CytoscapePlugin constructor
 * does not have any arguments, since it is Network agnostic.  Instead all
 * access to the Cytoscape Data Structures is handled throught the static
 * methods provided by cytoscape.Cytoscape.
 *
 * It is encouraged, but not mandatory, for plugins to override the
 * {@link #describe describe} method to state what the plugin does and how it
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
   * Attempts to instantiate a plugin of the class argument.
   *
   * @return true if the plugin was successfulyl constructed, false otherwise
   */
  public static boolean loadPlugin ( Class pluginClass ) {

    if ( pluginClass == null ) {
      return false;
    }

    Object object = null;
      try {
        object = pluginClass.newInstance();
      } catch (InstantiationException e) {
        System.out.println( "InstantiationException");
          System.out.println(e);
          e.printStackTrace();
      } catch (IllegalAccessException e) {
        System.out.println( "IllegalAccessException");
          System.out.println(e);
          e.printStackTrace();
      }
      if ( object == null ) {
        System.out.println( "Instantiation seems to have failed" );
      }

      return true;
  }
}


