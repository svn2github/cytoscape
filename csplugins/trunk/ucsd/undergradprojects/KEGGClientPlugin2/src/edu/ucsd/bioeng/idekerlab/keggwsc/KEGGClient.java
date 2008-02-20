/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package edu.ucsd.bioeng.idekerlab.keggwsc;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.util.ModuleProperties;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;

import cytoscape.view.CyNetworkView;

import cytoscape.visual.VisualStyle;

import giny.model.Edge;
import giny.model.Node;

import keggapi.Definition;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;
import keggapi.PathwayElement;
import keggapi.PathwayElementRelation;
import keggapi.Subtype;

import java.beans.PropertyChangeEvent;

import java.rmi.RemoteException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;




/**
 *
 */
public class KEGGClient extends WebServiceClientImpl {
	private static final String DISPLAY_NAME = "KEGG Web Service Client";
	private static final String CLIENT_ID = "kegg";
	private static final WebServiceClient client;
	private static final String DEF_VS_NAME = "KEGG Style";
	private VisualStyle defaultVS = null;
	static {
		client = new KEGGClient();
	}
	

	private static final String END_POINT = "";
	private Map<String, Map<String, String>> pathwayNameMap;

	/*
	 * Pre-defined attribute names
	 */
	private enum AttrNames {
		OBJECT_TYPE,
		KEGG_NAME,
		RELATION;
	}
	public static WebServiceClient getClient() {
		return client;
	}


	private KEGGClient() {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[]{ClientType.NETWORK});

		KEGGLocator locator = new KEGGLocator();

		try {
			stub = locator.getKEGGPort();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Setup props
		
		ModuleProperties props = new ModulePropertiesImpl(displayName, "webservice");
		this.props = props;

		pathwayNameMap = new HashMap<String, Map<String, String>>();
	}


	public void search(String query, CyWebServiceEvent e){
        WebServiceClientImpl try2 = (KEGGClient) KEGGClient.getClient();
        try{
//        	setProperty();

        	
        	Object blah1 = try2.execute("findBinaryInteractions", new Class[]{String.class}, new Object[]{nodeId});

//	This is from Intact (embl)
//        	SearchResult result = (SearchResult) blah1;
        	  	
        	Cytoscape.firePropertyChange("SEARCH_RESULT", "uk.ac.ebi.intact.binarysearch.wsclient", new DatabaseSearchResult(result.getTotalCount(), result, WSEventType.IMPORT_NETWORK));

        	CyWebServiceEvent cyweb1 = new CyWebServiceEvent("KEGG", WSEventType.SEARCH_DATABASE, node);
        	
        	
        	System.out.println("RESULTS.GETINTERACTIONS() RETURNS...");
        	System.out.println(result.getInteractions().toString());
        	System.out.println("END OF RESULTS.GETINTERACTIONS");
        	
        	
        	search(cyweb1.getParameter().toString(), cyweb1);
        	
        	System.out.println("SEARCH RESULTS (blah1):");
        	System.out.println(blah1);
        	System.out.println("SEARCH RESULTS (cyweb1.getParameter):");
        	System.out.println(cyweb1.getParameter());

//        	importNetwork(blah1, null);
        	importNetwork(blah1, Cytoscape.getCurrentNetwork());
			
        } catch(Exception e){
        	System.out.println(e.toString());
        }
	}
	
