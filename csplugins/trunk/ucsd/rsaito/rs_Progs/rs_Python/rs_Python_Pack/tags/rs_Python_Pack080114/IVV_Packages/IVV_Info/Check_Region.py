#!/usr/bin/env python

import sys
sys.path.append("../")

import Usefuls.RangeList
import IVV_Conv
import IVV_Source
import IVV_Info
import IVV_seq

class Check_Source_Region:
    def __init__(self, source):
        self.source = source

    def filter_region(self, q_seqid, s_seqids, min_overlap):
        
        ivv_info = source.ret_ivv_info()
        prey_info = ivv_info.Prey_info()
        query_accession = prey_info.get_qual_noerror(q_seqid,
                                                     "hit_refseqid")
        query_region_s = prey_info.get_qual_noerror(q_seqid,
                                                    "hit_ref_position")

#        print "Query Region: ", query_accession, query_region_s

        if query_accession == "": return False
        start_s, end_s = query_region_s.split("..")
        query_region = (int(start_s), int(end_s))

        hit_seqids = []
        
        for s_seqid in s_seqids:
            subject_accession = prey_info.get_qual_noerror(s_seqid,
                                                           "hit_refseqid")
            subject_region_s = prey_info.get_qual_noerror(s_seqid,
                                                          "hit_ref_position")
#            print "Subject Region:", subject_accession, subject_region_s
            if subject_accession == "": continue
            if subject_accession != query_accession: continue
            start_s, end_s = subject_region_s.split("..")
            subject_region = (int(start_s), int(end_s))
            check_region =  Usefuls.RangeList.RangeList(query_region,
                                                        subject_region)
            o1, o2 = check_region.check_overlap()
#            print "Overlap:", o2 - o1
            if o2 - o1 >= min_overlap:
                hit_seqids.append(s_seqid)

        return hit_seqids

    def filter_region2(self, q_seq, s_seqs, min_overlap):
        
        ivv_info = self.source.ret_ivv_info()
        prey_info = ivv_info.Prey_info()
        query_refseq = q_seq.hit_refseq()
        if (q_seq.get_idtype() == "Prey" and
            query_refseq == ""): return []
        query_region = q_seq.hit_refseq_region()

        hit_seqids = []
        
        for s_seq in s_seqs:

            if q_seq.geneid() != s_seq.geneid(): continue

            subject_refseq = s_seq.hit_refseq()

#            print q_seq.get_seqid(), s_seq.get_seqid()
#            print query_refseq, subject_refseq

            if s_seq.get_idtype() == "Prey" and subject_refseq == "":
                continue
            if (q_seq.get_idtype() == "Prey" and
                s_seq.get_idtype() == "Prey" and
                query_refseq != subject_refseq): continue

            """ From here, both query and subject
            are either bait or has refseq hit. """

            subject_region = s_seq.hit_refseq_region()
            check_region =  Usefuls.RangeList.RangeList(query_region,
                                                        subject_region)
            o1, o2 = check_region.check_overlap()
#            print "Query region:", query_region
#            print "Subject region:", subject_region
#            print "Overlap:", o2 - o1
            if o2 - o1 >= min_overlap:
                hit_seqids.append(s_seq.get_seqid())
#                print s_seq.get_seqid(), "added."

        return hit_seqids


    def source_scan_region(self, seq1, seq2, min_overlap):

        if (seq1.geneid() != self.source.convid1() or
            seq2.geneid() != self.source.convid2()):
