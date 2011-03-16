#!/usr/bin/env python

import Data_Struct.NonRedSet1
from Usefuls.RangeList import RangeList
from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from IVV_Info.IVV_info1 import IVV_info
import Seq_Packages.Motif.SwissPfam as SwissPfam
from IVV_Packages.IVV_Homology.RefSeq_based_map3 import RefSeq_based_map

""" Hit_Region: General form of class which describes hit region and
associated information. In this module, hitid denotes motif name,
pos1 pos2 denotes motif position in refseq and additional_info denotes
overlap between given refseq and motif """
class Hit_Region:
    def __init__(self, hitid, pos1, pos2, additional_info = False):
	self.hitid = hitid
	self.pos1 = pos1
	self.pos2 = pos2
	self.additional_info = additional_info

    def hit_id(self):
	return self.hitid

    def hit_pos1(self):
	return self.pos1

    def hit_pos2(self):
	return self.pos2

    def info(self):
        return self.additional_info


class Motif_Sprot_Pfam_hit_info:
    def __init__(self, nucseqid, nucseqtype):
        self.nucseqid = nucseqid
        self.nucseqtype = nucseqtype
        self.motif_info = []

    def set_stdseqid(self, stdseqid):
        self.stdseqid = stdseqid

    def set_std_hit_position(self, std_hit_position):
        self.std_hit_position = std_hit_position

    def reg_motif_pos(self, motif, pos1, pos2, overlap_len):
        self.motif_info.append(Hit_Region(motif, pos1, pos2, overlap_len))

    def get_stdseqid(self):
        return self.stdseqid

    def get_nucseqtype(self):
        return self.nucseqtype

    def get_std_hit_pos(self):
        return self.std_hit_position

    def get_motif(self):
        ret = {}
        for motif_info in self.motif_info:
            motif, pos, overlap = motif_info
            if flag:
                ret[ motif ] = ""
        return ret.keys()

    def get_motif_descr(self):
        return self.motif_info


class Motif_Sprot_Pfam_info_Prey:
    def __init__(self, ivv_info, swiss_pfam_info, refseq_based_map):
        self._ivv_info = ivv_info
        self.motif_set = swiss_pfam_info
        self.refseq_based_map = refseq_based_map
	self.motif_to_seqid = False

    def motif_info_descr(self, seqid):

        rbmap = self.refseq_based_map
        m_set = self.motif_set

        if self._ivv_info.ID_Type(seqid) == "Prey":
            m_info = Motif_Sprot_Pfam_hit_info(
                seqid, self._ivv_info.ID_Type(seqid))

            refseqid = rbmap.get_refseq(seqid)
            if not refseqid:
                return m_info

#            print "RefSeq ID OK"

            swissid = rbmap.get_sprot(refseqid)
            if not swissid:
                return m_info

#            print "Swiss ID OK"

            if not rbmap.region_soundness(refseqid):
                return m_info

#            print "Region sound"

            if not swissid in m_set.get_protein_IDs():
                return m_info

#            print "Motif found in Swiss"

            prey_start, prey_end = rbmap.get_refseq_pos(seqid)
            m_info.set_stdseqid(refseqid)
            m_info.set_std_hit_position((prey_start, prey_end))

            m_inf = m_set.get_motif_info(swissid)

            for motif in m_inf.get_motif():
                positions = m_inf.get_motif_pos(motif)
                for pos in positions:
                    motif_start_raw, motif_end_raw = pos
                    motif_start, motif_end = (
                        rbmap.map_prot_to_mRNA(motif_start_raw, refseqid),
                        rbmap.map_prot_to_mRNA(motif_end_raw, refseqid))
                    overlap = RangeList((prey_start, prey_end),
                                        (motif_start, motif_end)).overlap_length()
		    m_info.reg_motif_pos(motif,
					 motif_start,
					 motif_end,
					 overlap)

	    return m_info

    def motif_info(self, seqid, thres_overlap):
	""" Overlap is denoted by number of amino acids """
	thres_overlap *= 3

	m_info = self.motif_info_descr(seqid)
	ret_m_set = []
	for m_descr in m_info.get_motif_descr():
	    if m_descr.info() >= thres_overlap:
		ret_m_set.append(m_descr.hit_id())
	return ret_m_set

    def get_seqid_from_motif(self, motif, thres_overlap):
	if not self.motif_to_seqid:
	    self.motif_to_seqid = Data_Struct.NonRedSet1.NonRedSetDict()
            for prey in self._ivv_info.Prey_info().preys():
                motifs = self.motif_info(prey, thres_overlap)
                for mtf in motifs:
                    self.motif_to_seqid.append_Dict(mtf, prey)

        if self.motif_to_seqid.has_key(motif):
            return self.motif_to_seqid.ret_set_Dict(motif)
        else:
            return []


