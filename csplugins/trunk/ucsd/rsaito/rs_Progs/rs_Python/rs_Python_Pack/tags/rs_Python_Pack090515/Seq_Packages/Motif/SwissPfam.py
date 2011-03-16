#!/usr/bin/env python

import re
from General_Packages.Data_Struct.Hash2 import Hash
from Seq_Packages.Motif.MotifDescr1 import MotifDescr

class Motif_swiss_set:

    def __init__(self):
        self.motif_s_info = {}
        self.PfamB_allowed = False

    def allow_PfamB(self):
        self.PfamB_allowed = True

    def read_swisspfam(self, swisspfam_file):
        re_prot = re.compile(r"^>(\S+)\s+\|=*\| (\S+) (\d+) a.a.")
        re_motf = re.compile(r"^(\S+) +(\d+)[ \-]+\((\d+)\) (\S+) (.*)")
        re_motp = re.compile(r"  (\d+\-\d+ *)+$")

        swisspfam = open(swisspfam_file, "r")
        motif_s_info = False

        for line in swisspfam:
            search_prot_res = re_prot.search(line)
            search_motf_res = re_motf.search(line)
            search_motp_res = re_motp.search(line)

            if search_prot_res:
                protein_ID_alt = search_prot_res.group(1)
                protein_ID     = search_prot_res.group(2)
                protein_len    = search_prot_res.group(3)
                motif_s_info = MotifDescr()
                motif_s_info.set_Protein_ID(protein_ID_alt)

            elif search_motf_res:
                motif         = search_motf_res.group(1)
                positions_str = search_motp_res.group()[2:]
                positions_arr = positions_str.split(" ")

                for pos_str in positions_arr:
                    pos1, pos2 = pos_str.split("-")
                    pos1, pos2 = int(pos1), int(pos2)
                    motif_s_info.set_motif(motif, pos1, pos2)


            elif motif_s_info:
                self.motif_s_info[ motif_s_info.get_protein_ID() ] = \
                                   motif_s_info

#                if len(self.motif_s_info) > 10:
#                    return

    def get_protein_IDs(self):
        return self.motif_s_info.keys()

    def has_protein_ID(self, protein_ID):
        return protein_ID in self.motif_s_info

    def get_motif_info(self, protein_ID):
        return self.motif_s_info.get(protein_ID, None)

    def save_motif_info(self, filter, savefile):

        filt = {}
        for filt_id in filter:
            filt[ filt_id ] = ""

        fh = open(savefile, "w")
        for protein in self.get_protein_IDs():
            if not filt.has_key(protein):
                continue
            motif_info = self.get_motif_info(protein)
            for motif in motif_info.get_motif():
                for pos in motif_info.get_motif_pos(motif):
                    fh.write(string.join((
                        protein, motif,
                        `pos[0]`, `pos[1]`), "\t") + "\n")

    def save_all_motif_info(self, savefile):

        fh = open(savefile, "w")
        for protein in self.get_protein_IDs():
            motif_info = self.get_motif_info(protein)
            for motif in motif_info.get_motif():
                for pos in motif_info.get_motif_pos(motif):
                    fh.write(string.join((
                        protein, motif,
                        `pos[0]`, `pos[1]`), "\t") + "\n")

    def load_motif_info(self, motif_file):
        swiss_pfam = Hash("A")
        swiss_pfam.read_file(filename = motif_file,
                             Key_cols = [0],
                             Val_cols = [1,2,3])
        for protein in swiss_pfam.keys():
            motif_s_info = MotifDescr()
            motif_s_info.set_Protein_ID(protein)
            reg_motif = False
            for motif_info in swiss_pfam.val(protein):
                motif, pos1, pos2 = motif_info.split("\t")
                pos1, pos2 = int(pos1), int(pos2)
                if (self.PfamB_allowed or motif[0:6] != "Pfam-B"):
                    motif_s_info.set_motif(motif, pos1, pos2)
                    reg_motif = True

            if reg_motif:
                self.motif_s_info[ protein ] = motif_s_info


if __name__ == "__main__":

    import string
    import sys
    from Seq_Packages.SwissProt.SwissProt1 import SwissProt
    from Usefuls.rsConfig import RSC_II

    rsc = RSC_II("rsIVV_Config")

    # swissprot_file  = rsc.SProt_Human
    # SWISS_PFAM_file = rsc.SwissPfam
    # swiss_pfam_file = rsc.SwissPfam_save

    swissprot_file  = rsc.SProt_Yeast
    SWISS_PFAM_file = rsc.SwissPfam_Yeast
    swiss_pfam_file = rsc.SwissPfam_save_Yeast

    sys.stderr.write("Reading SwissProt information ...\n")
    sprt = SwissProt(swissprot_file)
    sprt.set_keys("AC")
    sprt.load()

    sys.stderr.write("Reading swisspfam information ...\n")
    motif_set = Motif_swiss_set()
    motif_set.read_swisspfam(SWISS_PFAM_file)
    motif_set.save_motif_info(sprt.get_IDs(), swiss_pfam_file)


    """
    from Homology.Homology4_descr import Homology4_descr
    # The following is compatible to Homology1_descr

    SWISS_PFAM_file = "../../Motifs/swisspfam"
    # Old version. swiss_homl_file = "../../Homology/homol_ivv_human7.3_refseq_uniprot_sprot_simp_res95"
    # Old version. swiss_homl_bait_file = "../../Homology/homol_ivv_human7.3_Bait_Sprot_simp_res_1st"
    swiss_pfam_file = "../../Motifs/swisspfam_save"

    swiss_homl = Homology4_descr()
    swiss_homl.read_homol_file(swiss_homl_file)

    swiss_homl_bait = Homology4_descr()
    swiss_homl_bait.read_homol_file(swiss_homl_bait_file)

    motif_set = Motif_swiss_set()
    motif_set.allow_PfamB()

    motif_set.read_swisspfam(SWISS_PFAM_file)
    motif_set.save_motif_info(swiss_homl.hits() + swiss_homl_bait.hits(),
                              swiss_pfam_file)

    motif_set.load_motif_info(swiss_pfam_file)
    motif_info = motif_set.get_motif_info("CASP_HUMAN")
    for motif in motif_info.get_motif():
        print motif, motif_info.get_motif_pos(motif)
    """
