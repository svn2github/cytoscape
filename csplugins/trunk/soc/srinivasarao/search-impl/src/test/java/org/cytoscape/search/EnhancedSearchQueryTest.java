package org.cytoscape.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.internal.*;
import org.cytoscape.search.internal.*;

public class EnhancedSearchQueryTest extends AbstractEnhancedSearchQueryTest {

	private EnhancedSearchIndex esi = null;
	private EnhancedSearchQuery esq = null;
	private CyNetwork net;
	private RAMDirectory rd;
	
	protected void setUp(){
		net = new ArrayGraph(new DummyCyEventHelper());
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();
		CyNode n6 = net.addNode();
		
		CyDataTable nodetable = (CyDataTable)net.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		nodetable.createColumn("Official HUGO Symbol", String.class, true);
		nodetable.createColumn("canonicalName", String.class, true);
		
		CyRow r1 = nodetable.getRow(n1.getSUID());
		r1.set("Official HUGO Symbol","ING5");
		r1.set("canonicalName","84289");
		
		CyRow r2 = nodetable.getRow(n2.getSUID());
		r2.set("Official HUGO Symbol","CCNG1");
		r2.set("canonicalName","900");
		
		CyRow r3 = nodetable.getRow(n3.getSUID());
		r3.set("Official HUGO Symbol","SCOTIN");
		r3.set("canonicalName", "51246");
		
		CyRow r4 = nodetable.getRow(n4.getSUID());
		r4.set("Official HUGO Symbol","KLF4");
		r4.set("canonicalName", "9314");
		
		CyRow r5 = nodetable.getRow(n5.getSUID());
		r5.set("Official HUGO Symbol","TP53");
		r5.set("canonicalName", "7157");
		
		CyRow r6 = nodetable.getRow(n6.getSUID());
		r6.set("Official HUGO Symbol","HMGB1");
		r6.set("canonicalName", "3146");
		
		CyEdge e1 = net.addEdge(n5, n3, true);
		CyEdge e2 = net.addEdge(n5, n4, true);
		CyEdge e3 = net.addEdge(n2, n5, true);
		CyEdge e4 = net.addEdge(n6, n5, true);
		
		CyDataTable edgetable = (CyDataTable)net.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		edgetable.createColumn("canonicalName", String.class, true);
		edgetable.createColumn("interaction", String.class, true);
		
		CyRow re1 = edgetable.getRow(e1.getSUID());
		re1.set("canonicalName", "7157 (non_core) 51246");
		re1.set("interaction","non_core");
		
		CyRow re2 = edgetable.getRow(e2.getSUID());
		re2.set("canonicalName", "7157 (non_core) 9314");
		re2.set("interaction","non_core");
		
		CyRow re3 = edgetable.getRow(e3.getSUID());
		re3.set("canonicalName", "900 (non_core) 7157");
		re3.set("interaction","non_core");

		CyRow re4 = edgetable.getRow(e4.getSUID());
		re4.set("canonicalName", "3146 (non_core) 7157");
		re4.set("interaction","non_core");
		
		esi = new EnhancedSearchIndexImpl(net);
		rd = esi.getIndex();
		esq = new EnhancedSearchQueryImpl(rd, net);
		
	}
	
	@Override
	public void testgetHitCount() {
		// TODO Auto-generated method stub
		String queryString = "9314" ;
		esq.executeQuery(queryString);
		assertEquals(esq.getHitCount(),2);
	}
	
	@Override
	public void testgetNodeHits() {
		// TODO Auto-generated method stub
		String queryString = "node.Official HUGO Symbol:TP53";
		esq.executeQuery(queryString);
		ArrayList<CyNode> al = esq.getNodeHits();
		assertEquals(al.size(),1);
	}
	
	@Override
	public void testgetEdgeHits() {
		// TODO Auto-generated method stub
		String queryString = "edge.canonicalName:7157";
		esq.executeQuery(queryString);
		ArrayList<CyEdge> al = esq.getEdgeHits();
		assertEquals(al.size(),4); 
	}
	 
}
