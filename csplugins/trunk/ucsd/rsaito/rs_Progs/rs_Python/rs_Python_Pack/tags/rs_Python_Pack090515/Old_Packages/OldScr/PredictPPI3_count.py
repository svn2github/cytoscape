#!/usr/bin/env python

import sys
import string
sys.path.append("../")

import Usefuls.Column_Sheet_Count as CSC

pred_result_file = "../../PPI_Pred/PredictPPI07s_2"

def func1(data, param):
    if string.atof(data["Reprod BP"]) >= param["rep"]:
        return True
    else:
        return False

def func2(data, param):
    if (string.atof(data["Reprod BP"]) >= param["rep"] and
        data["Literature"] != ""):
        return True
    else:
        return False

def func3(data, param):
    if (string.atof(data["Reprod BP"]) >= param["rep"] and
        string.atof(data["Bait geneid match"]) > 0):
        return True
    else:
        return False

def func4(data, param):
    if (string.atof(data["Reprod BP"]) >= param["rep"] and
        string.atof(data["Bait geneid match"]) > 0 and
        data["Literature"] != ""):
        return True
    else:
        return False

def func5(data, param):
    if (string.atof(data["Reprod PB"]) >= param["rep"]):
        return True
    else:
        return False

def func6(data, param):
    if (string.atof(data["Reprod PB"]) >= param["rep"] and
        data["Literature"] != ""):
        return True
    else:
        return False

def func7(data, param):
    if (string.atof(data["Prey redund level"]) >= param["rep"]):
        return True
    else:
        return False

def func8(data, param):
    if (string.atof(data["Prey redund level"]) >= param["rep"] and
        data["Literature"] != ""):
        return True
    else:
        return False

pred_sheet_calc = CSC.Column_Sheet_Count(pred_result_file)


for irep in range(10):
    pred_sheet_calc.reg_func("Reproducibility BP", func7, rep = irep)
    pred_sheet_calc.reg_func("Reproducibility BP-Known", func8, rep = irep)
    pred_sheet_calc.sheet_calculation()

    print irep,
    print pred_sheet_calc.ret_result("Reproducibility BP"),
    print pred_sheet_calc.ret_result("Reproducibility BP-Known"),
    print 1.0 * pred_sheet_calc.ret_result("Reproducibility BP-Known") / pred_sheet_calc.ret_result("Reproducibility BP")
    pred_sheet_calc.reset()

"""
for irep in range(10):
    pred_sheet_calc.reg_func("Reproducibility BP", func1, rep = irep)
    pred_sheet_calc.reg_func("Reproducibility BP-Known", func2, rep = irep)
    pred_sheet_calc.sheet_calculation()

    print pred_sheet_calc.ret_result("Reproducibility BP"),
    print pred_sheet_calc.ret_result("Reproducibility BP-Known"),
    print 1.0 * pred_sheet_calc.ret_result("Reproducibility BP-Known") / pred_sheet_calc.ret_result("Reproducibility BP")
    pred_sheet_calc.reset()
"""
