package csplugins.sbw;

import java.awt.event.*;
import javax.swing.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

import ViolinStrings.*;
import java.util.*;
import edu.caltech.sbw.*; 

import giny.model.*;


public class SBWProvider {

  CyWindow window;
  
  /**
   * The helpMap stores usage info for each available command
   */
  Map helpMap;

  
  public SBWProvider ( CyWindow window ) {
    this.window = window;
    initialize();
  }

  protected void initialize () {

    // create and Populate the helpMap
    helpMap = new HashMap();
    
    helpMap.put( "displayString", "displayString( String )\n\ts -- The given String\n\treturn -> the given string with \"OK!!!???\" appended to the end.");


  }


  public void connect ( String host_module ) {
    try{
      SBW.getModuleInstance( host_module );
    } catch ( SBWException e ) {
      System.out.println( "Connection Failed\n"+e.getDetailedMessage() );
     
    }
  }

  public void link ( String host_module ) {
    try{
      SBW.link( host_module );
    } catch ( SBWException e ) {
      System.out.println( "Link Connection Failed\n"+e.getDetailedMessage() );
     
    }
  }
  

  public String displayString ( String s ) {
    System.out.println( s );
    return s+"OK!!!???";
  }

  /**
   * Returns a string with all available commands listed.
   */
  public List help () {
    
    return new ArrayList( helpMap.entrySet() );

  }

  /**
   * Returns help about a given command
   */
  public String help ( String command ) {

    if ( !helpMap.containsKey( command ) ) {
      return "Command Not Found.";
    }
    return ( String )helpMap.get( command );

  }

  public void refreshView () {
    window.redrawGraph( false, false );
    window.getView().fitContent();
  }

  public int[] getNodeIndicesArray () {
    return window.getNetwork().getGraphPerspective().getNodeIndicesArray();
  }
  
  public int[] getEdgeIndicesArray () {
     return window.getNetwork().getGraphPerspective().getEdgeIndicesArray();
  }
  
  public List getNodesList () {

    window = SBWPlugin.getCyWindow();
    List list =  window.getNetwork().getGraphPerspective().nodesList();

    for ( int i = 0; i <list.size(); ++i ) {
       list.set(i, ( ( Node )list.get(i) ).getIdentifier() );
    }
    return list;
  }

  public List getEdgesList () {

    window = SBWPlugin.getCyWindow();
    List list =  window.getNetwork().getGraphPerspective().edgesList();

    for ( int i = 0; i <list.size(); ++i ) {
       list.set(i, ( ( Edge )list.get(i) ).getIdentifier() );
    }
    return list;
  }

  public double getNodeXPosition ( int node ) {
    return window.getView().getNodeView( node ).getXPosition();
  }

  public void setNodeXPosition ( int node, double new_x ) {
    window.getView().getNodeView( node ).setXPosition( new_x );
  }

  public double getNodeYPosition ( int node ) {
    return window.getView().getNodeView( node ).getYPosition();
  }

  public void setNodeYPosition ( int node, double new_y ) {
    window.getView().getNodeView( node ).setYPosition( new_y );
  }
 
  


}
