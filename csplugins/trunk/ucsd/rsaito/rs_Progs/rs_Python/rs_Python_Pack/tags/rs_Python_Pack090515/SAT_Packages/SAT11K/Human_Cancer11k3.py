#!/usr/bin/env python

import Data_Struct.Hash2 as Hash
import Data_Struct.Data_Sheet2
import SAT_Packages.SAT11K.OptParse_Cancer11k3 as OptParse_Cancer11k
from Usefuls.rsConfig import RSC_II
from Usefuls.Instance_check import instance_class_check
from Usefuls.Adjust_to_thres import adjust_to_lower_thres_list
from Calc_Packages.Math.Math_SAT1 import select_abs_min_sgn
from Calc_Packages.Math.Vector1 import vector_diff

rsc = RSC_II("rsSAT_Config")

def adjust_by_flags(exppat, flags, min_thres):
    if len(exppat) != len(flags):
        raise "Expression pattern size error..."

    for i in range(len(flags)):
        if flags[i] == 0:
            exppat[i] = min_thres


class Okay_Sheet(Data_Struct.Data_Sheet2.Data_Sheet):

    def __init__(self, filename, sep = "\t"):

        self.conv_onc_afas = Hash.Hash("A")
        self.conv_onc_afas.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                                        Key_cols_hd = [ "Gene ID (ONC)" ],
                                        Val_cols_hd = [ "Gene ID (AFAS)" ])
        self.conv_afas_onc = Hash.Hash("S")
        self.conv_afas_onc.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                                        Key_cols_hd = [ "Gene ID (AFAS)" ],
                                        Val_cols_hd = [ "Gene ID (ONC)" ])

        Data_Struct.Data_Sheet2.Data_Sheet.__init__(
            self, filename, sep = "\t")
        self.numerize()

    def extract_col_labels(self, lines, sep):
        first_line = lines.pop(0)
        """ This will extract and eliminate first line """
        col_lb_immature = first_line.split(sep)
        return col_lb_immature[4:]

    def extract_row_label_data(self, line_a):
        label = line_a[1]
        data = line_a[4:]
        return (label, data)

    def get_four_exp_pat(self, afs_id,
                         col_keys_normal,
                         col_keys_cancer):

        onc_id = self.conv_afas_onc.val_force(afs_id)
        if not onc_id:
            return None

        exp_pat_sense_normal = self.get_data_accord_keys(
            onc_id, col_keys_normal)
        exp_pat_sense_cancer = self.get_data_accord_keys(
            onc_id, col_keys_cancer)
        exp_pat_antis_normal = self.get_data_accord_keys(
            afs_id, col_keys_normal)
        exp_pat_antis_cancer = self.get_data_accord_keys(
            afs_id, col_keys_cancer)

        return (exp_pat_sense_normal,
                exp_pat_sense_cancer,
                exp_pat_antis_normal,
                exp_pat_antis_cancer)

    def get_reverse_level(self, onc_id,
                          col_keys_normal,
                          col_keys_cancer):

        afs_ids = self.conv_onc_afas.val_force(onc_id)
        if not afs_ids:
            return None

        ret = []
        for afs_id in afs_ids:

            (exp_pat_sense_normal,
             exp_pat_sense_cancer,
             exp_pat_antis_normal,
             exp_pat_antis_cancer) = \
             self.get_four_exp_pat(afs_id,
                                   col_keys_normal,
                                   col_keys_cancer)

            exp_pat_sense_diff = vector_diff(exp_pat_sense_cancer,
                                             exp_pat_sense_normal)
            exp_pat_antis_diff = vector_diff(exp_pat_antis_cancer,
                                             exp_pat_antis_normal)

            """
            print onc_id, afs_id
            print "Sense Normal", exp_pat_sense_normal
            print "Sense Cancer", exp_pat_sense_cancer
            print "Sense Differ", exp_pat_sense_diff
            print "Antis Normal", exp_pat_antis_normal
            print "Antis Cancer", exp_pat_antis_cancer
            print "Antis Differ", exp_pat_antis_diff
            print "Reverse Scor", select_abs_min_sgn(exp_pat_sense_diff,
                                                     exp_pat_antis_diff)
            print
            """

            ret.append(select_abs_min_sgn(exp_pat_sense_diff,
                                          exp_pat_antis_diff))
        return ret

    def get_reverse_level_afs(self, afs_id,
                              col_keys_normal,
                              col_keys_cancer):

        onc_id = self.conv_afas_onc.val_force(afs_id)
        if not onc_id:
            return None

        (exp_pat_sense_normal,
         exp_pat_sense_cancer,
         exp_pat_antis_normal,
         exp_pat_antis_cancer) = self.get_four_exp_pat(afs_id,
                                                       col_keys_normal,
                                                       col_keys_cancer)

        exp_pat_sense_diff = vector_diff(exp_pat_sense_cancer,
                                         exp_pat_sense_normal)
        exp_pat_antis_diff = vector_diff(exp_pat_antis_cancer,
                                         exp_pat_antis_normal)


        """
        print onc_id, afs_id
        print "Sense Normal", exp_pat_sense_normal
        print "Sense Cancer", exp_pat_sense_cancer
        print "Sense Differ", exp_pat_sense_diff
        print "Antis Normal", exp_pat_antis_normal
        print "Antis Cancer", exp_pat_antis_cancer
        print "Antis Differ", exp_pat_antis_diff
        print "Reverse Scor", select_abs_min_sgn(exp_pat_sense_diff,
                                                 exp_pat_antis_diff)
                                                 """

        return select_abs_min_sgn(exp_pat_sense_diff,
                                  exp_pat_antis_diff)


