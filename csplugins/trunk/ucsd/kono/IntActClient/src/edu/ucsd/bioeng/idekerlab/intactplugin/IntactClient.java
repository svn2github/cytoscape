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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;

import cytoscape.layout.Tunable;

import cytoscape.util.ModulePropertiesImpl;

import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import cytoscape.visual.VisualStyle;

import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.NodeCalculator;

import cytoscape.visual.mappings.PassThroughMapping;

import giny.model.Edge;
import giny.model.Node;

import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.Author;
import psidev.psi.mi.tab.model.Confidence;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.InteractionDetectionMethod;
import psidev.psi.mi.tab.model.InteractionType;
import psidev.psi.mi.tab.model.Interactor;

import uk.ac.ebi.intact.binarysearch.wsclient.BinarySearchServiceClient;
import uk.ac.ebi.intact.psimitab.IntActBinaryInteraction;

import java.awt.Color;

import java.beans.PropertyChangeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 */
public class IntactClient extends WebServiceClientImpl implements NetworkImportWebServiceClient {
	private static final String DISPLAY_NAME = "IntAct Web Service Client";
	private static final String CLIENT_ID = "intact";
	private static final WebServiceClient client;
	private static final String DEF_VS_NAME = "IntAct Style";
	private VisualStyle defaultVS = null;
	private final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
	private Set<Edge> edges = new HashSet<Edge>();

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
			if (searchResult instanceof SearchResult == false) {
				return;
			}

			SearchResult<IntActBinaryInteraction> result = (SearchResult) searchResult;
			List<IntActBinaryInteraction> binaryInteractions = result.getInteractions();

			final Integer max = (Integer) props.get("max_interactions").getValue();
			int i = 0;
			Set<Node> nodes = new HashSet<Node>();

			Node n1 = null;
			Node n2 = null;

			Interactor a;
			Interactor b;
			CyAttributes attr = Cytoscape.getNodeAttributes();

			List<String> nonUP = new ArrayList<String>();

