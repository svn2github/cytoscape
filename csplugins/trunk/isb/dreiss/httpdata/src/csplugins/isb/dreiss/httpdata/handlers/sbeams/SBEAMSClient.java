package csplugins.isb.dreiss.httpdata.handlers.sbeams;

import java.io.*;
import java.net.*;
import java.util.*;

//==================================================================================
// HttpsClient by Matt Fitzgibbon, ma2t@u.washington.edu, 3 Feb 2003
// SBEAMSClient by David Reiss, dreiss@systemsbiology.org, 29 Sep 2003
//
// Can specifiy keystore on command line with -Djavax.net.ssl.trustStore=samplecerts
//==================================================================================

public class SBEAMSClient extends HttpsClient {
   String cookie = null;

   public SBEAMSClient() throws Exception { 
      if ( cookie == null ) {
	 String userPass[] = promptForUsernamePassword();
	 cookie = gimmieCookie( userPass[ 0 ], userPass[ 1 ] );
      } // end of if ()
   }

   public SBEAMSClient( String userName, String password ) throws Exception {
      cookie = gimmieCookie( userName, password );
   }

   public String getResponse( String url, String params ) throws Exception {
      if ( cookie == null ) {
	 String userPass[] = promptForUsernamePassword();
	 cookie = gimmieCookie( userPass[ 0 ], userPass[ 1 ] );
      } // end of if ()

      if ( cookie != null ) {
	 //System.out.println("Proceeding to POST with cookie: " + cookie);
	 if ( params.indexOf( "row_limit" ) < 0 ) params += "&row+limit=20";
	 if ( params.indexOf( "output_mode=tsv" ) < 0 ) params += "&output_mode=tsv";
	 if ( params.indexOf( "apply_action=QUERY" ) < 0 ) 
	    params += "&apply_action=QUERY";
	 if ( params.indexOf( "action=QUERY" ) < 0 ) params += "&action=QUERY";

	 Resp res = postRequest(url, params, cookie);
	 //System.out.println("Content-Type: " + res.contentType);
	 //System.out.println("Content:\n" + res.content);
	 return res.content;
      } else {
	 //System.out.println("Got no cookies");
	 throw new Exception( "Got no cookie for sending request to SBEAMS" );
      }
   }

   public String getResponse( String url, Map params ) throws Exception {
      StringBuffer out = new StringBuffer();
      if ( params.get( "row_limit" ) == null ) params.put( "row_limit", "20" );
      if ( params.get( "output_mode" ) == null ) params.put( "output_mode", "tsv" );
      if ( params.get( "apply_action" ) == null ) params.put( "apply_action", "QUERY" );
      if ( params.get( "action" ) == null ) params.put( "action", "QUERY" );
      
      for ( Iterator it = params.keySet().iterator(); it.hasNext(); ) {
	 String key = (String) it.next();
	 out.append( key + "=" );
	 out.append( URLEncoder.encode( (String) params.get( key ), "UTF8" ) );
	 out.append( "&" );		     
      }
      return getResponse( url, out.toString() );
   }

   // Return a hashmap of vectors, mapped by string->vector (column_header->column_values)
   // Assumes your parameters requested "tsv" output ("output_mode=tsv")
   public Map parseResponse( String response ) {
      Map out = new HashMap();
      String lines[] = response.split( "\n" );
      String heads[] = lines[ 0 ].split( "\t" );
      for ( int i = 0; i < heads.length; i ++ ) out.put( heads[ i ], new Vector() );
      for ( int i = 1; i < lines.length; i ++ ) {
	 String toks[] = lines[ i ].split( "\t" );
	 for ( int j = 0; j < toks.length; j ++ ) {
	    if ( toks[ j ].startsWith( "\"" ) ) toks[ j ] = toks[ j ].substring( 1 );
	    if ( toks[ j ].endsWith( "\"" ) ) // Trim off any starting/ending double quotes
	       toks[ j ] = toks[ j ].substring( 0, toks[ j ].length() - 1 );
	    ( (Vector) out.get( heads[ j ] ) ).add( toks[ j ] );
	 }
      }
      return out;
   }

   // Not yet implemented in a good way (does not hide password on terminal)
   protected String[] promptForUsernamePassword() {
      String out[] = new String[ 2 ];
      System.out.print( "Enter SBEAMS Username: " );
      try {
         String line;
         BufferedReader stdin = new BufferedReader( new InputStreamReader( System.in ) );
         while ( ( line = stdin.readLine() ) == null ) { };
         out[ 0 ] = line;
      } catch( Exception e ) { out[ 0 ] = "unknown"; }
      System.out.print( "Enter Password: " );
      try {
         String line;
         BufferedReader stdin = new BufferedReader( new InputStreamReader( System.in ) );
         while ( ( line = stdin.readLine() ) == null ) { };
         out[ 1 ] = line;
      } catch( Exception e ) { out[ 1 ] = "????????"; }
      //System.err.println( "UserPass: " + out[ 0 ] + " " + out[ 1 ] );
      return out;
   }

   public static void main(String argv[]) throws Exception {
      String userName = argv.length >= 1 ? argv[ 0 ] : null;
      String password = argv.length >= 2 ? argv[ 1 ] : null;

      String url = "https://db.systemsbiology.net/sbeams/cgi/Proteomics/BrowseBioSequence.cgi";
      //String params = "row_limit=20&QUERY_NAME=PR_BrowseBioSequence&" +
      //"biosequence_set_id=14&biosequence_name_constraint=YHL040C%25";
      Map params = new HashMap();
      params.put( "QUERY_NAME", "PR_BrowseBioSequence" );
      params.put( "row_limit", "20" );
      params.put( "biosequence_set_id", "14" );
      params.put( "biosequence_name_constraint", "YHL0%" );

      SBEAMSClient client = userName != null ? 
	 new SBEAMSClient( userName, password ) :
	 new SBEAMSClient();
      String response = client.getResponse( url, params );
      System.out.println( "RESPONSE: " + response );

      Map parsed = client.parseResponse( response );
      System.out.println( "PARSED: " + parsed );
   }
}
