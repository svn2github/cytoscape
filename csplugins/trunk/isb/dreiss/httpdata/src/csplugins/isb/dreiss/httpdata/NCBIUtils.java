package csplugins.isb.dreiss.httpdata;

import java.io.*;
import java.net.*;

/**
 * Class <code>NCBIUtils</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class NCBIUtils {
   public static String getPage (String urlString) throws Exception {
      //System.err.println( "querying url: " + urlString );
      return getPage (new URL (urlString));
   }

   public static String getPage (URL url) throws Exception {
      int characterCount = 0;
      StringBuffer result = new StringBuffer ();

      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
      int responseCode = urlConnection.getResponseCode ();
      String contentType = urlConnection.getContentType ();

      int contentLength = urlConnection.getContentLength ();

      String contentEncoding = urlConnection.getContentEncoding ();

      if (responseCode != HttpURLConnection.HTTP_OK)
	 throw new IOException ("\nHTTP response code: " + responseCode);

      BufferedReader theHTML = new BufferedReader 
	 (new InputStreamReader (urlConnection.getInputStream ()));
      String thisLine;
      while ((thisLine = theHTML.readLine ()) != null) {
	 result.append (thisLine);
	 result.append (" ");
      }
      return result.toString ();
   }

   public static String getGINumber( String name ) throws Exception {
      return getGINumber( name, "Saccharomyces cerevisiae" );
   }

   public static String getGINumber( String pname, String species ) throws Exception {
      String pname2 = null;
      if ( ! pname.startsWith( "NP_" ) && ! pname.startsWith( "XP_" ) ) pname2 = getRefSeqFromOrfName( pname );
      if ( pname2 != null && ! "".equals( pname2 ) && pname2.length() > 0 ) pname = pname2;
      
      String searchURL = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Protein&cmd=search&doptcmdl=DocSum&term=";
      species = species.replace( ' ', '+' );
      String rawSearchPage = getPage( searchURL + pname + "+" + species );

      int ind1 = rawSearchPage.indexOf( "gi|" );
      if ( ind1 < 0 ) return "";
      int ind2 = rawSearchPage.indexOf( "|", ind1 + 3 );
      if ( ind2 < 0 || ind2 <= ind1 ) return "";
      String giNum = rawSearchPage.substring( ind1 + 3, ind2 );
      return giNum;
   }

   public static String getCommonNameFromGINumber( String giNumber ) throws Exception {
      if ( giNumber == null || "".equals( giNumber ) || giNumber.indexOf( ' ' ) >= 0 ) return "";
      String searchURL = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Protein&cmd=search&doptcmdl=DocSum&term=";
      String rawSearchPage = getPage( searchURL + giNumber );
      
      int ind1 = rawSearchPage.indexOf( "p [Saccharomyces cerevisiae]" );
      int ind2 = ind1 - 1;
      while( rawSearchPage.charAt( ind2 ) != ';' ) ind2 --;
      ind2 += 2;
      return rawSearchPage.substring( ind2, ind1 );
   }

   public static String getOrfNameFromRefSeq( String refSeq ) throws Exception {
      String searchURL = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Protein&cmd=search&doptcmdl=DocSum&term=";
      String rawSearchPage = getPage( searchURL + refSeq + "+Saccharomyces+cerevisiae" );
      
      int ind1 = rawSearchPage.indexOf( "p [Saccharomyces cerevisiae]" );
      int ind2 = ind1 - 1;
      while( rawSearchPage.charAt( ind2 ) != ';' ) ind2 --;
      ind2 += 2;
      return rawSearchPage.substring( ind2, ind1 );
   }

   public static String getRefSeqFromOrfName( String yeastOrf ) throws Exception {
      String searchURL = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=Protein&cmd=search&doptcmdl=DocSum&term=";
      searchURL += yeastOrf;
      if ( ! yeastOrf.endsWith( "p" ) ) searchURL += "p";
      searchURL += "+Saccharomyces+cerevisiae";
      String rawSearchPage = getPage( searchURL );
      
      int ind1 = rawSearchPage.indexOf( "|NP_" );
      if ( ind1 < 0 ) ind1 = rawSearchPage.indexOf( "|XP_" );
      int ind2 = rawSearchPage.indexOf( '.', ind1 );
      if ( ind2 - ind1 > 10 || ind2 - ind1 <= 0 ) ind2 = rawSearchPage.indexOf( '|', ind1 );
      return rawSearchPage.substring( ind1 + 1, ind2 );
   }

   public static String getSequenceFromGINumber( String giNumber ) throws Exception {
      return getSequenceFromGINumber( giNumber, null );
   }

   public static String getOrfNameFromGINumber( String giNumber ) throws Exception {
      if ( giNumber == null || "".equals( giNumber ) || giNumber.indexOf( ' ' ) >= 0 ) return "";
      String url = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=Protein&dopt=GenPept&val=";
      String rawPage = getPage( url + giNumber );

      String signature = "/locus_tag=\"";
      int start = rawPage.indexOf (signature);
      if (start < 0) return "";

      start += signature.length ();

      int end = rawPage.indexOf ("\"", start + 1);
      String seqText = rawPage.substring (start,end);
      return seqText;
   }

   public static String getSequenceFromGINumber( String giNumber, String species ) throws Exception {
      if ( giNumber == null || "".equals( giNumber ) || giNumber.indexOf( ' ' ) >= 0 ) return "";
      String url = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=Protein&dopt=GenPept&val=";
      String rawPage = getPage( url + giNumber );
      
      if ( species != null && ! "".equals( species ) ) { // double check the species
	 if ( rawPage.toLowerCase().indexOf( species.toLowerCase() ) < 0 ) 
	    System.err.println( "Uh oh -- sequence retrieved may not be of species " + species );
      }

      String signature = "ORIGIN";
      int start = rawPage.indexOf (signature);
      if (start < 0) return "";

      start += signature.length ();

      int end = rawPage.indexOf ("//", start + 1);
      String seqText = rawPage.substring (start,end);
      String [] seqLines = seqText.split ("\n");

      StringBuffer sb = new StringBuffer ();

      for (int i=0; i < seqLines.length; i++) {
	 String line = seqLines [i].trim ();
	 if (line.length () == 0) continue;
	 String [] tokens = line.split ("\\s+");
	 for (int j=0; j < tokens.length; j++) {
	    String token = tokens [j];
	    if (token.matches ("\\d+")) continue;
	    sb.append (token);
	 }
      } 
      return sb.toString().toUpperCase();
   }

   public static String getSequence( String name ) throws Exception {
      return getSequence( name, "Saccharomyces cerevisiae" );
   }

   public static String getSequence( String name, String species ) throws Exception {
      String out = "";
      try {
	 if ( name.startsWith( "EST" ) ) System.err.println( "Unknown sequence type: EST" );
	 else if ( name.startsWith( "IPI" ) ) System.err.println( "Unknown sequence type: IPI" );
	 else if ( name.startsWith( "Hs." ) ) System.err.println( "Unknown sequence type: Hs." );
	 else {
	    try {
	       if ( name.startsWith( "GI" ) ) name = name.substring( 2 );
	       Integer.parseInt( name );
	       out = getSequenceFromGINumber( name, species );
	    } catch ( Exception ee ) {
	       try {
		  out = getSequenceFromGINumber( getGINumber( name, species ) );
	       } catch ( Exception eee ) { out = ""; }
	    }
	 }
      } catch ( Exception e ) { out = ""; }
      return out;
   }

   public static void main( String args[] ) {
      try {
	 System.out.println( getGINumber( "Ref2" ) );
	 System.out.println( getSequence( getGINumber( "Ref2" ) ) );
	 System.out.println( getRefSeqFromOrfName( "Ref2" ) + "\n" );

	 System.out.println( getGINumber( "NP_010481" ) );
	 System.out.println( getSequence( getGINumber( "NP_010481" ) ) );
	 System.out.println( getOrfNameFromRefSeq( "NP_010481" ) );
      } catch( Exception e ) {
	 e.printStackTrace();
      }
   }
}
