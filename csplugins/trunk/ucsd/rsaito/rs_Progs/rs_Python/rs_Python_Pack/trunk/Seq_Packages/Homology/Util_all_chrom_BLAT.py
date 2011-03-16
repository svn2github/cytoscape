#!/usr/bin/env python

import os

query_file = "/Users/rsaito/UNIX/Work/Research/Antisense/Antis_analysis/ES_Neuro_AFAS/ES_Neuro_control1.fna"

blat_exec = "/usr/local/Bioinfo/blatSuite/blat"
chr_dir = "/Users/rsaito/UNIX/Work/Research/Data_Public/Genome_seq/mm9"
chr_files = ("chr1.fa",
             "chr2.fa",
             "chr3.fa",
             "chr4.fa",
             "chr5.fa",
             "chr6.fa",
             "chr7.fa",
             "chr8.fa",
             "chr9.fa",
             "chr10.fa",
             "chr11.fa",
             "chr12.fa",
             "chr13.fa",
             "chr14.fa",
             "chr15.fa",
             "chr16.fa",
             "chr17.fa",
             "chr18.fa",
             "chr19.fa",
             "chrM.fa",
             "chrX.fa",
             "chrY.fa"
             )

result_dir = "/tmp"

query_file_base = query_file.split("/")[-1]

for chr_file in chr_files:
    exec_com = "%s %s/%s %s %s/BLAT_res_%s_%s" % (
                                                 blat_exec,
                                                 chr_dir,
                                                 chr_file,
                                                 query_file,
                                                 result_dir,
                                                 chr_file,
                                                 query_file_base
                                                 )
    #print exec_com
    os.system(exec_com)
    