#!/usr/bin/env python

import Seq_Packages.Homology.Homology_map as Homology_map

class Homology_map1_preys(Homology_map.Homology_map):
    def __init__(self, homology, subjectid, hm, ivv_info):
        instance_class_check(ivv_info, IVV_info.IVV_info.IVV_info)

        Homology_map.Homology_map.__init__(self, homology, subjectid, hm)
        self.ivv_info = ivv_info

    def get_preys(self, pos):
        preys = []
        for hit in self.get(pos):
            if self.ivv_info.ID_Type(hit) == "Prey":
                preys.append(hit)
        return IVV_info.Prey_info.Prey_Set(self.ivv_info.Prey_info(), 
                                           preys)

    def get_preys_invalid_MOCK(self, pos):
        """ If you are using IVV filters, this function will
        not work properly as Mock may be eliminated by the filter. """
        
        preys = []
        for hit in self.get(pos):
            if self.ivv_info.ID_Type(hit) == "Prey":
                bait_ID = self.ivv_info.Prey_info().bait_ID(hit)
                if self.ivv_info.Bait_info().bait_type(bait_ID) == "Mock":
                    return IVV_info.Prey_info.Prey_Set(
                        self.ivv_info.Prey_info(), [])
                preys.append(hit)

        return IVV_info.Prey_info.Prey_Set(self.ivv_info.Prey_info(), 
                                           preys)

    def get_preys_invalid_MOCK2(self, pos):
        preys = []
        for hit in self.get(pos):
            if self.ivv_info.ID_Type(hit) == "Prey":
                if (self.ivv_info.Prey_info().get_qual_noerror(hit, "mock")
                    == "1"):
                    print hit, "is MOCK"
                    return IVV_info.Prey_info.Prey_Set(
                        self.ivv_info.Prey_info(), [])
                preys.append(hit)

        return IVV_info.Prey_info.Prey_Set(self.ivv_info.Prey_info(), 
                                           preys)

    def count_preys(self, pos):
        return len(self.get_preys(pos))

    def count_preys_invalid_MOCK(self, pos):
        return len(self.get_preys_invalid_MOCK(pos))

    def count_bait_geneids(self, pos, reprod_thres):
        prey_set = self.get_preys(pos)
        # print "POS:", pos, "Preys:", prey_set.get_Preys()
        return prey_set.count_bait_geneids(reprod_thres,
                                           self.ivv_info.Bait_info())

    def count_bait_geneids_invalid_MOCK(self, pos, reprod_thres):
        prey_set = self.get_preys_invalid_MOCK(pos)
        # print "POS:", pos, "Preys:", prey_set.get_Preys()
        return prey_set.count_bait_geneids(reprod_thres,
                                           self.ivv_info.Bait_info())

    def max_count_bait_geneids(self, reprod_thres):
        max = 0
        for pos in range(1, self.subject_len + 1):
            count = self.count_bait_geneids(pos, reprod_thres)
            if max < count:
                max = count
        return max

    def max_count_bait_geneids_invalid_MOCK(self, reprod_thres):
        max = 0
        for pos in range(1, self.subject_len + 1):
            count = self.count_bait_geneids_invalid_MOCK(pos, reprod_thres)
            if max < count:
                max = count
        return max

    def max_count_preys_invalid_MOCK(self):
        max = 0
        for pos in range(1, self.subject_len + 1):
            count = self.count_preys_invalid_MOCK(pos)
            if max < count:
                max = count
        return max

    def check_MOCK(self):
        for pos in range(1, self.subject_len + 1):
            preys = self.get_preys(pos)
            if preys.check_MOCK(self.ivv_info.Bait_info()):
                return True
        return False

    def display_preys(self, reprod_thres):
        for pos in range(1, self.subject_len + 1):
            print pos, self.get_preys(pos).get_PreyIDs(), self.count_bait_geneids(pos, reprod_thres), self.get_preys(pos).check_MOCK(self.ivv_info.Bait_info())

if __name__ == "__main__":
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")
    import IVV_Packages.IVV_Info.IVV_info1 as IVV_info
    ivv_info = IVV_info.IVV_info(rsc.IVVInfo)

    homol_ivv_refseq = Homology4_descr.HomologyDescr4(rsc.HomolIVVRefSeq)

    map1 = Homology_map1_preys(homol_ivv_refseq, "4826806",
                               HM(1.0e-30, 0.0, 0.0, 30),
                               ivv_info)
    map1.display_preys(1)
    print map1.count_bait_geneids(559, 1)
    print map1.count_bait_geneids_invalid_MOCK(559, 1)
    print map1.count_preys(509)
    print map1.count_preys_invalid_MOCK(509)
    print map1.max_count_bait_geneids(1)
    print map1.max_count_bait_geneids_invalid_MOCK(1)
    print map1.max_count_preys_invalid_MOCK()
    print map1.check_MOCK()

    """
    map1 = Homology_map1_preys(homol_ivv_refseq, "55741641", 1.0,
                               ivv_info)
    map1.display_preys(1)
    print map1.count_bait_geneids(500, 1)
    print map1.max_count_bait_geneids(1)
    print map1.check_MOCK()
    print map1.count_preys_invalid_MOCK(304)
    """
    
