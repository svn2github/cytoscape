#!/usr/bin/env python

import IVV_info
import Usefuls.Hash
import Usefuls.NonRedSet

class RefSeq_based_map1:
    def __init__(self, ivv_info, refseq_to_sprot_file):
        self.ivv_info = ivv_info

        refseq_to_sprot = Usefuls.Hash.Hash("L")
        refseq_to_sprot.read_file(filename = refseq_to_sprot_file,
                                  Key_cols = [0],
                                  Val_cols = [0])
        self.refseq_to_sprot = refseq_to_sprot

    def refseq_based_clustering(self):
        self.refseq_based = Usefuls.NonRedSet.NonRedSetDict()
        for prey in self.ivv_info.Prey_info().preys():
            refseqid = self.get_refseq(prey)
            if refseqid:
                self.refseq_based.append_Dict(refseqid, prey)

    def get_all_refseq(self):
        return self.refseq_based.keys()

    def mapped_preys(self, refseqid):
        return self.refseq_based.ret_set_Dict(refseqid)

    def get_refseq(self, prey):
        refseqid = self.ivv_info.Prey_info().get_qual_noerror(prey,
                                                              "hit_refseqid")
        if refseqid:
            return refseqid
        else:
            return False

    def get_refseq_pos(self, prey):
        prey_info = ivv_info.Prey_info()
        refseq_pos_str = prey_info.get_qual_noerror(prey, "hit_ref_position")
        pos1, pos2 = refseq_pos_str.split("..")

        return (int(pos1), int(pos2))

    def sprot_hit_info(self, refseq):
        hit_info = self.refseq_to_sprot.val_force(refseq)
        if hit_info:
            return hit_info.split("\t")
        else:
            return False

    def get_sprot(self, refseq):
        if self.sprot_hit_info(refseq):
            return self.sprot_hit_info(refseq)[1]
        else:
            return False

    def q_len(self, refseq):
        if self.sprot_hit_info(refseq):
            return int(self.sprot_hit_info(refseq)[6])
        else:
            return False

    def s_len(self, refseq):
        if self.sprot_hit_info(refseq):
            return int(self.sprot_hit_info(refseq)[7])
        else:
            return False

    def q_start(self, refseq):
        if self.sprot_hit_info(refseq):
            return int(self.sprot_hit_info(refseq)[8])
        else:
            return False

    def q_end(self, refseq):
        if self.sprot_hit_info(refseq):
            return int(self.sprot_hit_info(refseq)[9])
        else:
            return False

    def s_start(self, refseq):
        if self.sprot_hit_info(refseq):
            return int(self.sprot_hit_info(refseq)[10])
        else:
            return False

    def s_end(self, refseq):
        if self.sprot_hit_info(refseq):
            return int(self.sprot_hit_info(refseq)[11])
        else:
            return False

    def region_soundness(self, refseq):
        mRNA_length = self.q_end(refseq) - self.q_start(refseq) + 1
        prot_length = self.s_end(refseq) - self.s_start(refseq) + 1

        if mRNA_length == prot_length * 3:
            return True
        else:
            return False

    def map_prot_to_mRNA(self, pos, refseq):
        return (pos - 1) * 3 + self.q_start(refseq)

if __name__ == "__main__":

    import string
    import SwissPfam
    import IVV_filter

    ivv_info_file = "../IVV/ivv_human7.3_info"
    ivv_prey_filter = "../basic_filter_list1"
    refseq_to_sprot_file = "../Homology/homol_ivv_human7.3_uniprot_sprot_human_simp_res95_95"
    swiss_pfam_file = "../Motifs/swisspfam_save"
    
    filter = IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)
    ivv_info = IVV_info.IVV_info(ivv_info_file, filter)

    swiss_pfam_info = SwissPfam.Motif_swiss_set()
    swiss_pfam_info.load_motif_info(swiss_pfam_file)

    refseq_based = RefSeq_based_map1(ivv_info, refseq_to_sprot_file)
    refseq_based.refseq_based_clustering()

    for refseqid in refseq_based.get_all_refseq():
        swissid = refseq_based.get_sprot(refseqid)
        if not swissid:
            continue
        if not swissid in swiss_pfam_info.get_protein_IDs():
            continue

        motif_info = swiss_pfam_info.get_motif_info(swissid)

        print string.join((refseqid, "RefSeq", refseqid, "",
                           "1",
                           `refseq_based.q_len(refseqid)`), "\t")

        print string.join((refseqid, "Protein", swissid, "",
                           `refseq_based.q_start(refseqid)`,
                           `refseq_based.q_end(refseqid)`), "\t")

        for motif in motif_info.get_motif():
            for pos in motif_info.get_motif_pos(motif):
                pos1_raw, pos2_raw = pos
                pos1 = refseq_based.map_prot_to_mRNA(pos1_raw, refseqid)
                pos2 = refseq_based.map_prot_to_mRNA(pos2_raw, refseqid)
                print string.join((refseqid, "Motif", motif, "",
                                   `pos1`, `pos2`), "\t")

        preys = refseq_based.mapped_preys(refseqid)
        bait2prey = ivv_info.Prey_info().group_preys_by_bait(preys)
        for bait in bait2prey.keys():
            for prey in bait2prey[ bait ]:
                pos1, pos2 = refseq_based.get_refseq_pos(prey)
                print string.join((refseqid, "Prey", prey, bait,
                                   `pos1`, `pos2`), "\t")
                
                

    """
    print refseq_based.get_refseq("S20051122_A09_01_A04.seq")
    print refseq_based.get_refseq_pos("S20051122_A09_01_A04.seq")
    print refseq_based.sprot_hit_info("NM_004446.2")
    print refseq_based.get_sprot("NM_004446.2")
    print refseq_based.q_len("NM_004446.2")
    print refseq_based.s_len("NM_004446.2")
    print refseq_based.q_start("NM_004446.2")
    print refseq_based.q_end("NM_004446.2")
    print refseq_based.s_start("NM_004446.2")
    print refseq_based.s_end("NM_004446.2")
    print refseq_based.region_soundness("NM_004446.2")
    print refseq_based.map_prot_to_mRNA(1, "NM_004446.2")
    print refseq_based.map_prot_to_mRNA(1440, "NM_004446.2")
    """
