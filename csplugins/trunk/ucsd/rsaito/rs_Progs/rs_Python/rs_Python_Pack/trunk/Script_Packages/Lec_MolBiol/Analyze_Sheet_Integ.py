#!/usr/bin/env python

from Analyze_Sheet import Analyze_Sheet
from Usefuls.rsConfig import RSC_II
from Usefuls.Table_maker import Table_row
from Calc_Packages.Stats.StatsI import SamplesI

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

def tostr_TF1(TorF):
    if TorF:
        return ""
    else:
        return "Invalid"

def max_scores(stuid, asheet1, asheet2):
    
    valid_1A = (asheet1.get_count_validA(stuid) > 0
                and asheet1.get_validity_from_extra_infoA(stuid))
    valid_1B = (asheet1.get_count_validB(stuid) > 0
                and asheet1.get_validity_from_extra_infoB(stuid))
    valid_2A = (asheet2.get_count_validA(stuid) > 0
                and asheet2.get_validity_from_extra_infoA(stuid))
    valid_2B = (asheet2.get_count_validB(stuid) > 0
                and asheet2.get_validity_from_extra_infoB(stuid))
    
    if valid_1A and valid_2A:
        maxA = max(asheet1.get_dscoreA_mod(stuid), asheet2.get_dscoreA_mod(stuid))
    elif valid_1A and not valid_2A:
        maxA = asheet1.get_dscoreA_mod(stuid)
    elif not valid_1A and valid_2A:
        maxA = asheet2.get_dscoreA_mod(stuid)
    else:
        maxA = None
    
    if valid_1B and valid_2B:
        maxB = max(asheet1.get_dscoreB_mod(stuid), asheet2.get_dscoreB_mod(stuid))
    elif valid_1B and not valid_2B:
        maxB = asheet1.get_dscoreB_mod(stuid)
    elif not valid_1B and valid_2B:
        maxB = asheet2.get_dscoreB_mod(stuid)
    else:
        maxB = None
    
    return maxA, maxB


rsc1 = RSC_II("rsLec_MCB_Config1")
rsc2 = RSC_II("rsLec_MCB_Config2")

students = rsc1.students

if 'extra' in vars(rsc1):
    extra1 = rsc1.extra
else:
    extra1 = None

if 'extra' in vars(rsc2):
    extra2 = rsc2.extra
else:
    extra2 = None

asheet1 = Analyze_Sheet(rsc1.sheet_t,
                        rsc1.correctA,
                        rsc1.correctB,
                        students,
                        extra1)

asheet2 = Analyze_Sheet(rsc2.sheet_t,
                        rsc2.correctA,
                        rsc2.correctB,
                        students,
                        extra2)

students = asheet1.get_students()

tb = Table_row()

total_scores = []

for stuid in students:
    maxA, maxB = max_scores(stuid, asheet1, asheet2)
    if maxA is not None and maxB is not None:
        total_scores.append(maxA + maxB)
        
total_score_distr = SamplesI(total_scores)

for stuid in students:
    tb.append("Student ID", stuid)
    tb.append("Department", students.val_accord_hd(stuid, "Department"))
    tb.append("Grade", students.val_accord_hd(stuid, "Grade"))
    tb.append("Family Name", students.val_accord_hd(stuid, "Family Name"))
    tb.append("Given Name", students.val_accord_hd(stuid, "Given Name"))
    tb.append("Account", students.val_accord_hd(stuid, "Account"))
    tb.append("E-Mail", students.val_accord_hd(stuid, "E-Mail"))
    
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
    tb.append("Validity 1A", tostr_TF1(asheet1.get_validity_from_extra_infoA(stuid)))
    tb.append("Validity 1B", tostr_TF1(asheet1.get_validity_from_extra_infoB(stuid)))    
    tb.append("Validity 2A", tostr_TF1(asheet2.get_validity_from_extra_infoA(stuid)))
    tb.append("Validity 2B", tostr_TF1(asheet2.get_validity_from_extra_infoB(stuid)))    
    tb.append("Presentation Score A",   tostr_above0(stuid, asheet1.get_extra_scoreA)) # Corresponding rows in sheet 1 and 2 are usually identical, because presentation in 1st half also affect 2A-related score.
    tb.append("Presentation Score B",   tostr_above0(stuid, asheet2.get_extra_scoreB)) # Corresponding rows in sheet 1 and 2 are usually identical, because presentation in 2nd half also affect 1B-related score. 
    tb.append("Presentation Days A", asheet1.get_extra_infoA(stuid))
    tb.append("Presentation Days B", asheet2.get_extra_infoB(stuid))
    tb.append("DS 1A Mod", tostr_valid(stuid, asheet1.get_dscoreA_mod, asheet1.get_count_validA))
    tb.append("DS 1B Mod", tostr_valid(stuid, asheet1.get_dscoreB_mod, asheet1.get_count_validB))
    tb.append("DS 2A Mod", tostr_valid(stuid, asheet2.get_dscoreA_mod, asheet2.get_count_validA))
    tb.append("DS 2B Mod", tostr_valid(stuid, asheet2.get_dscoreB_mod, asheet2.get_count_validB))  
    
    maxA, maxB = max_scores(stuid, asheet1, asheet2)
    if maxA is not None and maxB is not None:
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