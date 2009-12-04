require 'java'

include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.CyNode'
include_class 'cytoscape.CyEdge'
include_class 'cytoscape.CyNetwork'
include_class 'cytoscape.layout.CyLayouts'
include_class 'java.lang.Integer'

ORIGINAL_NODES = 2
MAX_LOOP = 1000 - ORIGINAL_NODES
EACH_TRIAL = 2

# Create network
graph = Cytoscape.createNetwork("Barabasi-Albert Scale Free Network");

nodes = Array.new

#Create seed graph
node1 = Cytoscape.getCyNode("Seed 1", true)
node2 = Cytoscape.getCyNode("Seed 2", true)
nodes << node1
nodes << node2
graph.addNode(node1)
graph.addNode(node2)
edge = Cytoscape.getCyEdge(node1, node2, "interaction", "-", true);
graph.addEdge(edge);

degree = 0.0
prob = 0.0
i = 0

# Grow the seed graph
while i<MAX_LOOP do
	newNode = Cytoscape.getCyNode("Node " + Integer.toString(i), true)
	graph.addNode(newNode)
	
	j = 0
	while j<EACH_TRIAL do	
		created = false
		while created == false do
			selectedNode = nodes[rand(nodes.length)]
			degree = graph.getDegree(selectedNode)
			prob = (degree + 1.0) / (graph.getNodeCount() + graph.getEdgeCount() - 1.0)
			if prob >= rand && newNode.getIdentifier != selectedNode.getIdentifier
				created = true;
			end			
		end
		
		edge = Cytoscape.getCyEdge(newNode, selectedNode, "interaction", "-", true);
		graph.addEdge(edge);
		nodes << newNode
		j = j+1		
	end	
	i = i+1
end

# Layout nodes
CyLayouts.getLayout("force-directed").doLayout();
Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
