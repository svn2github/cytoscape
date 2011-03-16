#!/usr/bin/python

import sys
sys.path.append("../rsIVV_Python3")

import string
import Usefuls.Hash
import Usefuls.Sheet_Analysis
import Usefuls.MultiDimDict

swiss_to_geneid_file = "../Gene_info/loc2acc_hs_mm"
swiss_pfam_file = "SwissPfam_list"
swiss_to_geneid = Usefuls.Hash.Hash_filt("S")
swiss_to_geneid.set_filt([3, "p"])
swiss_to_geneid.read_file(swiss_to_geneid_file, [4], [0])

geneid_pfam = Usefuls.MultiDimDict.MultiDimDict(2, "No-value")

class swissprot_pfam_analysis(Usefuls.Sheet_Analysis.Sheet_Analysis):
    def analyze(self, r):
	global swiss_to_geneid
	global geneid_pfam
	pid = r[0]
	motif = r[1]
	if swiss_to_geneid.has_key(pid):
#	    print string.join([swiss_to_geneid.val(pid), motif], "\t")
	    geneid_pfam.set_val((swiss_to_geneid.val(pid), motif), "")

spa = swissprot_pfam_analysis(swiss_pfam_file, "\t")
spa.readlines()

all = geneid_pfam.get_all_data()
for geneid in all:
    for motif in all[geneid]:
	print string.join([geneid, motif], "\t")
