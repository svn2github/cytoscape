#!/usr/bin/python

from General_Packages.Data_Struct.Hash2 import Hash
import IVV_filter1
import Bait_Control

class Bait_info:
    def __init__(self, ivv_info_file,
		 filter = IVV_filter1.IVV_filter()):

        self.ivv_info_file = ivv_info_file
	self.filter = filter

        self.bait_info = Hash("S")
        self.bait_info.set_filt([0, "[ Bait Info ]"])


	""" Only Bait_filter and Bait_ID_filter function """

	b_filter = []
	if filter.get_Bait_filter():
	    b_filter += map(lambda bait: [ 3, bait ],
			    filter.get_Bait_filter())
            self.bait_info.set_filt_OR(*b_filter)

	if filter.get_Bait_ID_filter():
	    b_filter += map(lambda baitID: [ 1, baitID ],
			    filter.get_Bait_ID_filter())
            self.bait_info.set_filt_OR(*b_filter)

        self.bait_info.read_file(filename = ivv_info_file,
                                 Key_cols = [1],
                                 Val_cols = [2,3])

    def baits(self):
        return self.bait_info.keys()

    def geneid(self, baitID):
        return self.bait_info.val(baitID).split("\t")[0]

    def genesymbol(self, baitID):
        return self.bait_info.val(baitID).split("\t")[1]

    def bait_type(self, baitID):
        if not baitID in self.bait_info.keys():
            return False
        elif Bait_Control.control_bait(baitID) != False:
            return Bait_Control.control_bait(baitID)
        else:
            return "Bait"

    def bait_is_protein(self, baitID):
        if self.bait_type(baitID) == "Bait":
            return True
        else:
            return False


if __name__ == "__main__":
    ivv_info_file = "../IVV/ivv_human7.3_info"

    filter = IVV_filter.IVV_filter1()
    filter.set_Bait_filter(("JUN", "FOS"))
    filter.set_Bait_ID_filter(("4193_all",))
    bait_info = Bait_info(ivv_info_file, filter)

    print bait_info.baits()
    print bait_info.geneid("2353_all")
    print bait_info.genesymbol("2353_all")
    print bait_info.bait_type("2353_all")
    print bait_info.bait_is_protein("2353_all")
    print bait_info.bait_is_protein("Rintaro")