	// Ellen dumps in executeService code
	public void executeService(CyWebServiceEvent e) {
		System.out.println("CHECKPOINT 0 in executeService");
		System.out.println("e.getSource() is: " + e.getSource());
		System.out.println("CLIENT_ID is: " + CLIENT_ID);
		System.out.println("e.getEventType() is: " + e.getEventType());
		System.out.println("WSEventType.IMPORT_NETWORK is: " + WSEventType.IMPORT_NETWORK);
		System.out.println("e.getParameter() is: " + e.getParameter());
		System.out.println();
		
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_NETWORK)) {
				System.out.println("CHECKPOINT 1 in executeService");
				importNetwork((String)e.getParameter(), null);
			} else if (e.getEventType().equals(WSEventType.EXPAND_NETWORK)) {
				System.out.println("CHECKPOINT 2 in executeService");
				importNetwork((String)e.getParameter(), Cytoscape.getCurrentNetwork());
			} else if (e.getEventType().equals(WSEventType.SEARCH_DATABASE)) {
				System.out.println("CHECKPOINT 2 in executeService");
//				search(e.getParameter().toString(), e);				
			}
		}
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("SEARCH_NETWORK") && e.getNewValue().equals(CLIENT_ID)) {
			System.out.println("This is for me! : " + CLIENT_ID);

			importNetwork(e.getOldValue().toString(), null);
		} else if (e.getPropertyName().equals("EXPAND_NETWORK")
		           && e.getNewValue().equals(CLIENT_ID)) {
			System.out.println("This is for me! : " + CLIENT_ID);

			importNetwork(e.getOldValue().toString(), Cytoscape.getCurrentNetwork());
		}
	}

	private void getVisualStyle() {
		final VisualStyle keggStyle = new VisualStyle("KEGG Pathway Default Style");
	}

	private void importNetwork(String string, CyNetwork currentNetwork) {
		// TODO Auto-generated method stub
		Set<Node> nodes = new HashSet<Node>();
		Set<Edge> edges = new HashSet<Edge>();

		KEGGPortType port = (KEGGPortType) stub;
		StringBuilder builder = new StringBuilder();

		try {
			String pwName = "kegg";
			final String organismName = string.substring(5, 8);
			System.out.println("Org = " + string.substring(5, 8));

			if (pathwayNameMap.get(organismName) == null) {
				Map<String, String> pathwayNames = new HashMap<String, String>();
				Definition[] pwList = port.list_pathways(organismName);

				for (Definition def : pwList) {
					pathwayNames.put(def.getEntry_id(), def.getDefinition());

					if (def.getEntry_id().equals(string)) {
						pwName = def.getDefinition();
					}
				}

				pathwayNameMap.put(organismName, pathwayNames);
			} else {
				System.out.println("-------------Found in map");
				pwName = pathwayNameMap.get(organismName).get(string);
			}

			PathwayElementRelation[] relations = port.get_element_relations_by_pathway(string);
			PathwayElement[] elements = port.get_elements_by_pathway(string);
			CyAttributes att = Cytoscape.getNodeAttributes();

			Map<Integer, PathwayElement> idmap = new HashMap<Integer, PathwayElement>();

			Map<String, String> reactions = new HashMap<String, String>();

			for (PathwayElement el : elements) {
				System.out.print("Element " + el.getElement_id() + ": " + el.getType() + " - ");

				for (String name : el.getNames()) {
					if (name.startsWith(organismName)) {
						builder.append(name + " ");
					}

					System.out.print(name + ", ");
					nodes.add(Cytoscape.getCyNode(name, true));
					att.setAttribute(name, "KEGG Element Type", el.getType());
					if(el.getType().equals("map") && pathwayNameMap.get(organismName).get(name) != null) {
						att.setAttribute(name, "gene name", pathwayNameMap.get(organismName).get(name));
					}
				}

				idmap.put(el.getElement_id(), el);
				System.out.println("");
			}

			System.out.println(builder.toString());

			String result = port.bget(builder.toString());
			String[] ents = result.split("///");
			System.out.println("===================Got " + ents.length + " entries");

			for (String en : ents) {
				//				System.out.println(en);
				String[] parts = en.split("\n");

				if (parts.length > 3) {
					System.out.println("2: " + parts[1]);
					System.out.println("3: " + parts[2]);

					if (parts[2].startsWith("NAME")) {
						Pattern pattern = Pattern.compile(" [, ]+");
						String[] strs = pattern.split(parts[1]);

						String[] p2 = parts[2].replaceAll(" ", "").split(",");
						String name = p2[0].substring(4);
						att.setAttribute(organismName + ":" + strs[1], "gene name", name);
					}
				}
			}

			Node n1 = null;
			Node n2 = null;
			Edge e1 = null;
			Edge e2 = null;

			Node compound = null;

			System.out.println("####### of relations = " + relations.length);

			for (int i = 0; i < relations.length; i++) {
				Integer id1 = relations[i].getElement_id1();
				Integer id2 = relations[i].getElement_id2();

				String name1 = idmap.get(id1).getNames()[0];
				String name2 = idmap.get(id2).getNames()[0];

				System.out.println("Node 1 = " + name1 + ", Node 2 = " + name2);

				n1 = Cytoscape.getCyNode(name1, true);
				n2 = Cytoscape.getCyNode(name2, true);
				att.setAttribute(name1, "KEGG Element Type", idmap.get(id1).getType());
				att.setAttribute(name2, "KEGG Element Type", idmap.get(id2).getType());

				att.setAttribute(name1, "KEGG ID", idmap.get(id1).getNames()[0]);
				att.setAttribute(name2, "KEGG ID", idmap.get(id2).getNames()[0]);

				nodes.add(n1);
				nodes.add(n2);

				Subtype[] subtypes = relations[i].getSubtypes();

				for (Subtype st : subtypes) {
					String cpName = "?";

					if (st.getElement_id() == 0) {
						e1 = Cytoscape.getCyEdge(n1, n2, "interaction", st.getRelation(), true);
						Cytoscape.getEdgeAttributes()
						         .setAttribute(e1.getIdentifier(), "KEGG Relation", st.getRelation());
						edges.add(e1);
					} else {
						cpName = idmap.get(st.getElement_id()).getNames()[0];
						compound = Cytoscape.getCyNode(cpName, true);
						System.out.println("subtype = " + st.getRelation() + ", "
						                   + st.getElement_id() + ", " + cpName);
						att.setAttribute(cpName, "KEGG Element Type", st.getRelation());
						att.setAttribute(cpName, "KEGG ID", cpName);
						nodes.add(compound);
						e1 = Cytoscape.getCyEdge(n1, compound, "interaction",
						                         relations[i].getType(), true);
						e2 = Cytoscape.getCyEdge(compound, n2, "interaction",
						                         relations[i].getType(), true);

						Cytoscape.getEdgeAttributes()
						         .setAttribute(e1.getIdentifier(), "KEGG Relation", st.getRelation());
						Cytoscape.getEdgeAttributes()
						         .setAttribute(e2.getIdentifier(), "KEGG Relation", st.getRelation());
						edges.add(e1);
						edges.add(e2);
					}
				}
			}

			// Now extract reactions.
			String[] reactions2 = port.get_reactions_by_pathway(string);
			StringBuilder b2 = new StringBuilder();

			

			String result2 = port.bget(string);

			System.out.println("===================Got2 " + result2);

			CyNetwork net = Cytoscape.createNetwork(nodes, edges, pwName, null);
			CyNetworkView view;

			if (Cytoscape.getNetworkView(net.getIdentifier()).equals(Cytoscape.getNullNetworkView()) == false) {
				view = Cytoscape.getNetworkView(net.getIdentifier());
			} else {
				view = Cytoscape.createNetworkView(net);
			}

			view.setVisualStyle(Cytoscape.getVisualMappingManager().getVisualStyle().getName());
			view.redrawGraph(false, true);

			Cytoscape.getNetworkAttributes().setAttribute(net.getIdentifier(), "External ID", string);
			Cytoscape.getNetworkAttributes().setAttribute(net.getIdentifier(), "Datasource", "KEGG");
			Cytoscape.getPropertyChangeSupport()
			         .firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
