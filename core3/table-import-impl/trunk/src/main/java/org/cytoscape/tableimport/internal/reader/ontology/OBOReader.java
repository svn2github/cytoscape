package org.cytoscape.tableimport.internal.reader.ontology;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.tableimport.internal.reader.AbstractGraphReader;
import org.cytoscape.tableimport.internal.reader.NetworkTableReader;
import org.cytoscape.tableimport.internal.reader.TextTableReader;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cytoscape.tableimport.internal.reader.ontology.OBOTags.*;


public class OBOReader extends AbstractTask implements CyNetworkReader {

	private static final Logger logger = LoggerFactory.getLogger(OBOReader.class);

	private static final String[] COMPATIBLE_VERSIONS = { "1.2" };
	//
	private static final String ONTOLOGY_DAG_ROOT = "Ontology DAGs";

	public static final String OBO_PREFIX = "ontology";
	private static final String DEF_ORIGIN = "def_origin";
	protected static final String TERM_TAG = "[Term]";
	private static final String DEF_ONTOLOGY_NAME = "Ontology DAG";
	private List<String[]> interactionList;
	
	private final CyNetworkViewFactory cyNetworkViewFactory;
	private final CyNetworkFactory cyNetworkFactory;
	private final CyEventHelper eventHelper;

	// DAG
	private CyNetwork ontologyDAG;
	private CyNetwork[] networks;

	private CyTable termAttributes;
	private CyTable networkAttributes;
	
	private Map<String, String> header;
	private String name;
	
	private final InputStream inputStream;
	
	
	private final Map<String, CyNode> termID2nodeMap;

	public OBOReader(final InputStream oboStream, final String name, final CyNetworkViewFactory cyNetworkViewFactory,
			final CyNetworkFactory cyNetworkFactory, final CyEventHelper eventHelper) {
		this.inputStream = oboStream;
		this.name = name;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.eventHelper = eventHelper;
		
		termID2nodeMap = new HashMap<String, CyNode>();
		networks = new CyNetwork[1];
		interactionList = new ArrayList<String[]>();
		// initialize();
	}

//	private void initialize() {
//		interactionList = new ArrayList<String[]>();
//		header = new HashMap<String, String>();
//
//		networkAttributes = Cytoscape.getNetworkAttributes();
//		termAttributes = Cytoscape.getNodeAttributes();
//
//		if (name == null) {
//			name = DEF_ONTOLOGY_NAME;
//		}
//
//		/*
//		 * Ontology DAGs will be distinguished by this attribute.
//		 */
//		networkAttributes.setAttribute(name, Ontology.IS_ONTOLOGY, true);
//		networkAttributes.setUserVisible(Ontology.IS_ONTOLOGY, false);
//		networkAttributes.setUserEditable(Ontology.IS_ONTOLOGY, false);
//
//		String rootID = Cytoscape.getOntologyRootID();
//
//		if (rootID == null) {
//			Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
//
//			for (CyNetwork net : networkSet) {
//				if (net.getTitle().equals(ONTOLOGY_DAG_ROOT)) {
//					rootID = net.getIdentifier();
//				}
//			}
//
//			if (rootID == null) {
//				rootID = Cytoscape.createNetwork(ONTOLOGY_DAG_ROOT, false).getIdentifier();
//				Cytoscape.setOntologyRootID(rootID);
//			}
//		}
//
//		ontologyDAG = Cytoscape.createNetwork(name, Cytoscape.getNetwork(rootID), false);
//	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
	
		BufferedReader bufRd = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		
		this.ontologyDAG = cyNetworkFactory.getInstance();
		
		// Phase 1: read header information
		header = new HashMap<String, String>();
		while ((line = bufRd.readLine()) != null) {
			if (line.startsWith(TERM_TAG))
				break;
			else if(line.startsWith("!"))
				continue;
			else if (line.trim().length() != 0)
				parseHeader(line);
		}
		mapHeader();
		
		
		// Phase 2: read actual contents
		readEntry(bufRd);
		
