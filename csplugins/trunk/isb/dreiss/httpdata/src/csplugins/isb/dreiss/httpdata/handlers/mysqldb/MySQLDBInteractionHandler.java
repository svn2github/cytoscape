package csplugins.isb.dreiss.httpdata.handlers.mysqldb;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.handlers.db.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>MySQLDBInteractionHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class MySQLDBInteractionHandler extends DBInteractionHandler {

   // each species/source combo. has its own table.
   //  the table row used in this space has the following fields:
   //Column  Field                   type
   //1       Interactor1             String
   //2	     Interaction type	     String 
   //3       Interactor2             String
   //4       pvalue                  double (use 0.0 for unknown)
   //5	     info		     String (e.g. "URL=http://blah|qual=GOOD")

   public MySQLDBInteractionHandler( String dbURL, String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( dbURL, tname );
      this.tableName = tname;
   }

   public MySQLDBInteractionHandler( String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( tname );
      this.tableName = tname;
   }

   protected String createInteractionTable( String species, String source ) {
      try { createSourcesTable(); } catch ( Exception e ) { };
      String spec = species.replace( ' ', '_' );
      if ( "".equals( spec ) ) spec = "unknown_species";
      String src = source.replace( ' ', '_' );
      if ( "".equals( src ) ) src = "unknown_source";
      String table = tableName + "_" + src + "_" + spec;
      dbhandler.createTable( table, " ( " +
		   "id INT NOT NULL AUTO_INCREMENT, " +
		   "i1 VARCHAR(128) NOT NULL, " +
		   "itype VARCHAR(64) NOT NULL, " +
		   "i2 VARCHAR(128) NOT NULL, " +
		   "pval DOUBLE, " +
		   "info TEXT, " +
		   "PRIMARY KEY (id), " +
		   "UNIQUE INDEX (i1,itype,i2) )" );
      return table;
   }

   protected void createSourcesTable() throws Exception {
      String table = "interaction_sources";
      if ( dbhandler.createTable( table, " ( " +
			"id INT NOT NULL AUTO_INCREMENT, " +
			"source VARCHAR(64) NOT NULL, " +
			"description VARCHAR(128) NOT NULL, " +
			"PRIMARY KEY (id), " +
			"UNIQUE INDEX (source,description) )" ) ) {
      }
   }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.mysqldb.MySQLDBInteractionHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.mysqldb.MySQLDBInteractionHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
