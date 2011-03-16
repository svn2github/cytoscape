#!/usr/bin/env python

import sys

from Seq_Packages.Seq.MultiFasta2 import MultiFasta_MEM

import SAT_Packages.SAT11K.Human_Cancer11k5 as Cancer11k
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

import SAT_Packages.SAT11K.Read11k1 as Read11k
from Expr_Packages.Expr_II.Transcript1 import Transcript_Factory

from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kII

from Usefuls.Table_maker import Table_row

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")


class MultiFasta(MultiFasta_MEM):
    def extract_id(self, id):
        return id.split("|")[1]


sys.stderr.write("Reading command line ...\n")
opt = Option_Cancer11kII()

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k_dt, cancer11k_rd = Cancer11k.create_Human_Cancer11k_dt_rd(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

# 11k of multiple normal tissues
sys.stderr.write("Reading 11K of normal tissues ...\n")
expr_pat_set_dt, sat_set = Read11k.read_human11k_dT()
expr_pat_set_rd, sat_set = Read11k.read_human11k_random()

sys.stderr.write("Reading Probe Information ...\n")
probes = MultiFasta(rsc.Human11k_Cancer_ONC_AFAS_probes)

output = Table_row()

for afs_id in cancer11k_rd.get_exp_sheet().conv_afas_onc.keys():
    onc_id = cancer11k_dt.get_exp_sheet().conv_afas_onc.val_force(afs_id)
    annot = cancer_gene_info.get_annotation(onc_id)   
    
    (exp_pat_dt_sense_normal,
     exp_pat_dt_sense_cancer,
     exp_pat_dt_antis_normal,
     exp_pat_dt_antis_cancer) = \
     cancer11k_dt.get_four_exp_pat(afs_id)

    (exp_pat_rd_sense_normal,
     exp_pat_rd_sense_cancer,
     exp_pat_rd_antis_normal,
     exp_pat_rd_antis_cancer) = \
     cancer11k_rd.get_four_exp_pat(afs_id)

    exppat_antis_dt = \
        expr_pat_set_dt.expression_pat(Transcript_Factory()[afs_id].get_probes()[0])

    exppat_antis_rd = \
        expr_pat_set_rd.expression_pat(Transcript_Factory()[afs_id].get_probes()[0])

    exppat_cond_dt = expr_pat_set_dt.conditions()
    exppat_cond_rd = expr_pat_set_rd.conditions()

    output.append("Probe ID", afs_id)
    output.append("Accession", afs_id.split("-")[3])
    output.append("Sequence", probes.get_sequence(afs_id).get_seq())
    output.append("Strand", "Antisense")
    output.append("Description", "[AFAS] " + annot.replace('"', ""))
    
    for i in range(len(opt.get_normal_keys())):
        output.append("dT:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_dt_antis_normal[i])
        
    for i in range(len(opt.get_cancer_keys())):
        output.append("dT:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_dt_antis_cancer[i])

    for i in range(len(opt.get_normal_keys())):
        output.append("Rd:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_rd_antis_normal[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("Rd:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_rd_antis_cancer[i])
        
    for i in range(len(exppat_cond_dt)):
        output.append("dT:" + exppat_cond_dt[i],
                      "%.1f" % exppat_antis_dt[i])

    for i in range(len(exppat_cond_dt)):
        output.append("Rd:" + exppat_cond_rd[i],
                      "%.1f" % exppat_antis_rd[i])
    
    output.output("\t")


for onc_id in cancer11k_rd.get_exp_sheet().get_conv_onc_afas_table().keys():
    annot = cancer_gene_info.get_annotation(onc_id)
    afs_id0 = cancer11k_dt.get_exp_sheet().conv_onc_afas.val_force(onc_id)[0]

    (exp_pat_dt_sense_normal,
     exp_pat_dt_sense_cancer,
     exp_pat_dt_antis_normal,
     exp_pat_dt_antis_cancer) = \
     cancer11k_dt.get_four_exp_pat(afs_id0)

    (exp_pat_rd_sense_normal,
     exp_pat_rd_sense_cancer,
     exp_pat_rd_antis_normal,
     exp_pat_rd_antis_cancer) = \
     cancer11k_rd.get_four_exp_pat(afs_id0) 

    exppat_sense_dt = \
        expr_pat_set_dt.expression_pat(Transcript_Factory()[onc_id].get_probes()[0])

    exppat_sense_rd = \
        expr_pat_set_rd.expression_pat(Transcript_Factory()[onc_id].get_probes()[0])

    exppat_cond_dt = expr_pat_set_dt.conditions()
    exppat_cond_rd = expr_pat_set_rd.conditions()

    output.append("Probe ID", onc_id)
    output.append("Accession", onc_id.split("-")[1])
    output.append("Sequence", probes.get_sequence(onc_id).get_seq())
    output.append("Strand", "Sense")
    output.append("Description", annot.replace('"', ""))
    
    for i in range(len(opt.get_normal_keys())):
        output.append("dT:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_dt_sense_normal[i])
        
    for i in range(len(opt.get_cancer_keys())):
        output.append("dT:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_dt_sense_cancer[i])

    for i in range(len(opt.get_normal_keys())):
        output.append("Rd:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_rd_sense_normal[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("Rd:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_rd_sense_cancer[i])
        
    for i in range(len(exppat_cond_dt)):
        output.append("dT:" + exppat_cond_dt[i],
                      "%.1f" % exppat_sense_dt[i])

    for i in range(len(exppat_cond_dt)):
        output.append("Rd:" + exppat_cond_rd[i],
                      "%.1f" % exppat_sense_rd[i])
    
    output.output("\t")
