package csplugins.isb.dreiss.httpdata.handlers.mysqldb;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;
import csplugins.isb.dreiss.httpdata.handlers.db.*;
//import djr.util.bio.Sequence;

/**
 * Class <code>MySQLDBSequenceHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 *
 *  The tables are named by the species (should be formal Linaean genus/species nomenclature)
 * 	    and type of sequence (DNA, RNA, protein, upstream, others...? (protein is default)
 *  table rows are structured like this:
 *      0) entity (typically gene or protein) names/synonyms separated by "|"
 *      //1) species (we recommend formal Linaean genus/species nomenclature)
 *      //2) type: DNA, RNA, protein, upstream, others...? (protein is default)
 *      3) sequence: a string of characters (typically DNA or amino acids)
 */
public class MySQLDBSequenceHandler extends DBSequenceHandler {

   public MySQLDBSequenceHandler( String dbURL, String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( dbURL, tname );
      this.tableName = tname;
   }

   public MySQLDBSequenceHandler( String tname ) throws Exception {
      dbhandler = new MySQLDBDataHandler( tname );
      this.tableName = tname;
   }

   protected String createSequenceTable( String species, String type ) {
      String spec = species.replace( ' ', '_' );
      String table = tableName + "_" + type + "_" + spec;
      dbhandler.createTable( table, " ( " +
		   "id INT NOT NULL AUTO_INCREMENT, " +
		   "name VARCHAR(128) NOT NULL, " +
		   "sequence TEXT NOT NULL, " +
		   "PRIMARY KEY (id), " +
		   "UNIQUE INDEX idxname (name) )" );
      return table;
   }

   public static void main( String [] args ) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.mysqldb.MySQLDBSequenceHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.mysqldb.MySQLDBSequenceHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
