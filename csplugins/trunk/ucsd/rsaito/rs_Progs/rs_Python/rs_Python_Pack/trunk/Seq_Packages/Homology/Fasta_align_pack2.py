#!/usr/bin/env python

import sys

import tempfile
import os
import re
import string

import Seq_Packages.Seq.SingleSeq2 as SingleSeq2

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsBioinfo_Config")

from Seq_Packages.Seq.Transl1 import codon_table_std

""" FASTA memo

-  ... Ignore
\  ... One skip from current position
/  ... Two skips from current position?

*** Insertion ***

\  <-- Single nucleotide insertion
-

G/ <-- Two nucleotide insertion (Similar to single nucleotide deletion)
-- 

M  <-- Codon insertion
-

*** Deletion ***

/  <-- Single nucleotide deletion
-

-\ <-- Two nucleotide deletion (Similar to single nucleotide insertion)
D-

-  <-- Codon deletion
D

"""

class FASTA_pack_error:
    def __init__(self, prog_name, singleseq_obj1, singleseq_obj2, msg = "FASTA alignment failed."):
        self.prog_name = prog_name
        self.singleseq_obj1 = singleseq_obj1
        self.singleseq_obj2 = singleseq_obj2
        self.msg = msg
        
    def __str__(self):
        return "[Error] %s Prog:%s  Seq#1:%s (%s...)  Seq#2:%s (%s...)" % (self.msg, self.prog_name,
                                                                           self.singleseq_obj1.get_ID(),
                                                                           self.singleseq_obj1.get_seq()[:10],
                                                                           self.singleseq_obj2.get_ID(),
                                                                           self.singleseq_obj2.get_seq()[:10])


class FASTA_pack:

    def set_fasta_file(self, fasta_file1, fasta_file2):
        self.fasta1 = SingleSeq2.SingleFasta(fasta_file1)
        self.fasta2 = SingleSeq2.SingleFasta(fasta_file2)
        self.result = False

    def set_fasta_obj(self, fasta1, fasta2):
        if not isinstance(fasta1, SingleSeq2.SingleFasta):
            raise "Instance type mismatch."
        if not isinstance(fasta2, SingleSeq2.SingleFasta):
            raise "Instance type mismatch."
        
        self.fasta1 = fasta1
        self.fasta2 = fasta2

    def set_singleseq_obj(self, singleseq1, singleseq2):
        if not isinstance(singleseq1, SingleSeq2.SingleSeq):
            raise "Instance type mismatch."
        if not isinstance(singleseq2, SingleSeq2.SingleSeq):
            raise "Instance type mismatch."

        self.set_fasta_obj(singleseq1.return_fasta_obj(),
                           singleseq2.return_fasta_obj())


    def set_path(self, path):
        self.exec_path = path

    def get_fasta1(self):
        return self.fasta1
    
    def get_fasta2(self):
        return self.fasta2

    def parse_result(self):
        fh = open(self.tmpfile, "r")
        result = {}

        pat_result = re.compile("^; (\S+): +(\S+)")

        for line in fh:
            parsing = pat_result.search(line)
            if parsing:
                rkey = parsing.group(1)
                rval = parsing.group(2)
                if rkey in result:
                    result[ rkey ].append(rval)
                else:
                    result[ rkey ] = [ rval ]

        score_flag = False
        for rkey in result:
            try:
                if rkey.index("score") >= 0:
                    score_flag = True
                    break
            except ValueError: 
                pass

        if score_flag is False:
            raise FASTA_pack_error, FASTA_pack_error(self.exec_path,
                                                     self.get_fasta1().get_singleseq(),
                                                     self.get_fasta2().get_singleseq())

        self.result = result        
        return result

    def get_disp_alignment(self):
        fh = open(self.tmpfile, "r")
        disp_align1 = ""
        disp_align2 = ""

        for line in fh:
            if line[:19] == "; al_display_start:":
                break;
        for line in fh:
            if not line[0].isalpha() and not line[0] in r'*\/-':
                break;
            else:
                disp_align1 += line.rstrip()

        for line in fh:
            if line[:19] == "; al_display_start:":
                break;
        for line in fh:
            if not line[0].isalpha() and not line[0] in r'*\/-' :
                break;
            else:
                disp_align2 += line.rstrip()

        return disp_align1, disp_align2

    def get_alignment(self):
        # disp_align2 must NOT be protein sequence automatically
        # translated from nucleic acid sequence.

        disp_align1, disp_align2 = self.get_disp_alignment()

        # Gap should not exist in unaligned region.
        al_s_start = self.s_start() - self.s_disp_start()
        if self.q_start() < self.q_end():
            al_q_start = self.q_start() - self.q_disp_start()
        else:
            al_q_start = self.q_disp_start() - self.q_start()

        seq_pos = self.s_start()
        for i in range(al_s_start, len(disp_align2)):
            if seq_pos >= self.s_end():
                break
            if disp_align2[i].isalpha():
                seq_pos += 1

        al_s_end = i
        al_len = al_s_end - al_s_start + 1
        
        return(disp_align1[al_q_start:al_q_start + al_len],
               disp_align2[al_s_start:al_s_start + al_len])

    def display_raw_result(self):

        for line in open(self.tmpfile, "r"):
            print line.rstrip()        



