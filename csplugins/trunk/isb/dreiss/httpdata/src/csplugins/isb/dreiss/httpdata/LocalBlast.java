package csplugins.isb.dreiss.httpdata;

import java.util.*;
import java.io.*;

import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 

import csplugins.isb.dreiss.util.Exec;

/**
 * Class <code>LocalBlast</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 *
 * Matrix:   BLOSUM62
 * Gap open: 11
 * Gap extension:  1
 * X_dropoff: 50
 * Expect: 300
 * Wordsize: 3
 *
 * Expectation value: 1e-03 =20
 * Filter: seg =20
 * Cost to open a gap: 10 =20
 * Cost to extend a gap: 1 =20
 * Max alignments: 250 =20
 * Substitution Matrix: BLOSUM62=20
 * Matrix:         BLOSUM90
 * Gap open:               9
 * Gap extension:  1=20
 * X_dropoff:              50
 * Expect:         100
 * Wordsize:               3
 */
public class LocalBlast implements Serializable {
   static double defaultEValueThreshold = 0.0001;
   String sequenceFileDirectory = "/data/seqdb/blastformat/";
   String blastCommand = "/package/genome/bin/blastall";
   String temporarySequenceFilename, tmpXmlFileName;

   String blastVariant;
   String sequence;
   String sequenceName; 
   String sourceSpecies;
   String targetSequenceFile;
   double eValueThreshold = defaultEValueThreshold;
   String xmlResult = "";
   String errorMessage = "";
   String actualBlastCommand = "";
   HomologSet homologSet;

   String matrixString =  "BLOSUM62";
   int costToOpenGap = 11;
   int costToExtendGap = 1;

   /************************************************************
    //---------------- blastall runtime parameters
  -p  Program Name [String]
  -d  Database [String]     default = nr
  -i  Query File [File In]    default = stdin
  -e  Expectation value (E) [Real]     default = 10.0
  -m  alignment view options:
          0 = pairwise,
          1 = query-anchored showing identities,
          2 = query-anchored no identities,
          3 = flat query-anchored, show identities,
          4 = flat query-anchored, no identities,
          5 = query-anchored no identities and blunt ends,
          6 = flat query-anchored, no identities and blunt ends,
          7 = XML Blast output,
          8 = tab-delimited fields [Integer]
              default = 0
  -o  BLAST report Output File [File Out]  Optional     default = stdout
  -F  Filter query sequence (DUST with blastn, SEG with others) [String]     default = T
  -G  Cost to open a gap (zero invokes default behavior) [Integer]    default = 0
  -E  Cost to extend a gap (zero invokes default behavior) [Integer]    default = 0
  -X  X dropoff value for gapped alignment (in bits) (zero invokes default behavior) [Integer]     default = 0
  -I  Show GI's in deflines [T/F]    default = F
  -q  Penalty for a nucleotide mismatch (blastn only) [Integer]    default = -3
  -r  Reward for a nucleotide match (blastn only) [Integer]    default = 1
  -v  Number of database sequences to show one-line descriptions for (V) [Integer]    default = 500
  -b  Number of database sequence to show alignments for (B) [Integer]    default = 250
  -f  Threshold for extending hits, default if zero [Integer]    default = 0
  -g  Perfom gapped alignment (not available with tblastx) [T/F]     default = T
  -Q  Query Genetic code to use [Integer]    default = 1
  -D  DB Genetic code (for tblast[nx] only) [Integer]     default = 1
  -a  Number of processors to use [Integer]    default = 1
  -O  SeqAlign file [File Out]  Optional
  -J  Believe the query defline [T/F]    default = F
  -M  Matrix [String]    default = BLOSUM62
  -W  Word size, default if zero [Integer]    default = 0
  -z  Effective length of the database (use zero for the real size) [Real]    default = 0
  -K  Number of best hits from a region to keep (off by default, if used a value of 
      100 is recommended) [Integer]     default = 0
  -P  0 for multiple hits 1-pass, 1 for single hit 1-pass, 2 for 2-pass [Integer]  default = 0
  -Y  Effective length of the search space (use zero for the real size) [Real]     default = 0
  -S  Query strands to search against database (for blast[nx], and tblastx).  3 is both, 1 
      is top, 2 is bottom [Integer]     default = 3
  -T  Produce HTML output [T/F]    default = F
  -l  Restrict search of database to list of GI's [String]  Optional
  -U  Use lower case filtering of FASTA sequence [T/F]  Optional     default = F
  -y  Dropoff (X) for blast extensions in bits (0.0 invokes default behavior) [Real]     default = 0.0
  -Z  X dropoff value for final gapped alignment (in bits) [Integer]    default = 0
  -R  PSI-TBLASTN checkpoint file [File In]  Optional
  -n  MegaBlast search [T/F]    default = F
  -L  Location on query sequence [String]  Optional
  -A  Multiple Hits window size (zero for single hit algorithm) [Integer]     default = 40
   ************************************************************/

