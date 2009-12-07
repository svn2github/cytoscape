// Visualize Relationship in Twitter by using Google Social Graph API

importPackage(java.io);
importPackage(java.net);
importPackage( Packages.cytoscape.layout );
importPackage( Packages.cytoscape );

// Change this to your id
var myURL = "http://twitter.com/c_z";

var newNetwork = Cytoscape.createNetwork("Twitter Graph");
var me = newNetwork.addNode(Cytoscape.getCyNode(myURL, true));

var nodes = new Array();
var url = new URL('http://socialgraph.apis.google.com/lookup?q=' + myURL + '&edo=1');
var stream = new BufferedReader(new InputStreamReader(url.openStream()));
var line, json = '';
  
while(line = stream.readLine()) 
	json += line;  

stream.close();

relations = eval("(" + json + ")");

var people = relations.nodes[myURL].nodes_referenced;
for(key in people) {
	if(people[key]['types'] != 'me')
		nodes.push(newNetwork.addNode(Cytoscape.getCyNode(key, true)));
}

for(i=0; i<nodes.length; i++) { 
	edge = Cytoscape.getCyEdge(me, nodes[i], "interaction", "contact", true);
	if(edge != null)
		newNetwork.addEdge(edge);
}
	
Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
CyLayouts.getLayout("force-directed").doLayout();