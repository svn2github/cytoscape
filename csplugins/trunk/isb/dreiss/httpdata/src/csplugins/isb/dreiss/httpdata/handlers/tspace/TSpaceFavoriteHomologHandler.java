package csplugins.isb.dreiss.httpdata.handlers.tspace;
import java.util.*;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;

import lights.*;
import lights.interfaces.*;

import csplugins.isb.dreiss.httpdata.handlers.*;

/**
 * Class <code>TSpaceFavoriteHomologHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class TSpaceFavoriteHomologHandler extends TSpaceDataHandler {
   public static final String serviceName = "favoriteHomolog";

   //  the tuple used in this space has the following 5 String fields:
   //     the human user
   //     species of the source sequence
   //     name of the source sequence
   //     species of the homologous sequence
   //     name of the homologous sequence

   public TSpaceFavoriteHomologHandler( String tsname ) {
      super( tsname );
   }

   public boolean add (String user, String sourceSpecies, String sourceSequenceName,
		    String targetSpecies, String targetSequenceName) 
                             throws Exception {
      ITuple template = createTuple(new Object[] { user, sourceSpecies, 
						   sourceSequenceName,
				    targetSpecies, targetSequenceName } );
      if (tspace.count (template) > 0) return false;
      tspace.out(template);
      changed = true;
      return true;
   } // add

   public boolean delete (String user, String sourceSpecies, 
		       String sourceSequenceName) throws Exception {
      ITuple template = createTuple (new Object[] { user, sourceSpecies, sourceSequenceName,
				     String.class, String.class } );
      tspace.ing (template);
      changed = true;
      return true;
   }

   public boolean delete (String user, String sourceSpecies, String sourceSequenceName,
		       String targetSpecies, String targetSequenceName) throws Exception {
      ITuple template = createTuple (new Object[] { user, sourceSpecies, sourceSequenceName,
				     targetSpecies, targetSequenceName } );
      tspace.ing (template);
      changed = true;
      return true;
   } // add

   public Vector getFavorites (String user, String sourceSpecies, 
			       String sourceSequenceName) throws Exception {
      ITuple template = createTuple (new Object[] { user, sourceSpecies, 
						    sourceSequenceName,
						    String.class, String.class } );

      Vector list = new Vector ();

      ITuple tuples[] = tspace.rdg (template);
      if (tuples != null && tuples.length > 0) {
	 int max = tuples.length;
	 for (int t=0; t < max; t++) {
	    ITuple tuple = tuples[ t ];
	    String favoriteHomologName = (String) tuple.get(4).getValue();
	    list.add (favoriteHomologName);
	 } // for t
      } // if !null

      return list;
   } // getFavorites

   public Vector getFavorites (String user, String sourceSpecies, 
			       String sourceSequenceName, 
			       String targetSpecies) throws Exception {
      ITuple template = createTuple (new Object[] { user, sourceSpecies, 
						    sourceSequenceName,
						    targetSpecies, String.class } );

      Vector list = new Vector ();

      ITuple tuples[] = tspace.rdg (template);
      if (tuples != null && tuples.length > 0) {
	 int max = tuples.length;
	 for (int t=0; t < max; t++) {
	    ITuple tuple = tuples[ t ];
	    String favoriteHomologName = (String) tuple.get(4).getValue();
	    list.add (favoriteHomologName);
	 } // for t
      } // if !null

      return list;
   } // getFavorites

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.tspace.TSpaceFavoriteHomologHandler <port> <spaceName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, 
		       "csplugins.httpdata.handlers.tspace.TSpaceFavoriteHomologHandler", 
			    args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }

   static ITuple wildcardTemplate = null;
   static { 
      try { wildcardTemplate = createTuple( new Object[] { String.class, String.class, 
							   String.class, String.class, 
							   String.class } );	
      } catch( Exception e ) { };
   }
}
