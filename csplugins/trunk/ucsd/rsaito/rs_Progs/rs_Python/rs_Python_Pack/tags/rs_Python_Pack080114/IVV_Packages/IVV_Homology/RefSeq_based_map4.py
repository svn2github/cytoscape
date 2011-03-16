#!/usr/bin/env python

from IVV_Packages.IVV_Info.IVV_info1 import IVV_info
from IVV_Packages.IVV_Info.IVV_RefSeq_match2 import IVV_RefSeq_match
from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from Seq_Packages.Homology.Homol_measure import HM
from General_Packages.Data_Struct.NonRedSet1 import NonRedSetDict

hm_thres = HM(1.0e-30, 0.90, None, 30)

class RefSeq_based_map:
    def __init__(self, ivv_info,
                 ivv_to_refseq_file, refseq_to_sprot_file):

        self.ivv_info = ivv_info

        """ Mapping information from IVV sequence to RefSeq """
        ivv_to_refseq = IVV_RefSeq_match()
        self.ivv_to_refseq = ivv_to_refseq.load_match(ivv_to_refseq_file)

        """ Mapping information from RefSeq sequence (cDNA)
        to SwissProt (protein) """
        refseq_to_sprot = HomologyDescr(refseq_to_sprot_file)
        self.refseq_to_sprot = refseq_to_sprot

    def refseq_based_clustering(self):
        """ Cluster prey sequences according to mapped RefSeq """
        self.refseq_based = NonRedSetDict()
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

        refseqids = self.ivv_to_refseq.\
                    subject_ID_hm_thres(prey, hm_thres)

        if refseqids:
            return refseqids[0]
        else:
            return False

    def get_refseq_pos(self, prey):

        refseq = self.get_refseq(prey)

        if refseq:
            pos1 = self.ivv_to_refseq.subject_start(prey, refseq)
            pos2 = self.ivv_to_refseq.subject_end(prey, refseq)
            return (pos1, pos2)
        else:
            return None

    def get_sprot(self, refseq):

        sprots = self.refseq_to_sprot.\
                 subject_ID_hm_thres(refseq, hm_thres)

        if sprots:
            return sprots[0]
        else:
            return False

    def q_len(self, refseq):

        sprot = self.get_sprot(refseq)
        if sprot:
            return self.refseq_to_sprot.query_len(refseq, sprot)
        else:
            return None

    def s_len(self, refseq):

        sprot = self.get_sprot(refseq)
        if sprot:
            return self.refseq_to_sprot.subject_len(refseq, sprot)
        else:
            return None

    def q_start(self, refseq):

        sprot = self.get_sprot(refseq)
        if sprot:
            return self.refseq_to_sprot.query_start(refseq, sprot)
        else:
            return None

    def q_end(self, refseq):

        sprot = self.get_sprot(refseq)
        if sprot:
            return self.refseq_to_sprot.query_end(refseq, sprot)
        else:
            return None

    def s_start(self, refseq):

        sprot = self.get_sprot(refseq)
        if sprot:
            return self.refseq_to_sprot.subject_start(refseq, sprot)
        else:
            return None

    def s_end(self, refseq):

        sprot = self.get_sprot(refseq)
        if sprot:
            return self.refseq_to_sprot.subject_end(refseq, sprot)
        else:
            return None

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
    import IVV_Packages.IVV_Info.IVV_filter1 as IVV_filter
    from Seq_Packages.Motif.SwissPfam import Motif_swiss_set

    from General_Packages.Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    filter = IVV_filter.IVV_filter()
    filter.set_Prey_filter_file(rsc.PreyFilter)
    ivv_info = IVV_info(rsc.IVVInfo, filter)

    swiss_pfam_info = Motif_swiss_set()
    swiss_pfam_info.load_motif_info(rsc.SwissPfam_save)

    refseq_based = RefSeq_based_map(
        ivv_info,
        rsc.HomolIVVRefSeq_Ssearch,
        rsc.HomolIVVRefSeq_Sprot)

    refseq_based.refseq_based_clustering()

    for refseqid in refseq_based.get_all_refseq():
        swissid = refseq_based.get_sprot(refseqid)
        print refseqid, swissid
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
