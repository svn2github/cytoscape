package csplugins.isb.dreiss.httpdata.handlers.mysqldb;
import java.io.*;
import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.handlers.db.*;
//import csplugins.isb.dreiss.httpdata.xmlrpc.MyXmlRpcHandler;

/**
 * Class <code>MySQLDBDataHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class MySQLDBDataHandler extends DBDataHandler {
   String dbURL = "mysql://mysql/dreiss?user=dreiss&password=DR721";
   String additionalParams = "&autoreconnect=true&maxReconnects=99999";

   public MySQLDBDataHandler( String tname ) {
      initialize( dbURL, tname );
   }

   public MySQLDBDataHandler( String dbURL, String tname ) {
      initialize( dbURL, tname );
   }

   protected void initialize( String dbURL, String tname ) {
      this.tableName = tname;
      this.dbURL = dbURL;
      this.CREATE_TABLE = "CREATE TABLE ";

      System.err.println( "STARTING HANDLER: " + tableName );
      try {
	 Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
	 conn = DriverManager.getConnection( "jdbc:" + dbURL + additionalParams );

	 Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
	    try { MySQLDBDataHandler.this.shutdown(); } catch ( Exception e) { }; } } );

      } catch ( Exception e ) { e.printStackTrace(); }
   }

   public boolean createTable( String table, String desc ) {
      if ( createdTables.get( table ) != null ) return false;
      if ( debug ) System.err.println( "Creating table: " + table );
      boolean out = false;
      try {
	 update( CREATE_TABLE + " " + table + " " + desc );
	 out = true;
      } catch ( Exception e ) { e.printStackTrace(); out = false; }
      createdTables.put( table, new Boolean( true ) );
      return out;
   }

   public void shutdown() throws SQLException {
      System.out.println( "IN SHUTDOWN: " + tableName );
      conn.close();
   }

   // use for SQL commands CREATE and SELECT
   protected boolean inQueryAlready = false;
   public Object query( String expression ) throws SQLException {
      if ( inQueryAlready ) return null;
      inQueryAlready = true;
      if ( debug ) System.err.println( "QUERY: " + expression );
      Statement st = null;
      ResultSet rs = null;
      try {
	 st = conn.createStatement();
	 rs = st.executeQuery( expression );
	 rs.next();
	 Object out = rs.getObject( 1 );
	 return out;
      } catch ( SQLException e ) { // Connection is probably closed (closees automatically after 8 hours)
	 // See http://www.mysql.com/documentation/connector-j/index.html#id2802490
	 //if ( e.getSQLState().equals( "08S01" ) ) { 
	 try { conn.close(); } catch ( Exception ee ) { ; }
	 conn = DriverManager.getConnection( "jdbc:" + dbURL + additionalParams );
	 return query( expression );
	 //}
      } finally {
	 inQueryAlready = false;
	 if ( rs != null ) rs.close();
	 if ( st != null ) st.close();
      }
   }

   // use for SQL commands INSERT and DELETE
   protected boolean inUpdateAlready = false;
   public void update( String expression ) throws SQLException {
      if ( inUpdateAlready || ! allowUpdates ) return;
      inUpdateAlready = true;
      if ( debug ) System.err.println( "UPDATE: " + expression );
      Statement st = null;
      try {
	 st = conn.createStatement(); 
	 int i = st.executeUpdate( expression );
	 if ( i == -1 ) System.out.println( "db error : " + expression );
      } catch ( SQLException e ) { // Connection is probably closed (closees automatically after 8 hours)
	 // See http://www.mysql.com/documentation/connector-j/index.html#id2802490 :
	 //if ( e.getSQLState().equals( "08S01" ) ) { 
	 try { conn.close(); } catch ( Exception ee ) { ; }
	 conn = DriverManager.getConnection( "jdbc:" + dbURL + additionalParams );
	 update( expression );
	 //}
      } finally {
	 inUpdateAlready = false;
	 if ( st != null ) st.close();
      }
   }
}
