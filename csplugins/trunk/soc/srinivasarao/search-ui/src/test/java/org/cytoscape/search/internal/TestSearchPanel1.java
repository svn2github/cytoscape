package org.cytoscape.search.internal;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.internal.ArrayGraph;
import org.cytoscape.search.ui.SearchPanel;
import org.cytoscape.search.ui.SearchPanelFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.internal.NetworkManager;

public class TestSearchPanel1 {

	private JFrame jf = new JFrame();

	private CyNetworkManager nm;

	private SearchPanel esp;

	private CyNetwork net;

	public TestSearchPanel1() {
		setup();
		nm = new NetworkManager(new DummyCyEventHelper());
		nm.addNetwork(net);
		nm.setCurrentNetwork(net.getSUID());
		esp = SearchPanelFactory.getGlobalInstance(nm);
	}

	public void createAndShowGUI() {
		jf.add(esp);
		jf.setTitle("Search Panel");
		jf.setLocation(650, 130);
		jf.setSize(300, 600);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestSearchPanel1 sp = new TestSearchPanel1();
		sp.createAndShowGUI();
	}

	public void setup() {
		net = new ArrayGraph(new DummyCyEventHelper());
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();
		CyNode n6 = net.addNode();
		CyNode n7 = net.addNode();
		CyNode n8 = net.addNode();
		CyNode n9 = net.addNode();
		CyNode n10 = net.addNode();
		CyNode n11 = net.addNode();

		CyDataTable nodetable = (CyDataTable) net.getNodeCyDataTables().get(
				CyNetwork.DEFAULT_ATTRS);
		nodetable.createColumn("Official HUGO Symbol", String.class, true);
		nodetable.createColumn("CanonicalName", String.class, true);
		nodetable.createColumn("TestNodeNumericAttr", Integer.class, true);
		nodetable.createColumn("testDoubleAttr", Double.class, true);

		CyRow r1 = nodetable.getRow(n1.getSUID());
		r1.set("Official HUGO Symbol", "ING5");
		r1.set("CanonicalName", "84289");
		r1.set("TestNodeNumericAttr", 14356);
		r1.set("testDoubleAttr", 10034.54);

		CyRow r2 = nodetable.getRow(n2.getSUID());
		r2.set("Official HUGO Symbol", "CCNG1");
		r2.set("CanonicalName", "900");
		r2.set("TestNodeNumericAttr", 3410);
		r2.set("testDoubleAttr", 9088.31);

		CyRow r3 = nodetable.getRow(n3.getSUID());
		r3.set("Official HUGO Symbol", "SCOTIN");
		r3.set("CanonicalName", "51246");
		r3.set("TestNodeNumericAttr", 44601);
		r3.set("testDoubleAttr", 4217.6);

		CyRow r4 = nodetable.getRow(n4.getSUID());
		r4.set("Official HUGO Symbol", "KLF4");
		r4.set("CanonicalName", "9314");
		r4.set("TestNodeNumericAttr", 99807);
		r4.set("testDoubleAttr", 1129.08);

		CyRow r5 = nodetable.getRow(n5.getSUID());
		r5.set("Official HUGO Symbol", "TP53");
		r5.set("CanonicalName", "7157");
		r5.set("TestNodeNumericAttr", 57691);
		r5.set("testDoubleAttr", 5410.71);

		CyRow r6 = nodetable.getRow(n6.getSUID());
		r6.set("Official HUGO Symbol", "HMGB1");
		r6.set("CanonicalName", "3146");
		r6.set("TestNodeNumericAttr", 4286);
		r6.set("testDoubleAttr", 4456.7);

		CyRow r7 = nodetable.getRow(n7.getSUID());
		r7.set("Official HUGO Symbol", "HFG2");
		r7.set("CanonicalName", "56301");
		r7.set("TestNodeNumericAttr", 27740);
		r7.set("testDoubleAttr", 590.12);

		CyRow r8 = nodetable.getRow(n8.getSUID());
		r8.set("Official HUGO Symbol", "MBG12");
		r8.set("CanonicalName", "6737");
		r8.set("TestNodeNumericAttr", 4537);
		r8.set("testDoubleAttr", 2479.6);

		CyRow r9 = nodetable.getRow(n9.getSUID());
		r9.set("Official HUGO Symbol", "BHD27");
		r9.set("CanonicalName", "33278");
		r9.set("TestNodeNumericAttr", 118976);
		r9.set("testDoubleAttr", 610.21);

		CyRow r10 = nodetable.getRow(n10.getSUID());
		r10.set("Official HUGO Symbol", "PTG69");
		r10.set("CanonicalName", "1106");
		r10.set("TestNodeNumericAttr", 90087);
		r10.set("testDoubleAttr", 7341.32);

		CyRow r11 = nodetable.getRow(n11.getSUID());
		r11.set("Official HUGO Symbol", "HMTV");
		r11.set("CanonicalName", "8897");
		r11.set("TestNodeNumericAttr", 4286);
		r11.set("testDoubleAttr", 347.74);

		CyEdge e1 = net.addEdge(n5, n3, true);
		CyEdge e2 = net.addEdge(n5, n4, true);
		CyEdge e3 = net.addEdge(n2, n5, true);
		CyEdge e4 = net.addEdge(n6, n5, true);
		CyEdge e5 = net.addEdge(n10, n1, true);
		CyEdge e6 = net.addEdge(n11, n9, true);
		CyEdge e7 = net.addEdge(n7, n1, true);
		CyEdge e8 = net.addEdge(n8, n7, true);

		CyDataTable edgetable = (CyDataTable) net.getEdgeCyDataTables().get(
				CyNetwork.DEFAULT_ATTRS);
		edgetable.createColumn("CanonicalName", String.class, true);
		edgetable.createColumn("interaction", String.class, true);
		edgetable.createColumn("TestEdgeNumericAttr", Integer.class, true);

		CyRow re1 = edgetable.getRow(e1.getSUID());
		re1.set("CanonicalName", "7157 (non_core) 51246");
		re1.set("interaction", "core");
		re1.set("TestEdgeNumericAttr", 99081);

		CyRow re2 = edgetable.getRow(e2.getSUID());
		re2.set("CanonicalName", "7157 (non_core) 9314");
		re2.set("interaction", "non_core");
		re2.set("TestEdgeNumericAttr", 67550);

		CyRow re3 = edgetable.getRow(e3.getSUID());
		re3.set("CanonicalName", "900 (non_core) 7157");
		re3.set("interaction", "hyper_core");
		re3.set("TestEdgeNumericAttr", 9084);

		CyRow re4 = edgetable.getRow(e4.getSUID());
		re4.set("CanonicalName", "3146 (non_core) 7157");
		re4.set("interaction", "Y2H");
		re4.set("TestEdgeNumericAttr", 1145);

		CyRow re5 = edgetable.getRow(e5.getSUID());
		re5.set("CanonicalName", "1106 (non_core) 84289");
		re5.set("interaction", "Y2H");
		re5.set("TestEdgeNumericAttr", 8154);

		CyRow re6 = edgetable.getRow(e6.getSUID());
		re6.set("CanonicalName", "8897 (non_core) 33278");
		re6.set("interaction", "core");
		re6.set("TestEdgeNumericAttr", 20956);

		CyRow re7 = edgetable.getRow(e7.getSUID());
		re7.set("CanonicalName", "56301 (non_core) 84289");
		re7.set("interaction", "hyper_core");
		re7.set("TestEdgeNumericAttr", 7102);

		CyRow re8 = edgetable.getRow(e8.getSUID());
		re8.set("CanonicalName", "6737 (non_core) 56301");
		re8.set("interaction", "core");
		re8.set("TestEdgeNumericAttr", 1093);
	}
}
