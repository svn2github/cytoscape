#!/usr/bin/python

import sys
import string

sys.path.append("./rsIVV_Python2")

import Usefuls.Hash
import Usefuls.Usefuls1
import Usefuls.Table

import Homology
import Motif
import IVV_info
import Interolog
import IVV_Gene

ivv_info_file = "./IVV/ivv_human7.2_info"
homology_file = "./Homology/homol_ivv_human7.2.tfa-human.protein.faa_simp_res"
motif_file = "./Motifs/Pfam_ivv_human7.2_motif_info"
reported_ppi_file = "./PPI_public/ppi_ncbi.txt"
wanted_prot_file = "./PPI_public/wanted_list3.txt"
refseq2gene_file = "./Gene_info/gene2refseq_hs_mm"

print "Reading ivv information..."
ivv_info = IVV_info.IVV_info(ivv_info_file)

# print "Reading homology information..."
# homology = Homology.Homology(homology_file)

# print "Reading Motif information..."
# motif_info = Motif.Motif_info(motif_file)

# print "Reading reported PPIs..."
# reported_ppi = Usefuls.Hash.Hash_filt("S")
# reported_ppi.read_file(filename = reported_ppi_file,
#                        Key_cols = [0,1],
#                        Val_cols = [2])

# print "Reading wanted proteins..."
# wanted_prot = Usefuls.Hash.Hash_filt("A")
# wanted_prot.read_file(filename = wanted_prot_file,
#                       Key_cols = [0],
#                       Val_cols = [1])


# print "Reading gene info..."
# refseq2gene = Usefuls.Hash.Hash_filt("S")
# refseq2gene.read_file(filename = refseq2gene_file,
#                       Key_cols = [6],
#                       Val_cols = [1])

print "IVV -> Gene Calculation..."
ivv_gene = IVV_Gene.IVV_Gene(ivv_info)
ivv_gene.ivv_to_gene()

all_gene_spoke = ivv_gene.all_pair()
line_c = 0
for bait_geneid in all_gene_spoke:
    for prey_geneid in all_gene_spoke[ bait_geneid ]:
        
        output = Usefuls.Table.Table_row()
        output.append("Bait gene ID", bait_geneid)
        output.append("Prey gene ID", prey_geneid)
        output.append("Count", `all_gene_spoke[bait_geneid][prey_geneid]`)

        if prey_geneid == "":
            combait = []
        else:
            combait = ivv_gene.gene_to_ivv_common_bait(bait_geneid,
                                                       prey_geneid)
        output.append("Detectable baits", string.join(combait, ","))

        count = 0
        for c in combait:
            if c <> "Initial_Initial" and c <> "Mock_Mock":
                count = count + 1
        output.append("Detectable baits number", `count`)
        
        if line_c == 0:
            print string.join(output.return_header(), "\t")
        print string.join(output.return_row(), "\t")
        line_c += 1

