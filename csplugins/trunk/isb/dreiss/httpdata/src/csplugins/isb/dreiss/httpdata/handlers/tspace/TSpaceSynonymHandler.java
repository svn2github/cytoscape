package csplugins.isb.dreiss.httpdata.handlers.tspace;
import java.util.*;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;

import lights.*;
import lights.interfaces.*;

import csplugins.isb.dreiss.httpdata.*;

/**
 * Class <code>TSpaceSynonymHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class TSpaceSynonymHandler extends TSpaceDataHandler {
   public static final String serviceName = "synonym";

   //  the tuple used in this space has the following fields:
   //     one name for a bioentity
   //     a second name for the bioentity
   //     species
   // 	  is the second name the "preferred" synonym for the first name

   public TSpaceSynonymHandler (String tspaceName) throws Exception {
      super( tspaceName );
   }

   public boolean put ( String name1, String name2, String species,
			boolean isPreferred ) throws Exception {
      String result = null;

      // remove any previous (commonName, canonicalName, species) tuple
      ITuple remove = createTuple( new Object[] { name1, name2, species,
						  Boolean.class } );
      tspace.ing (remove);
      changed = true;

      // finally, add the new information:
      ITuple tuple = createTuple ( new Object[] { name1, name2, species,
						  new Boolean( isPreferred ) } );
      tspace.out (tuple);
      changed = true;
      return true;
   } // put

   public boolean put( String name1, String name2, String species ) throws Exception {
      return put (name1, name2, species, false);
   } // put

   public Vector getAllSynonyms( String name1, String species ) throws Exception {
      ITuple template = createTuple( new Object[] { name1, String.class, species,
						    Boolean.class } );
      Vector list = new Vector ();
      ITuple tupleSet[] = tspace.rdg (template);
      if (tupleSet != null && tupleSet.length > 0) {
	 for (int t=0; t < tupleSet.length; t++) 
	    list.add( (String) tupleSet[ t ].get(1).getValue() );
      }
      return list;
   }

   public String getSynonym( String name1, String species ) throws Exception {
      ITuple template1 = createTuple( new Object[] { name1, String.class, species,
						     Boolean.class});
      ITuple template2 = createTuple( new Object[] { name1, String.class, species,
						     new Boolean (true)});

      String result = name1; // the fallback: return name1 if no synonyms are discovered
      ITuple tuple = tspace.rdp( template2 );
      if ( tuple != null ) { // First try to get a preferred synonym
	 result = (String) tuple.get (1).getValue ();
      } else { // if none exist, just get any synonym
	 tuple = tspace.rdp( template1 );
	 if ( tuple != null ) result = (String) tuple.get (1).getValue ();
      } // end of else
      return result;
   }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.tspace.TSpaceSynonymHandler <port> <spaceName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.tspace.TSpaceSynonymHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
} // class TSpaceSynonymFetcher
