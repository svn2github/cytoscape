// PreBind.java: find (from PreBind: http://bind.ca/index2.phtml?site=prebind) all
// the proteins which (speculatively) interact with the one supplied
//---------------------------------------------------------------------------
// $Revision$ $Date$
// $Author$
//---------------------------------------------------------------------------
package csplugins.isb.dreiss.getInteractions;
//------------------------------------------------------------------------------
import java.io.*;
import java.net.*;
import java.util.*;
//---------------------------------------------------------------------------
/**
 *  This class makes the html web service PreBIND available to java clients. 
 *  It does a brute force http get, and simple parsing
 *  of the resulting html text, to extract proteins which may interact
 *  with the protein nambed by refseq NP number in the constructors argument.
 *
 *  As of this writing (2004.08.06) PreBIND distinguishes between <i>definite</i> (curated), <i>probable</i>
 *  and <i>possible</i> interactions.  (They say 'Unknown' rather than 'possible', and 'Yes' when curated.)
 *  A more nuanced and possibly numeric ranking might be available someday.
 *
 *  When interactions are found, they are returned as a HashMap, in which the
 *  keys are the NP numbers of the putative interactors, and the value is
 *  a 3-element list:  the PreBIND likelihood assessment (possible or probable),
 *  an ordinal number -- the position of this interactor on the PreBIND web page --
 *  which helps the client to create an html anchor url, which directly displays
 *  the portion of that webpage giving evidence for this link, and 3rd, the common
 *  name listed for the NP number.
 * 
 * @see  <a href="http://bind.ca/index2.phtml?site=prebind">PreBIND</a>
 * @see  csplugins.trial.pshannon.prebind.unitTests.PreBindTest
 * 
 */
public class PreBind {
   String np; // doesnt require NPs anymore.
   String [] possiblePartners;
   static private final String urlHead = "http://prebind.bind.ca/cgi-bin/prebind_cgi?";
   String rawText, species, url = urlHead;
   HashMap nps = new HashMap ();

   public PreBind (String np, String species) throws Exception {
      this.np = np;
      this.species = species;

      url = constructURL( np, species );
      rawText = getPartialPage (url);
      extractNPs ();
   }

   public String getNP () { return np; }
   public HashMap getNPs () { return nps; }
   public String getURL () { return url; }
   public String getRawText () { return rawText; }
   private String getPartialPage (String urlString) throws Exception {
      return getPartialPage (new URL (urlString));
   }

   public static String constructURL( String name, String species ) throws Exception {
      String url = urlHead;

      String taxId = "9606";
      if ( "Homo sapiens".equalsIgnoreCase( species ) || "Human".equalsIgnoreCase( species ) ) taxId = "9606";
      else if ( "Saccharomyces cerevisiae".equalsIgnoreCase( species ) || "yeast".equalsIgnoreCase( species ) ) 
	 taxId = "4932";
      else if ( "Mus musculus".equalsIgnoreCase( species ) || "Mouse".equalsIgnoreCase( species ) )
	 taxId = "10090";
      else throw new Exception( "Error: species " + species + " is currently not supported by PreBIND" );

      if ( ! ( name.startsWith( "NP_" ) || name.startsWith( "XP_" ) ) ) {      
	 url += "name=" + name + "&search_type=nametax&sloi=1&=fnip=1&taxid=" + taxId + "" ;
      } else {
	 url += "accession=" + name + "&search_type=accession&sloi=1&=fnip=1";
      }

      //System.err.println("URL: "+url);
      return url;
   }

   /**
    *  terminates at the first 'Unknown' -- which indicates we are past the
    *  last 'Probably' (these two words answer the question:  is this interaction real?).
    */
   public String getPartialPage (URL url) throws Exception {
      int characterCount = 0;
      StringBuffer result = new StringBuffer ();

      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
      int responseCode = urlConnection.getResponseCode ();
      String contentType = urlConnection.getContentType ();

      int contentLength = urlConnection.getContentLength ();

      String contentEncoding = urlConnection.getContentEncoding ();

      if (responseCode != HttpURLConnection.HTTP_OK)
	 throw new IOException ("\nHTTP response code: " + responseCode + "; URL=" + url.toString());

      BufferedReader theHTML = new BufferedReader 
	 (new InputStreamReader (urlConnection.getInputStream ()));
      String thisLine;
      boolean done = false;

      while (!done) {
	 thisLine = theHTML.readLine ();
	 if (thisLine == null) {
	    done = true;
	    break;
	 }
	 /*if (strongCandidatesOnly) {
	   int location = thisLine.indexOf ("Unknown");
	   if (location >= 0) {
	   done = true;
	   break;
	   }
	   } else {*/ // look for the end of the listing of candidates
	 int location = thisLine.indexOf ("Potential interactors and evidence");
	 if (location >= 0) {
	    // System.out.println ("  ---------terminating on title --------------: " + location);
	    done = true;
	    break;
	 }
	 //}
	 result.append (thisLine);
	 result.append (" ");
      } // while

      return result.toString ();
   } // getPartialPage

   private void extractNPs () {
      // System.out.println (rawText);
      String signature = "cgi-bin/prebind_cgi?accession=NP_";
      boolean done = false;
      int base = 0;

      int tempInd = rawText.indexOf( "Clicking on this number will take you to a more detailed view of these co-occurences" );
      if ( tempInd > 0 ) {
	 rawText = rawText.substring( tempInd );
	 tempInd = rawText.indexOf( "<strong>name</strong>" );
	 if ( tempInd > 0 ) rawText = rawText.substring( tempInd + 21 );
      }

      int count = 0;
      while (!done) {
	 int start = rawText.indexOf (signature, base);
	 if (start < 0) {
	    done = true;
	    continue;
	 }
	 start += signature.length () - 3;
	 String endSignature = "&search";
	 int end = rawText.indexOf (endSignature, start);
	 if (end < 0) {
	    done = true;
	    continue;
	 }
	 String preamble = rawText.substring (base, end);
	 int probably = preamble.indexOf ("Probably");
	 int unknown  = preamble.indexOf ("Unknown");
	 int yes  = preamble.indexOf ("Yes");
	 int no  = preamble.indexOf ("No");
	 //System.out.println ("probably: " + probably);
	 //System.out.println ("unknown: " + unknown);
	 String np = rawText.substring (start,end);

	 String cnamePrefix = //"<td bgcolor=\"lightsteelblue\" width=\"90%\" height=\"20\">\n<strong>";
	    "<td width=\"5%\" height=\"20\">\n<strong>";
	 int cnameStart = preamble.indexOf( cnamePrefix ) + cnamePrefix.length();
	 cnameStart = preamble.indexOf( "<strong>", cnameStart ) + "<strong>".length();
	 int cnameEnd = preamble.indexOf( "</strong>", cnameStart );
	 String cname = preamble.substring( cnameStart, cnameEnd );
	 if ( cname.length() > 10 ) cname = "";

	 count ++;
	 Vector neighborMetaData = new Vector ();
	 neighborMetaData.add (new Integer (count));
	 String assessmentString = "Unknown";
	 if (probably >= 0) assessmentString = "Probably";
	 else if ( yes >= 0 ) assessmentString = "Yes";
	 else if ( no >= 0 ) assessmentString = "No";
	 if (assessmentString.equals ("Unknown")) assessmentString = "Possibly";
	 neighborMetaData.add (assessmentString);
	 neighborMetaData.add( cname );
	 if (!nps.containsValue (np)) nps.put (np, neighborMetaData);	 
	 base = end;
      } // while not done
   } // extractNPs
} // class PreBind
