package csplugins.isb.dreiss.httpdata.handlers.db;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>DBHomologHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class DBHomologHandler {
   public static final String serviceName = "homolog";
   protected Map availableSpecies = null, speciesMap = new HashMap();

   protected String tableName;
   protected DBDataHandler dbhandler;

   // each species/homolog_species combo. has its own table.
   //  the table row used in this space has the following fields:
   //Column  Field                   type
   //1       prot	             String
   //2	     homolog		     String 
   //4       score                   double (use 0.0 for unknown)
   //5	     info		     String (e.g. "URL=http://blah|qual=GOOD")

   public DBHomologHandler() { };

   public DBHomologHandler (String tableName) throws Exception {
      dbhandler = new DBDataHandler( tableName );
      this.tableName = tableName;
   }

   protected String createHomologTable( String species, String hSpecies ) throws Exception {
      try { createSpeciesTable(); } catch ( Exception e ) { };
      if ( species.indexOf( ' ' ) < 0 ) species = getSpeciesFullName( species );
      if ( hSpecies.indexOf( ' ' ) < 0 ) hSpecies = getSpeciesFullName( hSpecies );
      String spec = species.replace( ' ', '_' );
      String hspec = hSpecies.replace( ' ', '_' );
      String table = tableName + "_" + spec + "_" + hspec;
      dbhandler.createTable( table, " ( " +
		   "prot VARCHAR NOT NULL, " +
		   "homolog VARCHAR NOT NULL, " +
		   "score DOUBLE, " +
		   "info VARCHAR, " +
		   "CONSTRAINT UNIQUE_ID UNIQUE(PROT,HOMOLOG) )" );
      return table;
   }

   protected void createSpeciesTable() throws Exception {
      String table = "homolog_species";
      if ( dbhandler.createTable( table, " ( " +
			"spec VARCHAR NOT NULL, " +
			"fullname VARCHAR NOT NULL, " +
			"CONSTRAINT UNIQUE_ID UNIQUE(SPEC,FULLNAME) )" ) ) {
	 //addSpecies( "human", "Homo sapiens" );
	 //addSpecies( "yeast", "Saccharomyces cerevisiae" );
	 //addSpecies( "fruitfly", "Drosophila melanogaster" );
	 //addSpecies( "mouse", "Mus musculus" );
	 //addSpecies( "worm", "Caenorhabditis elegans" );
	 //addSpecies( "ecoli", "Escherichia coli" );
	 //addSpecies( "rat", "Rattus norvegicus" );
	 //addSpecies( "zfish", "Danio rerio" );
	 //addSpecies( "mosquito", "Anopheles gambiae" );
	 //addSpecies( "weed", "Arabidopsis thaliana" );
	 //addSpecies( "rice", "???" );
      }
   }

   public boolean addSpecies( String spec, String fullname ) throws Exception {
      //try { createSpeciesTable(); } catch ( Exception e ) { };
      try {
	 dbhandler.query( "INSERT INTO homolog_species (spec,fullname) VALUES('" + spec + "', '" + fullname + "')" );
	 return true;
      } catch ( Exception e ) { };
      return false;
   }

   public boolean hasSpecies( String spec ) throws Exception {
      if ( speciesMap.containsKey( spec ) ) return true;
      try { createSpeciesTable(); } catch ( Exception e ) { };
      String query = "SELECT spec FROM homolog_species WHERE fullname = '" + spec + "'";
      boolean out = false;
      try {
	 String result = dbhandler.query( query ).toString();
	 out = true;
	 speciesMap.put( spec, new Boolean( true ) );
      } catch ( Exception e ) { out = false; }
      if ( ! out ) {
	 query = "SELECT fullname FROM homolog_species WHERE spec = '" + spec + "'";
	 try {
	    String result = dbhandler.query( query ).toString();
	    out = true;
	    speciesMap.put( spec, new Boolean( true ) );
	 } catch ( Exception e ) { out = false; }
      }
      return out;
   }

   public String getSpeciesFullName( String spec ) throws Exception {
      try { createSpeciesTable(); }
      catch ( Exception e ) { ; }

      String query = "SELECT fullname FROM homolog_species WHERE spec = '" + spec + "'";
      String result = spec;
      try { result = dbhandler.query( query ).toString(); }
      catch ( Exception e ) { result = spec; }
      if ( result.equals( "???" ) ) result = spec;
      return result;
   }

   public Hashtable getAvailableSpecies() throws Exception {
      try { createSpeciesTable(); }
      catch ( Exception e ) { ; }

      Hashtable map = new Hashtable();
      Connection conn = dbhandler.getConnection();
      String query = "SELECT spec,fullname FROM homolog_species";
      if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
      try {
	 Statement st = conn.createStatement();
	 ResultSet rs = st.executeQuery( query );
	 for ( ; rs.next() ; )
	    map.put( rs.getObject( 1 ), rs.getObject( 2 ) );
      } catch ( Exception e ) { };
      return map;
   }

   public boolean put( String prot, String homolog, double score, 
		       String info, String species1, String species2 ) throws Exception {
      if ( ! hasSpecies( species1 ) ) return false;
      if ( ! hasSpecies( species2 ) ) return false;
      String table = createHomologTable( species1, species2 );
      String query = "INSERT INTO " + table + " (prot,homolog,score,info) " +
	 "VALUES ('" + prot + "', '" + homolog + "', '" + score + "', '" + info + "')";
      try { dbhandler.update( query ); } catch (Exception e) { return false; }
      return true;
   }

   public boolean put( String prot, String homolog, double score, 
		       String species1, String species2 ) throws Exception {
      if ( ! hasSpecies( species1 ) ) return false;
      if ( ! hasSpecies( species2 ) ) return false;
      String table = createHomologTable( species1, species2 );
      String query = "INSERT INTO " + table + " (prot,homolog,score) " +
	 "VALUES ('" + prot + "', '" + homolog + "', '" + score + "')";
      try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      return true;
   }

   public boolean hasHomolog( String prot, String homolog, String species1, 
			      String species2 ) throws Exception {
      if ( ! hasSpecies( species1 ) ) return false;
      if ( ! hasSpecies( species2 ) ) return false;
      String table = createHomologTable( species1, species2 );
      boolean out = false;

      String prots[] = prot.indexOf( ";" ) >= 0 ? prot.split( "\\;" ) : new String[] { prot };

      for ( int i = 0; i < prots.length; i ++ ) {
	 String lookup = prots[ i ];
	 String query = "SELECT homolog FROM " + table + " WHERE prot = '" + lookup + "'";

	 try {
	    String result = dbhandler.query( query ).toString();
	    out = true;
	    break;
	 } catch ( Exception e ) { out = false; }
      }
      return out;
   }

   public Vector getAllHomologs( String prot, String species1, String species2 ) 
      throws Exception {
      if ( ! hasSpecies( species1 ) ) return new Vector();
      if ( ! hasSpecies( species2 ) ) return new Vector();
      String table = createHomologTable( species1, species2 );
      Vector list = new Vector();

      String prots[] = prot.indexOf( ";" ) >= 0 ? prot.split( "\\;" ) : new String[] { prot };
      Connection conn = dbhandler.getConnection();

      for ( int i = 0; i < prots.length; i ++ ) {
	 String lookup = prots[ i ];
	 String query = "SELECT homolog FROM " + table + " WHERE prot = '" + lookup + "'";
	 try {
	    if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
	    Statement st = conn.createStatement();
	    ResultSet rs = st.executeQuery( query );
	    for ( ; rs.next() ; ) list.add( rs.getObject( 1 ) );
	 } catch ( Exception e ) { };
      }
      return list;
   }

   public Vector getAllHomologs( String prot, String species1 ) 
      throws Exception {
      if ( ! hasSpecies( species1 ) ) return new Vector();
      Vector list = new Vector();
      if ( availableSpecies == null ) availableSpecies = getAvailableSpecies();
      for ( Iterator it = availableSpecies.keySet().iterator(); it.hasNext(); ) {
	 String shortName = (String) it.next();
	 String species2 = (String) availableSpecies.get( shortName );
	 Vector v = getAllHomologs( prot, species1, species2 );
	 for ( int i = 0; i < v.size(); i ++ ) list.add( v.get( i ) );
      }
      return list;
   }

   public Vector getAllHomologsAndInfo( String prot, String species1, String species2 ) 
      throws Exception {
      if ( ! hasSpecies( species1 ) ) return new Vector();
      if ( ! hasSpecies( species2 ) ) return new Vector();
      String table = createHomologTable( species1, species2 );
      Vector list = new Vector();

      String prots[] = prot.indexOf( ";" ) >= 0 ? prot.split( "\\;" ) : new String[] { prot };
      Connection conn = dbhandler.getConnection();

      for ( int i = 0; i < prots.length; i ++ ) {
	 String lookup = prots[ i ];
	 String query =
	    "SELECT homolog,score,info FROM " + table + " WHERE prot = '" + lookup + "'";
	 if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
	 try {
	    Statement st = conn.createStatement();
	    ResultSet rs = st.executeQuery( query );
	    for ( ; rs.next() ; ) {
	       Map map = new Hashtable();
	       map.put( "homolog", rs.getObject( 1 ) );
	       map.put( "score", rs.getObject( 2 ) );
	       map.put( "species", species2 );
	       map.put( "info", rs.getObject( 3 ) != null ? rs.getObject( 3 ) : "" );
	       list.add( map );
	    }
	 } catch ( Exception e ) { };
      }
      return list;
   }

   public Vector getAllHomologsAndInfo( String prot, String species1 ) 
      throws Exception {
      if ( ! hasSpecies( species1 ) ) return new Vector();
      Vector list = new Vector();
      if ( availableSpecies == null ) availableSpecies = getAvailableSpecies();
      for ( Iterator it = availableSpecies.keySet().iterator(); it.hasNext(); ) {
	 String shortName = (String) it.next();
	 String species2 = (String) availableSpecies.get( shortName );
	 Vector v = getAllHomologsAndInfo( prot, species1, species2 );
	 for ( int i = 0; i < v.size(); i ++ ) list.add( v.get( i ) );
      }
      return list;
   }

   public Vector getAllHomologsAndInfo( String prot, String species1, String species2, 
					double betterThan ) throws Exception {
      if ( ! hasSpecies( species1 ) ) return new Vector();
      if ( ! hasSpecies( species2 ) ) return new Vector();
      Vector v = getAllHomologsAndInfo( prot, species1, species2 );
      Vector out = new Vector();
      for ( int i = 0, sz = v.size(); i < sz; i ++ ) {
	 Map map = (Map) v.get( i );
	 double score = ( (Double) map.get( "score" ) ).doubleValue();
	 if ( score >= betterThan ) out.add( map );
      }
      if ( v.size() > 0 && out.size() <= 0 ) {
	 Map map = new Hashtable();
	 map.put( "homolog", "None" );
	 out.add( map );
      }
      return out;
   }

   public Vector getAllHomologsAndInfo( String prot, String species1, double betterThan ) 
      throws Exception {
      if ( ! hasSpecies( species1 ) ) return new Vector();
      Vector list = new Vector();
      if ( availableSpecies == null ) availableSpecies = getAvailableSpecies();
      for ( Iterator it = availableSpecies.keySet().iterator(); it.hasNext(); ) {
	 String shortName = (String) it.next();
	 String species2 = (String) availableSpecies.get( shortName );
	 Vector v = getAllHomologsAndInfo( prot, species1, species2, betterThan );
	 for ( int i = 0; i < v.size(); i ++ ) list.add( v.get( i ) );
      }
      return list;
   }

   public boolean setDebug( String deb ) { return dbhandler.setDebug( deb ); }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.db.DBHomologHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.db.DBHomologHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
