package org.cytoscape.io.internal.read.datatable;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import java.io.File;
import org.cytoscape.model.CyNetwork;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.net.URLDecoder;
import java.io.FileInputStream;
import java.util.Iterator;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.io.read.CyTableReader;

public class CyAttributesReader implements CyTableReader {


	/**
	 * This type corresponds to java.lang.Boolean.
	 */
	public final byte TYPE_BOOLEAN = 1;

	/**
	 * This type corresponds to java.lang.Double.
	 */
	public final byte TYPE_FLOATING_POINT = 2;

	/**
	 * This type corresponds to java.lang.Integer.
	 */
	public final byte TYPE_INTEGER = 3;

	/**
	 * This type corresponds to java.lang.String.
	 */
	public final byte TYPE_STRING = 4;

	public static final String ENCODING_SCHEME = "UTF-8";


	////////////////////

	public static final String DECODE_PROPERTY = "cytoscape.decode.attributes";
	private static final String badDecodeMessage =
		"Trouble when decoding attribute value, first occurence line no. {0}" +
		"\nIgnore if attributes file was created before 2.6.3 or wasn't creatad by Cytoscape." +
		"\nUse -Dcytoscape.decode.attributes=false when starting Cytoscape to turn off decoding.";

	private boolean badDecode;
	private int lineNum;
	private boolean doDecoding;
	final Map<String, Map<String, Class>> idsToAttribNameToTypeMapMap;
	//private final CyLogger logger;

	private CyTableManager tableMgr;
	private CyTableFactory tableFactory;;
	private InputStream inputStream;

	private CyTable[] cyTables = null;
	private CyNetwork[] networks= null;
	private String nodeOrEdge; //node or edge
	private String globalTableTitle = "ThisIsAGlobalTable";// This will be replaced by the "file name" later
	private CyApplicationManager appMgr;
	private CyNetworkManager netMgr;
	private CyTable globalTable = null;
	
	@Tunable(description = "Select Data Type")
	public final ListSingleSelection<String> dataTypeOptions;
	
	@Tunable(description = "Select Network")
	public final ListSingleSelection<String> networkOptions;
	
	
	public CyAttributesReader(InputStream inputStream, CyTableFactory tableFactory, CyTableManager tableMgr,
			CyApplicationManager appMgr, CyNetworkManager netMgr) {
		
		//logger = CyLogger.getLogger(CyAttributesReader.class);
		lineNum = 0;
		doDecoding = Boolean.valueOf(System.getProperty(DECODE_PROPERTY, "true"));
		idsToAttribNameToTypeMapMap = new HashMap<String, Map<String, Class>>();
		
		this.tableMgr = tableMgr;
		this.tableFactory = tableFactory;
		this.appMgr = appMgr;
		this.netMgr = netMgr;
		
		this.inputStream = inputStream;

		ArrayList<String> options1 = new  ArrayList<String>();
		options1.add("Node");
		options1.add("Edge");
		
		dataTypeOptions = new ListSingleSelection<String>(options1);
		
		ArrayList<String> options2 = new  ArrayList<String>();
		options2.add("Current network");
		options2.add("All networks");
		options2.add("No network");
		
		networkOptions = new ListSingleSelection<String>(options2);	
	}

	
	@Override
	public void run(TaskMonitor tm) throws IOException {
		if (dataTypeOptions.getSelectedValue().equalsIgnoreCase("Node")){
			nodeOrEdge = "node";
		}
		else { // Edge
			nodeOrEdge = "edge";
		}
		
		////
		String networkType = networkOptions.getSelectedValue();
		
		if (networkType.equalsIgnoreCase("Current network")){
			networks = new CyNetwork[1];//
			networks[0] = this.appMgr.getCurrentNetwork();			
		}
		else if (networkType.equalsIgnoreCase("All networks")){
			networks = (CyNetwork[])netMgr.getNetworkSet().toArray();
		}
		else { // no network yet, its global attribute
			networks = null;
		}
		
		///
		try {
			loadAttributesInternal();
		} finally {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}

		tm.setProgress(1.0);
	}
	

	/*
	private Class mapCytoscapeAttribTypeToEqnType(final byte attribType) {
		switch (attribType) {
		case CyAttributes.TYPE_BOOLEAN:
			return Boolean.class;
		case CyAttributes.TYPE_INTEGER:
			return Long.class;
		case CyAttributes.TYPE_FLOATING:
			return Double.class;
		case CyAttributes.TYPE_STRING:
			return String.class;
		case CyAttributes.TYPE_SIMPLE_LIST:
			return List.class;
		default:
			return null;
		}
	}
	 */


