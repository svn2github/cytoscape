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
package edu.ucsd.bioeng.idekerlab.ncbiclientui.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.util.swing.AttributeImportPanel;
import giny.model.Node;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;


/**
 * Simple attribute import GUI for Entrez Gene database
 * This UI depends on
 */
public class NCBIGenePanel extends AttributeImportPanel {
	protected static WebServiceClient ncbi = WebServiceClientManager.getClient("ncbi_entrez");
	private static final Icon LOGO = new ImageIcon(NCBIGenePanel.class.getResource("/images/entrez_page_title.gif"));

	
	private Map<String, String> resMap = new ConcurrentHashMap<String, String>();
	
	
	/**
	 * Creates a new NCBIGenePanel object.
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public NCBIGenePanel() throws IOException {
		this(LOGO, "Entrez Gene", "Available Attributes");
	}

	/**
	 * Creates a new NCBIGenePanel object.
	 *
	 * @param logo  DOCUMENT ME!
	 * @param title  DOCUMENT ME!
	 * @param attrPanelName  DOCUMENT ME!
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public NCBIGenePanel(Icon logo, String title, String attrPanelName) throws IOException {
		super(logo, title, attrPanelName);
		initDataSources();
	}

	private void initDataSources() {
		this.databaseComboBox.addItem("NCBI Entrez Gene");
		setDataType();
	}

	private void setDataType() {
		this.attributeTypeComboBox.addItem("Entrez Gene ID");
	}

	protected void importButtonActionPerformed(ActionEvent e) {
		System.out.println("======================PW Import =================");
		importPathway();
	}

	private void importPathway() {
		final List<Node> nodes = Cytoscape.getRootGraph().nodesList();
		String entrezID = null;

//		ExecutorService e = Executors.newCachedThreadPool();

		long startTime = System.nanoTime();
		
		ExecutorService e = Executors.newFixedThreadPool(20);
		System.out.println("Initial Thread Pool:");

		for (Node n : nodes) {
			entrezID = n.getIdentifier();
			if(entrezID.equals("Target") == false && entrezID.equals("Source") == false) {
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
	      	
		
	}

	class ImportTask implements Runnable {
		private String entrezID;

		public ImportTask(String id) {
			this.entrezID = id;
		}

		public void run() {
			System.out.println("RunnableTask starts.");

			try {
				EFetchRequest parameters = new EFetchRequest();
				parameters.setDb("gene");

				parameters.setId(entrezID);

				EFetchResult res = (EFetchResult) ncbi.execute("run_eFetch",
				                                               new Class[] { EFetchRequest.class },
				                                               new Object[] { parameters });

				// Create network from Interactions section
				for (int i = 0; i < res.getEntrezgeneSet().getEntrezgene().length; i++) {
					System.out.println("summary: "
					                   + res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_gene()
					                                                              .getGeneRef()
					                                                              .getGeneRef_desc());

					String centerID = res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_gene()
					                                                           .getGeneRef()
					                                                           .getGeneRef_locus();

					if (centerID == null) {
						centerID = entrezID;
					}

					GeneCommentaryType[] gc = res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_comments()
					                                                                   .getGeneCommentary();

					for (GeneCommentaryType g : gc) {
						//    					System.out.println("---------GC : "
						//    					                   + g.getGeneCommentary_label());
						//    					System.out.println("---------GC : "
						//    					                   + g.getGeneCommentary_heading());
						if ((g.getGeneCommentary_heading() != null)
						    && g.getGeneCommentary_heading().equals("Pathways")) {
							GeneCommentaryType[] gcc = g.getGeneCommentary_comment()
							                            .getGeneCommentary();

							for (GeneCommentaryType ggg : gcc) {
								System.out.println(entrezID + " -----Pathway: "
								                   + ggg.getGeneCommentary_text());
								
								String pwName = ggg.getGeneCommentary_text();
								resMap.put(entrezID, pwName);
								
								//    							if (ggg.getGeneCommentary_comment().getGeneCommentary().length > 1) {
								//    								String nodeid = ggg.getGeneCommentary_comment()
								//    								                   .getGeneCommentary(1)
								//    								                   .getGeneCommentary_source()
								//    								                   .getOtherSource(0)
								//    								                   .getOtherSource_anchor();
								//    								System.out.println("---------Intr : " + nodeid);
								//
								//    							
								//    							}
							}
						}
					}

					//System.out.println("summary: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_properties().getGeneCommentary(0).getGeneCommentary_type());
					//		                System.out.println("Abstract: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_nonUniqueKeys().getDbtag(0));
					System.out.println("--------------------------\n");
				}
			} catch (Exception e) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Problem for: " + entrezID );
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void databaseComboBoxActionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void importAttributes() {
		// TODO Auto-generated method stub
	}
}
