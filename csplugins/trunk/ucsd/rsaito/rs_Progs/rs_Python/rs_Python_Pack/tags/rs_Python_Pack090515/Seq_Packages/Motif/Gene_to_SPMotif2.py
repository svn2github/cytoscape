#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
from Seq_Packages.SwissProt.SwissProt1 import SwissProt
import Usefuls.ListProc1
import Usefuls.Table_maker
from Seq_Packages.Motif.SwissPfam import Motif_swiss_set

class Hash_keyword_filt(Hash):

    def set_keywords(self, keywords, col):
        self.filt_keywords = Usefuls.ListProc1.list_to_dict(keywords)
        self.filt_keywords_col = col

    def filt_line(self, r):

        r[ self.filt_keywords_col ] = r[ self.filt_keywords_col ].split(".")[0]
        # Eliminates version number

        if (r[ self.filt_keywords_col ] in
            self.filt_keywords):
            return False
        else:
            return True

class Gene_to_SPMotif:

    def __init__(self,
                 swissprot_file,
                 gene2accession_file,
                 swisspfam_file):

        geneid_col = 1
        accid_col  = 5

        sprt = SwissProt(swissprot_file)
        sprt.set_keys("AC")
        sprt.load()
        self.ac_to_id = sprt.accession_to_id()

        self.g2a = Hash_keyword_filt("A")
        self.g2a.set_keywords(self.ac_to_id.keys(), accid_col)
        self.g2a.read_file(filename = gene2accession_file,
                           Key_cols = [geneid_col], Val_cols = [accid_col])
 
        self.motif_set = Motif_swiss_set()
        self.motif_set.load_motif_info(swisspfam_file)


    def geneid_to_swpracc(self, geneid):

        swpracc = self.g2a.val_force(geneid)
        if swpracc == "":
            return False
        else:
            return swpracc

    def eswpracc_to_swprid(self, eswpracc):

        if self.ac_to_id.has_key(eswpracc):
            return self.ac_to_id.ret_set_Dict(eswpracc)
        else:
            return False

    def swprid_to_motif(self, eswprid):
        if self.motif_set.has_protein_ID(eswprid):
            return self.motif_set.get_motif_info(eswprid)
        else:
            return False


    def geneid_to_motif(self, geneid):

        ret_motif = {}

        swpracc = self.geneid_to_swpracc(geneid)
        if swpracc is False:
            return False


        for eswpracc in swpracc:
            swpid = self.eswpracc_to_swprid(eswpracc)
            if not swpid is False:
                for eswpid in swpid:
                    motif_info = self.swprid_to_motif(eswpid)
                    if not motif_info is False:
                        for emotif in motif_info.get_motif():
                            ret_motif[ emotif ] = ""

        return ret_motif.keys()

    def all_valid_geneids(self):

        ret_geneids = {}

        for geneid in self.g2a.keys():
            if self.geneid_to_motif(geneid):
                ret_geneids[ geneid ] = ""

        return ret_geneids.keys()

    def output_all(self):

        output = Usefuls.Table_maker.Table_row()

        all_valid_geneids = self.all_valid_geneids()

        for geneid in all_valid_geneids:
            output.append("Gene ID", geneid)
            swpracc = self.geneid_to_swpracc(geneid)
            for eswpracc in swpracc:
                output.append("SwissProt Accession", eswpracc)
                swprid = self.eswpracc_to_swprid(eswpracc)
                if swprid is False:
                    continue
                for eswprid in swprid:
                    output.append("SwissProt ID", eswprid)
                    motif_info = self.swprid_to_motif(eswprid)
                    if motif_info is False:
                        continue
                    for motif in motif_info.get_motif():
                        output.append("Motif", motif)
                        for pos in motif_info.get_motif_pos(motif):
                            pos1, pos2 = pos
                            output.append("Position 1", `pos1`)
                            output.append("Position 2", `pos2`)
                            output.output("\t")

if __name__ == "__main__":

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    # gtsp = Gene_to_SPMotif(rsc.SProt_Human,
    #                        rsc.Gene2Accession,
    #                        rsc.SwissPfam_save)

    gtsp = Gene_to_SPMotif(rsc.SProt_Yeast,
                           rsc.Gene2Accession,
                           rsc.SwissPfam_save_Yeast)

    """ This is the easiest way to convert gene ID directly to
    protein motifs """
    # print gtsp.geneid_to_motif("2353")
    gtsp.output_all()