			for (IntActBinaryInteraction bin : binaryInteractions) {
				// Extract interactors.
				a = bin.getInteractorA();
				b = bin.getInteractorB();

				final List<CrossReference> aIds = new ArrayList<CrossReference>(a.getIdentifiers());

				for (CrossReference ref : aIds) {
					//					System.out.println("ID A: " + ref.getDatabase() + ": " + ref.getIdentifier()
					//					                   + ", " + ref.getText());
					if (ref.getDatabase().equals("uniprotkb") == false) {
						nonUP.add(ref.getDatabase() + ":" + ref.getIdentifier());
					}
				}

				String aID = a.getIdentifiers().iterator().next().getIdentifier();

				List<Alias> al = new ArrayList<Alias>(a.getAliases());

				for (Alias cr : al) {
					System.out.println("--------Alias A: " + cr.getAliasType() + ": "
					                   + cr.getDbSource() + ", " + cr.getName());
				}

				List<CrossReference> altA = new ArrayList<CrossReference>(a
				                                                                                                                                                                                      .getAlternativeIdentifiers());

				for (CrossReference ref : altA) {
					attr.setAttribute(aID, "Official Symbol", ref.getIdentifier());

					//					System.out.println("########Alt A: " + ref.getDatabase() + ": "
					//					                   + ref.getIdentifier() + ", " + ref.getText());
					break;
				}

				final List<CrossReference> bIds = new ArrayList<CrossReference>(b.getIdentifiers());

				for (CrossReference ref : bIds) {
					System.out.println("ID B: " + ref.getDatabase() + ": " + ref.getIdentifier()
					                   + ", " + ref.getText());

					if (ref.getDatabase().equals("uniprotkb") == false) {
						nonUP.add(ref.getDatabase() + ":" + ref.getIdentifier());
					}
				}

				String bID = bIds.get(0).getIdentifier();

				List<Alias> al2 = new ArrayList<Alias>(b.getAliases());

				for (Alias cr : al2) {
					System.out.println("--------Alias B: " + cr.getAliasType() + ": "
					                   + cr.getDbSource() + ", " + cr.getName());
				}

				List<CrossReference> altB = new ArrayList<CrossReference>(b
				                                                                                                                                                                                                                     .getAlternativeIdentifiers());

				for (CrossReference ref : altB) {
					attr.setAttribute(bID, "Official Symbol", ref.getIdentifier());

					//					System.out.println("########Alt B: " + ref.getDatabase() + ": "
					//					                   + ref.getIdentifier() + ", " + ref.getText());
					break;
				}

				n1 = Cytoscape.getCyNode(aID, true);
				n2 = Cytoscape.getCyNode(bID, true);

				if (a.hasOrganism() && (a.getOrganism().getIdentifiers().size() != 0)) {
					attr.setAttribute(n1.getIdentifier(), "species",
					                  a.getOrganism().getIdentifiers().iterator().next().getText());
				}

				if (b.hasOrganism() && (b.getOrganism().getIdentifiers().size() != 0)) {
					attr.setAttribute(n2.getIdentifier(), "species",
					                  b.getOrganism().getIdentifiers().iterator().next().getText());
				}

				nodes.add(n1);
				nodes.add(n2);

				// Add edges
				extractEdgeEntry(n1, n2, bin);

				// Add more attributes
				List<String> aProp = new ArrayList<String>();

				for (CrossReference prop : bin.getExperimentalRolesInteractorA()) {
					aProp.add(prop.getText());
				}

				attr.setListAttribute(n1.getIdentifier(), "property", aProp);

				List<String> bProp = new ArrayList<String>();

				for (CrossReference prop : bin.getExperimentalRolesInteractorB()) {
					bProp.add(prop.getText());
				}

				attr.setListAttribute(n2.getIdentifier(), "property", bProp);

				i++;

				if (i > max) {
					break;
				}
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

				final PropertyChangeEvent pce = new PropertyChangeEvent(this,
				                                                        Cytoscape.NETWORK_MODIFIED,
				                                                        null, null);
				Cytoscape.getPropertyChangeSupport().firePropertyChange(pce);
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void extractEdgeEntry(Node a, Node b, IntActBinaryInteraction bin) {
		// Size of this list is equal to number of edges.
		List<CrossReference> acs = bin.getInteractionAcs();
		List<InteractionType> itrTypes = bin.getInteractionTypes();
		int numEdge = acs.size();

		List<Author> auth = bin.getAuthors();
		List<Confidence> confs = bin.getConfidenceValues();
		List<String> dataset = bin.getDataset();
		List<InteractionDetectionMethod> detMethod = bin.getDetectionMethods();
		String expMethod = bin.getExpansionMethod();
		List<CrossReference> hostOrg = bin.getHostOrganism();
		List<CrossReference> pubs = bin.getPublications();
		List<CrossReference> sourceDB = bin.getSourceDatabases();

		if (numEdge > itrTypes.size()) {
			System.out.println("####################ANOMARY! " + acs.size() + ", " + numEdge
			                   + " source = " + sourceDB.size());
		}

		System.out.println("========> size = " + numEdge + ", " + itrTypes.size());

		for (int i = 0; i < numEdge; i++) {
			final Edge e = Cytoscape.getCyEdge(a, b, "interaction", acs.get(i).getIdentifier(), true);
			edges.add(e);

			final String edgeID = e.getIdentifier();

			if (itrTypes.size() > i)
				edgeAttr.setAttribute(edgeID, "interaction type", itrTypes.get(i).getText());

			// Set attributes
			if ((auth.size() != 0) && (auth.size() > i))
				edgeAttr.setAttribute(edgeID, "author", auth.get(i).getName());

			if ((detMethod.size() != 0) && (detMethod.size() > i))
				edgeAttr.setAttribute(edgeID, "detection method", detMethod.get(i).getText());

			if ((hostOrg.size() != 0) && (hostOrg.size() > i))
				edgeAttr.setAttribute(edgeID, "host organism", hostOrg.get(i).getText());

			if ((dataset != null) && (dataset.size() != 0) && (dataset.size() > i))
				edgeAttr.setAttribute(edgeID, "dataset", dataset.get(i));

			if ((pubs.size() != 0) && (pubs.size() > i))
				edgeAttr.setAttribute(edgeID, "publication", pubs.get(i).getIdentifier());

			if ((sourceDB.size() != 0) && (sourceDB.size() > i))
				edgeAttr.setAttribute(edgeID, "source database", sourceDB.get(i).getText());

			if ((confs.size() != 0) && (confs.size() > i)) {
				edgeAttr.setAttribute(edgeID, "confidence", confs.get(i).getText());
				edgeAttr.setAttribute(edgeID, "confidence value", confs.get(i).getValue());
			}

			if (expMethod != null)
				edgeAttr.setAttribute(edgeID, "expansion method", expMethod);
		}
	}

	private void search(String query, CyWebServiceEvent e) {
		if (stub == null) {
			stub = new BinarySearchServiceClient();
		}

		BinarySearchServiceClient client = (BinarySearchServiceClient) stub;
		System.out.println("=========CLASS = " + client.getClass() + ", Max itr = "
		                   + (Integer) props.get("max_interactions").getValue());

		final SearchResult<IntActBinaryInteraction> result = client.findBinaryInteractionsLimited(query,
		                                                                                          0,
		                                                                                          (Integer) props.get("max_interactions")
		                                                                                                         .getValue());

		if (e.getNextMove() != null) {
			Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
			                             new DatabaseSearchResult(result.getTotalCount(), result,
			                                                      e.getNextMove()));
		} else {
			Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
			                             new DatabaseSearchResult(result.getTotalCount(), result,
			                                                      WSEventType.IMPORT_NETWORK));
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
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 120);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_OPACITY, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 1);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR, new Color(0, 100, 200));
		nac.setNodeSizeLocked(false);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR, Color.white);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 150);

		return defStyle;
	}
}
