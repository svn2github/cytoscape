#!/usr/bin/env python

from Bio.Seq import Seq
my_seq = Seq("AGTACACTGGT")

print my_seq
print my_seq.__repr__()
print my_seq.tostring()

print my_seq.complement()
print my_seq.reverse_complement()

