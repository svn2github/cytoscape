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
import IVV_filter
import Interolog
import IVV_Conv


ivv_info_file = "./IVV/ivv_human7.3_info"
ivv_prey_filter = "./basic_filter_list1"
homology_file = "./Homology/homol_ivv_human7.3.tfa-human.protein.faa_simp_res"
motif_file = "./Motifs/Pfam_ivv_human7.3_motif_info"
reported_ppi_file = "./PPI_public/ppi_ncbi.txt"
wanted_prot_file = "./PPI_public/wanted_list3.txt"
refseq2gene_file = "./Gene_info/gene2refseq_hs_mm"

sys.stderr.write("Reading ivv information...\n")
filter = IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)
ivv_info = IVV_info.IVV_info(ivv_info_file, filter)

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
ivv_gene.set_reprod_thres(1)
ivv_gene.ivv_to_convid()

matrix = ivv_gene.get_matrix()

tb = Usefuls.Table_maker.Table_row()

done = {}

for geneid1 in matrix:
    for geneid2 in matrix[geneid1]:

	pair12 = geneid1 + "\t" + geneid2
	pair21 = geneid2 + "\t" + geneid1

	if pair12 in done or pair21 in done:
	    continue
	done[ pair12 ] = ""
	done[ pair21 ] = ""

        tb.append("Type", "Matrix")
        tb.append("Gene 1", geneid1)
        tb.append("Gene 2", geneid2)
        tb.append("Reprod", `matrix[geneid1][geneid2]`)

        source = ivv_gene.gene_to_ivv_common_bait_descr(geneid1, geneid2)

        common_baits = source.common_baits()

        tb.append("Common baits", string.join(common_baits, ","))

        n_detec_b = source.count_common_baits()
        tb.append("Num. detec. baits", `n_detec_b`)

        ppi_ncbi = reported_ppi.pair_val(geneid1, geneid2)
        if not ppi_ncbi: ppi_ncbi = ""
        tb.append("PubMed", ppi_ncbi)

        m1, m2, mmi = source.get_motifs(motif_info, mode = "M")
        tb.append("MMI", string.join(mmi, ","))

        itr = source.get_interolog(homology, refseq2gene, reported_ppi,
                                   mode = "M")
        interolog = ""
        if itr != False:
            interolog = itr.get_best_ref()
        tb.append("Interolog", interolog)

        tb.output("\t")




