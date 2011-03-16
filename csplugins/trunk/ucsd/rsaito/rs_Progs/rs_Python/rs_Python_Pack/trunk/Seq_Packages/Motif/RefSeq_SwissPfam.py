#!/usr/bin/env python

import SwissPfam
import Seq_Packages.Homology.Homology_descr4 as Homology_descr
from Seq_Packages.Homology.Homol_measure import HM
from MotifDescr1 import MotifDescr

class RefSeqProt_SwissPfam:
    def __init__(self,
                 refseq2sprot_homol_file,
                 swisspfam_save_file):
        
        self.swisspfam = SwissPfam.Motif_swiss_set()
        self.swisspfam.load_motif_info(swisspfam_save_file)

        self.refseq2sprot = Homology_descr.HomologyDescr(
            refseq2sprot_homol_file)
        
    def ret_motif_info(self, refseqid):

        sprot_ids = self.refseq2sprot.subject_ID_hm_thres(
            refseqid, HM(1.0e-3, 1, 1, 0))

        motifdescr = MotifDescr()
        motifdescr.set_Protein_ID(refseqid)
        
        for sprot_id in sprot_ids:
            r_hit_start = int(self.refseq2sprot.query_start(refseqid,
                                                            sprot_id))
            r_hit_end   = int(self.refseq2sprot.query_end(refseqid,
                                                          sprot_id))
            s_hit_start = int(self.refseq2sprot.subject_start(refseqid,
                                                              sprot_id))
            s_hit_end   = int(self.refseq2sprot.subject_end(refseqid,
                                                            sprot_id))
            
            motif_s_info = self.swisspfam.get_motif_info(sprot_id)

            if motif_s_info:
                for each_motif in motif_s_info.get_motif():
                    protid = motif_s_info.get_protein_ID()
                    for each_pos in motif_s_info.get_motif_pos(each_motif):
                        pos1, pos2 = each_pos
                        pos1_mod = r_hit_start + (pos1 - s_hit_start) # * 3
                        pos2_mod = r_hit_start + (pos2 - s_hit_start) # * 3
                        # print each_motif, pos1, pos2, pos1_mod, pos2_mod
                        # print r_hit_start, r_hit_end, s_hit_start, s_hit_end
                        if pos2_mod <= r_hit_end:
                            motifdescr.set_motif(each_motif, 
                                                 pos1_mod, pos2_mod) 
                        # pos1 ? pos2 ? 

        return motifdescr
            
    def ret_motif_info2(self, refseqid):

        sprot_ids = self.refseq2sprot.subject_ID_hm_thres(
            refseqid, HM(1.0e-3, 1, 1, 0))

        motifdescr = MotifDescr()
        motifdescr.set_Protein_ID(refseqid)
        
        for sprot_id in sprot_ids:
            r_hit_start = int(self.refseq2sprot.query_start(refseqid,
                                                            sprot_id))
            r_hit_end   = int(self.refseq2sprot.query_end(refseqid,
                                                          sprot_id))
            s_hit_start = int(self.refseq2sprot.subject_start(refseqid,
                                                              sprot_id))
            s_hit_end   = int(self.refseq2sprot.subject_end(refseqid,
                                                            sprot_id))
            
            motif_s_info = self.swisspfam.get_motif_info(sprot_id)

            if motif_s_info:
                for each_motif in motif_s_info.get_motif():
                    protid = motif_s_info.get_protein_ID()
                    for each_pos in motif_s_info.get_motif_pos(each_motif):
                        pos1, pos2 = each_pos
                        pos1_mod = r_hit_start + (pos1 - s_hit_start) # * 3
                        pos2_mod = r_hit_start + (pos2 - s_hit_start) # * 3
                        # print each_motif, pos1, pos2, pos1_mod, pos2_mod
                        # print r_hit_start, r_hit_end, s_hit_start, s_hit_end
                        if pos1_mod >= r_hit_start and pos2_mod <= r_hit_end:
                            motifdescr.set_motif(each_motif, 
                                                 pos1_mod, pos2_mod) 
                            """ This could further be modified to extract
                            domain fragments. """

        return motifdescr

if __name__ == "__main__":

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

    refseqprot_swisspfam = RefSeqProt_SwissPfam(
        rsc.HomolRefSeqProtSprot,
        rsc.SwissPfam_save)

    motifdescr =  refseqprot_swisspfam.ret_motif_info2("270288806")
    for motif in motifdescr.get_motif():
        print motif, motifdescr.get_motif_pos(motif)

    motifdescr =  refseqprot_swisspfam.ret_motif_info("25453474")
    for motif in motifdescr.get_motif():
        print motif, motifdescr.get_motif_pos(motif)
