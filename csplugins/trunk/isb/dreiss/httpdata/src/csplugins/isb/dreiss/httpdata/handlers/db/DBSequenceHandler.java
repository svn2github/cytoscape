package csplugins.isb.dreiss.httpdata.handlers.db;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;
//import djr.util.bio.Sequence;

/**
 * Class <code>DBSequenceHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 *
 *  The tables are named by the species (should be formal Linaean genus/species nomenclature)
 * 	    and type of sequence (DNA, RNA, protein, upstream, others...? (protein is default)
 *  table rows are structured like this:
 *      0) entity (typically gene or protein) name (canonical name: refseq should be the default)
 *      3) sequence: a string of characters (typically DNA or amino acids)
 */
public class DBSequenceHandler {
   public static final String serviceName = "sequence";
   public static final String TYPES[] = { "DNA", "RNA", "protein", "upstream" };

   protected String tableName;
   protected DBDataHandler dbhandler;

   public DBSequenceHandler() { };

   public DBSequenceHandler( String tableName ) throws Exception {
      dbhandler = new DBDataHandler( tableName );
      this.tableName = tableName;
   }

   protected String createSequenceTable( String species, String type ) {
      String spec = species.replace( ' ', '_' );
      String table = tableName + "_" + type + "_" + spec;
      dbhandler.createTable( table, " ( " +
		   "name VARCHAR NOT NULL, " +
		   "sequence VARCHAR NOT NULL, " +
		   "CONSTRAINT UNIQUE_ID UNIQUE(NAME,SEQUENCE) )" );
      return table;
   }

   public boolean put( String name, String species, String sequence ) throws Exception {
      return put( name, species, sequence, "protein" );
   }

   public boolean put( String name, String species, String sequence,
		       String type ) throws Exception {
      String table = createSequenceTable( species, type );
      String query = "INSERT INTO " + table + " (name,sequence) " +
	 "VALUES ('" + name + "', '" + sequence + "')";
      try { dbhandler.update( query ); } catch ( Exception e) { return false; }
      return true;
   }

   public String get( String name, String species ) throws Exception {
      return get( name, species, "protein" );
   }

   public String get( String name, String species, String type ) throws Exception {
      if ( name.length() <= 0 ) return "";
      String table = createSequenceTable( species, type );

      String prots[] = name.indexOf( ";" ) >= 0 ? name.split( "\\;" ) : new String[] { name };
      String result = "";

      for ( int i = 0; i < prots.length; i ++ ) {
	 String lookup = prots[ i ];
	 String query = "SELECT sequence FROM " + table + " WHERE name = '" + lookup + "'";
	 try { result = dbhandler.query( query ).toString(); }
	 catch ( Exception e ) { result = ""; }

	 /*
	 if ( result == null || result.length() <= 0 ) {
	    query = "SELECT sequence FROM " + table + " WHERE name LIKE '%" + lookup + "%'";
	    result = "";
	    try { result = dbhandler.query( query ).toString(); }
	    catch ( Exception e ) { result = ""; }
	    if ( result != null && result.length() > 0 ) {
	       query = "INSERT INTO " + table + " (name,sequence) " +
		  "VALUES ('" + name + "', '" + result + "')";
	       try { dbhandler.update( query ); } catch ( Exception e) { ; }
	    }
	 }
	 */

	 if ( result != null && result.length() > 0 ) return result;
      }

      //if ( ! "".equals( result ) && "protein".equals( result ) ) result = toProtein( result );

      return result;
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

   public Vector get( Vector names, String species ) throws Exception {
      return get( names, species, "protein" );
   }

   public Vector get( Vector names, String species, String type ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( get( (String) names.get( i ), species, type ) );
      return out;
   }

   public Vector get( Vector names, Vector species ) throws Exception {
      return get( names, species, "protein" );
   }

   public Vector get( Vector names, Vector species, String type ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( get( (String) names.get( i ), (String) species.get( i ), type ) );
      return out;
   }

   public boolean setDebug( String deb ) { return dbhandler.setDebug( deb ); }

   public static void main( String [] args ) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.db.DBSequenceHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.db.DBSequenceHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
