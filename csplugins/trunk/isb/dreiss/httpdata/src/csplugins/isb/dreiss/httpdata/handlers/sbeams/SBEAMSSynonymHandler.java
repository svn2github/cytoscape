package csplugins.isb.dreiss.httpdata.handlers.sbeams;

import java.util.*;
import java.sql.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;
import csplugins.isb.dreiss.httpdata.handlers.db.DBSynonymHandler;

/**
 * Class <code>SBEAMSSynonymHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class SBEAMSSynonymHandler extends SBEAMSDataHandler {
   public static final String serviceName = "synonym";
   protected static DBSynonymHandler synonymHandler = null;

   public SBEAMSSynonymHandler() throws Exception {
      super();
   }

   public SBEAMSSynonymHandler( String userName, String password ) throws Exception {
      super( userName, password );
   }

   static DBSynonymHandler getDBSynonymHandler() throws Exception {
      if ( synonymHandler != null ) return synonymHandler;
      synonymHandler = new DBSynonymHandler( "synonyms" );
      return synonymHandler;
   }      

   public boolean put ( String name1, String name2, String species ) throws Exception {
      return getDBSynonymHandler().put( name1, name2, species );
   }

   public boolean putSynonymsString ( String name, String strng, String species ) throws Exception {
      return getDBSynonymHandler().putSynonymsString( name, strng, species );
   }

   public Vector getSynonyms( String name1, String species ) throws Exception {
      //Vector list = getDBSynonymHandler().getSynonyms( name1, species );
      //if ( list.size() > 0 && ( (String) list.get( 0 ) ).equals( "UNKNOWN" ) ) {
      // list.clear(); return list; }
      
      //if ( list.size() > 0 ) return list;
      Vector list = new Vector();
      try {
	 name1 = preprocessName( name1 );
	 String synString = getSynonymsString( name1, species );
	 if ( "|".equals( synString ) ) throw new Exception( "get outta here" );
	 if ( ! isSubstringAValidDescriptor( synString.toUpperCase(), name1.toUpperCase() ) ) 
	    throw new Exception( "get outta here" );
	 
	 String toks[] = synString.split( "\\|" );
	 for ( int i = 0; i < toks.length; i ++ ) {
	    String tok = toks[ i ].trim();
	    if ( tok.endsWith( "," ) || tok.endsWith( ";" ) ) tok = tok.substring( 0, tok.length() - 1 );
	    if ( i > 0 && toks[ i - 1 ].trim().equals( "gi" ) ) tok = "GI" + tok;
	    if ( tok.length() > 3 && ! tok.equalsIgnoreCase( name1 ) &&
		 ! list.contains( tok ) ) list.add( tok );
	    if ( tok.indexOf( ' ' ) > 0 ) {
	       String ttoks[] = tok.split( " " );
	       for ( int j = 0; j < ttoks.length; j ++ ) {
		  String ttok = ttoks[ j ].trim();
		  //if ( ! isSubstringAValidDescriptor( "|" + ttok.toUpperCase() + "|", name1.toUpperCase() ) )
		  // continue;
		  if ( ! ttok.equals( "" ) && ttok.indexOf( ':' ) > 0 ) {
		     if ( ttok.endsWith( "," ) || ttok.endsWith( ";" ) ) 
			ttok = ttok.substring( 0, ttok.length() - 1 );
		     if ( ttok.length() > 3 && ! ttok.equalsIgnoreCase( name1 ) &&
			  ! list.contains( ttok ) ) list.add( ttok );
		     String ntok = ttok.substring( ttok.indexOf( ':' ) + 1 );
		     if ( ntok.length() > 3 && ! ntok.equalsIgnoreCase( name1 ) &&
			  ! list.contains( ntok ) ) list.add( ntok );
		  } else if ( ttok.startsWith( "/" ) && ! ttok.startsWith( "/len=" ) &&
			      ! ttok.startsWith( "/clone_end" ) ) {
		     if ( ttok.startsWith( "/gb=" ) ) ttok = ttok.substring( "/gb=".length() );
		     else if ( ttok.startsWith( "/gi=" ) ) ttok = "GI" + ttok.substring( "/gi=".length() );
		     else if ( ttok.startsWith( "/ug=" ) ) ttok = ttok.substring( "/ug=".length() );
		     if ( ! tok.startsWith( "/" ) && ttok.length() > 3 && ! ttok.equalsIgnoreCase( name1 ) &&
			  ! list.contains( ttok ) ) list.add( ttok );
		  }
	       }
	    
	       String taxID = "Tax_Id=9606";
	       int ind = tok.indexOf( taxID );
	       if ( ind > 0 && ind + taxID.length() < tok.length() - 3 ) {
		  String ttok = tok.substring( ind + taxID.length() + 1 );
		  if ( ttok.endsWith( "," ) || ttok.endsWith( ";" ) ) 
		     ttok = ttok.substring( 0, ttok.length() - 1 );
		  if ( ttok.length() > 3 && ! ttok.equalsIgnoreCase( name1 ) &&
		       ! list.contains( ttok ) ) list.add( ttok );
	       }

	       if ( tok.indexOf( " /" ) > 0 ) {
		  String ttok = tok.substring( 0, tok.indexOf( " /" ) );
		  if ( ttok.endsWith( "," ) || ttok.endsWith( ";" ) ) 
		     ttok = ttok.substring( 0, ttok.length() - 1 );
		  if ( ttok.length() > 3 && ! ttok.equalsIgnoreCase( name1 ) &&
		       ! list.contains( ttok ) ) list.add( ttok );
	       }
	    }
	 }
      } catch( Exception e ) { /*e.printStackTrace();*/ }
      //System.err.println("HERE: "+list+" "+list.size());
      //if ( list.size() <= 0 ) list.add( "UNKNOWN" );
      //for ( int i = 0; i < list.size(); i ++ )
      // getDBSynonymHandler().put( name1, (String) list.get( i ), species );
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
      if ( "".equals( name ) ) return "";

      name = preprocessName( name );

      String test = getDBSynonymHandler().getSynonymsString( name, species );
      //System.err.println("*********HERE1: "+name+" "+species+" "+test);
      if ( test.indexOf( "UNKNOWN" ) >= 0 ) return "";
      if ( ! "".equals( test ) ) return test;

      if ( name.startsWith( "gi|" ) ) {
	 test = getDBSynonymHandler().getSynonymsString( "GI" + name.substring( 3 ), species );
	 //System.err.println("*********HERE1a: "+name+" "+species+" "+test);
	 if ( test.indexOf( "UNKNOWN" ) >= 0 ) return "";
	 if ( ! "".equals( test ) ) return test;
      }

      //System.err.println("*********HERE2: "+name+" "+test);

      String table = (String) speciesToTableMap.get( species );
      //System.err.println("*********HERE3: "+name+" "+species+" "+table);
      if ( table == null ) return "";

      Map parsedAndParams[] = getClientResponse( name, table );
      Map parsed = parsedAndParams[ 0 ];
      String out = "|";
      //System.err.println("*********PARSED: "+parsed);

      Vector names = (Vector) parsed.get( "biosequence_name" );
      if ( names.size() <= 0 ) {
	 Map params = parsedAndParams[ 1 ];
	 params.remove( "biosequence_name_constraint" );
	 params.put( "biosequence_desc_constraint", "%" + name + "%" );
	 String response = client.getResponse( sbeamsURL, params );
	 parsed = client.parseResponse( response );
	 //System.err.println("*********PARSED2: "+parsed);
	 names = (Vector) parsed.get( "biosequence_name" );
      } 

      for ( int i = 0; i < names.size(); i ++ ) {
	 String gname = (String) names.get( i );
	 if ( gname != null && gname.length() > 3 && out.indexOf( gname + "|" ) < 0 ) out += gname + "|";
	 if ( parsed.get( "gene_name" ) != null ) {
	    gname = (String) ( (Vector) parsed.get( "gene_name" ) ).get( i );
	    if ( gname != null && gname.length() > 3 && out.indexOf( gname + "|" ) < 0 ) out += gname + "|";
	 }
	 if ( parsed.get( "biosequence_gene_name" ) != null ) {
	    gname = (String) ( (Vector) parsed.get( "biosequence_gene_name" ) ).get( i );
	    if ( gname != null && gname.length() > 3 && out.indexOf( gname + "|" ) < 0 ) out += gname + "|";
	 }
	 if ( parsed.get( "biosequence_desc" ) != null ) {
	    gname = (String) ( (Vector) parsed.get( "biosequence_desc" ) ).get( i );
	    if ( gname != null && gname.length() > 3 && out.indexOf( gname + "|" ) < 0 ) out += gname + "|";
	 }
      }
      //System.out.println("*************GOT HERE: (" + name + ")\n\t\t"+out);
      if ( ! "".equals( out ) && ! "|".equals( out ) ) {
	 if ( out.toUpperCase().indexOf( "|" + name.toUpperCase() + "|" ) < 0 ) out = "|" + name + out;
	 putSynonymsString( name, out, species );
      } else putSynonymsString( name, "|UNKNOWN|", species );
      return out;
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

   public static void main (String [] args) throws Exception {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java csplugins.httpdata.handlers.sbeams.SBEAMSSynonymHandler <port> [<UserName> <Password>]" );
	 System.exit( -1 );
      }

      try {
	 MyXmlRpcServer server = new MyXmlRpcServer( args );
	 if ( args.length > 1 ) server.addService( serviceName, 
						   "csplugins.httpdata.handlers.sbeams.SBEAMSSynonymHandler", 
						   args[ 1 ], args[ 2 ] );
	 else server.addService( serviceName, 
				 "csplugins.httpdata.handlers.sbeams.SBEAMSSynonymHandler" );
	 
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
