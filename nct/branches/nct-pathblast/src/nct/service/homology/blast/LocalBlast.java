
//============================================================================
// 
//  file: LocalBlast.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.service.homology.blast;


import java.lang.*;
import java.util.*;
import java.io.*;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import nct.graph.SequenceGraph;
import nct.service.homology.HomologyModel;
import nct.service.synonyms.SynonymMapper;

/**
 * A Blast implementation of the HomologyModel interface that runs Blast locally.
 * LocalBlast needs to know how to actually run Blast locally. This is accomplished 
 * by specifying these details in a properties file. The specific properties that 
 * this command looks for are:
<ul>
<li>blast.blastall.command - The actual command used to run blast. The command
specification is expected to contain 3 different sentinels which provide details
necessary for the command to run which are specified at runtime.
	<ul>
	<li>TARGET_DB</li> - The name (and path) specifying the target database to
	use for the Blast search.
	<li>QUERY_SEQS</li> - The name (and path) specifying the FASTA file containing
	the query sequences to be Blasted against the target database.
	<li>OUTPUT_FILE</li> - The output file location.  
	<li>E_VALUE</li> - The expectation value to use for the -e argument in blastall. 
	</ul>
</li>
<li>blast.blastall.location - The location of the blastall executable.</li>
<li>blast.formatdb.command - The actual command used to run formatdb</li>
<li>blast.formatdb.location - The location of the formatdb executable.</li>
</ul>
Here is an example of a blast.properties file:
<pre>
blast.blastall.command=blastall -p blastp -d TARGET_DB -i QUERY_SEQS -m 7 -e E_VALUE -o OUTPUT_FILE
blast.blastall.location=/cellar/users/mes/software/blast-2.2.12/bin
blast.formatdb.command=formatdb -i TARGET_DB
blast.formatdb.location=/cellar/users/mes/software/blast-2.2.12/bin
</pre>
 */
public class LocalBlast implements HomologyModel { 
		
	/**
	 * Sentinel used to specify the target database name in the property string.
	 */
	public static final String TARGET_DB_SENTINEL = "TARGET_DB";

	/**
	 * Sentinel used to specify the query sequence name in the property string.
	 */
	public static final String QUERY_SENTINEL = "QUERY_SEQS";

	/**
	 * Sentinel used to specify the output file name. 
	 */
	public static final String OUTPUT_FILE = "OUTPUT_FILE";

	/**
	 * Sentinel used to specify the expectation value threshold.
	 */
	public static final String E_VALUE = "E_VALUE";

	/**
	 * The file that contains the blast output to be parsed.
	 */
	protected String blastOutputFile; 

	/**
	 * The map where the evalues between proteins are stored.  This must be reset each
	 * time expectationValues() is called.
	 */
	protected Map<String,Map<String,Double>> evalues; 

	/**
	 * A properties object that contains the installation specific details of where
	 * and how to run Blast.
	 */
	protected Properties props;

	/**
	 * Used for mapping various ids to types of synonyms
	 */
	protected SynonymMapper synonyms;

	/**
	 * A string representation of a double value that is the expectation
	 * value threshold for the blastall command (-e argument).
	 */
	protected String eValueThreshold;

	/**
	 * Whether or not to normalize e-values based on database size.  The normalization
	 * function is evalue*matchLength/dbLength.
	 */
	protected boolean normalize;


	/**
	 * Constructor. Synonyms are set to null, meaning you get whatever id is present
	 * in the blast results.
	 * @param props The Properties that specify how and where to run Blast.
	 * @param tmpOutFile A temporary output file that holds the blast output. 
	 */
	public LocalBlast(Properties props, String tmpOutFile, double eValue ) {
		this (props,null,tmpOutFile,eValue);
	}