		buildEdge();
		
//		while ((line = bufRd.readLine()) != null) {
//						// Read header
//						if (line.startsWith(TERM_TAG)) {
//							readEntry(bufRd);
//						}
//					}
//
//				} finally {
//					if (bufRd != null) {
//						bufRd.close();
//					}
//				}
//			} finally {
//				if (inputStream != null) {
//					inputStream.close();
//				}
//			}
//
//			buildDag();
//			setAttributeDescriptions();
		
		
		bufRd.close();
		inputStream.close();

		logger.debug("node Cont ===> " + this.termID2nodeMap.size());
		networks[0] = this.ontologyDAG;
	}
	
	private void parseHeader(final String line) {
		final int colonInx = line.indexOf(':');

		if (colonInx == -1)
			return;
		
		final String key = line.substring(0, colonInx).trim();
		final String val = line.substring(colonInx + 1).trim();
		header.put(key, val);
	}
	
	private void mapHeader() {
		final CyTable networkTable = this.ontologyDAG.getDefaultNetworkTable();
		
		for(String tag: header.keySet()) {
			if(networkTable.getColumn(tag) == null)
				networkTable.createColumn(tag, String.class, false);
			
			networkTable.getRow(ontologyDAG.getSUID()).set(tag, header.get(tag));
		}
	}


	private void readEntry(final BufferedReader rd) throws IOException {
		String id = "";
		String line = null;

		String key;
		String val;

		int colonInx;

		String[] definitionParts;
		String[] synonymParts;
		String[] entry;
		String targetId;
		List<String> listAttr;
		Map<String, String> synoMap;

		
		CyNode termNode = null;
		boolean termSection = true;
		while ((line = rd.readLine()) != null) {
			line = line.trim();

			if (line.length() == 0 || line.startsWith("!"))
				continue;

			//Tag?
			if(line.startsWith("[")) {
				logger.debug("tag found: " + line);
				if(line.startsWith(TERM_TAG) == false) {
					termSection = false;
					continue;
				} else {
					termSection = true;
				}
			}
			
			if(!termSection)
				continue;
			
			
			colonInx = line.indexOf(':');
			if (colonInx == -1) 
				continue;

			key = line.substring(0, colonInx).trim();
			val = line.substring(colonInx + 1).trim();
			logger.debug(key + " = " + val);

			if (key.equals(ID.toString())) {
				// Create node for this term.
				termNode = termID2nodeMap.get(val);
				if(termNode ==  null) {
					termNode = this.ontologyDAG.addNode();
					termNode.getCyRow().set(CyTableEntry.NAME, val);
					termID2nodeMap.put(val, termNode);
					id = val;
				}
				
//			} else if (key.equals(DEF.toString())) {
//				definitionParts = val.split("\"");
//				termAttributes.setAttribute(id, OBO_PREFIX + "." + key, definitionParts[1]);
//
//				List<String> originList = getReferences(val.substring(definitionParts[1].length() + 2));
//
//				if (originList != null)
//					termAttributes.setListAttribute(id, OBO_PREFIX + "." + DEF_ORIGIN, originList);
//			} else if (key.equals(EXACT_SYNONYM.toString()) || key.equals(RELATED_SYNONYM.toString())
//					|| key.equals(BROAD_SYNONYM.toString()) || key.equals(NARROW_SYNONYM.toString())
//					|| key.equals(SYNONYM.toString())) {
//				synonymParts = val.split("\"");
//
//				synoMap = termAttributes.getMapAttribute(id, OBO_PREFIX + "." + OBOTags.SYNONYM.toString());
//
//				if (synoMap == null)
//					synoMap = new HashMap<String, String>();
//
//				if (key.equals(SYNONYM.toString())) {
//					synoMap.put(synonymParts[1], synonymParts[2].trim());
//				} else
//					synoMap.put(synonymParts[1], key);
//
//				termAttributes.setMapAttribute(id, OBO_PREFIX + "." + SYNONYM.toString(), synoMap);
			} else if (key.equals(RELATIONSHIP.toString())) {
				entry = val.split(" ");
				final String[] itr = new String[3];
				itr[0] = id;
				itr[1] = entry[1];
				itr[2] = entry[0];
				interactionList.add(itr);
				
//				CyNode targetNode = termID2nodeMap.get(entry[0]);
//				if(targetNode == null) {
//					targetNode = this.ontologyDAG.addNode();
//					termID2nodeMap.put(val, targetNode);
//				}
//				ontologyDAG.addEdge(termNode, targetNode, true);
			} else if (key.equals(IS_A.toString())) {

				int colonidx = val.indexOf('!');

				if (colonidx == -1)
					targetId = val.trim();
				else
					targetId = val.substring(0, colonidx).trim();

				
				final String[] itr = new String[3];
				itr[0] = id;
				itr[1] = targetId;
				itr[2] = "is_a";
				interactionList.add(itr);
				
//				CyNode targetNode = termID2nodeMap.get(targetId);
//				if(targetNode == null) {
//					targetNode = ontologyDAG.addNode();
//					termID2nodeMap.put(val, targetNode);
//				}
//				ontologyDAG.addEdge(termNode, targetNode, true);
//				target = Cytoscape.getCyNode(targetId, true);
//				ontologyDAG.addNode(target);
//
//				final String[] itr = new String[3];
//				itr[0] = id;
//				itr[1] = targetId;
//				itr[2] = "is_a";
//				interactionList.add(itr);
//			} else if (key.equals(IS_OBSOLETE.toString())) {
//				termAttributes.setAttribute(id, OBO_PREFIX + "." + key, Boolean.parseBoolean(val));
//			} else if (key.equals(XREF.toString()) || key.equals(XREF_ANALOG.toString())
//					|| key.equals(ALT_ID.toString()) || key.equals(SUBSET.toString())
//					|| key.equals(DISJOINT_FROM.toString())) {
//				listAttr = termAttributes.getListAttribute(id, OBO_PREFIX + "." + key);
//
//				if (listAttr == null)
//					listAttr = new ArrayList<String>();
//
//				if (val != null) {
//					if (key.equals(DISJOINT_FROM.toString())) {
//						listAttr.add(val.split("!")[0].trim());
//					} else
//						listAttr.add(val);
//				}
//
//				termAttributes.setListAttribute(id, OBO_PREFIX + "." + key, listAttr);
//			} else
//				termAttributes.setAttribute(id, OBO_PREFIX + "." + key, val);
			}
		}
	}
	
	private void buildEdge() {
		for(String[] entry: this.interactionList) {
			CyEdge edge = ontologyDAG.addEdge(termID2nodeMap.get(entry[0]), termID2nodeMap.get(entry[1]) , true);
			// add attr
		}
		
	}

	
	
//	private List<String> getReferences(String list) {
//		String trimed = list.trim();
//		trimed = trimed.substring(trimed.indexOf("[") + 1, trimed.indexOf("]"));
//
//		if (trimed.length() == 0) {
//			return null;
//		} else {
//			List<String> entries = new ArrayList<String>();
//
//			for (String entry : trimed.split(",")) {
//				entries.add(entry.trim());
//			}
//
//			return entries;
//		}
//	}
//
//	private void setAttributeDescriptions() {
//		String[] attrNames = termAttributes.getAttributeNames();
//		Set<String> attrNameSet = new TreeSet<String>();
//
//		for (String name : attrNames) {
//			attrNameSet.add(name);
//		}
//
//		for (OBOTags tags : OBOTags.values()) {
//			if (attrNameSet.contains(OBOTags.getPrefix() + "." + tags.toString())) {
//				termAttributes.setAttributeDescription(OBOTags.getPrefix() + "." + tags.toString(),
//						tags.getDescription());
//			}
//		}
//	}

	@Override
	public CyNetworkView buildCyNetworkView(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CyNetwork[] getCyNetworks() {
		return networks;
	}
}
