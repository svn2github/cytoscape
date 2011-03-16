#!/usr/bin/env python

import Feature_Extraction1
from Data_Struct.Dict_Ordered import Dict_Ordered

class Feature_Ext_Set:
    def __init__(self, label = None):
        self.feature_ext_set = Dict_Ordered()
        self.label = label

    def set_feature_ext(self, name, filename, scaling_featext = None):
        self.feature_ext_set[ name ] = Feature_Extraction1.Feature_Ext(filename)
        if scaling_featext:
            self.feature_ext_set[ name ].set_scaling_factor(scaling_featext)

    def get_feature_ext(self, name):
        return self.feature_ext_set[ name ]

    def set_feature_exts(self, idict, scaling_featext = None):
        for name in idict:
            self.set_feature_ext(name, idict[name], scaling_featext)

    def get_data_names(self):
        return self.feature_ext_set.keys()

    def get_label(self):
        return self.label

    def get_exp_pat(self, names, genename):
        exppat = []
        for name in names:
            featext = self.get_feature_ext(name)
            exppat.append(featext.
                          get_gProcessedSignal_from_genename_s(genename))
        return exppat

    def get_gIsPosAndSignif(self, names, genename):
        exppat = []
        for name in names:
            featext = self.get_feature_ext(name)
            exppat.append(featext.
                          get_gIsPosAndSignif_from_genename_s(genename))
        return exppat

    def get_gIsWellAboveBG(self, names, genename):
        exppat = []
        for name in names:
            featext = self.get_feature_ext(name)
            exppat.append(featext.
                          get_gIsWellAboveBG_from_genename_s(genename))
        return exppat

if __name__ == "__main__":
    
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")
    from SAT_Packages.SAT11K.Human_Cancer11k_Global \
        import colon_dT, colon_normal_keys, colon_cancer_keys

    print "Reading Brain_dT ..."
    feset = Feature_Ext_Set()
    feset.set_feature_ext("Brain_dT", rsc.FE_Human11k_Brain_dT)
    print "Successful."
    print "Brain_dT average", feset.get_feature_ext("Brain_dT").calc_average()

    print "Reading Colon dT ..."
    feset_colon_dT = Feature_Ext_Set()
    feset_colon_dT.set_feature_exts(colon_dT)
    print "Successful."
    print "Colon_N2 average", feset_colon_dT.get_feature_ext("Colon_N2").calc_average()
    print feset_colon_dT.get_exp_pat(colon_normal_keys, 
                                     "AFAS-Onc-Anti-A03911-01")
    feset_colon_dT.set_feature_exts(colon_dT, feset.get_feature_ext("Brain_dT"))

    print "Scaled Colon_N2 average", feset_colon_dT.get_feature_ext("Colon_N2").calc_average()  

    print feset_colon_dT.get_exp_pat(colon_normal_keys, 
                                     "AFAS-Onc-Anti-A03911-01")
    print feset_colon_dT.get_gIsPosAndSignif(colon_normal_keys,
                                             "AFAS-Onc-Anti-A03911-01")
    print feset_colon_dT.get_gIsWellAboveBG(colon_normal_keys,
                                            "AFAS-Onc-Anti-A03911-01")
    print feset_colon_dT.get_data_names()


