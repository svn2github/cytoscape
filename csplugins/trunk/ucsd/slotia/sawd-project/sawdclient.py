import socket
import os
PARAM_SEPARATOR = ','
class sawdclient:
	def __init__(self, server, port):
		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.socket.connect((server, port))
		self.reader = self.socket.makefile()
	def close(self):
		self.reader.close()
		self.socket.close()
	def write(self, params):
		self.socket.send(PARAM_SEPARATOR.join(params) + '\n')
	def parse_ints(self, line):
		if len(line) == 0:
			return []
		elif line.startswith('ERROR'):
			return None
		else:
			return line.split(',')
	def new_graph(self):
		self.socket.send('new_graph\n')
		return self.reader.readline()[:-1]
	def set_graph_attr(self, graph_index, attr_name, attr_value):
		self.write(('set_graph_attribute', graph_index, attr_name, attr_value))
		return not self.reader.readline().startswith('ERROR')
	def list_nodes(self, graph_index):
		self.write(('list_nodes', graph_index))
		return self.parse_ints(self.reader.readline()[:-1])
	def new_node(self, graph_index):
		self.write(('new_node', graph_index))
		return self.reader.readline()[:-1]
	def get_node_attr(self, graph_index, node_index, attr_name):
		self.write(('get_node_attribute', graph_index, node_index, attr_name))
		return self.reader.readline()[:-1]
	def set_node_attr(self, graph_index, node_index, attr_name, attr_value):
		self.write(('set_node_attribute', graph_index, node_index, attr_name, attr_value))
		return not self.reader.readline().startswith('ERROR')
	def list_edges(self, graph_index):
		self.write(('list_edges', graph_index))
		return self.parse_ints(self.reader.readline()[:-1])
	def new_edge(self, graph_index, source_index, target_index):
		self.write(('new_edge', graph_index, source_index, target_index))
		return self.reader.readline()[:-1]
	def set_edge_attr(self, graph_index, edge_index, attr_name, attr_value):
		self.write(('set_edge_attribute', graph_index, edge_index, attr_name, attr_value))
		return not self.reader.readline().startswith('ERROR')
	def get_edge_source(self, graph_index, edge_index):
		self.write(('get_edge_source', graph_index, edge_index))
		return self.reader.readline()[:-1]
	def get_edge_target(self, graph_index, edge_index):
		self.write(('get_edge_target', graph_index, edge_index))
		return self.reader.readline()[:-1]
