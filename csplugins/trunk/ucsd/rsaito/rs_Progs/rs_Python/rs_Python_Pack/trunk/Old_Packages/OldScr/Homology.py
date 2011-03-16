#!/usr/bin/python

import sys
import Data_Struct.Hash

class Homology:

    def __init__(self, homology_file):

        self.homology = Data_Struct.Hash.Hash("A")
        self.homology.read_file(filename = homology_file,
                                Key_cols = [0],
                                Val_cols = [1,2])

    def homologs(self, query):
        if self.homology.has_key(query):
            hms = self.homology.val(query)
            homolog_info = {}
            for hm in hms:
                [ subj, eval ] = hm.split("\t")
                homolog_info[ subj ] = eval
            return homolog_info
        else:
            return {}

if __name__ == "__main__":
    homol = Homology("../Homology/homol_ivv_human7.3.tfa-human.protein.faa_simp_res")
    print homol.homologs("NM_006703.2")

