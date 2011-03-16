#!/usr/bin/env python

from MultiFasta2 import MultiFasta_MEM
# from Seq_Packages.Seq_Split import Seq_Split_proc
from Useful_Seq1 import read_fasta, read_fasta2

import sys
  
import Usefuls.TmpFile as TMP
tmpseq = TMP.TmpFile_II("""
>Seq1|First The first sequence
baaaattttccccgggg@@@atcg
aaaattttccccgggg

>Seq2|Second The second sequence
bac # this is a comment
acgta

  # another comment
# comment again.

>Seq3|Third|San Mittsume

zaattccgg
aaccggtt

>Seq4|Fourth|Yon Yottsume

aattcnnn
aaccggtt

>Seq5
cc # Hi!
aattcnnn
aaccggtt

>Seq6
# This is a pen.
aattcnnn#Comment
aacc #ggtt

# Comment

atcg

>Seq7

aattcnnn
aaccggtt

>Seq8

aattcnnn
aaccggtt

>Seq9

aattcnnn
aaccggtt
aattcnnn
aaccggtt

>Seq10

aattcnnn
aaccggtt
aattcnnn
aaccggtt

""")

if len(sys.argv) <= 1:
    fastafile = tmpseq.filename()
else:
    fastafile = sys.argv[1]

#seqs = read_fasta2(fastafile, True)
# print seqs

mf = MultiFasta_MEM(fastafile, shuffle = True)
print "First seq read finished. Now going to divide."
mfs = mf.divide_to_seqs_set(4)

for mf in mfs:
    print "XXXXX"
    print mf.out_fasta_all()
print "Over."
