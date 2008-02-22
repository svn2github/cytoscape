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
package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.visual.VisualStyle;

import giny.model.Edge;
import giny.model.Node;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.DbtagType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EntrezgeneType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentary_sourceType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.OtherSourceType;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.OtherSource_srcType;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.ESearchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.ESearchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.IdListType;

import gov.nih.nlm.ncbi.www.soap.eutils.efetch.*;

import java.beans.PropertyChangeEvent;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class NCBI extends Thread{
	private static final String DISPLAY_NAME = "NCBI Entrez Utilities Web Service Client";
	private static final String CLIENT_ID = "ncbi_entrez";
	private static WebServiceClient client;
	private Map<String, List<String[]>> resMap = new ConcurrentHashMap<String, List<String[]>>();
	private static final int THREAD_POOL_SIZE = 50;
	// Newly Added {
	private static final String DEF_VS_NAME = "NCBI Entrez Style";
	private VisualStyle defaultVS = null;
	private String nodeId;
    private Node node;
    private javax.swing.JProgressBar jProgressBar2;
    
    public NCBI(String nodeID, Node node1, javax.swing.JProgressBar jBar){
    	nodeId = nodeID;
    	node=node1;
    	jProgressBar2 = jBar;
    }
	Object stub;

	// }
	
//	static {
//		try {
//			client = new NCBI();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	

	public static WebServiceClient getClient() {
		return client;
	}

	public NCBI() {
//		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.NETWORK });

//		URL url1 = new URL("gov.nih.nlm.ncbi.www.soap.eutils.efetch.EntrezgeneType");
		URL url1 = new URL("www.ncbi.nlm.nih.gov");
		
		try {
		EUtilsServiceLocator service3 = new EUtilsServiceLocator();
		
		System.out.println("THIS IS YOUR SERVICE3: " + service3);
		
//		System.out.println("THIS IS YOUR SERVICE SOAP ADDY" + service3.geteUtilsServiceSoapAddres());
		
//		EUtilsServiceSoap stub = service3.geteUtilsServiceSoap();
		stub = service3.geteUtilsServiceSoap();
		
		System.out.println("THIS IS YOUR geteUtils... : "  + service3.geteUtilsServiceSoap());
		jProgressBar2.setIndeterminate(false);
		
		} catch(Throwable t) {
			System.out.println("Oh Noes!");
		}
	}

	
	public void startSearch(String nodeId, Node node){
		
    	CyWebServiceEvent cyweb1 = new CyWebServiceEvent("ncbi_entrez", WSEventType.SEARCH_DATABASE, node);
    	ImportTask task1 = new ImportTask(nodeId);
		task1.run();
		System.out.println("trying to import network");
		importNetwork("675", null);
//		importNetwork("675", Cytoscape.getCurrentNetwork());
		System.out.println("the network has been imported");
		
		jProgressBar2.setIndeterminate(false);
		
    	
//		search(cyweb1.getParameter().toString(), cyweb1);
	}
	
//	@Override
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
				importNetwork(e.getParameter().toString(), null);
			} else if (e.getEventType().equals(WSEventType.EXPAND_NETWORK)) {
				System.out.println("CHECKPOINT 2 in executeService");
				importNetwork(e.getParameter().toString(), Cytoscape.getCurrentNetwork());
			} else if (e.getEventType().equals(WSEventType.SEARCH_DATABASE)) {
				System.out.println("CHECKPOINT 2 in executeService");
//				search(e.getParameter().toString(), e);

			}
		}
	}
	
	
	public void propertyChange(PropertyChangeEvent e) {
		System.out.println("++++++++++++++ Got event in client: " + e.getPropertyName());

		if (e.getPropertyName().equals("SEARCH_NETWORK") && e.getNewValue().equals(CLIENT_ID)) {
			System.out.println("This is for me! : " + CLIENT_ID);

			importNetwork(e.getOldValue().toString(), null);
		} else if (e.getPropertyName().equals("EXPAND_NETWORK")
		           && e.getNewValue().equals(CLIENT_ID)) {
			System.out.println("This is for me! : " + CLIENT_ID);

			importNetwork(e.getOldValue().toString(), Cytoscape.getCurrentNetwork());
		}
	}

