#!/usr/bin/python

from General_Packages.Data_Struct.Hash2 import Hash
from  General_Packages.Data_Struct.NonRedSet1 \
     import NonRedSet, NonRedSetDict

import Prey_info1
import Bait_Control
import IVV_filter1
import random

class Prey_info_ext(Prey_info1.Prey_info):
    def __init__(self, ivv_info_file,
		 filter = IVV_filter1.IVV_filter()):

        Prey_info1.Prey_info.__init__(self, ivv_info_file, filter)

	self.prey2baitID = {}
	for prey in self.preys():
	    prey_qual = self.prey_info.val(prey).split("\t")
	    baitID = prey_qual[2]
	    self.prey2baitID[ prey ] = baitID

	self.bait_to_prey = NonRedSetDict()
	for prey in self.preys():
	    bait_ID = self.bait_ID(prey)
	    self.bait_to_prey.append_Dict(bait_ID, prey)

	self.gene_to_prey = NonRedSetDict()
	for prey in self.preys():
	    geneid = self.geneid(prey)
	    self.gene_to_prey.append_Dict(geneid, prey)

    def bait_ID(self, preyID): # Overrides the method in superclass
        return self.prey2baitID[ preyID ]

    def gene2prey(self, geneid):
        if self.gene_to_prey.has_key(geneid):
            return self.gene_to_prey.ret_set_Dict(geneid)
        else:
            return []

    def bait2prey(self, baitID):

        if self.bait_to_prey.has_key(baitID):
            return self.bait_to_prey.ret_set_Dict(baitID)
        else:
            return []

    def preys_gene_count(self, bait_ID):
        preys = self.bait2prey(bait_ID)
        counts = {}
        for prey in preys:
            gene = self.geneid(prey)
            if gene <> "":
                if gene in counts:
                    counts[ gene ] += 1
                else:
                    counts[ gene ] = 1
        return counts

    def group_preys_by_bait(self, preys = ()):
        """ Groups preys by baitID. Prey redundancies not considered."""

        if preys == ():
            preys = self.preys()

        bait2prey = {}
        for prey in preys:
            bait = self.bait_ID(prey)
            if bait2prey.has_key(bait):
                bait2prey[ bait ].append(prey)
            else:
                bait2prey[ bait ] = [ prey ]
        return bait2prey

    def shuffle_prey_bait_relations(self, iteration_limit):
        preys = self.prey2baitID.keys()

        iteration = 0
        while iteration < iteration_limit:
            rand1 = random.randint(0, len(preys) - 1)
            rand2 = random.randint(0, len(preys) - 1)
            if rand1 == rand2: continue
            prey1 = preys[rand1]
            prey2 = preys[rand2]
            old_bait_ID1 = self.prey2baitID[ prey1 ]
            old_bait_ID2 = self.prey2baitID[ prey2 ]

            if (Bait_Control.control_bait(old_bait_ID1) == False and
                Bait_Control.control_bait(old_bait_ID2) == False):
                self.prey2baitID[ prey1 ] = old_bait_ID2
                self.prey2baitID[ prey2 ] = old_bait_ID1

            iteration += 1

	self.bait_to_prey = Data_Struct.NonRedSet.NonRedSetDict()
	for prey in self.preys():
	    bait_ID = self.bait_ID(prey)
	    self.bait_to_prey.append_Dict(bait_ID, prey)




if __name__ == "__main__":

    ivv_info_file = "../IVV/ivv_human7.3_info"
    filter_file = "./test_prey_list3"

    filter = IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(filter_file)

    prey_info = Prey_info_ext1(ivv_info_file, filter)


    # for prey in prey_info.preys():
    #    print prey, prey_info.bait_ID(prey)


    print "Original"
    bait2prey = prey_info.group_preys_by_bait()
    for bait in bait2prey:
        print bait, bait2prey[bait]

    prey_info.shuffle_prey_bait_relations(1000)

    print "Shuffled"
    bait2prey = prey_info.group_preys_by_bait()
    for bait in bait2prey:
        print bait, bait2prey[bait]


    """
    print prey_info.get_qual("T051018_C1_M02.seq", "accession")
    print prey_info.bait_ID("T051018_C1_M02.seq")
    print prey_info.expno("T051018_C1_M02.seq")
    print prey_info.bait2prey("2353_all")
    print prey_info.gene2prey("2353")

    gene_count = prey_info.preys_gene_count("2353_all")
    for gene in gene_count.keys():
        print gene, gene_count[ gene ]

    prey_list = (
        "050519_m5_2_E03_m5_2_E3_019.seq",
        "T051018_B12_I19.seq",
        "T060407_C01_K11.seq",
        "T051018_C12_G07.seq",
        "T060407_C01_K11.seq")

    print prey_info.group_preys_by_bait(prey_list)
    """
