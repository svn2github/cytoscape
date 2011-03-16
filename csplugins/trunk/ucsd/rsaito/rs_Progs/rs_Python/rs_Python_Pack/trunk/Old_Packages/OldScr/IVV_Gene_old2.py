#!/usr/bin/python

import sys
import Usefuls.NonRedSet
import Usefuls.Usefuls1
import IVV_info

class IVV_Gene:
    def __init__(self, ivv_info, mode = "S"):

       	self.mode = mode
        self.bait_info = ivv_info.Bait_info()
        self.prey_info = ivv_info.Prey_info()
	self.geneid_pair = {}
	self.geneid_pair_m = {}

    def bait2gene(self, bait_ID):
        return [ self.bait_info.geneid(bait_ID) ]

    def prey2gene(self, prey_ID):
        return [ self.prey_info.geneid(prey_ID) ]

    def gene2baits(self, geneid):
        return self.bait_info.gene2bait(geneid)

    def gene2preys(self, geneid):
        return self.prey_info.gene2prey(geneid)
    
    def set_pair(self, b_geneid, p_geneid):
        """ Counts and Records b_geneid and p_geneid to self.geneid_pair
        as bait and prey """

	if self.geneid_pair.has_key(b_geneid):
	    if self.geneid_pair[ b_geneid ].has_key(p_geneid):
		self.geneid_pair[ b_geneid ][ p_geneid ] += 1
	    else:
		self.geneid_pair[ b_geneid ][ p_geneid ] = 1
	else:
	    self.geneid_pair[ b_geneid ] = { p_geneid : 1 }


	if self.geneid_pair.has_key(p_geneid):
	    if self.geneid_pair[ p_geneid ].has_key(b_geneid):
		self.geneid_pair[ p_geneid ][ b_geneid ] += 1
	    else:
		self.geneid_pair[ p_geneid ][ b_geneid ] = 1
	else:
	    self.geneid_pair[ p_geneid ] = { b_geneid : 1 }


	if b_geneid == p_geneid:
	    self.geneid_pair[ p_geneid ][ b_geneid ] -= 1

    def set_pair_spoke(self, b_geneid, p_geneid):
        """ Counts and Records b_geneid and p_geneid to self.geneid_pair
        as bait and prey """

	if self.geneid_pair.has_key(b_geneid):
	    if self.geneid_pair[ b_geneid ].has_key(p_geneid):
		self.geneid_pair[ b_geneid ][ p_geneid ] += 1
	    else:
		self.geneid_pair[ b_geneid ][ p_geneid ] = 1
	else:
	    self.geneid_pair[ b_geneid ] = { p_geneid : 1 }


    def set_pair_matrix(self, p_geneid1, p_geneid2):
        """ Counts and Records p_geneid1 and p_geneid2 to self.geneid_pair_m
        as prey1 and prey2 """

	if p_geneid1 in self.geneid_pair_m:
	    if p_geneid2 in self.geneid_pair_m[ p_geneid1 ]:
		self.geneid_pair_m[ p_geneid1 ][ p_geneid2 ] += 1
	    else:
		self.geneid_pair_m[ p_geneid1 ][ p_geneid2 ] = 1
	else:
	    self.geneid_pair_m[ p_geneid1 ] = { p_geneid2 : 1 }

    def get_spoke(self):
        return self.geneid_pair

    def get_matrix(self):
        return self.geneid_pair_m

    def ivv_to_gene(self):
        """ Records and counts all interactions according bait_info """
        
	for bait in self.bait_info.baits():
            for prey in self.prey_info.bait2prey(bait):
		b_geneids = self.bait2gene(bait)
		p_geneids = self.prey2gene(prey)
		for b_geneid in b_geneids:
		    if b_geneid == "": continue
		    for p_geneid in p_geneids:
			if p_geneid == "": continue
			if b_geneid <> p_geneid:
			    self.set_pair_spoke(b_geneid, p_geneid)

	if self.mode == "M":
	    for bait in self.bait_info.baits():
                preys = self.prey_info.bait2prey(bait)
                preys = preys[:10]
                for i in range(len(preys) - 1):
                    for j in range(i+1, len(preys)):
                        print i,j
                        prey1 = preys[i]
                        prey2 = preys[j]
                        p_geneids1 = self.prey2gene(prey1)
                        p_geneids2 = self.prey2gene(prey2)
                        for p_geneid1 in p_geneids1:
                            if p_geneid1 == "": continue
                            for p_geneid2 in p_geneids2:
                                if p_geneid2 == "": continue
                                if p_geneid1 <> p_geneid2:
                                    print p_geneid1, p_geneid2
                                    self.set_pair_matrix(p_geneid1,
                                                         p_geneid2)



    def gene_to_ivv(self, gene1, gene2):
        """ Returns source baits and preyIDs for interaction
        between gene1 and gene 2 """

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
        """ Returns baits which detected gene1 and gene2.
        """

        baits1 = self.gene2baits(gene1)
        preys2 = self.gene2preys(gene2)

        baits2 = self.bait_info.gene2bait(gene2)
        preys1 = self.prey_info.gene2prey(gene1)

