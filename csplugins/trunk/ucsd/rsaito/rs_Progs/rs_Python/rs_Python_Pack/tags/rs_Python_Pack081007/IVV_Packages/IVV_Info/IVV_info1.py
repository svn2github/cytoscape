#!/usr/bin/python

import Bait_info_ext1
import Prey_info_ext1
import IVV_filter1
from General_Packages.Data_Struct.Hash2 import Hash

class IVV_info:
    def __init__(self, ivv_info_file,
                 filter = IVV_filter1.IVV_filter()):
	# set_Bait_ID_filter by user is invalid.

        self.prey_info = Prey_info_ext1.Prey_info_ext(ivv_info_file,
                                                      filter)


	# Only baits having corresponding preys are accepted.
	# set_Bait_ID_filter by user is invalid.
        baits = self.prey_info.group_preys_by_bait().keys()
        filter.set_Bait_ID_filter(baits)

        self.bait_info = Bait_info_ext1.Bait_info_ext(ivv_info_file,
                                                      filter)

        self.idtype = {}
        for id in self.Bait_info().baits():
            self.idtype[ id ] = "Bait"
        for id in self.Prey_info().preys():
            if id in self.idtype:
                raise "Duplicated ID " + id
            self.idtype[ id ] = "Prey"

    def Bait_info(self):
        return self.bait_info

    def Prey_info(self):
        return self.prey_info

    def ID_Type(self, id):
        if id in self.idtype:
            return self.idtype[ id ]
        else:
            return False

if __name__ == "__main__":
    from General_Packages.Usefuls.rsConfig import RSC_II

    rsc = RSC_II("rsIVV_Config")

    filter = IVV_filter1.IVV_filter()
    filter.set_Prey_filter_file(rsc.PreyFilter)
    ivv_info = IVV_info(rsc.IVVInfo, filter)

    for prey in ivv_info.Prey_info().preys():
	print ivv_info.Prey_info().bait_ID(prey), prey

    print
    print "Number of Baits:", len(ivv_info.Bait_info().baits())
    print "Number of Preys:", len(ivv_info.Prey_info().preys())

    # print ivv_info.ID_Type("4087_all")
    # print ivv_info.ID_Type("T060407_B12_F03.seq")
