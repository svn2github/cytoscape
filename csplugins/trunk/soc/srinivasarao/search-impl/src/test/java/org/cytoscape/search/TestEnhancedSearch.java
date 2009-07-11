
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

package org.cytoscape.search;

//import java.io.File;
//import java.util.*;
import junit.framework.TestCase;
import org.cytoscape.search.internal.*;
import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.internal.ArrayGraph;
//import org.cytoscape.task.internal.loadnetwork.LoadNetworkFileTask;
//import org.cytoscape.io.internal.read.*;
//import org.cytoscape.view.model.internal.ColumnOrientedNetworkViewFactoryImpl;
//import org.osgi.framework.*;
//import cytoscape.CyNetworkManager;
//import org.cytoscape.work.TaskMonitor;
//import org.cytoscape.event.internal.CyEventHelperImpl;
//import org.cytoscape.work.internal.task.*;
public class TestEnhancedSearch extends TestCase {

	String query = null;
	int hitCount;
//	private LoadNetworkFileTask lf;
//	private CyNetworkManager netmgr;
//	private TaskMonitor tm;
	CyNetwork cyNetwork;
	//private BundleContext bc;
	protected CyEventHelper helper;
	// Load sample network and attributes into memory
	public TestEnhancedSearch() {
		helper = new DummyCyEventHelper();
		//cyNetwork = new MGraph(helper);
		cyNetwork = new ArrayGraph(new DummyCyEventHelper());
		CyNode n1 = cyNetwork.addNode();
		//System.out.println(tn.toString());
		CyNode n2 = cyNetwork.addNode();
		CyNode n3 = cyNetwork.addNode();
		CyDataTable nodetable = (CyDataTable)cyNetwork.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		nodetable.createColumn("Official HUGO Symbol", String.class, true);
		CyRow r1 = nodetable.getRow(n1.getSUID());
		r1.set("Official HUGO Symbol","a");
		CyRow r2 = nodetable.getRow(n2.getSUID());
		r2.set("Official HUGO Symbol","b");
		CyRow r3 = nodetable.getRow(n3.getSUID());
		r3.set("Official HUGO Symbol","c");
/*	//	Timer timer = new Timer();
//        tm = new ConsoleTaskMonitor(timer);

		//cyNetwork = Cytoscape.createNetworkFromFile("testData/network.sif");
		lf = new LoadNetworkFileTask(new CyReaderManagerImpl(), new ColumnOrientedNetworkViewFactoryImpl(), new CyLayoutsImpl(), netmgr , new Properties(), new CyNetworkNamingImpl() );
		lf.file = new File("testData/network.sif");
		lf.run(tm);
		String[] noa = new String[7];
		noa[0] = new File("testData/GOMolecularFunction.NA").getAbsolutePath();
		noa[1] = new File("testData/GOCellularComponent.NA").getAbsolutePath();
		noa[2] = new File("testData/GOBiologicalProcess.NA").getAbsolutePath();
		noa[3] = new File("testData/GeneTitle.NA").getAbsolutePath();
		noa[4] = new File("testData/DesiccationResponse.NA").getAbsolutePath();
		noa[5] = new File("testData/Chromosome.NA").getAbsolutePath();
		noa[6] = new File("testData/AGI.NA").getAbsolutePath();

		String[] eda = new String[2];
		eda[0] = new File("testData/weight.EA").getAbsolutePath();
		eda[1] = new File("testData/interaction.EA").getAbsolutePath();

		Cytoscape.loadAttributes(noa, eda);
*/
	}
	public static void main(String[] args) throws Exception{
		TestEnhancedSearch te = new TestEnhancedSearch();
		te.testSimpleQuery();
	}
	
	public void testSimpleQuery() throws Exception{
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);
		
		query="b";
		queryHandler.executeQuery(query); // 56
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);
		if(hitCount == 1){
			System.out.println("Yeah");
		}else{
			System.out.println("Nooooooooooooooooooo");
		}
	}
	/*
	// Simple queries
	public void testSimpleQueries() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "Gene_Title:putative";
		queryHandler.executeQuery(query); // 56
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 56, hitCount);

		query = "gENE_TitlE:putATIVE";
		queryHandler.executeQuery(query); // 56 - tests case insensitivity
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 56, hitCount);

		query = "GO_Cellular_Component:\"plasma membrane\"";
		queryHandler.executeQuery(query); // 7
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 7, hitCount);

		query = "canonicalName:251155_at";
		queryHandler.executeQuery(query); // 12 - notice it returnes both nodes and edges.
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 12, hitCount);

		query = "265480_at";
		queryHandler.executeQuery(query); // 26 - notice: search is executed on nodes and edges.
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 26, hitCount);

		query = "response";
		queryHandler.executeQuery(query); // Search in all attributes
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 70, hitCount);
	}

	// Queries on multiple attribute fields
	public void testComplexQueries() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "GO_Biological_Process:\"water deprivation\" AND Gene_Title:aquaporin";
		queryHandler.executeQuery(query); // 3
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 3, hitCount);

		query = "Desiccation_Response:true NOT Chromosome:5";
		queryHandler.executeQuery(query); // 20
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 20, hitCount);

		query = "GO_Biological_Process:stress AND (GO_Molecular_Function:peroxidase OR GO_Molecular_Function:catalase)";
		queryHandler.executeQuery(query); // 4
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);
	}

	// Multiple values for same attribute
	public void testFieldGrouping() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "Gene_Title:(+60S +\"ribosomal protein\")";
		queryHandler.executeQuery(query); // 9
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 9, hitCount);
	}

	// Wildcards queries
	public void testWildcardsQueries() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "Gene_Title:deHYdr*n";
		queryHandler.executeQuery(query); // 4
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);
	}

	// Range queries
	public void testRangeQueries() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "Chromosome:5";
		queryHandler.executeQuery(query); // 39
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 39, hitCount);

		query = "Chromosome:[4 TO 5]";
		queryHandler.executeQuery(query); // 79
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 79, hitCount);

		query = "weight:[0.95 TO 1]";
		queryHandler.executeQuery(query); // 369
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 369, hitCount);

		query = "weight:[-1 TO -0.95]";
		queryHandler.executeQuery(query); // 30
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 30, hitCount);

		query = "weight:[0.95 TO 1] OR weight:[-1 TO -0.95]";
		queryHandler.executeQuery(query); // 399
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 399, hitCount);
	}
	
	
	// Queries with no results
	public void testNoResultsQueries() throws Exception {
	
		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "interaction:neg AND weight:[0.9 TO 0.95]";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 0, hitCount);
	}
	*/
}
