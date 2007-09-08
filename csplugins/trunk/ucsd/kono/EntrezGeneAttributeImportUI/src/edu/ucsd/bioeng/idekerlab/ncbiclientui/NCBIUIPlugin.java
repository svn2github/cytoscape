
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

package edu.ucsd.bioeng.idekerlab.ncbiclientui;

import cytoscape.Cytoscape;

import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;

import cytoscape.plugin.CytoscapePlugin;

import giny.model.Edge;
import giny.model.Node;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.ESearchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.esearch.ESearchResult;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.ucsd.bioeng.idekerlab.ncbiclientui.ui.NCBIGeneDialog;


/**
 *
 */
public class NCBIUIPlugin extends CytoscapePlugin {
	/**
	 * Creates a new NCBIUIPlugin object.
	 */
	public NCBIUIPlugin() {
		final JMenu menu = new JMenu("Access NCBI Entrez");

		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Import").add(menu);

		menu.add(new JMenuItem(new AbstractAction("Build network from keyword") {
				public void actionPerformed(ActionEvent e) {
					buildNetwork();
				}
			}));
		
		menu.add(new JMenuItem(new AbstractAction("Import annotations from Entrez Gene...") {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new NCBIGeneDialog();
				dialog.setVisible(true);
			}
		}));
	}

	private void buildNetwork() {
		WebServiceClient client = WebServiceClientManager.getClient("ncbi_entrez");

		// First, run esearch
		ESearchRequest parameters1 = new ESearchRequest();
		parameters1.setDb("gene");
		parameters1.setRetMax("5");

		String keyword = JOptionPane.showInputDialog(this, "Enter search keyword");
		keyword = keyword.replace(" ", "+");
		parameters1.setTerm(keyword);

		ESearchResult res2;

		try {
			res2 = (ESearchResult) client.execute("run_eSearch",
			                                      new Class[] { ESearchRequest.class },
			                                      new Object[] { parameters1 });
			System.out.println("Found ids: " + res2.getCount());
			System.out.print("First " + res2.getRetMax() + " ids: ");

			String[] ids = new String[Integer.parseInt(res2.getRetMax())];

			for (int i = 0; i < res2.getIdList().getId().length; i++) {
				ids[i] = res2.getIdList().getId()[i];
				System.out.print(res2.getIdList().getId()[i] + " ");
			}

			for (String id : ids) {
				EFetchRequest parameters = new EFetchRequest();
				parameters.setDb("gene");

				parameters.setId(id);

				EFetchResult res = (EFetchResult) client.execute("run_eFetch",
				                                                 new Class[] {
				                                                     EFetchRequest.class
				                                                 },
				                                                 new Object[] { parameters });

				// results output
				List<Node> nodes = new ArrayList<Node>();
				List<Edge> edges = new ArrayList<Edge>();

				// Create network from Interactions section
				for (int i = 0; i < res.getEntrezgeneSet().getEntrezgene().length;
				     i++) {
					System.out.println("summary: "
					                   + res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_gene()
					                                                              .getGeneRef()
					                                                              .getGeneRef_desc());

					String centerID = res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_gene()
					                                                           .getGeneRef()
					                                                           .getGeneRef_locus();
					if(centerID == null) {
						centerID = id;
					}
					Node centernode = Cytoscape.getCyNode(centerID, true);
					nodes.add(centernode);

					GeneCommentaryType[] gc = res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_comments()
					                                                                   .getGeneCommentary();

					for (GeneCommentaryType g : gc) {
						System.out.println("---------GC : "
						                   + g.getGeneCommentary_label());
						System.out.println("---------GC : "
						                   + g.getGeneCommentary_heading());

						if ((g.getGeneCommentary_heading() != null)
						    && g.getGeneCommentary_heading().equals("Interactions")) {
							GeneCommentaryType[] gcc = g.getGeneCommentary_comment()
							                            .getGeneCommentary();

							for (GeneCommentaryType ggg : gcc) {
								System.out.println("---------GC2 : "
								                   + ggg.getGeneCommentary_text());
								String interactionType = ggg.getGeneCommentary_text();
								
								if(interactionType == null) {
									interactionType = "unknown";
								}
								if (ggg.getGeneCommentary_comment().getGeneCommentary().length > 1) {
									String nodeid = ggg.getGeneCommentary_comment()
									                   .getGeneCommentary(1)
									                   .getGeneCommentary_source()
									                   .getOtherSource(0)
									                   .getOtherSource_anchor();
									System.out.println("---------Intr : " + nodeid);

									Node n1 = Cytoscape.getCyNode(nodeid, true);
									nodes.add(n1);

									Edge e1 = Cytoscape.getCyEdge(centernode, n1,
									                              "interaction", interactionType,
									                              true);
									edges.add(e1);
								}
							}

							Cytoscape.createNetwork(nodes, edges, "NCBI-" + centerID,
							                        null);
						}
					}
					
					//System.out.println("summary: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_properties().getGeneCommentary(0).getGeneCommentary_type());
					//		                System.out.println("Abstract: "+res.getEntrezgeneSet().getEntrezgene()[i].getEntrezgene_nonUniqueKeys().getDbtag(0));
					System.out.println("--------------------------\n");
				}
			}

			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
