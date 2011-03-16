#!/usr/bin/env python

import string

from IVV_Packages.IVV_Info.IVV_info1 import IVV_info
# from IVV_Packages.IVV_Info.IVV_RefSeq_match2 import IVV_RefSeq_match
from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from Seq_Packages.Homology.Homol_measure import HM
from Seq_Packages.Motif.SwissPfam import Motif_swiss_set
from General_Packages.Data_Struct.Hash2 import Hash
from General_Packages.Data_Struct.NonRedSet1 import NonRedSetDict

import Seq_Packages.Map.PosMap1 as PosMap

hm_thres = HM(1.0e-30, 0.90, None, 30)

class RefSeq_based_map:
    def __init__(self,
                 ivv_to_refseq_file,
                 refseq_to_sprot_file,
                 swisspfam_save_file):

        """ Mapping information from IVV sequence to RefSeq """
        self.ivv_to_refseq = Hash("S")
        self.ivv_to_refseq.read_file_hd(
            ivv_to_refseq_file,
            Key_cols_hd = [ "Prey Seq. No." ],
            Val_cols_hd = [ "Prey GenBank Acc. No.",
                            "Prey Region Start (AA)",
                            "Prey Region End (AA)" ])

        self.refseq_CDS = Hash("S")
        self.refseq_CDS.read_file_hd(
            ivv_to_refseq_file,
            Key_cols_hd = [ "Prey GenBank Acc. No." ],
            Val_cols_hd = [ "Prey GenBank CDS Start",
                            "Prey GenBank CDS End" ])  
        
        """ Mapping information from RefSeq sequence (cDNA)
        to SwissProt (protein) """

        self.refseq_to_sprot = HomologyDescr(refseq_to_sprot_file)

        """ Information on UniProt protein and related motifs """

        self.swiss_pfam_info = Motif_swiss_set()
        self.swiss_pfam_info.load_motif_info(swisspfam_save_file)


    def get_refseq_CDS(self, refseqid):
        cds_start = \
                  self.refseq_CDS.val_accord_hd(refseqid,
                                                "Prey GenBank CDS Start")
        cds_end = \
                self.refseq_CDS.val_accord_hd(refseqid,
                                              "Prey GenBank CDS End")
        return string.atoi(cds_start), string.atoi(cds_end)


    def get_refseq(self, preyid):

        refseq_info = self.ivv_to_refseq.val_accord_hd(
            preyid, "Prey GenBank Acc. No.")
        
        if refseq_info:
            return refseq_info.split("\t")[0]
        else:
            return False

    def get_refseq_pos(self, preyid):
        """ Position of prey sequence in full-length refseq """

        prey_start_aa = string.atoi(self.ivv_to_refseq.val_accord_hd(
            preyid, "Prey Region Start (AA)"))
        prey_end_aa = string.atoi(self.ivv_to_refseq.val_accord_hd(
            preyid, "Prey Region End (AA)"))
        refseqid = self.get_refseq(preyid)
        
        cds_start, cds_end = self.get_refseq_CDS(refseqid)
        return (refseqid,
                cds_start + (prey_start_aa - 1)*3,
                cds_start + (prey_end_aa   - 1)*3)


    def refseq_based_clustering(self):
        """ Cluster prey sequences according to mapped RefSeq """
        self.refseq_based = NonRedSetDict()
        for prey in self.ivv_to_refseq.keys():
            refseqid = self.get_refseq(prey)
            if refseqid:
                self.refseq_based.append_Dict(refseqid, prey)

        self.refseq_based_map = {}
        for refseqid in self.refseq_based.keys():
            refseq = PosMap.PosMap_Obj("RefSeq\t" + refseqid)
            self.refseq_based_map[ refseqid ] = refseq

            cds_start, cds_end = self.get_refseq_CDS(refseqid)
            refseq.take(PosMap.PosMap_Obj("CDS\tCDS"),
                        cds_start, cds_end, None, None, False)

            for preyid in self.refseq_based.ret_set_Dict(refseqid):
                refseqid_, prey_start, prey_end = self.get_refseq_pos(preyid)
                refseq.take(PosMap.PosMap_Obj("Prey\t" + preyid),
                            prey_start, prey_end, 
                            None, None, False)

            swissid = self.get_sprot(refseqid)
            # print refseqid, swissid
            if swissid and self.region_soundness(refseqid):
                sprot = PosMap.PosMap_Obj_Prot_to_DNA("Protein\t" + swissid)
                refseq.take(sprot,
                            self.q_start(refseqid), self.q_end(refseqid),
                            self.s_start(refseqid), self.s_end(refseqid),
                            True)

                motif_info = self.swiss_pfam_info.get_motif_info(swissid)
                if motif_info:
                    for motif in motif_info.get_motif():
                        for pos in motif_info.get_motif_pos(motif):
                            pos1, pos2 = pos
                            sprot.take(PosMap.PosMap_Obj("Motif\t" + motif),
                                       pos1, pos2, None, None, False)
                        

    def get_refseq_based_map(self, refseqid):
        return self.refseq_based_map[ refseqid ]

                
    def get_all_refseq(self):
        """ Returns all RefSeqs. refseq_based_clustering() must be
        pre-called. """
        return self.refseq_based.keys()


    def mapped_preys(self, refseqid):
        return self.refseq_based.ret_set_Dict(refseqid)


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
    from General_Packages.Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    preyid_to_bait = Hash("S")
    preyid_to_bait.read_file_hd(rsc.GNP_IVV,
                                [ "Prey Seq. No." ],
                                [ "Bait Symbol",
                                  "Bait Region Start (AA)",
                                  "Bait Region End (AA)" ])

    refseq_based = RefSeq_based_map(
        rsc.GNP_IVV,
        rsc.HomolIVVRefSeqGNP_Sprot,
        rsc.SwissPfam_save
        )

    refseq_based.refseq_based_clustering()
    for refseqid in refseq_based.get_all_refseq():
        print "\t".join((refseqid, "RefSeq", refseqid, "", `1`,
                         `refseq_based.q_len(refseqid)`))
        for map_info in \
            refseq_based.get_refseq_based_map(refseqid).mapped_positions():
            mobj, start, end = map_info
            otype, id = mobj.get_ID().split("\t")
            bait = ""
            if otype == "Prey":
                bait = (preyid_to_bait.val_accord_hd(id, "Bait Symbol")
                        + "_" +
                        preyid_to_bait.val_accord_hd(id,
                                                     "Bait Region Start (AA)")
                        + ".." +
                        preyid_to_bait.val_accord_hd(id,
                                                     "Bait Region End (AA)"))
            
            print "\t".join((refseqid, otype, id, bait, `start`, `end`))
        
    """

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
