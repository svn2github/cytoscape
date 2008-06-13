import sys
from sawdclient import sawdclient

PARAM_SEPARATOR = ','
X_INCREMENT = 10
Y_INCREMENT = 10
MAX_WIDTH = 300

def print_usage_and_exit(message):
	print message
	print 'Parameters: load_graph.py server port graph_file'
	sys.exit(1)

class node_factory:
	def __init__(self, client, graph_index):
		self.client = client
		self.graph_index = graph_index
		self.current_x = 0
		self.current_y = 0
	def new_node(self, name):
		if self.current_x >= MAX_WIDTH:
			self.current_x = 0
			self.current_y += Y_INCREMENT
		node_index = self.client.new_node(self.graph_index)
		self.client.set_node_attr(self.graph_index, node_index, 'name', name)
		self.client.set_node_attr(self.graph_index, node_index, 'x', str(self.current_x))
		self.client.set_node_attr(self.graph_index, node_index, 'y', str(self.current_y))
		self.current_x += X_INCREMENT
		return node_index

if len(sys.argv) < 4:
	print_usage_and_exit('Not enough parameters were specified')

file_path = sys.argv[3]
file = open(file_path, 'r')
client = sawdclient(sys.argv[1], int(sys.argv[2]))
graph_index = client.new_graph()
client.set_graph_attr(graph_index, 'name', file_path)

nodes = {}
node_creator = node_factory(client, graph_index)
for line in file:
	pieces = line.split()
	source_name = pieces[0]
	if not source_name in nodes:
		source_index = node_creator.new_node(source_name)
		nodes[source_name] = source_index
	else:
		source_index = nodes[source_name]
	target_name = pieces[1]
	if not target_name in nodes:
		target_index = node_creator.new_node(target_name)
		nodes[target_name] = target_index
	else:
		target_index = nodes[target_name]
	edge_index = client.new_edge(graph_index, source_index, target_index)

	if len(pieces) < 3: continue
	attrs = pieces[2].split(',')
	for attr in attrs:
		attr_pieces = attr.split('=')
		attr_name = attr_pieces[0]
		attr_value = attr_pieces[1]
		client.set_edge_attr(graph_index, edge_index, attr_name, attr_value)
client.close()
file.close()

print 'The graph stored in the file \'%s\' has been loaded to the server \'%s:%s\' at graph index \'%s\'.' % (file_path, sys.argv[1], sys.argv[2], graph_index)