class Motif_Sprot_Pfam_info_Bait:
    def __init__(self, ivv_info, swiss_pfam_info, bait_to_swiss):
        self._ivv_info = ivv_info
        self.motif_set = swiss_pfam_info
        self.bait_to_swiss = bait_to_swiss
	self.motif_to_seqid = False

    def motif_info_descr(self, seqid):

        m_set = self.motif_set

        if self._ivv_info.ID_Type(seqid) == "Bait":
            m_info = Motif_Sprot_Pfam_hit_info(
                seqid, self._ivv_info.ID_Type(seqid))

            swissid = self.bait_to_swiss.subject_ID(seqid)
            if not swissid:
                return m_info
            if not swissid in m_set.get_protein_IDs():
                return m_info

            m_info.set_stdseqid(swissid)

            bait_start = self.bait_to_swiss.subject_start(seqid)
            bait_end   = self.bait_to_swiss.subject_end(seqid)

            m_info.set_std_hit_position((bait_start, bait_end))

            m_inf = m_set.get_motif_info(swissid)

            for motif in m_inf.get_motif():
                positions = m_inf.get_motif_pos(motif)
                for pos in positions:
                    motif_start, motif_end = pos
                    overlap = RangeList(
                        (bait_start, bait_end),
                        (motif_start, motif_end)).overlap_length()
		    m_info.reg_motif_pos(motif,
					 motif_start,
					 motif_end,
					 overlap)


            return m_info

    def motif_info(self, seqid, thres_overlap):
	""" Overlap is denoted by number of amino acids """

	m_info = self.motif_info_descr(seqid)
	ret_m_set = []
	for m_descr in m_info.get_motif_descr():
	    if m_descr.info() >= thres_overlap:
		ret_m_set.append(m_descr.hit_id())
	return ret_m_set

    def get_seqid_from_motif(self, motif, thres_overlap):
	if not self.motif_to_seqid:
	    self.motif_to_seqid = Data_Struct.NonRedSet1.NonRedSetDict()
            for bait in self._ivv_info.Bait_info().baits():
                motifs = self.motif_info(bait, thres_overlap)
                for mtf in motifs:
                    self.motif_to_seqid.append_Dict(mtf, bait)

        if self.motif_to_seqid.has_key(motif):
            return self.motif_to_seqid.ret_set_Dict(motif)
        else:
            return []


if __name__ == "__main__":

    ivv_info_file = "../../IVV/ivv_human7.3_info"
    swiss_pfam_file = "../../Motifs/swisspfam_save"
    ivv_to_refseq_file = "../../IVV/ivv_human7.3_refseq_match"
    bait_to_swiss_file = "../../Homology/homol_ivv_human7.3_Bait_Sprot_simp_res_1st"
    refseq_to_sprot_file = "../../Homology/homol_ivv_human7.3_refseq_uniprot_sprot_simp_res95"


    ivv_info = IVV_info(ivv_info_file)
    swiss_pfam_info = SwissPfam.Motif_swiss_set()
    swiss_pfam_info.load_motif_info(swiss_pfam_file)

    bait_to_swiss = Homology1_descr()
    bait_to_swiss.read_homol_file(bait_to_swiss_file)

    refseq_based_map = RefSeq_based_map(ivv_info,
                                        ivv_to_refseq_file,
                                        refseq_to_sprot_file)

    motif_sprot_pfam_info_bait = Motif_Sprot_Pfam_info_Bait(
        ivv_info, swiss_pfam_info, bait_to_swiss)

    motif_sprot_pfam_info_prey = Motif_Sprot_Pfam_info_Prey(
        ivv_info, swiss_pfam_info, refseq_based_map)


    """
    print "### Bait Motifs ###"
    for bait in ivv_info.Bait_info().baits():
        motif_info = motif_sprot_pfam_info_bait.motif_info_descr(bait)
        motif_descr = motif_info.get_motif_descr()
        for md in motif_descr:
            print bait, motif_info.get_stdseqid(), motif_info.get_std_hit_pos(),md.hit_id(), md.hit_pos1(), md.hit_pos2(), md.info()
	print "Detected in", bait, ":", motif_sprot_pfam_info_bait.motif_info(bait, 5)

    print
    """

    """
    print "### Prey Motifs ###"
    for prey in ivv_info.Prey_info().preys():
        motif_info = motif_sprot_pfam_info_prey.motif_info_descr(prey)
        motif_descr = motif_info.get_motif_descr()
        for md in motif_descr:
            print prey, motif_info.get_stdseqid(), motif_info.get_std_hit_pos(),md.hit_id(), md.hit_pos1(), md.hit_pos2(), md.info()
	print "Detected in", prey, ":", motif_sprot_pfam_info_prey.motif_info(prey, 5)
        """

    prey = "T050726_H01_D24.seq"
    # prey = "T050726_MJun4_2_C07.seq"
    prey = "T050726_MJun4_2_J24.seq"
    motif_info = motif_sprot_pfam_info_prey.motif_info_descr(prey)
    motif_descr = motif_info.get_motif_descr()
    for md in motif_descr:
        print prey, motif_info.get_stdseqid(), motif_info.get_std_hit_pos(),md.hit_id(), md.hit_pos1(), md.hit_pos2(), md.info()
	print "Detected in", prey, ":", motif_sprot_pfam_info_prey.motif_info(prey, 5)

    """
    print motif_sprot_pfam_info_bait.get_seqid_from_motif(
        "bZIP_1", 5)
    print motif_sprot_pfam_info_bait.get_seqid_from_motif(
        "bZIP_2", 5)
    print motif_sprot_pfam_info_prey.get_seqid_from_motif(
        "bZIP_1", 5)
    print motif_sprot_pfam_info_prey.get_seqid_from_motif(
        "bZIP_2", 5)

    print motif_sprot_pfam_info_bait.get_seqid_from_motif(
        "XXX", 5)
    print motif_sprot_pfam_info_prey.get_seqid_from_motif(
        "XXX", 5)

    """
