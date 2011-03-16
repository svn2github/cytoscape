#!/usr/bin/python

import sys
import Hash_filt

class IVV_quality:
    def __init__(self, prey_info, exp_header):
	self.prey_info = prey_info
	self.exp_header = exp_header

    def get_qual(self, preyID, key):
	prey_qual = self.prey_info.val(preyID).split("\t")
	exp = prey_qual[ 3 ]
	header = self.exp_header.val(exp).split("\t")
	idx = header.index(key)
	val = prey_qual[ idx + 6 ]
	return val

    def bait_ID(self, preyID):
	prey_qual = self.prey_info.val(preyID).split("\t")
        return prey_qual[2]


if __name__ == "__main__":
    ivv_info_file = sys.argv[1]

    prey_info = Hash_filt.Hash_filt("L")
    prey_info.set_filt([0, "[ Prey Info ]"], [8, "FOS"])
    prey_info.read_file(filename = ivv_info_file,
			Key_cols = [1],
			Val_cols = [2])
    
    exp_header = Hash_filt.Hash_filt("L")
    exp_header.set_filt([0, "[ Exp quality header ]"])
    exp_header.read_file(filename = ivv_info_file,
			Key_cols = [1],
			Val_cols = [2])
    
    ivv_qual = IVV_quality(prey_info, exp_header)
    print ivv_qual.get_qual("S050511_F12_5TH_A06.seq", "accession")
