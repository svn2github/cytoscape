#!/usr/bin/env python

""" This module integrates Multiple normal tissues (11K) and normal/cancer tissues (11K)
and calculates all the necessary information. """

""" Program check required. (min_change_select_patient_noII) """

import math
import sys
from Calc_Packages.Math.Vector1 import vector_pair
from Calc_Packages.Math.Stats_OrderI import median, ordering
from Calc_Packages.Math.StatsI import mean, corr
import SAT_Packages.SAT11K.Read11k1 as Read11k # 11k of multiple normal tissues
import SAT_Packages.SAT11K.Okay_Sheet_Simple1 as SAT_Cancer11k
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from Expr_Packages.Expr_II.Transcript1 import Transcript_Factory

from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kII
from SAT_Packages.SAT11K.Read11kII1 import Read_SAT11KII

from Data_Struct.Hash2 import Hash
import Usefuls.Table_maker

from Usefuls.Counter import Count2
from Usefuls.ListProc1 import NonRedList

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
rsc_gene = RSC_II("NCBI_GeneInfo")

from Usefuls.ListProc1 import array_string_neat

from Bertucci import accession_to_updown_hash
from Patil import accession_to_foldchange_hash

from analysis_norm_cancer11k_AFAS_VBA4 import output_macro

def log_ratio(after, before):
    return math.log(after / before) / math.log(10.0)


def min_change(exp_pat_sense_normal,
               exp_pat_sense_cancer,
               exp_pat_antis_normal,
               exp_pat_antis_cancer,
               mult = (+1.0, -1.0, -1.0, +1.0)):
    
    ret = []
    for i in range(len(exp_pat_sense_normal)):
        s_n = exp_pat_sense_normal[i]
        s_c = exp_pat_sense_cancer[i]
        a_n = exp_pat_antis_normal[i]
        a_c = exp_pat_antis_cancer[i]
        ret.append(min(log_ratio(s_c, s_n)*mult[0],
                       log_ratio(a_c, a_n)*mult[1],
                       log_ratio(s_n, a_n)*mult[2],
                       log_ratio(s_c, a_c)*mult[3]
                       ))
        
    return ret

def min_change_select_patient_no(exp_pat_sense_normal,
                                 exp_pat_sense_cancer,
                                 exp_pat_antis_normal,
                                 exp_pat_antis_cancer,
                                 select_patient_nth,
                                 mult = (+1.0, -1.0, -1.0, +1.0)):

    min_change_array = min_change(exp_pat_sense_normal,
                                  exp_pat_sense_cancer,
                                  exp_pat_antis_normal,
                                  exp_pat_antis_cancer,
                                  mult)
    min_change_array_order = ordering(min_change_array)
    min_change_array_order_sort = min_change_array_order[:]
    min_change_array_order_sort.sort()
    patient_rank = min_change_array_order_sort[-select_patient_nth]
    patient_no   = min_change_array_order.index(patient_rank)     
    
    return patient_no, min_change_array[patient_no]
                                               

class Hash_without_version_k(Hash):
    def conv_key(self, k):
        return k.split(".")[0]
    

select_patient_nth = 3

sys.stderr.write("Reading command line ...\n")
opt = Option_Cancer11kII()

sys.stderr.write("Reading gene info ...\n")
accession2geneid  = Hash_without_version_k("S")
accession2geneid.read_file(rsc_gene.gene2accession_hs,
                           Key_cols = [3],
                           Val_cols = [1])

gene_info = Hash("S")
gene_info.read_file(rsc_gene.GeneInfo_hs,
                    Key_cols = [1],
                    Val_cols = [2,8])

sys.stderr.write("Reading Gene Ontology ...\n")
go = Hash("A")
go.read_file(rsc_gene.gene2go,
             Key_cols = [1],
             Val_cols = [5,7])

# SAT pairs 11K
sys.stderr.write("Reading SAT 11K pairs ...\n")
sats_strand, sats_pc_pc, sats_pc_nc, sats_nc_nc = \
    Read_SAT11KII(rsc.human11k_okay)

