package cytoscape.data.readers;

import java.io.*; 
import java.util.Vector;
import java.util.StringTokenizer;

import cytoscape.data.KeggPathways;

/**
 * Reads a file that map KEGG pathways to nodes
 * <p>
 * Each line maps one pathway to N nodes:
 * Pathway description\t PathwayID Node1ID Node2ID NodeNID
 * <p>
 * Notes: For convenience, this file will be stored with 
 *        the GO files and have a similar status,
 *        so the end user doesn't have to worry about it separately.
 * <p>
 * Issues: The file must end with a new line
 *
 * @author namin@mit.edu
 * @version 2002-04-24
 */
public class KeggPathwaysReader {
    String filename;

    public KeggPathwaysReader(String filename) {
	this.filename = filename;
    }

    public KeggPathwaysReader(File file) {
	this.filename = file.toString();
    }

    public KeggPathways read() {
	TextFileReader reader = new TextFileReader (filename);
	KeggPathways pathways = new KeggPathways();
	
	reader.read();
	StringTokenizer fileTok = new StringTokenizer(reader.getText(), "\n");
	while (fileTok.hasMoreTokens()) {
	    String line = fileTok.nextToken();

	    StringTokenizer lineTok = new StringTokenizer(line, "\t");
	    String desc = lineTok.nextToken();
	    // everything (ID and node names) apart from description
	    String chunk = lineTok.nextToken();

	    StringTokenizer chunkTok = new StringTokenizer(chunk, " ");
	    String id = chunkTok.nextToken();
	    Vector nodes = new Vector();
	    while (chunkTok.hasMoreTokens()) {
		String nodeName = chunkTok.nextToken();
		nodes.addElement(nodeName);
	    }
	    pathways.add(id, desc, nodes);
	}
	return pathways;
    }
}
