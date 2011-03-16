#!/usr/bin/env python

import os
import re
from string import maketrans

from Data_Struct.Dict_Ordered import Dict_Ordered

fasta_header1          = re.compile(r'^>([^ ]+)')
fasta_header2          = re.compile(r'^(\S+)')
wash_seq_match1        = re.compile(r"[^a-zA-Z]")
comment_line1          = re.compile(r'^\s*#')
comment_line3_1 = re.compile(r"^\s*#.*|\n\s*#.*")
comment_line3_2 = re.compile(r"([^#\n]+) #.*")

def reverse_complement(seq):
    cseq = seq.translate(maketrans("acgtACGT", "tgcaTGCA"))
    return cseq[::-1]

def wash_seq(seq):

    return wash_seq_match1.sub("", seq)

    """
    ret_seq = []
    for c in seq:
        if c.isalpha():
            ret_seq.append(c)
    return "".join(ret_seq)
    """

def read_fasta(filename, parseid = True):

    fh = open(filename, "r")

    # count = 0
    # seq_tmp = {}
    seq_tmp = Dict_Ordered()
    for line in fh.readlines():
        fasta_header1_match =  fasta_header1.match(line.rstrip())
        if fasta_header1_match:
            if parseid:
                id = fasta_header1_match.group(1)
            else:
                id = line.rstrip()[1:]
            seq_tmp[ id ] = []
            # count += 1
            # if count % 1000 == 0:
            #    print "Reading", id, count, "in read_fasta"

        elif (not line.isspace()) and (not comment_line1.match(line)):
            seq_tmp[ id ].append(wash_seq(line))

    # seq = {}
    seq = Dict_Ordered()
    for id in seq_tmp:
        seq[id] = "".join(seq_tmp[id])

    return seq

def read_fasta2(filename, parseid = True):
    
    seqs = Dict_Ordered()
    
    dummy = "###\n"
    filesize = os.path.getsize(filename)
    fh = open(filename, "r")
    allchars = dummy + fh.read(filesize)
    entries    = allchars.split("\n>")
    dummy    = entries.pop(0)
    
    count = 0 # For verbose output 
    for entry in entries:
        header = entry[:entry.index("\n")]
        if parseid:
            fasta_header_match = fasta_header2.match(header)
            id = fasta_header_match.group(1)
        else:
            id = header
        seqraw = entry[entry.index("\n")+1:]
        del_c1 = comment_line3_1.sub(r"", seqraw)
        del_c2 = comment_line3_2.sub(r"\1", del_c1) # raw string symbol r is essential.
        seq    = wash_seq(del_c2)
        seqs[ id ] = seq

        count += 1
        """
        if count % 1000 == 0:
            print count
            print "Header:", header
            print "Seq:", seq
        """
        
    return seqs
    

def read_fasta_ids(filename):

    fh = open(filename, "r")

    ids = []
    for line in fh.readlines():
        fasta_header1_match =  fasta_header1.match(line)
        if fasta_header1_match:
            ids.append(fasta_header1_match.group(1))
    return ids

def return_fasta(seq, header, blk = 50):
    if header:
        out = ">" + header + "\n"
    else:
        out = ""
    start = 0
    end = blk
    while start < len(seq):
        if len(seq) < end: end = len(seq)
        out += seq[start:end] + "\n"
        start += blk
        end += blk
    return out

def return_neatseq(seq, blk):
    i = 0
    out = []
    while i < len(seq):
        out.append(("%4d" % (i + 1)))
        j = i
        while j < len(seq) and j < i + blk:
            if((j - i) % 10 == 0):
               # and
               # j + 1 < len(seq) and
               # j + 1 < i + blk):
                out.append(" ")
            out.append(seq[j])
            j += 1
        out.append("\n")
        i += blk
    return "".join(out)


if __name__ == "__main__":
    
    print reverse_complement("aaaattcggg")
    print wash_seq("AAAaaa123\ntttTTT")

    import Usefuls.TmpFile
    fastafile_obj = Usefuls.TmpFile.TmpFile_II("""

>TestSeq1 This is a test.
aaaaattttt
cccccggggg
aaaaattttt
cccccggggg
aaaaattttt
cccccggggg
aaaaattttt
cccccggggg
aaaaattttt
cccccggggg
aaaaattttt
c

>TestSeq2 This is a test too.
cagctagctgatcgatgcta
cagtcagtnnngatgctaggct

>TestSeq3 This is a test too.
cagctagctgatcgatgcta
cagtcagtnnnntgctaggct

>TestSeq4 This is a test too.
cagctagctgatcgatgcta
cagtcagtcgatnnnngctaggct

   # End of a test

""")
    seqs = read_fasta(fastafile_obj.filename(), parseid = False)
    print seqs
    # print return_fasta(seqs["TestSeq1"], "TEST", 10)
    # print return_neatseq(seqs["TestSeq1"], 20)
    print read_fasta_ids(fastafile_obj.filename())