class Ssearch(FASTA_pack):

    def __init__(self):
        self.set_path(rsc.SSEARCH)

    def exec_fasta(self):
        self.tmpfile = tempfile.mktemp()
        
        fasta_input = "%s -QH -b 1 -d 1 -m 10 %s %s " % ( # -QHn nucleotide option
            self.exec_path,
            self.fasta1.get_fasta_file(),
            self.fasta2.get_fasta_file())
        os.system(fasta_input + " > " + self.tmpfile)
        self.parse_result()

    def eval(self):
        return string.atof(self.result[ "sw_expect" ][0])

    def ident(self):
        return string.atof(self.result[ "sw_ident" ][0])

    def similar(self):
        return string.atof(self.result[ "sw_sim" ][0])

    def overlp(self):
        return string.atoi(self.result[ "sw_overlap" ][0])

    def q_len(self):
        return string.atoi(self.result[ "sq_len" ][0])

    def s_len(self):
        return string.atoi(self.result[ "sq_len" ][1])

    def q_start(self):
        return string.atoi(self.result[ "al_start" ][0])

    def q_end(self):
        return string.atoi(self.result[ "al_stop" ][0])

    def s_start(self):
        return string.atoi(self.result[ "al_start" ][1])

    def s_end(self):
        return string.atoi(self.result[ "al_stop" ][1])

    def q_disp_start(self):
        return string.atoi(self.result[ "al_display_start" ][0])

    def s_disp_start(self):
        return string.atoi(self.result[ "al_display_start" ][1])

    def __del__(self):
        if 'tmpfile' in vars(self):
            os.remove(self.tmpfile)

    def residue_residue_corresp(self):

        INS = "Ins"
        # Del = "Del" # Unused.
        
        disp_align1, disp_align2 = self.get_disp_alignment()
        q_start = self.q_start()
        s_start = self.s_start()
        
        qseq = self.get_fasta1().get_singleseq().get_seq()
        sseq = self.get_fasta2().get_singleseq().get_seq()
        
        qseq_p = q_start - 1
        sseq_p = s_start - 1
        aln_p  = 0
        
        qseq_to_sseq = {}
        sseq_to_qseq = {}
        
        invalid_bases = 0
        
        while aln_p < len(disp_align1):
            if disp_align1[aln_p].isalpha() and disp_align2[aln_p].isalpha():
                qseq_to_sseq[ qseq_p ] = sseq_p
                sseq_to_qseq[ sseq_p ] = qseq_p                
                sseq_p += 1
                qseq_p += 1

            elif disp_align1[aln_p] == '-' and disp_align2[aln_p].isalpha():
                sseq_to_qseq[ sseq_p ] = INS
                sseq_p += 1

      
            elif disp_align1[aln_p].isalpha() and disp_align2[aln_p] == "-":
                qseq_to_sseq[ qseq_p ] = INS
                qseq_p += 1      
            
            aln_p += 1
                

        # print qseq_to_sseq
        # print sseq_to_qseq
            
        
        import Pair_residue_to_residue1
        
        return Pair_residue_to_residue1.Pair_res_to_res1(qseq,
                                                         sseq,
                                                         qseq_to_sseq,
                                                         sseq_to_qseq)


