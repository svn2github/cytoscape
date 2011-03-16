#!/usr/bin/env python

import sys
sys.path.append("../")

import PPI.PPi2
import Usefuls.MultiDimDict
import Usefuls.Hash

class MMI_Pred1:
    def __init__(self):
	self.ppi = False
	self.mmi = PPI.PPi2.PPi2()
	self.protein2motif = Usefuls.Hash.Hash("A")

    def set_PPI(self, ppi):
	self.ppi = ppi

    def set_motif_info(self, protein2motif_file):
	self.protein2motif.read_file(protein2motif_file,
				     Key_cols = [0], 
				     Val_cols = [1])
    
    def get_motif(self, protein):
	if self.protein2motif.val_force(protein) != "":
	    return self.protein2motif.val_force(protein)
	else:
	    return []

    def pred_mmi_from_ppi(self):

	mmi = Usefuls.MultiDimDict.MultiDimDict(dim = 2, init_val = 0)

	for p1, p2, val in self.ppi.get_non_redu_ppi():
	    
	    motifs1 = self.get_motif(p1)
	    motifs2 = self.get_motif(p2)

	    mmi_tmp = {}
	    for m1 in motifs1:
		for m2 in motifs2:
		    mmi_tmp[ m1 + "\t" + m2 ] = ""
		    mmi_tmp[ m2 + "\t" + m1 ] = ""
	
	    for m1m2 in mmi_tmp.keys():
		m1, m2 = m1m2.split("\t")
		mmi.plus_val((m1, m2), 1)
	    
	    """
	    print "**** MMI prediction for", p1, "and", p2
	    print "Motif 1:", motifs1 
	    print "Motif 2:", motifs2 
	    print "MMI    :", mmi_tmp.keys()
	    print "bZIP   :", mmi.get_val(("bZIP_1", "bZIP_1"))
	    """

	self.mmi.read_dict(mmi.get_all_data())

    def get_mmi_val(self, m1, m2):
	if self.mmi.get_ppi_val(m1, m2) != False:
	    return self.mmi.get_ppi_val(m1, m2)
	else:
	    return 0

    def get_all_mmi(self):
	return self.mmi.get_all_ppi()


if __name__ == "__main__":

    ppifile = "../../PPI_Public/ppi_ncbi.txt"
    motiffile = "../../Motifs/GeneIDPfam_list"

    ppi = PPI.PPi2.PPi2()
    ppi.read_from_file2(ppifile, 0, 1, "")

    mmi = MMI_Pred1()
    mmi.set_PPI(ppi)
    mmi.set_motif_info(motiffile)
    mmi.pred_mmi_from_ppi()

#    for m1, m2, val in mmi.get_all_mmi():
#	print m1, m2, val

    print mmi.get_mmi_val("bZIP_1", "bZIP_2")
