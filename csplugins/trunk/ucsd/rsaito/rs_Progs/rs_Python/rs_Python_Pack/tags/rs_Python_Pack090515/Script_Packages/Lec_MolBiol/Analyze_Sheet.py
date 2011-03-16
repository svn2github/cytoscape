#!/usr/bin/env python

from Ans_Sheet import Ans_Sheet, read_correct_answer, read_student_list, read_extra_info
from Usefuls.Table_maker import Table_row
from Calc_Packages.Math.StatsI import SamplesI

class Analyze_Sheet:
    def __init__(self,
                 ans_sheet_file,
                 correct_ansA_file,
                 correct_ansB_file,
                 student_list_file,
                 extra_info_list_file = None
                 ):
        
        self.ans_sheet = Ans_Sheet(ans_sheet_file)
        self.correctA  = read_correct_answer(correct_ansA_file)
        self.correctB  = read_correct_answer(correct_ansB_file)
        self.stulist   = read_student_list(student_list_file)
        if extra_info_list_file is None:
            self.extra = None
        else:
            self.extra = read_extra_info(extra_info_list_file)
        self.score_distrA = SamplesI(self.ans_sheet.score_distr(self.correctA, self.stulist))
        self.score_distrB = SamplesI(self.ans_sheet.score_distr(self.correctB, self.stulist))    

    def get_students(self):
        return self.stulist

    def get_extra_scoreA(self, stuid):
        if (self.extra and stuid in self.extra and
            len(self.extra[stuid].split("\t")) >= 1 and
            self.extra[stuid].split("\t")[0] != "" and
            (not self.extra[stuid].split("\t")[0].isspace())):
            return int(self.extra[stuid].split("\t")[0])
        else:
            return 0

    def get_extra_scoreB(self, stuid):
        if (self.extra and stuid in self.extra and
            len(self.extra[stuid].split("\t")) >= 2 and
            self.extra[stuid].split("\t")[1] != "" and
            (not self.extra[stuid].split("\t")[1].isspace())):
            return int(self.extra[stuid].split("\t")[1])
        else:
            return 0
    
    def get_extra_infoA(self, stuid):
        if (self.extra and stuid in self.extra and
            len(self.extra[stuid].split("\t")) >= 3 and
            self.extra[stuid].split("\t")[2] != "" and
            (not self.extra[stuid].split("\t")[2].isspace())):
            return self.extra[stuid].split("\t")[2]
        else:
            return ""

    def get_extra_infoB(self, stuid):
        if (self.extra and stuid in self.extra and
            len(self.extra[stuid].split("\t")) >= 4 and
            self.extra[stuid].split("\t")[3] != "" and
            (not self.extra[stuid].split("\t")[3].isspace())):
            return self.extra[stuid].split("\t")[3]
        else:
            return ""
    
    def get_score_distrA(self):
        return self.score_distrA
    
    def get_score_distrB(self):
        return self.score_distrB

    def get_qnumsA(self):
        return self.correctA.keys()

    def get_qnumsB(self):
        return self.correctB.keys()    

    def get_correctA(self, qnum):
        return self.correctA[qnum]

    def get_correctB(self, qnum):
        return self.correctB[qnum]

    def get_numqA(self):
        return len(self.correctA)

    def get_numqB(self):
        return len(self.correctB)
    
    def get_count_validA(self, stuid):
        return self.ans_sheet.count_valid_ans(self.correctA, stuid)
    
    def get_count_validB(self, stuid):
        return self.ans_sheet.count_valid_ans(self.correctB, stuid)  
    
    def count_correctA(self, stuid):
        return self.ans_sheet.count_correct_ans(self.correctA, stuid)
    
    def count_correctB(self, stuid):
        return self.ans_sheet.count_correct_ans(self.correctB, stuid)

    def match_correct_ansA(self, stuid, qnum):
        return self.ans_sheet.match_correct_ans_d(self.correctA, stuid)[qnum]

    def match_correct_ansB(self, stuid, qnum):
        return self.ans_sheet.match_correct_ans_d(self.correctB, stuid)[qnum]

    def count_correct_studentsA(self, qnum):
        return self.ans_sheet.count_students_correct(self.correctA, self.stulist, qnum)

    def count_correct_studentsB(self, qnum):
        return self.ans_sheet.count_students_correct(self.correctB, self.stulist, qnum)

    def get_answers_mode(self, qnum):
        return self.ans_sheet.get_answers_mode(self.stulist, qnum)

    def get_scoreA_mod(self, stuid):
        scoreA_mod = self.count_correctA(stuid) + self.get_extra_scoreA(stuid)
        if scoreA_mod > self.get_numqA():
            scoreA_mod = self.get_numqA()
        return scoreA_mod
    
    def get_scoreB_mod(self, stuid):
        scoreB_mod = self.count_correctB(stuid) + self.get_extra_scoreB(stuid)
        if scoreB_mod > self.get_numqB():
            scoreB_mod = self.get_numqB()
        return scoreB_mod
    
    def get_dscoreA(self, stuid):
        return self.get_score_distrA().calc_zscore(self.count_correctA(stuid),
                                                   10.0, 50.0)
        
    def get_dscoreB(self, stuid):
        return self.get_score_distrB().calc_zscore(self.count_correctB(stuid),
                                                   10.0, 50.0)

    def get_dscoreA_mod(self, stuid):
        return self.get_score_distrA().calc_zscore(self.get_scoreA_mod(stuid),
                                                   10.0, 50.0)

    def get_dscoreB_mod(self, stuid):
        return self.get_score_distrB().calc_zscore(self.get_scoreB_mod(stuid),
                                                   10.0, 50.0)

    def get_rankA(self, stuid): 
        return (self.get_score_distrA().get_rank(self.count_correctA(stuid), reverse = True) + 1,
                self.get_score_distrA().get_n())

    def get_rankB(self, stuid): 
        return (self.get_score_distrB().get_rank(self.count_correctB(stuid), reverse = True) + 1,
                self.get_score_distrB().get_n())        


    def output(self):
        score_distrA = self.get_score_distrA()
        score_distrB = self.get_score_distrB()
        qnums_A = self.get_numqA()
        qnums_B = self.get_numqB()
        
        tb = Table_row()
        for stuid in self.get_students():
            extra_scoreA   = self.get_extra_scoreA(stuid)
            extra_scoreB   = self.get_extra_scoreB(stuid)
            extra_infoA    = self.get_extra_infoA(stuid)
            extra_infoB    = self.get_extra_infoB(stuid)
            count_validA   = self.get_count_validA(stuid)
            count_validB   = self.get_count_validB(stuid)
            count_correctA = self.count_correctA(stuid)
            count_correctB = self.count_correctB(stuid)
            scoreA_mod     = self.get_scoreA_mod(stuid)
            scoreB_mod     = self.get_scoreB_mod(stuid)           

            if count_validA > 0:
                dscoreA     = self.get_dscoreA(stuid)
                dscoreA_mod = self.get_dscoreA_mod(stuid)
                count_correctA_str = `count_correctA`
                dscoreA_str     = "%.2f" % dscoreA
                dscoreA_mod_str = "%.2f" % dscoreA_mod
                rankA_str = "%d/%d" % self.get_rankA(stuid)
            else:
                count_correctA_str = ""
                dscoreA_str = ""
                rankA_str = ""
                dscoreA_mod_str = ""
                
            if count_validB > 0:
                dscoreB     = self.get_dscoreB(stuid)
                dscoreB_mod = self.get_dscoreB_mod(stuid)
                count_correctB_str = `count_correctB`
                dscoreB_str     = "%.2f" % dscoreB
                dscoreB_mod_str = "%.2f" % dscoreB_mod
                rankB_str = "%d/%d" % self.get_rankB(stuid)
            else:
                count_correctB_str = ""
                dscoreB_str = ""
                rankB_str = ""
                dscoreB_mod_str = ""

            if extra_scoreA > 0:
                extra_scoreA_str = `extra_scoreA`
            else:
                extra_scoreA_str = ""
                
            if extra_scoreB > 0:
                extra_scoreB_str = `extra_scoreB`
            else:
                extra_scoreB_str = ""
                
            tb.append("Student ID", stuid)
            for qnum in self.correctA.keys():
                mark = self.match_correct_ansA(stuid, qnum)
                tb.append(qnum, mark)
            for qnum in self.correctB.keys():
                mark = self.match_correct_ansB(stuid, qnum)
                tb.append(qnum, mark)            

            tb.append("Score A", count_correctA_str)
            tb.append("Score B", count_correctB_str)
            
            tb.append("Score A Rank", rankA_str)
            tb.append("Score B Rank", rankB_str)

            tb.append("DS A", dscoreA_str)
            tb.append("DS B", dscoreB_str)

            tb.append("Extra Score A", extra_scoreA_str)
            tb.append("Extra Score B", extra_scoreB_str)
            tb.append("Extra Info A",  extra_infoA)
            tb.append("Extra Info B",  extra_infoB)

            tb.append("DS A Mod", dscoreA_mod_str)
            tb.append("DS B Mod", dscoreB_mod_str)
            
            tb.output("\t")
            
        tb.clear()
        tb.append("Student ID", "Correct")
        for qnum in self.get_qnumsA():
            tb.append(qnum, self.get_correctA(qnum))
        for qnum in self.get_qnumsB():
            tb.append(qnum, self.get_correctB(qnum))
        tb.output("\t")

        tb.append("Student ID", "Mode")
        for qnum in self.get_qnumsA():
            mode = self.get_answers_mode(qnum)
            tb.append(qnum, ",".join(mode))
        for qnum in self.get_qnumsB():
            mode = self.get_answers_mode(qnum)
            tb.append(qnum, ",".join(mode))
        tb.output("\t")
        
        tb.append("Student ID", "Correct Students")
        for qnum in self.get_qnumsA():
            cs = self.count_correct_studentsA(qnum)
            tb.append(qnum, `cs`)
        for qnum in self.get_qnumsB():
            cs = self.count_correct_studentsB(qnum)
            tb.append(qnum, `cs`)
        tb.output("\t")

        tb.append("Student ID", "Question number")
        for qnum in self.get_qnumsA():
            tb.append(qnum, qnum)
        for qnum in self.get_qnumsB():
            tb.append(qnum, qnum)     
        tb.output("\t")

        print
        print "\t".join(("Samples A", "%d" % score_distrA.get_n()))
        print "\t".join(("Average A", "%.2f" % score_distrA.get_mean()))
        print "\t".join(("SD A", "%.2f" % score_distrA.get_sd()))
        print
        print "\t".join(("Samples B", "%d" % score_distrB.get_n()))                       
        print "\t".join(("Average B", "%.2f" % score_distrB.get_mean()))
        print "\t".join(("SD B", "%.2f" % score_distrB.get_sd()))


