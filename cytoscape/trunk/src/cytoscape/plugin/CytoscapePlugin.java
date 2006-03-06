
/*
  File: CytoscapePlugin.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.plugin;

import cytoscape.*;

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
   * If true, this plugin is capable if accepting scripts, and we 
   * will find out what its script name is
   */
  public boolean isScriptable () {
    return false;
  }

  /**
   * If this plugin is scriptable, then this will return a unique
   * script name, that will come after the colon like:
   * :name
   */
  public String getScriptName () {
    return "default";
  }

  /**
   * Take a CyNetwork as input along with some arguments, and return a CyNetwork,
   * which can be the same, or different, it doesn't really matter, and is 
   * up to the individual plugin.
   */
  public CyNetwork interpretScript ( String[] args, CyNetwork network ) {
    return null;
  }

  /**
   * If implemented, then this plugin will be activated after being initialized
   */
  public void activate () {
  }

  /**
   * If implemented then this plugin can remove itself from the Menu system, and
   * anything else, when the user decides to deactivate it.
   */
  public void deactivate () {
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

    System.out.println( "Loading: "+pluginClass );

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


