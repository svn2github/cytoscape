#!/usr/bin/env python

import sys
import SingleSeq2
import Fasta_align_pack2
import MultiFasta2
import Seq_Packages.Motif.MotifDescr1 as MotifDescr
from Usefuls.Instance_check import instance_class_check

GAPM   = "-"
NOBASE = " "
ANNOT  = "@"

class Pair_Align:
    def __init__(self, main_name, each_name, seq_main, seq_each):
        self.main_name = main_name
        self.each_name = each_name
        self.seq_main = seq_main
        self.seq_each = seq_each

        self.seq_main_match = []
        # Stores matches of bases in each sequence to main sequence
        
        self.seq_main_ins = []
        # Stores insertions of bases in each sequence compared
        # to main sequence. Bases inserted before position i
        # in main sequence are stored in position i of seq_main_ins.
        # Therefore,
        # length of seq_main_ins = length of main sequence + 1
        
        for i in range(len(seq_main) + 1):
            self.seq_main_ins.append([])

        self.aligned = False

    def align(self, align_main, align_each, start_pos = None):
        # Frame shift in main sequence not allowed.

        # align_main and align_each must be the same length
        if len(align_main) != len(align_each):
            raise "Sequence length mismatch."
        if len(align_main) == 0:
            return

        main_seq_p = start_pos

        for i in range(start_pos):
            self.seq_main_match.append(NOBASE)

        for i in range(len(align_main)):
            if align_main[i] == GAPM:
                self.seq_main_ins[main_seq_p].append(align_each[i])
            else:
                self.seq_main_match.append(align_each[i])
                main_seq_p += 1

        self.start_pos = start_pos
        self.end_pos   = main_seq_p

        for i in range(main_seq_p, len(self.seq_main)):
            self.seq_main_match.append(NOBASE)

        self.aligned = True

    def get_main_name(self):
        return self.main_name

    def get_each_name(self):
        return self.each_name

    def get_seq_main(self):
        return self.seq_main

    def get_seq_each(self):
        return self.seq_each

    def get_seq_main_match(self, pos):
        return self.seq_main_match[pos]

    def get_seq_main_ins(self, pos):
        return self.seq_main_ins[pos]

    def get_start_pos(self):
        return self.start_pos

    def get_end_pos(self):
        return self.end_pos

    def is_aligned(self):
        return self.aligned
    

class Align_MultiPair:

    def __init__(self, main_name, seq_main):
        self.main_name = main_name
        self.seq_main = seq_main
        self.each_align = []

    def len_seq_main(self):
        return len(self.seq_main)

    def align(self, each_name, seq_each,
              align_main, align_each, start_pos = None):

        # align_main and align_each must be the same length
        # print self.main_name
        # print each_name
        # print "Main", align_main
        # print "Each", align_each
        if len(align_main) != len(align_each):
            raise "Sequence length mismatch."

        pa = Pair_Align(self.main_name, each_name,
                        self.seq_main, seq_each)
        pa.align(align_main, align_each, start_pos)
        self.each_align.append(pa)


    def multialign(self):

        multial = [""] * len(self.each_align)
        multial_main = ""

        # Alignments up to the end of main sequence.
        for i in range(self.len_seq_main()):
            max_gap = self.longest_ins(i)
            multial_main += GAPM * max_gap
            multial_main += self.seq_main[i]
            
            for j in range(len(multial)):
                each_a = self.each_align[j]
                if (each_a.is_aligned() and
                    i >= each_a.get_start_pos() and
                    i <= each_a.get_end_pos()):
                    multial[j] += GAPM * (
                        self.longest_ins(i) -
                        len(each_a.get_seq_main_ins(i)))
                    multial[j] += "".join(each_a.get_seq_main_ins(i))
                    multial[j] += each_a.get_seq_main_match(i)
                else:
                    multial[j] += NOBASE * self.longest_ins(i) + NOBASE

        # Alignments beyond the end of main sequence.
        multial_main += GAPM * self.longest_ins(self.len_seq_main())
        for j in range(len(multial)):
            each_a = self.each_align[j]
            if (each_a.is_aligned() and
                each_a.get_end_pos() == self.len_seq_main()):
                multial[j] += "".join(each_a.get_seq_main_ins(
                    self.len_seq_main()))
                multial[j] += GAPM * (
                    self.longest_ins(self.len_seq_main()) -
                    len(each_a.get_seq_main_ins(self.len_seq_main())))
            else:
                multial[j] += (NOBASE * self.longest_ins(self.len_seq_main())
                               + NOBASE)

        return [ multial_main ] + multial 

    def disp_multialign(self, width):

        align = self.multialign()
        align_start_pos = 0
        align_end_pos = len(align[0])

        out = ""
        cur_pos = 0

        while align_start_pos < align_end_pos:
            if align_start_pos + width <= align_end_pos:
                align_stop_pos = align_start_pos + width
            else:
                align_stop_pos = align_end_pos

            for i in range(len(align)):
                if i == 0:
                    seqname = self.main_name
                    if len(seqname) > 25: seqname = seqname[0:25]
                    out += " " * 25 + "  " + `cur_pos + 1` + "\n"
                    out += "%-25s: %s" % \
                        (seqname,
                         align[i][align_start_pos:align_stop_pos]) + "\n"
                    for c in align[i][align_start_pos:align_stop_pos]:
                        if c.isalpha():
                            cur_pos += 1

                else:
                    seqname = self.each_align[i-1].each_name
                    if len(seqname) > 25: seqname = seqname[0:25]
                    out += "%-25s: %s" % \
                        (seqname,
                         align[i][align_start_pos:align_stop_pos]) + "\n"
            out += "\n"
            align_start_pos += width
        
        return out

    def longest_ins(self, pos):
        longest = 0
        for each in self.each_align:
            if len(each.get_seq_main_ins(pos)) > longest:
                longest = len(each.get_seq_main_ins(pos))
        return longest

    def annotate(self, annot, positions):
        
        annot_seq_a = [ NOBASE ] * len(self.seq_main)

        for pos in positions:
            start, end = pos
            for i in range(start, end + 1):
                annot_seq_a[i] = ANNOT
        
        annot_seq = "".join(annot_seq_a)
        self.align(annot, annot_seq, self.seq_main, annot_seq, 0)


