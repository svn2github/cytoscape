#!/usr/bin/env python

import sys
import string

sys.path.append("./rsIVV_Python3")

import Data_Struct.Hash
import Usefuls.Usefuls1
import Usefuls.Table_maker

import Homology
import Motif
import IVV_info
import Interolog
import IVV_Conv


ivv_info_file = "./IVV/ivv_human7.3_info"
homology_file = "./Homology/homol_ivv_human7.3.tfa-human.protein.faa_simp_res"
motif_file = "./Motifs/Pfam_ivv_human7.3_motif_info"
reported_ppi_file = "./PPI_public/ppi_ncbi.txt"
wanted_prot_file = "./PPI_public/wanted_list3.txt"
refseq2gene_file = "./Gene_info/gene2refseq_hs_mm"

sys.stderr.write("Reading ivv information...\n")
ivv_info = IVV_info.IVV_info(ivv_info_file)

sys.stderr.write("Reading homology information...\n")
homology = Homology.Homology(homology_file)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif.Motif_info(motif_file)

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Data_Struct.Hash.Hash_filt("S")
reported_ppi.read_file(filename = reported_ppi_file,
                       Key_cols = [0,1],
                       Val_cols = [2])

sys.stderr.write("Reading wanted proteins...\n")
wanted_prot = Data_Struct.Hash.Hash_filt("A")
wanted_prot.read_file(filename = wanted_prot_file,
                      Key_cols = [0],
                      Val_cols = [1])


sys.stderr.write("Reading gene info...\n")
refseq2gene = Data_Struct.Hash.Hash_filt("S")
refseq2gene.read_file(filename = refseq2gene_file,
                      Key_cols = [6],
                      Val_cols = [1])

sys.stderr.write("IVV -> Gene Calculation...\n")

ivv_gene = IVV_Conv.IVV_Conv(ivv_info, mode = "M")
ivv_gene.set_reprod_thres(2)
ivv_gene.ivv_to_convid()

matrix = ivv_gene.get_matrix()

tb = Usefuls.Table_maker.Table_row()

for p1 in matrix:
    for p2 in matrix[p1]:
        tb.append("Type", "Matrix")
        tb.append("Gene 1", p1)
        tb.append("Gene 2", p2)
        tb.append("Reprod", `matrix[p1][p2]`)
        tb.output("\t")




