#!/usr/bin/python

import sys
sys.path.append("../")

import Usefuls.Hash

class ppi(Usefuls.Hash.Hash):
    def __init__(self):
	self.__set_data("", "", initialize = True)
        self.val_type = "A"
	self.verbose = False

    def both_dir(self):
        work_hash = {}
        for pair in self.keys():
            p1, p2 = pair.split("\t")
            vals = self.val(pair)
            for val in vals:
                p12 = p1 + "\t" + p2
                p21 = p2 + "\t" + p1
                if p12 in work_hash:
                    work_hash[ p12 ][val] = ""
                    work_hash[ p21 ][val] = ""
                else:
                    work_hash[ p12 ] = { val: "" }
                    work_hash[ p21 ] = { val: "" }

        
    
