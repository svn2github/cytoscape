package csplugins.isb.dreiss.httpdata.handlers.mysqldb;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.handlers.db.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>MySQLDBHomologHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class MySQLDBHomologHandler extends DBHomologHandler {

   // each species/homolog_species combo. has its own table.
   //  the table row used in this space has the following fields:
   //Column  Field                   type
   //1       prot	             String
   //2	     homolog		     String 
   //4       score                   double (use 0.0 for unknown)
   //5	     info		     String (e.g. "URL=http://blah|qual=GOOD")

   public MySQLDBHomologHandler( String dbURL, String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( dbURL, tname );
      this.tableName = tname;
   }

   public MySQLDBHomologHandler( String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( tname );
      this.tableName = tname;
   }

   protected String createHomologTable( String species, String hSpecies ) throws Exception {
      try { createSpeciesTable(); } catch ( Exception e ) { };
      if ( species.indexOf( ' ' ) < 0 ) species = getSpeciesFullName( species );
      if ( hSpecies.indexOf( ' ' ) < 0 ) hSpecies = getSpeciesFullName( hSpecies );
      String spec = species.replace( ' ', '_' );
      String hspec = hSpecies.replace( ' ', '_' );
      String table = tableName + "_" + spec + "_" + hspec;
      dbhandler.createTable( table, " ( " +
		   "id INT NOT NULL AUTO_INCREMENT, " +
		   "prot VARCHAR(128) NOT NULL, " +
		   "homolog VARCHAR(128) NOT NULL, " +
		   "score DOUBLE, " +
		   "info TEXT, " +
		   "PRIMARY KEY (id), " +
		   "UNIQUE INDEX (PROT,HOMOLOG) )" );
      return table;
   }

   protected void createSpeciesTable() throws Exception {
      String table = "homolog_species";
      dbhandler.createTable( table, " ( " +
			     "id INT NOT NULL AUTO_INCREMENT, " +
			     "spec VARCHAR(64) NOT NULL, " +
			     "fullname VARCHAR(128) NOT NULL, " +
			     "PRIMARY KEY (id), " +
			     "UNIQUE INDEX idx (SPEC,FULLNAME) )" );
   }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.mysqldb.MySQLDBHomologHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.mysqldb.MySQLDBHomologHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
