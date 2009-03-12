package org.cytoscape.webservice.psicquic.ontology;

import static cytoscape.data.ontology.readers.OBOTags.ALT_ID;
import static cytoscape.data.ontology.readers.OBOTags.BROAD_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.DEF;
import static cytoscape.data.ontology.readers.OBOTags.DISJOINT_FROM;
import static cytoscape.data.ontology.readers.OBOTags.EXACT_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.ID;
import static cytoscape.data.ontology.readers.OBOTags.IS_A;
import static cytoscape.data.ontology.readers.OBOTags.IS_OBSOLETE;
import static cytoscape.data.ontology.readers.OBOTags.NARROW_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.RELATED_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.RELATIONSHIP;
import static cytoscape.data.ontology.readers.OBOTags.SUBSET;
import static cytoscape.data.ontology.readers.OBOTags.SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.XREF;
import static cytoscape.data.ontology.readers.OBOTags.XREF_ANALOG;
import giny.model.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.ontology.readers.OBOTags;
import cytoscape.util.URLUtil;

public class OBOMapReader {
	
	private static final String OBO_URL = "http://psidev.sourceforge.net/mi/psi-mi.obo";
	
	private static final String TERM_TAG = "[Term]";
	
	public OBOMapReader(Map<String, String> id2name, Map<String, String> name2id ) throws IOException {
		final InputStream is = URLUtil.getBasicInputStream(new URL(OBO_URL));
	}
	
	private void buildOntologyMap(InputStream is, Map<String, String> id2name, Map<String, String> name2id) throws IOException {
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(is));
		String line;

		String key;
		String val;
		int colonInx;


		while ((line = bufRd.readLine()) != null) {
			// Read header
			if (line.startsWith(TERM_TAG)) {
				//readEntry(bufRd);
			}
		}


		if (is != null) {
			is.close();
		}
		
		is = null;

	}
	
//	private readEntry() {
//		while (true) {
//			line = rd.readLine().trim();
//
//			if (line.length() == 0)
//				break;
//
//			colonInx = line.indexOf(':');
//			key = line.substring(0, colonInx).trim();
//			val = line.substring(colonInx + 1).trim();
//
//			Node source = null;
//
//			if (key.equals(ID.toString())) {
//				// There's only one id.
//				id = val;
//			} else if (key.equals(DEF.toString())) {
//				// CyLogger.getLogger().info("DEF: " + id + " = " + val);
//				definitionParts = val.split("\"");
//				termAttributes.setAttribute(id, OBO_PREFIX + "." + key, definitionParts[1]);
//
//				List<String> originList = getReferences(val.substring(definitionParts[1].length()
//				                                                      + 2));
//
//				if (originList != null)
//					termAttributes.setListAttribute(id, OBO_PREFIX + "." + DEF_ORIGIN, originList);
//			} else if (key.equals(EXACT_SYNONYM.toString())
//			           || key.equals(RELATED_SYNONYM.toString())
//			           || key.equals(BROAD_SYNONYM.toString())
//			           || key.equals(NARROW_SYNONYM.toString()) || key.equals(SYNONYM.toString())) {
//				synonymParts = val.split("\"");
//
//				synoMap = termAttributes.getMapAttribute(id,
//				                                         OBO_PREFIX + "."
//				                                         + OBOTags.SYNONYM.toString());
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
//			} else if (key.equals(RELATIONSHIP.toString())) {
//				if (source == null) {
//					source = Cytoscape.getCyNode(id, true);
//					ontologyDAG.addNode(source);
//				}
//
//				entry = val.split(" ");
//
//				final String[] itr = new String[3];
//				itr[0] = id;
//				itr[1] = entry[1];
//				itr[2] = entry[0];
//				interactionList.add(itr);
//			} else if (key.equals(IS_A.toString())) {
//				/*
//				 * This is the keyword to create an edge. IS_A relationship
//				 * means current node is the source, and target is the one
//				 * written here.
//				 */
//				final Node target;
//
//				if (source == null) {
//					source = Cytoscape.getCyNode(id, true);
//					ontologyDAG.addNode(source);
//				}
//
//				int colonidx = val.indexOf('!');
//
//				if (colonidx == -1)
//					targetId = val.trim();
//				else
//					targetId = val.substring(0, colonidx).trim();
//
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
//			           || key.equals(ALT_ID.toString()) || key.equals(SUBSET.toString())
//			           || key.equals(DISJOINT_FROM.toString())) {
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
//		}
//	}

}
