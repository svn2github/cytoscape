package csplugins.isb.dreiss.httpdata.handlers.sbeams;

import java.util.*;

import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.httpdata.xmlrpc.*;

public class SBEAMSDataHandler {
   public static final Map speciesToTableMap = new HashMap();
   public static final String sbeamsURL = "https://db.systemsbiology.net/sbeams/cgi/Proteomics/BrowseBioSequence.cgi";
   SBEAMSClient client;
   boolean use_strict = false;
   protected boolean debug = false;

   public SBEAMSDataHandler() throws Exception {
      System.setProperty( "javax.net.ssl.trustStore", "sbeamsTrustStore" );
      speciesToTableMap.put( "Saccharomyces cerevisiae protein", "30,14" ); //"21" );
      speciesToTableMap.put( "Homo sapiens protein", "31,22,2" ); //"26" );
      speciesToTableMap.put( "Saccharomyces cerevisiae", "30,14" ); //"21" );
      speciesToTableMap.put( "Homo sapiens", "31,22,2" ); //"26" );
      client = new SBEAMSClient();
   }

   public SBEAMSDataHandler( String userName, String password ) throws Exception {
      System.setProperty( "javax.net.ssl.trustStore", "sbeamsTrustStore" );
      speciesToTableMap.put( "Saccharomyces cerevisiae protein", "30,14" ); //"21" );
      speciesToTableMap.put( "Homo sapiens protein", "31,22,2" ); //"26" );
      speciesToTableMap.put( "Saccharomyces cerevisiae", "30,14" ); //"21" );
      speciesToTableMap.put( "Homo sapiens", "31,22,2" ); //"26" );
      client = new SBEAMSClient( userName, password );
   }

   public Map[] getClientResponse( String name, String table ) throws Exception {
      Map params = new HashMap();
      params.put( "QUERY_NAME", "PR_BrowseBioSequence" );
      params.put( "biosequence_set_id", table );
      params.put( "biosequence_name_constraint", "%" + name + "%" );
      if ( debug ) System.out.println("QUERYING SBEAMS: "+params);
      String response = client.getResponse( sbeamsURL, params );
      //System.out.println("RESPONSE: "+response);
      Map parsed = client.parseResponse( response );
      //if ( debug ) System.out.println("PARSED: "+parsed);
      Map out[] = new Map[] { parsed, params };
      return out;
   }

   public String preprocessName( String name ) {
      if ( name.startsWith( "GI" ) ) name = "gi|" + name.substring( 2 );
      else if ( name.startsWith( "EMB" ) ) name = "emb|" + name.substring( 3 );
      //else if ( name.startsWith( "IPI" ) ) name += ".";
      //else if ( name.toLowerCase().startsWith( "hs." ) ) name += " ";
      return name;
   }

   public int getBestResponse( Vector responses, String name ) {
      String nm = name.toUpperCase();
      for ( int i = 0, sz = responses.size(); i < sz; i ++ ) {
	 String resp = ( (String) responses.get( i ) ).toUpperCase();
	 if ( isSubstringAValidDescriptor( resp, nm ) ) return i;
      }
      return -1;
   }

   // assumes both resp and nm are "toUpperCase()"-ed
   protected boolean isSubstringAValidDescriptor( String resp, String nm ) {
      if ( resp.startsWith( nm ) ) resp = "|" + resp;
      if ( resp.indexOf( "|" + nm + "|" ) >= 0 ) return true;
      if ( resp.indexOf( ":" + nm + " " ) >= 0 ) return true;
      if ( resp.indexOf( "|" + nm + "." ) >= 0 ) return true;
      if ( resp.indexOf( ":" + nm + "." ) >= 0 ) return true;
      if ( resp.indexOf( ":" + nm + "," ) >= 0 ) return true;
      if ( resp.indexOf( ":" + nm + "|" ) >= 0 ) return true;
      if ( resp.indexOf( "(" + nm + " " ) >= 0 ) return true;
      if ( resp.indexOf( "(" + nm + ")" ) >= 0 ) return true;
      if ( resp.indexOf( "|" + nm + " " ) >= 0 ) return true;
      if ( resp.indexOf( "=" + nm + " " ) >= 0 ) return true;
      if ( resp.indexOf( "=" + nm + "|" ) >= 0 ) return true;
      if ( resp.indexOf( "=" + nm + ";" ) >= 0 ) return true;
      if ( resp.indexOf( " " + nm + " " ) >= 0 &&
		checkIfSpaceSurroundedIsOkay( resp, " " + nm + " " ) ) return true;
      else if ( resp.indexOf( " " + nm + "|" ) >= 0 &&
		checkIfSpaceSurroundedIsOkay( resp, " " + nm + "|" ) ) return true;
      else if ( resp.indexOf( " " + nm + ";" ) >= 0 &&
		checkIfSpaceSurroundedIsOkay( resp, " " + nm + ";" ) ) return true;
      return false;
   }

   // assumes both resp and test are "toUpperCase()"-ed
   protected boolean checkIfSpaceSurroundedIsOkay( String resp, String test ) {
      if ( resp.indexOf( "ATES" + test ) > 0 ) return false;
      if ( resp.indexOf( "ATE" + test ) > 0 ) return false;
      if ( resp.indexOf( "ES" + test ) > 0 ) return false;
      if ( resp.indexOf( "ING" + test ) > 0 ) return false;
      if ( resp.indexOf( " BY" + test ) > 0 ) return false;
      if ( resp.indexOf( " TO" + test ) > 0 ) return false;
      if ( resp.indexOf( " OF" + test ) > 0 ) return false;
      if ( resp.indexOf( " WITH" + test ) > 0 ) return false;
      if ( resp.indexOf( " FOR" + test ) > 0 ) return false;
      if ( resp.indexOf( " AND" + test ) > 0 ) return false;
      if ( resp.indexOf( " IN" + test ) > 0 ) return false;
      if ( resp.indexOf( test + "-LIKE" ) > 0 ) return false;
      if ( test.endsWith( " " ) ) {
	 if ( resp.indexOf( test + " BINDING" ) > 0 ) return false;
	 if ( resp.indexOf( test + "" ) > 0 ) return false;
      }
      return true;
   }
}
