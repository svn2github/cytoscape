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
package edu.ucsd.bioeng.idekerlab.ncbiclient;

import edu.ucsd.bioeng.idekerlab.ncbiclient.util.BioGRIDUtil;
import edu.ucsd.bioeng.idekerlab.ncbiclient.util.VisualStyleBuilder;
import giny.model.Edge;
import giny.model.Node;
import giny.view.NodeView;
import gov.nih.ncbi.soap.eutils.gene.Dbtag;
import gov.nih.ncbi.soap.eutils.gene.EFetchGeneService;
import gov.nih.ncbi.soap.eutils.gene.EFetchRequest;
import gov.nih.ncbi.soap.eutils.gene.EFetchResult;
import gov.nih.ncbi.soap.eutils.gene.Entrezgene;
import gov.nih.ncbi.soap.eutils.gene.GeneCommentary;
import gov.nih.ncbi.soap.eutils.gene.GeneRef;
import gov.nih.ncbi.soap.eutils.gene.OtherSource;
import gov.nih.ncbi.soap.eutils.gene.Pub;
import gov.nih.ncbi.soap.eutils.gene.Entrezgene.EntrezgeneType;
import gov.nih.ncbi.soap.eutils.gene.GeneRef.GeneRefDb;
import gov.nih.ncbi.soap.eutils.gene.GeneRef.GeneRefSyn;
import gov.nih.nlm.ncbi.soap.eutils.EUtilsService;
import gov.nih.nlm.ncbi.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.soap.eutils.esearch.ESearchRequest;
import gov.nih.nlm.ncbi.soap.eutils.esearch.ESearchResult;
import gov.nih.nlm.ncbi.soap.eutils.esearch.IdListType;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEventListener;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImplWithGUI;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.CyWebServiceEvent.WSResponseType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.data.webservice.util.NetworkExpansionMenu;
import cytoscape.layout.Tunable;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.visual.VisualStyle;

/**
 * NCBI Web Service Client Plugin main class.
 * <p>This is part of the core since 2.7.</p>
 * 
 * 
 * @author kono
 * @since Cytoscape 2.7
 */
