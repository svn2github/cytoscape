package csplugins.isb.dreiss.httpdata.handlers.sbeams;

import java.util.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;
//import djr.util.bio.Sequence;
import csplugins.isb.dreiss.httpdata.handlers.db.DBSequenceHandler;

/**
 * Class <code>SBEAMSSequenceHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 *
 */
public class SBEAMSSequenceHandler extends SBEAMSDataHandler {
   public static final String serviceName = "sequence";
   public static DBSequenceHandler sequenceHandler = null;

   public SBEAMSSequenceHandler() throws Exception {
      super();
   }

   public SBEAMSSequenceHandler( String userName, String password ) throws Exception {
      super( userName, password );
   }

   static DBSequenceHandler getDBSequenceHandler() throws Exception {
      if ( sequenceHandler != null ) return sequenceHandler;
      sequenceHandler = new DBSequenceHandler( "sequences" );
      return sequenceHandler;
   }      

   public boolean put (String name, String species, String sequence ) throws Exception {
      return getDBSequenceHandler().put( name, species, sequence );
   }

   public boolean put (String name, String species, String sequence,
		       String type) throws Exception {
      return getDBSequenceHandler().put( name, species, sequence, type );
   }   

   public String get (String name, String species) throws Exception {
      return get( name, species, "protein" );
   }

   public String get (String name, String species, String type) throws Exception {
      String seq = "";
      if ( "".equals( name ) ) return seq;

      try {
	 name = preprocessName( name );

	 seq = getDBSequenceHandler().get( name, species, type );
	 if ( seq == null || "UNKNOWN".equals( seq ) ) return "";
	 if ( ! "".equals( seq ) ) return seq;
      
	 String table = (String) speciesToTableMap.get( species + " " + type );
	 if ( table == null ) return seq;
	 Map parsedAndParams[] = getClientResponse( name, table );
	 Map parsed = parsedAndParams[ 0 ];

	 if ( ( (Vector) parsed.get( "biosequence_seq" ) ).size() > 0 ) {
	    Vector names = (Vector) parsed.get( "biosequence_name" );
	    int ind = getBestResponse( names, name );
	    if ( ind >= 0 )
	       seq = (String) ( (Vector) parsed.get( "biosequence_seq" ) ).get( ind );
	 } else {
	    Map params = parsedAndParams[ 1 ];
	    params.remove( "biosequence_name_constraint" );
	    params.put( "biosequence_desc_constraint", "%" + name + "%" );
	    String response = client.getResponse( sbeamsURL, params );
	    parsed = client.parseResponse( response );
	    if ( ( (Vector) parsed.get( "biosequence_seq" ) ).size() > 0 ) {
	       Vector names = (Vector) parsed.get( "biosequence_desc" );
	       int ind = getBestResponse( names, name );
	       if ( ind >= 0 )
		  seq = (String) ( (Vector) parsed.get( "biosequence_seq" ) ).get( ind );
	    }
	 }

	 //if ( ! "".equals( seq ) && "protein".equals( type ) ) seq = toProtein( seq );
	 if ( ! "".equals( seq ) ) getDBSequenceHandler().put( name, species, seq, type );
	 else getDBSequenceHandler().put( name, species, "UNKNOWN", type );

      } catch ( Exception e ) {
	 e.printStackTrace();
      } // end of catch
      
      return seq;
   }

   public Vector get (Vector names, String species) throws Exception {
      return get( names, species, "protein" );
   }

   public Vector get( Vector names, String species, String type ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( get( (String) names.get( i ), species, type ) );
      return out;
   }

   public Vector get (Vector names, Vector species) throws Exception {
      return get( names, species, "protein" );
   }

   public Vector get( Vector names, Vector species, String type ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( get( (String) names.get( i ), (String) species.get( i ), type ) );
      return out;
   }

   /*
   protected String toProtein( String seq ) {
      Sequence ss = new Sequence( seq );
      if ( ss.GetTypeName() == "protein" ) {
	 String sseq = seq.replace( 'N', 'A' ); // Some dna seqs have 'N' which means any acid.
	 ss = new Sequence( sseq );
	 if ( ss.GetTypeName() == "protein" ) return seq;
	 else return ss.ToProtein().GetSequence();
      }
      else return ss.ToProtein().GetSequence();
   }
   */

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.sbeams.SBEAMSSequenceHandler <port> <UserName> <Password>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 if ( args.length > 1 ) server.addService( serviceName, 
						   "csplugins.httpdata.handlers.sbeams.SBEAMSSequenceHandler", 
						   args[ 1 ], args[ 2 ] );
	 else server.addService( serviceName, 
				 "csplugins.httpdata.handlers.sbeams.SBEAMSSequenceHandler" );

      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }   
}
