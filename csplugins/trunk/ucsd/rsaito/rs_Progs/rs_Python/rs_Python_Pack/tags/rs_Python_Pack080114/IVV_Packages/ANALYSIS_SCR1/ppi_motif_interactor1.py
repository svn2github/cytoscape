#!/usr/bin/env python

import sys
sys.path.append("./rsIVV_Python3")
import string
import Data_Struct.Hash
import PPI.PPi2

geneid_motif_file = "./Motifs/GeneIDPfam_list"
pubppifile = "./PPI_public/ppi_ncbi.txt"
ivvmmifile = "./mmi7.3_spoke"

geneid_motif = Data_Struct.Hash.Hash("A")
geneid_motif.read_file(geneid_motif_file, [1], [0])

pubppi = PPI.PPi2.PPi2()
pubppi.read_from_file2(pubppifile, 0, 1, "PPI")
pubppi.both_dir()

ivvmmi = PPI.PPi2.PPi2()
ivvmmi.read_from_file2(ivvmmifile, 0, 1, "MMI")
ivvmmi.both_dir()

for motif in geneid_motif.keys():
    motif_interactor = ivvmmi.interactor(motif)

    prots = geneid_motif.val(motif)
    total_interactors = 0
    total_proteins = 0
    for prot in prots:
	interactors = pubppi.interactor(prot)
	if len(interactors) == 0: continue
	total_interactors += len(interactors)
	total_proteins += 1
#	print motif, prot, interactors
    if total_proteins > 0:
	print string.join([motif, `1.0*total_interactors/total_proteins`,
			   `len(motif_interactor)`], "\t")
