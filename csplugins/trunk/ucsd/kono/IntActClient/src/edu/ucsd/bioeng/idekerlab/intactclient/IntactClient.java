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
package edu.ucsd.bioeng.idekerlab.intactclient;

import static cytoscape.data.webservice.CyWebServiceEvent.WSResponseType.DATA_IMPORT_FINISHED;
import static cytoscape.data.webservice.CyWebServiceEvent.WSResponseType.SEARCH_FINISHED;
import static cytoscape.data.webservice.CyWebServiceException.WSErrorCode.NO_RESULT;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.tab.converter.txt2tab.MitabLineException;
import psidev.psi.mi.tab.converter.txt2tab.MitabLineParser;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.Author;
import psidev.psi.mi.tab.model.Confidence;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.InteractionDetectionMethod;
import psidev.psi.mi.tab.model.InteractionType;
import psidev.psi.mi.tab.model.Interactor;
import uk.ac.ebi.intact.BinarySearch;
import uk.ac.ebi.intact.BinarySearchService;
import uk.ac.ebi.intact.BinarySearchService_Impl;
import uk.ac.ebi.intact.SimplifiedSearchResult;
import uk.ac.ebi.intact.psimitab.IntActBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntActColumnHandler;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEventListener;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImplWithGUI;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.data.webservice.util.NetworkExpansionMenu;
import cytoscape.layout.Tunable;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.EdgeCalculator;
import cytoscape.visual.calculators.NodeCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import giny.model.Edge;
import giny.model.Node;
import giny.view.NodeView;



/**
 * IntAct web service client.
 *
 * @author kono
 * @version 0.5
 * @since Cytoscape 2.6
 *
 */
public class IntactClient extends WebServiceClientImplWithGUI<BinarySearchService, JPanel> implements NetworkImportWebServiceClient {
	
	private static final Icon ABOUT_ICON = new ImageIcon(IntactClient.class.getResource("/images/logo_intact_small.gif"));
	
	// Display name of this client.
	private static final String DISPLAY_NAME = "IntAct Web Service Client";

	// Client ID. This should be unique.
	private static final String CLIENT_ID = "intact";

	// Instance of this client.  This is a singleton.
	private static WebServiceClient<BinarySearchService> client = new IntactClient();

	// Visual Style name for the networks generated by this client.
	private static final String DEF_VS_NAME = "IntAct Style";
	private VisualStyle defaultVS = null;

	// Attributes. 
	private final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
	private final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

	// Nodes and edges to be added.
	private Set<Node> nodes = new HashSet<Node>();
	private Set<Edge> edges = new HashSet<Edge>();
	
	private SimplifiedSearchResult sResult = null;

	private void setDescription() {
		description = "http://www.ebi.ac.uk/intact/site/contents/all_printerfriendly.jsf";
	}
	
	/**
	 * Return instance of this client.
	 * @return
	 */
	public static WebServiceClient<BinarySearchService> getClient() {
		return client;
	}