class Align_MultiPair_obj_FastY:
    def __init__(self, main_fasta):
        if not isinstance(main_fasta, SingleSeq2.SingleFasta):
            raise "Instance type mismatch"

        self.main_fasta = main_fasta
        self.align_multipair = Align_MultiPair(
            main_fasta.get_ID(),
            main_fasta.get_singleseq().get_seq())

    def annot_motif(self, motifdescr):
        instance_class_check(motifdescr, MotifDescr.MotifDescr)
        # if motifdescr.get_protein_ID() != self.main_name:
        #    raise "Protein ID mismatch"
        
        for each_motif in motifdescr.get_motif():
            annot_pos = []
            for position in motifdescr.get_motif_pos(each_motif):
                pos1, pos2 = position
                pos1 -= 1; pos2 -= 1
                annot_pos.append((pos1, pos2))
            self.align_multipair.annotate(each_motif, annot_pos)

    def align(self, each_fasta):
        falign = Fasta_align_pack2.FastY()
        falign.set_fasta_obj(each_fasta, self.main_fasta)
        falign.exec_fasta()

        align_each, align_main = falign.get_alignment()
        self.align_multipair.align(
            each_fasta.get_ID(),
            each_fasta.get_singleseq().get_seq(),
            align_main, 
            align_each,
            falign.s_start() - 1)
                     
    def disp_multialign(self, width):
        return self.align_multipair.disp_multialign(width)


def Align_MultiFasta(main_multifasta, each_multifasta,
                     main_id, each_ids):
    if not isinstance(main_multifasta, MultiFasta2.MultiFasta):
        raise "Instance type mismatch"
    if not isinstance(each_multifasta, MultiFasta2.MultiFasta):
        raise "Instance type mismatch"

    main_fasta = main_multifasta.get_singlefasta(main_id)
    main_fasta.set_ID(main_id)
    multia = Align_MultiPair_obj_FastY(main_fasta)

    for each_id in each_ids:
        each_fasta = each_multifasta.get_singlefasta(each_id)
        each_fasta.set_ID(each_id)
        multia.align(each_fasta)

    print multia.disp_multialign(50)
    

def ret_Align_MultiFasta_motif(main_multifasta, each_multifasta,
                               main_id, each_ids, motifdescr):
    instance_class_check(main_multifasta, MultiFasta2.MultiFasta)
    instance_class_check(each_multifasta, MultiFasta2.MultiFasta)
    instance_class_check(motifdescr, MotifDescr.MotifDescr)

    main_fasta = main_multifasta.get_singlefasta(main_id)
    if not main_fasta:
        sys.stderr.write("Warning: Sequence " + main_id + " not found.\n")
        return "\n<< Sequence %s not found.>>\n" % main_id

    main_fasta.set_ID(main_id)
    multia = Align_MultiPair_obj_FastY(main_fasta)
    
    multia.annot_motif(motifdescr)

    for each_id in each_ids:
        each_fasta = each_multifasta.get_singlefasta(each_id)
        if each_fasta:
            each_fasta.set_ID(each_id)
            multia.align(each_fasta)
        else:
            sys.stderr.write("Warning: Sequence " + each_id + " not found.\n")
    return multia.disp_multialign(50)

