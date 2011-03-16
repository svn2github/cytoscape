#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
from Homol_measure import HM
from Usefuls.Hierarch_gothrough1 import squash, recur_apply

from Homology_term1 import *

class HomologyDescr:
    def __init__(self, homol_file):

        self.homol_file = homol_file
        self.query2subject = Hash("A")
        self.querysubject2hitinfo = Hash("A")

        self.term_query = [ t_query_ID ]
        self.term_subject = [ t_subject_ID ]
        self.term_homol = [ t_e_value,
                            t_identity_abs,
                            t_positive_abs,
                            t_overlap,
                            t_query_len,
                            t_subject_len,
                            t_query_start,
                            t_query_end,
                            t_subject_start,
                            t_subject_end ]

        self.query2subject.read_file_hd(filename = self.homol_file,
                                        Key_cols_hd = self.term_query,
                                        Val_cols_hd = self.term_subject)

        self.querysubject2hitinfo.read_file_hd(
            filename = self.homol_file,
            Key_cols_hd = self.term_query + self.term_subject,
            Val_cols_hd = self.term_homol)

        self.info_num = {}
        for i in range(len(self.term_homol)):
            self.info_num[self.term_homol[i]] = i

        self.reverse_enabled = False

    def enable_reverse(self):

        if not self.reverse_enabled:
            self.subject2query = Hash("A")
            self.subject2query.read_file_hd(filename = self.homol_file,
                                            Key_cols_hd = self.term_subject,
                                            Val_cols_hd = self.term_query)
            self.reverse_enabled = True

    def queries(self):
        return self.query2subject.keys()

    def subjects(self):
        return self.subject2query.keys()

    def subject_ID(self, query_ID):
        return self.query2subject.val_force(query_ID)

    def hits(self):
        """ Return hits (subject IDs). This does not care query IDs """

        subject_IDs = {}
        for query_ID in self.queries():
            for subject_ID in self.subject_ID(query_ID):
                subject_IDs[ subject_ID ] = ""
        return subject_IDs.keys()

    def e_value(self, query_ID, subject_ID):

        hit_info = self.querysubject2hitinfo.val_force(query_ID, subject_ID)
        if hit_info == []: return None
        homol_info = hit_info[0].split("\t")
        return float(homol_info[self.info_num[t_e_value]])

    def identity(self, query_ID, subject_ID):

        hit_info = self.querysubject2hitinfo.val_force(query_ID, subject_ID)
        if hit_info == []: return None
        homol_info = hit_info[0].split("\t")
        return 1.0 * int(homol_info[self.info_num[t_identity_abs]]) / \
               self.overlap(query_ID, subject_ID)

    def positive(self, query_ID, subject_ID):

        hit_info = self.querysubject2hitinfo.val_force(query_ID, subject_ID)
        if hit_info == []: return None
        homol_info = hit_info[0].split("\t")
        return 1.0 * int(homol_info[self.info_num[t_positive_abs]]) / \
               self.overlap(query_ID, subject_ID)

    def overlap(self, query_ID, subject_ID):

        hit_info = self.querysubject2hitinfo.val_force(query_ID, subject_ID)
        if hit_info == []: return None
        homol_info = hit_info[0].split("\t")
        return int(homol_info[self.info_num[t_overlap]])

    def hm(self, query_ID, subject_ID):

        return HM(eval = self.e_value(query_ID, subject_ID),
                  identity = self.identity(query_ID, subject_ID),
                  positive = self.positive(query_ID, subject_ID),
                  overlap  = self.overlap(query_ID, subject_ID),
                  hit_len_ratio_query =
                  1.0 * (self.query_end(query_ID, subject_ID) -
                         self.query_start(query_ID, subject_ID) + 1)
                  / self.query_len(query_ID, subject_ID),
                  hit_len_ratio_subj =
                  1.0 * (self.subject_end(query_ID, subject_ID) -
                         self.subject_start(query_ID, subject_ID) + 1)
                  / self.subject_len(query_ID, subject_ID))

    def homologs(self, query_ID):

        homolog_info = {}
        for subject_ID in self.subject_ID(query_ID):
            evalue = self.e_value(query_ID, subject_ID)
            homolog_info[ subject_ID ] = evalue

        return homolog_info

    def query_len(self, query_ID, subject_ID = None): # subject_ID is actually unnecessary.

        if subject_ID == None:
            subject_ID = self.subject_ID(query_ID)[0]

        homol_info = self.querysubject2hitinfo. \
                     val_force(query_ID, subject_ID)[0].split("\t")
        return int(homol_info[self.info_num[t_query_len]])

    def subject_len(self, query_ID, subject_ID): # query_ID is actually unnecessary.

        homol_info = self.querysubject2hitinfo. \
                     val_force(query_ID, subject_ID)[0].split("\t")
        return int(homol_info[self.info_num[t_subject_len]])

    def subject_len2(self, subject_ID): # reverse must be enabled.

        reverse_query_IDs = self.reverse_query_ID(subject_ID)
        if len(reverse_query_IDs) == 0:
            return None
        query_ID = reverse_query_IDs[0]
        return self.subject_len(query_ID, subject_ID)

    def query_start(self, query_ID, subject_ID):

        homol_info = self.querysubject2hitinfo. \
                     val_force(query_ID, subject_ID)[0].split("\t")
        return int(homol_info[self.info_num[t_query_start]])

    def query_end(self, query_ID, subject_ID):

        homol_info = self.querysubject2hitinfo. \
                     val_force(query_ID, subject_ID)[0].split("\t")
        return int(homol_info[self.info_num[t_query_end]])

    def subject_start(self, query_ID, subject_ID):

        homol_info = self.querysubject2hitinfo. \
                     val_force(query_ID, subject_ID)[0].split("\t")
        return int(homol_info[self.info_num[t_subject_start]])

    def subject_end(self, query_ID, subject_ID):

        homol_info = self.querysubject2hitinfo. \
                     val_force(query_ID, subject_ID)[0].split("\t")
        return int(homol_info[self.info_num[t_subject_end]])

    def reverse_query_ID(self, subject_ID):
        return self.subject2query.val_force(subject_ID)


    def subject_ID_evalue_thres(self, query_ID, evalue_thres):
        subject_IDs = self.subject_ID(query_ID)

        ret_subject_IDs = []
        for subject_ID in subject_IDs:
            if self.e_value(query_ID, subject_ID) <= evalue_thres:
                    ret_subject_IDs.append(subject_ID)

        return ret_subject_IDs

    def subject_ID_evalue_identity_overlap_thres(self, query_ID,
                                                 evalue_thres,
                                                 identity_thres,
                                                 overlap_thres):
        subject_IDs = self.subject_ID(query_ID)

        ret_subject_IDs = []
        for subject_ID in subject_IDs:
            if (self.e_value(query_ID, subject_ID) <= evalue_thres and
                self.identity(query_ID, subject_ID) >= identity_thres and
                self.overlap(query_ID, subject_ID) >= overlap_thres):
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

    def reverse_query_ID_evalue_identity_overlap_thres(
        self, subject_ID, evalue_thres, identity_thres, overlap_thres):

        reverse_query_IDs = self.reverse_query_ID(subject_ID)

        ret_query_IDs = []

        for query_ID in reverse_query_IDs:
            evalue   = self.e_value(query_ID, subject_ID)
            identity = self.identity(query_ID, subject_ID)
            overlap  = self.overlap(query_ID, subject_ID)
            if (evalue <= evalue_thres and
                identity >= identity_thres and
                overlap >= overlap_thres):
                ret_query_IDs.append(query_ID)

        return ret_query_IDs

    def subject_ID_hm_thres(self, query_ID, hm):

        if not isinstance(hm, HM):
            return self.subject_ID_evalue_thres(query_ID, hm)

        subject_IDs = self.subject_ID(query_ID)

        ret_subject_IDs = []
        for subject_ID in subject_IDs:
            if hm.eval(self.hm(query_ID, subject_ID)):
                ret_subject_IDs.append(subject_ID)

        return ret_subject_IDs

    def reverse_query_ID_hm_thres(self, subject_ID, hm):

        if not isinstance(hm, HM):
            return self.reverse_query_ID_evalue_thres(subject_ID, hm)

        reverse_query_IDs = self.reverse_query_ID(subject_ID)

        ret_query_IDs = []
        for query_ID in reverse_query_IDs:
            if hm.eval(self.hm(query_ID, subject_ID)):
                ret_query_IDs.append(query_ID)

        return ret_query_IDs


