package csplugins.isb.dreiss.httpdata.handlers.db;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>DBSynonymHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class DBSynonymHandler {
   public static final String serviceName = "synonym";

   protected String tableName;
   protected DBDataHandler dbhandler;

   // The table is named by the species.
   //  the table row used in this space has the following fields:
   //     a gene/protein name
   //     a string of its synonyms, separated by "|"

   public DBSynonymHandler() { };

   public DBSynonymHandler(String tableName) throws Exception {
      dbhandler = new DBDataHandler( tableName );
      this.tableName = tableName;
   }

   protected String createTable( String species ) {
      String spec = species.replace( ' ', '_' );
      String table = tableName + "_" + spec;
      dbhandler.createTable( table, " ( " +
	       "name VARCHAR NOT NULL, " +
	       "synonyms VARCHAR NOT NULL, " +
	       "CONSTRAINT UNIQUE_ID UNIQUE(name,synonyms) )" );
      return table;
   }

   protected boolean createFullSynonymsTable() {
      return dbhandler.createTable( "synonyms", " ( " +
				    "name VARCHAR NOT NULL, " +
				    "synonyms VARCHAR NOT NULL, " +
				    "CONSTRAINT UNIQUE_ID UNIQUE(name,synonyms) )" );
   }

   public boolean addSynonym( String name1, String name2, String species ) throws Exception {
      return put( name1, name2, species );
   }

   public boolean put( String name1, String name2, String species ) throws Exception {
      String origName1 = name1.trim();
      String origName2 = name2.trim();
      if ( name1.indexOf( "'" ) >= 0 ) name1 = name1.replaceAll( "\\'", "\"" );
      if ( name2.indexOf( "'" ) >= 0 ) name2 = name2.replaceAll( "\\'", "\"" );
      if ( origName1.indexOf( ' ' ) >= 0 || origName2.indexOf( ' ' ) >= 0 ) return false;
      name1 = origName1.toUpperCase(); name2 = origName2.toUpperCase();
      if ( name1.equalsIgnoreCase( name2 ) || "".equals( name1 ) || "".equals( name2 ) ) return false;

      String table = createTable( species ), val = "";
      String query = "SELECT synonyms FROM " + table + " WHERE name = '" + name1 + "'";

      try { val = (String) dbhandler.query( query ); } catch (Exception e) { val = ""; }
      if ( val == null || "".equals( val ) || "null".equals( val ) ) val = "|";
      boolean update = ! "|".equals( val );

      String origVal = new String( val );
      String ucVal = val.toUpperCase();
      if ( ucVal.indexOf( "|" + name1 + "|" ) < 0 ) val += origName1 + "|";
      if ( ucVal.indexOf( "|" + name2 + "|" ) < 0 ) val += origName2 + "|";
      //if ( val.startsWith( "|" ) ) val = val.substring( 1 );
      val = val.replaceAll( "\\'", "\"" );
      
      if ( ! update ) {
	 query = "INSERT INTO " + table + " (name,synonyms) " +
	    "VALUES ('" + name1 + "', '" + val + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { return false; }	 

      } else if ( ! val.equals( origVal ) ) {
	 query = "UPDATE " + table + " SET synonyms = '" + val + 
	    "' WHERE name = '" + name1 + "'";
	 try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      }
      return true;
   }

   public boolean putSynonymsString( String name, String syns, String species ) throws Exception {
      String table = createTable( species );
      if ( name.indexOf( "'" ) >= 0 ) name = name.replaceAll( "\\'", "\"" );
      if ( syns.indexOf( "'" ) >= 0 ) syns = syns.replaceAll( "\\'", "\"" );
      String query = "INSERT INTO " + table + " (name,synonyms) " + "VALUES ('" + name + "', '" + syns + "')";
      try { dbhandler.update( query ); } catch ( Exception e ) { e.printStackTrace(); return false; }
      return true;
   }

   public Vector getSynonyms( String name, String species ) throws Exception {
      String table = createTable( species );
      String query = "SELECT synonyms FROM " + table + " WHERE name = '" + name + "'"; 

      if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
      Vector list = new Vector();
      Connection conn = dbhandler.getConnection();
      try {
	 Statement st = conn.createStatement();
	 ResultSet rs = st.executeQuery(query);
	 ResultSetMetaData meta = rs.getMetaData();
	 for ( ; rs.next() ; ) {
	    String val = (String) rs.getObject( 1 );
	    if ( "".equals( val ) ) continue;
	    String toks[] = val.split( "\\|" );
	    for ( int i = 0; i < toks.length; i ++ ) {
	       String tok = toks[ i ].trim();
	       if ( ! "".equals( tok ) && ! isInvalidSynonym( tok ) &&
		    ! tok.equalsIgnoreCase( name ) && 
		    ! list.contains( tok ) ) list.add( tok );
	       //if ( tok.indexOf( ' ' ) > 0 && ( tok.indexOf( "Tax_Id=" ) > 0 ||
	       //	tok.indexOf( ':' ) > 0 || tok.indexOf( ';' ) > 0 ) )
	       //addSubTokens( tok, list );
	    }
	 }
      } catch ( Exception e ) { };
      return list;
   }

   public Vector getSynonyms( Vector names, String species ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( getSynonyms( (String) names.get( i ), species ) );
      return out;      
   }

   public Vector getSynonyms( Vector names, Vector species ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( getSynonyms( (String) names.get( i ), (String) species.get( i ) ) );
      return out;      
   }

   public String getSynonymsString( String name, String species ) throws Exception {
      String val = "";
      String table = createTable( species );
      String query = "SELECT synonyms FROM " + table + " WHERE name = '" + name + "'";
      try { val = (String) dbhandler.query( query ); } catch ( Exception e ) { val = ""; }
      if ( val == null ) val = "";
      return val;
   }

   public Vector getSynonymsString( Vector names, String species ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( getSynonymsString( (String) names.get( i ), species ) );
      return out;      
   }

   public Vector getSynonymsString( Vector names, Vector species ) throws Exception {
      Vector out = new Vector();
      for ( int i = 0, sz = names.size(); i < sz; i ++ )
	 out.add( getSynonymsString( (String) names.get( i ), (String) species.get( i ) ) );
      return out;      
   }

   protected boolean isInvalidSynonym( String syn ) {
      if ( syn.equalsIgnoreCase( "ipi" ) ) return true;
      if ( syn.equalsIgnoreCase( "ug" ) ) return true;
      if ( syn.equalsIgnoreCase( "refseq" ) ) return true;
      if ( syn.equalsIgnoreCase( "ref" ) ) return true;
      if ( syn.equalsIgnoreCase( "sp" ) ) return true;
      if ( syn.equalsIgnoreCase( "loc" ) ) return true;
      if ( syn.equalsIgnoreCase( "ens" ) ) return true;
      if ( syn.equalsIgnoreCase( "est" ) ) return true;
      if ( syn.equalsIgnoreCase( "tr" ) ) return true;
      if ( syn.equalsIgnoreCase( "gi" ) ) return true;
      if ( syn.equalsIgnoreCase( "gb" ) ) return true;
      if ( syn.equalsIgnoreCase( "emb" ) ) return true;
      if ( syn.equalsIgnoreCase( "gnl" ) ) return true;
      return false;
   }

   protected void addSubTokens( String tok, Vector list ) {
      //System.err.println("TOKAAA: "+tok);
      if ( tok.indexOf( "Tax_Id=9606" ) > 0 ) {
	 String toks[] = tok.split( "Tax_Id=9606" );
	 String tt = toks[ 1 ].trim();
	 if ( ! list.contains( tt ) ) list.add( tt  );
	 //System.err.println( "HEREXXX: "+tt);
	 tok = toks[ 0 ].trim();
      }
      //System.err.println("TOKBBB: "+tok);
      
      if ( tok.indexOf( ' ' ) > 0 || tok.indexOf( ';' ) > 0 ) {
	 if ( tok.indexOf( ':' ) < 0 ) {
	    String toks[] = tok.split( ";" );
	    for ( int i = 0; i < toks.length; i ++ ) {
	       String tt = toks[ i ].trim();
	       if ( ! list.contains( tt ) ) list.add( tt );
	       //System.err.println("HEREYYY: "+tt);
	    }
	 } else {
	    String toks[] = tok.split( "[\\s:;]" );
	    for ( int i = 0; i < toks.length; i ++ ) {
	       String tt = toks[ i ].trim();
	       if ( ! tt.equalsIgnoreCase( "REFSEQ_NP" ) && 
		    ! tt.equalsIgnoreCase( "TREMBL" ) &&
		    ! tt.equalsIgnoreCase( "ENSEMBL" ) &&
		    ! tt.equalsIgnoreCase( "SWISS-PROT" ) &&
		    ! tt.equalsIgnoreCase( "REFSEQ_XP" ) &&
		    ! list.contains( tt ) ) list.add( tt );
	       //System.err.println("HEREZZZ: "+tt);
	    }
	 }
      }
   }

   public boolean setDebug( String deb ) { return dbhandler.setDebug( deb ); }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.db.DBSynonymHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.db.DBSynonymHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