	/**
	 * Constructor. 
	 * @param props The Properties that specify how and where to run Blast.
	 * @param synonyms A SynonymMapper object used for mapping the ids in the
	 * blast results to meaningful names.  If it is set to null, you'll simply
	 * get the id found in the blast results.
	 * @param tmpOutFile A temporary output file that holds the blast output. 
	 */
	public LocalBlast(Properties props, SynonymMapper synonyms, String tmpOutFile, double eValue ) { 
		this.synonyms = synonyms;
		this.normalize = false; // we should never normalize.  It is invalid.
		try { 
			this.props = props;
			blastOutputFile = tmpOutFile; 
			eValueThreshold = Double.toString(eValue); 
		} catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * Returns a map of expectation values between nodes of the specified graphs. 
	 * @param sg1 The first graph containing sequences whose Blast expectation values
	 * are to be found.
	 * @param sg2 The second graph containing sequences whose Blast expectation values
	 * are to be found.
	 * @return A map of expectation values between nodes of the specified graphs. 
	 */
	public Map<String,Map<String,Double>> expectationValues( SequenceGraph sg1, SequenceGraph sg2 ) {

		evalues = new HashMap<String,Map<String,Double>>();
		if ( (sg1.getDBType() != SequenceGraph.FASTA && 
			  sg1.getDBType() != SequenceGraph.BLAST) ||
			 (sg2.getDBType() != SequenceGraph.FASTA && 
			  sg2.getDBType() != SequenceGraph.BLAST) ) 
			 return evalues;

		String db1 = sg1.getDBLocation() + "/" + sg1.getDBName();
		String db2 = sg2.getDBLocation() + "/" + sg2.getDBName();
		System.out.println("DB1 " + db1);
		System.out.println("DB2 " + db2);

		try {
				
		if ( sg1.getDBType() == SequenceGraph.FASTA )  
			createBlastDB(db1);

		if ( sg2.getDBType() == SequenceGraph.FASTA )  
			createBlastDB(db2);
				
		// run the smaller file against the larger file
		if ( sg1.numberOfNodes() > sg2.numberOfNodes() )
			runBlast(db1, db2);
		else
			runBlast(db2, db1);

	
		XMLReader xr = XMLReaderFactory.createXMLReader();
		HitHandler hh = new HitHandler();
		xr.setContentHandler(hh);
		xr.setErrorHandler(hh);

		xr.parse(new InputSource( new BlastXMLFileFilterInputStream( new FileInputStream(blastOutputFile), false ) ) ); 


		} catch (Exception e) {
			e.printStackTrace();
		}

		return evalues;
	}

	/**
	 * Runs the blast command specified in the properties file (blast.blastall.command).
	 * Use the static public Strings defined in this class as placeholders while defining
	 * the commands in the attributes file. Also uses the property blast.blastall.location.
	 */
	private InputStream runBlast(String dbName, String querySeqName ) 
		throws InterruptedException, IOException, SecurityException, NullPointerException, IllegalArgumentException {
		String blastCmd = props.getProperty("blast.blastall.command");
		blastCmd = blastCmd.replaceAll( TARGET_DB_SENTINEL, dbName );
		blastCmd = blastCmd.replaceAll( QUERY_SENTINEL, querySeqName );
		blastCmd = blastCmd.replaceAll( OUTPUT_FILE, blastOutputFile );
		blastCmd = blastCmd.replaceAll( E_VALUE, eValueThreshold );
		String blastLocation = props.getProperty("blast.blastall.location");
		String sep = System.getProperty("file.separator");
		String cmd = blastLocation + sep + blastCmd; 
		System.out.println(cmd);
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		return p.getInputStream();
	}

	/**
	 * Used to create a blast database from a fasta file.  Runs the formatdb command specified
	 * in the properties file. Use the static public Strings defined in this class as 
	 * placeholders while defining the commands in the attributes file. Uses the properties 
	 * blast.formatdb.location and blast.formatdb.command. 
	 */
	private void createBlastDB(String dbName) 
		throws InterruptedException, IOException, SecurityException, NullPointerException, IllegalArgumentException {
		String formatdbCmd = props.getProperty("blast.formatdb.command");
		formatdbCmd = formatdbCmd.replaceAll( TARGET_DB_SENTINEL, dbName );
		String formatdbLocation = props.getProperty("blast.formatdb.location");
		String sep = System.getProperty("file.separator");
		String cmd = formatdbLocation + sep + formatdbCmd; 
		System.out.println(cmd);
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
	}

	/**
	 * A custom ContentHandler for extracting the expectation value and sequence ids
	 */
	private class HitHandler extends DefaultHandler {

		String queryId;
		String subjectId;
		double evalue;

		boolean getValue;
		StringBuffer value;


		// Event handlers.

		/**
		 * Basic SAX event handler.
		 */
		public void startDocument () { 
			value = new StringBuffer();
			getValue = false;
		}

		/**
		 * Basic SAX event handler.
		 */
		public void endDocument () { }

		/**
		 * Basic SAX event handler.
		 */
		public void startElement (String uri, String name,
					      String qName, Attributes atts) {
			if ( qName.equals("BlastOutput_query-def") )
				getValue = true;	
			else if ( qName.equals("Hit_def") )
				getValue = true;	
			else if ( qName.equals("Hsp_evalue") )
				getValue = true;	
		}

		/**
		 * Basic SAX event handler.
		 */
		public void endElement (String uri, String name, String qName) {

			if ( qName.equals("BlastOutput_query-def") ) {
				getValue = false;	
				if ( synonyms != null )
					queryId = synonyms.getSynonym(value.toString(),"name");
				else
					queryId = value.toString();
				if ( value.length() > 0 )
					value.delete(0,value.length());
			} else if ( qName.equals("Hit_def") ) {
				getValue = false;	
				if ( synonyms != null ) {
					String[] syns = value.toString().split("\\|");	
					subjectId = synonyms.getSynonym(syns[0],"name");
				} else 
					subjectId = value.toString();
				if ( value.length() > 0 )
					value.delete(0,value.length());
			} else if ( qName.equals("Hsp_evalue") ) {
				getValue = false;	
				evalue = Double.parseDouble(value.toString());
				add();
				if ( value.length() > 0 )
					value.delete(0,value.length());
			}
		}

		/**
		 * Basic SAX event handler.
		 */
		public void characters (char[] ch, int start, int length) {
			if ( getValue ) { 
				value.append(ch,start,length);
			}
		}

		private void add() {
			if ( !evalues.containsKey(queryId) ) {
				Map<String,Double> map = new HashMap<String,Double>();
				evalues.put(queryId,map);
			}
			Map<String,Double> evals = evalues.get(queryId);

			// because there can be multiple sub-hits for a sequence, just
			// pick the best one.
			Double currentEvalue = evals.get( subjectId );
			if ( currentEvalue == null || currentEvalue.doubleValue() > evalue ) {
				evals.put(subjectId,evalue);
			}
		}
	}
}
