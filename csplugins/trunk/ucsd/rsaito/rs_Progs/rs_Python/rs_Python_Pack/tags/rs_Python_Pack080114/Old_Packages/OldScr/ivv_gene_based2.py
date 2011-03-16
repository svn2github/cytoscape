#!/usr/bin/python

import sys
import string

sys.path.append("./rsIVV_Python3")

import Usefuls.Hash
import Usefuls.Usefuls1
import Usefuls.Table

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
ivv_info = IVV_info.IVV_info(ivv_info_file)

sys.stderr.write("Reading homology information...\n")
homology = Homology.Homology(homology_file)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif.Motif_info(motif_file)

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Usefuls.Hash.Hash_filt("S")
reported_ppi.read_file(filename = reported_ppi_file,
                       Key_cols = [0,1],
                       Val_cols = [2])

sys.stderr.write("Reading wanted proteins...\n")
wanted_prot = Usefuls.Hash.Hash_filt("A")
wanted_prot.read_file(filename = wanted_prot_file,
                      Key_cols = [0],
                      Val_cols = [1])


sys.stderr.write("Reading gene info...\n")
refseq2gene = Usefuls.Hash.Hash_filt("S")
refseq2gene.read_file(filename = refseq2gene_file,
                      Key_cols = [6],
                      Val_cols = [1])

sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_gene = IVV_Gene.IVV_Gene(ivv_info)
ivv_gene.ivv_to_gene()


all_gene_spoke = ivv_gene.all_pairs()
line_c = 0
for bait_geneid in all_gene_spoke:
    for prey_geneid in all_gene_spoke[ bait_geneid ]:
        if prey_geneid == "": continue

        output = Usefuls.Table.Table_row()
        output.append("Gene ID", bait_geneid)
        output.append("Gene ID", prey_geneid)
        output.append("Count", `all_gene_spoke[bait_geneid][prey_geneid]`)

        source = ivv_gene.gene_to_ivv_common_bait_descr(bait_geneid,
                                                        prey_geneid)
        
        combait = source.common_baits()
        output.append("Detectable baits", string.join(combait, ","))

        count = source.count_common_baits()
        output.append("Number of detectable baits", `count`)

        orf_score, orf_total = source.eval_quals_spoke("orf", "0")

        orf_score_m, orf_total_m = source.eval_quals_matrix("orf", "0")

        output.append("ORF Score", `orf_score`)
        output.append("ORF Score Total", `orf_total`)

        output.append("ORF Score Matrix", `orf_score_m`)
        output.append("ORF Score Matrix Total", `orf_total_m`)

        itr = source.get_interolog(homology,
                                   refseq2gene, reported_ppi)
        if itr:
            i_pubmed = itr.get_best_ref()
            s1, s2 = itr.get_source()
            i1, i2 = itr.get_best()
            e1, e2 = itr.get_best_eval()
        else:
            i_pubmed = ""
            s1, s2 = ("", "")
            i1, i2 = ("", "")
            e1, e2 = ("", "")
        
        output.append("Interolog Pubmed", i_pubmed)
        output.append("Interolog source 1", s1)
        output.append("Interolog source 2", s2)
        output.append("Interolog 1", i1)
        output.append("Interolog 2", i2)
        output.append("Evalue 1", `e1`)
        output.append("Evalue 2", `e2`)

        motif1, motif2, mmi = source.get_motifs(motif_info)
        output.append("Motif 1", string.join(motif1, ","))
        output.append("Motif 2", string.join(motif2, ","))
        output.append("MMI", string.join(mmi, ","))
        
        if line_c == 0:
            print string.join(output.return_header(), "\t")
        print string.join(output.return_row(), "\t")
        line_c += 1

