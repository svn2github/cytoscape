package csplugins.isb.dreiss.httpdata.handlers.db;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>DBFavoriteHomologHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class DBFavoriteHomologHandler {
   public static final String serviceName = "favoriteHomolog";

   protected String tableName;
   protected DBDataHandler dbhandler;

   //  The tables are named by the source species (should be formal Linaean genus/species nomenclature)
   //  the table used in this space has the following 5 String fields:
   //     the human user
   //     //species of the source sequence
   //     name of the source sequence
   //     species of the homologous sequence
   //     name of the homologous sequence

   public DBFavoriteHomologHandler() { };

   public DBFavoriteHomologHandler( String tname ) {
      dbhandler = new DBDataHandler( tname );
      this.tableName = tname;
   }

   protected String createTable( String species ) {
      String spec = species.replace( ' ', '_' );
      String table = tableName + "_" + spec;
      dbhandler.createTable( table, " ( " +
		   "user VARCHAR, " +
		   "source_sequence VARCHAR NOT NULL, " +
		   "target_species VARCHAR NOT NULL, " +
		   "target_sequence VARCHAR NOT NULL, " +
		   "CONSTRAINT UNIQUE_ID UNIQUE(USER,SOURCE_SEQUENCE," +
		   "TARGET_SPECIES,TARGET_SEQUENCE) )" );
      return table;
   }      

   public boolean add (String user, String sourceSpecies, String sourceSequenceName,
		       String targetSpecies, String targetSequenceName) 
      throws Exception {
      String table = createTable( sourceSpecies );
      String query = "INSERT INTO " + table + 
	 " (user,source_sequence,target_species,target_sequence) " +
	 "VALUES ('" + user + "', '" + 
	 sourceSequenceName + "', '" + targetSpecies + "', '" +
	 targetSequenceName + "')";
      try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      return true;
   }

   public boolean delete (String user, String sourceSpecies, 
			  String sourceSequenceName) throws Exception {
      String table = createTable( sourceSpecies );
      String query = "DELETE FROM " + table + " WHERE user = '" + user + 
	 "' AND source_sequence = '" + sourceSequenceName + "'";
      try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      return true;
   }

   public boolean delete (String user, String sourceSpecies, String sourceSequenceName,
			  String targetSpecies, String targetSequenceName) throws Exception {
      String table = createTable( sourceSpecies );
      String query = "DELETE FROM " + table + " WHERE user = '" + user + 
	 "' AND source_sequence = '" + sourceSequenceName + 
	 "' AND target_species = '" + targetSpecies + 
	 "' AND target_sequence = '" + targetSequenceName + "'";
      try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      return true;
   }

   public Vector getFavorites (String user, String sourceSpecies, 
			       String sourceSequenceName) throws Exception {
      String table = createTable( sourceSpecies );
      String query = "SELECT target_sequence FROM " + table + " WHERE user = '" + user + 
	 "' AND source_sequence = '" + sourceSequenceName + "'";
      if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );

      Vector list = new Vector();
      Connection conn = dbhandler.getConnection();
      try {
	 Statement st = conn.createStatement();
	 ResultSet rs = st.executeQuery(query);
	 ResultSetMetaData meta = rs.getMetaData();
	 for ( ; rs.next() ; ) list.add( rs.getObject( 1 ) );
      } catch ( Exception e ) { };
      return list;
   }

   public Vector getFavorites (String user, String sourceSpecies, 
			       String sourceSequenceName, 
			       String targetSpecies) throws Exception {
      String table = createTable( sourceSpecies );
      String query = "SELECT target_sequence FROM " + table + " WHERE user = '" + user + 
	 "' AND source_sequence = '" + sourceSequenceName + 
	 "' AND target_species = '" + targetSpecies + "'";
      if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );

      Vector list = new Vector();
      Connection conn = dbhandler.getConnection();
      try {
	 Statement st = conn.createStatement();
	 ResultSet rs = st.executeQuery(query);
	 ResultSetMetaData meta = rs.getMetaData();
	 for ( ; rs.next() ; ) list.add( rs.getObject( 1 ) );
      } catch ( Exception e ) { };
      return list;
   }

   public boolean setDebug( String deb ) { return dbhandler.setDebug( deb ); }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.db.DBFavoriteHomologHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, 
			    "csplugins.httpdata.handlers.db.DBFavoriteHomologHandler", 
			    args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