if __name__ == "__main__":
    homol_file = "../../Homology/homol_ivv_human8.0-human_refseq"
    homol_file = "HomologyDescr_test"

    homol = HomologyDescr(homol_file)
    homol.enable_reverse()

    print homol.hits()
    print "Homolog:", homol.homologs("NM_003802")
    print homol.reverse_query_ID("TAF1_HUMAN")
    print homol.reverse_query_ID("XXXXX"), "XXXX"

    print "E-value:", homol.e_value("NM_004606", "TAF1_HUMAN")

    print homol.subject_ID("XXXXX"), "XXXXX"

    print "----"
    print homol.subject_ID_evalue_thres("NM_003802", 2.0)
    print homol.subject_ID_evalue_identity_overlap_thres("NM_004606", 2.0, 0.71, 100)
    print homol.reverse_query_ID_evalue_thres("TAF1_HUMAN", 5.0)
    print homol.subject_len2("TAF1L_HUMAN")
    print "#####"

    print homol.hm("NM_004606", "TAF1_HUMAN")
    print homol.subject_ID_hm_thres("NM_003802", HM(1.0, None, None, None))
    print homol.reverse_query_ID_hm_thres("TAF1_HUMAN", HM(5, 0.8, None, 90))

    """
    for query in homol.queries():
        for subject in homol.subject_ID(query):
            print query, subject
            print "Homol", homol.hm(query, subject)
            print "E-value", homol.e_value(query, subject)
            print "Identity", homol.identity(query, subject)
            print "Overlap", homol.overlap(query, subject)
            print "Query length", homol.query_len(query) #, subject)
            print "Subject length", homol.subject_len(query, subject)
            print "Query start", homol.query_start(query, subject)
            print "Query end", homol.query_end(query, subject)
            print "Subject start", homol.subject_start(query, subject)
            print "Subject end", homol.subject_end(query, subject)
            print
    """
