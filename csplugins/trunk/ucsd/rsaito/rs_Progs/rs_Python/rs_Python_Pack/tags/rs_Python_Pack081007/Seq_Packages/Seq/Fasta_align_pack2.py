#!/usr/bin/env python

import sys

import tempfile
import os
import re
import string

import SingleSeq2

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsBioinfo_Config")

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
        # translated into protein.

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


class Ssearch(FASTA_pack):

    def __init__(self):
        self.set_path(rsc.SSEARCH)

    def exec_fasta(self):
        self.tmpfile = tempfile.mktemp()
        
        fasta_input = "%s -QHn -b 1 -d 1 -m 10 %s %s " % (
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
        os.remove(self.tmpfile)


class FastY(FASTA_pack):

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

    fy = FastY()
    s1 = SingleSeq2.SingleSeq("cagtcagtcgatcgacgtagtcgatgcta")
    s1.set_ID("SeqA")
    s2 = SingleSeq2.SingleSeq("ASDLDASAMDLSAMDLSMALDMSLMDLSL")
    s2.set_ID("SeqB")
    fy.set_singleseq_obj(s1, s2)
        
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