//	private void importNetwork(String string, Object object) {
	private void importNetwork(String string, CyNetwork net) {
	ESearchRequest parameters1 = new ESearchRequest();
		
		// Set search parameters.
		parameters1.setDb("gene");
		parameters1.setRetMax("1000");

		String keyword = string.replace(" ", "+");

		parameters1.setTerm(keyword);

		ESearchResult res2 = null;
		EUtilsServiceSoap service = (EUtilsServiceSoap) stub;

		try {
			res2 = service.run_eSearch(parameters1);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Now fetch the genes
		long startTime = System.nanoTime();

		ExecutorService e = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		System.out.println("Initial Thread Pool:");

		IdListType idlist = res2.getIdList();

		for (int i = 0; i < idlist.getId().length; i++) {
			{
				String entrezID = idlist.getId(i);

				System.out.println("##########EXECUTE: " + entrezID);
				e.submit(new ImportTask(entrezID));
			}
		}

		try {
			e.shutdown();
			e.awaitTermination(1500, TimeUnit.SECONDS);

			long endTime = System.nanoTime();
			double msec = (endTime - startTime) / (1000.0 * 1000.0);
			System.out.println("FINISHED!!!!!!!!! = " + msec);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			System.out.println("TIMEOUT=================================");
			e1.printStackTrace();
		}

		Set<Node> nodes = new HashSet<Node>();
		Set<Edge> edges = new HashSet<Edge>();

		CyAttributes nAttr = Cytoscape.getNodeAttributes();
		CyAttributes eAttr = Cytoscape.getEdgeAttributes();
		
		
		for (String key : resMap.keySet()) {
			List<String[]> itrs = resMap.get(key);

			Node node1 = Cytoscape.getCyNode(key, true);
			
			for (String[] ent : itrs) {
				if (ent == null || ent.length != 5 || ent[3] == null) {
					continue;
				}
				if(ent[0] != null) {
					nAttr.setAttribute(node1.getIdentifier(), "Official Symbol", ent[0]);
				}
				
				Node node2 = Cytoscape.getCyNode(ent[3], true);
				if(ent[1] != null) {
					nAttr.setAttribute(node2.getIdentifier(), "Official Symbol", ent[1]);
				}
				String itrType = null;
				if(ent[4] != null) {
					itrType = ent[2] + ":" + ent[4];
				} else {
					itrType = ent[2] + ":pp";
				}
				
				Edge edge = Cytoscape.getCyEdge(node1, node2, "interaction", itrType, true);

				System.out.println("Entry: " + node1.getIdentifier() + " - "
				                   + node2.getIdentifier() + "===== " + node2.getClass());
				nodes.add(node1);
				nodes.add(node2);
				edges.add(edge);
			}
		}
		
		if (net == null) {
			Cytoscape.createNetwork(nodes, edges, "NCBI: ", null);
//			Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, null);
		} else {
			for (Node node : nodes) {
				net.addNode(node);
			}

			for (Edge edge : edges) {
				net.addEdge(edge);
			}

			net.setSelectedNodeState(nodes, true);
//			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
		}

		// Cytoscape.createNetwork(nodes, edges, "NCBI: " + string);
	}

	/**
	 * Task to execute the search and fetch.
	 *
	 * @author kono
	 *
	 */
	class ImportTask implements Runnable {
		private String entrezID;

		public ImportTask(String id) {
			this.entrezID = id;
		}

		public void run() {
			System.out.println("start run() in ImportTask.");
			
			
			try {
				EFetchRequest parameters = new EFetchRequest();
				
				System.out.println("This is parameters (new EFetchRequesT())" + parameters);
				
				parameters.setDb("gene");
//				parameters.setId(entrezID);
				parameters.setId("675");
			
				EUtilsServiceSoap service = (EUtilsServiceSoap) stub;
				
				System.out.println("This is service(EUtilsServiceSoap): " + service);
				
//				EFetchResult res = service.run_eFetch(parameters);	// THIS DOESN'T WORK :(
				
				EFetchResult res = service.run_eFetch(parameters);
				
				System.out.println("This is your result: " + res);

				// Create network from Interactions section
				EntrezgeneType curEntry = null;
				String commonName = null;
				GeneCommentaryType[] gc = null;
				GeneCommentaryType[] interactionTypes = null;
				final int entrySize = res.getEntrezgeneSet().getEntrezgene().length;

				for (int i = 0; i < entrySize; i++) {
					curEntry = res.getEntrezgeneSet().getEntrezgene()[i];

					// Extract common name (Official Symbol)
					commonName = curEntry.getEntrezgene_gene().getGeneRef().getGeneRef_locus();
					gc = curEntry.getEntrezgene_comments().getGeneCommentary();

					// Loop through the commentary, and find Interaction section.
					for (GeneCommentaryType g : gc) {
						if ((g.getGeneCommentary_heading() != null)
						    && g.getGeneCommentary_heading().equals("Interactions")) {
							// Interaction section found.
							interactionTypes = g.getGeneCommentary_comment().getGeneCommentary();

							List<String[]> otherGenes = new ArrayList<String[]>();

							for (GeneCommentaryType itr : interactionTypes) {
								// First, check the information from the source database.
								GeneCommentary_sourceType sourceDBInfo = itr.getGeneCommentary_source();
								String sourceDB = sourceDBInfo.getOtherSource(0)
								                              .getOtherSource_anchor();
								String sourceID = sourceDB + ":" + sourceDBInfo.getOtherSource(0).getOtherSource_src()
								                              .getDbtag().getDbtag_tag()
								                              .getObjectId().getObjectId_id();
								String interactionType = itr.getGeneCommentary_text();

								// Check GeneID first
								GeneCommentaryType[] itrData = itr.getGeneCommentary_comment()
								                                  .getGeneCommentary();
								String geneSymbol = null;

								for (GeneCommentaryType com : itrData) {
									GeneCommentary_sourceType other = com.getGeneCommentary_source();

									if ((other == null) || (other.getOtherSource(0) == null)
									    || (other.getOtherSource(0).getOtherSource_src() == null)) {
										continue;
									}

									OtherSource_srcType validOther = other.getOtherSource(0)
									                                      .getOtherSource_src();
									String id = validOther.getDbtag().getDbtag_tag().getObjectId()
									                      .getObjectId_id();
									String tagStr = validOther.getDbtag().getDbtag_tag()
									                          .getObjectId().getObjectId_str();
									System.out.println("Source DB: "
									                   + validOther.getDbtag().getDbtag_db() + ", "
									                   + entrezID + "<--- "
									                   + com.getGeneCommentary_source()
									                        .getOtherSource(0)
									                        .getOtherSource_anchor() + "--->" + id
									                   + ", " + tagStr);

									if (validOther.getDbtag().getDbtag_db().equals("GeneID")) {
										sourceID = id;
										geneSymbol = com.getGeneCommentary_source().getOtherSource(0)
					                    .getOtherSource_anchor();
										break;
									}

									
								}

								/*
								 * Data inside this array:
								 *  0: official symbol of source
								 *   1: official symbol of target
								 *   2: source database name
								 *   3: id of target
								 *   4: detection method
								 */
								String[] interaction = new String[5];
								interaction[0] = commonName;
								interaction[1] = geneSymbol;
								interaction[2] = sourceDB;
								interaction[3] = sourceID;
								interaction[4] = interactionType;

								otherGenes.add(interaction);

								// If not found, get ID from the source DB
							}

							resMap.put(entrezID, otherGenes);
							
							
							//						if ((g.getGeneCommentary_heading() != null)
							//						    && g.getGeneCommentary_heading().equals("Pathways")) {
							//							GeneCommentaryType[] gcc = g.getGeneCommentary_comment()
							//							                            .getGeneCommentary();
							//
							//							for (GeneCommentaryType ggg : gcc) {
							//								System.out.println(entrezID + " -----Pathway: "
							//								                   + ggg.getGeneCommentary_text());
							//
							//								String pwName = ggg.getGeneCommentary_text();
							//								resMap.put(entrezID, pwName);
							//
							//								//    							if (ggg.getGeneCommentary_comment().getGeneCommentary().length > 1) {
							//								//    								String nodeid = ggg.getGeneCommentary_comment()
							//								//    								                   .getGeneCommentary(1)
							//								//    								                   .getGeneCommentary_source()
							//								//    								                   .getOtherSource(0)
							//								//    								                   .getOtherSource_anchor();
							//								//    								System.out.println("---------Intr : " + nodeid);
							//								//
							//								//    							
							//								//    							}
							//							}
							//						}
						}
					}

				}
			} catch (Exception e) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Problem for: " + entrezID);
				e.printStackTrace();
			}
		}
	}
}
