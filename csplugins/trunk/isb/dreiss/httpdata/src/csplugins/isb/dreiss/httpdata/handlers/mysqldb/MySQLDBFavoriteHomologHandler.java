package csplugins.isb.dreiss.httpdata.handlers.mysqldb;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.handlers.db.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>MySQLDBFavoriteHomologHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class MySQLDBFavoriteHomologHandler extends DBFavoriteHomologHandler {

   //  The tables are named by the source species (should be formal Linaean genus/species nomenclature)
   //  the table used in this space has the following 5 String fields:
   //     the human user
   //     //species of the source sequence
   //     name of the source sequence
   //     species of the homologous sequence
   //     name of the homologous sequence

   public MySQLDBFavoriteHomologHandler( String dbURL, String tname ) {
      dbhandler = new MySQLDBDataHandler( dbURL, tname );
      this.tableName = tname;
   }

   public MySQLDBFavoriteHomologHandler( String tname ) {
      dbhandler = new MySQLDBDataHandler( tname );
      this.tableName = tname;
   }

   protected String createTable( String species ) {
      String spec = species.replace( ' ', '_' );
      String table = tableName + "_" + spec;
      dbhandler.createTable( table, " ( " +
		   "id INT NOT NULL AUTO_INCREMENT, " +
		   "user VARCHAR(32), " +
		   "source_sequence VARCHAR(128) NOT NULL, " +
		   "target_species VARCHAR(128) NOT NULL, " +
		   "target_sequence VARCHAR(128) NOT NULL, " +
		   "PRIMARY KEY (id), " +
		   "UNIQUE INDEX (user,source_sequence,target_species,target_sequence) )" );
      return table;
   }      

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.mysqldb.MySQLDBFavoriteHomologHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, 
			    "csplugins.httpdata.handlers.mysqldb.MySQLDBFavoriteHomologHandler", 
			    args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
