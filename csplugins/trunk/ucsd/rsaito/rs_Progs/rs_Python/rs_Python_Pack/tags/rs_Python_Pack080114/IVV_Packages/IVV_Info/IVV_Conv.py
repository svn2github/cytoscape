#!/usr/bin/env python

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

import Data_Struct.NonRedSet1
import Usefuls.Usefuls_I
from IVV_info1 import IVV_info
from IVV_filter1 import IVV_filter
import IVV_Source
import Data_Struct.MultiDimDict1

class IVV_Conv:
    """ This class relates specific type of ID pairs (Here, referred to
    as "Converted ID pairs" to IVV pairs and vice versa """
    def __init__(self, ivv_info, mode = "S"):
        """ If mode is "M", matrix calculation will be performed. """

       	self.mode = mode
        self.ivv_info = ivv_info
        self.bait_info = ivv_info.Bait_info()
        self.prey_info = ivv_info.Prey_info()
	self.convid_pair = Data_Struct.MultiDimDict1.MultiDimDict(2, 0)
	self.convid_pair_m = Data_Struct.MultiDimDict1.MultiDimDict(2, 0)
        self.reprod_thres = 1

    def set_reprod_thres(self, thres):
        """ Reproducibility threshold for registering gene pairs
        according to IVV data. """
        # This must be related to
        # IVV_Source.get_bait_prey_reprod(self)

        self.reprod_thres = thres

    """ The followging 4 methods can be overridden so that
    the inherited class can perform predictions of PPIs according
    to interologs """

    def bait2convid(self, bait_ID):
        """ bait ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """

        return [ self.bait_info.geneid(bait_ID) ]

    def prey2convid(self, prey_ID):
        """ prey ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """

        return [ self.prey_info.geneid(prey_ID) ]

    def convid2baits(self, convid):
        """ gene ---> set of Bait IDs
        Return [] if no corresponding IDs.
        """

        return self.bait_info.gene2bait(convid)

    def convid2preys(self, convid):
        """ gene ---> set of Prey IDs
        Return [] if no corresponding IDs.
        """

        return self.prey_info.gene2prey(convid)

    """ ----------------------------------------------- """


    def bait2convid_h(self, bait_ID):
        """ Bait ID ---> { convid1: 3, convid2: 1, convid3: 5, ... }
        where the numbers denote weights such as reproducibility """
        ret = {}
        for convid in self.bait2convid(bait_ID):
            if convid != "": ret[ convid ] = 1
        return ret

    def bait2prey_convid_h(self, bait_ID):
        """ Bait ID ---> Prey IDs screened by Bait ID
        --->  { convid1: 3, convid2: 1, convid3: 5, ... }
        where the numbers denote weights such as reproducibility """

        preys = self.prey_info.bait2prey(bait_ID)
        count = Data_Struct.MultiDimDict1.MultiDimDict(1, 0)
        for prey in preys:
            if prey == "": continue
            for convid in self.prey2convid(prey):
                if convid == "": continue
                count.plus_val((convid,), 1)
        return count.get_all_data()

    def set_pair_spoke(self, b_convid, p_convid, inc = 1):
        """ Counts and Records b_convid and p_convid to self.convid_pair
        as bait and prey """

        self.convid_pair.plus_val((b_convid, p_convid), inc)

    def set_pair_matrix(self, p_convid1, p_convid2, inc = 1):
        """ Counts and Records p_convid1 and p_convid2 to self.convid_pair_m
        as prey1 and prey2 """

        self.convid_pair_m.plus_val((p_convid1, p_convid2), inc)

    def get_spoke(self):
        return self.convid_pair.get_all_data()

    def get_matrix(self):
        return self.convid_pair_m.get_all_data()

    def ivv_to_convid(self):
        """ Records and counts all interactions according bait_info """

	for bait in self.bait_info.baits():
            b_convids_h = self.bait2convid_h(bait)
            p_convids_h = self.bait2prey_convid_h(bait)

#	    print "Bait is", bait
#	    print "Bait:", b_geneids_h
#	    print "Prey:", p_geneids_h

# Notice: Homodimer allowed

            for b_convid in b_convids_h.keys():
                for p_convid in p_convids_h.keys():
                    # This must be related to
                    # IVV_Source.get_bait_prey_reprod(self)
                    if (p_convids_h[p_convid] >= self.reprod_thres):
                        count = (b_convids_h[ b_convid ] *
				 p_convids_h[ p_convid ])
                        self.set_pair_spoke(b_convid, p_convid, count)
#			print "Counting", b_geneid, p_geneid, count

	if self.mode <> "M": return

	for bait in self.bait_info.baits():

	    p_convids_h = self.bait2prey_convid_h(bait)

#	    print "Matrix!!"
#	    print "Bait is", bait
#	    print "Prey:", p_geneids_h

	    prey_convids = p_convids_h.keys()
	    for i in range(len(prey_convids) - 1):
		prey_convid1 = prey_convids[i]
		if p_convids_h[ prey_convid1 ] < self.reprod_thres:
		    continue
		for j in range(i+1, len(prey_convids)):
		    prey_convid2 = prey_convids[j]
		    if p_convids_h[ prey_convid2 ] < self.reprod_thres:
			continue

		    count = (p_convids_h[ prey_convid1 ] *
			     p_convids_h[ prey_convid2 ])
		    self.set_pair_matrix(prey_convid1,prey_convid2, count)
		    self.set_pair_matrix(prey_convid2,prey_convid1, count)
#		    print "Counting", prey_geneid1, prey_geneid2, count
#		    print "Counting", prey_geneid2, prey_geneid1, count

    def gene_to_ivv_common_bait_descr(self, convid1, convid2):
        """ Returns baits which detected convid1 and convid2 along with
        descriptions as IVV_Source_set class
        """

        source = IVV_Source.IVV_Source_set(convid1, convid2,
                                           self.ivv_info)

        baits1 = self.convid2baits(convid1)
        baits2 = self.convid2baits(convid2)
        preys2 = self.convid2preys(convid2)
        preys1 = self.convid2preys(convid1)

        baits1_rec = Usefuls.Usefuls_I.list_to_dict(baits1)
        baits2_rec = Usefuls.Usefuls_I.list_to_dict(baits2)
        preys1_rec = self.prey_info.group_preys_by_bait(preys1)
        preys2_rec = self.prey_info.group_preys_by_bait(preys2)

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
    import Usefuls.rsConfig

    rsc = Usefuls.rsConfig.RSC("../../../rsIVV_Config")

    ivv_info_file = rsc.IVVInfo
    ivv_prey_filter = rsc.PreyFilter
    filter = IVV_filter1.IVV_filter()
    filter.set_Bait_filter(("FOS", "JUN", "ATF2"))

    sys.stderr.write("Reading IVV information...\n")
    ivv_info = IVV_Info.IVV_info(ivv_info_file, filter)

    sys.stderr.write("IVV -> Gene Calculation...\n")
    ivv_gene = IVV_Conv(ivv_info, mode = "S")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_convid()

    """
    spoke = ivv_gene.get_spoke()
    for p1 in spoke:
	for p2 in spoke[p1]:
	    print string.join([p1, p2, `spoke[p1][p2]`], "\t")

    return

    matrix = ivv_gene.get_matrix()
    for p1 in matrix:
	for p2 in matrix[p1]:
	    print "Matrix", p1, p2, matrix[p1][p2]
    """

    print "Search..."
    source = ivv_gene.gene_to_ivv_common_bait_descr('3725', '2353')

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

