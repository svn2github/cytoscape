package csplugins.isb.dreiss.httpdata.handlers.mysqldb;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.handlers.db.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>MySQLDBSynonymHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class MySQLDBSynonymHandler extends DBSynonymHandler {

   // The table is named by the species.
   //  the table row used in this space has the following fields:
   //     'name': a gene/protein name
   //     'synonyms': a string of its synonyms, separated by "|"

   public MySQLDBSynonymHandler( String dbURL, String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( dbURL, tname );
      this.tableName = tname;
   }

   public MySQLDBSynonymHandler( String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( tname );
      this.tableName = tname;
   }

   protected String createTable( String species ) {
      String spec = species.replace( ' ', '_' );
      String table = tableName + "_" + spec;
      dbhandler.createTable( table, " ( " +
			     "id INT NOT NULL AUTO_INCREMENT, " +
			     "name VARCHAR(128) NOT NULL, " +
			     "synonyms TEXT, " +
			     "PRIMARY KEY (id), " +
			     "UNIQUE INDEX (name) )" );
      return table;
   }

   public Vector getSynonyms( String name, String species ) throws Exception {
      Vector out = super.getSynonyms( name, species );

      if ( out == null || out.size() <= 0 ) { // use "master" table
	 out = getSynonymsFromMasterTable( name );
	 if ( out != null && out.size() > 0 ) {
	    String synString = "";
	    for ( int i = 0; i < out.size(); i ++ )
	       synString += out.get( i ) + "|";
	    putSynonymsString( name, synString, species );
	 } else {
	    putSynonymsString( name, "Unknown", species );
	 }
      }
      return out;
   }

   protected Vector getSynonymsFromQuery( String query ) throws Exception {
      if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );

      Vector list = null;
      Connection conn = dbhandler.getConnection();
      Statement st = null;
      ResultSet rs = null;
      try {
	 st = conn.createStatement();
	 rs = st.executeQuery( query );
	 ResultSetMetaData meta = rs.getMetaData();
	 list = parseSynonymsStrings( rs );
      } catch ( Exception e ) { 
      } finally {
	 if ( rs != null ) rs.close();
	 if ( st != null ) st.close();	 
      } // end of finally
      
      return list;
   }

   public Vector getSynonymsFromMasterTable( String name ) throws Exception {
      String table = "synonyms", lookup = name, temp = name.toUpperCase();

      if ( temp.startsWith( "NP_" ) || temp.startsWith( "XP_" ) ||
	   temp.startsWith( "NM_" ) || temp.startsWith( "XM_" ) ) lookup = "ref:" + lookup;
      else if ( temp.startsWith( "LOC" ) ) lookup = "loc:" + lookup.substring( 3 );
      else if ( temp.startsWith( "GI" ) || temp.startsWith( "GB" ) ) lookup = "gi:" + lookup.substring( 2 );
      else if ( temp.startsWith( "HS." ) || temp.startsWith( "MM." ) ||
		temp.startsWith( "DM." ) ) lookup = "ug:" + lookup;

      /*
      if ( temp.startsWith( "NP_" ) || temp.startsWith( "XP_" ) ||
	   temp.startsWith( "NM_" ) || temp.startsWith( "XM_" ) ) table = "synonyms_ref";
      else if ( temp.startsWith( "LOC" ) ) { table = "synonyms_loc"; lookup = name.substring( 3 ); }
      else if ( temp.startsWith( "GI" ) || temp.startsWith( "GB" ) ) { 
	 table = "synonyms_gi"; lookup = name.substring( 2 ); }
      else if ( temp.startsWith( "HS." ) || temp.startsWith( "MM." ) ||
		temp.startsWith( "DM." ) ) table = "synonyms_ug";
      */

      String query = "SELECT synonyms FROM " + table + " WHERE name = '" + lookup + "'"; 
      Vector list = getSynonymsFromQuery( query );

      if ( list != null && list.size() > 0 ) return list;

      if ( name.indexOf( ':' ) >= 0 ) name = name.substring( name.indexOf( ':' ) + 1 );
      if ( name.indexOf( '=' ) >= 0 ) name = name.substring( name.indexOf( '=' ) + 1 );
      if ( name.indexOf( '|' ) >= 0 ) name = name.substring( name.lastIndexOf( '|' ) + 1 );

      query = "SELECT synonyms FROM synonyms " +
	 "WHERE synonyms REGEXP '[=:;|]" + name + "[|;.]'";
      list = getSynonymsFromQuery( query );

      if ( list == null ) list = new Vector(); // Cant return null
      return list;
   }

   protected Vector parseSynonymsStrings( ResultSet rs ) throws Exception {
      Vector list = new Vector();
      Map map = new HashMap();
      for ( ; rs.next() ; ) {
	 String val = (String) rs.getObject( 1 );
	 if ( "".equals( val ) ) continue;
	 String toks[] = val.split( "\\|" );
	 for ( int i = 0; i < toks.length; i ++ ) {
	    String tok = toks[ i ].trim();	
	    if ( "".equals( tok ) ) continue;
	    String key = tok.substring( 0, tok.indexOf( '=' ) );
	    String inMap = (String) map.get( key );
	    if ( inMap == null ) map.put( key, tok );
	    else {
	       String entry = tok.substring( tok.indexOf( '=' ) + 1 );
	       map.remove( key );
	       map.put( key, inMap + ";" + entry );
	    }
	 }
      }
      for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) list.add( map.get( it.next() ) );
      return list;
   }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.mysqldb.MySQLDBSynonymHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.mysqldb.MySQLDBSynonymHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
