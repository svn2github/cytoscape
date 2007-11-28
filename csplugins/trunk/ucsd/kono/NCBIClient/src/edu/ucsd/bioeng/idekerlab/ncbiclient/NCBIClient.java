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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;

import cytoscape.layout.Tunable;

import cytoscape.util.ModulePropertiesImpl;

import cytoscape.visual.VisualStyle;

import giny.model.Edge;
import giny.model.Node;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EntrezgeneType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneRefType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.PubType;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.ESearchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.ESearchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.IdListType;

import java.beans.PropertyChangeEvent;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class NCBIClient extends WebServiceClientImpl implements NetworkImportWebServiceClient {
	public enum AnnotationCategory {
		SUMMARY("Summary"),
		REFERENCE("References"),
		PHENOTYPE("Phenotypes"),
		PATHWAY("Pathways"),
		GENERAL("General"),
		LINK("Additional Links");

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

	// Display name for this Client
	private static final String DISPLAY_NAME = "NCBI Entrez EUtilities Web Service Client";
	
	// Client's ID
	private static final String CLIENT_ID = "ncbi_entrez";
	
	private static NCBIClient client;
	private static final String DEF_ITR_TYPE = "pp";
	private CopyOnWriteArrayList<Node> nodeList;
	private CopyOnWriteArrayList<Edge> edgeList;
	private Map<String[], Object> nodeAttrMap = new ConcurrentHashMap<String[], Object>();
	private Map<String[], Object> edgeAttrMap = new ConcurrentHashMap<String[], Object>();
	private List<AnnotationCategory> selectedAnn = new ArrayList<AnnotationCategory>();

	static {
		try {
			client = new NCBIClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Creates a new NCBIClient object.
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public NCBIClient() throws Exception {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.NETWORK, ClientType.ATTRIBUTE });

		EUtilsServiceLocator service = new EUtilsServiceLocator();
		stub = service.geteUtilsServiceSoap();

		props = new ModulePropertiesImpl(clientID, "wsc");
		props.add(new Tunable("max_search_result", "Maximum number of search result",
		                      Tunable.INTEGER, new Integer(50)));
		props.add(new Tunable("with_anno", "Import selected annotations", Tunable.BOOLEAN,
		                      new Boolean(false)));
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
			} else if (e.getEventType().equals(WSEventType.IMPORT_ATTRIBUTE)) {
				importAnnotations(e.getParameter());
			}
		}
	}

	private void search(String keyword, CyWebServiceEvent e) {
		EUtilsServiceSoap ncbiStub = (EUtilsServiceSoap) stub;
		System.out.println("=========CLASS = " + ncbiStub.getClass());

		ESearchRequest searchParam = new ESearchRequest();
		searchParam.setDb("gene");
		searchParam.setRetMax(props.getValue("max_search_result"));
		keyword = keyword.replace(" ", "+");
		searchParam.setTerm(keyword);

		try {
			ESearchResult result = ncbiStub.run_eSearch(searchParam);
			int resSize = Integer.parseInt(result.getCount());
			System.out.println("=========result size = " + resSize);

			if (e.getNextMove() != null) {
				Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
				                             new DatabaseSearchResult(resSize, result,
				                                                      e.getNextMove()));
			} else {
				Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
				                             new DatabaseSearchResult(Integer.getInteger(result
				                                                                                                                                                                                               .getCount()),
				                                                      result,
				                                                      WSEventType.IMPORT_NETWORK));
			}
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void importAnnotations(Object parameter) {
		if (parameter instanceof AttributeImportQuery == false) {
			return;
		}

		final Object[] selectedAttr = (Object[]) ((AttributeImportQuery) parameter).getParameter();

		AnnotationCategory ann;
		selectedAnn = new ArrayList<AnnotationCategory>();

		for (Object name : selectedAttr) {
			System.out.println("---> Selected: " + name);
			ann = AnnotationCategory.getValue(name.toString());

			if (ann != null) {
				selectedAnn.add(ann);
			}
		}

		final String keyAttrName = ((AttributeImportQuery) parameter).getKeyCyAttrName();

		nodeAttrMap = new ConcurrentHashMap<String[], Object>();
		edgeAttrMap = new ConcurrentHashMap<String[], Object>();

		long startTime = System.nanoTime();

		ExecutorService e = Executors.newFixedThreadPool(20);
		System.out.println("Initial Thread Pool:");

		List<Node> nodes = Cytoscape.getRootGraph().nodesList();

		for (Node node : nodes) {
			System.out.println("##########EXECUTE2: " + node.getIdentifier());
			e.submit(new ImportAnnotationTask(node.getIdentifier()));
		}

		try {
			e.shutdown();
			e.awaitTermination(1500, TimeUnit.SECONDS);

			long endTime = System.nanoTime();
			double msec = (endTime - startTime) / (1000.0 * 1000.0);
			System.out.println("FINISHED!!!!!!!!! = " + msec);

			CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

			Object attrVal;

			for (String[] key : edgeAttrMap.keySet()) {
				attrVal = edgeAttrMap.get(key);

				if (attrVal instanceof List) {
					nodeAttr.setListAttribute(key[0], key[1], (List) attrVal);
				} else {
					nodeAttr.setAttribute(key[0], key[1], attrVal.toString());
				}
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			System.out.println("TIMEOUT 2=================================");
			e1.printStackTrace();
		}
	}

	private void importNetwork(Object searchResult, CyNetwork net) {
		ESearchResult res = (ESearchResult) searchResult;
		IdListType ids = res.getIdList();

		nodeList = new CopyOnWriteArrayList<Node>();
		edgeList = new CopyOnWriteArrayList<Edge>();

		long startTime = System.nanoTime();

		ExecutorService e = Executors.newFixedThreadPool(20);
		System.out.println("Initial Thread Pool:");

		for (String entrezID : ids.getId()) {
			System.out.println("##########EXECUTE: " + entrezID);
			e.submit(new ImportNetworkTask(entrezID));
		}

		try {
			e.shutdown();
			e.awaitTermination(1500, TimeUnit.SECONDS);

			long endTime = System.nanoTime();
			double msec = (endTime - startTime) / (1000.0 * 1000.0);
			System.out.println("FINISHED!!!!!!!!! = " + msec);

			// Set attributes
			CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

			Object attrVal;

			for (String[] key : edgeAttrMap.keySet()) {
				attrVal = edgeAttrMap.get(key);

				if (attrVal instanceof List) {
					edgeAttr.setListAttribute(key[0], key[1], (List) attrVal);
				} else {
					edgeAttr.setAttribute(key[0], key[1], attrVal.toString());
				}
			}

			if (net == null) {
				Cytoscape.createNetwork(nodeList, edgeList, "NCBI-Net", null);
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, null);
			} else {
				for (Node node : nodeList) {
					net.addNode(node);
				}

				for (Edge edge : edgeList) {
					net.addEdge(edge);
				}

				net.setSelectedNodeState(nodeList, true);

				final PropertyChangeEvent pce = new PropertyChangeEvent(this,
				                                                        Cytoscape.NETWORK_MODIFIED,
				                                                        null, null);
				Cytoscape.getPropertyChangeSupport().firePropertyChange(pce);
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			System.out.println("TIMEOUT=================================");
			e1.printStackTrace();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualStyle getDefaultVisualStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Thereads which will be executed
	 * @author kono
	 *
	 */
	class ImportNetworkTask implements Runnable {
		private String entrezID;

		public ImportNetworkTask(String id) {
			this.entrezID = id;
		}

		private void parseAnnotation(EFetchResult res) {
		}

		public void run() {
			System.out.println("RunnableTask starts.");

			try {
				EFetchRequest parameters = new EFetchRequest();
				parameters.setDb("gene");
				parameters.setId(entrezID);

				EFetchResult res = ((EUtilsServiceSoap) stub).run_eFetch(parameters);

				// Create network from Interactions section
				final int entryLen = res.getEntrezgeneSet().getEntrezgene().length;
				Node centerNode = null;
				String interactionType = null;
				String sourceDB = null;
				String otherGeneName = null;
				String nodeid = null;
				GeneCommentaryType[] gc = null;
				GeneCommentaryType[] interactions = null;

				String[] attrPair = new String[2];

				for (int i = 0; i < entryLen; i++) {
					//					System.out.println("summary: "
					//					                   + res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_gene()
					//					                                                              .getGeneRef()
					//					                                                              .getGeneRef_desc());
					centerNode = Cytoscape.getCyNode(entrezID, true);
					nodeList.add(centerNode);

					gc = res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_comments()
					                                              .getGeneCommentary();

					for (GeneCommentaryType g : gc) {
						if ((g.getGeneCommentary_heading() != null)
						    && g.getGeneCommentary_heading().equals("Interactions")) {
							// Interaction section found.
							// Parse individual interactions.
							interactions = g.getGeneCommentary_comment().getGeneCommentary();

							for (GeneCommentaryType itr : interactions) {
								interactionType = itr.getGeneCommentary_text();

								if (interactionType == null) {
									interactionType = DEF_ITR_TYPE;
								}

								if (itr.getGeneCommentary_comment().getGeneCommentary().length > 1) {
									String otherGene = itr.getGeneCommentary_comment()
									                      .getGeneCommentary(1)
									                      .getGeneCommentary_source()
									                      .getOtherSource(0).getOtherSource_anchor();

									// Find node ID.  If available, use Entrez Gene ID.
									// If not, use the database-specific ID instead.
									try {
										nodeid = itr.getGeneCommentary_comment().getGeneCommentary(1)
										            .getGeneCommentary_source().getOtherSource(0)
										            .getOtherSource_src().getDbtag().getDbtag_tag()
										            .getObjectId().getObjectId_id();
									} catch (NullPointerException npe) {
										// This gene is not in NCBI DB.
										// Use original database ID
										continue;
									}

									Node n1 = Cytoscape.getCyNode(nodeid, true);
									nodeList.add(n1);

									Edge e1 = Cytoscape.getCyEdge(centerNode, n1, "interaction",
									                              interactionType, true);
									edgeList.add(e1);

									// Add edge attributes
									String[] source = new String[] { e1.getIdentifier(), "datasource" };

									sourceDB = itr.getGeneCommentary_source().getOtherSource(0)
									              .getOtherSource_src().getDbtag().getDbtag_db();
									edgeAttrMap.put(source, sourceDB);

									PubType[] pubmed = itr.getGeneCommentary_refs().getPub();

									if ((pubmed != null) && (pubmed.length > 0)) {
										String[] pmid = new String[] { e1.getIdentifier(), "PubMed ID" };
										List<String> pmids = new ArrayList<String>();

										for (PubType pub : pubmed) {
											pmids.add(pub.getPub_pmid().getPubMedId());
										}

										edgeAttrMap.put(pmid, pmids);
									}
								}
							}
						}
					}

					//System.out.println("summary: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_properties().getGeneCommentary(0).getGeneCommentary_type());
					//		                System.out.println("Abstract: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_nonUniqueKeys().getDbtag(0));
				}
			} catch (Exception e) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Problem for: " + entrezID);
				e.printStackTrace();
			}
		}
	}

	// Task for importing annotations
	//
	class ImportAnnotationTask implements Runnable {
		private String entrezID;

		public ImportAnnotationTask(String id) {
			this.entrezID = id;
		}

		private void parseAnnotation(EFetchResult res) {
		}

		public void run() {
			System.out.println("Annotation task starts.");

			try {
				EFetchRequest parameters = new EFetchRequest();
				parameters.setDb("gene");
				parameters.setId(entrezID);

				EFetchResult res = ((EUtilsServiceSoap) stub).run_eFetch(parameters);

				if ((res == null) || (res.getEntrezgeneSet() == null)
				    || (res.getEntrezgeneSet().getEntrezgene(0) == null)
				    || (res.getEntrezgeneSet().getEntrezgene().length < 1)) {
					return;
				}

				EntrezgeneType entry = res.getEntrezgeneSet().getEntrezgene(0);

				// Species
				String species = entry.getEntrezgene_source().getBioSource().getBioSource_org()
				                      .getOrgRef().getOrgRef_taxname();
				String[] sp = new String[] { entrezID, "Speices" };
				edgeAttrMap.put(sp, species);

				// Prot. info
				List<String> proteinInfo = new ArrayList<String>();
				String[] pNames = entry.getEntrezgene_prot().getProtRef().getProtRef_name()
				                       .getProtRef_name_E();

				for (String name : pNames) {
					proteinInfo.add(name);
				}

				String[] pn = new String[] { entrezID, "General protein information" };
				edgeAttrMap.put(pn, proteinInfo);

				// General info
				GeneRefType geneRef = entry.getEntrezgene_gene().getGeneRef();

				try {
					String officialSymbol = geneRef.getGeneRef_locus();

					String[] os = new String[] { entrezID, "Official Symbol" };
					edgeAttrMap.put(os, officialSymbol);
				} catch (NullPointerException npe) {
				}

				try {
					String desc = geneRef.getGeneRef_desc();

					String[] ds = new String[] { entrezID, "Description" };
					edgeAttrMap.put(ds, desc);
				} catch (NullPointerException npe) {
				}

				try {
					String summary = entry.getEntrezgene_summary();

					String[] sm = new String[] { entrezID, "Summary" };
					edgeAttrMap.put(sm, summary);
				} catch (NullPointerException npe) {
				}

				// Pathway
				if (selectedAnn.contains(AnnotationCategory.PATHWAY)) {
					GeneCommentaryType[] commentary = entry.getEntrezgene_comments()
					                                       .getGeneCommentary();

					for (GeneCommentaryType comment : commentary) {
						if ((comment.getGeneCommentary_heading() != null)
						    && comment.getGeneCommentary_heading().equals("Pathways")) {
							final GeneCommentaryType[] pathways = comment.getGeneCommentary_comment()
							                                             .getGeneCommentary();

							List<String> pathwayNames = new ArrayList<String>();
							List<String> pathwayLinks = new ArrayList<String>();

							for (GeneCommentaryType p : pathways) {
								String pName = p.getGeneCommentary_text();
								String pLink = p.getGeneCommentary_source().getOtherSource(0)
								                .getOtherSource_url();
								pathwayNames.add(pName);
								pathwayLinks.add(pLink);
							}

							String[] pwn = new String[] { entrezID, "Pathway" };
							edgeAttrMap.put(pwn, pathwayNames);

							String[] pwl = new String[] { entrezID, "Pathway Link" };
							edgeAttrMap.put(pwl, pathwayLinks);
						}
					}
				}

				// Link

				//					for (GeneCommentaryType g : gc) {
				//						if ((g.getGeneCommentary_heading() != null)
				//						    && g.getGeneCommentary_heading().equals("Interactions")) {
				//							// Interaction section found.
				//							// Parse individual interactions.
				//							interactions = g.getGeneCommentary_comment().getGeneCommentary();
				//
				//					}

				//System.out.println("summary: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_properties().getGeneCommentary(0).getGeneCommentary_type());
				//		                System.out.println("Abstract: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_nonUniqueKeys().getDbtag(0));
			} catch (Exception e) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Problem for: " + entrezID);
				e.printStackTrace();
			}
		}
	}
}
