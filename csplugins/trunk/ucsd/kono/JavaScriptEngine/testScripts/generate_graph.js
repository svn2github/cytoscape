// Generate a complete graph

importPackage( Packages.javax.swing );
importPackage( Packages.cytoscape );
importPackage( Packages.cytoscape.layout );

newNetwork = Cytoscape.createNetwork("Complete Graph 1");

var nodes = new Array();

for (i=0; i<10; i++) {
	nodeName = "Node " + i;
	nodes.push(newNetwork.addNode(Cytoscape.getCyNode(nodeName, true)));
}

for (i=0; i<10; i++) {
	for (j=0; j<10; j++) {
		if(i != j) {
			edge = Cytoscape.getCyEdge(nodes[i], nodes[j], "interaction", "pp", true);
    		newNetwork.addEdge(edge);
    	}
    }
}
Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
CyLayouts.getLayout("force-directed").doLayout();