#!/usr/bin/env python

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("FastaDB")
fasta_file = rsc.hg17DIR + "chr1.fa"

from Bio import SeqIO

start = 101 - 1
end   = 111 - 1

handle = open(fasta_file)
seq_record = SeqIO.parse(handle, "fasta").next()
print seq_record.id
print seq_record.seq[start:end]
print seq_record.seq[start:end].tostring() # Convert to string
print seq_record.seq[start:end].reverse_complement()
handle.close()
