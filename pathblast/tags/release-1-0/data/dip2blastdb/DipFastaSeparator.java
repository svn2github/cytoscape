
//============================================================================
// 
//  file: DipFastaSeparator.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================


import java.io.FileReader;
import java.util.*;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

import org.biojava.bio.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;

public class DipFastaSeparator extends DefaultHandler
{
    public static String organismID = "organism";

    protected String nodeId;
    protected String organism;
    protected StringBuffer value;
    protected boolean getOrganism;
    protected boolean getValue;
    protected Map<String,String> nodeOrganismMap;
    protected Map<String,String> nodeSeqMap;
    //Map<String,Graph<String,Double>> organismGraphMap;
    protected String fastaFile;
    protected String outDir;

    public static void main (String args[])
	throws Exception
    {
	XMLReader xr = XMLReaderFactory.createXMLReader();
	DipFastaSeparator handler = new DipFastaSeparator(args[1],args[2]);
	handler.readFasta();

	xr.setContentHandler(handler);
	xr.setErrorHandler(handler);

	FileReader r = new FileReader(args[0]);
	xr.parse(new InputSource(r));

    }


    public DipFastaSeparator (String faf, String out)
    {
	super();
	fastaFile = faf;
	outDir = out;
	value = new StringBuffer();
	nodeOrganismMap = new HashMap<String,String>();
	nodeSeqMap = new HashMap<String,String>();
    }


    // Event handlers.
    public void startDocument () { 
    	System.out.println("parsing DIP XML document");
    	init();	
    }

    public void endDocument () { 
    	System.out.println("creating organism specific fasta files");
    	try { 
    	Set<String> species = new HashSet<String>(nodeOrganismMap.values());
	for (String s : species) {
		StringBuffer fasta = new StringBuffer();
		int numSeqs = 0;
		for (String node: nodeSeqMap.keySet()) {
			if ( nodeOrganismMap.containsKey(node) ) {
			if ( nodeOrganismMap.get(node).equals(s) ) {
				fasta.append(">");
				fasta.append(node);
				fasta.append("\n");
				fasta.append(nodeSeqMap.get(node));
				fasta.append("\n");
				numSeqs++;
			}
			}
		}
		if ( numSeqs > 0 ) {
			s = s.replace(' ','_') + ".fa";
			System.out.println("creating " + s + " (" + numSeqs + " seqs)");
			s = outDir + "/" + s;
			FileOutputStream fo = new FileOutputStream(s);
			fo.write(fasta.toString().getBytes());
			fo.close();
		}
		fasta = null;
	}
	} catch ( Exception e) { e.printStackTrace(); }
    }

    public void startElement (String uri, String name,
			      String qName, Attributes atts) {
    	if ( qName.equals("node") )
		getId(atts);
	else if ( qName.equals("att") )
		checkOrganism(atts);
	else if ( qName.equals("val") )
		checkValue();	
	else if ( qName.equals("edge") )
		makeEdge(atts);
    }

    public void endElement (String uri, String name, String qName) {

	if ( qName.equals("node") )
		makeNode();
	else if ( qName.equals("val") )
		finishOrganism();	
    }

    public void characters (char[] ch, int start, int length) {
    	if ( getValue ) { 
		value.append(ch,start,length);
	}
    }


    // Specific handlers.
    private void getId(Attributes atts) {
	nodeId = atts.getValue(atts.getIndex("uid"));	
    }

    private void checkValue() {
    	if ( getOrganism ) 
		getValue = true;
    }

    private void checkOrganism(Attributes atts) {
	if ( organismID.equals(atts.getValue(atts.getIndex("name"))) )
		getOrganism = true;
    }

    private void makeEdge(Attributes atts) {
	String from = atts.getValue(atts.getIndex("from"));	
	String to = atts.getValue(atts.getIndex("to"));	

/*
	String o1 = nodeOrganismMap.get(from);
	String o2 = nodeOrganismMap.get(to);
	if (o1.equals(o2))
		System.out.println(o1 + " edge from: " + from + "  to: " + to);
*/
    }

    private void makeNode() {
	//System.out.println("node: " + nodeId + "  for " + organism);
	nodeOrganismMap.put(nodeId,organism);
	init();
    }

    private void finishOrganism() {
    	organism = value.toString();
	getValue = false;
	getOrganism = false;
    }

    private void init() {
	organism = "";
	nodeId = "";
	if ( value.length() > 0 )
		value.delete(0,value.length()); 
	getOrganism = false;
	getValue = false;
    }

    public void readFasta() {
	System.out.println("parsing combined fasta file");    	
	try {
	BufferedReader br = new BufferedReader(new FileReader(fastaFile));
	SequenceIterator iter = (SequenceIterator)SeqIOTools.fileToBiojava("fasta","protein", br);

	while (iter.hasNext()) {
		Sequence s = iter.nextSequence();
		String seq = s.seqString();
		String id = s.getName();
		String[] ids = id.split("\\|");
		nodeSeqMap.put(ids[0],seq);

	}
	} catch (Exception e) { e.printStackTrace(); }

    }	
    

}


