#!/usr/bin/python

import Bait_info1
import IVV_filter1

class Bait_info_ext(Bait_info1.Bait_info):
    def __init__(self, ivv_info_file,
		 filter = IVV_filter1.IVV_filter()):

        Bait_info1.Bait_info.__init__(self, ivv_info_file, filter)
        self.gene_to_bait = {}
	
	for bait_ID in self.baits():
	    geneid = self.geneid(bait_ID)
	    if geneid in self.gene_to_bait:
		self.gene_to_bait[ geneid ].append( bait_ID )
	    else:
		self.gene_to_bait[ geneid ] = [ bait_ID ]
        
    def gene2bait(self, geneid):
        if self.gene_to_bait.has_key(geneid):
            return self.gene_to_bait[ geneid ]
        else:
            return []


if __name__ == "__main__":
    ivv_info_file = "../IVV/ivv_human7.3_info"

    filter = IVV_filter1.IVV_filter()
    filter.set_Bait_filter(("JUN", "FOS"))
    filter.set_Bait_ID_filter(("4193_all",))
    bait_info = Bait_info_ext1(ivv_info_file, filter)

    print bait_info.baits()
    print bait_info.geneid("2353_all")
    print bait_info.genesymbol("2353_all")
    print bait_info.gene2bait("2353")
