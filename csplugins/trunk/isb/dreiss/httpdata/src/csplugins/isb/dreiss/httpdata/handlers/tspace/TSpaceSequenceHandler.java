package csplugins.isb.dreiss.httpdata.handlers.tspace;

import java.util.*;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;

import lights.*;
import lights.interfaces.*;

import csplugins.isb.dreiss.httpdata.*;

/**
 * Class <code>TSpaceSequenceHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 *
 *  tuples are structured like this:
 *
 *      0) entity (typically gene or protein) name
 *      1) species (we recommend formal Linaean genus/species nomenclature)
 *      2) type: DNA, RNA, protein, upstream, others...? (protein is default)
 *      3) sequence: a string of characters (typically DNA or amino acids)
 */
public class TSpaceSequenceHandler extends TSpaceDataHandler {
   public static final String serviceName = "sequence";

   public TSpaceSequenceHandler (String tspaceName) throws Exception {
      super( tspaceName );
   }

   public boolean put (String name, String species, String sequence ) throws Exception {
      return put( name, species, sequence, "protein" );
   }

   public boolean put (String name, String species, String sequence,
		       String type) throws Exception {
      String result = null;
      if (contains (name, species, type)) delete (name, species, type);

      ITuple tuple = createTuple( new Object[] { name, species, type, sequence } );
      tspace.out (tuple);
      changed = true;
      return true;
   } // put

   public boolean contains (String name, String species) throws Exception {
      return contains (name, species, "protein");
   } // contains

   public boolean contains (String name, String species, String type) throws Exception {
      ITuple template = createTuple( new Object[] { name, species,
						   type, String.class } );
      int count = tspace.count (template);
      return (count >= 1);
   } // contains

   public String get (String name, String species) throws Exception {
      return get( name, species, "protein" );
   }

   public String get (String name, String species, String type) throws Exception {
      String result = null;
      ITuple completedTemplate = createTuple( new Object[] { name, species,
							     type, String.class } );
      int count = tspace.count (completedTemplate);
      if (count >= 1) {
	 ITuple[] tuples = tspace.rdg (completedTemplate);
	 if (tuples != null && tuples.length > 0) {
	    ITuple tuple = (ITuple) tuples[ 0 ];
	    result = (String) tuple.get(3).getValue();
	 }
      } // if count >= 1
      return result;
   } // get

   public boolean delete (String name, String species) throws Exception {
      ITuple wildcardTemplate = createTuple( new Object[] { name, species, String.class, 
				     String.class } );
      tspace.ing(wildcardTemplate);
      changed = true;
      return true;
   }

   public boolean delete (String name, String species, String type) throws Exception {
      ITuple wildcardTemplate = createTuple( new Object[] { name, species, type, 
				     String.class } );
      tspace.ing (wildcardTemplate);
      changed = true;
      return true;
   }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.tspace.TSpaceSequenceHandler <port> <spaceName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.tspace.TSpaceSequenceHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
