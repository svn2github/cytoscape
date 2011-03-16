#!/usr/bin/env python

import re
import string
import Dag

class GO2Class:
    def __init__(self):
	self.dag = Dag.Dag()
	self.treeisa = []
	print "GO2Class"

    def local(self, database):
	text = ""
	local =[]
	gofile = open(database, "r")
	text = gofile.read()
	local = re.split(r'\n\n', text)
	return local

    def set_class(self, database):
	local = self.local( database )
	local.pop( 0 )
	local.pop
	lines = []
	for term in local:
	    lines = re.split(r'\n', term)
	    for line in lines:
		if re.search(r'^id:\sGO:(\d+)', line):
		    idmatch = re.match(r'^id:\s(GO:\d+)', line)
		    mainid = idmatch.group(1)
		    self.dag.set_id(mainid)
		elif re.search(r'^name:', line):
		    name = line[6:]
		    self.dag.set_name( name )
		elif re.search(r'^namespace:', line):
		    namespace = line[11:]
		    self.dag.set_namespace( namespace )
		elif re.search(r'^is_a:', line):
		    isamatch = re.match(r'^is_a:\s(GO:\d+)\s', line)
		    isa = isamatch.group(1)
		    self.dag.set_isa( isa )
		    self.dag.set_upper_isa( mainid, isa )
		elif re.search(r'^relationship: part_of', line):
		    partofmatch = re.match(r'^relationship:\spart_of\s(GO:\d+)\s', line)
		    partof = partofmatch.group(1)
		    self.dag.set_partof( partof )
		    self.dag.set_upper_partof( mainid, partof )
	print "Set GO2Class Done"
	return local

"""
if __name__ == "__main__":
    go = GO2Class()
    dag = Dag.Dag()
    text =  go.set_class("gene_ontology_sample")


    print
    print "GO2Class Print"
    print go.get_tree_upperisa2dag( ["GO:0007017"] )
"""
