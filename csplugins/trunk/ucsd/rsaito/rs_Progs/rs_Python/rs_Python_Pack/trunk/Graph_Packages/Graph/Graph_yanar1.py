#!/usr/bin/env python

from networkx import *
from random import*
# import psyco
# psyco.profile()

_path_list = []

def id_search(step, output, input):
	""" step: Number of steps (maximal)
	    output: goal node
	    input : start node
    """	    
	_path_list = []
	return _id_search(step, output, input)

def _id_search(step, output, input):
	n = len(input)
	m = input[n - 1]
	if n == step:
		if m == output:
			_path_list.append(input[:])
	else:
		for x in adjacent[m]:
			if x not in input:
				input.append(x)
				_id_search(step, output, input)
				input.pop()
	return [_path_list,len(_path_list)]
###########################
def get_abj(G):
	adjacent = []
	node = G.nodes()
	ad = 0
	while ad < len(node):
		if G.nodes().count(ad) == 0:
			adjacent.append([])
		else:
			adjacent.append(G.neighbors(ad))
		ad = ad + 1
	return adjacent
###########################

if __name__ == "__main__":
	
	from Usefuls.rsConfig import RSC_II
	rsc = RSC_II("rsGraph_Config")
	
	def kegg2g(file):
		G = DiGraph()
		for line in open(file):
			line = line.lstrip().rstrip()
			if line.startswith("<relation"):
				elems = line.split('"')
				G.add_edge(int(elems[1]),int(elems[3]))
		return G


	file = rsc.yanar_sample1
	G = kegg2g(file)
	#adjacent = get_abj(G)

	adjacent = ((1, 2),(2, 3),(3, 4),(4, 5),(),(),(1,))
	for x in range(1, 8):
		print x, 'moves'
		print id_search(x, 5, [0])