def Align_MultiFasta_motif(main_multifasta, each_multifasta,
                           main_id, each_ids, motifdescr):
    
    print ret_Align_MultiFasta_motif(main_multifasta, each_multifasta,
                                     main_id, each_ids, motifdescr)


if __name__ == "__main__":

    """
    pa1 = Pair_Align("Main", "Each", 
                    "ATCGTGAG",
                    "CCAGAATGAGAA")
    pa1.align("--ATCG--TGAG--",
              "CCA--GAATGTGAA", 0)
    
    print pa1.seq_main_match
    print pa1.get_seq_main_match(3)
    print pa1.seq_main_ins
    print pa1.get_seq_main_ins(4)

    pa2 = Pair_Align("Main", "Each", 
                     "ATCGTGAG",
                     "CCAGAATGAGAA")
    pa2.align("--G--TGAG--",
              "CAGATTGAGAA", 3)
    
    print pa2.seq_main_match
    print pa2.seq_main_ins

    motifdescr = MotifDescr.MotifDescr()
    motifdescr.set_Protein_ID("Lazy")
    motifdescr.set_motif("Motif1", 3, 4)
    motifdescr.set_motif("Motif3", 3, 5)
    motifdescr.set_motif("Motif1", 8, 10)

    multia = Align_MultiPair(main_name = "Main", 
                             seq_main = "AAAAACCCCCGGGGGTTTTT")
    multia.annotate("REGION", ((5, 7), (11, 13)))
    
    multia.align(each_name = "test 1",
                 seq_each   = "Lazy",
                 align_main = "AAAAA--CCCCCGGGGGTTTTT-",
                 align_each = "-AAAAGG---CCGGGGGTTTTTC",
                 start_pos = 0)
    multia.align(each_name = "test 2",
                 seq_each   = "Lazy",
                 align_main = "-AAAAACCCCCGG---GGGTTTTT--",
                 align_each = "RAAAAACCCCCGGXXXGGGTTTTTCC",
                 start_pos = 0)
    multia.align(each_name = "test 3",
                 seq_each   = "Lazy",
                 align_main = "AAAACCCCCGG--GGGTTT",
                 align_each = "AAAACCCCCGGXXGGGTTT",
                 start_pos = 1)
    multia.align(each_name = "test 4",
                 seq_each   = "Lazy",
                 align_main = "",
                 align_each = "")

    print multia.disp_multialign(20)

    multia2 = Align_MultiPair(main_name = "Main", 
                              seq_main = "AAAAACCCCCGGGGGTTTTT")
    multia2.align(each_name = "test 1",
                  seq_each   = "Lazy",
                  align_main = "",
                  align_each = "")
    multia2.align(each_name = "test 2",
                  seq_each   = "Lazy",
                  align_main = "",
                  align_each = "")
    print multia2.disp_multialign(20)

    

    #
    #

    p0 = SingleSeq2.SingleFasta("100816397.tfa")
    p0.set_ID("100816397")
    p1 = SingleSeq2.SingleFasta("T060117_F1_J04.tfa")
    p1.set_ID("T060117_F1_J04")
    p2 = SingleSeq2.SingleFasta("T050726_H01_C16.tfa")
    p2.set_ID("T050726_H01_C16")
    p3 = SingleSeq2.SingleFasta("S20051122_F09_03_F07.tfa")
    p3.set_ID("S20051122_F09_03_F07")
    
    motifdescr = MotifDescr.MotifDescr()
    motifdescr.set_Protein_ID("Lazy")
    motifdescr.set_motif("Motif1", 3, 4)
    motifdescr.set_motif("Motif3", 3, 5)
    motifdescr.set_motif("Motif1", 8, 10)

    multia = Align_MultiPair_obj_FastY(p0)
    multia.annot_motif(motifdescr)
    multia.align(p1)
    multia.align(p2)
    multia.align(p3)
    print multia.disp_multialign(30)

    """

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

    motifdescr = MotifDescr.MotifDescr()
    motifdescr.set_Protein_ID("Lazy")
    motifdescr.set_motif("Motif1", 3, 4)
    motifdescr.set_motif("Motif3", 3, 5)
    motifdescr.set_motif("Motif1", 8, 10)

    ivv_mf = MultiFasta2.MultiFasta(rsc.IVVSeq)
    ref_mf = MultiFasta2.MultiFasta(rsc.RefSeq_Prot_Human)

    Align_MultiFasta_motif(ref_mf, ivv_mf,
                           "100816397",
                           ("T060117_F1_J04.seq",
                            "T050726_H01_C16.seq",
                            "S20051122_F09_03_F07.seq"),
                           motifdescr)



