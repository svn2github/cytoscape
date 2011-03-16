#!/usr/bin/env python

import os
import tempfile

import re
non_eng_char = re.compile("[^a-zA-Z]")
nnn3 = re.compile("[nN]+$")

def read_raw_seq(filename):

    seq = ""
    fh = open(filename)
    for line_raw in fh:
        if line_raw[0] == ">":
            continue
        line = line_raw.rstrip()
        line = non_eng_char.sub("", line)
	seq += line

    fh.close()
    return seq


def make_neat(seq, blk):

    ret = ""
    i = 0
    while i < len(seq):
	ret += "%4d" % (i + 1,)
        j = i
        while j < len(seq) and j < i + blk:
            if (j - i) % 10 == 0:
                ret += " "
	    ret += seq[j]
            j += 1

	ret += "\n" # Maybe "\r" for Windows?
        i += blk


    return ret


def make_seqcr(seq, blk):

    ret = ""
    i = 0
    while i < len(seq):
        if i + blk < len(seq):
            ret += seq[i:i+blk]
	else:
            ret += seq[i:]

	ret += "\n"; # Maybe "\r" for Windows?
        i += blk

    return ret


def output_fasta(filename, outfasta, id):

    seq = read_raw_seq(filename)
    seqcr = make_seqcr(seq, 10)

    fw = open(outfasta, "w")
    fw.write(">%s\n" % (id,))
    fw.write(seqcr)
    fw.write("\n")
    fw.close()
    

def invoke_fasta_align(fasta_bin_path,
                       query_file, subj_file):
    
    query_file_fasta = tempfile.mktemp()
    out_file = tempfile.mktemp()
    output_fasta(query_file, query_file_fasta, "Yours")

    fasta_invoke = "%s -QHn -b 1 -d 1 %s %s " % (
        fasta_bin_path,
        query_file_fasta,
        subj_file)

    os.system(fasta_invoke + " > " + out_file)

    output_lines = open(out_file).readlines()
    
    os.remove(query_file_fasta)
    os.remove(out_file)

    return output_lines


def BFG_out(fasta_bin_path,
            fasty_bin_path,
            subj_fasta_CDS,
            subj_fasta_AA,
            result_dir,
            filename):

    iseqout_file  = result_dir + filename.split("/")[-1] + "_seq.txt"
    fastaout_file = result_dir + filename.split("/")[-1] + "_fasta.txt"
    fastyout_file = result_dir + filename.split("/")[-1] + "_fasty.txt"

    iseq      = read_raw_seq(filename)
    iseq      = nnn3.sub("", iseq)
    iseq_neat = make_neat(iseq, 60).lower()


    a2dseq      = read_raw_seq(subj_fasta_CDS)
    a2dseq_neat = make_neat(a2dseq, 60)
    
    a2pseq      = read_raw_seq(subj_fasta_AA)
    a2pseq_neat = make_neat(a2pseq, 60)

    fw = open(iseqout_file, "w")
    fw.write("""********************************
****  Your DNA Sequence     ****\n\n""")
    fw.write(iseq_neat)
    fw.write("\n")
    fw.write("""********************************
**** ALDH2 DNA Sequence     ****\n\n""")
    fw.write(a2dseq_neat)
    fw.write("\n")
    fw.write("""********************************
**** ALDH2 Protein Sequence ****\n\n""")
    fw.write(a2pseq_neat)
    fw.write("\n")
    fw.close()

    fastaout_lines = invoke_fasta_align(fasta_bin_path,
                                        filename, subj_fasta_CDS)

    fw = open(fastaout_file, "w")
    fw.write("""
**********************************************************
**** Alignment of your sequence and ALDH DNA sequence ****
**********************************************************

""")
    fw.write("".join(fastaout_lines) + "\n")
    fw.close()


    fastyout_lines = invoke_fasta_align(fasty_bin_path,
                                        filename, subj_fasta_AA)
    fw = open(fastyout_file, "w")
    fw.write("""
**************************************************************
**** Alignment of your sequence and ALDH Protein sequence ****
**************************************************************

""")
    fw.write("".join(fastyout_lines) + "\n")
    fw.close()    