class FastY(FASTA_pack):
    """ Query must be nucleic acid sequence, and subject must be amino acid sequence. """

    def __init__(self):
        self.set_path(rsc.FASTY)

    def exec_fasta(self):
        self.tmpfile = tempfile.mktemp()
        
        fasta_input = "%s -QHn -b 1 -d 1 -m 10 %s %s " % (
            self.exec_path,
            self.fasta1.get_fasta_file(),
            self.fasta2.get_fasta_file())
        os.system(fasta_input + " > " + self.tmpfile)
        self.parse_result()

    def eval(self):
        return string.atof(self.result[ "fy_expect" ][0])

    def ident(self):
        return string.atof(self.result[ "sy_ident" ][0])

    def similar(self):
        return string.atof(self.result[ "sy_sim" ][0])

    def overlp(self):
        return string.atoi(self.result[ "sy_overlap" ][0])

    def q_len(self):
        return string.atoi(self.result[ "sq_len" ][0])

    def s_len(self):
        return string.atoi(self.result[ "sq_len" ][1])

    def q_start(self):
        return string.atoi(self.result[ "al_start" ][0])

    def q_end(self):
        return string.atoi(self.result[ "al_stop" ][0])

    def s_start(self):
        return string.atoi(self.result[ "al_start" ][1])

    def s_end(self):
        return string.atoi(self.result[ "al_stop" ][1])

    def q_disp_start(self):
        return string.atoi(self.result[ "al_display_start" ][0])

    def s_disp_start(self):
        return string.atoi(self.result[ "al_display_start" ][1])

    def __del__(self):
        os.remove(self.tmpfile)
        
    def codon_amino_corresp(self):

        INS = "Ins"
        # Del = "Del" # Unused.
        
        disp_align1, disp_align2 = self.get_disp_alignment()
        q_start = self.q_start()
        s_start = self.s_start()
        
        inuc_seq = self.get_fasta1().get_singleseq().get_seq()
        iamn_seq = self.get_fasta2().get_singleseq().get_seq()
        
        nuc_p = q_start - 1
        prt_p = s_start - 1
        aln_p = 0
        
        codon_to_amino = {}
        amino_to_codon = {}
        amino_to_aligned_amino = {}
        
        invalid_bases = 0
        
        while aln_p < len(disp_align1):
            if disp_align1[aln_p].isalpha() and disp_align2[aln_p].isalpha():
                if invalid_bases > 0:
                    amino_to_codon[ prt_p ] = { 0: INS }
                    invalid_bases -= 1
                else:
                    codon_to_amino[ nuc_p ] = prt_p
                    amino_to_codon[ prt_p ] = { 0: nuc_p }
                    
                if invalid_bases > 0:
                    amino_to_codon[ prt_p ][1] = INS
                    invalid_bases -= 1
                else:
                    codon_to_amino[ nuc_p+1 ]  = prt_p
                    amino_to_codon[ prt_p ][1] = nuc_p+1
                    
                if invalid_bases > 0:
                    amino_to_codon[ prt_p ][2] = INS
                    invalid_bases -= 1
                else:
                    codon_to_amino[ nuc_p+2 ]  = prt_p
                    amino_to_codon[ prt_p ][2] = nuc_p+2

                amino_to_aligned_amino[ prt_p ] = disp_align1[aln_p]
                # codon = inuc_seq[nuc_p:nuc_p+3].lower()
                # amino = iamn_seq[prt_p].upper()
                # print codon, amino, codon_table_std[codon]
                nuc_p += 3
                prt_p += 1
                
            elif disp_align1[aln_p] == '\\' and disp_align2[aln_p] == '-': # Single nucleotide insertion?
                # r"\" not allowed as \" is double quotation even in raw string mode.
                codon_to_amino[ nuc_p   ] = INS
                nuc_p += 1
            elif disp_align1[aln_p] == '/' and disp_align2[aln_p] == '-': # Two nucleotide insertion, single nucleotide deletion.
                nuc_p -= 1
                invalid_bases += 1
            elif disp_align1[aln_p].isalpha() and disp_align2[aln_p] == '-': # Codon insertion
                codon_to_amino[ nuc_p   ] = INS
                codon_to_amino[ nuc_p+1 ] = INS
                codon_to_amino[ nuc_p+2 ] = INS
                nuc_p += 3
            elif disp_align1[aln_p] == '-' and disp_align2[aln_p].isalpha(): # Codon deletion
                amino_to_codon[ prt_p ] = { 0:INS, 1:INS, 2:INS }
                amino_to_aligned_amino[ prt_p ] = disp_align1[aln_p]
                prt_p += 1
            
            aln_p += 1
                
        # print inuc_seq
        # print disp_align1
        # print disp_align2
        # print q_start, s_start
        
        import Pair_residue_to_residue1
        
        return Pair_residue_to_residue1.Pair_nuc_to_amino(inuc_seq, 
                                                          iamn_seq, 
                                                          codon_to_amino, 
                                                          amino_to_codon,
                                                          amino_to_aligned_amino)


class FastX(FASTA_pack):

    def __init__(self):
        self.set_path(rsc.FASTX)

    def exec_fasta(self):
        self.tmpfile = tempfile.mktemp()
        
        fasta_input = "%s -QHn -b 1 -d 1 -m 10 %s %s " % (
            self.exec_path,
            self.fasta1.get_fasta_file(),
            self.fasta2.get_fasta_file())
        os.system(fasta_input + " > " + self.tmpfile)
        self.parse_result()

    def eval(self):
        return string.atof(self.result[ "fx_expect" ][0])

    def ident(self):
        return string.atof(self.result[ "sx_ident" ][0])

    def similar(self):
        return string.atof(self.result[ "sx_sim" ][0])

    def overlp(self):
        return string.atoi(self.result[ "sx_overlap" ][0])

    def q_len(self):
        return string.atoi(self.result[ "sq_len" ][0])

    def s_len(self):
        return string.atoi(self.result[ "sq_len" ][1])

    def q_start(self):
        return string.atoi(self.result[ "al_start" ][0])

    def q_end(self):
        return string.atoi(self.result[ "al_stop" ][0])

    def s_start(self):
        return string.atoi(self.result[ "al_start" ][1])

    def s_end(self):
        return string.atoi(self.result[ "al_stop" ][1])

    def q_disp_start(self):
        return string.atoi(self.result[ "al_display_start" ][0])

    def s_disp_start(self):
        return string.atoi(self.result[ "al_display_start" ][1])

    def __del__(self):
        os.remove(self.tmpfile)


