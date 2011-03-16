#!/usr/bin/python

import sys
import Hash_filt
import Prey_info_ext1
import Bait_info_ext1
import NonRedSet

class IVV_Gene:
    def __init__(self, bait_info, prey_info, mode = "S"):

	self.mode = mode
        self.bait_info = bait_info
        self.prey_info = prey_info
	self.geneid_pair = {}

    def bait2gene(self, bait_ID):
        return [ self.bait_info.geneid(bait_ID) ]

    def prey2gene(self, prey_ID):
        return [ self.prey_info.geneid(prey_ID) ]

    def gene2baits(self, geneid):
        return self.bait_info.gene2bait(geneid)

    def gene2preys(self, geneid):
        return self.prey_info.gene2prey(geneid)


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
	    

    def ivv_to_gene(self):

	for bait in self.bait_info.baits():
            for prey in self.bait_info.bait2prey(bait):
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

        baits1 = self.gene2baits(gene1)
        preys2 = self.gene2preys(gene2)

	source_ppi = []

	for bait in baits1:
	    for prey in preys2:
		s_bait = self.prey_info.bait_ID(prey)
		if bait == s_bait:
		    source_ppi.append(("BP", bait, prey))

        baits2 = self.bait_info.gene2bait(gene2)
        preys1 = self.prey_info.gene2prey(gene1)

	for bait in baits2:
	    for prey in preys1:
		s_bait = self.prey_info.bait_ID(prey)
		if bait == s_bait:
		    source_ppi.append(("PB", prey, bait))

        for prey1 in preys1:
            for prey2 in preys2:
                s_bait1 = self.prey_info.bait_ID(prey1)
                s_bait2 = self.prey_info.bait_ID(prey2)
                if s_bait1 == s_bait2:
                    source_ppi.append(("PP", prey1, prey2, s_bait1))
		    
	return source_ppi

    def gene_to_ivv_common_bait(self, gene1, gene2):

        baits1 = self.gene2baits(gene1)
        preys2 = self.gene2preys(gene2)

        source_bait = NonRedSet.NonRedSet()

	for bait in baits1:
	    for prey in preys2:
		s_bait = self.prey_info.bait_ID(prey)
		if bait == s_bait:
                    source_bait.append(bait)

        baits2 = self.bait_info.gene2bait(gene2)
        preys1 = self.prey_info.gene2prey(gene1)

	for bait in baits2:
	    for prey in preys1:
		s_bait = self.prey_info.bait_ID(prey)
		if bait == s_bait:
                    source_bait.append(bait)

        for prey1 in preys1:
            for prey2 in preys2:
                s_bait1 = self.prey_info.bait_ID(prey1)
                s_bait2 = self.prey_info.bait_ID(prey2)
                if s_bait1 == s_bait2:
                    source_bait.append(s_bait1)
		    
	return source_bait.ret_set()

	
    def all_pair(self):
	return self.geneid_pair


if __name__ == "__main__":
    ivv_info_file = sys.argv[1]

    print "Reading Bait info..."
    bait_info = Bait_info_ext1.Bait_info_ext1(ivv_info_file)
    print "Reading Prey info..."
    prey_info = Prey_info_ext1.Prey_info_ext1(ivv_info_file)

    print "IVV -> Gene Calculation..."
    ivv_gene = IVV_Gene(bait_info, prey_info)
    ivv_gene.ivv_to_gene()

#    print ivv_gene.all_pair()
    print "Search..."
    print ivv_gene.gene_to_ivv(`2353`, `1740`)
    print ivv_gene.gene_to_ivv(`1740`, `2353`)

    print ivv_gene.gene_to_ivv_common_bait(`27101`, `10657`)