class Human_Cancer11k:
    def __init__(self, opt_cancer = None):
        
        self.min_thres = 0.0
        self.filtering_level = 0

        if opt_cancer:
            instance_class_check(opt_cancer, OptParse_Cancer11k.Option_Cancer11kI)    
            self.set_related_files(opt_cancer.get_exp_file(),
                                   opt_cancer.get_pas_file(),
                                   opt_cancer.get_wab_file())
            self.min_thres       = opt_cancer.get_min_thres()
            self.filtering_level = opt_cancer.get_filtering_level()
            self.normal_keys = opt_cancer.get_normal_keys()
            self.cancer_keys = opt_cancer.get_cancer_keys()
    
    def set_related_files(self, okay_exp_file, okay_gIsPosAndSignif_file, okay_gIsWellAboveBG_file):
        self.okay_exp = Okay_Sheet(okay_exp_file)
        self.okay_pas = Okay_Sheet(okay_gIsPosAndSignif_file)
        self.okay_wab = Okay_Sheet(okay_gIsWellAboveBG_file)
        
    def set_min_thres(self, min_thres):
        self.min_thres = min_thres
        
    def set_filtering_level(self, filtering_level):
        self.filtering_level = filtering_level
        
    def set_normal_keys(self, normal_keys):
        self.normal_keys = normal_keys
        
    def set_cancer_keys(self, cancer_keys):
        self.cancer_keys = cancer_keys
        
    def get_exp_sheet(self):
        return self.okay_exp
    
    def get_pas_sheet(self):
        return self.okay_pas
    
    def get_wab_sheet(self):
        return self.okay_wab
        
    def get_four_exp_pat(self, afs_id):
        
        (exp_pat_sense_normal,
         exp_pat_sense_cancer,
         exp_pat_antis_normal,
         exp_pat_antis_cancer) = \
            self.okay_exp.get_four_exp_pat(afs_id,
                                           self.normal_keys,
                                           self.cancer_keys)

        """
        tmpout = map(lambda x: "%3.1f" % x, exp_pat_antis_cancer)
        print "No adjustment:", tmpout
        """

        if self.filtering_level == 0:
            return (exp_pat_sense_normal,
                    exp_pat_sense_cancer,
                    exp_pat_antis_normal,
                    exp_pat_antis_cancer)
            
        exp_pat_sense_normal_adjusted = \
            adjust_to_lower_thres_list(exp_pat_sense_normal, self.min_thres)
        exp_pat_sense_cancer_adjusted = \
            adjust_to_lower_thres_list(exp_pat_sense_cancer, self.min_thres)
        exp_pat_antis_normal_adjusted = \
            adjust_to_lower_thres_list(exp_pat_antis_normal, self.min_thres)
        exp_pat_antis_cancer_adjusted = \
            adjust_to_lower_thres_list(exp_pat_antis_cancer, self.min_thres)
        

        """
        tmpout = map(lambda x: "%3.1f" % x, exp_pat_antis_cancer_adjusted)
        print "Adjustment #1:", tmpout
        """

        if self.filtering_level == 1:
            return (exp_pat_sense_normal_adjusted,
                    exp_pat_sense_cancer_adjusted,
                    exp_pat_antis_normal_adjusted,
                    exp_pat_antis_cancer_adjusted)
        
        (pas_sense_normal,
         pas_sense_cancer,
         pas_antis_normal,
         pas_antis_cancer) = \
            self.okay_pas.get_four_exp_pat(afs_id,
                                           self.normal_keys,
                                           self.cancer_keys)            

        adjust_by_flags(exp_pat_sense_normal_adjusted,
                        pas_sense_normal, self.min_thres)
        adjust_by_flags(exp_pat_sense_cancer_adjusted,
                        pas_sense_cancer, self.min_thres)
        adjust_by_flags(exp_pat_antis_normal_adjusted,
                        pas_antis_normal, self.min_thres)
        adjust_by_flags(exp_pat_antis_cancer_adjusted,
                        pas_antis_cancer, self.min_thres)

        """
        tmpout = map(lambda x: "%3.1f" % x, exp_pat_antis_cancer_adjusted)
        print "Adjustment #2:", tmpout
        print "by           ;", pas_antis_cancer
        """

        if self.filtering_level == 2:
            return (exp_pat_sense_normal_adjusted,
                    exp_pat_sense_cancer_adjusted,
                    exp_pat_antis_normal_adjusted,
                    exp_pat_antis_cancer_adjusted)        
        
        (wab_sense_normal,
         wab_sense_cancer,
         wab_antis_normal,
         wab_antis_cancer) = \
            self.okay_wab.get_four_exp_pat(afs_id,
                                           self.normal_keys,
                                           self.cancer_keys)   
        
        adjust_by_flags(exp_pat_sense_normal_adjusted,
                        wab_sense_normal, self.min_thres)
        adjust_by_flags(exp_pat_sense_cancer_adjusted,
                        wab_sense_cancer, self.min_thres)
        adjust_by_flags(exp_pat_antis_normal_adjusted,
                        wab_antis_normal, self.min_thres)
        adjust_by_flags(exp_pat_antis_cancer_adjusted,
                        wab_antis_cancer, self.min_thres)

        """
        tmpout = map(lambda x: "%3.1f" % x, exp_pat_antis_cancer_adjusted)
        print "Adjustment #3:", tmpout
        print "by           ;", wab_antis_cancer
        """

        if self.filtering_level == 3:
            return (exp_pat_sense_normal_adjusted,
                    exp_pat_sense_cancer_adjusted,
                    exp_pat_antis_normal_adjusted,
                    exp_pat_antis_cancer_adjusted)      


