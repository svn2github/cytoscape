#!/usr/bin/env python

import string

from Usefuls.Instance_check import instance_class_check
import Usefuls.Array_regist1
import Homology4_descr
import Usefuls.Counter
import IVV_info.IVV_info
import IVV_info.Prey_info
from Homol_measure import HM

class Homology_map1:
    # Manipulating self.map directly may cause missuse of
    # sequence positions.
    def __init__(self, homology, subjectid, hm):
        instance_class_check(homology, Homology4_descr.HomologyDescr4)

        self.homology = homology
        self.homology.enable_reverse()

        if self.homology.subject_len2(subjectid) is None:
            self.status = False
            return

        self.subject_len = string.atoi(self.homology.subject_len2(subjectid))
        self.map = Usefuls.Array_regist1.Array_regist(self.subject_len)
        self.hm = hm

        query_IDs = self.homology.reverse_query_ID_hm_thres(
            subjectid, hm)

        for queryid in query_IDs:
            start = string.atoi(
                self.homology.subject_start(queryid, subjectid))
            end = string.atoi(
                self.homology.subject_end(queryid, subjectid))
            self.map.register_range(queryid, start - 1, end - 1)

        self.status = True

    def get_status(self):
        return self.status

    def get(self, pos):
        return self.map.get(pos - 1)

    def display_all(self):
        for pos in range(1, self.subject_len + 1):
            print pos, self.get(pos)


if __name__ == "__main__":

    """
    homol_file = "HomologyDescr_test"
    homol = Homology4_descr.HomologyDescr4(homol_file)
    homol.enable_reverse()
    map = Homology_map1(homol, "TAF1_HUMAN", 10)
    map.display_all()
    print map.get(600)
    """

