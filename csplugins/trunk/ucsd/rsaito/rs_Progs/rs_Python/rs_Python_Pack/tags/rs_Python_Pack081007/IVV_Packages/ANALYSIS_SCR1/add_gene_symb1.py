#!/usr/bin/env python

import sys
import IVV_Packages.Integration.IVV_Global_Center as IVV_Global

gene_info       = IVV_Global.get_gene_info()

inputfile = sys.argv[1]

for line in open(inputfile, "r"):
    r = line.rstrip().split("\t")
    geneid1 = r[0]
    geneid2 = r[1]
    genesb1 = gene_info.val_force(geneid1).split("\t")[0]
    genesb2 = gene_info.val_force(geneid2).split("\t")[0]
    
    print "\t".join([geneid1, geneid2, genesb1, genesb2]) # + r[2:])
    
