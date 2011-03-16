#!/usr/bin/env python

import sys
import string

import Data_Struct.Hash2
import Usefuls.Table_maker

from IVV_Packages.IVV_Motif.Motif_info1 import Motif_info
from IVV_Packages.IVV_Info.IVV_info1 import IVV_info
from IVV_Packages.IVV_Info.IVV_filter1 import IVV_filter
import IVV_Packages.IVV_Motif.IVV_Conv_Motif
import Seq_Packages.Motif.MMI_Pred2
import Graph_Packages.Graph.Graph1

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")

ivv_info_file      = rsc.IVVInfo
ivv_prey_filter    = rsc.PreyFilter
motif_file         = rsc.MotifInfo2
reported_ppi_file  = rsc.KnownPPI_Hsap
protein2motif_file = rsc.GeneID2Pfam

filter = IVV_filter()
filter.set_Prey_filter_file(ivv_prey_filter)

sys.stderr.write("Reading IVV information...\n")
ivv_info = IVV_info(ivv_info_file) # , filter)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif_info(motif_file)

sys.stderr.write("Prey -> Motif Conversion...\n")
ivv_motif = IVV_Packages.IVV_Motif.IVV_Conv_Motif.IVV_Conv_Motif(ivv_info, 
																 motif_info,
																 0.01,
																 mode = "M")
ivv_motif.ivv_to_convid()

sys.stderr.write("Reading Public PPI data...\n")
ppi_hash = Data_Struct.Hash2.Hash("A")
ppi_hash.read_file_hd(rsc.KnownPPI_Hsap,
                      Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                      Val_cols_hd = ["PubMed ID"])

ppi = Graph_Packages.Graph.Graph1.Graph()
ppi.read_hash_tab2(ppi_hash, "")

sys.stderr.write("Predicting MMI from public data...\n")
mmi = Seq_Packages.Motif.MMI_Pred2.MMI_Pred()
mmi.set_PPI(ppi)
mmi.set_motif_info(rsc.GeneID2Pfam)
mmi.pred_mmi_from_ppi()


tb = Usefuls.Table_maker.Table_row()

for m1 in ivv_motif.get_spoke():
	for m2 in ivv_motif.get_spoke()[m1]:
		seq_rep = ivv_motif.get_spoke()[m1][m2]
		source_list = ivv_motif.gene_to_ivv_common_bait_descr(
		    m1, m2)
	
		tb.append("Info Type", "[ Spoke ]")
		tb.append("Motif1", m1)
		tb.append("Motif2", m2)
		tb.append("Rep_Seqs", `seq_rep`)
	
		ppi_count = {}
		ppi_counter = 0
		ppi_known_counter = 0
		for source in source_list.Bait_Prey():
			for prey in source.get_preys():
				bait = source.get_bait()
		
				geneid_bait = ivv_info.Bait_info().geneid(bait)
				geneid_prey = ivv_info.Prey_info().geneid(prey)
		
				pair12 = geneid_bait + "\t" + geneid_prey
				pair21 = geneid_prey + "\t" + geneid_bait
		
				if not pair12 in ppi_count:
					ppi_counter += 1
					ppi_count[ pair12 ] = ""
					ppi_count[ pair21 ] = ""
		
					if ppi.get_edge_by_node_names(geneid_bait, geneid_prey) is not False:
						ppi_known_counter += 1


		tb.append("Rep_PPIs", `ppi_counter`)
		tb.append("Known PPIs", `ppi_known_counter`)
		known = ""
		if motif_info.mmi_has_pair(m1, m2):
			known = "*"
		
		
		tb.append("Known MMI", known)
		tb.append("Public PPIs", `mmi.get_mmi_val(m1, m2)`)
		
		tb.output("\t")