   public LocalBlast (String blastVariant, String sequence, 
		      String sequenceName, String sourceSpecies,
		      String targetSequenceFile) throws Exception {
      this.blastVariant = blastVariant;
      this.sequence = sequence;
      this.eValueThreshold = eValueThreshold;
      this.targetSequenceFile = targetSequenceFile;
      this.sequenceName = sequenceName;
      this.sourceSpecies = sourceSpecies;

      temporarySequenceFilename = createTemporarySequenceFile (sequence);
   } // ctor

   public void setBlastCommand(String newValue) {
      this.blastCommand = newValue;
   }

   public void setSequenceFileDirectory(String newValue) {
      this.sequenceFileDirectory = newValue;
   }

   public void setEValueThreshold(double newValue) {
      this.eValueThreshold = newValue;
   }

   public void setMatrix (String newValue) {
      matrixString = newValue;
   }

   public void setCostToOpenGap (int newValue) {
      costToOpenGap = newValue;
   }

   public void setCostToExtendGap (int newValue) {
      costToExtendGap = newValue;
   }

   public void run () throws Exception {
      homologSet = new HomologSet (sequenceName, sourceSpecies, sequence, targetSequenceFile);
      execute ();
      parseXml ();
      deleteTemporaryFiles();
   }

   protected void deleteTemporaryFiles() throws Exception {
      new File( temporarySequenceFilename ).delete();
      new File( tmpXmlFileName ).delete();
   }

   protected void execute () throws Exception {
      String [] cmd = new String [17];
      cmd [0] = blastCommand;
      cmd [1] = "-p";
      cmd [2] = blastVariant;
      cmd [3] = "-d";
      cmd [4] = sequenceFileDirectory + targetSequenceFile;
      cmd [5] = "-e";
      cmd [6] = (new Double (eValueThreshold)).toString ();
      cmd [7] = "-m";
      cmd [8] = "7";   // xml output
      cmd [9] = "-M";
      cmd [10] = matrixString;
      cmd [11] = "-i";
      cmd [12] = temporarySequenceFilename;
      cmd [13] = "-G";
      cmd [14] = (new Integer (costToOpenGap)).toString ();
      cmd [15] = "-E";
      cmd [16] = (new Integer (costToExtendGap)).toString ();

      StringBuffer sb = new StringBuffer ();
      for (int i=0; i < cmd.length; i++) sb.append (cmd [i] + " ");
      actualBlastCommand = sb.toString ();

      Exec child = new Exec (cmd);
      int result = child.runThreaded ();

      xmlResult = child.getStdoutAsString ();
      errorMessage = child.getStderrAsString ();
      if (errorMessage.length () > 0)
	 throw new IllegalArgumentException (errorMessage + "\ncommand: " + sb.toString ());

   } // run

   private String createTemporarySequenceFile (String sequence) throws IOException {
      File tmpSequenceFile = File.createTempFile ("blast.", ".seq");
      tmpSequenceFile.deleteOnExit();
      String filename = tmpSequenceFile.getPath ();
      FileWriter fileWriter = new FileWriter (tmpSequenceFile);
      fileWriter.write (sequence);
      fileWriter.close ();
      return filename;

   } // createTemporarySequenceFile

   public String getErrorMessage () {
      return errorMessage;
   }

   public String getActualBlastCommand () {
      return actualBlastCommand;
   }

   public String getResultsInXml () {
      return xmlResult;
   }

   private void parseIteration (Element root) {
      Element iterationHits = root.getChild ("Iteration_hits");
      if (iterationHits == null)
	 return;

      List hits = iterationHits.getChildren ("Hit");
      ListIterator iterator = hits.listIterator ();
      int count = 0;
      while (iterator.hasNext ()) {
	 count++;
	 Element iteration = (Element) iterator.next ();
	 Homolog homolog = new Homolog (sequenceName, sourceSpecies, sequence, targetSequenceFile);
	 homolog = parseHit (iteration, homolog);
	 deduceTargetSpecies (homolog);
	 homologSet.addHit (homolog);
      }
  
   } // parseIteration

