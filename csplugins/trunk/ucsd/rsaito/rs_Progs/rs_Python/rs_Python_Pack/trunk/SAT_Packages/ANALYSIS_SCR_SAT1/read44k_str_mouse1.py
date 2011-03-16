#!/usr/bin/env python

import Usefuls.rsConfig
import SAT_Packages.SAT44K_Info.sas
import cPickle
import Usefuls.Table_maker

def exon_region_str(exon_region):
    
    ret = []
    for exon_region in exon_regions:
        s, e = exon_region
        ret.append("%d-%d" % (s, e))
    return ",".join(ret)
        

rsc_antis = Usefuls.rsConfig.RSC_II("rsSAT_Config")

all_info = cPickle.load(open(rsc_antis.mouse44k_str, "r"))

output = Usefuls.Table_maker.Table_row()


for isoform in all_info.get_all_isoforms():

    isoform_ID   = isoform.get_ID()
    exon_regions = isoform.get_exon_regions()
    cluster      = isoform.get_cluster()
    cluster_ID   = cluster.get_ID()
    repr_ID      = cluster.get_rep_ID()
    SAT_IDs      = cluster.get_all_SAT_ID()
    chromosome   = cluster.get_chromosome()
    strand       = cluster.get_strand()

    output.append("Isoform ID", isoform.get_ID())
    output.append("Chromosome", chromosome)
    output.append("Strand", strand)
    output.append("Cluster ID", cluster_ID)
    output.append("Representative isoform ID", repr_ID)
    output.append("SAT IDs", ",".join(SAT_IDs))
    output.append("Isoform start", `isoform.get_start()`)
    output.append("Isoform end",   `isoform.get_end()`)
    output.append("Exon regions", exon_region_str(exon_regions))
    output.output("\t")