	/**
	 * Creates a new IntactClient object.
	 */
	private IntactClient() {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.NETWORK }, null, new BinarySearchService_Impl(), null);
		setDescription();
		// Set properties for this client.
		setProperty();
	}

	/**
	 * Set props for this client.
	 */
	private void setProperty() {
		props = new ModulePropertiesImpl(clientID, "wsc");

		props.add(new Tunable("max_interactions", "Maximum number of records", Tunable.INTEGER,
		                      new Integer(50000)));

		props.add(new Tunable("timeout", "Timeout (sec.)", Tunable.INTEGER, new Integer(1200)));
		//props.add(new Tunable("select_interaction", "Import only selected interactions",
		//                      Tunable.BOOLEAN, new Boolean(false)));
	}

	private void setListAttr(final CyAttributes cyAttr, final List<CrossReference> crossRefs,
	                         final String id, final String attrName) {
		if ((crossRefs != null) && (crossRefs.size() > 0)) {
			Collection<Object> attr = cyAttr.getListAttribute(id, attrName);

			if (attr == null) {
				List<String> newList = new ArrayList<String>();

				for (CrossReference ref : crossRefs) {
					if (ref.getText() != null)
						newList.add(ref.getText());
				}

				cyAttr.setListAttribute(id, attrName, newList);
			} else {
				for (CrossReference ref : crossRefs) {
					if ((ref.getText() != null) && (attr.contains(ref.getText()) == false))
						attr.add(ref.getText());
				}

				cyAttr.setListAttribute(id, attrName, new ArrayList<Object>(attr));
			}
		}
	}

	private void setDBListAttr(final List<CrossReference> crossRefs, final String id) {
		for (CrossReference prop : crossRefs) {
			Collection<Object> attr = nodeAttr.getListAttribute(id, prop.getDatabase());

			if (attr == null) {
				List<String> newList = new ArrayList<String>();
				newList.add(prop.getIdentifier().replaceAll("\"", ""));
				nodeAttr.setListAttribute(id, prop.getDatabase(), newList);
			} else {
				if (attr.contains(prop.getIdentifier()) == false) {
					attr.add(prop.getIdentifier().replaceAll("\"", ""));
					nodeAttr.setListAttribute(id, prop.getDatabase(), new ArrayList<Object>(attr));
				}
			}
		}
	}

	/**
	 *  Import network from the search result.
	 * @throws CyWebServiceException
	 */
	private void importNetwork(final Object searchResult, final CyNetwork net)
	    throws CyWebServiceException {
		if (searchResult instanceof SimplifiedSearchResult == false) {
			throw new CyWebServiceException(NO_RESULT);
		}

		final SimplifiedSearchResult result = (SimplifiedSearchResult) searchResult;
		final SearchResult<IntActBinaryInteraction> srObj = toSearchResult(result);
		final List<IntActBinaryInteraction> binaryInteractions = srObj.getInteractions();

		final Integer max = (Integer) props.get("max_interactions").getValue();
		int i = 0;
		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();

		Node n1 = null;
		Node n2 = null;

		// Loop through the result and extract the interactions.
		for (IntActBinaryInteraction bin : binaryInteractions) {
			n1 = Cytoscape.getCyNode(extractNodeEntry(bin.getInteractorA()), true);
			n2 = Cytoscape.getCyNode(extractNodeEntry(bin.getInteractorB()), true);

			nodes.add(n1);
			nodes.add(n2);

			// Extract node attributes.

			if (bin.hasInteractorTypeA()) {
				setListAttr(nodeAttr, bin.getInteractorTypeA(), n1.getIdentifier(),
				            "interactor type");
				setDBListAttr(bin.getInteractorTypeA(), n1.getIdentifier());
			}

			if (bin.hasInteractorTypeB()) {
				setListAttr(nodeAttr, bin.getInteractorTypeB(), n2.getIdentifier(),
				            "interactor type");
				setDBListAttr(bin.getInteractorTypeB(), n2.getIdentifier());
			}

			if (bin.hasPropertiesA())
				setDBListAttr(bin.getPropertiesA(), n1.getIdentifier());

			if (bin.hasPropertiesB())
				setDBListAttr(bin.getPropertiesB(), n2.getIdentifier());

			// Add edges
			extractEdgeEntry(n1, n2, bin);

			i++;

			if (i > max) {
				break;
			}
		}

		if (net == null) {
			Cytoscape.createNetwork(nodes, edges, "IntAct: ", null);
			Cytoscape.firePropertyChange(DATA_IMPORT_FINISHED.toString(), null, "IntAct Network");
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
	}

	private String extractNodeEntry(Interactor a) {
		// Try to use UniProt Acc # as the node ID.
		String aID = null;

		for (CrossReference ref : a.getIdentifiers()) {
			if (ref.getDatabase().equals("uniprotkb"))
				aID = ref.getIdentifier().replaceAll("\"", "");
			else if (ref.getDatabase().equals("intact")) {
				if (aID != null) {
					nodeAttr.setAttribute(aID, "intact id", ref.getIdentifier().replaceAll("\"", ""));
				} else {
					nodeAttr.setAttribute(ref.getIdentifier(), "intact id", ref.getIdentifier().replaceAll("\"", ""));
				}
			}
		}

		if (aID == null)
			aID = a.getIdentifiers().iterator().next().getIdentifier().replaceAll("\"", "");

		List<String> aliasA = new ArrayList<String>();

		for (CrossReference ref : a.getAlternativeIdentifiers()) {
			aliasA.add(ref.getIdentifier());
		}

		if(aliasA.size() != 0)
			nodeAttr.setAttribute(aID, "official symbol", aliasA.get(0));
		
		if (a.getAliases().size() != 0) {

			for (Alias ref : a.getAliases()) {
				aliasA.add(ref.getName());
			}
		}

		nodeAttr.setListAttribute(aID, "aliases", aliasA);

		if (a.hasOrganism() && (a.getOrganism().getIdentifiers().size() != 0)) {
			final String value = a.getOrganism().getIdentifiers().iterator().next().getText();
			if(value != null)
				nodeAttr.setAttribute(aID, "species", value);
		}

		return aID;
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

		List<CrossReference> roleA = bin.getExperimentalRolesInteractorA();
		List<CrossReference> roleB = bin.getExperimentalRolesInteractorB();

		for (int i = 0; i < numEdge; i++) {
			final Edge e = Cytoscape.getCyEdge(a, b, "interaction", acs.get(i).getIdentifier(), true);
			edges.add(e);

			final String edgeID = e.getIdentifier();

			if (bin.hasExperimentalRolesInteractorA() && (roleA.size() > 0) && roleA.size()>i)
				edgeAttr.setAttribute(e.getIdentifier(), "source experimental role",
				                      roleA.get(i).getText());

			if (bin.hasExperimentalRolesInteractorB() && (roleB.size() > 0) && roleB.size()>i)
				edgeAttr.setAttribute(e.getIdentifier(), "target experimental role",
				                      roleB.get(i).getText());

			if (itrTypes.size() > i)
				edgeAttr.setAttribute(edgeID, "interaction type", itrTypes.get(i).getText());

			// Set attributes
			if ((auth.size() != 0) && (auth.size() > i))
				edgeAttr.setAttribute(edgeID, "author", auth.get(i).getName());

			if ((detMethod.size() != 0) && (detMethod.size() > i))
				edgeAttr.setAttribute(edgeID, "detection method", detMethod.get(i).getText());

			if (bin.hasHostOrganism() && (hostOrg.size() > i)
			    && (hostOrg.get(i).getIdentifier() != null))
				edgeAttr.setAttribute(edgeID, "host organism", hostOrg.get(i).getIdentifier());

			if (bin.hasDatasetName() && (dataset.size() > i)) {
				//				System.out.println("Dataset len = " + dataset.size() +", edge = " + numEdge);
				edgeAttr.setAttribute(edgeID, "dataset", dataset.get(i));
			}

			if ((pubs.size() != 0) && (pubs.size() > i))
				edgeAttr.setAttribute(edgeID, "publication", pubs.get(i).getIdentifier());

			if ((sourceDB.size() != 0) && (sourceDB.size() > i))
				edgeAttr.setAttribute(edgeID, "source database", sourceDB.get(i).getText());

			if ((confs.size() != 0) && (confs.size() > i)) {
				//				System.out.println("CONF = " + confs.get(0).getText());
				//				System.out.println("CONF VAL = " + confs.get(0).getValue());
				edgeAttr.setAttribute(edgeID, "confidence", confs.get(i).getText());
				edgeAttr.setAttribute(edgeID, "confidence value", confs.get(i).getValue());
			}

			if (expMethod != null)
				edgeAttr.setAttribute(edgeID, "expansion method", expMethod);
		}
	}

	private void search(String query, CyWebServiceEvent<String> e) throws CyWebServiceException {

		sResult = null;
		final ExecutorService exe = Executors.newCachedThreadPool();
		long startTime = System.currentTimeMillis();
		
		Future<?> res = null;
		try {
			res = exe.submit(new SearchTask(query, exe));
			res.get((Integer) props.get("timeout")
                    .getValue(), TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("IntAct DB search finished in " + sec + " msec.");
		} catch (ExecutionException ee) {
			throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		} catch (TimeoutException te) {
			throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		} catch (InterruptedException ie) {
			throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		} finally {
			res.cancel(true);
			exe.shutdown();
		}

		if(sResult == null) return;
		
		WSEventType nextMove = e.getNextMove();
		if (nextMove == null)
			nextMove = WSEventType.IMPORT_NETWORK;
		
		Cytoscape.firePropertyChange(SEARCH_FINISHED.toString(), this.clientID,
				new DatabaseSearchResult<SimplifiedSearchResult>(
						sResult.getTotalResults(),sResult, nextMove));
	}

	private SearchResult<IntActBinaryInteraction> toSearchResult(SimplifiedSearchResult ssr) {
		List<IntActBinaryInteraction> interactions = new ArrayList<IntActBinaryInteraction>(ssr
		                                                                                                                                                                                                                                                                                                                                                                                                                                                      .getInteractionLines().length);

		MitabLineParser parser = new MitabLineParser();

		parser.setBinaryInteractionClass(IntActBinaryInteraction.class);
		parser.setColumnHandler(new IntActColumnHandler());

		for (String line : ssr.getInteractionLines()) {
			IntActBinaryInteraction interaction = null;

			try {
				interaction = (IntActBinaryInteraction) parser.parse(line);
			} catch (MitabLineException e) {
				throw new RuntimeException("Wrong line returned by the server: " + line);
			}

			interactions.add(interaction);
		}

		return new SearchResult<IntActBinaryInteraction>(interactions, ssr.getTotalResults(),
		                                                 ssr.getFirstResult(), ssr.getMaxResults(),
		                                                 ssr.getLuceneQuery());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	@Override
	public void executeService(CyWebServiceEvent e) throws CyWebServiceException {
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
		PassThroughMapping me = new PassThroughMapping("", "detection method");

		EdgeCalculator calce = new EdgeCalculator(DEF_VS_NAME + "-" + "EdgeLabelMapping", me, null,
		                                          EDGE_LABEL);
		nac.setCalculator(calc);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, Color.white);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 100);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_OPACITY, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 1);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH, 80);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT, 35);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR, Color.white);
		nac.setNodeSizeLocked(false);

		eac.setCalculator(calce);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR, Color.green);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_COLOR, Color.red);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_FONT_SIZE, 5);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 120);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_SRCARROW_OPACITY, 120);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_OPACITY, 120);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_OPACITY, 80);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_WIDTH, 5);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL, "");

		// Prey and Bait
		DiscreteMapping targetShape = new DiscreteMapping(ArrowShape.NONE,
		                                                  "target experimental role",
		                                                  ObjectMapping.EDGE_MAPPING);

		targetShape.putMapValue("bait", ArrowShape.DIAMOND);
		targetShape.putMapValue("prey", ArrowShape.CIRCLE);

		EdgeCalculator targetShapeCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		                                                    + "EdgeTargetArrowShapeMapping",
		                                                    targetShape, null,
		                                                    VisualPropertyType.EDGE_TGTARROW_SHAPE);

		DiscreteMapping sourceShape = new DiscreteMapping(ArrowShape.NONE,
		                                                  "source experimental role",
		                                                  ObjectMapping.EDGE_MAPPING);

		sourceShape.putMapValue("bait", ArrowShape.DIAMOND);
		sourceShape.putMapValue("prey", ArrowShape.CIRCLE);

		EdgeCalculator sourceShapeCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		                                                    + "EdgeSourceArrowShapeMapping",
		                                                    sourceShape, null,
		                                                    VisualPropertyType.EDGE_SRCARROW_SHAPE);

		DiscreteMapping targetColor = new DiscreteMapping(Color.black, "target experimental role",
		                                                  ObjectMapping.EDGE_MAPPING);

		targetColor.putMapValue("bait", Color.red);
		targetColor.putMapValue("prey", Color.red);

		EdgeCalculator targetColorCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		                                                    + "EdgeTargetArrowColorMapping",
		                                                    targetColor, null,
		                                                    VisualPropertyType.EDGE_TGTARROW_COLOR);

		DiscreteMapping sourceColor = new DiscreteMapping(Color.black, "source experimental role",
		                                                  ObjectMapping.EDGE_MAPPING);

		sourceColor.putMapValue("bait", Color.red);
		sourceColor.putMapValue("prey", Color.red);

		EdgeCalculator sourceColorCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		                                                    + "EdgeSourceArrowColorMapping",
		                                                    targetColor, null,
		                                                    VisualPropertyType.EDGE_SRCARROW_COLOR);

		eac.setCalculator(sourceShapeCalc);
		eac.setCalculator(targetShapeCalc);
		eac.setCalculator(sourceColorCalc);
		eac.setCalculator(targetColorCalc);

		return defStyle;
	}

	public List<JMenuItem> getNodeContextMenuItems(NodeView nv) {
		List<JMenuItem> menuList = new ArrayList<JMenuItem>();
		menuList.add(NetworkExpansionMenu.getExpander(this));
		return menuList;
	}
	
	public Icon getIcon(IconSize type) {
		return ABOUT_ICON;
	}
	
	class SearchTask implements Callable, CyWebServiceEventListener {
		
		String query;
		ExecutorService exe;
		
		public SearchTask(String query, ExecutorService exe) {
			this.query = query;
			this.exe = exe;
			WebServiceClientManager.getCyWebServiceEventSupport().addCyWebServiceEventListener(this);
		}
		
		public Object call() throws CyWebServiceException {
			BinarySearch searchClient = ((BinarySearchService_Impl) clientStub).getBinarySearchPort();

			try {
				sResult = searchClient.findBinaryInteractionsLimited(query, 0,
                        (Integer) props.get("max_interactions")
                                       .getValue());
			} catch (RemoteException e) {
				throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
			}
			return null;
		}

		public void executeService(CyWebServiceEvent event)
				throws CyWebServiceException {

			if (event.getEventType().equals(WSEventType.CANCEL)) {
				throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
			}
		}
	}
}
