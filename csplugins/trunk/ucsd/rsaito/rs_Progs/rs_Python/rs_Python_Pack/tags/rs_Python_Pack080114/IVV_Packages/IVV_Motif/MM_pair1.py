#!/usr/bin/python

import sys
sys.path.append("../")

import IVV_info.IVV_info
import IVV_info.IVV_filter
import Motif_info1
import SwissPfam

from Homology.Homology1_descr import Homology1_descr
from Integ_class.RefSeq_based_map2 import RefSeq_based_map2
from Motif_Sprot_Pfam_info import Motif_Sprot_Pfam_info_Bait, Motif_Sprot_Pfam_info

class MM_pair:

    sep = ":-:"

    def __init__(self, ivv_info, motif_info):
        
        self.ivv_info = ivv_info
        self.motif_info = motif_info
        self.mm_pairs = {}
        self.mm_pairs_origin = {}
    
        for preyID in self.ivv_info.Prey_info().preys():
        
            baitID = self.ivv_info.Prey_info().bait_ID(preyID)
            motifs_prey = self.motif_info.get_motif(preyID, 1.0e-3)
            motifs_bait = self.motif_info.get_motif(baitID, 1.0e-3)
            mm_pairs = motif_info.get_mm_pair(preyID, baitID, 1.0e-3,
                                              MM_pair.sep)

            for mm_pair in mm_pairs:
                if mm_pair in self.mm_pairs:
                    self.mm_pairs[ mm_pair ] += 1
                    self.mm_pairs_origin[ mm_pair ].append((preyID, baitID))
                else:
                    self.mm_pairs[ mm_pair ] = 1
                    self.mm_pairs_origin[ mm_pair ] = [ (preyID, baitID) ]
 
    def get_mm_pair(self):
	mm_pairs = []
        for mm_pair in self.mm_pairs:
	    m1, m2 = mm_pair.split(MM_pair.sep)
	    known = ""
	    if self.motif_info.mmi.has_pair(m1, m2):
		known = "*"
	    mm_pairs.append((m1, m2, self.mm_pairs[ mm_pair ], known))

	return mm_pairs

    def get_origin(self, m1, m2):
        """ Order of m1 and m2 is sensitive """
        mm_pair = m1 + MM_pair.sep + m2
        return self.mm_pairs_origin[ mm_pair ]

class MM_pair_SwissPfam(MM_pair):

    def __init__(self, ivv_info, motif_info,
                 motif_sprot_pfam_info_bait,
                 motif_sprot_pfam_info):

        self.ivv_info = ivv_info
        self.motif_info = motif_info
        self.motif_spi = motif_sprot_pfam_info
        self.motif_spi_b = motif_sprot_pfam_info_bait
        self.mm_pairs = {}
        self.mm_pairs_origin = {}
    
        for preyID in self.ivv_info.Prey_info().preys():
        
            baitID = self.ivv_info.Prey_info().bait_ID(preyID)
            motifs_prey = (
                self.motif_spi.motif_info(preyID, 5).get_motif())
            motifs_bait = (
                self.motif_spi_b.motif_info(baitID, 5).get_motif())
            mm_pairs = motif_info.get_mm_pair_from_motifs(
                motifs_prey, motifs_bait, MM_pair.sep)

            for mm_pair in mm_pairs:
                if mm_pair in self.mm_pairs:
                    self.mm_pairs[ mm_pair ] += 1
                    self.mm_pairs_origin[ mm_pair ].append((preyID, baitID))
                else:
                    self.mm_pairs[ mm_pair ] = 1
                    self.mm_pairs_origin[ mm_pair ] = [ (preyID, baitID) ]
    

if __name__ == "__main__":

    import string
    
    ivv_info_file = "../../IVV/ivv_human7.3_info"
    ivv_prey_filter = "../../IVV/basic_filter_list1"
    motif_file = "../../Motifs/Pfam_ivv_human7.3_motif_info"

    swiss_pfam_file = "../../Motifs/swisspfam_save"
    ivv_to_refseq_file = "../../IVV/ivv_human7.3_refseq_match"
    bait_to_swiss_file = "../../Homology/homol_ivv_human7.3_Bait_Sprot_simp_res_1st"
    refseq_to_sprot_file = "../../Homology/homol_ivv_human7.3_refseq_uniprot_sprot_simp_res95"

    filter = IVV_info.IVV_filter.IVV_filter1()
    ivv_info = IVV_info.IVV_info.IVV_info(ivv_info_file, filter)
    motif_info = Motif_info1.Motif_info(motif_file)

    swiss_pfam_info = SwissPfam.Motif_swiss_set()
    swiss_pfam_info.load_motif_info(swiss_pfam_file)

    bait_to_swiss = Homology1_descr()
    bait_to_swiss.read_homol_file(bait_to_swiss_file)

    refseq_based_map = RefSeq_based_map2(ivv_info,
                                         ivv_to_refseq_file,
                                         refseq_to_sprot_file)

    motif_sprot_pfam_info_bait = Motif_Sprot_Pfam_info_Bait(
        ivv_info, swiss_pfam_info, bait_to_swiss)

    motif_sprot_pfam_info = Motif_Sprot_Pfam_info(
        ivv_info, swiss_pfam_info, refseq_based_map)


    mm_pairs = MM_pair_SwissPfam(ivv_info, motif_info,
                                 motif_sprot_pfam_info_bait,
                                 motif_sprot_pfam_info)

    for mm_pair in mm_pairs.get_mm_pair():
        print string.join(("[ MM pair ]",
                           mm_pair[0], mm_pair[1],
                           `mm_pair[2]`, mm_pair[3]), "\t")
        for source in mm_pairs.get_origin(mm_pair[0], mm_pair[1]):
            print string.join(("[ MM pair source ]",
                               mm_pair[0], mm_pair[1],
                               `mm_pair[2]`, mm_pair[3],
                               source[0], source[1]), "\t")