#        print "Preprocessing..."
        
        s_baits_prey1 = []
        for prey1 in preys1:        
            s_baits_prey1.append(self.prey_info.bait_ID(prey1))

        s_baits_prey2 = []
        for prey2 in preys2:        
            s_baits_prey2.append(self.prey_info.bait_ID(prey2))

#        print "Preprocessing finished."

        source_bait = Usefuls.NonRedSet.NonRedSet()

#        print "Possible baits for", gene1, "is", baits1
#        print "Possible preys for", gene2, "is", preys2

        common1 = Usefuls.Usefuls1.common(baits1, s_baits_prey2)

#        print "First check finished."

#        print "Possible baits for", gene2, "is", baits2
#        print "Possible preys for", gene1, "is", preys1

        common2 = Usefuls.Usefuls1.common(baits2, s_baits_prey1)

#        print "Second check finished."

        common = Usefuls.Usefuls1.common(s_baits_prey1, s_baits_prey2)

        for c in common1 + common2 + common:
            source_bait.append(c)

#        print "Third check finished."
        
	return source_bait.ret_set()


    def gene_to_ivv_common_bait_descr(self, gene1, gene2):
        """ Returns baits which detected gene1 and gene2.
        """

        source = []

        baits1 = self.gene2baits(gene1)
        baits1_rec = Usefuls.Usefuls1.list_to_dict(baits1)

        preys2 = self.gene2preys(gene2)
        preys2_rec = self.prey_info.group_preys_by_bait(preys2)

        baits2 = self.bait_info.gene2bait(gene2)
        baits2_rec = Usefuls.Usefuls1.list_to_dict(baits2)
        
        preys1 = self.prey_info.gene2prey(gene1)
        preys1_rec = self.prey_info.group_preys_by_bait(preys1)

        for sbait in preys2_rec.keys():
            if baits1_rec.has_key(sbait):
                source.append(("BP", sbait, preys2_rec[ sbait ]))

        for sbait in preys1_rec.keys():
            if baits2_rec.has_key(sbait):
                source.append(("PB", preys1_rec[ sbait ], sbait))

        for sbait in preys1_rec.keys():
            if preys2_rec.has_key(sbait):
                source.append(("PP",
                               preys1_rec[ sbait ],
                               preys2_rec[ sbait ],
                               sbait))

	return source_ivv(gene1, gene2, source)
	
    def all_pair(self):
	return self.geneid_pair

class source_ivv:
    def __init__(self, geneid1, geneid2, source_list):
	self.geneid1 = geneid1
	self.geneid2 = geneid2
	self.source_list = source_list

    def geneid(self):
        return (self.geneid1, self.geneid2)
    
    def Bait_Prey(self):
        """ Format: [ (Source-Bait1, [ Prey1, Prey2, ... ]),
                      (Source-Bait2, [ Prey4, Prey5, Prey6, ... ]), ...]
                      """
	ret = []
	for source in self.source_list:
            if source[0] == "BP":
		ret.append((source[1], source[2]))
	return ret

    def Prey_Bait(self):
        """ Format: [ ([ Prey1, Prey2, ... ], Source-Bait1),
                      ([ Prey3, Prey4, Prey5, ... ], Source-Bait2), ... ]
                      """
        
	ret = []
	for source in self.source_list:
	    if source[0] == "PB":
		ret.append((source[1], source[2]))
	return ret

    def Prey_Prey(self):
        """ Format: [ ([ Prey1, Prey2, ... ], [ Prey3, ... ], Source-Bait1),
                      ([ Prey4, Prey5, ... ], [ Prey6, ... ], Source-Bait2)]
                      """

	ret = []
	for source in self.source_list:
	    if source[0] == "PP":
		ret.append((source[1], source[2], source[3]))
	return ret

    def Bait_Prey_preys(self):
        
	ret = []
	for source in self.source_list:
            if source[0] == "BP":
		ret += source[2]
	return ret

def test():

    ivv_info_file = "/pub/IVV_data/IVV7.3/ivv_human7.3_info"
    
    print "Reading IVV info..."
    ivv_info = IVV_info.IVV_info(ivv_info_file, "PITX2")
    
    print "IVV -> Gene Calculation..."
    ivv_gene = IVV_Gene(ivv_info, mode="M")
    ivv_gene.ivv_to_gene()
    
    #    print ivv_gene.all_pair()
    print "Search..."
    #    print ivv_gene.gene_to_ivv(`2353`, `1740`)
    #    print ivv_gene.gene_to_ivv(`1740`, `2353`)
    
    #    source = ivv_gene.gene_to_ivv_common_bait_descr('23287', `282470`)
    source = ivv_gene.gene_to_ivv_common_bait_descr('2308', '57703')

    print "Bait-Prey"
    print source.Bait_Prey()
    print "Prey-Bait"
    print source.Prey_Bait()
    print "Prey-Prey"
    print source.Prey_Prey()
    print "Bait-Prey-preys"
    print source.Bait_Prey_preys()
    #    print ivv_gene.gene_to_ivv_common_bait_descr(`2353`, `3725`)
    print ivv_gene.get_matrix()

if __name__ == "__main__":
   test() 
        