   /**
    *  in some cases (for instance, blasting against 'yeast.aa') the species of a homologous
    *  sequece is directly implied (in this case, 'Saccharomyces cerevisiae').  in other cases
    *  the defline must be consulted as well -- for example, the 'nr' (non-redundant) 
    *  fasta file sometimes has multiple species for a single sequence, separated on the defline
    *  by a control character.
    */
   private void deduceTargetSpecies (Homolog homolog) {
      if (targetSequenceFile.equalsIgnoreCase ("yeast.aa"))
	 homolog.setTargetSpecies ("Saccharomyces cerevisiae");
   }

   private Homolog parseHit (Element root, Homolog homolog) {
      String defLine = root.getChild ("Hit_def").getText().trim();
      int hitLength = -1;
      try {
	 hitLength = Integer.parseInt (root.getChild ("Hit_len").getText().trim());
      }
      catch (NumberFormatException ignore) {}
      homolog.setHitLength (hitLength);

      homolog.setRawDefLine (root.getChild ("Hit_def").getText().trim());
      parseRawDefLine (homolog);
  
      List hsps = root.getChild ("Hit_hsps").getChildren ("Hsp");
      ListIterator iterator = hsps.listIterator ();
      while (iterator.hasNext ()) {
	 Element e = (Element) iterator.next ();
	 homolog = parseHsp (e, homolog);
      }

      return homolog;
   } // parseHit

   private void parseRawDefLine (Homolog homolog) {
      // one example, from yeast:
      //     gi|6320049|ref|NP_010128.1| nuclear protein involved in silencing; Sas10p
      String s = homolog.getRawDefLine ();
      int start, end;
      start = s.indexOf ("gi|");
      if (start >= 0) {
	 start += 3;
	 end = s.indexOf ("|", start);
	 int giNumber = -1;
	 try {
	    homolog.setGiNumber (Integer.parseInt (s.substring (start, end)));
	 }
	 catch (NumberFormatException nfe) {}
      } // if foudn "gi|"

      start = s.indexOf ("|ref|");
      if (start > 0) {
	 start += 5;
	 end = s.indexOf ("|", start);
	 String refSeqID = s.substring (start, end);
	 int period = refSeqID.indexOf (".");
	 if (period >= 0)
	    refSeqID = refSeqID.substring (0,period);
	 homolog.setRefSeqID (refSeqID);
      }
   } // parseRawDefLine

   private Homolog parseHsp (Element root, Homolog homolog) {
      String bitScoreString = root.getChild ("Hsp_bit-score").getText().trim();
      String scoreString =  root.getChild ("Hsp_score").getText().trim();
      String eValueString = root.getChild ("Hsp_evalue").getText().trim();
      double bitScore = -1.0;
      int score = -1;
      double eValue = -1.0;
      try {
	 bitScore = Double.parseDouble (bitScoreString);
	 score = Integer.parseInt (scoreString);
	 eValue = Double.parseDouble (eValueString);
      }
      catch (NumberFormatException nfe) {}

      homolog.setScore (score);
      homolog.setEValue (eValue);

      return homolog;
   } // parseHsp

   protected void parseXml () throws Exception {
      if ( "".equals( xmlResult ) ) return;
      // first, strip off the xml header and doctype tag -- it confuses the xml parser
      File tmpXmlFile = File.createTempFile ("blast.", ".xml");
      tmpXmlFile.deleteOnExit();
      tmpXmlFileName = tmpXmlFile.getPath ();
      FileWriter fileWriter = new FileWriter (tmpXmlFile);

      int newHeadOfDocument = xmlResult.indexOf ("<BlastOutput>");
      fileWriter.write (xmlResult.substring (newHeadOfDocument));
      fileWriter.flush();
      fileWriter.close();
  
      SAXBuilder builder = new SAXBuilder (); 
      Document doc = builder.build (tmpXmlFileName);
      Element root = doc.getRootElement ();

      Element e0 = root.getChild ("BlastOutput_iterations");
      List iterations = e0.getChildren ("Iteration");
      ListIterator iterator = iterations.listIterator ();
      while (iterator.hasNext ()) {
	 Element iteration = (Element) iterator.next ();
	 parseIteration (iteration);
      }
   } // parseXml

   public HomologSet getResults () {
      return homologSet;
   }
} // class LocalBlast
