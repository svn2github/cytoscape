#!/usr/bin/env python

import sys
import string

sys.path.append("./rsIVV_Python3")

import Data_Struct.Hash
import Usefuls.Usefuls1
import Usefuls.Table_maker

import Motif
import IVV_info
import IVV_filter
import IVV_Conv_Motif
import MMI.MMI_Pred1
import PPI.PPi2

ivv_info_file = "./IVV/ivv_human7.3_info"
ivv_prey_filter = "./basic_filter_list1"
motif_file = "./Motifs/Pfam_ivv_human7.3_motif_info"
reported_ppi_file = "./PPI_public/ppi_ncbi.txt"
protein2motif_file = "./Motifs/GeneIDPfam_list"

filter = IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)

sys.stderr.write("Reading IVV information...\n")
ivv_info = IVV_info.IVV_info(ivv_info_file) # , filter)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif.Motif_info(motif_file)

sys.stderr.write("Prey -> Motif Conversion...\n")
ivv_motif = IVV_Conv_Motif.IVV_Conv_Motif(ivv_info,
					  motif_info,
					  0.01,
					  mode = "M")
ivv_motif.ivv_to_convid()

sys.stderr.write("Reading Public PPI data...\n")
ppi = PPI.PPi2.PPi2()
ppi.read_from_file2(reported_ppi_file, 0, 1, "")
ppi.both_dir()

sys.stderr.write("Predicting MMI from public data...\n")
mmi = MMI.MMI_Pred1.MMI_Pred1()
mmi.set_PPI(ppi)
mmi.set_motif_info(protein2motif_file)
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

		    if ppi.get_ppi_val(geneid_bait,
				       geneid_prey) != False:
			ppi_known_counter += 1


	tb.append("Rep_PPIs", `ppi_counter`)
	tb.append("Known PPIs", `ppi_known_counter`)
	known = ""
	if motif_info.mmi_has_pair(m1, m2):
	    known = "*"


	tb.append("Known MMI", known)
	tb.append("Public PPIs", `mmi.get_mmi_val(m1, m2)`)

	tb.output("\t")

done = {}
for m1 in ivv_motif.get_matrix():
    for m2 in ivv_motif.get_matrix()[m1]:
	m12 = m1 + "\t" + m2
	m21 = m2 + "\t" + m1
	if m12 in done: continue
	done[m12] = ""
	done[m21] = ""

	seq_rep = ivv_motif.get_matrix()[m1][m2]
	source_list = ivv_motif.gene_to_ivv_common_bait_descr(
	    m1, m2)

	tb.append("Info Type", "[ Matrix ]")
	tb.append("Motif1", m1)
	tb.append("Motif2", m2)
	tb.append("Rep_Seqs", `seq_rep`)

	ppi_count = {}
	ppi_counter = 0
	ppi_known_counter = 0
	for source in source_list.Prey_Prey():
	    for prey1 in source.get_preys1():
		for prey2 in source.get_preys2():

		    geneid_prey1 = ivv_info.Prey_info().geneid(prey1)
		    geneid_prey2 = ivv_info.Prey_info().geneid(prey2)

		    pair12 = geneid_prey1 + "\t" + geneid_prey2
		    pair21 = geneid_prey2 + "\t" + geneid_prey1

		    if not pair12 in ppi_count:
#			print "For", m1, "and", m2,
#			print geneid_prey1, geneid_prey2

			ppi_counter += 1
			ppi_count[ pair12 ] = ""
			ppi_count[ pair21 ] = ""

			if ppi.get_ppi_val(geneid_prey1,
					   geneid_prey2) != False:
			    ppi_known_counter += 1

	tb.append("Rep_PPIs", `ppi_counter`)
	tb.append("Known PPIs", `ppi_known_counter`)
	known = ""
	if motif_info.mmi_has_pair(m1, m2):
	    known = "*"

	tb.append("Known MMI", known)
	tb.append("Public PPIs", `mmi.get_mmi_val(m1, m2)`)

	tb.output("\t")

