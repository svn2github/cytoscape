#!/usr/bin/env python

from Usefuls.Instance_check import instance_class_check
from Usefuls.DictProc1 import list_count_dict, rev_key_val_redund
from Data_Struct.Hash2 import Hash
import Data_Struct.Dict_Ordered
import re

correct_ans_format  = re.compile(r'(\d+)=(\d+)')
student_id_format   = re.compile(r'^\d{8}$')
invalid_ans    = '#'

def judge_valid_ans(ans):
    if (ans == "" or
        ans is None or
        ans.isspace() or
        ans == invalid_ans):
        return False
    else:
        return True

class Ans_Sheet:
    def __init__(self, ans_sheet_file):
        self.ans_sheet_file = ans_sheet_file
        self.stuid_to_answer = {}
        self.stuid_to_snum   = {}
        fh = open(ans_sheet_file, "r")
        for line in fh:
            r = line.rstrip().split(",")
            snum  = r[0]
            stuid = r[1]
            ans   = r[2:]
            self.stuid_to_answer[ stuid ] = ans
            self.stuid_to_snum[ stuid ]   = snum
            
    def get_answers(self, stuid):
        if stuid in self.stuid_to_answer:
            return self.stuid_to_answer[stuid]
        else:
            return None
    
    def get_answers_accord_qnum(self, stulist, qnum):
        ret = []
        for stuid in stulist:
            ret.append(self.get_ans(stuid, qnum))
        return ret
    
    def get_answers_mode(self, stulist, qnum):
        l = []
        for stuid in stulist:
            ans = self.get_ans(stuid, qnum)
            if judge_valid_ans(ans):
                l.append(ans)
               
        if l == []:
            return ""
        else: 
            d  = list_count_dict(l)
            rd = rev_key_val_redund(d)
            count = rd.keys()
            count.sort(reverse = True)
            return rd[ count[0] ]
       
    def get_ans(self, stuid, qnum):
        if type(qnum) is str:
            qnum = int(qnum)
        if stuid in self.stuid_to_answer:
            return self.get_answers(stuid)[qnum - 1]
        else:
            return None

    def match_correct_ans(self, correct, stuid, right = "o", wrong = "x"):
        instance_class_check(correct, Data_Struct.Dict_Ordered.Dict_Ordered)
        match = Data_Struct.Dict_Ordered.Dict_Ordered()
        for ques_num in correct:
            if self.get_ans(stuid, ques_num) == correct[ ques_num ]:
                match[ ques_num ] = right
            else:
                match[ ques_num ] = wrong
        return match
    
    def match_correct_ans_d(self, correct, stuid, sep = ":", right = "o", wrong = "x"):
        instance_class_check(correct, Data_Struct.Dict_Ordered.Dict_Ordered)
        ret = Data_Struct.Dict_Ordered.Dict_Ordered()
        for ques_num in correct:
            ans = self.get_ans(stuid, ques_num)
            if (judge_valid_ans(ans)) is False:
                ret[ques_num] = ""
            elif ans == correct[ ques_num ]:
                ret[ques_num] = ans + sep + right
            else:
                ret[ques_num] = ans + sep + wrong
        return ret
                    
    def count_correct_ans(self, correct, stuid):
        instance_class_check(correct, Data_Struct.Dict_Ordered.Dict_Ordered)
        correct_count = 0
        for ques_num in correct:
            if self.get_ans(stuid, ques_num) == correct[ ques_num ]:
                correct_count += 1
        return correct_count

    def count_students_correct(self, correct, stulist, qnum):
        count = 0
        for stuid in stulist:
            ans = self.get_ans(stuid, qnum)
            if ans == correct[qnum]:
                count += 1
        return count

    def count_valid_ans(self, correct, stuid):
        valid_count = 0
        for ques_num in correct:
            if judge_valid_ans(self.get_ans(stuid, ques_num)):
                valid_count += 1
        return valid_count
                    
    def score_distr(self, correct, stulist):
        distr = []
        for stuid in stulist:
            if self.count_valid_ans(correct, stuid) > 0:
                distr.append(self.count_correct_ans(correct, stuid))
        return distr

def read_correct_answer(correct_ans_file):
    
    correct   = Data_Struct.Dict_Ordered.Dict_Ordered()
    fh = open(correct_ans_file)
    for line in fh:  
        read_res = correct_ans_format.match(line)
        if read_res:
            qnum = read_res.group(1)
            cans = read_res.group(2)
            correct[ qnum ] = cans
    
    return correct

def read_student_list(student_list_file):
    
    stulist = []
    fh = open(student_list_file, "r")
    for line in fh:
        r = line.rstrip().split("\t")
        if student_id_format.match(r[0]):
            stulist.append(r[0]) 
        
    return stulist

def read_extra_info(extra_info_file):
    stuid_to_extra_info = Hash("L")
    stuid_to_extra_info.read_file(extra_info_file)
    return stuid_to_extra_info
    

