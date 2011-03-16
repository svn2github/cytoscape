#!/usr/bin/env python

from Expr_Packages.Agilent.Feature_Extraction1 import Feature_Ext
from Expr_Packages.Agilent.Feature_Ext_Set1 import Feature_Ext_Set

from Calc_Packages.ListCalc.ListCalcI import big_diff
from Calc_Packages.Stats.StatsI import mean, sd

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

import Usefuls.Table_maker

arrays = [ 
          "CMB4_D0",
          "CMB4_D4",
          "CMB4_D8",
          "CMB4_N5",
          "CMB4_N9",
          "CMB4_N9_K"
          ]

kf = {}
for array in arrays:
    kf[ array ] = rsc.__dict__[ array ]

# print kf

info_keys = ["ProbeName",
             # "Sequence",
             "accessions",
             "chr_coord",
             "GeneName",
             "SystematicName",
             "gProcessedSignal",
             "gIsPosAndSignif",
             "gIsWellAboveBG",
             "gIsSaturated"]

fe_D0 = Feature_Ext(kf["CMB4_D0"])
feset = Feature_Ext_Set()
feset.set_feature_exts(kf, fe_D0, info_keys)

# print feset.get_data_names()
# print feset.get_feature_ext("CMB4_D0").get_genenames()

featureNums = feset.get_feature_ext("CMB4_D0").get_featureNums()
featureNums.sort(lambda x,y:int(x)-int(y))

output = Usefuls.Table_maker.Table_row()

for featureNum in featureNums:
    output.append("featureNum", featureNum)
    
    exppat = feset.get_exp_pat_feanum(arrays, featureNum)
    inc_rate, inc_diff, dec_rate, dec_diff = big_diff(exppat)
    
    for array in arrays:
        output.append("SystematicName", feset.get_feature_ext(array).featureNum_to_info.val_accord_hd(featureNum, "SystematicName"))
        # output.append("accessions", feset.get_feature_ext(array).featureNum_to_info.val_accord_hd(featureNum, "accessions"))
        output.append(array, "%6f" % (feset.get_feature_ext(array).get_gProcessedSignal(featureNum), ))

    output.append("Mean", "%3f" % (mean(exppat), ))
    output.append("Std. dev", "%3f" % (sd(exppat), ))

    output.append("Max inc rate", "%3f" % (inc_rate,))
    output.append("Inc diff", "%3f" % (inc_diff,))
    output.append("Max dec rate", "%3f" % (dec_rate,))
    output.append("Dec diff", "%3f" % (dec_diff,))

    #for array in arrays:
    #    output.append(array + "-Strtd", `feset.get_feature_ext(array).get_gIsSaturated(featureNum)`)

    output.output("\t")

