#!/usr/bin/python

import sys
import Usefuls.NonRedSet
import Usefuls.Usefuls1
import IVV_info
import IVV_filter
import IVV_Source
import Usefuls.MultiDimDict

class IVV_Gene:
    """ This class relates gene pairs to IVV pairs and vice versa """
    def __init__(self, ivv_info, mode = "S"):
        """ If mode is "M", matrix calculation will be performed. """

       	self.mode = mode
        self.ivv_info = ivv_info
        self.bait_info = ivv_info.Bait_info()
        self.prey_info = ivv_info.Prey_info()
	self.geneid_pair = Usefuls.MultiDimDict.MultiDimDict(2, 0)
	self.geneid_pair_m = Usefuls.MultiDimDict.MultiDimDict(2, 0)
        self.reprod_thres = 1

    def set_reprod_thres(self, thres):
        """ Reproducibility threshold for registering gene pairs
        according to IVV data. """
        
        self.reprod_thres = thres

    """ The followging 4 methods can be overridden so that
    the inherited class can perform predictions of PPIs according
    to interologs """

    def bait2gene(self, bait_ID):
        """ bait ID ---> Set of Genes """
        return [ self.bait_info.geneid(bait_ID) ]

    def prey2gene(self, prey_ID):
        """ prey ID ---> Set of Genes """
        return [ self.prey_info.geneid(prey_ID) ]

    def gene2baits(self, geneid):
        """ gene ---> set of Bait IDs """
        return self.bait_info.gene2bait(geneid)

    def gene2preys(self, geneid):
        """ gene ---> set of Prey IDs """
        return self.prey_info.gene2prey(geneid)

    """ ----------------------------------------------- """

    
    def bait2gene_h(self, bait_ID):
        """ Bait ID ---> { gene1: 3, gene2: 1, gene3: 5, ... }
        where the numbers denote weights such as reproducibility """
        ret = {}
        for geneid in self.bait2gene(bait_ID):
            if geneid != "": ret[ geneid ] = 1
        return ret

    def bait2prey_gene_h(self, bait_ID):
        """ Bait ID ---> Prey IDs screened by Bait ID
        --->  { gene1: 3, gene2: 1, gene3: 5, ... }
        where the numbers denote weights such as reproducibility """

        preys = self.prey_info.bait2prey(bait_ID)
        count = Usefuls.MultiDimDict.MultiDimDict(1, 0)
        for prey in preys:
            if prey == "": continue
            for geneid in self.prey2gene(prey):
                if geneid == "": continue
                count.plus_val((geneid,), 1)
        return count.get_all_data()

    def set_pair_spoke(self, b_geneid, p_geneid, inc = 1):
        """ Counts and Records b_geneid and p_geneid to self.geneid_pair
        as bait and prey """

        self.geneid_pair.plus_val((b_geneid, p_geneid), inc)

    def set_pair_matrix(self, p_geneid1, p_geneid2, inc = 1):
        """ Counts and Records p_geneid1 and p_geneid2 to self.geneid_pair_m
        as prey1 and prey2 """

        self.geneid_pair_m.plus_val((p_geneid1, p_geneid2), inc)

    def get_spoke(self):
        return self.geneid_pair.get_all_data()

    def get_matrix(self):
        return self.geneid_pair_m.get_all_data()

    def ivv_to_gene(self):
        """ Records and counts all interactions according bait_info """
        
	for bait in self.bait_info.baits():
            b_geneids_h = self.bait2gene_h(bait)
            p_geneids_h = self.bait2prey_gene_h(bait)

#	    print "Bait is", bait
#	    print "Bait:", b_geneids_h
#	    print "Prey:", p_geneids_h

            for b_geneid in b_geneids_h.keys():
                for p_geneid in p_geneids_h.keys():
                    if (b_geneid <> p_geneid and
                        p_geneids_h[p_geneid] >= self.reprod_thres):
                        count = (b_geneids_h[ b_geneid ] *
				 p_geneids_h[ p_geneid ])
                        self.set_pair_spoke(b_geneid, p_geneid, count)
