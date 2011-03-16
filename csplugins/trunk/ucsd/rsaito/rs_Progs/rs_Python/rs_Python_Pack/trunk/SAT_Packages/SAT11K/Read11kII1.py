#!/usr/bin/env python

import Data_Struct.Hash2
from SAT_Packages.SAT.SAT_simple1 import SAT, SAT_Factory, SAT_Set
from Expr_Packages.Expr_II.Transcript1 import Transcript, Transcript_Factory


def Read_SAT11KII(sat_info_file):

    okay_info_label = ("strand", "chromosome", "type",
                       "category", "length", "coding")
    
    satidid_label = ("said", "id")

    human11k_okayinfo_hash = Data_Struct.Hash2.Hash("S")
    human11k_okayinfo_hash.read_file_hd(filename = sat_info_file,
                                        Key_cols_hd = satidid_label,
                                        Val_cols_hd = okay_info_label)
    
    sats_pc_pc = SAT_Set()
    sats_pc_nc = SAT_Set()
    sats_nc_nc = SAT_Set()
    
    sats_strand = SAT_Set()
    
    for satidid in human11k_okayinfo_hash.keys():
        satid, id = satidid.split("\t")
        sat = SAT_Factory().make(satid)
        strand, chromosome, type, category, length, coding = \
                human11k_okayinfo_hash.val_force(satidid).split("\t")
        transcript = Transcript_Factory().make(id)
        transcript.set_info("coding", coding)
        if strand == "plus":
            sat.set_transcript1(transcript)
        elif strand == "minus":
            sat.set_transcript2(transcript)
        sat.set_info("type", type)
        sat.set_info("category", category)
        sat.set_info("overlap length", length)

        sats_strand.add_sat(sat)

    for sat in sats_strand.get_sats():
        t1,  t2  = sat.get_transcripts()
        cn1, cn2 = t1.get_info("coding"), t2.get_info("coding")
        if   cn1 == "PProt"   and cn2 == "PProt":
            sats_pc_pc.add_sat(sat)
        elif cn1 == "NoPProt" and cn2 == "NoPProt":
            sats_nc_nc.add_sat(sat)
        elif cn1 == "PProt"   and cn2 == "NoPProt":
            sats_pc_nc.add_sat(sat)
        elif cn1 == "NoPProt" and cn2 == "PProt":
            sat.set_transcript1(t2)
            sat.set_transcript2(t1)
            sats_pc_nc.add_sat(sat)
        else:
            raise "SAT coding error ... " + t1.get_transcriptID() + " " + t2.get_transcriptID()
    
    return sats_strand, sats_pc_pc, sats_pc_nc, sats_nc_nc

if __name__ == "__main__":
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")
    sats_strand, sats_pc_pc, sats_pc_nc, sats_nc_nc = Read_SAT11KII(rsc.human11k_okay)

    print "All transcripts:"
    counter = 1
    for sat in sats_strand.get_sats():
        t1,  t2  = sat.get_transcripts()
        cn1, cn2 = t1.get_info("coding"), t2.get_info("coding")
        print counter, t1.get_transcriptID(), t2.get_transcriptID(), cn1, cn2
        counter += 1
    print
        
    print "PC - PC transcripts:"
    counter = 1
    for sat in sats_pc_pc.get_sats():
        t1,  t2  = sat.get_transcripts()
        cn1, cn2 = t1.get_info("coding"), t2.get_info("coding")
        print counter, sat.get_satid(), t1.get_transcriptID(), t2.get_transcriptID(), cn1, cn2
        counter += 1
    print 
                                  
    print "PC - NC transcripts:"
    counter = 1
    for sat in sats_pc_nc.get_sats():
        t1,  t2  = sat.get_transcripts()
        cn1, cn2 = t1.get_info("coding"), t2.get_info("coding")
        print counter, sat.get_satid(), t1.get_transcriptID(), t2.get_transcriptID(), cn1, cn2
        counter += 1
    print 
                
    print "NC - NC transcripts:"
    counter = 1
    for sat in sats_nc_nc.get_sats():
        t1,  t2  = sat.get_transcripts()
        cn1, cn2 = t1.get_info("coding"), t2.get_info("coding")
        print counter, sat.get_satid(), t1.get_transcriptID(), t2.get_transcriptID(), cn1, cn2
        counter += 1
    print
    