#            print seq1.geneid(), self.source.convid1()
#            print seq2.geneid(), self.source.convid2()
            raise "SeqID match error"

        ivv_info = self.source.ret_ivv_info()
        
        scanned_source_list = IVV_Source.IVV_Source_set(seq1.geneid(),
                                                        seq2.geneid(),
                                                        ivv_info)
        BP = self.source.Bait_Prey()
        for source in BP:
            s_bait = source.get_bait()
            preys  = source.get_preys()

            scanned_bait = self.filter_region2(
                seq1,
                (IVV_seq.IVV_seq(s_bait, ivv_info, idtype = "Bait"),),
                min_overlap
                )
            scanned_preys = self.filter_region2(
                seq2,
                map(lambda pid: IVV_seq.IVV_seq(pid, ivv_info), preys),
                min_overlap)
            if len(scanned_bait) > 0 and len(scanned_preys) > 0:
                scanned_source_list.add_Bait_Prey(s_bait, scanned_preys)
                # scanned_bait not used????

        PB = self.source.Prey_Bait()
        for source in PB:
            preys  = source.get_preys()
            s_bait = source.get_bait()
            scanned_preys = self.filter_region2(
                seq1,
                map(lambda pid: IVV_seq.IVV_seq(pid, ivv_info), preys),
                min_overlap)
            scanned_bait = self.filter_region2(
                seq2,
                (IVV_seq.IVV_seq(s_bait, ivv_info, idtype = "Bait"),),
                min_overlap)
            if len(scanned_preys) > 0 and len(scanned_bait):
                scanned_source_list.add_Prey_Bait(s_bait, scanned_preys)

        PP = self.source.Prey_Prey()
        for source in PP:
            preys1 = source.get_preys1()
            preys2 = source.get_preys2()
            s_bait = source.get_bait()
            scanned_preys1 = self.filter_region2(
                seq1,
                map(lambda pid: IVV_seq.IVV_seq(pid, ivv_info), preys1),
                min_overlap)
            scanned_preys2 = self.filter_region2(
                seq2,
                map(lambda pid: IVV_seq.IVV_seq(pid, ivv_info), preys2),
                min_overlap)

            if len(scanned_preys1) > 0 and len(scanned_preys2) > 0:
                scanned_source_list.add_Prey_Prey(s_bait, 
						  scanned_preys1,
                                                  scanned_preys2)

        gene1 = self.source.convid1()
        gene2 = self.source.convid2()
        return scanned_source_list


if __name__ == "__main__":
    ivv_info_file = "../../IVV/ivv_human7.3_info"
    ivv_info = IVV_info.IVV_info(ivv_info_file)

    sys.stderr.write("IVV -> Gene Calculation...\n")
    ivv_gene = IVV_Conv.IVV_Conv(ivv_info)
    ivv_gene.ivv_to_convid()

    source = ivv_gene.gene_to_ivv_common_bait_descr('3725', '2353')
    region_check = Check_Source_Region(source)
    refined_source = region_check.source_scan_region(
        IVV_seq.IVV_seq("S050511_D1_5TH_C04.seq", ivv_info),
        IVV_seq.IVV_seq("2353_349..633", ivv_info, idtype = "Bait"),
        15)

#    source = ivv_gene.gene_to_ivv_common_bait_descr('351', '3725')
#    region_check = Check_Source_Region(source)
#    refined_source = region_check.source_scan_region(
#        IVV_seq.IVV_seq("T050726_MJun4_2_D17.seq", ivv_info),
#        IVV_seq.IVV_seq("3725_502..957", ivv_info, idtype = "Bait"),
#        15)

    print "Common Baits"
    print source.common_baits()

    print "Refined Common Baits"
    print refined_source.common_baits()

    print "Bait-Prey"
    for src in source.Bait_Prey():
        print src.get_bait(), src.get_preys()
        
    print "Refined Bait-Prey"
    for src in refined_source.Bait_Prey():
        print src.get_bait(), src.get_preys()
        
    print "Prey-Bait"
    for src in source.Prey_Bait():
        print src.get_preys(), src.get_bait()
    
    print "Refined Prey-Bait"
    for src in refined_source.Prey_Bait():
        print src.get_preys(), src.get_bait()

    print "Prey-Prey"
    for src in source.Prey_Prey():
        print src.get_preys1(), src.get_preys2()

    print "Refined Prey-Prey"
    for src in refined_source.Prey_Prey():
        print src.get_preys1(), src.get_preys2()

