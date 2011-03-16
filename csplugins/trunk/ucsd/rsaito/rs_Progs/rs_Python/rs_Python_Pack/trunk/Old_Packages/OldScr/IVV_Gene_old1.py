#!/usr/bin/python

import sys
import Hash_filt

class IVV_Gene:
    def __init__(self, mode):

	self.mode = mode
	self.geneid_pair = {}

    ######## IVV -> Gene ID Converter settings #######

    def set_bait_conv(self, bait_conv):
	self.bait_to_geneid = bait_conv

    def set_prey_conv(self, prey_conv):
	self.prey_to_geneid = prey_conv

    def bait2gene(self, bait_ID):
	return [ self.bait_to_geneid.val(bait_ID) ]

    def prey2gene(self, prey_ID):
	return [ self.prey_to_geneid.val(prey_ID) ]

    ##################################################

    def set_pair(self, b_geneid, p_geneid):
	if self.geneid_pair.has_key(b_geneid):
	    if self.geneid_pair[ b_geneid ].has_key(p_geneid):
		self.geneid_pair[ b_geneid ][ p_geneid ] = (
		    self.geneid_pair[ b_geneid ][ p_geneid ] + 1 )
	    else:
		self.geneid_pair[ b_geneid ][ p_geneid ] = 1
	else:
	    self.geneid_pair[ b_geneid ] = { p_geneid : 1 }


	if self.geneid_pair.has_key(p_geneid):
	    if self.geneid_pair[ p_geneid ].has_key(b_geneid):
		self.geneid_pair[ p_geneid ][ b_geneid ] = (
		    self.geneid_pair[ p_geneid ][ b_geneid ] + 1 )
	    else:
		self.geneid_pair[ p_geneid ][ b_geneid ] = 1
	else:
	    self.geneid_pair[ p_geneid ] = { b_geneid : 1 }


	if b_geneid == p_geneid:
	    self.geneid_pair[ p_geneid ][ b_geneid ] = (
		self.geneid_pair[ p_geneid ][ b_geneid ] - 1)
	    

    def ivv_to_gene(self, prey_to_bait):

	self.prey_to_bait = prey_to_bait
	bait_to_prey = Hash_filt.Hash_filt("A")
	bait_to_prey.reverse_Hash(prey_to_bait)
	baits = bait_to_prey.keys()

	for bait in baits:
	    for prey in bait_to_prey.val(bait):
		b_geneids = self.bait2gene(bait)
		p_geneids = self.prey2gene(prey)
		for b_geneid in b_geneids:
		    if b_geneid == "": continue
		    for p_geneid in p_geneids:
			if p_geneid == "": continue
			if b_geneid <> p_geneid:
			    self.set_pair(b_geneid, p_geneid)

	if self.mode == "M":
	    for bait in baits:
		pass # Under construction

    def gene_to_ivv(self, gene1, gene2):
	gene_to_bait = Hash_filt.Hash_filt("A")
	gene_to_bait.reverse_Hash(self.bait_to_geneid)
	gene_to_prey = Hash_filt.Hash_filt("A")
	gene_to_prey.reverse_Hash(self.prey_to_geneid)

	if gene_to_bait.has_key(gene1):
	    baits1 = gene_to_bait.val(gene1)
	else:
	    baits1 = []
	if gene_to_prey.has_key(gene2):
	    preys2 = gene_to_prey.val(gene2)
	else:
	    preys2 = []

	source_ppi = []

	for bait in baits1:
	    for prey in preys2:
		s_bait = self.prey_to_bait.val(prey)
		if bait == s_bait:
		    source_ppi.append(["BP", bait, prey])

	if gene_to_bait.has_key(gene2):
	    baits2 = gene_to_bait.val(gene2)
	else:
	    baits2 = []
	if gene_to_prey.has_key(gene1):
	    preys1 = gene_to_prey.val(gene1)
	else:
	    preys1 = []

	for bait in baits2:
	    for prey in preys1:
		s_bait = self.prey_to_bait.val(prey)
		if bait == s_bait:
		    source_ppi.append(("PB", prey, bait))
		    
	return source_ppi
		    
	
    def all_pair(self):
	return self.geneid_pair


if __name__ == "__main__":
    ivv_info_file = sys.argv[1]

    prey_to_bait = Hash_filt.Hash_filt("S")
    prey_to_bait.set_filt([0, "[ Prey Info ]"], [8, "FOS"])
    prey_to_bait.read_file(filename = ivv_info_file,
			   Key_cols = [1],
			   Val_cols = [4])
    bait_to_geneid = Hash_filt.Hash_filt("S")
    bait_to_geneid.set_filt([0, "[ Bait Info ]"], [3, "FOS"])
    bait_to_geneid.read_file(filename = ivv_info_file,
			     Key_cols = [1],
			     Val_cols = [2])

    prey_to_geneid = Hash_filt.Hash_filt("S")
    prey_to_geneid.set_filt([0, "[ Prey Info ]"], [8, "FOS"])
    prey_to_geneid.read_file(filename = ivv_info_file,
			     Key_cols = [1],
			     Val_cols = [2])

    ivv_gene = IVV_Gene("S")
    ivv_gene.set_bait_conv(bait_to_geneid)
    ivv_gene.set_prey_conv(prey_to_geneid)
    ivv_gene.ivv_to_gene(prey_to_bait)

#    print ivv_gene.all_pair()
    print "Search..."
    print ivv_gene.gene_to_ivv(`2353`, `1740`)
