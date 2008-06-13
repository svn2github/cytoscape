import sys
from sawdclient import sawdclient
from random import random, uniform

# definitions

ITERATIONS = 100
FORCE_STRENGTH = 10
FORCE_MULTIPLIER = 0.10
EDGE_WEIGHT_MULTIPLIER = 15
MAX_NODE_MOVEMENT = 1.5
MAX_REPULSIVE_RADIUS = 15

class point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

def calc_norm(point2, point1):
	dx = point1.x - point2.x
	dy = point1.y - point2.y
	result = (dx ** 2.0 + dy ** 2.0) ** 0.5
	if result < 0.1:
		dx = random() * 0.1 + 0.1
		dy = random() * 0.1 + 0.1
		result = (dx ** 2.0 + dy ** 2.0) ** 0.5
	return (dx, dy, result)

def repulsion(node1, node2):
	(dx, dy, norm) = calc_norm(node1[0], node2[0])
	if norm < MAX_REPULSIVE_RADIUS:
		f = (FORCE_STRENGTH ** 2.0) / (norm ** 2.0)
		node2[1].x += f * dx
		node2[1].y += f * dy
		node1[1].x -= f * dx
		node1[1].y -= f * dy

def attraction(node1, node2):
	(dx, dy, norm) = calc_norm(node1[0], node2[0])
	norm = min(norm, MAX_REPULSIVE_RADIUS)
	f = 1.0 / norm

	node2[1].x -= f * dx
	node2[1].y -= f * dy
	node1[1].x += f * dx
	node1[1].y += f * dy

# get parameters

def print_usage_and_exit(message):
	print message
	print 'Usage: force_layout.py server port graph_index'
	sys.exit(1)

if len(sys.argv) < 4:
	print_usage_and_exit('Not enough arguments')

server = sys.argv[1]
port = int(sys.argv[2])
graph_index = sys.argv[3]

client = sawdclient(server, port)

# read in graph

nodes = {}

node_indices = client.list_nodes(graph_index)
max_area = 3.0 * len(node_indices)
for node_index in node_indices:
	location = point(uniform(0.0, max_area), uniform(0.0, max_area))
	nodes[node_index] = (location, point(0,0))
edges = []

edge_indices = client.list_edges(graph_index)
for edge_index in edge_indices:
	source = client.get_edge_source(graph_index, edge_index)
	target = client.get_edge_target(graph_index, edge_index)
	edges.append((source, target))

# perform layout algorithm

for iteration in range(ITERATIONS):
	for i in range(len(node_indices)):
		node1_name = node_indices[i]
		for j in range(i + 1, len(node_indices)):
			node2_name = node_indices[j]
			repulsion(nodes[node1_name], nodes[node2_name])
	for edge in edges:
		attraction(nodes[edge[0]], nodes[edge[1]])
	
	for node_name in node_indices:
		node = nodes[node_name]
		x = max(-MAX_NODE_MOVEMENT, min(FORCE_MULTIPLIER * node[1].x, MAX_NODE_MOVEMENT))
		y = max(-MAX_NODE_MOVEMENT, min(FORCE_MULTIPLIER * node[1].y, MAX_NODE_MOVEMENT))
		node[0].x += x
		node[0].y += y
		node[1].x = 0
		node[1].y = 0

# update graph on server
minx = 100000000000000000.0
for node_name in node_indices:
	minx = min(nodes[node_name][0].x, minx)
for node_name in node_indices:
	nodes[node_name][0].x -= minx
	client.set_node_attr(graph_index, node_name, 'x', str(nodes[node_name][0].x))

miny = 100000000000000000.0
for node_name in node_indices:
	miny = min(nodes[node_name][0].y, miny)
for node_name in node_indices:
	nodes[node_name][0].y -= miny
	client.set_node_attr(graph_index, node_name, 'y', str(nodes[node_name][0].y))

client.close()
