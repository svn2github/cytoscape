#!/usr/bin/env python

import sys
sys.path.append("../")

import string

from PPI_Pred.PPIPred1 import PPIPred1
import IVV_info.IVV_info
import Usefuls.Table


ivv_info_file = "../../IVV/ivv_human8.0_info"
ivv_prey_filter = "../../IVV/basic_filter_list2"

ivv_to_refseq_homol_file = "../../Homology/homol_ivv_human8.0-human_refseq"
refseq_to_geneid_file = "/home/rsaito/work/Gene_info/gene2refseq_hs"

    
filter = IVV_info.IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)

sys.stderr.write("Reading IVV information...\n")
ivv_info = IVV_info.IVV_info.IVV_info(ivv_info_file, filter)
    
ivv_pred = PPIPred1(ivv_info, mode = "S")


sys.stderr.write("Reading ID Conversion files...\n")
ivv_pred.set_mapping(ivv_to_refseq_homol_file,
                     refseq_to_geneid_file)

sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_pred.set_reprod_thres(2)
ivv_pred.ivv_to_convid()

tb = Usefuls.Table.Table_row()

spoke = ivv_pred.get_spoke()
for p1 in spoke:
    for p2 in spoke[p1]:
        tb.append("Gene 1", p1)
        tb.append("Gene 2", p2)
        tb.append("Reprod", `spoke[p1][p2]`)

        source = ivv_pred.gene_to_ivv_common_bait_descr(p1, p2)

        tb.append("Common baits",
                  string.join(source.common_baits(), ","))
        tb.append("Common bait count BP",
                  `source.count_common_baits_BP()`)
        tb.append("Common bait count PB",
                  `source.count_common_baits_PB()`)

        for src in source.Bait_Prey():
            tb.append("Bait", src.get_bait())
            tb.append("BP Type", "BP")
            for prey in src.get_preys():
                tb.append("IVV Src 1", src.get_bait())
                tb.append("IVV Src 2", prey)
                tb.output("\t")

        for src in source.Prey_Bait():
            tb.append("Bait", src.get_bait())
            tb.append("BP Type", "PB")
            for prey in src.get_preys():
                tb.append("IVV Src 1", prey)
                tb.append("IVV Src 2", src.get_bait())
                tb.output("\t")

