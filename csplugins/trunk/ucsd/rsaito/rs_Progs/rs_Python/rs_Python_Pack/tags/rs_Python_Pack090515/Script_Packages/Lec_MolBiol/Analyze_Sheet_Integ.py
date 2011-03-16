#!/usr/bin/env python

from Analyze_Sheet import Analyze_Sheet
from Usefuls.rsConfig import RSC_II
from Usefuls.Table_maker import Table_row
from Calc_Packages.Math.StatsI import SamplesI

def tostr_valid(stuid, calcfunc, validfunc):
    if validfunc(stuid) > 0:
        calc = calcfunc(stuid)
        if type(calc) is int:
            return `calcfunc(stuid)`
        else:
            return "%.2f" % calc
    else:
        return ""

def tostr_rank_valid(stuid, calcfunc, validfunc):
    if validfunc(stuid) > 0:
        rank, of = calcfunc(stuid)
        return "%d/%d" % (rank, of)
    else:
        return ""

def tostr_above0(stuid, calcfunc):
    calc = calcfunc(stuid)
    if calc > 0:
        return `calc`
    else:
        return ""

rsc1 = RSC_II("rsLec_MCB_Config1")
rsc2 = RSC_II("rsLec_MCB_Config2")

students = rsc1.students

if 'extra' in vars(rsc1):
    extra = rsc1.extra
else:
    extra = None

asheet1 = Analyze_Sheet(rsc1.sheet_t,
                        rsc1.correctA,
                        rsc1.correctB,
                        students,
                        extra)

asheet2 = Analyze_Sheet(rsc2.sheet_t,
                        rsc2.correctA,
                        rsc2.correctB,
                        students,
                        extra)

students = asheet1.get_students()

tb = Table_row()

total_scores = []

for stuid in students:
    if((asheet1.get_count_validA(stuid) > 0 or asheet2.get_count_validA(stuid) > 0) and
       (asheet1.get_count_validB(stuid) > 0 or asheet2.get_count_validB(stuid) > 0)):
        maxA = max(asheet1.get_dscoreA_mod(stuid), asheet2.get_dscoreA_mod(stuid))
        maxB = max(asheet1.get_dscoreB_mod(stuid), asheet2.get_dscoreB_mod(stuid))
        total_scores.append(maxA + maxB)
total_score_distr = SamplesI(total_scores)

for stuid in students:
    tb.append("Student ID", stuid)
    tb.append("Score 1A", tostr_valid(stuid, asheet1.count_correctA, asheet1.get_count_validA))
    tb.append("Score 1B", tostr_valid(stuid, asheet1.count_correctB, asheet1.get_count_validB))
    tb.append("Score 2A", tostr_valid(stuid, asheet2.count_correctA, asheet2.get_count_validA))
    tb.append("Score 2B", tostr_valid(stuid, asheet2.count_correctB, asheet2.get_count_validB))
    tb.append("Score 1A Rank",  tostr_rank_valid(stuid, asheet1.get_rankA, asheet1.get_count_validA))
    tb.append("Score 1B Rank",  tostr_rank_valid(stuid, asheet1.get_rankB, asheet1.get_count_validB))
    tb.append("Score 2A Rank",  tostr_rank_valid(stuid, asheet2.get_rankA, asheet2.get_count_validA))
    tb.append("Score 2B Rank",  tostr_rank_valid(stuid, asheet2.get_rankB, asheet2.get_count_validB))    
      
    tb.append("DS 1A", tostr_valid(stuid, asheet1.get_dscoreA, asheet1.get_count_validA))
    tb.append("DS 1B", tostr_valid(stuid, asheet1.get_dscoreB, asheet1.get_count_validB))
    tb.append("DS 2A", tostr_valid(stuid, asheet2.get_dscoreA, asheet2.get_count_validA))
    tb.append("DS 2B", tostr_valid(stuid, asheet2.get_dscoreB, asheet2.get_count_validB))  
    tb.append("Presentation Score A",   tostr_above0(stuid, asheet1.get_extra_scoreA))
    tb.append("Presentation Score B",   tostr_above0(stuid, asheet1.get_extra_scoreB))
    tb.append("Presentation Days A", asheet1.get_extra_infoA(stuid))
    tb.append("Presentation Days B", asheet1.get_extra_infoB(stuid))    
    tb.append("DS 1A Mod", tostr_valid(stuid, asheet1.get_dscoreA_mod, asheet1.get_count_validA))
    tb.append("DS 1B Mod", tostr_valid(stuid, asheet1.get_dscoreB_mod, asheet1.get_count_validB))
    tb.append("DS 2A Mod", tostr_valid(stuid, asheet2.get_dscoreA_mod, asheet2.get_count_validA))
    tb.append("DS 2B Mod", tostr_valid(stuid, asheet2.get_dscoreB_mod, asheet2.get_count_validB))  
    

    if((asheet1.get_count_validA(stuid) > 0 or asheet2.get_count_validA(stuid) > 0) and
       (asheet1.get_count_validB(stuid) > 0 or asheet2.get_count_validB(stuid) > 0)):
        maxA = max(asheet1.get_dscoreA_mod(stuid), asheet2.get_dscoreA_mod(stuid))
        maxB = max(asheet1.get_dscoreB_mod(stuid), asheet2.get_dscoreB_mod(stuid))
        maxA_str = `maxA`
        maxB_str = `maxB`
        total_score  = maxA + maxB
        total_score_str = `total_score`
        total_DS_str = "%.2f" % total_score_distr.calc_zscore(total_score, 10.0, 50.0)
    else:
        maxA_str = ""
        maxB_str = ""
        total_score_str = ""
        total_DS_str = ""
    tb.append("DS Total", total_DS_str)
        
    tb.output("\t")