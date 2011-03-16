#!/usr/bin/python

import sys
import string
import Hash

class Hash_filt(Hash.Hash):

    def __init__(self, type):
        self.filts = []
        Hash.Hash.__init__(self, type)
        
    def set_filt(self, *filts):
	self.filts = filts

    def filt_line(self, r):
	for filt in self.filts:
	    [ col, filt_key ] = filt
	    if col < len(r) and r[col] <> filt_key:
		return True
	return False


if __name__ == "__main__":
    h_test = Hash_filt("S")
    h_test.set_filt([0, "[ Prey Info ]"], [8, "FOS"])
    h_test.verbose_mode()
    h_test.read_file(filename = sys.argv[1],
		     Key_cols = [1],
		     Val_cols = [2])
    print h_test.all_data()
