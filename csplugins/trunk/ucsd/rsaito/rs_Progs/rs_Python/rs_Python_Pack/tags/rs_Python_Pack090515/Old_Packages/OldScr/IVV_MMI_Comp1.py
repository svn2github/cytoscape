#!/usr/bin/env python

import sys
sys.path.append("../")

from IVV_info.IVV_info import IVV_info
from IVV_info.IVV_filter import IVV_filter1
import Usefuls.Table_maker
import Motif.SwissPfam
from Motif.IVV_Conv_Motif_Sprot_Pfam import IVV_Conv_Motif_Sprot_Pfam
from Motif.Motif_Sprot_Pfam_info import Motif_Sprot_Pfam_info_Bait
from Motif.Motif_Sprot_Pfam_info import Motif_Sprot_Pfam_info_Prey
from Motif.Motif_info1 import Motif_info
from Motif.MMI1 import MMI1
from Integ_class.RefSeq_based_map2 import RefSeq_based_map2
from Homology.Homology1_descr import Homology1_descr

import Motif.MMI_Pred1
import PPI.PPi2

ivv_info_file = "../../IVV/ivv_human7.3_info"
ivv_prey_filter = "../../IVV/basic_filter_list1"

swiss_pfam_file = "../../Motifs/swisspfam_save"
ivv_to_refseq_file = "../../IVV/ivv_human7.3_refseq_match"
bait_to_swiss_file = "../../Homology/homol_ivv_human7.3_Bait_Sprot_simp_res_1st"
refseq_to_sprot_file = "../../Homology/homol_ivv_human7.3_refseq_uniprot_sprot_simp_res95"

motif_file = "../../Motifs/Pfam_ivv_human7.3_motif_info"

iPfam_file = "../../Motifs/MMI_iPfam"

reported_ppi_file = "../../PPI_public/ppi_ncbi.txt"
protein2motif_file = "../../Motifs/GeneIDPfam_list"

filter = IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)

sys.stderr.write("Reading IVV information...\n")
ivv_info = IVV_info(ivv_info_file, filter)

sys.stderr.write("Reading Motifs in SwissProt...\n")
swiss_pfam_info = Motif.SwissPfam.Motif_swiss_set()
swiss_pfam_info.load_motif_info(swiss_pfam_file)

sys.stderr.write("Reading Bait -> SwissProt...\n")
bait_to_swiss = Homology1_descr()
bait_to_swiss.read_homol_file(bait_to_swiss_file)

refseq_based_map = RefSeq_based_map2(ivv_info,
				     ivv_to_refseq_file,
				     refseq_to_sprot_file)

sys.stderr.write("Reading Motif information...\n")

motif_sprot_pfam_info_bait = Motif_Sprot_Pfam_info_Bait(
    ivv_info, swiss_pfam_info, bait_to_swiss)
motif_sprot_pfam_info_prey = Motif_Sprot_Pfam_info_Prey(
    ivv_info, swiss_pfam_info, refseq_based_map)
ivv_motif = IVV_Conv_Motif_Sprot_Pfam(ivv_info,
				      motif_sprot_pfam_info_bait,
				      motif_sprot_pfam_info_prey,
				      5, mode = "S")

sys.stderr.write("Reading MMI information...\n")
motif_info = Motif_info(motif_file)
iPfam_info = MMI1(iPfam_file)


sys.stderr.write("Reading Public PPI data...\n")
ppi = PPI.PPi2.PPi2()
ppi.read_from_file2(reported_ppi_file, 0, 1, "")
ppi.both_dir()

sys.stderr.write("Predicting MMI from public data...\n")
mmi = Motif.MMI_Pred1.MMI_Pred1()
mmi.set_PPI(ppi)
mmi.set_motif_info(protein2motif_file)
mmi.pred_mmi_from_ppi()


ivv_motif.ivv_to_convid()

tb = Usefuls.Table_maker.Table_row()
tb.append("Info Type", "")
tb.append("Motif1", "")
tb.append("Motif2", "")
tb.append("Rep_Seq", "")
tb.append("Rep_PPI", "")
tb.append("Known", "")
tb.append("Bait", "")
tb.append("Prey", "")
tb.append("Bait gene ID", "")
tb.append("Prey gene ID", "")
tb.append("Prey ORF", "")

for m1 in ivv_motif.get_spoke():
    for m2 in ivv_motif.get_spoke()[m1]:
	seq_rep = ivv_motif.get_spoke()[m1][m2]
	source_list = ivv_motif.gene_to_ivv_common_bait_descr(
	    m1, m2)

	tb.append("Info Type", "[ MMI ]")
	tb.append("Motif1", m1)
	tb.append("Motif2", m2)
	tb.append("Rep_Seq", `seq_rep`)

	ppi_count = {}
	for source in source_list.Bait_Prey():
	    for prey in source.get_preys():
		bait = source.get_bait()

		geneid_bait = ivv_info.Bait_info().geneid(bait)
		geneid_prey = ivv_info.Prey_info().geneid(prey)

		ppi_count[ geneid_bait + "\t" + geneid_prey ] = ""

	tb.append("Rep_PPI", `len(ppi_count.keys())`)

	interdom = ""
	if motif_info.mmi_has_pair(m1, m2):
	    interdom = "*"
	tb.append("Interdom", interdom)

	iPfam = ""
	if iPfam_info.mmi_has_pair(m1, m2):
	    iPfam = "*"
	tb.append("iPfam", iPfam)

	tb.append("Public PPIs", `mmi.get_mmi_val(m1, m2)`)

	tb.append("Info Type", "[ MMI ]")
	tb.output("\t", "Public PPIs")

	for source in source_list.Bait_Prey():
	    for prey in source.get_preys():
		bait = source.get_bait()
		geneid_bait = ivv_info.Bait_info().geneid(bait)
		geneid_prey = ivv_info.Prey_info().geneid(prey)

		tb.append("Rep_PPI", `len(ppi_count.keys())`)
		tb.append("Bait", bait)
		tb.append("Prey", prey)
		tb.append("Bait gene ID", geneid_bait)
		tb.append("Prey gene ID", geneid_prey)
		tb.append("Prey ORF",
			  ivv_info.Prey_info().
			  get_qual_noerror(prey, "orf"))
		tb.append("Info Type", "[ MMI descr ]")
		tb.output("\t")
