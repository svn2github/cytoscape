package console;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;

import jline.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.beans.*;
import java.awt.event.*;

public class CytoscapeActionCompletor 
  implements Completor {
  
  public  CytoscapeActionCompletor () {
    System.out.println( "init completor" );
  }

  public int complete ( final String buf, 
                        final int cursor,
                        final List candidates ) {

    
    List actions = CytoscapeAction.getActionList();
    for ( Iterator i = actions.iterator(); i.hasNext(); ) {

      CytoscapeAction action = ( CytoscapeAction )i.next();
      candidates.add( action.getName() );

      System.out.println( "adding actions: "+action.getName() );

    }

    Collections.sort( candidates );
    return 0;

  }
  

}