if __name__ == "__main__":
    from Usefuls.TmpFile import TmpFile_II, TmpFile_III
    ans_sheet = TmpFile_II("""
001,70644300,1,9,6,2,3,8,4,2,3,4,4,#,3,1,5,1,3,1,2,3,2,1,2,3,2,5,2,1,4,3,3,2,5,3,3,5,1,2,1,3,3,2,3,4,2,2,3,4,4,4,#,#,#,#,#,#,#,#,#,#
002,70647600,2,Y,2,9,2,2,2,2,2,2,3,4,1,4,4,2,2,4,4,2,1,1,2,3,3,5,1,3,4,9,3,5,6,1,5,4,2,2,3,4,7,2,2,1,3,2,3,3,1,4,#,#,#,#,#,#,#,#,#,#
003,70642300,1,9,7,9,1,3,2,3,1,2,3,2,1,6,5,5,2,3,1,3,5,1,3,4,3,6,2,1,3,4,3,5,6,1,2,3,3,5,2,4,6,2,4,2,4,4,3,2,4,3,#,#,#,#,#,#,#,#,#,#
004,70641200,#,#,#,#,#,3,3,3,3,3,3,3,6,2,4,4,1,1,5,3,3,3,3,3,3,6,1,2,9,5,7,2,5,3,3,3,3,3,3,3,3,3,3,3,3,4,4,1,2,3,#,#,#,#,#,#,#,#,#,#
005,70643900,4,9,#,5,1,8,5,6,4,4,3,4,2,3,5,2,1,3,2,2,1,1,3,1,3,6,2,1,3,4,3,1,6,3,5,4,4,2,5,3,3,3,2,3,2,2,3,3,4,1,2,#,#,#,#,#,#,#,#,#
006,70681600,6,9,5,9,3,7,2,2,4,1,3,4,2,3,4,1,4,5,2,4,1,1,3,2,3,6,2,1,3,4,3,7,5,4,3,3,5,4,4,4,2,1,3,3,3,1,3,2,5,3,#,#,#,#,#,#,#,#,#,#
007,70680000,4,9,7,9,1,3,2,4,3,1,2,3,4,3,2,4,4,3,2,3,8,1,3,2,3,6,8,1,4,3,3,7,5,3,3,4,1,2,1,4,3,2,3,1,4,1,3,2,5,1,#,#,#,#,#,#,#,#,#,#
008,70740700,4,9,2,9,1,2,2,2,4,2,3,4,1,4,4,2,1,3,2,3,5,1,3,3,2,6,2,1,3,4,3,5,6,2,1,3,4,3,2,4,5,2,3,3,4,1,3,4,4,4,#,#,#,#,#,#,#,#,#,#
009,70680500,2,9,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,6,8,2,7,4,3,5,6,4,2,3,2,4,5,4,3,2,3,1,2,4,3,3,4,3,#,#,#,#,#,#,#,#,#,#
010,70649300,6,9,2,9,1,5,1,2,4,4,3,4,1,4,1,1,1,2,1,3,1,1,3,2,3,6,2,1,3,4,3,5,6,2,5,5,5,3,2,4,1,2,3,3,4,1,3,2,1,4,#,#,#,#,#,#,#,#,#,#
""", trim_f_line_flag = True)
    
    correct_ans = TmpFile_II("""
1=1
2=6
3=2
4=9
5=2
""")
    
    student_list = TmpFile_III("""
This is the list.
70641200 *
70644300 *
79151932 *
70642300 *
""")

    extra_info_list = TmpFile_III("""
70644300 3 1 9/15 10/15
""")

    ans_sheet = Ans_Sheet(ans_sheet.filename())
    correct   = read_correct_answer(correct_ans.filename())
    stulist   = read_student_list(student_list.filename())
    extra     = read_extra_info(extra_info_list.filename())
    

    for stuid in stulist:
        match      = ans_sheet.match_correct_ans(correct, stuid)
        each_extra = ""
        if stuid in extra:
            each_extra = extra[stuid].replace("\t", "...")
        mark  = map(lambda qnum: ans_sheet.match_correct_ans_d(correct, stuid)[qnum],
                    correct.keys())
        valid_ans = ans_sheet.count_valid_ans(correct, stuid)
        print "\t".join([stuid] + mark + 
                        [`ans_sheet.count_correct_ans(correct, stuid)`, `valid_ans`, each_extra]) 
    print
    
    for qnum in correct:
        out = []
        for elem in ans_sheet.get_answers_accord_qnum(stulist, qnum):
            if elem is None:
                out.append("NA")
            else:
                out.append(elem)
        print "\t".join([qnum] + out + [ `ans_sheet.count_students_correct(correct, stulist, qnum)`,
                                        ",".join(ans_sheet.get_answers_mode(stulist, qnum))]) 
    
    print ans_sheet.score_distr(correct, stulist)
    
    """
    print ans_sheet.get_answers("70647600")
    print ans_sheet.get_ans("70647600", "2")
    match = ans_sheet.match_correct_ans(correct, "70647600")
    print ans_sheet.count_correct_ans(correct, "70647600")
    for qnum in match.keys():
        print qnum, match[qnum]
    """