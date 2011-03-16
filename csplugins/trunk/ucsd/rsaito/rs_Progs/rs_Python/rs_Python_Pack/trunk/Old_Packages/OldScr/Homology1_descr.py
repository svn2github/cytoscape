#!/usr/bin/env python

import sys
sys.path.append("../")

import Data_Struct.Hash
from Usefuls.Usefuls1 import squash, recur_apply

class Homology1_descr:
    def __init__(self, mode = "S"):
        self.homol = Data_Struct.Hash.Hash_headf(mode)


    def read_homol_file(self, homol_file,
                        terms = [ [ "Query ID" ],
                                  [  "Subject ID",
                                     "E-value",
                                     "Identity",
                                     "Positive",
                                     "Overlap",
                                     "Query length",
                                     "Subject length",
                                     "Query start",
                                     "Query end",
                                     "Subject start",
                                     "Subject end" ]]):

        key_cols_hd, val_cols_hd = terms
        self.homol.read_file(filename = homol_file,
                             Key_cols_hd = key_cols_hd,
                             Val_cols_hd = val_cols_hd)

    def keys(self):
        return self.homol.keys()

    def vals(self, term):
        ret = []
        for key in self.keys():
            ret.append(self.homol.val_accord_hd(key, term))
        return ret

    def hits(self):
        if self.homol.get_val_type() == "S":
            return self.vals("Subject ID")
        elif self.homol.get_val_type() == "A":
            return squash(self.vals("Subject ID"))

    def homologs(self, query_ID):
        hit_IDs = self.subject_ID(query_ID)
        hit_evs = self.e_value(query_ID)

        homolog_info = {}
        for i in range(len(hit_IDs)):
            homolog_info[ hit_IDs[i] ] = hit_evs[i]

        return homolog_info

    def subject_ID(self, query_ID):
        return self.homol.val_accord_hd(query_ID, "Subject ID")

    def e_value(self, query_ID):
        return recur_apply(float,
                           (self.homol.val_accord_hd(query_ID,
                                                     "E-value")))

    def query_len(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Query length"))

    def subject_len(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Subject length"))

    def query_start(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Query start"))

    def query_end(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Query end"))

    def subject_start(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Subject start"))

    def subject_end(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Subject end"))

if __name__ == "__main__":
    homol_file = "../../Homology/homol_ivv_human8.0-human_refseq"
    homol_file = "tmp"

    homol = Homology1_descr("A")
    homol.read_homol_file(homol_file)

    print homol.hits()
    print homol.homologs("NM_003802")

    for query in homol.keys():
        print query, homol.subject_ID(query), homol.e_value(query), homol.query_len(query), homol.subject_len(query), homol.query_start(query), homol.query_end(query), homol.subject_start(query), homol.subject_end(query)

