#!/usr/bin/env python

from Data_Struct.MultiDimDict1 import MultiDimDict, MultiDimDict_L
from Data_Struct.DictSet1 import DictSet

import IVV_Packages.Integration.IVV_Global_Center as IVV_Global
import IVV_Packages.Integration.IVV_Global_Center_II

from Seq_Packages.Homology.Homol_measure import HM

class StepBack_KnownPPI:
    def __init__(self):
        self.info_bp_knownppi = MultiDimDict_L(2)
        self.info_knownppi_bp = MultiDimDict_L(2)
        self._rec = MultiDimDict(2)
        
    def add_info(self, bait, prey, knownppi_gene1, knownppi_gene2):
        self.info_bp_knownppi.add_item((bait, prey),
                                      (knownppi_gene1, knownppi_gene2))

        self.info_knownppi_bp.add_item((knownppi_gene1, knownppi_gene2),
                                      (bait, prey))

    def get_info_from_bp(self, bait, prey):
        return self.info_bp_knownppi.get_val((bait, prey))

    def validate_by_stepback(self, p1, p2):
        if self._rec.get_val((p1, p2)):
            return self._rec.get_val((p1, p2))
        
        validated_by = {}
        ivv_pred = IVV_Packages.Integration.IVV_Global_Center_II.get_ppi_pred()
        source = ivv_pred.gene_to_ivv_common_bait_descr(p1, p2)
        bps = source.Bait_Prey()
        for bp in bps:
            bait = bp.get_source1()[0]
            preys = bp.get_source2()
            for prey in preys:
                evids = self.get_info_from_bp(bait, prey)
                if evids:
                    validated_by[(bait, prey)] = evids
        
        self._rec.set_val((p1, p2), validated_by)
        return validated_by


    def validate_by_stepback_info1(self, p1, p2):
        
        validated_by = self.validate_by_stepback(p1, p2)
        if not validated_by.keys():
            return 0, 0, 0

        bait_h  = {}
        prey_h  = {}  # There may be an easier way to count it...
        kppis_h = {}
        
        for bait_prey in validated_by:
            bait, prey = bait_prey
            kppis      = validated_by[bait_prey]

            bait_h[ bait ] = ""
            prey_h[ prey ] = ""
            for kppi in kppis:
                kp1, kp2 = kppi
                kppis_h[ (kp1, kp2 ) ] = ""
        
        return len(bait_h), len(prey_h), len(kppis_h)
        
    def validate_by_stepback_info2(self, p1, p2):
        
        validated_by = self.validate_by_stepback(p1, p2)
        if not validated_by.keys():
            return {}

        kppi_bait_to_prey = DictSet()

        for bait_prey in validated_by:
            bait, prey = bait_prey
            kppis      = validated_by[bait_prey]

            for kppi in kppis:
                kp1, kp2 = kppi
                kppi_bait_to_prey.append(((kp1, kp2), bait),
                                         prey)
        
        return kppi_bait_to_prey


def construct_StepBack_KnownPPI():
    """ This function should be called after proper IVV data processing and
    PPI prediction """

    reported_ppi    = IVV_Global.get_reported_ppi2()
    ivv_pred        = IVV_Packages.Integration.IVV_Global_Center_II.get_ppi_pred()

    bp_knownPPI = StepBack_KnownPPI()
    
    spoke = ivv_pred.get_spoke()
    for p1 in spoke:
        for p2 in spoke[p1]:
            if reported_ppi.has_pair(p1, p2):
                # print p1, p2
                source = ivv_pred.gene_to_ivv_common_bait_descr(p1, p2)
                bps = source.Bait_Prey()
                for bp in bps:
                    bait = bp.get_source1()[0]
                    preys = bp.get_source2()
                    for prey in preys:
                        bp_knownPPI.add_info(bait, prey, p1, p2)
                        # print bait, prey
    
    return bp_knownPPI


if __name__ == "__main__":
    
    IVV_Global.set_filter_mode(True)
    ivv_pred = IVV_Packages.Integration.IVV_Global_Center_II.calc_ppipred()
    bp_knownPPI = construct_StepBack_KnownPPI()
    
    spoke = ivv_pred.get_spoke()
    
    for p1 in spoke:
        for p2 in spoke[p1]:
            # print bp_knownPPI.validate_by_stepback_info1(p1, p2)
            if len(bp_knownPPI.validate_by_stepback(p1, p2).keys()) > 1:
                print p1, p2, bp_knownPPI.validate_by_stepback(p1, p2)
                print bp_knownPPI.validate_by_stepback_info2(p1, p2)
    
    
    