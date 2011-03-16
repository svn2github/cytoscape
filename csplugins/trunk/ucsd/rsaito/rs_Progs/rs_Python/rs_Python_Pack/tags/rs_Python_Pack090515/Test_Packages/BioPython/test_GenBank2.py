#!/usr/bin/env python

from Bio import SeqIO

handle = open("ls_orchid.gbk")

for seq_record in SeqIO.parse(handle, "genbank") :
    print seq_record.id, seq_record.annotations["organism"]

handle.close()