def create_Human_Cancer11k_dt_rd(opt):
    instance_class_check(opt, OptParse_Cancer11k.Option_Cancer11kII) 
    
    cancer11k_dt = Human_Cancer11k()
    cancer11k_dt.set_related_files(opt.get_exp_file_dt(),
                                   opt.get_pas_file_dt(),
                                   opt.get_wab_file_dt())
    cancer11k_dt.set_min_thres(opt.get_min_thres())
    cancer11k_dt.set_filtering_level(opt.get_filtering_level())
    cancer11k_dt.set_normal_keys(opt.get_normal_keys())
    cancer11k_dt.set_cancer_keys(opt.get_cancer_keys())
    
    cancer11k_rd = Human_Cancer11k()
    cancer11k_rd.set_related_files(opt.get_exp_file_rd(),
                                   opt.get_pas_file_rd(),
                                   opt.get_wab_file_rd())
    cancer11k_rd.set_min_thres(opt.get_min_thres())
    cancer11k_rd.set_filtering_level(opt.get_filtering_level())
    cancer11k_rd.set_normal_keys(opt.get_normal_keys())
    cancer11k_rd.set_cancer_keys(opt.get_cancer_keys())
    
    return cancer11k_dt, cancer11k_rd


if __name__ == "__main__":

    Colon_dT = Okay_Sheet(rsc.Human11k_Cancer_Colon_dT)
    print Colon_dT.col_labels()

    print Colon_dT.get_reverse_level("ONC-AF001954", # "ONC-U68485", # "ONC-D63780",
                                     [ "Colon_N1",
                                       "Colon_N12",
                                       "Colon_N13",
                                       "Colon_N15",
                                       "Colon_N2",
                                       "Colon_N7",
                                       "Colon_N8"],
                                     [ "Colon_C1",
                                       "Colon_C12",
                                       "Colon_C13",
                                       "Colon_C15",
                                       "Colon_C2",
                                       "Colon_C7",
                                       "Colon_C8" ]
                                     )

    print Colon_dT.get_reverse_level_afs("AFAS-Onc-Anti-AF001954-04",
                                         [ "Colon_N1",
                                           "Colon_N12",
                                           "Colon_N13",
                                           "Colon_N15",
                                           "Colon_N2",
                                           "Colon_N7",
                                           "Colon_N8"],
                                         [ "Colon_C1",
                                           "Colon_C12",
                                           "Colon_C13",
                                           "Colon_C15",
                                           "Colon_C2",
                                           "Colon_C7",
                                           "Colon_C8" ]
                                         )


    Colon_dT = Human_Cancer11k()
    Colon_dT.set_related_files(rsc.Human11k_Cancer_Colon_dT,
                               rsc.Human11k_Cancer_Colon_gIsPosAndSignif_dT,
                               rsc.Human11k_Cancer_Colon_gIsWellAboveBG_dT)
    Colon_dT.set_normal_keys([ "Colon_N1",
                              "Colon_N12",
                              "Colon_N13",
                              "Colon_N15",
                              "Colon_N2",
                              "Colon_N7",
                              "Colon_N8"])
    Colon_dT.set_cancer_keys([ "Colon_C1",
                              "Colon_C12",
                              "Colon_C13",
                              "Colon_C15",
                              "Colon_C2",
                              "Colon_C7",
                              "Colon_C8" ])
    
    print Colon_dT.get_four_exp_pat("AFAS-Onc-Anti-AF001954-04")
    print

    """
    opt = OptParse_Cancer11k.Option_Cancer11kI()
    human_cancer11k = Human_Cancer11k(opt)
    four_exp_pat = human_cancer11k.get_four_exp_pat("AFAS-Onc-Anti-AF001954-04")
    for exp_pat in four_exp_pat:
        print exp_pat
    
    """
    opt = OptParse_Cancer11k.Option_Cancer11kII()
    human_cancer11k_dt, human_cancer11k_rd = create_Human_Cancer11k_dt_rd(opt)
    four_exp_pat = human_cancer11k_rd.get_four_exp_pat("AFAS-Onc-Anti-AF001954-04")
    for exp_pat in four_exp_pat:
        print exp_pat

