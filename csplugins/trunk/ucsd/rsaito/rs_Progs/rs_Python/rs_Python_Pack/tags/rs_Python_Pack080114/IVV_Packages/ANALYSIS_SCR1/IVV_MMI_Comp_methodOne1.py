#!/usr/bin/env python

import sys
sys.path.append("../")

from IVV_info.IVV_info import IVV_info
from IVV_info.IVV_filter import IVV_filter1
import Usefuls.Table_maker
from Motif.Motif_info1 import Motif_info
from Motif.MMI2 import MMI2
import Motif.MMI_Pred2
import Motif.IVV_Conv_Motif
import Motif.InterDom
import Graph.Graph1
from Data_Struct.Hash2 import Hash
from Usefuls.rsConfig import RSC

rsc = RSC("../../../rsIVV_Config")

filter = IVV_filter1()
filter.set_Prey_filter_file(rsc.PreyFilter)

sys.stderr.write("Reading IVV information...\n")
ivv_info = IVV_info(rsc.IVVInfo) # , filter)

sys.stderr.write("Reading Motif and MMI information...\n")
motif_info = Motif_info(rsc.MotifInfo)
iPfam_info = MMI2(rsc.iPfam)

sys.stderr.write("Reading Public PPI data...\n")
ppi_hash = Hash("A")
ppi_hash.read_file_hd(rsc.KnownPPI_Hsap,
                      Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                      Val_cols_hd = ["PubMed ID"])
ppi = Graph.Graph1.Graph()
ppi.read_hash_tab2(ppi_hash, "")
ppi.both_dir()

sys.stderr.write("Predicting MMI from public data...\n")
mmi = Motif.MMI_Pred2.MMI_Pred()
mmi.set_PPI(ppi)
mmi.set_motif_info(rsc.GeneID2Pfam)
mmi.pred_mmi_from_ppi()

interdom = Motif.InterDom.InterDom(rsc.InterDom, rsc.Pfam_fs)

ivv_motif = Motif.IVV_Conv_Motif.IVV_Conv_Motif(ivv_info,
                                                motif_info,
                                                1.0e-2)
ivv_motif.ivv_to_convid()

tb = Usefuls.Table_maker.Table_row()
tb.append("Info Type", "")
tb.append("Motif 1", "")
tb.append("Motif 2", "")
tb.append("Rep_Seq", "")
tb.append("Rep_PPI", "")
tb.append("Public PPIs", "")
tb.append("Interdom", "")
tb.append("iPfam", "")
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
	tb.append("Motif 1", m1)
	tb.append("Motif 2", m2)
	tb.append("Rep_Seq", `seq_rep`)

	ppi_count = {}
	for source in source_list.Bait_Prey():
	    for prey in source.get_preys():
		bait = source.get_bait()

		geneid_bait = ivv_info.Bait_info().geneid(bait)
		geneid_prey = ivv_info.Prey_info().geneid(prey)

		ppi_count[ geneid_bait + "\t" + geneid_prey ] = ""

	tb.append("Rep_PPI", `len(ppi_count.keys())`)

	tb.append("Public PPIs", `mmi.get_mmi_val(m1, m2)`)

        interdom_check = interdom.entry_check(m1, m2)
        if interdom_check is True:
            interdom_res = "*"
        elif interdom_check is False:
            interdom_res = "X"
        else:
            interdom_res = ""
	tb.append("Interdom", interdom_res)

	iPfam = ""
	if iPfam_info.mmi_has_pair(m1, m2):
	    iPfam = "*"
	tb.append("iPfam", iPfam)


	tb.append("Info Type", "[ MMI ]")
	tb.output("\t", "iPfam")

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
