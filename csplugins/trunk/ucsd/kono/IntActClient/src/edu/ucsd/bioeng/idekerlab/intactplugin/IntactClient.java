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
package edu.ucsd.bioeng.idekerlab.intactplugin;

import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.intact.binarysearch.wsclient.BinarySearchServiceClient;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.Alias;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.BinaryInteraction;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.Confidence;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.CrossReference;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.InteractionDetectionMethod;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.Interactor;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.SearchResult;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.layout.Tunable;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.NodeCalculator;
import cytoscape.visual.mappings.PassThroughMapping;
import giny.model.Edge;
import giny.model.Node;


/**
 *
 */
public class IntactClient extends WebServiceClientImpl implements NetworkImportWebServiceClient {
	private static final String DISPLAY_NAME = "IntAct Web Service Client";
	private static final String CLIENT_ID = "intact";
	private static final WebServiceClient client;
	private static final String DEF_VS_NAME = "IntAct Style";
	private VisualStyle defaultVS = null;

	static {
		client = new IntactClient();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static WebServiceClient getClient() {
		return client;
	}

	/**
	 * Creates a new IntactClient object.
	 */
	private IntactClient() {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.NETWORK });

		stub = new BinarySearchServiceClient();

		setProperty();
	}

	private void setProperty() {
		props = new ModulePropertiesImpl(clientID, "wsc");

		List<String> searchType = new ArrayList<String>();
		searchType.add("Lucene");
		searchType.add("Keyword Search");

		//		props.add(new Tunable("search_type", "The edge attribute that contains the weights",
		//                Tunable.LIST, searchType));
		props.add(new Tunable("search_mode", "Enable keyword search", Tunable.BOOLEAN,
		                      new Boolean(false)));

		props.add(new Tunable("max_interactions", "Maximum number of interactions",
		                      Tunable.INTEGER, new Integer(500)));
		props.add(new Tunable("search_depth", "Search depth", Tunable.INTEGER, new Integer(0)));
		props.add(new Tunable("select_interaction", "Import only selected interactions",
		                      Tunable.BOOLEAN, new Boolean(false)));
	}

	/**
	 *  DOCUMENT ME!
	 */
	private void importNetwork(Object searchResult, CyNetwork net) {
		try {
			if (stub == null) {
				stub = new BinarySearchServiceClient();
			}
			
			if(searchResult instanceof SearchResult == false) {
				return;
			}

			BinarySearchServiceClient client = (BinarySearchServiceClient) stub;
			SearchResult result = (SearchResult) searchResult;
			List<BinaryInteraction> binaryInteractions = result.getInteractions();

			final Integer max = (Integer) props.get("max_interactions").getValue();
			int i = 0;
			Set<Node> nodes = new HashSet<Node>();
			Set<Edge> edges = new HashSet<Edge>();

			Node n1 = null;
			Node n2 = null;
			Edge e = null;

			List<Alias> alias1;
			List<Alias> alias2;

			String a1;
			String a2;

			Interactor a;
			Interactor b;
			CyAttributes attr = Cytoscape.getNodeAttributes();
			CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

			List<String> nonUP = new ArrayList<String>();

			for (BinaryInteraction bin : binaryInteractions) {
				// do stuff with interactions here
				a = bin.getInteractorA();
				b = bin.getInteractorB();

				List<CrossReference> ids = a.getIdentifiers();

				for (CrossReference ref : ids) {
					System.out.println("ID A: " + ref.getDatabase() + ": " + ref.getIdentifier()
					                   + ", " + ref.getText());

					if (ref.getDatabase().equals("uniprotkb") == false) {
						nonUP.add(ref.getDatabase() + ":" + ref.getIdentifier());
					}
				}

				String aID = a.getIdentifiers().get(0).getIdentifier();

				List<Alias> al = a.getAliases();

				for (Alias cr : al) {
					System.out.println("--------Alias A: " + cr.getAliasType() + ": "
					                   + cr.getDbSource() + ", " + cr.getName());
				}

				List<CrossReference> altA = a.getAlternativeIdentifiers();

				for (CrossReference ref : altA) {
					attr.setAttribute(aID, "Official Symbol", ref.getIdentifier());
					System.out.println("########Alt A: " + ref.getDatabase() + ": "
					                   + ref.getIdentifier() + ", " + ref.getText());

					break;
				}

				ids = b.getIdentifiers();

				for (CrossReference ref : ids) {
					System.out.println("ID B: " + ref.getDatabase() + ": " + ref.getIdentifier()
					                   + ", " + ref.getText());

					if (ref.getDatabase().equals("uniprotkb") == false) {
						nonUP.add(ref.getDatabase() + ":" + ref.getIdentifier());
					}
				}

				String bID = b.getIdentifiers().get(0).getIdentifier();

				List<Alias> al2 = b.getAliases();

				for (Alias cr : al2) {
					System.out.println("--------Alias B: " + cr.getAliasType() + ": "
					                   + cr.getDbSource() + ", " + cr.getName());
				}

				List<CrossReference> altB = b.getAlternativeIdentifiers();

				for (CrossReference ref : altB) {
					attr.setAttribute(bID, "Official Symbol", ref.getIdentifier());
					System.out.println("########Alt B: " + ref.getDatabase() + ": "
					                   + ref.getIdentifier() + ", " + ref.getText());

					break;
				}

				n1 = Cytoscape.getCyNode(a.getIdentifiers().get(0).getIdentifier(), true);
				n2 = Cytoscape.getCyNode(b.getIdentifiers().get(0).getIdentifier(), true);

				if ((a.getOrganism() != null) && (a.getOrganism().getIdentifiers() != null)
				    && (a.getOrganism().getIdentifiers().size() > 0)) {
					attr.setAttribute(n1.getIdentifier(), "species",
					                  a.getOrganism().getIdentifiers().get(0).getText());
				}

				if ((b.getOrganism() != null) && (b.getOrganism().getIdentifiers() != null)
				    && (b.getOrganism().getIdentifiers().size() > 0)) {
					attr.setAttribute(n2.getIdentifier(), "species",
					                  b.getOrganism().getIdentifiers().get(0).getText());
				}

				if ((bin.getInteractorA().getAliases() != null)
				    && (bin.getInteractorA().getAliases().size() != 0)) {
					alias1 = bin.getInteractorA().getAliases();
				}

				if ((bin.getInteractorB().getAliases() != null)
				    && (bin.getInteractorB().getAliases().size() != 0)) {
					alias2 = bin.getInteractorB().getAliases();
				}

				e = Cytoscape.getCyEdge(n1, n2, "interaction",
				                        bin.getInteractionTypes().get(0).getText(), true);
				System.out.println("-----------> " + bin.getInteractorA() + " interacts with "
				                   + bin.getInteractorB());

				nodes.add(n1);
				nodes.add(n2);
				edges.add(e);

				List<Confidence> confs = bin.getConfidenceValues();

				for (Confidence c : confs) {
					edgeAttr.setAttribute(e.getIdentifier(), "confidence", c.getValue());
				}

				List<InteractionDetectionMethod> iType = bin.getDetectionMethods();

				for (InteractionDetectionMethod it : iType) {
					edgeAttr.setAttribute(e.getIdentifier(), "interaction detection method",
					                      it.getText());
				}

				edgeAttr.setAttribute(e.getIdentifier(), "Source Database",
				                      bin.getSourceDatabases().get(0).getDatabase());

				i++;

				if (i > max) {
					break;
				}
			}

			for (String n : nonUP) {
				System.out.println("!!!!!!!!Non UP id = " + n);
			}

			if (net == null) {
				CyNetwork newNet = Cytoscape.createNetwork(nodes, edges, "IntAct: ", null);
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, null);
			} else {
				for (Node node : nodes) {
					net.addNode(node);
				}

				for (Edge edge : edges) {
					net.addEdge(edge);
				}

				net.setSelectedNodeState(nodes, true);
				final PropertyChangeEvent pce = new PropertyChangeEvent(this, Cytoscape.NETWORK_MODIFIED, null, null);
				Cytoscape.getPropertyChangeSupport().firePropertyChange(pce);
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void search(String query, CyWebServiceEvent e) {
		if (stub == null) {
			stub = new BinarySearchServiceClient();
		}

		BinarySearchServiceClient client = (BinarySearchServiceClient) stub;
		System.out.println("=========CLASS = " + client.getClass());

		SearchResult result = client.findBinaryInteractions(query);
		if(e.getNextMove() != null) {
			Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID, new DatabaseSearchResult(result.getTotalCount(), result, e.getNextMove()));
		} else {
			Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID, new DatabaseSearchResult(result.getTotalCount(), result, WSEventType.IMPORT_NETWORK));
		}
		

	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	@Override
	public void executeService(CyWebServiceEvent e) {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_NETWORK)) {
				importNetwork(e.getParameter(), null);
			} else if (e.getEventType().equals(WSEventType.EXPAND_NETWORK)) {
				importNetwork(e.getParameter(), Cytoscape.getCurrentNetwork());
			} else if (e.getEventType().equals(WSEventType.SEARCH_DATABASE)) {
				
				search(e.getParameter().toString(), e);
			}
		}
	}

	/**
	 *  Returns default visual style for networks build from this database
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualStyle getDefaultVisualStyle() {
		if (defaultVS == null) {
			defaultVS = defaultVisualStyleBuilder();
		}

		return defaultVS;
	}

	/**
	 * Generate default visual style.
	 * The style is database-dependent.
	 * 
	 * @return default visual style.
	 */
	private VisualStyle defaultVisualStyleBuilder() {
		final VisualStyle defStyle = new VisualStyle(DEF_VS_NAME);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle.getGlobalAppearanceCalculator();

		gac.setDefaultBackgroundColor(Color.black);

		PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);

		NodeCalculator calc = new NodeCalculator(DEF_VS_NAME + "-" + "NodeLabelMapping", m, null,
		                                         NODE_LABEL);
		nac.setCalculator(calc);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, Color.white);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 150);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, Color.white);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR, new Color(0, 100, 200));
		nac.setNodeSizeLocked(false);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR, Color.white);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 150);

		return defStyle;
	}
}