	/**
	 *  DOCUMENT ME!
	 *
	 * @param cyAttrs DOCUMENT ME!
	 * @param fileIn DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	private void loadAttributesInternal() throws IOException
	{		
		InputStreamReader reader1 = new InputStreamReader(this.inputStream);
		
		badDecode = false;
		boolean guessedAttrType = false; // We later set this to true if we have to guess the attribute type.

		try {
			final BufferedReader reader;

			reader = new BufferedReader(reader1);

			String attributeName;
			byte type = -1;

			{
				final String firstLine = reader.readLine();
				lineNum++;

				if (firstLine == null) {
					return;
				}

				final String searchStr = "class=";
				final int inx = firstLine.indexOf(searchStr);

				if (inx < 0) {
					attributeName = firstLine.trim();
				} else {
					attributeName = firstLine.substring(0, inx - 1).trim();

					String foo = firstLine.substring(inx);
					final StringTokenizer tokens = new StringTokenizer(foo);
					foo = tokens.nextToken();

					String className = foo.substring(searchStr.length()).trim();

					if (className.endsWith(")")) {
						className = className.substring(0, className.length() - 1);
					}

					if (className.equalsIgnoreCase("java.lang.String")
							|| className.equalsIgnoreCase("String")) {
						type = TYPE_STRING;
					} else if (className.equalsIgnoreCase("java.lang.Boolean")
							|| className.equalsIgnoreCase("Boolean")) {
						type = TYPE_BOOLEAN;
					} else if (className.equalsIgnoreCase("java.lang.Integer")
							|| className.equalsIgnoreCase("Integer")) {
						type = TYPE_INTEGER;
					} else if (className.equalsIgnoreCase("java.lang.Double")
							|| className.equalsIgnoreCase("Double")
							|| className.equalsIgnoreCase("java.lang.Float")
							|| className.equalsIgnoreCase("Float")) {
						type = TYPE_FLOATING_POINT;
					}
				}
			}

			if (attributeName.indexOf("(") >= 0) {
				attributeName = attributeName.substring(0, attributeName.indexOf("(")).trim();
			}

			boolean firstLine = true;
			boolean list = false;

			while (true) {
				final String line = reader.readLine();
				lineNum++;

				if (line == null)
					break;

				// Empty line?
				if ("".equals(line.trim())) {
					continue;
				}

				int inx = line.indexOf('=');
				String key = line.substring(0, inx).trim();
				String val = line.substring(inx + 1).trim();
				final boolean equation = val.startsWith("=");

				key = decodeString(key);

				if (firstLine && val.startsWith("("))
					list = true;

				if (list) {

					// Chop away leading '(' and trailing ')'.
					val = val.substring(1).trim();
					val = val.substring(0, val.length() - 1).trim();

					String[] elms = val.split("::");
					final ArrayList elmsBuff = new ArrayList();

					for (String vs : elms) {
						vs = decodeString(vs);
						vs = decodeSlashEscapes(vs);
						elmsBuff.add(vs);
					}

					if (firstLine) {
						if (type < 0) {
							guessedAttrType = true;
							while (true) {
								try {
									new Integer((String) elmsBuff.get(0));
									type = TYPE_INTEGER;
									break;
								} catch (Exception e) {
								}

								try {
									new Double((String) elmsBuff.get(0));
									type = TYPE_FLOATING_POINT;
									break;
								} catch (Exception e) {
								}
								type = TYPE_STRING;
								break;
							}
						}

						firstLine = false;
					}

					for (int i = 0; i < elmsBuff.size(); i++) {
						if (type == TYPE_INTEGER) {
							elmsBuff.set(i, new Integer((String) elmsBuff.get(i)));
						} else if (type == TYPE_BOOLEAN) {
							elmsBuff.set(i, new Boolean((String) elmsBuff.get(i)));
						} else if (type == TYPE_FLOATING_POINT) {
							elmsBuff.set(i, new Double((String) elmsBuff.get(i)));
						} else {
							// A string; do nothing.
						}
					}

					//cyAttrs.setListAttribute(key, attributeName, elmsBuff);					
					setListArrtibute(type, key, attributeName, elmsBuff);
					
					
				} else { // Not a list.

					val = decodeString(val);
					val = decodeSlashEscapes(val);

					if (firstLine) {
						if (type < 0) {
							guessedAttrType = true;
							while (true) {
								try {
									new Integer(val);
									type = TYPE_INTEGER;
									break;
								} catch (Exception e) {
								}

								try {
									new Double(val);
									type = TYPE_FLOATING_POINT;
									break;
								} catch (Exception e) {
								}

								type = TYPE_STRING;
								break;
							}
						}

						firstLine = false;
					}


					if (networks != null){
						// Load attributes for current network or all networks there						
						setAttributeForType(type, key, attributeName, val);	
					}
					else { // networks = null, load the attribute into global table

						// If globalTable does not exist, create it
						if (globalTable == null){
							String primaryKey = "ID";
							globalTable = tableFactory.createTable(this.globalTableTitle, primaryKey, String.class, true);
						}
						
						// key, attributeName, val
						// If the column does not exist, create it
						if (globalTable.getColumnTypeMap().get(attributeName)== null)
						{
							if (type == TYPE_INTEGER){
								globalTable.createColumn(attributeName, Integer.class);	
							}
							else if (type == TYPE_BOOLEAN){
								globalTable.createColumn(attributeName, Boolean.class);	
							}
							else if (type == TYPE_FLOATING_POINT){
								globalTable.createColumn(attributeName, Double.class);	
							}
							else { // String
								globalTable.createColumn(attributeName, String.class);
							}
						}
						
						// Now add the attributes for the row based on the key
						CyRow row = globalTable.getRow(key);

						if (type == TYPE_INTEGER){
							row.set(attributeName, new Integer(val));	
						}
						else if (type == TYPE_BOOLEAN){
							row.set(attributeName, new Boolean(val));
						}
						else if (type == TYPE_FLOATING_POINT){
							row.set(attributeName, new Double(val));
						}
						else { // String
							row.set(attributeName, new String(val));
						}
					}
						
				}

			}// End of while loop
		} catch (Exception e) {
			String message;
			if (guessedAttrType) {
				message = "failed parsing attributes file at line: " + lineNum
				+ " with exception: " + e.getMessage()
				+ " This is most likely due to a missing attribute type on the first line.\n"
				+ "Attribute type should be one of the following: "
				+ "(class=String), (class=Boolean), (class=Integer), or (class=Double). "
				+ "(\"Double\" stands for a floating point a.k.a. \"decimal\" number.)"
				+ " This should be added to end of the first line.";
			}
			else
				message = "failed parsing attributes file at line: " + lineNum
				+ " with exception: " + e.getMessage();
			//logger.warn(message, e);
			throw new IOException(message);
		}
	}

	private void setAttributeForType(byte type, String key, String attributeName, String val){

		for (int i= 0; i< networks.length; i++){
			Map<String, CyTable> map;
			if (nodeOrEdge.equalsIgnoreCase("Node")){
				map = tableMgr.getTableMap(CyNode.class, networks[i]);
			}
			else {
				map = tableMgr.getTableMap(CyEdge.class, networks[i]);
			}
			
			CyTable tbl = map.get(CyNetwork.DEFAULT_ATTRS);
				
			if (!tbl.getColumnTypeMap().keySet().contains(attributeName))
			{
				if (type == TYPE_INTEGER){
					tbl.createColumn(attributeName, Integer.class);
				}
				else if (type == TYPE_BOOLEAN){
					tbl.createColumn(attributeName, Boolean.class);
				}
				else if (type == TYPE_FLOATING_POINT) {
					tbl.createColumn(attributeName, Double.class);
				}
				else { // type is String
					tbl.createColumn(attributeName, String.class);
				}
			}
				
			Set<CyRow> rows = tbl.getMatchingRows("name", key);

			Iterator<CyRow> it = rows.iterator();
			while (it.hasNext()){
				CyRow row = it.next();
				if (type == TYPE_INTEGER){
					row.set(attributeName, new Integer(val));							
				}
				else if (type == TYPE_BOOLEAN){
					row.set(attributeName, new Boolean(val));
				}
				else if (type == TYPE_FLOATING_POINT) {
					row.set(attributeName, (new Double(val)));
				}
				else {// type is String
					row.set(attributeName, new String(val));
				}	
			}
		}
	}


	private void setListArrtibute(Byte type, String key, String attributeName, final ArrayList elmsBuff){

		// Load global attribute
		if (networks == null){
			// If globalTable does not exist, create it
			if (globalTable == null){
				String primaryKey = "ID";
				globalTable = tableFactory.createTable(this.globalTableTitle, primaryKey, String.class, true);
			}
			
			// key, attributeName, val
			// If the column does not exist, create it
			if (globalTable.getColumnTypeMap().get(attributeName)== null)
			{
				if (type == TYPE_INTEGER){
					globalTable.createListColumn(attributeName, Integer.class);	
				}
				else if (type == TYPE_BOOLEAN){
					globalTable.createListColumn(attributeName, Boolean.class);	
				}
				else if (type == TYPE_FLOATING_POINT){
					globalTable.createListColumn(attributeName, Double.class);	
				}
				else { // String
					globalTable.createListColumn(attributeName, String.class);
				}
			}
			
			// Now add the attributes for the row based on the key
			CyRow row = globalTable.getRow(key);

			if (type == TYPE_INTEGER){
				row.set(attributeName, elmsBuff);	
			}
			else if (type == TYPE_BOOLEAN){
				row.set(attributeName, elmsBuff);
			}
			else if (type == TYPE_FLOATING_POINT){
				row.set(attributeName, elmsBuff);
			}
			else { // String
				row.set(attributeName, elmsBuff);
			}

			return;
		}
		
		// networks != null
		////////Load network-specific attributes ////////
		for (int i= 0; i< networks.length; i++){
			Map<String, CyTable> map;
			if (nodeOrEdge.equalsIgnoreCase("Node")){
				map = tableMgr.getTableMap(CyNode.class, networks[i]);
			}
			else {// Edge
				map = tableMgr.getTableMap(CyEdge.class, networks[i]);				
			}
			CyTable	tbl = map.get(CyNetwork.DEFAULT_ATTRS);
				
			if (!tbl.getColumnTypeMap().keySet().contains(attributeName))
			{
				if (type == TYPE_INTEGER){
					tbl.createListColumn(attributeName, Integer.class);
				}
				else if (type == TYPE_BOOLEAN){
					tbl.createListColumn(attributeName, Boolean.class);
				}
				else if (type == TYPE_FLOATING_POINT) {
					tbl.createListColumn(attributeName, Double.class);
				}
				else { // type is String, do nothing
				}
			}
					
			Set<CyRow> rows = tbl.getMatchingRows("name", key);

			Iterator<CyRow> it = rows.iterator();
			while (it.hasNext()){
				CyRow row = it.next();
				row.set(attributeName, elmsBuff);							
			}				
		}
	}
	
	
	private String decodeString(String in) throws IOException {
		if (doDecoding) {
			try {
				in = URLDecoder.decode(in, ENCODING_SCHEME);
			}
			catch (IllegalArgumentException iae) {
				if (!badDecode) {
					//logger.info(MessageFormat.format(badDecodeMessage, lineNum), iae);
					badDecode = true;
				}
			}
		}

		return in;
	}


	private static String decodeSlashEscapes(String in) {
		final StringBuilder elmBuff = new StringBuilder();
		int inx2;

		for (inx2 = 0; inx2 < in.length(); inx2++) {
			char ch = in.charAt(inx2);

			if (ch == '\\') {
				if ((inx2 + 1) < in.length()) {
					inx2++;

					char ch2 = in.charAt(inx2);

					if (ch2 == 'n') {
						elmBuff.append('\n');
					} else if (ch2 == 't') {
						elmBuff.append('\t');
					} else if (ch2 == 'b') {
						elmBuff.append('\b');
					} else if (ch2 == 'r') {
						elmBuff.append('\r');
					} else if (ch2 == 'f') {
						elmBuff.append('\f');
					} else {
						elmBuff.append(ch2);
					}
				} else {
				/* val ends in '\' - just ignore it. */ }
			} else {
				elmBuff.append(ch);
			}
		}

		return elmBuff.toString();
	}

	public boolean isDoDecoding() {
		return doDecoding;
	}

	public void setDoDecoding(boolean doDec) {
		doDecoding = doDec;
	}
	
	public CyTable[] getCyTables(){
		//Gloal table
		if (networks == null){
			cyTables = new CyTable[] { this.globalTable };
			return cyTables;
		}

		//network-specific tables
		cyTables = new CyTable[networks.length];
		if (this.nodeOrEdge.equalsIgnoreCase("Node")){
			for (int i=0; i< networks.length; i++){
				Map<String, CyTable> map = tableMgr.getTableMap(CyNode.class, networks[i]);
				CyTable tbl = map.get(CyNetwork.DEFAULT_ATTRS);
				cyTables[i] = tbl;
			}
		}
		else {// Edge
			for (int i=0; i< networks.length; i++){
				Map<String, CyTable> map = tableMgr.getTableMap(CyEdge.class, networks[i]);
				CyTable tbl = map.get(CyNetwork.DEFAULT_ATTRS);
				cyTables[i] = tbl;
			}			
		}
		
		return cyTables;
	}
	
	public void cancel(){
	}
}
