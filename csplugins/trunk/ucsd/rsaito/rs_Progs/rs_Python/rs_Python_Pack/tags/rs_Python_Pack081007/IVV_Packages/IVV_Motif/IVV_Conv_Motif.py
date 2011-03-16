#!/usr/bin/env python

import string
from IVV_Packages.IVV_Info.IVV_info1 import IVV_info
from IVV_Packages.IVV_Info.IVV_filter1 import IVV_filter
from IVV_Packages.IVV_Motif.Motif_info1 import Motif_info
from IVV_Packages.IVV_Info.IVV_Conv import IVV_Conv
import Usefuls.Table_maker

class IVV_Conv_Motif(IVV_Conv):
    def __init__(self, ivv_info, motif_info, thres, mode = "S"):
        IVV_Conv.__init__(self, ivv_info, mode)
        self.motif_info = motif_info
        self.thres = thres

    def bait2convid(self, bait_ID):
        return self.motif_info.get_motif(bait_ID, self.thres)

    def prey2convid(self, prey_ID):
        return self.motif_info.get_motif(prey_ID, self.thres)

    def convid2baits(self, motif):
        ids = self.motif_info.get_seqid_from_motif(
            motif, self.thres)

        baits = []
        for id in ids:
            if self.ivv_info.ID_Type(id) == "Bait":
                baits.append(id)
        return baits

    def convid2preys(self, motif):
        ids = self.motif_info.get_seqid_from_motif(
            motif, self.thres)
        baits = []
        for id in ids:
            if self.ivv_info.ID_Type(id) == "Prey":
                baits.append(id)
        return baits

if __name__ == "__main__":
    import sys
    ivv_info_file = "../../IVV/ivv_human7.3_info"
    motif_file = "../../Motifs/Pfam_ivv_human7.3_motif_info"

    filter = IVV_filter1()
    filter.set_Bait_filter(("FOS", "JUN", "ATF2"))

    sys.stderr.write("Reading IVV information...\n")
    ivv_info = IVV_info(ivv_info_file) # , filter)
    sys.stderr.write("Reading Motif information...\n")
    motif_info = Motif_info(motif_file)

    ivv_motif = IVV_Conv_Motif(ivv_info, motif_info, 0.01, mode = "M")
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
	    known = ""
	    if motif_info.mmi_has_pair(m1, m2):
		known = "*"

	    tb.append("Known", known)
	    tb.append("Info Type", "[ MMI ]")
	    tb.output("\t", "Known")


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



