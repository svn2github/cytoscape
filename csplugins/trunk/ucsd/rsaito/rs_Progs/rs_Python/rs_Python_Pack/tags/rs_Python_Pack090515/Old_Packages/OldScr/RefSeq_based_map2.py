#!/usr/bin/env python

import sys
sys.path.append("../")

import IVV_info.IVV_info
import IVV_info.IVV_RefSeq_match
import Homology.Homology1_descr
import Usefuls.NonRedSet

class RefSeq_based_map2:
    def __init__(self, ivv_info,
                 ivv_to_refseq_file, refseq_to_sprot_file):
        
        self.ivv_info = ivv_info

        """ Mapping information from IVV sequence to RefSeq """
        ivv_to_refseq = IVV_info.IVV_RefSeq_match.IVV_RefSeq_match()
        self.ivv_to_refseq = ivv_to_refseq.load_match(ivv_to_refseq_file)

        """ Mapping information from RefSeq sequence (cDNA)
        to SwissProt (protein) """ 
        refseq_to_sprot = Homology.Homology1_descr.Homology1_descr("S")
        refseq_to_sprot.read_homol_file(refseq_to_sprot_file)
        self.refseq_to_sprot = refseq_to_sprot

    def refseq_based_clustering(self):
        """ Cluster prey sequences according to mapped RefSeq """
        self.refseq_based = Usefuls.NonRedSet.NonRedSetDict()
        for prey in self.ivv_info.Prey_info().preys():
            refseqid = self.get_refseq(prey)
            if refseqid:
                self.refseq_based.append_Dict(refseqid, prey)

    def get_all_refseq(self):
        """ Returns all RefSeqs. refseq_based_clustering() must be
        pre-called. """
        return self.refseq_based.keys()

    def mapped_preys(self, refseqid):
        return self.refseq_based.ret_set_Dict(refseqid)

    def get_refseq(self, prey):

        refseqid = self.ivv_to_refseq.subject_ID(prey)

        if refseqid:
            return refseqid
        else:
            return False

    def get_refseq_pos(self, prey):

        pos1 = self.ivv_to_refseq.subject_start(prey)
        pos2 = self.ivv_to_refseq.subject_end(prey)

        return (pos1, pos2)

    def get_sprot(self, refseq):

        return self.refseq_to_sprot.subject_ID(refseq)

    def q_len(self, refseq):

        return self.refseq_to_sprot.query_len(refseq)

    def s_len(self, refseq):

        return self.refseq_to_sprot.subject_len(refseq)

    def q_start(self, refseq):

        return self.refseq_to_sprot.query_start(refseq)

    def q_end(self, refseq):

        return self.refseq_to_sprot.query_end(refseq)

    def s_start(self, refseq):

        return self.refseq_to_sprot.subject_start(refseq)

    def s_end(self, refseq):

        return self.refseq_to_sprot.subject_end(refseq)

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
    import IVV_info.IVV_filter
    import Motif.SwissPfam

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

    refseq_based = RefSeq_based_map2(
        ivv_info,
        ivv_to_refseq_file,
        refseq_to_sprot_file)
    
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
