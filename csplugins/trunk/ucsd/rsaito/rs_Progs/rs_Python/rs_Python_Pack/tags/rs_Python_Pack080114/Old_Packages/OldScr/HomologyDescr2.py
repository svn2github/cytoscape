#!/usr/bin/env python

import sys
sys.path.append("../")

import Data_Struct.Hash_A
from Usefuls.Usefuls1 import squash, recur_apply

class HomologyDescr2:
    def __init__(self, homol_file):
        self.homol = Data_Struct.Hash_A.Hash_headf_A()
        self.homol_file = homol_file

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
                     "Subject end" ]]

        key_cols_hd, val_cols_hd = terms
        self.homol.read_file(filename = self.homol_file,
                             Key_cols_hd = key_cols_hd,
                             Val_cols_hd = val_cols_hd)

    def enable_reverse(self):

        terms = [ [ "Subject ID" ], [ "Query ID" ] ]

        self.homol_rev = Data_Struct.Hash_A.Hash_headf_A()
        key_cols_hd, val_cols_hd = terms
        self.homol_rev.read_file(filename = self.homol_file,
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
        """ Return hits (subject IDs). This does not care query IDs """
        return squash(self.vals("Subject ID"))

    def homologs(self, query_ID):
        hit_IDs = self.subject_ID(query_ID)
        hit_evs = self.e_value_all(query_ID)

        homolog_info = {}
        for i in range(len(hit_IDs)):
            homolog_info[ hit_IDs[i] ] = hit_evs[i]

        return homolog_info

    def subject_ID(self, query_ID):
        subject_IDs = self.homol.val_accord_hd(query_ID, "Subject ID")
        if subject_IDs is None:
            return []
        else:
            return subject_IDs

    def e_value_all(self, query_ID):
        return recur_apply(float,
                           (self.homol.val_accord_hd(query_ID,
                                                     "E-value")))

    def e_value(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.e_value_all(query_ID)[ subject_ID_idx ]

    def query_len_all(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Query length"))

    def query_len(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.query_len_all(query_ID)[ subject_ID_idx ]

    def subject_len_all(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Subject length"))

    def subject_len(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.subject_len_all(query_ID)[ subject_ID_idx ]

    def query_start_all(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Query start"))

    def query_start(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.query_start_all(query_ID)[ subject_ID_idx ]

    def query_end_all(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Query end"))

    def query_end(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.query_end_all(query_ID)[ subject_ID_idx ]

    def subject_start_all(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Subject start"))
    def subject_start(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.subject_start_all(query_ID)[ subject_ID_idx ]


    def subject_end_all(self, query_ID):
        return recur_apply(int,
                           self.homol.val_accord_hd(query_ID,
                                                    "Subject end"))

    def subject_end(self, query_ID, subject_ID):
        subject_IDs = self.subject_ID(query_ID)
        subject_ID_idx = subject_IDs.index(subject_ID)
        return self.subject_end_all(query_ID)[ subject_ID_idx ]

    def reverse_query_ID(self, subject_ID):
        query_IDs = self.homol_rev.val_accord_hd(subject_ID, "Query ID")
        if query_IDs is None:
            return []
        else:
            return query_IDs


    def subject_ID_evalue_thres(self, query_ID, evalue_thres):
        subject_IDs = self.subject_ID(query_ID)
        evalues = self.e_value_all(query_ID)

        ret_subject_IDs = []
        for i in range(len(subject_IDs)):
            subject_ID = subject_IDs[i]
            evalue = evalues[i]
            if evalue <= evalue_thres:
                ret_subject_IDs.append(subject_ID)

        return ret_subject_IDs


    def reverse_query_ID_evalue_thres(self, subject_ID, evalue_thres):
        reverse_query_IDs = self.reverse_query_ID(subject_ID)

        ret_query_IDs = []

        for query_ID in reverse_query_IDs:
            evalue = self.e_value(query_ID, subject_ID)
            if evalue <= evalue_thres:
                ret_query_IDs.append(query_ID)

        return ret_query_IDs


if __name__ == "__main__":
    homol_file = "../../Homology/homol_ivv_human8.0-human_refseq"
    homol_file = "HomologyDescr_test"

    homol = HomologyDescr2(homol_file)
    homol.enable_reverse()

    print homol.hits()
    print homol.homologs("NM_003802")
    print homol.reverse_query_ID("TAF1_HUMAN")
    print homol.reverse_query_ID("XXXXX"), "XXXX"

    print homol.e_value("NM_004606", "TAF1_HUMAN")

    print homol.subject_ID("XXXXX"), "XXXXX"

    print homol.subject_ID_evalue_thres("NM_003802", 2.0)
    print homol.reverse_query_ID_evalue_thres("TAF1_HUMAN", 8.0)


    """
    for query in homol.keys():
        for subject in homol.subject_ID(query):
            print query, subject
            print "E-value", homol.e_value(query, subject)
            print "Query length", homol.query_len(query, subject)
            print "Subject length", homol.subject_len(query, subject)
            print "Query start", homol.query_start(query, subject)
            print "Query end", homol.query_end(query, subject)
            print "Subject start", homol.subject_start(query, subject)
            print "Subject end", homol.subject_end(query, subject)
            print

    """