# 11k of multiple normal tissues
sys.stderr.write("Reading 11K of normal tissues ...\n")
expr_pat_set_dt, sat_set = Read11k.read_human11k_dT()
expr_pat_set_rd, sat_set = Read11k.read_human11k_random()

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k_dt, cancer11k_rd = SAT_Cancer11k.create_Human_Cancer11k_dt_rd(opt)

output = Usefuls.Table_maker.Table_row()

counter_all = 0
counter_sub = 0
counter_GO_all = Count2()
counter_GO_sub = Count2()
geneid_checked_all = {}
geneid_checked_sub = {}

counter = 0
for sat in sats_strand.get_sats():

    t1, t2  = sat.get_transcripts()
    s_id, a_id = t1.get_transcriptID(), t2.get_transcriptID()
    geneid_s, geneid_a = accession2geneid.val_force(s_id), accession2geneid.val_force(a_id)
    go_s, go_a = go.val_force(geneid_s), go.val_force(geneid_a)
   
    """ *** Human 11k cancer tissues *** """
    
    if gene_info.val_force(geneid_s):
        annot_s = gene_info.val_force(geneid_s).split("\t")[1]
    else:
        annot_s = ""
    # categ_s = ", ".join(cancer_gene_info.get_major_categ_descr(s_id))
    # oncts_s = cancer_gene_info.get_OncTS_from_onc_id(s_id)

    if gene_info.val_force(geneid_a):
        annot_a = gene_info.val_force(geneid_a).split("\t")[1]
    else:
        annot_a = ""
    # categ_a = ", ".join(cancer_gene_info.get_major_categ_descr(a_id))
    # oncts_a = cancer_gene_info.get_OncTS_from_onc_id(a_id)

    (exp_pat_dt_sense_normal,
     exp_pat_dt_sense_cancer,
     exp_pat_dt_antis_normal,
     exp_pat_dt_antis_cancer) = \
     cancer11k_dt.get_four_exp_pat(sat)

    (exp_pat_rd_sense_normal,
     exp_pat_rd_sense_cancer,
     exp_pat_rd_antis_normal,
     exp_pat_rd_antis_cancer) = \
     cancer11k_rd.get_four_exp_pat(sat)


    """ Calculates dT expressions """
       
    patient_dt, min_change_dt = \
        min_change_select_patient_no(exp_pat_dt_sense_normal,
                                     exp_pat_dt_sense_cancer,
                                     exp_pat_dt_antis_normal,
                                     exp_pat_dt_antis_cancer,
                                     select_patient_nth)

    patient_dt_op, min_change_dt_op = \
        min_change_select_patient_no(exp_pat_dt_sense_normal,
                                     exp_pat_dt_sense_cancer,
                                     exp_pat_dt_antis_normal,
                                     exp_pat_dt_antis_cancer,
                                     select_patient_nth,
                                     mult = (-1.0, +1.0, +1.0, -1.0)
                                     )    

    """ Calculates Rd expressions """
   
    patient_rd, min_change_rd = \
        min_change_select_patient_no(exp_pat_rd_sense_normal,
                                     exp_pat_rd_sense_cancer,
                                     exp_pat_rd_antis_normal,
                                     exp_pat_rd_antis_cancer,
                                     select_patient_nth)
        
    patient_rd_op, min_change_rd_op = \
        min_change_select_patient_no(exp_pat_rd_sense_normal,
                                     exp_pat_rd_sense_cancer,
                                     exp_pat_rd_antis_normal,
                                     exp_pat_rd_antis_cancer,
                                     select_patient_nth,
                                     mult = (-1.0, +1.0, +1.0, -1.0)
                                     )

    """ Calculates dT-Rd expressions """
    
    patient_dt_rd, min_change_dt_rd = \
        min_change_select_patient_no(exp_pat_dt_sense_normal,
                                     exp_pat_dt_sense_cancer,
                                     exp_pat_rd_antis_normal,
                                     exp_pat_rd_antis_cancer,
                                     select_patient_nth)   

    patient_dt_rd_op, min_change_dt_rd_op = \
        min_change_select_patient_no(exp_pat_dt_sense_normal,
                                     exp_pat_dt_sense_cancer,
                                     exp_pat_rd_antis_normal,
                                     exp_pat_rd_antis_cancer,
                                     select_patient_nth,
                                     mult = (-1.0, +1.0, +1.0, -1.0)
                                     )

    if geneid_s not in geneid_checked_all:
        counter_all += 1
        counter_GO_all.count_up_list(go_s)
        geneid_checked_all[ geneid_s ] = ""
    if geneid_a not in geneid_checked_all:
        counter_all += 1
        counter_GO_all.count_up_list(go_a)
        geneid_checked_all[ geneid_a ] = ""


    flag = ""
    if (min_change_dt >= 0.0414 or min_change_dt_op >= 0.0414 or
        min_change_rd >= 0.0414 or min_change_rd_op >= 0.0414):
        if geneid_s not in geneid_checked_sub:
            counter_sub += 1
            counter_GO_sub.count_up_list(go_s)
            geneid_checked_sub[ geneid_s ] = ""
            flag = "!"
        if geneid_a not in geneid_checked_sub:
            counter_sub += 1
            counter_GO_sub.count_up_list(go_a)
            geneid_checked_sub[ geneid_a ] = ""
            flag = "!"

    output.append("SAT ID", "SAT-" + sat.get_satid())
    output.append("Sense ID",  s_id)
    output.append("Gene ID", geneid_s)
    if gene_info.val_force(geneid_s) is not None:
        output.append("Gene symbol", gene_info.val_force(geneid_s).split("\t")[0])
    else:
        output.append("Gene symbol", "")
    output.append("Annotation", annot_s)
    # output.append("Categories", categ_s)
    # output.append("Oncogenes / Tumor suppressors", oncts_s)

    output.append("Antisense ID", a_id)
    output.append("Antisense Gene ID", geneid_a)
    if gene_info.val_force(geneid_a) is not None:
        output.append("Antisense Gene symbol", gene_info.val_force(geneid_a).split("\t")[0])
    else:
        output.append("Antisense Gene symbol", "")
    output.append("Antisense Annotation", annot_a)
    
    if go_s:
        go_s = NonRedList(go_s)
        go_s_str = ",".join(map(lambda x:x.split("\t")[0] + '@' + x.split("\t")[1][0], go_s))
    else:
        go_s_str = ""
    if go_a:
        go_a = NonRedList(go_a)
        go_a_str = ",".join(map(lambda x:x.split("\t")[0] + '@' + x.split("\t")[1][0], go_a))
    else:
        go_a_str = ""
    
    output.append("Sense GO", go_s_str)
    output.append("Antisense GO", go_a_str)
    
    output.append("Selected change dT", "%.3f" % min_change_dt)
    output.append("Selected change Rd", "%.3f" % min_change_rd)
    # output.append("Selected change dT-rd", "%.3f" % min_change_dt_rd)
    
    output.append("Selected change dT (Opposite)", "%.3f" % min_change_dt_op)
    output.append("Selected change Rd (Opposite)", "%.3f" % min_change_rd_op)
    # output.append("Selected change dT-rd (Opposite)", "%.3f" % min_change_dt_rd_op)

    output.append("Flag", flag)

    # output.output("\t")

    """
    if flag:
        counter += 1
    if counter >= 10:
        break
    """

for go_categ in "Function", "Process", "Component":

    all_go = []
    for go in counter_GO_all.get_elems():
        if go.split("\t")[1] == go_categ:
            all_go.append(go)
    all_go.sort()
    
    print go_categ
    output = Usefuls.Table_maker.Table_row()
    for go in all_go:
        go_all = counter_GO_all[go]
        if go in counter_GO_sub:
            go_sub = counter_GO_sub[go]
        else:
            go_sub = 0
        output.append("GO", go.split("\t")[0])
        output.append("All", `go_all`)
        output.append("Sub", `go_sub`)
        output.append("All ratio", "%.3f" % (1.0*go_all/counter_all))
        output.append("Sub ratio", "%.3f" % (1.0*go_sub/counter_sub))
        if go_sub > 0:
            output.output("\t")
    print