#			print "Counting", b_geneid, p_geneid, count

	if self.mode <> "M": return

	for bait in self.bait_info.baits():

	    p_geneids_h = self.bait2prey_gene_h(bait)

#	    print "Matrix!!"
#	    print "Bait is", bait
#	    print "Prey:", p_geneids_h

	    prey_geneids = p_geneids_h.keys()
	    for i in range(len(prey_geneids) - 1):
		prey_geneid1 = prey_geneids[i]
		if p_geneids_h[ prey_geneid1 ] < self.reprod_thres:
		    continue
		for j in range(i+1, len(prey_geneids)):
		    prey_geneid2 = prey_geneids[j]
		    if p_geneids_h[ prey_geneid2 ] < self.reprod_thres:
			continue

		    count = (p_geneids_h[ prey_geneid1 ] *
			     p_geneids_h[ prey_geneid2 ])
		    self.set_pair_matrix(prey_geneid1,prey_geneid2, count)
		    self.set_pair_matrix(prey_geneid2,prey_geneid1, count)
#		    print "Counting", prey_geneid1, prey_geneid2, count
#		    print "Counting", prey_geneid2, prey_geneid1, count

    def gene_to_ivv_common_bait_descr(self, gene1, gene2):
        """ Returns baits which detected gene1 and gene2 along with
        descriptions as Source_ivv class
        """

        source = IVV_Source.IVV_Source_set(gene1, gene2,
                                           self.ivv_info)

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
                source.add_Bait_Prey(sbait, preys2_rec[ sbait ])

        for sbait in preys1_rec.keys():
            if baits2_rec.has_key(sbait):
                source.add_Prey_Bait(sbait, preys1_rec[ sbait ])

        for sbait in preys1_rec.keys():
            if preys2_rec.has_key(sbait):
                source.add_Prey_Prey(sbait,
                                     preys1_rec[ sbait ],
                                     preys2_rec[ sbait ])

	return source
	
def test():
    import string

    ivv_info_file = "../IVV/ivv_human7.3_info"
    ivv_prey_filter = "./rank3_5_filter_list1"
    filter = IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)
    
#    print "Reading IVV info..."
    ivv_info = IVV_info.IVV_info(ivv_info_file, filter)
    
#    print "IVV -> Gene Calculation..."
    ivv_gene = IVV_Gene(ivv_info, mode = "S")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_gene()

    spoke = ivv_gene.get_spoke()
    for p1 in spoke:
	for p2 in spoke[p1]:
	    print string.join([p1, p2, `spoke[p1][p2]`], "\t")

    return

    matrix = ivv_gene.get_matrix()
    for p1 in matrix:
	for p2 in matrix[p1]:
	    print "Matrix", p1, p2, matrix[p1][p2]


    print "Search..."
    source = ivv_gene.gene_to_ivv_common_bait_descr('1386', '3312')

    print "Common baits"
    print source.common_baits()

    print "Common bait count"
    print source.count_common_baits()

    print "Bait-Prey"
    for src in source.Bait_Prey():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()
    print

    print "Prey-Bait"
    for src in source.Prey_Bait():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()
    print

    print "Prey-Prey"
    for src in source.Prey_Prey():
        print "Bait  :", src.get_bait()
        print "Prey 1:", src.get_preys1()
        print "Prey 2:", src.get_preys2()
    print
    
    print "Bait-Prey-preys"
    print source.Bait_Prey_preys()
    print "Bait Prey quality"
    print source.get_quals_spoke("orf")
    print source.eval_quals_spoke("orf", "0")
    print "Prey-Prey quality"
    print source.get_quals_matrix("orf")
    print source.eval_quals_matrix("orf", "0")


if __name__ == "__main__":
   test() 
        
