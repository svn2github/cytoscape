package csplugins.isb.dreiss.httpdata.handlers.db;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

/**
 * Class <code>DBInteractionHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class DBInteractionHandler {
   public static final String serviceName = "interaction";

   protected String tableName;
   protected DBDataHandler dbhandler;
   protected HashMap sourcesMap = new HashMap();

   // each species/source combo. has its own table.
   //  the table row used in this space has the following fields:
   //Column  Field                   type
   //1       Interactor1             String
   //2	     Interaction type	     String 
   //3       Interactor2             String
   //4       pvalue                  double (use 0.0 for unknown)
   //5	     info		     String (e.g. "URL=http://blah|qual=GOOD")

   public DBInteractionHandler() { };

   public DBInteractionHandler( String tname ) throws Exception {
      dbhandler = new DBDataHandler( tname );
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
			     "i1 VARCHAR NOT NULL, " +
			     "itype VARCHAR NOT NULL, " +
			     "i2 VARCHAR NOT NULL, " +
			     "pval DOUBLE, " +
			     "info VARCHAR, " +
			     "CONSTRAINT UNIQUE_ID UNIQUE(I1,ITYPE,I2) )" );
      return table;
   }

   protected void createSourcesTable() throws Exception {
      String table = "interaction_sources";
      if ( dbhandler.createTable( table, " ( " +
				  "source VARCHAR NOT NULL, " +
				  "description VARCHAR NOT NULL, " +
				  "level INT, " +
				  "CONSTRAINT UNIQUE_ID UNIQUE(SOURCE,DESCRIPTION) )" ) ) {
      }
   }
   
   public boolean setDebug( String deb ) { return dbhandler.setDebug( deb ); }

   public boolean addSource( String source, String desc ) throws Exception {
      try {
	 dbhandler.query( "INSERT INTO interaction_sources (source,description) VALUES('" + 
			  source + "', '" + desc + "')" );
	 return true;
      } catch ( Exception e ) { };
      return false;
   }

   public boolean hasSource( String source ) throws Exception {
      if ( sourcesMap.containsKey( source ) ) return true;
      try { createSourcesTable(); } catch ( Exception e ) { };
      String query = "SELECT description FROM interaction_sources WHERE source = '" + source + "'";
      boolean out = false;
      try {
	 String result = dbhandler.query( query ).toString();
	 out = true;
	 sourcesMap.put( source, new Boolean( true ) );
      } catch ( Exception e ) { out = false; }
      return out;
   }

   public Hashtable getSources( int userLevel ) throws Exception {
      try { createSourcesTable(); }
      catch ( Exception e ) { ; }
      Hashtable map = new Hashtable();
      String query = "SELECT source,description,level FROM interaction_sources";
      if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
      Connection conn = dbhandler.getConnection();
      try {
	 Statement st = conn.createStatement();
	 ResultSet rs = st.executeQuery( query );
	 for ( ; rs.next() ; ) {
	    String src = (String) rs.getObject( 1 );
	    int level = ( (Integer) rs.getObject( 3 ) ).intValue();
	    if ( userLevel < level ) continue;
	    //if ( userLevel <= 5 && src.equals( "hprd" ) ) continue;
	    //if ( userLevel <= 5 && src.equals( "megarefnet" ) ) continue;
	    //if ( userLevel <= 9 && ( src.equals( "transfac" ) || src.equals( "hprd2" ) ) ) continue;
	    map.put( src, rs.getObject( 2 ) );
	 }
      } catch ( Exception e ) { if ( dbhandler.debug() ) e.printStackTrace(); }
      return map;
   }

   public boolean put( String i1, String itype, String i2, double pval, 
		       String source, String info, String species ) throws Exception {
      if ( ! hasSource( source ) ) return false;
      String table = createInteractionTable( species, source );
      if ( ! "None".equalsIgnoreCase( i1 ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2,pval,info) " +
	    "VALUES ('" + i1 + "', '" + itype + "', '" + i2 + "', '" + pval + "', '" + info + "')";
	 try { dbhandler.update( query ); } catch ( Exception e) { ; }
      }
      if ( ! "None".equalsIgnoreCase( i2 ) && ! table.startsWith( "prebind" ) ) {
	 info = reverseInfo( info );
	 String query = "INSERT INTO " + table + " (i1,itype,i2,pval,info) " +
	    "VALUES ('" + i2 + "', '" + itype + "', '" + i1 + "', '" + pval + "', '" + info + "')";
	 try { dbhandler.update( query ); } catch (Exception e ) { return false; }
      }
      return true;
   }

   protected String reverseInfo( String info ) {
      if ( info.indexOf( "2=" ) > 0 ) {
	 if ( info.indexOf( "1=" ) <= 0 ) info = info.replaceAll( "2=", "1=" );
	 else {
	    info = info.replaceAll( "2=", "ZZZQQQ=" );
	    info = info.replaceAll( "1=", "2=" );
	    info = info.replaceAll( "ZZZQQQ=", "1=" );
	 }
      }
      return info;
   }

   public boolean put( String i1, String itype, String i2, double pval, 
		       String source, String species ) throws Exception {
      if ( ! hasSource( source ) ) return false;
      String table = createInteractionTable( species, source );
      if ( ! "None".equalsIgnoreCase( i1 ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2,pval) " +
	    "VALUES ('" + i1 + "', '" + itype + "', '" + i2 + "', '" + pval + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { ; }	 
      }
      if ( ! "None".equalsIgnoreCase( i2 ) && ! table.startsWith( "prebind" ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2,pval) " +
	    "VALUES ('" + i2 + "', '" + itype + "', '" + i1 + "', '" + pval + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      }
      return true;
   }

   public boolean put( String i1, String itype, String i2, 
		       String source, String species ) throws Exception {
      if ( ! hasSource( source ) ) return false;
      String table = createInteractionTable( species, source );
      if ( ! "None".equalsIgnoreCase( i1 ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2) " +
	    "VALUES ('" + i1 + "', '" + itype + "', '" + i2 +  "')";
	 try { dbhandler.update( query ); } catch (Exception e) { ; }	 
      }
      if ( ! "None".equalsIgnoreCase( i2 ) && ! table.startsWith( "prebind" ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2) " +
	    "VALUES ('" + i2 + "', '" + itype + "', '" + i1 +  "')";
	 try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      }
      return true;
   }

   public boolean put( String i1, String itype, String i2, 
		       String species ) throws Exception {
      String table = createInteractionTable( species, "unknown" );
      if ( ! "None".equalsIgnoreCase( i1 ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2) " +
	    "VALUES ('" + i1 + "', '" + itype + "', '" + i2 + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { ; }	 
      }
      if ( ! "None".equalsIgnoreCase( i2 ) && ! table.startsWith( "prebind" ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2) " +
	    "VALUES ('" + i2 + "', '" + itype + "', '" + i1 + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      }
      return true;
   }

   public boolean put( String i1, String itype, String i2, 
		       double pval, String species ) throws Exception {
      String table = createInteractionTable( species, "unknown" );
      if ( ! "None".equalsIgnoreCase( i1 ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2,pval) " +
	    "VALUES ('" + i1 + "', '" + itype + "', '" + i2 + "', '" + 
	    pval + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { ; }	 
      }
      if ( ! "None".equalsIgnoreCase( i2 ) && ! table.startsWith( "prebind" ) ) {
	 String query = "INSERT INTO " + table + " (i1,itype,i2,pval) " +
	    "VALUES ('" + i2 + "', '" + itype + "', '" + i1 + "', '" + 
	    pval + "')";
	 try { dbhandler.update( query ); } catch (Exception e) { return false; }	 
      }
      return true;
   }

   public boolean hasInteraction( String i1, String i2, String source, String species ) throws Exception {
      if ( ! hasSource( source ) ) return false;
      String table = createInteractionTable( species, source );
      String query = "SELECT type FROM " + table +
	 " WHERE i1 = '" + i1 + "' AND i2 = '" + i2 + "'";

      boolean out = false;
      try {
	 String result = dbhandler.query( query ).toString();
	 out = true;
      } catch ( Exception e ) { out = false; }
      return out;
   }

   public Vector getAllInteractors( String i1, String species, String source ) throws Exception {
      return getAllInteractors( i1, species, source, false );
   }

   public Vector getAllInteractors( String i1, String species, String source, boolean justOneWay ) 
      throws Exception {
      if ( ! hasSource( source ) ) return new Vector();
      String table = createInteractionTable( species, source );

      String query1 = "SELECT i2 FROM " + table + " WHERE i1 = '" + i1 + "'";
      String query2 = "SELECT i1 FROM " + table + " WHERE i2 = '" + i1 + "'";
      
      Vector list = new Vector();
      try {
	 Connection conn = dbhandler.getConnection();
         if ( dbhandler.debug() ) System.err.println( "QUERY: " + query1 );
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery( query1 );
         for ( ; rs.next() ; ) list.add( rs.getObject( 1 ) );
         if ( ! justOneWay ) {
            if ( dbhandler.debug() ) System.err.println( "QUERY: " + query2 );
            rs = st.executeQuery( query2 );
            for ( ; rs.next() ; ) list.add( rs.getObject( 1 ) );
         }
      } catch ( Exception e ) { };
      return list;
   }

   public Vector getAllInteractions( String i1, String species, String source ) throws Exception {
      return getAllInteractions( i1, species, source, false );
   }

   public Vector getAllInteractions( String i1, String species, String source, boolean justOneWay ) 
      throws Exception {
      if ( ! hasSource( source ) ) return new Vector();
      String table = createInteractionTable( species, source );
      Vector list = new Vector();

      Connection conn = dbhandler.getConnection();
      for ( int i = 0; i < 2; i ++ ) {
         if ( justOneWay && i == 1 ) break;
         String query = i == 0 ? 
            "SELECT i2,itype FROM " + table + " WHERE i1 = '" + i1 + "'" :
            "SELECT i1,itype FROM " + table + " WHERE i2 = '" + i1 + "'";
         if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
         try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery( query );
            for ( ; rs.next() ; ) {
               Map map = new Hashtable();
               map.put( "partner", rs.getObject( 1 ) );
               map.put( "type", rs.getObject( 2 ) );
               list.add( map );
            }
         } catch ( Exception e ) { };
      }
      return list;
   }

   public Vector getAllInteractionsAndInfo( String i1, String species, String source ) throws Exception {
      return getAllInteractionsAndInfo( i1, species, source, false );
   }

   public Vector getAllInteractionsAndInfo( String i1, String species, String source,
					    boolean justOneWay ) throws Exception {
      if ( ! hasSource( source ) ) return new Vector();
      String table = createInteractionTable( species, source );
      Vector list = new Vector();

      String prots[] = i1.indexOf( ';' ) >= 0 ? i1.split( "\\;" ) : new String[] { i1 };
      Connection conn = dbhandler.getConnection();

      for ( int j = 0; j < prots.length; j ++ ) {
	 String lookup = prots[ j ];
	 for ( int i = 0; i < 2; i ++ ) {
	    if ( justOneWay && i == 1 ) break;
	    String query = i == 0 ? 
	       "SELECT i2,itype,pval,info FROM " + table + " WHERE i1 = '" + lookup + "'" :
	       "SELECT i1,itype,pval,info FROM " + table + " WHERE i2 = '" + lookup + "'";
	    if ( dbhandler.debug() ) System.err.println( "QUERY: " + query );
	    try {
	       Statement st = conn.createStatement();
	       ResultSet rs = st.executeQuery( query );
	       for ( ; rs.next() ; ) {
		  Map map = new Hashtable();
		  map.put( "partner", rs.getObject( 1 ) );
		  map.put( "type", rs.getObject( 2 ) );
		  map.put( "pval", rs.getObject( 3 ) != null ? rs.getObject( 3 ) : "" );
		  map.put( "source", source );
		  String info = (String) ( rs.getObject( 4 ) != null ? rs.getObject( 4 ) : "" );
		  if ( i == 1 && info.indexOf( "2=" ) > 0 ) info = reverseInfo( info );
		  map.put( "info", info );
		  list.add( map );
	       }
	    } catch ( Exception e ) { };
	 }
      }
      return list;
   }

   public Vector getAllInteractionsAndInfo( String i1, String species, String source, 
					    String infoConstraint, boolean justOneWay ) throws Exception {
      if ( ! hasSource( source ) ) return new Vector();
      Vector v = getAllInteractionsAndInfo( i1, species, source, justOneWay );
      boolean not = infoConstraint.startsWith( "!" );
      if ( not ) infoConstraint = infoConstraint.substring( 1 );
      Vector out = new Vector();
      for ( int i = 0, sz = v.size(); i < sz; i ++ ) {
	 Map map = (Map) v.get( i );
	 String info = (String) map.get( "info" );
	 
	 if ( info.indexOf( infoConstraint ) >= 0 ) {
	    if ( ! not ) out.add( map );
	 } else if ( not ) out.add( map );
      }
      if ( v.size() > 0 && out.size() <= 0 ) {
	 Map map = new Hashtable();
	 map.put( "partner", "None" );
	 out.add( map );
      }
      return out;
   }

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.db.DBInteractionHandler <port> <tableName>" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 server.addService( serviceName, "csplugins.httpdata.handlers.db.DBInteractionHandler", args[ 1 ] );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
