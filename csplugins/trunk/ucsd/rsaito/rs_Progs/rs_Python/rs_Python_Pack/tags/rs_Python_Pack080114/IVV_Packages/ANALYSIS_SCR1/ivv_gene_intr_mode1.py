#!/usr/bin/python

import sys
import string

sys.path.append("./rsIVV_Python2")

import Data_Struct.Hash
import Usefuls.Usefuls1
import Usefuls.Table_maker

import Homology
import Motif
import IVV_info
import Interolog
import IVV_Gene

import sys

ivv_info_file = "./IVV/ivv_human7.3_info"
homology_file = "./Homology/homol_ivv_human7.3.tfa-human.protein.faa_simp_res"
motif_file = "./Motifs/Pfam_ivv_human7.3_motif_info"
reported_ppi_file = "./PPI_public/ppi_ncbi.txt"
wanted_prot_file = "./PPI_public/wanted_list3.txt"
refseq2gene_file = "./Gene_info/gene2refseq_hs_mm"

sys.stderr.write("Reading ivv information...\n")
ivv_info = IVV_info.IVV_info(ivv_info_file, "JUN", "FOS")

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
ivv_gene = IVV_Gene.IVV_Gene(ivv_info)
ivv_gene.ivv_to_gene()

while True:

    print

    geneid1 = raw_input("Input gene ID 1: ")
    geneid2 = raw_input("Input gene ID 2: ")

    if geneid1 == "" or geneid2 == "": break

    print
    print "Information for gene ID 1:", geneid1, "and gene ID 2:", geneid2

    source = ivv_gene.gene_to_ivv_common_bait_descr(geneid1,
                                                    geneid2)

    print "[ Bait ]", geneid1, "-> [ Prey ]", geneid2
    output = Usefuls.Table_maker.Table_row()
    for b_p in source.Bait_Prey():
        sbait, preys = b_p
        for prey in preys:
            output.clear()
            output.append("Bait ID for " + geneid1, sbait)
            output.append("Prey ID for " + geneid2, prey)

            output.output("\t")

    print

    print "[ Prey ]", geneid1, "-> [ Bait ]", geneid2
    output = Usefuls.Table_maker.Table_row()
    for p_b in source.Prey_Bait():
        preys, sbait = p_b
        for prey in preys:
            output.clear()
            output.append("Prey ID for " + geneid1, prey)
            output.append("Bait ID for " + geneid2, sbait)

            output.output("\t")

    print

    print "[ Prey ]", geneid1, "-> [ Prey ]", geneid2
    for p_p in source.Prey_Prey():
        preys1, preys2, sbait = p_p

        output = Usefuls.Table_maker.Table_row()
        for prey in preys1:
            output.clear()
            output.append("Prey ID (1) for " + geneid1, prey)
            output.append("Source Bait ID", sbait)

            output.output("\t")

        print

        output = Usefuls.Table_maker.Table_row()
        for prey in preys2:
            output.clear()
            output.append("Prey ID (2) for " + geneid2, prey)
            output.append("Source Bait ID", sbait)

            output.output("\t")

        print