if __name__ == "__main__":

    from Usefuls.TmpFile import TmpFile_II, TmpFile_III
    ans_sheet = TmpFile_II("""
001,70644300,1,6,6,2,3,8,4,2,3,4
002,70647600,2,Y,2,9,2,2,2,2,2,2
003,70642300,#,#,#,#,#,3,2,3,1,2
004,70641200,1,6,2,3,3,3,3,3,3,3
005,70643900,4,9,7,5,1,8,5,6,4,4
006,70681600,6,9,5,9,3,7,2,2,4,1,3,4
007,70680000,4,8,7,9,1,3,2,4,3,1,2,3
008,70740700,4,4,2,9,1,2,2,2,4,2,3,4
009,70680500,2,2,2,2,2,2,2,2,2,2,2,2
010,70649300,6,8,2,9,1,5,1,2,4,4,3,4
""", trim_f_line_flag = True)
    
    correct_ans1 = TmpFile_II("""
1=1
2=6
3=2
4=9
5=2
""")
    
    correct_ans2 = TmpFile_II("""
6=1
7=6
8=2
9=9
10=2
""")
    
    student_list = TmpFile_III("""
This is the list.
70641200 *
70644300 *
70642300 *
70680000 *
70649300 *
79151932 *
""")
    
    extra_info_list = TmpFile_III("""
70644300 7 15 9/15 10/15
70649300
""")

    analyze = Analyze_Sheet(ans_sheet.filename(),
                            correct_ans1.filename(),
                            correct_ans2.filename(),
                            student_list.filename(),
                            extra_info_list.filename())
    analyze.output()
