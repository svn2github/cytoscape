require 'java'

include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.CyNetwork'
include_class 'java.util.ArrayList'

# Nodes in current network
net = Cytoscape.getCurrentNetwork
nodes =  Cytoscape.getCurrentNetwork.nodesList
subnet = ArrayList.new

nodes.each do |node|
 
  subnet.add(node)
  puts net.getConnectingEdges(subnet)
end