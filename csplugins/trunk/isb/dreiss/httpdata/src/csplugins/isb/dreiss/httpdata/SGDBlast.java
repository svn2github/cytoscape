package csplugins.isb.dreiss.httpdata;

import java.util.*;
import java.io.*;

/**
 * Class <code>SGDBlast</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class SGDBlast extends LocalBlast {
   public SGDBlast( String blastVariant, String sequence, 
		    String sequenceName, String sourceSpecies,
		    String targetSequenceFile) throws Exception {
      super( blastVariant, sequence, sequenceName, sourceSpecies, targetSequenceFile );
   }

   public void run() throws Exception {
      String url = "http://seq.yeastgenome.org/cgi-bin/SGD/nph-blast2sgd?";
      String query = "program=" + blastVariant + "&database=YeastORF-P&filtop=default&output=gapped&matrix=" +
	 matrixString + "&sthr=60&ethr=" + eValueThreshold + "&showal=25&sortop=pvalue&sequence=" + sequence;

      try {
	 homologSet = new HomologSet (sequenceName, sourceSpecies, sequence, targetSequenceFile);
	 xmlResult = NCBIUtils.getPage( url + query );
	 parseXML();
      } catch( Exception e ) { e.printStackTrace(); }
   }

   protected void parseXML() throws Exception {
      String signature = "Sequences producing High-scoring Segment Pairs:";
      int start = xmlResult.indexOf( signature );
      start += signature.length();
      int end = xmlResult.indexOf( "<hr>", start );
      String resultString = xmlResult.substring( start, end );
      String lines[] = resultString.split ("<a name");
      for ( int i = 1; i < lines.length; i ++ ) {
	 String line = lines[ i ];
	 int ind1 = line.indexOf( "<b>" ) + 3;
	 int ind2 = line.indexOf( "</b>", ind1 );
	 String name1 = line.substring( ind1, ind2 );
	 ind1 = ind2 + 5;
	 ind2 = line.indexOf( " ", ind1 );
	 String name2 = line.substring( ind1, ind2 );
	 ind1 = line.indexOf( "SGDID:" ) + 6;
	 ind2 = line.indexOf( ", ", ind1 );
	 String sgdid = line.substring( ind1 + 1, ind2 ); // trim off the "S" so it becomes an int
	 ind1 = line.indexOf( "<a href" ) - 6;
	 String score = line.substring( ind1, ind1 + 6 ).trim();
	 ind1 = line.indexOf( "\">", ind2 + 1 ) + 2;
	 ind2 = line.indexOf( "</a>", ind1 );
	 String evalue = line.substring( ind1, ind2 );
	 String hits = line.substring( ind2 + 5 ).trim();

	 Homolog homolog = new Homolog (sequenceName, sourceSpecies, sequence, targetSequenceFile);
	 homolog.setTargetSpecies( "Saccharomyces cerevisiae" );
	 homolog.setRefSeqID( name1 );
	 int ginumber = Integer.parseInt( getGINumber( name1 ) );
	 //System.err.println("GI = "+ginumber);
	 homolog.setGiNumber( ginumber );
	 homolog.setScore( Integer.parseInt( score ) );
	 homolog.setEValue( Double.parseDouble( evalue ) );
	 homolog.setHitLength( 1 );
	 homolog.setRawDefLine( line.trim() );
	 homologSet.addHit( homolog );
      }
   }

   protected String getGINumber( String orfName ) throws Exception {
      String url = "http://db.yeastgenome.org/cgi-bin/SGD/locus.pl?locus=";
      String rawPage = NCBIUtils.getPage( url + orfName );
      int ind1 = rawPage.indexOf( "list_uids=" );
      ind1 += "list_uids=".length();
      int ind2 = rawPage.indexOf( "&", ind1 );
      return rawPage.substring( ind1, ind2 );
   }
}