if __name__ == "__main__":
    """
    ss = Ssearch()
    ss.set_fasta_file1(sys.argv[1])
    ss.set_fasta_file2(sys.argv[2])
    ss.exec_fasta()
    print ss.parse_result()
    print ss.eval()
    print ss.ident(), ss.similar()
    print ss.overlp()
    print ss.q_start(), "-", ss.q_end()
    print ss.s_start(), "-", ss.s_end()
    print ss.q_disp_start()
    print ss.s_disp_start()
    print ss.get_alignment()
    """
    """
    fy = FastY()
    fy.set_fasta_file(sys.argv[1], sys.argv[2])
    fy.exec_fasta()
    print fy.parse_result()
    print fy.eval()
    print fy.ident(), fy.similar()
    print fy.overlp()
    print fy.q_start(), "-", fy.q_end()
    print fy.s_start(), "-", fy.s_end()
    print fy.q_disp_start()
    print fy.s_disp_start()
    print fy.get_alignment()
    """
    """
    fx = FastX()
    s1 = SingleSeq2.SingleSeq("agctagctagtcgatgcta") # "gcttgtgatgaattt"
    s1.set_ID("SeqA")
    s2 = SingleSeq2.SingleSeq("ASDNHHHQMAC")
    s2.set_ID("SeqB")
    fx.set_singleseq_obj(s1, s2)
        
    fx.exec_fasta()
    fx.display_raw_result()
    print fx.parse_result()
    print fx.eval()
    print fx.ident(), fx.similar()
    print fx.overlp()
    print fx.q_start(), "-", fx.q_end()
    print fx.s_start(), "-", fx.s_end()
    print fx.q_disp_start()
    print fx.s_disp_start()
    print fx.get_alignment()
    """

    """
    fy = FastY()
    s1 = SingleSeq2.SingleSeq("atg atg atg ccc ccc at ccc ccc ccc tag atg atg atg atg atg")
    #                          M   M   M   P   P      P   P   P   Y   M   M   M   M   M
    s1.set_ID("SeqA")
    s2 = SingleSeq2.SingleSeq("MMMPPPPPYMMMMMM")
    s2.set_ID("SeqB")
    fy.set_singleseq_obj(s1, s2)
        
    fy.exec_fasta()
    fy.display_raw_result()
    print fy.parse_result()
    print fy.eval()
    print fy.ident(), fy.similar()
    print fy.overlp()
    print fy.q_start(), "-", fy.q_end()
    print fy.s_start(), "-", fy.s_end()
    print fy.q_disp_start()
    print fy.s_disp_start()
    print fy.get_alignment() # Currently for non-gapped alignment only?
    print "\n".join(fy.get_disp_alignment())
    pair_nuc_prt = fy.codon_amino_corresp()
    pair_nuc_prt.investigate_each_by_amino()
    """

    ss = Ssearch()
    s1 = SingleSeq2.SingleSeq("MQHCCASDLGEDECQHPTIIHMSDMAHWMQHCCASDLGEDECQHPTIIHMSDMAHW")
    s1.set_ID("SeqA")
    s2 = SingleSeq2.SingleSeq("MQHCCASDLGEQQECQHPTIHMSDMAHWMQHCCASDLDECQHPTIIHMSDMAHW")
    s2.set_ID("SeqB")
    ss.set_singleseq_obj(s1, s2)
    ss.exec_fasta()
    ss.display_raw_result()
    print "\n".join(ss.get_disp_alignment())
    ss.residue_residue_corresp()
    
    print FASTA_pack_error("fasty", s1, s2)
    
    
    ss = Ssearch()
    s1 = SingleSeq2.SingleSeq("ASDKKKKKKKKL")
    s1.set_ID("Seq#A")
    s2 = SingleSeq2.SingleSeq("ACACACACAC")
    s2.set_ID("Seq#B")
    ss.set_singleseq_obj(s1, s2)
    try:
        ss.exec_fasta()
        ss.display_raw_result()
        print "\n".join(ss.get_disp_alignment())
        ss.residue_residue_corresp()
    except FASTA_pack_error, err:
        print err
    
   

