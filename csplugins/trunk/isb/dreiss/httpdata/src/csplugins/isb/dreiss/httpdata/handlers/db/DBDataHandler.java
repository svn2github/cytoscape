package csplugins.isb.dreiss.httpdata.handlers.db;
import java.io.*;
import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
//import csplugins.isb.dreiss.httpdata.xmlrpc.MyXmlRpcHandler;

/**
 * Class <code>DBDataHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class DBDataHandler {//extends MyXmlRpcHandler {
   protected Map createdTables = new HashMap();
   protected String tableName;
   protected Connection conn;
   protected int updates = 0, totalUpdates = 0;
   protected boolean debug = true, allowUpdates = true;

   protected String CREATE_TABLE = "CREATE TEXT TABLE "; //"CREATE CACHED TABLE ";
   protected boolean CONVERT_TABLES = false;

   public DBDataHandler() { };

   public DBDataHandler( String tname ) {
      initialize( tname );
   }

   protected void initialize( String tname ) {
      this.tableName = tname;
      System.err.println( "STARTING HANDLER: " + tableName );
      try {
	 Class.forName( "org.hsqldb.jdbcDriver" );
	 conn = DriverManager.getConnection( "jdbc:hsqldb:db_dir/" + 
					     tableName, "sa", "" );
	 
	 Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
	    try { DBDataHandler.this.shutdown(); } catch ( Exception e) { }; } } );

	 try {
	    query( "SET IGNORECASE TRUE" );
	    if ( ! CONVERT_TABLES ) {
	       query( "SET WRITE_DELAY TRUE" );
	       query( "SET LOGSIZE 10" );
	       query( "SET AUTOCOMMIT TRUE" );
	       if ( ! allowUpdates ) query( "SET READONLY TRUE" );
	       else query( "SET READONLY FALSE" );
	    } else {
	       //query( "SET CACHE SCALE 8" );
	    }
	 } catch ( Exception e ) { ; }

      } catch (Exception e) { e.printStackTrace(); }
   }

   public Connection getConnection() { return conn; }
   public boolean debug() { return debug; }

   public boolean createTable( String table, String desc ) {
      if ( createdTables.get( table ) != null ) return false;
      String newtab = table; if ( CONVERT_TABLES ) table += "_old";
      if ( debug ) System.err.println( "Creating table: " + table );
      boolean out = false;
      try {
	 query( CREATE_TABLE + " " + table + " " + desc );
	 setTableSource( table, newtab );
	 out = true;
      } catch ( Exception e ) { out = false; }
      createdTables.put( table, new Boolean( true ) );
      if ( CONVERT_TABLES ) {
	 convertTableFromText( table, newtab ); 
	 try { query( "DROP TABLE " + table + " IF EXISTS;" ); }
	 catch ( Exception e ) { e.printStackTrace(); }
	 createdTables.remove( table );
	 createdTables.put( newtab, new Boolean( true ) );
      }
      return out;
   }

   public void convertTableFromText( String oldtable, String table ) {
      try { 
	 query( "DROP TABLE " + table + " IF EXISTS;" );
	 query( "SELECT * INTO CACHED " + table + " FROM " + oldtable );
      } catch ( Exception e ) { e.printStackTrace(); }
   }

   public void convertTableToText( String table ) {
      // Can open the table in excel... (csv)
      try { query( "SELECT * INTO TEXT " + table + " FROM" ); }
      catch ( Exception e ) { e.printStackTrace(); }
   }

   public void setTableSource( String table, String csvName ) {
      // Can open the table in excel... (csv)
      if ( CREATE_TABLE.indexOf( " TEXT TABLE " ) >= 0 ) {
	 if ( debug ) System.err.println( "Setting table source to text file: " + csvName + ".csv" );
	 try { query( "SET TABLE " + table + " SOURCE \"" + csvName + ".csv\"" ); }
	 catch ( Exception e ) { e.printStackTrace(); }
      }
   }

   public void shutdown() throws SQLException {
      System.out.println( "IN SHUTDOWN: " + tableName );
      try { 
	 if ( totalUpdates > 0 ) query( "SHUTDOWN COMPACT" ); 
	 else query( "SHUTDOWN" );
      } catch ( Exception e ) { e.printStackTrace(); }
      conn.close();
   }

   // use for SQL commands CREATE and SELECT
   public Object query(String expression) throws SQLException {
      if ( debug ) System.err.println( "QUERY: " + expression );
      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery(expression);
      try {
	 rs.next();
	 Object out = rs.getObject( 1 );
	 st.close();
	 return out;
      } catch ( Exception e ) { return null; }
   }

   public void queryAndDump(String expression) throws SQLException {
      if ( debug ) System.err.println( "QUERY: " + expression );
      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery(expression);
      try { dump(rs); }
      catch ( Exception e ) { ; }
   }

   // use for SQL commands INSERT and DELETE
   public void update(String expression) throws SQLException {
      if ( debug ) System.err.println( "UPDATE: " + expression );
      if ( allowUpdates ) {
	 Statement st = conn.createStatement();                // statements
	 int i = st.executeUpdate(expression);       // run the query
	 if (i == -1) System.out.println("db error : " + expression);
	 updates ++;
	 totalUpdates ++;
	 if ( updates > 1000 ) { 
	    if ( debug ) System.err.println( "CHECKPOINTING: " + tableName);
	    st.executeUpdate("COMMIT");
	    st.executeUpdate("CHECKPOINT"); updates = 0; 
	 }
	 st.close();
      }
   }

   public static void dump(ResultSet rs) throws SQLException {
      ResultSetMetaData meta   = rs.getMetaData();
      int               colmax = meta.getColumnCount();
      for (;rs.next();) {
         for (int i = 0; i < colmax; ++i) {
            Object o = rs.getObject(i + 1);
            System.out.print(o.toString() + " ");
         }
         System.out.println(" ");
      }
   }

   public boolean setDebug( String db ) { 
      this.debug = Boolean.valueOf( db ).booleanValue(); return this.debug; }
}
