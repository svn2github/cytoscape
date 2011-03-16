#!/usr/bin/env python

import Data_Struct.Hash2_II as Hash
from Usefuls.Instance_check import instance_class_check

class Feature_Ext_File_IO(Hash.Hash_II):
    def pre_read_file(self):
        while True:
            line = self.fh.readline()
            if line.startswith("FEATURES"):
                break
        self.fh.seek(-len(line), 1)

class Feature_Ext:
    def __init__(self, filename,
                 info_keys = None):
        self.filename = filename
        self.featureNum_to_info = Feature_Ext_File_IO("S")
        
        if info_keys is None:
            info_keys = ["ProbeName",
                         # "Sequence",
                         "GeneName",
                         "SystematicName",
                         "gProcessedSignal",
                         "gIsPosAndSignif",
                         "gIsWellAboveBG",
                         "gIsSaturated"]
        
        self.featureNum_to_info.read_file_hd(self.filename,
                                             Key_cols_hd = ["FeatureNum"],
                                             Val_cols_hd = info_keys,
                                             typeconv = { "gProcessedSignal" : float,
                                                          "gIsPosAndSignif"  : int,
                                                          "gIsWellAboveBG"   : int,
                                                          "gIsSaturated"     : int })
        
        self.genename_to_featureNums = Feature_Ext_File_IO("A")
        self.genename_to_featureNums.read_file_hd(self.filename,
                                                  Key_cols_hd = ["GeneName"],
                                                  Val_cols_hd = ["FeatureNum"])
        self.probe_to_featureNums = Feature_Ext_File_IO("A")
        self.probe_to_featureNums.read_file_hd(self.filename,
                                               Key_cols_hd = ["ProbeName"],
                                               Val_cols_hd = ["FeatureNum"])
        
        self.scaling_factor = 1.0
        
    def get_featureNums(self):
        return self.featureNum_to_info.keys_s()
        
    def get_probes(self):
        return self.probe_to_featureNums.keys_s()
    
    def get_genenames(self):
        return self.genename_to_featureNums.keys_s()
    
    def get_genename_from_featureNum(self, featurenum):
        return self.featureNum_to_info.val_accord_hd(featurenum, "GeneName")
        
    def get_gProcessedSignal(self, featurenum):
        return self.featureNum_to_info.val_accord_hd(featurenum, "gProcessedSignal") * \
            self.scaling_factor
    
    def get_gIsPosAndSignif(self, featurenum):
        return self.featureNum_to_info.val_accord_hd(featurenum, "gIsPosAndSignif")
    
    def get_gIsWellAboveBG(self, featurenum):
        return self.featureNum_to_info.val_accord_hd(featurenum, "gIsWellAboveBG")
    
    def get_gIsSaturated(self, featurenum):
        return self.featureNum_to_info.val_accord_hd(featurenum, "gIsSaturated")
    
    def get_gProcessedSignal_from_genename(self, genename):
        ret = {}
        for featurenum in self.genename_to_featureNums.val_accord_hd(genename, "FeatureNum"):
            ret[featurenum] = self.get_gProcessedSignal(featurenum)
        return ret
    
    def get_gProcessedSignal_from_genename_s(self, genename):
        featurenums = self.genename_to_featureNums.val_accord_hd(genename, "FeatureNum")
        if len(featurenums) != 1:
            raise "Multiple probes for " + genename
        return self.get_gProcessedSignal(featurenums[0])
    
    def get_gIsPosAndSignif_from_genename_s(self, genename):
        featurenums = self.genename_to_featureNums.val_accord_hd(genename, "FeatureNum")
        if len(featurenums) != 1:
            raise "Multiple probes for " + genename
        return self.get_gIsPosAndSignif(featurenums[0])
    
    def get_gIsWellAboveBG_from_genename_s(self, genename):
        featurenums = self.genename_to_featureNums.val_accord_hd(genename, "FeatureNum")
        if len(featurenums) != 1:
            raise "Multiple probes for " + genename
        return self.get_gIsWellAboveBG(featurenums[0])
    
    def calc_average(self):
        total = 0
        count = 0
        for featurenum in self.get_featureNums():
            total += self.get_gProcessedSignal(featurenum)
            count += 1
        return 1.0 * total / count
    
    def set_scaling_factor_direct(self, scaling_factor):
        self.scaling_factor = scaling_factor
    
    def set_scaling_factor(self, featext):
        instance_class_check(featext, Feature_Ext)
        self.set_scaling_factor_direct(1.0 * featext.calc_average() / self.calc_average())   

    
if __name__ == "__main__":
    
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsSAT_Config")
    featext = Feature_Ext(rsc.FE_Human11k_Brain_dT)
       
    print featext.get_gProcessedSignal("10")
    print featext.get_gIsSaturated("10")
    print featext.get_gProcessedSignal_from_genename("AFAS-Onc-Anti-M18082-03").values()[0]
    print featext.get_gProcessedSignal_from_genename_s("AFAS-Onc-Anti-M18082-03")
    print featext.get_gIsPosAndSignif_from_genename_s("AFAS-Onc-Anti-M18082-03")
    print featext.get_gIsWellAboveBG_from_genename_s("AFAS-Onc-Anti-M18082-03")
    print featext.calc_average()
    
    """
    for featureNum in featext.get_featureNums():
        print featureNum, featext.get_genename_from_featureNum(featureNum), featext.get_gProcessedSignal(featureNum), \
            featext.get_gIsPosAndSignif(featureNum), featext.get_gIsWellAboveBG(featureNum)
    """
    
    """
    for genename in featext.get_genenames():
        print genename, featext.get_gProcessedSignal_from_genename(genename)
        if len(featext.get_gProcessedSignal_from_genename(genename).keys()) == 1:
            print featext.get_gProcessedSignal_from_genename_s(genename), \
                featext.get_gIsPosAndSignif_from_genename_s(genename), \
                featext.get_gIsWellAboveBG_from_genename_s(genename)
    """
        
    
