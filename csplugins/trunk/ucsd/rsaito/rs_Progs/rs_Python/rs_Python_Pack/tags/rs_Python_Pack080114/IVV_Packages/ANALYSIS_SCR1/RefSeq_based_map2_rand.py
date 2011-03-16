#!/usr/bin/env python

import sys
sys.path.append("../")

import random
import IVV_info.IVV_info
import IVV_info.IVV_RefSeq_match
import Homology.Homology1_descr
import Data_Struct.NonRedSet
from Integ_class.RefSeq_based_map2 import RefSeq_based_map2

class RefSeq_based_map2_rand(RefSeq_based_map2):

    def get_refseq_pos(self, prey):

        pos1 = self.ivv_to_refseq.subject_start(prey)
        pos2 = self.ivv_to_refseq.subject_end(prey)
        len_ivv = pos2 - pos1 + 1

        refseqid = self.get_refseq(prey)
        prot_pos1 = self.q_start(refseqid)
        prot_pos2 = self.q_end(refseqid)
        len_prot = prot_pos2 - prot_pos1

        pos1_rand = prot_pos1 + random.randint(0, len_prot - len_ivv)
        pos2_rand = pos1_rand + len_ivv - 1

	"""
	print "CDS   ", prot_pos1, prot_pos2
	print "Actual", pos1, pos2
	print "Random", pos1_rand, pos2_rand
        """

#        return (pos1_rand, pos2_rand)
	return (pos1, pos2)

if __name__ == "__main__":

    import string
    import IVV_info.IVV_filter
    import Motif.SwissPfam
    from Motif.Motif_Sprot_Pfam_info import Motif_Sprot_Pfam_info_Prey

    ivv_info_file = "../../IVV/ivv_human7.3_info"
    ivv_prey_filter = "../../IVV/basic_filter_list1"
    ivv_to_refseq_file = "../../IVV/ivv_human7.3_refseq_match"
    refseq_to_sprot_file = "../../Homology/homol_ivv_human7.3_refseq_uniprot_sprot_simp_res95"
    swiss_pfam_file = "../../Motifs/swisspfam_save"

    filter = IVV_info.IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)
    ivv_info = IVV_info.IVV_info.IVV_info(ivv_info_file, filter)

    swiss_pfam_info = Motif.SwissPfam.Motif_swiss_set()
    swiss_pfam_info.load_motif_info(swiss_pfam_file)

    refseq_based = RefSeq_based_map2_rand(
        ivv_info,
        ivv_to_refseq_file,
        refseq_to_sprot_file)

    refseq_based.refseq_based_clustering()

    motif_sprot_pfam_info_prey = Motif_Sprot_Pfam_info_Prey(
        ivv_info, swiss_pfam_info, refseq_based)

    while True:
	count = 0
	for prey in ivv_info.Prey_info().preys():
	    m = motif_sprot_pfam_info_prey.motif_info(prey, 5)
	    if m:
		# print count, prey, m
		count += 1
	print count
	"""
	fh = open("log", "a")
	fh.write(`count` + "\n")
	fh.close()
	"""
#        motif_info = motif_sprot_pfam_info_prey.motif_info_descr(prey)
#        motif_descr = motif_info.get_motif_descr()
#        for md in motif_descr:
#            print prey, motif_info.get_stdseqid(), motif_info.get_std_hit_pos(),md.hit_id(), md.hit_pos1(), md.hit_pos2(), md.info()
#	print "Detected in", prey, ":", motif_sprot_pfam_info_prey.motif_info(prey, 5)

