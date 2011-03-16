#!/usr/bin/env python

import sys

from Data_Struct.Hash2 import Hash

from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kII
from SAT_Packages.SAT11K.Read11kII1 import Read_SAT11KII
import SAT_Packages.SAT11K.Okay_Sheet_Simple1 as SAT_Cancer11k

from Seq_Packages.Seq.MultiFasta2 import MultiFasta_MEM

from Usefuls.Table_maker import Table_row
from Usefuls.DirPath import sdp

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
rsc_gene = RSC_II("NCBI_GeneInfo")

class Hash_without_version_k(Hash):
    def conv_key(self, k):
        return k.split(".")[0]
    

class Table_row_file(Table_row):
    def record_out_file(self, path, sep = '\t'):
        for column_name in self.return_header():
            fw = open(sdp(path + "/"
                          + column_name.replace(" ", "-")), "w")
            fw.write(sep.join(("ID_REF", "VALUE")) + '\n')
            for rec in self.rec:
                column_idx = self.get_header_idx(column_name)
                keyitem    = rec[0]
                valitem    = rec[ column_idx ]
                fw.write(sep.join((keyitem, valitem)) + '\n')


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

# SAT pairs 11K
sys.stderr.write("Reading SAT 11K pairs ...\n")
sats_strand, sats_pc_pc, sats_pc_nc, sats_nc_nc = \
    Read_SAT11KII(rsc.human11k_okay)

# 11k of multiple normal tissues
# sys.stderr.write("Reading 11K of normal tissues ...\n")
# expr_pat_set_dt, sat_set = Read11k.read_human11k_dT()
# expr_pat_set_rd, sat_set = Read11k.read_human11k_random()

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k_dt, cancer11k_rd = SAT_Cancer11k.create_Human_Cancer11k_dt_rd(opt)
okay_sheet_dt = cancer11k_dt.get_exp_sheet()
okay_sheet_rd = cancer11k_rd.get_exp_sheet()

sys.stderr.write("Reading Probe Information ...\n")
probes = MultiFasta_MEM(rsc.Human11k_SAT_probes)

output = Table_row_file()

"""
for sat in sats_strand.get_sats():
    t1, t2  = sat.get_transcripts()
    transcripts[ t1 ] = transcripts.get(t1, 0) + 1
    transcripts[ t2 ] = transcripts.get(t1, 0) + 1
    strand_plus[ t1 ] = ""
    strand_minus[ t2 ] = ""
    """
    
for transcript in sats_strand.get_transcripts():
    id = transcript.get_transcriptID()
    geneid = accession2geneid.val_force(id)
    if gene_info.val_force(geneid):
        annot = gene_info.val_force(geneid).split("\t")[1]
    else:
        annot = ""
        
    if transcript in sats_strand.plus2minus():
        strand = "+"
        counterpart = ",".join(
            map(lambda x: x.get_transcriptID(), 
                sats_strand.plus2minus()[transcript]))
        
    elif transcript in sats_strand.minus2plus():
        strand = "-"
        counterpart = ",".join(
            map(lambda x: x.get_transcriptID(),
                sats_strand.minus2plus()[transcript]))
        
    exp_pat_dt_normal = okay_sheet_dt.get_data_accord_keys(id,
                                                           opt.get_normal_keys())
    exp_pat_dt_cancer = okay_sheet_dt.get_data_accord_keys(id,
                                                           opt.get_cancer_keys())
    exp_pat_rd_normal = okay_sheet_rd.get_data_accord_keys(id,
                                                           opt.get_normal_keys())
    exp_pat_rd_cancer = okay_sheet_rd.get_data_accord_keys(id,
                                                           opt.get_cancer_keys())
        
    output.append("ID", id)
    output.append("Accession", id)
    output.append("Sequence", probes.get_sequence(id).get_seq())
    output.append("Strand", strand)
    output.append("Counterpart", counterpart)
    output.append("Description", annot)
    
    for i in range(len(opt.get_normal_keys())):
        output.append("dT:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_dt_normal[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("dT:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_dt_cancer[i])

    for i in range(len(opt.get_normal_keys())):
        output.append("Rd:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_rd_normal[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("Rd:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_rd_cancer[i])

    output.record()
    
output.record_out_file("/tmp/OUTDIR")


    