public class NCBIClient extends
		WebServiceClientImplWithGUI<EUtilsServiceSoap, JPanel> implements
		NetworkImportWebServiceClient {

	private static final long serialVersionUID = -8920082175139743743L;

	private static final Icon ABOUT_ICON = new ImageIcon(NCBIClient.class
			.getResource("/images/entrez32.png"));

	/**
	 * Annotation categories available in NCBI
	 * 
	 * @author kono
	 * 
	 */
	public enum AnnotationCategory {
		SUMMARY("Summary"), PUBLICATION("Publications"), PHENOTYPE("Phenotypes"), PATHWAY(
				"Pathways"), GENERAL("General Protein Information"), LINK(
				"Additional Links"),

		// MARKERS("Markers"),
		GO("Gene Ontology");
		private String name;

		private AnnotationCategory(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static AnnotationCategory getValue(String dispName) {
			for (AnnotationCategory ann : values()) {
				if (ann.name.equals(dispName)) {
					return ann;
				}
			}

			return null;
		}
	}

	private static final String GENE_ID_TAG = "GeneID";

	// Display name for this Client
	private static final String DISPLAY_NAME = "NCBI Entrez EUtilities Web Service Client";

	// Client's ID
	private static final String CLIENT_ID = "ncbi_entrez";
	private static NCBIClient client;
	private static final String DEF_ITR_TYPE = "pp";
	private CopyOnWriteArrayList<Node> nodeList;
	private CopyOnWriteArrayList<Edge> edgeList;
	private ConcurrentMap<String, String> nodeAltName;
	private ConcurrentMap<String, String> nodeTypes;

	private Boolean canceled = null;
	private ExecutorService executer;

	private Map<String[], Object> attrMap = new ConcurrentHashMap<String[], Object>();
	private List<AnnotationCategory> selectedAnn = new ArrayList<AnnotationCategory>();
	private int threadNum;
	private int buketNum;
	private static final int DEF_BUCKET_SIZE = 10;
	private static final int DEF_POOL_SIZE = Runtime.getRuntime()
			.availableProcessors() * 2;
	private Map<String, Set<String>> reverseMap;

	// Visual Style name for the networks generated by this client.

	static {
		try {
			client = new NCBIClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static WebServiceClient<EUtilsServiceSoap> getClient() {
		return client;
	}

	/**
	 * Creates a new NCBIClient object.
	 * 
	 * @throws CyWebServiceException
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public NCBIClient() throws CyWebServiceException {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.NETWORK,
				ClientType.ATTRIBUTE }, null, null, null);

		EUtilsService service = new EUtilsService();

		clientStub = service.getEUtilsServiceSoap();

		props = new ModulePropertiesImpl(clientID, "wsc");
		props.add(new Tunable("timeout", "Timeout for search (sec.)",
				Tunable.INTEGER, new Integer(6000)));
		props.add(new Tunable("max_search_result",
				"Maximum number of search result", Tunable.INTEGER,
				new Integer(10000)));
		props.add(new Tunable("thread_pool_size", "Thread pool size",
				Tunable.INTEGER, new Integer(DEF_POOL_SIZE)));
		props.add(new Tunable("buket_size", "Number of IDs send at once",
				Tunable.INTEGER, new Integer(DEF_BUCKET_SIZE)));

		prepareDescription();

		executer = Executors.newFixedThreadPool(DEF_POOL_SIZE);
	}

	private void prepareDescription() {
		description = "http://www.ncbi.nlm.nih.gov/entrez/query/static/esoap_help.html";
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 * @throws CyWebServiceException
	 */
	@Override
	public void executeService(CyWebServiceEvent e)
			throws CyWebServiceException {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_NETWORK)) {
				importNetwork(e.getParameter(), null);
			} else if (e.getEventType().equals(WSEventType.EXPAND_NETWORK)) {
				importNetwork(e.getParameter(), Cytoscape.getCurrentNetwork());
			} else if (e.getEventType().equals(WSEventType.SEARCH_DATABASE)) {
				search(e);
			} else if (e.getEventType().equals(WSEventType.IMPORT_ATTRIBUTE)) {
				importAnnotations(e.getParameter());
			}
		}
	}

	private void search(CyWebServiceEvent<String> e)
			throws CyWebServiceException {
		final ESearchRequest searchParam = new ESearchRequest();
		final String keyword = e.getParameter();

		searchParam.setDb("gene");
		searchParam.setRetMax(props.getValue("max_search_result"));
		// keyword = keyword.replace(" ", "+");
		searchParam.setTerm(keyword);

		ESearchResult result = null;

		long startTime = System.currentTimeMillis();
		executer = Executors.newSingleThreadExecutor();

		Future<ESearchResult> res = executer.submit(new SearchDatabaseTask(
				searchParam));

		try {
			result = res.get(Integer.parseInt(props.getValue("timeout")),
					TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("NCBI Search finished in " + sec + " msec.");
		} catch (ExecutionException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		} catch (TimeoutException te) {
			// TODO Auto-generated catch block
			te.printStackTrace();
		} catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			ie.printStackTrace();
		} finally {
			res.cancel(true);
			executer.shutdown();
		}

		if (result == null) {
			return;
		}

		int resSize = Integer.parseInt(result.getCount());
		System.out.println("Number of Result from Entrez Gene = " + resSize);

		if (e.getNextMove() != null) {
			Cytoscape.firePropertyChange(WSResponseType.SEARCH_FINISHED
					.toString(), this.clientID,
					new DatabaseSearchResult<ESearchResult>(resSize, result, e
							.getNextMove()));
		} else {
			Cytoscape.firePropertyChange(WSResponseType.SEARCH_FINISHED
					.toString(), this.clientID,
					new DatabaseSearchResult<ESearchResult>(Integer
							.getInteger(result.getCount()), result,
							WSEventType.IMPORT_NETWORK));
		}
	}

	private void importAnnotations(Object parameter)
			throws CyWebServiceException {
		/*
		 * Extract parameter from the message object
		 */
		if (parameter instanceof AttributeImportQuery == false)
			return;

		final Object[] selectedAttr = (Object[]) ((AttributeImportQuery) parameter)
				.getParameter();

		AnnotationCategory ann;
		selectedAnn = new ArrayList<AnnotationCategory>();

		for (Object name : selectedAttr) {
			System.out.println("Category Selected: " + name);
			ann = AnnotationCategory.getValue(name.toString());

			if (ann != null)
				selectedAnn.add(ann);
		}

		final String keyAttrName = ((AttributeImportQuery) parameter)
				.getKeyCyAttrName();
		System.out.println("Mapping key attribute name: " + keyAttrName);

		// nodeAttrMap = new ConcurrentHashMap<String[], Object>();
		attrMap = new ConcurrentHashMap<String[], Object>();

		long startTime = System.currentTimeMillis();
		setPerformanceParameters();

		final ExecutorService e = Executors.newFixedThreadPool(threadNum);

		final List<Node> nodes = Cytoscape.getRootGraph().nodesList();

		final Set<String> query = new HashSet<String>();

		if (keyAttrName == "ID")
			for (Node n : nodes) {
				if (n.getIdentifier().contains(":") == false)
					query.add(n.getIdentifier());
			}
		else {
			reverseMap = new HashMap<String, Set<String>>();

			Set<String> ids;
			Object attrVal = null;
			CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

			for (Node n : nodes) {
				if (nodeAttr.getType(keyAttrName) == CyAttributes.TYPE_SIMPLE_LIST) {
					attrVal = nodeAttr.getListAttribute(n.getIdentifier(),
							keyAttrName);
				} else {
					attrVal = nodeAttr.getAttribute(n.getIdentifier(),
							keyAttrName);
				}

				if (attrVal != null) {
					if (attrVal.getClass() == String.class) {
						if (attrVal.toString().contains(":") == false) {
							query.add(attrVal.toString());
							ids = reverseMap.get(attrVal.toString());

							if (ids == null) {
								ids = new HashSet<String>();
							}

							ids.add(n.getIdentifier());
							reverseMap.put(attrVal.toString(), ids);
						}
					} else if (attrVal instanceof List) {
						for (Object val : ((List) attrVal)) {
							if (val.toString().contains(":") == false) {
								query.add(val.toString());
								ids = reverseMap.get(val.toString());

								if (ids == null)
									ids = new HashSet<String>();

								ids.add(n.getIdentifier());
								reverseMap.put(val.toString(), ids);
							}
						}
					}
				}
			}
		}

		System.out.println("Number of EntrezGene ID to be sent: "
				+ query.size());

		int group = 0;
		String[] box = new String[buketNum];

		for (String q : query) {
			box[group] = q;
			group++;

			if (group == buketNum) {
				e.submit(new ImportTask(new ImportAnnotationTask(box)));
				group = 0;
				box = new String[buketNum];
			}
		}

		String[] newbox = new String[group];

		for (int i = 0; i < group; i++) {
			newbox[i] = box[i];
		}

		e.submit(new ImportTask(new ImportAnnotationTask(newbox)));

		try {
			e.shutdown();
			e.awaitTermination(Integer.parseInt(props.getValue("timeout")),
					TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double msec = (endTime - startTime) / (1000.0);
			System.out.println("NCBI Import finished in " + msec + " sec.");

			if ((canceled != null) && canceled) {
				canceled = null;

				return;
			}

			// Copy to CyAttributes.
			final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

			Object attrVal;

			if (keyAttrName.equals("ID")) {
				for (String[] key : attrMap.keySet()) {
					attrVal = attrMap.get(key);

					if (attrVal instanceof List) {
						nodeAttr.setListAttribute(key[0], key[1],
								(List) attrVal);
					} else {
						nodeAttr.setAttribute(key[0], key[1], attrVal
								.toString());
					}
				}
			} else {
				System.out.println("This is not ID.  ATTR conv.");

				// Need to convert attr to node ID
				for (String[] key : attrMap.keySet()) {
					attrVal = attrMap.get(key);

					Set<String> nodeIDs = reverseMap.get(key[0]);

					for (String id : nodeIDs) {
						if (attrVal instanceof List) {
							nodeAttr.setListAttribute(id, key[1],
									(List) attrVal);
						} else {
							nodeAttr.setAttribute(id, key[1], attrVal
									.toString());
						}
					}
				}
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null,
					null);
		} catch (InterruptedException e1) {
			System.out.println("TIMEOUT!");
			throw new CyWebServiceException(
					CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		}
	}

	public CyNetwork importNetwork(ESearchResult result)
			throws CyWebServiceException {
		return importNetwork(result, null);
	}

	private void setPerformanceParameters() {
		final String threadPool = props.getValue("thread_pool_size");
		threadNum = DEF_POOL_SIZE;

		try {
			threadNum = Integer.parseInt(threadPool);
		} catch (NumberFormatException e) {
			threadNum = DEF_POOL_SIZE;
		}

		final String buket = props.getValue("buket_size");

		try {
			buketNum = Integer.parseInt(buket);
		} catch (NumberFormatException e) {
			buketNum = DEF_BUCKET_SIZE;
		}
	}

	private CyNetwork importNetwork(Object searchResult, CyNetwork net)
			throws CyWebServiceException {
		ESearchResult res = (ESearchResult) searchResult;
		IdListType ids = res.getIdList();

		nodeList = new CopyOnWriteArrayList<Node>();
		edgeList = new CopyOnWriteArrayList<Edge>();

		nodeAltName = new ConcurrentHashMap<String, String>();
		nodeTypes = new ConcurrentHashMap<String, String>();

		System.gc();

		long startTime = System.currentTimeMillis();
		setPerformanceParameters();

		executer = Executors.newFixedThreadPool(threadNum);

		System.out.println("Thread Pool Initialized.");

		int group = 0;
		String[] box = new String[buketNum];

		for (String entrezID : ids.getId()) {
			box[group] = entrezID;
			group++;

			if (group == buketNum) {
				executer.submit(new ImportTask(new ImportNetworkTask(box)));
				group = 0;
				box = new String[buketNum];
			}
		}

		String[] newbox = new String[group];

		for (int i = 0; i < group; i++)
			newbox[i] = box[i];

		executer.submit(new ImportTask(new ImportNetworkTask(newbox)));

		try {
			executer.shutdown();
			executer.awaitTermination(Integer.parseInt(props
					.getValue("timeout")), TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("Finished in " + sec + " sec.");

			if ((canceled != null) && canceled) {
				canceled = null;

				return null;
			}

			// Set attributes
			CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
			CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

			Object attrVal;
			for (String key : nodeAltName.keySet()) {
				nodeAttr.setAttribute(key, "Alt Name", nodeAltName
						.get(key));
				nodeAttr.setAttribute(key, "Interactor Type", nodeTypes
						.get(key));
			}

			for (String[] key : attrMap.keySet()) {
				attrVal = attrMap.get(key);

				if (attrVal instanceof List) {
					edgeAttr.setListAttribute(key[0], key[1], (List) attrVal);
				} else {
					edgeAttr.setAttribute(key[0], key[1], attrVal.toString());
				}
			}

			if (net == null) {
				
				net = Cytoscape.createNetwork(nodeList, edgeList, "NCBI-Net",
						null, true);
				Cytoscape.getVisualMappingManager().setVisualStyle(getDefaultVisualStyle());
				
				Cytoscape.firePropertyChange(
						WSResponseType.DATA_IMPORT_FINISHED.toString(), null,
						net);
			} else {
				for (Node node : nodeList) {
					net.addNode(node);
				}

				for (Edge edge : edgeList) {
					net.addEdge(edge);
				}

				net.setSelectedNodeState(nodeList, true);

				final PropertyChangeEvent pce = new PropertyChangeEvent(this,
						Cytoscape.NETWORK_MODIFIED, null, null);
				Cytoscape.getPropertyChangeSupport().firePropertyChange(pce);
			}

		} catch (InterruptedException e1) {
			System.out.println("TIMEOUT");
			throw new CyWebServiceException(
					CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		} finally {
			nodeList.clear();
			edgeList.clear();
			nodeAltName.clear();
			nodeTypes.clear();

			nodeList = null;
			edgeList = null;
			nodeAltName = null;
			nodeTypes = null;

			System.gc();
		}

		return net;
	}

	class ImportTask extends FutureTask implements CyWebServiceEventListener {
		public ImportTask(Callable task) {
			super(task);
			WebServiceClientManager.getCyWebServiceEventSupport()
					.addCyWebServiceEventListener(this);
		}

		public void executeService(CyWebServiceEvent event)
				throws CyWebServiceException {
			if (event.getEventType().equals(WSEventType.CANCEL)) {
				cancel(true);

				if (canceled == null)
					canceled = true;
			}
		}
	}

	/**
	 * Thereads which will be executed
	 * 
	 * @author kono
	 * 
	 */
	class ImportNetworkTask implements Callable {
		private String[] entrezID;

		public ImportNetworkTask(String[] id) {
			this.entrezID = id;
		}

		public void run() {
			final EFetchRequest parameters = new EFetchRequest();
			final EFetchGeneService geneService = new EFetchGeneService();
			final gov.nih.ncbi.soap.eutils.gene.EUtilsServiceSoap geneServiceStub = geneService
					.getEUtilsServiceSoap();

			final StringBuilder builder = new StringBuilder();

			for (String id : entrezID)
				builder.append(id + ",");

			String query = builder.toString();
			System.out.println("Current Query ====> " + query);
			parameters.setId(query.substring(0, query.length() - 1));

			EFetchResult res = null;

			int retryCounter = 0;

			while (res == null) {
				try {
					res = geneServiceStub.runEFetch(parameters);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					res = null;

					try {
						System.out.println("Could not fetch data from NCBI: "
								+ query.substring(0, query.length()));
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e2) {
						System.out.println("Interrupted: "
								+ query.substring(0, query.length() - 1));
					}
				}

				if ((res == null) && (retryCounter < 3))
					System.out.println("Retry: "
							+ query.substring(0, query.length() - 1));
				else if ((res == null) && (retryCounter >= 3))
					return;

				retryCounter++;
			}

			// Create network from Interactions section
			final int entryLen = res.getEntrezgeneSet().getEntrezgene().size();
			Node centerNode = null;
			String interactionType = null;

			// String sourceDB = null;
			String otherGeneName = null;
			String nodeid = null;
			List<GeneCommentary> gc = null;
			List<GeneCommentary> interactions = null;

			Node n1;

			// Edge e1;
			String edgeID;

			String nodeType = null;
			String altName = null;

			for (int i = 0; i < entryLen; i++) {
				if ((canceled != null) && canceled)
					return;

				// Get commentary section.
				gc = res.getEntrezgeneSet().getEntrezgene().get(i)
						.getEntrezgeneComments().getGeneCommentary();

				for (GeneCommentary g : gc) {
					if ((g.getGeneCommentaryHeading() != null)
							&& g.getGeneCommentaryHeading().equals(
									"Interactions")) {
						// Interaction section found.
						try {
							centerNode = Cytoscape.getCyNode(res
									.getEntrezgeneSet().getEntrezgene().get(i)
									.getEntrezgeneTrackInfo().getGeneTrack()
									.getGeneTrackGeneid().toString(), true);
							System.out.println("Got Interactions for: "
									+ centerNode.getIdentifier());
						} catch (Exception e) {
							System.out.println("NPE!!!!!!!!!!!!!!!!");
						}

						nodeList.add(centerNode);

						// Parse individual interactions.
						interactions = g.getGeneCommentaryComment()
								.getGeneCommentary();

						for (GeneCommentary itr : interactions) {
							interactionType = itr.getGeneCommentaryText();

							if (interactionType == null) {
								interactionType = DEF_ITR_TYPE;
							}

							if (itr.getGeneCommentaryComment()
									.getGeneCommentary().size() > 1) {
								// Find node ID. If available, use Entrez Gene
								// ID.
								// If not, use the database-specific ID instead.
								try {
									nodeid = itr.getGeneCommentaryComment()
											.getGeneCommentary().get(1)
											.getGeneCommentarySource()
											.getOtherSource().get(0)
											.getOtherSourceSrc().getDbtag()
											.getDbtagTag().getObjectId()
											.getObjectIdId().toString();
								} catch (NullPointerException npe) {
									// This gene is not in NCBI DB.
									// Use original database ID
									continue;
								}

								// Check source Type

								nodeType = itr.getGeneCommentaryComment()
										.getGeneCommentary().get(1)
										.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceSrc().getDbtag()
										.getDbtagDb();

//								System.out.println("DB Tag for nodeID: "
//										+ nodeid + " = " + nodeType);

								// In case ID is not GeneID, put tag
								if (nodeType.equals(GENE_ID_TAG) == false) {
									nodeid = nodeType + ":" + nodeid;
								}

								n1 = Cytoscape.getCyNode(nodeid, true);
								nodeList.add(n1);

								// Add node attributes
								nodeTypes.put(nodeid, nodeType);
								
								altName = itr.getGeneCommentaryComment()
										.getGeneCommentary().get(1)
										.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceAnchor();
								if (altName != null && altName.length() != 0)
									nodeAltName.put(nodeid, altName);

								final String dataSource = itr
										.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceSrc().getDbtag()
										.getDbtagDb();

								List<Edge> eList2 = new ArrayList<Edge>();

								if (dataSource.equals("BioGRID")) {
									final String[] expTypes = interactionType
											.split(";");

									String etString = null;
									Edge newEdge = null;

									for (String eType : expTypes) {
										etString = eType.trim();

										eList2.add(newEdge = Cytoscape
												.getCyEdge(centerNode, n1,
														"interaction",
														etString, true));

										if (dataSource.equals("BioGRID")) {
											attrMap
													.put(
															new String[] {
																	newEdge
																			.getIdentifier(),
																	"interaction type" },
															BioGRIDUtil
																	.getInteractionType(etString));
										}
									}
								} else {
									eList2.add(Cytoscape.getCyEdge(centerNode,
											n1, "interaction", interactionType,
											true));
								}

								// e1 = Cytoscape.getCyEdge(centerNode, n1,
								// "interaction", interactionType, true);
								for (Edge e1 : eList2) {
									edgeList.add(e1);
									edgeID = e1.getIdentifier();

									// Add edge attributes
									attrMap.put(new String[] { edgeID,
											"datasource" }, dataSource);

									List<Pub> pubmed = itr
											.getGeneCommentaryRefs().getPub();

									if ((pubmed != null) && (pubmed.size() > 0)) {
										String[] pmid = new String[] { edgeID,
												"PubMed ID" };
										List<String> pmids = new ArrayList<String>();

										for (Pub pub : pubmed) {
											pmids.add(pub.getPubPmid()
													.getPubMedId().toString());
										}

										attrMap.put(pmid, pmids);
									} // /
								}
							}
						}

						break;
					}
				}
			}
		}

		public Object call() throws Exception {
			run();

			return null;
		}
	}

	// Task for importing annotations
	//
	class SearchDatabaseTask implements Callable<ESearchResult> {
		private final ESearchRequest query;

		public SearchDatabaseTask(final ESearchRequest query) {
			this.query = query;
		}

		public ESearchResult call() throws Exception {
			ESearchResult result = null;
			result = clientStub.runESearch(query);
			return result;
		}
	}

	// Task for importing annotations
	//
	class ImportAnnotationTask implements Callable {
		private String[] ids;

		public ImportAnnotationTask(String[] ids) {
			this.ids = ids;
		}

		private void parseAnnotation(EFetchResult res) {
		}

		public Object call() {
			StringBuilder builder = new StringBuilder();
			final EFetchRequest parameters = new EFetchRequest();
			final EFetchGeneService geneService = new EFetchGeneService();
			final gov.nih.ncbi.soap.eutils.gene.EUtilsServiceSoap geneServiceStub = geneService
					.getEUtilsServiceSoap();

			int numIDs = 0;

			for (String id : ids) {

				try {
					// Entrez ID should be an integer
					Integer.parseInt(id);
					builder.append(id + ",");
					numIDs++;
				} catch (NumberFormatException ne) {
					continue;
				}
			}

			String query = builder.toString();
			query = query.substring(0, query.length() - 1);
			System.out.println("Current Query String ====> " + query);
			parameters.setId(query);

			EFetchResult res = null;
			int retry = 0;

			while (res == null) {
				try {
					res = geneServiceStub.runEFetch(parameters);

				} catch (Exception e) {
					e.printStackTrace();

					res = null;

					try {
						retry++;
						System.out.println("Data fetching failed for: "
								+ query.substring(0, query.length()));
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						System.out.println("Time out!");
					}
				}

				if ((res == null) && (retry < 3)) {
					System.out.println("Retry: " + query);
				} else if (retry >= 3) {
					return null;
				}
			}

			final List<Entrezgene> entries = res.getEntrezgeneSet()
					.getEntrezgene();

			for (Entrezgene entry : entries) {
				if ((canceled != null) && canceled) {
					System.out.println("Operation canceled by user.");

					return null;
				}

				// Current target gene ID
				String entrezID = entry.getEntrezgeneTrackInfo().getGeneTrack()
						.getGeneTrackGeneid().toString();

				System.out.println("Extracting annotation for: " + entrezID);

				try {
					// Extract summary
					if (selectedAnn.contains(AnnotationCategory.SUMMARY)) {
						// Species
						String species = entry.getEntrezgeneSource()
								.getBioSource().getBioSourceOrg().getOrgRef()
								.getOrgRefTaxname();
						String[] sp = new String[] { entrezID, "Speices" };
						attrMap.put(sp, species);

						// General info
						final GeneRef geneRef = entry.getEntrezgeneGene()
								.getGeneRef();

						if (geneRef != null) {
							// Extract Official Symbol
							final String officialSymbol = geneRef
									.getGeneRefLocus();

							if (officialSymbol != null) {
								String[] os = new String[] { entrezID,
										"Official Symbol" };
								attrMap.put(os, officialSymbol);
							}

							// Extract Locus Tag
							final String locusTag = geneRef
									.getGeneRefLocusTag();

							if (locusTag != null) {
								String[] lt = new String[] { entrezID,
										"Locus Tag" };
								attrMap.put(lt, locusTag);
							}

							// Extract Location
							final String mapLoc = geneRef.getGeneRefMaploc();

							if (mapLoc != null) {
								String[] ds = new String[] { entrezID,
										"Location" };
								attrMap.put(ds, mapLoc);
							}

							// Extract source DB and its ID
							final GeneRefDb db = geneRef.getGeneRefDb();

							if ((db != null) && (db.getDbtag() != null)
									&& (db.getDbtag().size() != 0)) {
								final List<String> dbNames = new ArrayList<String>();
								final List<String> dbID = new ArrayList<String>();

								String id;
								String idStr;

								for (Dbtag dbTag : db.getDbtag()) {
									dbNames.add(dbTag.getDbtagDb());

									id = dbTag.getDbtagTag().getObjectId()
											.getObjectIdStr();
									idStr = dbTag.getDbtagTag().getObjectId()
											.getObjectIdStr();

									String[] singleID = new String[] {
											entrezID,

											dbTag.getDbtagDb() + " ID" };

									if (id != null) {
										dbID.add(id);
										attrMap.put(singleID, id);
									} else if (idStr != null) {
										dbID.add(idStr);
										attrMap.put(singleID, idStr);
									}
								}

								String[] dbn = new String[] { entrezID,
										"Source Database" };
								attrMap.put(dbn, dbNames);

								String[] dbid = new String[] { entrezID,
										"Source Database ID" };
								attrMap.put(dbid, dbID);
							}

							final GeneRefSyn syn = geneRef.getGeneRefSyn();

							if ((syn != null) && (syn.getGeneRefSynE() != null)
									&& (syn.getGeneRefSynE().size() != 0)) {
								final List<String> synonyms = new ArrayList<String>();

								for (String synonym : syn.getGeneRefSynE())
									synonyms.add(synonym);

								String[] sy = new String[] { entrezID,
										"aliases" };
								attrMap.put(sy, synonyms);
							}

							// Extract (official full name)
							final String desc = geneRef.getGeneRefDesc();

							if (desc != null) {
								String[] ds = new String[] { entrezID,
										"Description" };
								attrMap.put(ds, desc);
							}

							final EntrezgeneType t = entry.getEntrezgeneType();

							if ((t != null) && (t.getValue() != null)) {
								String[] ds = new String[] { entrezID,
										"Gene Type" };
								attrMap.put(ds, t.getValue().toString());
							}

							// Extract summary
							final String summary = entry.getEntrezgeneSummary();

							if (summary != null) {
								String[] sm = new String[] { entrezID,
										"Summary" };
								attrMap.put(sm, summary);
							}
						}
					}

					if (selectedAnn.contains(AnnotationCategory.GENERAL)) {
						// Prot. info
						List<String> proteinInfo = new ArrayList<String>();

						if ((entry.getEntrezgeneProt() != null)
								&& (entry.getEntrezgeneProt().getProtRef() != null)
								&& (entry.getEntrezgeneProt().getProtRef()
										.getProtRefName() != null)) {
							List<String> pNames = entry.getEntrezgeneProt()
									.getProtRef().getProtRefName()
									.getProtRefNameE();

							for (String name : pNames)
								proteinInfo.add(name);

							String[] pn = new String[] { entrezID,
									"General protein information" };
							attrMap.put(pn, proteinInfo);
						}
					}

					// final GeneCommentaryType[] geneProps = entry
					// .getEntrezgene_properties().getGeneCommentary();
					if ((entry.getEntrezgeneProperties() != null)
							&& (entry.getEntrezgeneProperties()
									.getGeneCommentary() != null)) {
						final List<GeneCommentary> geneProps = entry
								.getEntrezgeneProperties().getGeneCommentary();

						for (GeneCommentary comment : geneProps) {
							if (selectedAnn.contains(AnnotationCategory.GO)
									&& (comment.getGeneCommentaryHeading() != null)
									&& comment.getGeneCommentaryHeading()
											.equals("GeneOntology")) {
								// Extract GO section
								final List<GeneCommentary> go = comment
										.getGeneCommentaryComment()
										.getGeneCommentary();

								for (GeneCommentary goCategory : go) {
									List<GeneCommentary> goTermsObject = goCategory
											.getGeneCommentaryComment()
											.getGeneCommentary();

									List<String> goTerms = new ArrayList<String>();
									List<String> evidenceCodes = new ArrayList<String>();
									List<String> goTermIDs = new ArrayList<String>();

									List<String> goTermsProcess = new ArrayList<String>();
									List<String> evidenceCodesProcess = new ArrayList<String>();
									List<String> goTermIDsProcess = new ArrayList<String>();

									List<String> goTermsComponent = new ArrayList<String>();
									List<String> evidenceCodesComponent = new ArrayList<String>();
									List<String> goTermIDsComponent = new ArrayList<String>();

									for (GeneCommentary gt : goTermsObject) {
										List<OtherSource> oneTerm = gt
												.getGeneCommentarySource()
												.getOtherSource();

										if ((oneTerm != null)
												&& (oneTerm.size() != 0)) {
											String goTerm = oneTerm.get(0)
													.getOtherSourceAnchor();
											String goTermID = oneTerm.get(0)
													.getOtherSourceSrc()
													.getDbtag().getDbtagTag()
													.getObjectId()
													.getObjectIdId().toString();
											String evidence = oneTerm.get(0)
													.getOtherSourcePostText();

											if (goCategory
													.getGeneCommentaryLabel()
													.equals("Function") && goTermIDs.contains(goTermID) == false) {
												goTerms.add(goTerm);
												goTermIDs.add(goTermID);
												evidenceCodes.add(evidence
														.split(": ")[1]);
											} else if (goCategory
													.getGeneCommentaryLabel()
													.equals("Process") && goTermIDsProcess.contains(goTermID) == false) {
												goTermsProcess.add(goTerm);
												goTermIDsProcess.add(goTermID);
												evidenceCodesProcess
														.add(evidence
																.split(": ")[1]);
											} else if (goCategory
													.getGeneCommentaryLabel()
													.equals("Component") && goTermIDsComponent.contains(goTermID) == false) {
												goTermsComponent.add(goTerm);
												goTermIDsComponent
														.add(goTermID);
												evidenceCodesComponent
														.add(evidence
																.split(": ")[1]);
											}
										}
									}

									String[] got = new String[] { entrezID,
											"GO Term: Molecular Function" };
									attrMap.put(got, goTerms);

									String[] gotid = new String[] { entrezID,
											"GO ID: Molecular Function" };
									attrMap.put(gotid, goTermIDs);

									String[] ev = new String[] { entrezID,
											"GO Evidence Code: Molecular Function" };
									attrMap.put(ev, evidenceCodes);

									got = new String[] { entrezID,
											"GO Term: Biological Process" };
									attrMap.put(got, goTermsProcess);
									gotid = new String[] { entrezID,
											"GO ID: Biological Process" };
									attrMap.put(gotid, goTermIDsProcess);
									ev = new String[] { entrezID,
											"GO Evidence Code: Biological Process" };
									attrMap.put(ev, evidenceCodesProcess);

									got = new String[] { entrezID,
											"GO Term: Cellular Component" };
									attrMap.put(got, goTermsComponent);
									gotid = new String[] { entrezID,
											"GO ID: Cellular Component" };
									attrMap.put(gotid, goTermIDsComponent);
									ev = new String[] { entrezID,
											"GO Evidence Code: Cellular Component" };
									attrMap.put(ev, evidenceCodesComponent);
								}
							}
						}
					}

					List<GeneCommentary> commentary = entry
							.getEntrezgeneComments().getGeneCommentary();

					final List<String> geneRIFs = new ArrayList<String>();
					final List<String> geneRIFText = new ArrayList<String>();

					for (GeneCommentary comment : commentary) {
						// if (selectedAnn.contains(AnnotationCategory.MARKERS)
						// && (comment.getGeneCommentary_heading() != null)
						// &&
						// comment.getGeneCommentary_heading().startsWith("Markers"))
						// {
						// // Extract Marker section
						//						
						if (selectedAnn.contains(AnnotationCategory.PATHWAY)
								&& (comment.getGeneCommentaryHeading() != null)
								&& comment.getGeneCommentaryHeading().equals(
										"Pathways")) {
							final List<GeneCommentary> pathways = comment
									.getGeneCommentaryComment()
									.getGeneCommentary();

							List<String> pathwayNames = new ArrayList<String>();
							List<String> pathwayLinks = new ArrayList<String>();

							for (GeneCommentary p : pathways) {
								String pName = p.getGeneCommentaryText();
								String pLink = p.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceUrl();
								pathwayNames.add(pName);
								pathwayLinks.add(pLink);
							}

							String[] pwn = new String[] { entrezID, "Pathway" };
							attrMap.put(pwn, pathwayNames);

							String[] pwl = new String[] { entrezID,
									"Pathway Link" };
							attrMap.put(pwl, pathwayLinks);
						} else if (selectedAnn
								.contains(AnnotationCategory.PHENOTYPE)
								&& (comment.getGeneCommentaryHeading() != null)
								&& comment.getGeneCommentaryHeading().equals(
										"Phenotypes")) {
							final List<GeneCommentary> phenotypes = comment
									.getGeneCommentaryComment()
									.getGeneCommentary();

							List<String> phenotypeNames = new ArrayList<String>();
							List<String> phenotypeIDs = new ArrayList<String>();

							for (GeneCommentary p : phenotypes) {
								String pName = p.getGeneCommentaryText();
								String pID = p.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceAnchor();
								phenotypeNames.add(pName);
								phenotypeIDs.add(pID);
							}

							String[] pwn = new String[] { entrezID,
									"Phenotypes" };
							attrMap.put(pwn, phenotypeNames);

							String[] pwl = new String[] { entrezID,
									"Phenotype ID" };
							attrMap.put(pwl, phenotypeIDs);
						} else if (selectedAnn
								.contains(AnnotationCategory.LINK)
								&& (comment.getGeneCommentaryHeading() != null)
								&& comment.getGeneCommentaryHeading().equals(
										"Additional Links")) {
							final List<GeneCommentary> links = comment
									.getGeneCommentaryComment()
									.getGeneCommentary();

							List<String> sourceName = new ArrayList<String>();
							List<String> externalLink = new ArrayList<String>();

							for (GeneCommentary p : links) {
								String link = p.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceUrl();
								String name = p.getGeneCommentarySource()
										.getOtherSource().get(0)
										.getOtherSourceAnchor();

								if (link != null) {
									externalLink.add(link);
								}

								if (name != null) {
									sourceName.add(name);
								}
							}

							String[] pwn = new String[] { entrezID,
									"Additional Links Name" };
							attrMap.put(pwn, sourceName);

							String[] pwl = new String[] { entrezID,
									"Additional Links" };
							attrMap.put(pwl, externalLink);
						} else if (selectedAnn
								.contains(AnnotationCategory.PUBLICATION)
								&& (comment.getGeneCommentaryRefs() != null)
								&& (comment.getGeneCommentaryType() != null)
								&& (comment.getGeneCommentaryRefs() != null)) {
							final String comType = comment
									.getGeneCommentaryType().getValue()
									.toString();
							List<String> pubMedIDs = new ArrayList<String>();

							if (comType.equals("generif")) {
								geneRIFText
										.add(comment.getGeneCommentaryText());
							}

							final List<Pub> refs = comment
									.getGeneCommentaryRefs().getPub();
							String pmid = null;

							for (Pub ref : refs) {
								pmid = ref.getPubPmid().getPubMedId()
										.toString();

								if ((pmid != null) && comType.equals("comment"))
									pubMedIDs.add(pmid);
								else
									geneRIFs.add(pmid);
							}

							// Extract PubMed ID and GeneRif
							if (comType.equals("comment")) {
								String[] pmids = new String[] { entrezID,
										"PubMed ID" };
								attrMap.put(pmids, pubMedIDs);
							}
						}
					}

					// Add GeneRIF
					String[] grid = new String[] { entrezID, "GeneRIF ID" };
					attrMap.put(grid, geneRIFs);

					String[] grtx = new String[] { entrezID, "GeneRIF" };
					attrMap.put(grtx, geneRIFText);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nv
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<JMenuItem> getNodeContextMenuItems(NodeView nv) {
		List<JMenuItem> menuList = new ArrayList<JMenuItem>();
		menuList.add(NetworkExpansionMenu.getExpander(this));

		return menuList;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param type
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Icon getIcon(IconSize type) {
		return ABOUT_ICON;
	}

	/**
	 * Returns default visual style for networks build from this database
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualStyle getDefaultVisualStyle() {
		return VisualStyleBuilder.getNewVisualStyle();
	}
}
