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

public class ConsolePlugin 
  extends 
    CytoscapePlugin {

  ConsoleReader reader;
  List completors;

  int count = 0;

  public ConsolePlugin () {
   
      initialize();
   
  }


  protected void initialize ()  {

    

      
    Thread thread = new Thread () {

        public void run() {
          try {
            System.out.println( "INIT CONSOLE!!" );
            
            reader = new ConsoleReader ();
            
            reader.getHistory().setHistoryFile( CytoscapeInit.getConfigFile( "console.history" ) );

            completors = new LinkedList ();
            
            SimpleCompletor sc = new SimpleCompletor ( new String [] { "foo", "bar", "baz"});
            List actions = CytoscapeAction.getActionList();
            for ( Iterator i = actions.iterator(); i.hasNext(); ) {
              CytoscapeAction action = ( CytoscapeAction )i.next();
              sc.addCandidateString( action.getName() );
                            
            }
            completors.add ( sc );
            completors.add (new FileNameCompletor ());


            //completors.add ( new SimpleCompletor 
            //                 ( new String [] { "foo", "bar", "baz"}));
            //completors.add( new CytoscapeActionCompletor() );
            

            reader.addCompletor (new ArgumentCompletor (completors));
            
            
            String line;
            PrintWriter out = new PrintWriter (System.out);
            
            while ((line = reader.readLine ("prompt> ")) != null) {
              execute( line );
              out.println (count+"> "+ line );
              out.flush ();
              count++;
              // If we input the special word then we will mask
              // the next line.
            }
          } catch ( Exception e ) {}
        }  
        
      };

    thread.start();
    
  }

  private void execute ( String args ) {

    String[] argv = args.split(" ");

    System.out.println( "Execute: "+args );
    System.out.println( "Execute: "+argv[0]+" 2 "+argv[1] );

    boolean not_found = true;
    List actions = CytoscapeAction.getActionList();
    for ( Iterator i = actions.iterator(); i.hasNext() && not_found; ) {
      CytoscapeAction action = ( CytoscapeAction )i.next();
      System.out.println( "Compare: "+action.getName()+" "+argv[0] );
      if ( argv[0].equals(action.getName()) ) {
        action.takeArgs( new String[] {argv[1]} );
        not_found = false;
      }
      
    }

  }



}
