
//============================================================================
// 
//  file: DIPSynonyms.java
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



package nct.service.synonyms;

import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implements a SynonymMapper based on a DIP XIN file. This class
 * is an XML ContentHandler which needs to be added to an XMLReader
 * so that the events will be generated. 
 */
public class DIPSynonyms extends DefaultHandler implements SynonymMapper
{
	/** 
	 * Maps the dip file id to a synonym. The first
	 * String is the id, the second the type, the
	 * third the synonym.
	 */
	protected Map<String,Map<String,String>> synMap;

	/**
	 * Maps a synonym to a dip file id.
	 * The first String is the synonym, the second is the id;
	 */
	protected Map<String,String> idMap;

	private boolean getCrossRef;
	private String currentId;
	private boolean getShnCrossRef;
	private boolean getDesc;
	private boolean getOrganism;
	private StringBuffer value;
	private boolean getValue;

	/**
	 * Initializes the content handler.
	 */
	public DIPSynonyms() {
		synMap = new HashMap<String,Map<String,String>>();
		idMap = new HashMap<String,String>();
		value = new StringBuffer();
	}

        /**
         * Returns the unique id for the specified synonym.
         * @param synonym The synonym to check the database for.
         * @return The unique id for the input synonym, or null
	 * if the synonym can't be found in the database.
         */
	public String getIdFromSynonym( String synonym ) {
		return idMap.get(synonym);
	}

        /**
         * Returns a synonym of a particular type for the specified id.
         * @param id The id to check the database for.
         * @param type The type of synonym desired for the specified id.
         * @return The requested synonym or null if the id can't be
	 * found in the database.
         */
	public String getSynonymFromId( String id, String type ) {
		Map<String,String> map = synMap.get(id);

		String syn = null;
		if ( map != null ) 
			syn = map.get(type);
		
		return syn;
	}


        /**
         * A shortcut method for calling:
         * <code>
         * String s = syns.getSynonymFromId(syns.getIdFromSynonym(synonym), type);
         * </code>
         * @param synonym The synonym to check the database for.
         * @param type The type of synonym desired for the specified input synonym.
         * @return The requested synonym or null if the synonym can't be found
	 * in the database.
         */
	public String getSynonym( String synonym, String type ) {
		String id = getIdFromSynonym(synonym);
		if ( id == null )
			return null;
		else
			return getSynonymFromId(id, type);
	}

	/**
	 * Returns a list of matches to the input string which is treated as a
	 * regular expression pattern.
	 * @param regex The regular expression that will be evaluated against each key in
	 * the database.
	 * @return The list of matches found.
	 */
	public List<String> getPotentialSynonyms(String regex) {
		Matcher m = Pattern.compile(".*" + regex.toUpperCase() + ".*", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE ).matcher("");
		Set<String> matchList = new HashSet<String>();
		for ( String syn : idMap.keySet() ) {
			//System.out.println("checking regex: " + regex + " against " + syn);
			m.reset(syn.toUpperCase());
			if ( m.matches() ) 
				matchList.add( getSynonym(syn,"name") );
		}
		return new ArrayList<String>(matchList);
	}

	/**
	 * Adds an id to the synonym database for a specific type.
	 * @param id The id to use for the synonym.
	 * @param type The type of the synonym.
	 * @param synonym The synonym to be stored.
	 */
	protected void addId( String id, String type, String synonym) {
		idMap.put( synonym, id );

		if ( ! synMap.containsKey(id) ) 
			synMap.put( id, new HashMap<String,String>() );

		synMap.get(id).put(type,synonym);
	}

	/**
	 * Adds a value to the synonym database for a specific type. Adding
	 * a value will be accessible to any synonym of the specified id, but
	 * the value itself will not be a synonym.
	 * @param id The id to use for the value.
	 * @param type The type of the value.
	 * @param value The value to be stored.
	 */
	protected void addValue( String id, String type, String value) {
		if ( ! synMap.containsKey(id) ) 
			synMap.put( id, new HashMap<String,String>() );

		synMap.get(id).put(type,value);
	}



	// Event handlers.

	/**
	 * Standard SAX event handler.
	 */
	public void startDocument () { 
		init();		
	}

	/**
	 * Standard SAX event handler.
	 */
	public void endDocument () { }

	/**
	 * Standard SAX event handler.
	 */
	public void startElement (String uri, String name, String qName, Attributes atts) {
		if ( qName.equals("node") )
			getNodeIds(atts);
		else if ( qName.equals("feature") )
			initCrossRefs(atts);
		else if ( qName.equals("xref") )
			getCrossRefs(atts);
		else if ( qName.equals("att") )
			handleAtt(atts);
		else if ( qName.equals("val") )
			handleVal();
	}

	/**
	 * Standard SAX event handler.
	 */
	public void endElement (String uri, String name, String qName) {

		if ( qName.equals("node") )
			init();
		if ( qName.equals("feature") )
			getCrossRef = false;	
		if ( qName.equals("att") )
			finishAtt();
		if ( qName.equals("val") )
			finishVal();
	}

	/**
	 * Standard SAX event handler.
	 */
	public void characters (char[] ch, int start, int length) { 
		if ( getValue ) {
			value.append(ch,start,length);
		}
	}

	// specific handlers
	private void getNodeIds(Attributes atts) {
		currentId = atts.getValue(atts.getIndex("id")); 
		String uid = atts.getValue(atts.getIndex("uid")); 
		String name = atts.getValue(atts.getIndex("name")); 

		addId( currentId, "id", currentId);
		addId( currentId, "uid", uid); 
		addId( currentId, "name", name);
	}

	private void initCrossRefs(Attributes atts) {
		String cls = atts.getValue(atts.getIndex("class")); 
		if ( cls.equals("cref") )
			getCrossRef = true;
		else
			getCrossRef = false;
	}

	private void getCrossRefs(Attributes atts) {
		if ( getCrossRef ) {
			String db = atts.getValue(atts.getIndex("db")); 
			String id = atts.getValue(atts.getIndex("id")); 

			addId(currentId,db,id);
		}
	}

	private void handleAtt(Attributes atts) {
		String name = atts.getValue(atts.getIndex("name")); 
		if ( name.equals("shn") )
			getShnCrossRef = true;
		else if ( name.equals("descr") )
			getDesc = true;
		else if ( name.equals("organism") )
			getOrganism = true;
	}

	private void finishAtt() {
		if ( getShnCrossRef ) {
			addId(currentId,"shn",value.toString());
			value.delete(0,value.length());
			getShnCrossRef = false;
			getValue = false;
		}

		if ( getDesc ) {
			// Because of duplicate descriptions, add current id to desc,
			// however we don't want to show the id with the desc, so add
			// a unique_description as well.
			addValue(currentId,"description",value.toString());
			addId(currentId,"unique_description",value.toString() + " " + currentId);
			value.delete(0,value.length());
			getDesc = false;
			getValue = false;
		}

		if ( getOrganism ) {
			addValue(currentId,"species",value.toString());
			addValue(currentId,"organism",value.toString());
			value.delete(0,value.length());
			getOrganism = false;
			getValue = false;
		}
	}

	private void handleVal() {
		if ( getDesc || getShnCrossRef || getOrganism )
			getValue = true;
		else 
			getValue = false;
	}
	private void finishVal() {
		getValue = false;
	}

	private void init() {
		getCrossRef = false;
		getShnCrossRef = false;
		getDesc = false;
		getOrganism = false;
		getValue = false;
		currentId = null;
		if ( value.length() > 0 )
			value.delete(0,value.length());
	}
}



