#!/usr/bin/env python

import sys
from Seq_Packages.Homology.read_BLAT_psl4 import BLAT_psl
from Seq_Packages.Seq.Useful_Seq1 import read_fasta
from Usefuls.ListProc1 import list_get

probe_seq_file = sys.argv[1]
blat_res_file = sys.argv[2]
if len(sys.argv) >= 4:
    no_check = True
else:
    no_check = False

probe_seq = read_fasta(probe_seq_file)
blat_res      = BLAT_psl(blat_res_file)
blat_res.sort_by_match()

for query_id in blat_res:
    
    best_match = blat_res[query_id][0]
    block_sizes = blat_res.largest_block_sizes_sort(query_id)
    bs1 = list_get(block_sizes, 0, 0)
    bs2 = list_get(block_sizes, 1, 0)

    if ((best_match.get_matches() == 60 and bs1 == 60 and bs2 == 0)
        or no_check is True):
        print "\t".join((query_id,
                         # `best_match.get_matches()`,
                         # `bs1`,
                         # `bs2`,
                         best_match.get_subject_id(),
                         best_match.get_strand(),
                         `best_match.get_subject_start()`,
                         `best_match.get_subject_end()`,
                         probe_seq[query_id]
                         ))
