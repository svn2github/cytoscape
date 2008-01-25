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
import java.util.Date;
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
	// Annotation categories available in NCBI
	public enum AnnotationCategory {
		SUMMARY("Summary"),
		PUBLICATION("Publications"),
		PHENOTYPE("Phenotypes"),
		PATHWAY("Pathways"),
		GENERAL("General Protein Information"),
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
	private int threadNum;
	private int buketNum;
	private static final int DEF_BUCKET_SIZE = 5;
	private static final int DEF_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 5;

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
		                      Tunable.INTEGER, new Integer(10000)));
		props.add(new Tunable("thread_pool_size", "Thread pool size", Tunable.INTEGER,
		                      new Integer(DEF_POOL_SIZE)));
		props.add(new Tunable("buket_size", "Number of IDs send at once", Tunable.INTEGER,
		                      new Integer(DEF_BUCKET_SIZE)));
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

		long startTime = System.currentTimeMillis();
		setPerformanceParameters();

		ExecutorService e = Executors.newFixedThreadPool(10);

		List<Node> nodes = Cytoscape.getRootGraph().nodesList();

		int group = 0;
		String[] box = new String[10];

		for (Node node : nodes) {
			box[group] = node.getIdentifier();
			group++;

			if (group == 10) {
				e.submit(new ImportAnnotationTask(box));
				group = 0;
				box = new String[10];
			}
		}

		String[] newbox = new String[group];

		for (int i = 0; i < group; i++) {
			newbox[i] = box[i];
		}

		e.submit(new ImportAnnotationTask(newbox));

		try {
			e.shutdown();
			e.awaitTermination(6000, TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double msec = (endTime - startTime) / (1000.0);
			System.out.println("NCBI Import finished in " + msec +" msec.");

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
			System.out.println("TIMEOUT!");
			e1.printStackTrace();
		}
	}

	public CyNetwork importNetwork(ESearchResult result) {
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
		buketNum = DEF_BUCKET_SIZE;

		try {
			buketNum = Integer.parseInt(threadPool);
		} catch (NumberFormatException e) {
			buketNum = DEF_BUCKET_SIZE;
		}
	}

	private CyNetwork importNetwork(Object searchResult, CyNetwork net) {
		ESearchResult res = (ESearchResult) searchResult;
		IdListType ids = res.getIdList();

		nodeList = new CopyOnWriteArrayList<Node>();
		edgeList = new CopyOnWriteArrayList<Edge>();

		long startTime = System.currentTimeMillis();
		setPerformanceParameters();

		ExecutorService e = Executors.newFixedThreadPool(threadNum);

		System.out.println("Thread Pool Initialized.");

		int group = 0;
		String[] box = new String[3];

		for (String entrezID : ids.getId()) {
			box[group] = entrezID;
			group++;

			if (group == 3) {
				e.submit(new ImportNetworkTask(box));
				group = 0;
				box = new String[3];
			}
		}

		String[] newbox = new String[group];

		for (int i = 0; i < group; i++) {
			newbox[i] = box[i];
		}

		e.submit(new ImportNetworkTask(newbox));

		try {
			e.shutdown();
			e.awaitTermination(6000, TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("FINISHED!!!!!!!!! = " + sec + " sec.");

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
				net = Cytoscape.createNetwork(nodeList, edgeList, "NCBI-Net", null);
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
			e = null;
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			System.out.println("TIMEOUT");
			e1.printStackTrace();
		}

		return net;
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
		private String[] entrezID;

		public ImportNetworkTask(String[] id) {
			this.entrezID = id;
		}

		private void parseAnnotation(EFetchResult res) {
		}

		public void run() {
			{
				final EFetchRequest parameters = new EFetchRequest();
				final StringBuilder builder = new StringBuilder();
				parameters.setDb("gene");

				for (String id : entrezID) {
					builder.append(id + ",");
				}

				String query = builder.toString();
				parameters.setId(query.substring(0, query.length() - 1));

				EFetchResult res = null;

				while (res == null) {
					try {
						res = ((EUtilsServiceSoap) stub).run_eFetch(parameters);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//						e.printStackTrace();
						res = null;

						try {
							System.out.println("!!!!!!!!!!!!!!!!! BGW error !!!!!!!!!!!!!!!! Sleep "
							                   + query.substring(0, query.length() - 1));
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e2) {
							// TODO Auto-generated catch block
							System.out.println("=========Time error !!!!!!!!!!!!!!!!");
						}
					}

					if (res == null) {
						System.out.println("=========Try again!!! "
						                   + query.substring(0, query.length() - 1));
					}
				}

				// Create network from Interactions section
				final int entryLen = res.getEntrezgeneSet().getEntrezgene().length;
				Node centerNode = null;
				String interactionType = null;

				//String sourceDB = null;
				String otherGeneName = null;
				String nodeid = null;
				GeneCommentaryType[] gc = null;
				GeneCommentaryType[] interactions = null;

				Node n1;
				Edge e1;

				String edgeID;

				for (int i = 0; i < entryLen; i++) {
					// Get commentary section.
					gc = res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_comments()
					                                              .getGeneCommentary();

					for (GeneCommentaryType g : gc) {
						if ((g.getGeneCommentary_heading() != null)
						    && g.getGeneCommentary_heading().equals("Interactions")) {
							// Interaction section found.
							try {
								centerNode = Cytoscape.getCyNode(res.getEntrezgeneSet()
								                                                                          .getEntrezgene()[i].getEntrezgene_trackInfo()
								                                                                          .getGeneTrack()
								                                                                          .getGeneTrack_geneid(),
								                                 true);
								System.out.println("#########Center = "
								                   + centerNode.getIdentifier());
							} catch (Exception e) {
								System.out.println("NPE!!!!!!!!!!!!!!!!");
							}

							nodeList.add(centerNode);

							// Parse individual interactions.
							interactions = g.getGeneCommentary_comment().getGeneCommentary();

							for (GeneCommentaryType itr : interactions) {
								interactionType = itr.getGeneCommentary_text();

								if (interactionType == null) {
									interactionType = DEF_ITR_TYPE;
								}

								if (itr.getGeneCommentary_comment().getGeneCommentary().length > 1) {
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

									n1 = Cytoscape.getCyNode(nodeid, true);
									nodeList.add(n1);

									e1 = Cytoscape.getCyEdge(centerNode, n1, "interaction",
									                         interactionType, true);
									edgeList.add(e1);
									edgeID = e1.getIdentifier();

									// Add edge attributes
									edgeAttrMap.put(new String[] { edgeID, "datasource" },
									                itr.getGeneCommentary_source().getOtherSource(0)
									                   .getOtherSource_src().getDbtag().getDbtag_db());

									PubType[] pubmed = itr.getGeneCommentary_refs().getPub();

									if ((pubmed != null) && (pubmed.length > 0)) {
										String[] pmid = new String[] { edgeID, "PubMed ID" };
										List<String> pmids = new ArrayList<String>();

										for (PubType pub : pubmed) {
											pmids.add(pub.getPub_pmid().getPubMedId());
										}

										edgeAttrMap.put(pmid, pmids);
									}
								}
							}

							break;
						}
					}
				}

				System.out.println(entrezID + ": Finished " + (new Date()).toString());
			}
		}
	}

	// Task for importing annotations
	//
	class ImportAnnotationTask implements Runnable {
		private String[] ids;

		public ImportAnnotationTask(String[] ids) {
			this.ids = ids;
		}

		private void parseAnnotation(EFetchResult res) {
		}

		public void run() {
			

			StringBuilder builder = new StringBuilder();
			final EFetchRequest parameters = new EFetchRequest();
			parameters.setDb("gene");

			for (String id : ids) {
				try {
					// Entrez ID should be an integer
					Integer.parseInt(id);
					builder.append(id + ",");
				} catch (NumberFormatException ne) {
					continue;
				}
				
			}

			String query = builder.toString();
			parameters.setId(query.substring(0, query.length() - 1));

			EFetchResult res = null;
			int retry = 0;
			while (res == null) {
				try {
					res = ((EUtilsServiceSoap) stub).run_eFetch(parameters);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//						e.printStackTrace();
					res = null;

					try {
						retry++;
						System.out.println("!!!!!!!!!!!!!!!!! BGW error !!!!!!!!!!!!!!!! Sleep "
						                   + query.substring(0, query.length() - 1));
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						System.out.println("=========Time error !!!!!!!!!!!!!!!!");
					}
				}

				if (res == null && retry<3) {
					System.out.println("=========Try again!!! "
					                   + query.substring(0, query.length() - 1));
				} else if(retry>3) {
					return;
				}
			}


			EntrezgeneType[] entries = res.getEntrezgeneSet().getEntrezgene();

			for (EntrezgeneType entry : entries) {
				String entrezID = entry.getEntrezgene_trackInfo().getGeneTrack()
				                       .getGeneTrack_geneid();
				
				System.out.println("Extracting annotation for: " + entrezID);

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
						} else if ((comment.getGeneCommentary_heading() != null)
						    && comment.getGeneCommentary_heading().equals("Phenotypes")) {
							final GeneCommentaryType[] phenotypes = comment.getGeneCommentary_comment()
							                                             .getGeneCommentary();

							List<String> phenotypeNames = new ArrayList<String>();
							List<String> phenotypeIDs = new ArrayList<String>();

							for (GeneCommentaryType p : phenotypes) {
								String pName = p.getGeneCommentary_text();
								String pID = p.getGeneCommentary_source().getOtherSource(0)
								                .getOtherSource_anchor();
								phenotypeNames.add(pName);
								phenotypeIDs.add(pID);
							}

							String[] pwn = new String[] { entrezID, "Phenotypes" };
							edgeAttrMap.put(pwn, phenotypeNames);

							String[] pwl = new String[] { entrezID, "Phenotype ID" };
							edgeAttrMap.put(pwl, phenotypeIDs);
						} else if ((comment.getGeneCommentary_heading() != null)
						    && comment.getGeneCommentary_heading().equals("Additional Links")) {
							final GeneCommentaryType[] links = comment.getGeneCommentary_comment()
							                                             .getGeneCommentary();

							List<String> sourceName = new ArrayList<String>();
							List<String> externalLink = new ArrayList<String>();

							for (GeneCommentaryType p : links) {
								String link = p.getGeneCommentary_source().getOtherSource(0)
				                .getOtherSource_url();
								String name = p.getGeneCommentary_source().getOtherSource(0)
								                .getOtherSource_anchor();
								if(link != null) {
									externalLink.add(link);
								}
								if(name != null) {
								sourceName.add(name);
								}
							}

							String[] pwn = new String[] { entrezID, "Additional Links Name" };
							edgeAttrMap.put(pwn, sourceName);

							String[] pwl = new String[] { entrezID, "Additional Links" };
							edgeAttrMap.put(pwl, externalLink);
						}
						
					}
				} 
			}
		}
	}
}
