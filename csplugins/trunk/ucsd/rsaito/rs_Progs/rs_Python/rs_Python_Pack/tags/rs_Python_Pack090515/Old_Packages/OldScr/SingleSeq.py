#!/usr/bin/env python

class SingleSeq:
    def __init__(self, seq):
	self.seq = seq
	self.id = "NoID"

    def set_ID(self, id):
	self.id = id

    def get_seq(self):
	return self.seq

    def wash_seq(self):
	seq_tmp = self.seq
	self.seq = ""
	for c in seq_tmp:
            if c.isalpha(): self.seq += c

    def return_fasta(self, blk):
	out = ">" + self.id + "\n"
	seq = self.seq
	start = 0
	end = blk
	while start < len(seq):
	    if len(seq) < end: end = len(seq)
	    out += seq[start:end] + "\n"
	    start += blk
	    end += blk
	return out

    def return_neat(self, blk):
	seq = self.seq
	i = 0
	out = ""
	while i < len(seq):
	    out += "%4d" % (i + 1)
	    j = i
	    while j < len(seq) and j < i + blk:
		if (j - i) % 10 == 0: out += " "
		out += seq[j]
		j += 1
	    out += "\n"
	    i += blk
	return out

class SingleFasta(SingleSeq):
    def __init__(self, single_fasta_file):
	fh = open(single_fasta_file, "r")
	seq = ""
	for line in fh.readlines():
	    if line[0] == ">":
		self.set_ID(line[1:-1])
	    elif line[0] != "#":
		seq += line
	self.seq = seq
	fh.close()
	self.wash_seq()

if __name__ == "__main__":
    seq1 = SingleSeq("""
atgcatgctagctgatcgatgctagctagtcgatcgatgctagtcgatcgaaaaaaaaaaa
atgcatgctagctgatcgatgctagctagtcgatcgatgctagtcgatcgaaaaaaaaaaa
atgcatgctagctgatcgatgctagctagtcgatcgatgctagtcgatcgaaaaaaaaaaa
""")
    seq1.set_ID("TestSeq")
    seq1.wash_seq()
    print seq1.return_neat(30)
    print seq1.return_fasta(30)



