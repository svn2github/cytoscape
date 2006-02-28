
//============================================================================
// 
//  file: DIPInteractionNetwork.java
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



package nct.service.interactions;

import java.io.FileReader;
import java.util.*;

import nct.graph.Graph;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implements the InteractionNetwork interface by reading a DIP XIN (xml) file.
 * Read about DIP here: http://dip.doe-mbi.ucla.edu/
 */
public class DIPInteractionNetwork extends DefaultHandler 
	implements InteractionNetwork<String,Double>
{
    public static String organismID = "organism";

    protected String nodeId;
    protected String organism;
    protected StringBuffer value;
    protected boolean getOrganism;
    protected boolean getValue;
    protected Map<String,String> nodeOrganismMap;
    protected Map<String,String> idUidMap; 
    protected Map<String,String> idNameMap; 
    protected Graph<String,Double> graph;
    protected String targetOrganism;

    /**
     * A SAX event handler that extracts graph information (nodes and edges) for a specific 
     * organism from a specified DIP XIN database file http://dip.doe-mbi.ucla.edu.
     * 
     * @param targetOrganism The target organism the graph is being constructed for. 
     * The name must match the organism names used in the DIP file.
     */
    public DIPInteractionNetwork( String targetOrganism )
    {
	super();
	this.targetOrganism = targetOrganism;
	value = new StringBuffer();
	nodeOrganismMap = new HashMap<String,String>();
	idUidMap = new HashMap<String,String>();
	idNameMap = new HashMap<String,String>();
    }


    // Event handlers.

    /**
     * Basic SAX event handler.
     */
    public void startDocument () { 
    	init();	
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
    	if ( qName.equals("node") )
		getId(atts);
	else if ( qName.equals("att") )
		checkOrganism(atts);
	else if ( qName.equals("val") )
		checkValue();	
	else if ( qName.equals("edge") )
		makeEdge(atts);
    }

    /**
     * Basic SAX event handler.
     */
    public void endElement (String uri, String name, String qName) {

	if ( qName.equals("node") )
		makeNode();
	else if ( qName.equals("val") )
		finishOrganism();	
    }

    /**
     * Basic SAX event handler.
     */
    public void characters (char[] ch, int start, int length) {
    	if ( getValue ) { 
		value.append(ch,start,length);
	}
    }


    // Specific handlers.
    private void getId(Attributes atts) {
	nodeId = atts.getValue(atts.getIndex("id"));	
	String nodeUid = atts.getValue(atts.getIndex("uid"));	
	String nodeName = atts.getValue(atts.getIndex("name"));	
	idUidMap.put(nodeId,nodeUid);
	idNameMap.put(nodeId,nodeName);
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
	// use uids to identify things because uids are used to identify fasta seqs
//	String from = idUidMap.get(atts.getValue(atts.getIndex("from")));	
//	String to = idUidMap.get(atts.getValue(atts.getIndex("to")));	
	String fromName = idNameMap.get(atts.getValue(atts.getIndex("from")));	
	String toName = idNameMap.get(atts.getValue(atts.getIndex("to")));	
	
	String o1 = nodeOrganismMap.get(fromName);
	String o2 = nodeOrganismMap.get(toName);
	if (o1.equals(o2) && targetOrganism.equals(o1) )
		graph.addEdge(fromName,toName,1.0);
		//graph.addEdge(from,to,1.0);
		//System.out.println(o1 + " edge from: " + from + "  to: " + to);
    }

    private void makeNode() {
	// use uids to identify things because uids are used to identify fasta seqs
	String nodeName = idNameMap.get(nodeId);
	nodeOrganismMap.put(nodeName,organism);
	if ( targetOrganism.equals( organism ) )
		graph.addNode(nodeName);

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

    /**
     * Updates the specified graph with the relevant nodes and edges contained in the
     * XIN file and for the organism specified in the constructor.
     * @param graph The graph to be updated by adding nodes and edges.
     */
    public void updateGraph(Graph<String,Double> graph) {
    	this.graph = graph;
    }
}