def BFG_out2(fasta_bin_path,
             fasty_bin_path,
             subj_fasta_CDS,
             subj_fasta_AA,
             result_dir,
             filename):

    iseqout_file  = result_dir + filename.split("/")[-1] + "_seq.txt"
    fastaout_file = result_dir + filename.split("/")[-1] + "_fasta.txt"

    iseq      = read_raw_seq(filename)
    iseq      = nnn3.sub("", iseq)
    iseq_neat = make_neat(iseq, 60).lower()


    a2dseq      = read_raw_seq(subj_fasta_CDS)
    a2dseq_neat = make_neat(a2dseq, 60)
    
    a2pseq      = read_raw_seq(subj_fasta_AA)
    a2pseq_neat = make_neat(a2pseq, 60)

    fw = open(iseqout_file, "w")
    fw.write("""********************************
****  Your DNA Sequence     ****\n\n""")
    fw.write(iseq_neat)
    fw.write("\n")
    fw.write("""********************************
**** ALDH2 mRNA Sequence    ****\n\n""")
    fw.write(a2dseq_neat.replace("t", "u"))
    fw.write("\n")
    fw.write("""********************************
**** ALDH2 Protein Sequence ****\n\n""")
    fw.write(a2pseq_neat)
    fw.write("\n")
    fw.close()

    fastaout_lines = invoke_fasta_align(fasta_bin_path,
                                        filename, subj_fasta_CDS)

    fw = open(fastaout_file, "w")
    fw.write("""
****************************************************************
**** Alignment of your DNA sequence and ALDH2 mRNA sequence ****
****************************************************************
""")
    write_flag = False
    for line in fastaout_lines:
        if line.find("residues") >= 0:
            write_flag = False
        if write_flag:
            if line.startswith("ALDH2"):
                fw.write(line[:7] + line[7:].lower().replace("t", "u"))
            else:
                fw.write(line[:7] + line[7:].lower())
        if line.find("Smith-Waterman score") >= 0:
            write_flag = True
    # fw.close()


    fastyout_lines = invoke_fasta_align(fasty_bin_path,
                                        filename, subj_fasta_AA)
    # fw = open(fastyout_file, "w")
    fw.write("""
*******************************************************************
**** Alignment of your DNA sequence and ALDH2 Protein sequence ****
*******************************************************************
""")
    write_flag = False
    for line in fastyout_lines:
        if line.find("residues") >= 0:
            write_flag = False
        if write_flag:
            fw.write(line)
        if line.find("Smith-Waterman score") >= 0:
            write_flag = True
    fw.close()

def BFG_out3(fasta_bin_path,
             fasty_bin_path,
             subj_fasta_CDS,
             subj_fasta_AA,
             result_dir,
             filename):

    iseqout_file  = result_dir + filename.split("/")[-1] + "_seq.txt"
    fastaout_file = result_dir + filename.split("/")[-1] + "_fasta.txt"

    iseq      = read_raw_seq(filename)
    iseq      = nnn3.sub("", iseq)
    iseq_neat = make_neat(iseq, 60).lower()


    a2dseq      = read_raw_seq(subj_fasta_CDS)
    a2dseq_neat = make_neat(a2dseq, 60)
    
    a2pseq      = read_raw_seq(subj_fasta_AA)
    a2pseq_neat = make_neat(a2pseq, 60)

    fw = open(iseqout_file, "w")
    fw.write("""***********************************************************
**** Your DNA Sequence                                 ****\n\n""")
    fw.write(iseq_neat)
    fw.write("\n")
    fw.write("""***********************************************************
**** ALDH2 DNA Sequence (Introns removed. CDS only.)   ****\n\n""")
    fw.write(a2dseq_neat)
    fw.write("\n")
    fw.write("""***********************************************************
**** ALDH2 Protein Sequence                            ****\n\n""")
    fw.write(a2pseq_neat)
    fw.write("\n")
    fw.close()

    fastaout_lines = invoke_fasta_align(fasta_bin_path,
                                        filename, subj_fasta_CDS)

    fw = open(fastaout_file, "w")
    fw.write("""
****************************************************************
**** Alignment of your DNA sequence and ALDH2 DNA sequence  ****
****************************************************************
- Introns not removed from your DNA sequence.
- Introns removed from ALDH2 DNA sequence.
""")
    write_flag = False
    for line in fastaout_lines:
        if line.find("residues") >= 0:
            write_flag = False
        if write_flag:
            fw.write(line[:7] + line[7:].lower())
        if line.find("Smith-Waterman score") >= 0:
            write_flag = True
    # fw.close()


    fastyout_lines = invoke_fasta_align(fasty_bin_path,
                                        filename, subj_fasta_AA)
    # fw = open(fastyout_file, "w")
    fw.write("""
*******************************************************************
**** Alignment of your DNA sequence and ALDH2 Protein sequence ****
*******************************************************************
""")
    write_flag = False
    for line in fastyout_lines:
        if line.find("residues") >= 0:
            write_flag = False
        if write_flag:
            fw.write(line)
        if line.find("Smith-Waterman score") >= 0:
            write_flag = True
    fw.close()

    return (iseqout_file, fastaout_file)


if __name__ == "__main__":
    
    import sys

    fasta_bin_path = "/pub/software/fasta34/fasta34"
    fasty_bin_path = "/pub/software/fasta34/fasty34"

    subj_fasta_CDS = "../ALDH2_Seq/NM_000690.2_CDS.fna"
    subj_fasta_AA  = "../ALDH2_Seq/NM_000690.2.faa"

    result_dir = "../Results/"

    for filename in sys.argv[1:]:
        iseqout_file, fastaout_file = BFG_out3(
            fasta_bin_path,
            fasty_bin_path,
            subj_fasta_CDS,
            subj_fasta_AA,
            result_dir,
            filename)
        print '<a href="%s">Sequence info.</a>' % iseqout_file
        print '<a href="%s">Alignment info.</a>' % fastaout_file
        
