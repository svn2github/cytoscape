#!/usr/bin/env python

import sys
import os

import Usefuls.TmpFile

class SAT_gnuplot:
    def set_sensedata(self, data):
        self.sensedata = data

    def get_sensedata(self):
        return self.sensedata

    def set_antisensedata(self, data):
        self.antisensedata = data

    def get_antisensedata(self):
        return self.antisensedata

    def set_conditions(self, conditions):
        self.conditions = conditions

    def get_conditions(self):
        return self.conditions

    def set_dataname1(self, dataname):
        self.dataname1 = dataname

    def get_dataname1(self):
        return self.dataname1

    def set_dataname2(self, dataname):
        self.dataname2 = dataname

    def get_dataname2(self):
        return self.dataname2

    def import_sat(self, sat):
        sensedata     = sat.get_transcript1(). \
                        expression_pat_single_probe()
        antisensedata = sat.get_transcript2(). \
                        expression_pat_single_probe()
        dataname1 = sat.get_transcript1().get_transcriptID()
        dataname2 = sat.get_transcript2().get_transcriptID()

        self.set_sensedata(sensedata)
        self.set_antisensedata(antisensedata)
        self.set_dataname1(dataname1)
        self.set_dataname2(dataname2)
        self.set_conditions(sat.get_transcript1().
                            get_expression_data().conditions())


    def output_data(self):
        res = ""
        res += "# Sense Data\n\n"
        for i in range(len(self.conditions)):
            res += `i+1` + "\t" + `self.get_sensedata()[i]` + "\n"

        res += "\n"

        res += "# Antisense Data\n\n"
        for i in range(len(self.conditions)):
            res += `i+1` + "\t" + `self.get_antisensedata()[i]` + "\n"

        res += "\n"

        return res

    def output_batch(self, datafile):

        xtics = []
        count = 1
        for condition in self.get_conditions():
            xtics.append('"%s" %d' % (condition, count))
            count += 1

        xtics_out = "(" + ", ".join(xtics) + ")"

        return """
set xrange [0:%d]
# set logscale y
set xtics %s
plot "%s" index 0:0 using ($1-0.1):2 with impulse lw 10 lt 2 title "%s", \\
     "%s" index 1:1 using ($1+0.1):2 with impulse lw 10 lt 1 title "%s"

pause -1
""" % (count, xtics_out,
       datafile, self.get_dataname1(), datafile, self.get_dataname2())

    def output_batch_line(self, datafile):

        xtics = []
        count = 1
        for condition in self.get_conditions():
            xtics.append('"%s" %d' % (condition, count))
            count += 1

        xtics_out = "(" + ", ".join(xtics) + ")"

        return """
set xrange [0:%d]
# set logscale y
set xtics %s
plot "%s" index 0:0 using 1:2 with line lt 2 title "%s", \\
     "%s" index 1:1 using 1:2 with line lt 1 title "%s"

pause -1
""" % (count, xtics_out,
       datafile, self.get_dataname1(), datafile, self.get_dataname2())

    def gnuplot(self):
        datafile_obj = Usefuls.TmpFile.TmpFile_II(self.output_data())
        batchfile_obj = Usefuls.TmpFile.TmpFile_II(self.output_batch(
            datafile_obj.filename()))
        os.system("gnuplot " + batchfile_obj.filename())

    def gnuplot_line(self):
        datafile_obj = Usefuls.TmpFile.TmpFile_II(self.output_data())
        batchfile_obj = Usefuls.TmpFile.TmpFile_II(self.output_batch_line(
            datafile_obj.filename()))
        os.system("gnuplot " + batchfile_obj.filename())


if __name__ == "__main__":
    from Expr_Packages.Expr_II.Expression1 import *
    from Expr_Packages.Expr_II.Transcript1 import *
    from SAT1 import *

    sat_plot = SAT_gnuplot()
    sat_plot.set_conditions(["Day 1", "Day 2", "Day 3"])
    sat_plot.set_sensedata([3,2,5])
    sat_plot.set_antisensedata([2, 1.2, 3])
    sat_plot.set_dataname1("Sense")
    sat_plot.set_dataname2("AntiSense")

    print sat_plot.output_data()
    print sat_plot.output_batch("Datafile")

    sat_plot.gnuplot()

    expr1_1 = Single_Expression("Probe 1", 10.3)
    expr1_1.set_cond("Condition 1")
    expr1_2 = Single_Expression("Probe 1", 10.5)
    expr1_2.set_cond("Condition 2")
    expr1_3 = Single_Expression("Probe 1", 10.7)
    expr1_3.set_cond("Condition 3")
    expr_pat1 = Expression_Pat("Probe 1")
    expr_pat1.add_Single_Expression(expr1_1)
    expr_pat1.add_Single_Expression(expr1_2)
    expr_pat1.add_Single_Expression(expr1_3)
    expr2_1 = Single_Expression("Probe 2", 11.3)
    expr2_1.set_cond("Condition 1")
    expr2_2 = Single_Expression("Probe 2", 11.5)
    expr2_2.set_cond("Condition 2")
    expr2_3 = Single_Expression("Probe 2", 11.7)
    expr2_3.set_cond("Condition 3")
    expr_pat2 = Expression_Pat("Probe 2")
    expr_pat2.add_Single_Expression(expr2_1)
    expr_pat2.add_Single_Expression(expr2_2)
    expr_pat2.add_Single_Expression(expr2_3)
    expr_pat_set = Expression_Data()
    expr_pat_set.add_exp_pat(expr_pat1)
    expr_pat_set.add_exp_pat(expr_pat2)
    transcript1 = Transcript_Factory().make("T1001")
    transcript2 = Transcript_Factory().make("T1002")
    transcript1.add_probe("Probe 1")
    transcript2.add_probe("Probe 2")

    transcript1.set_expression_data(expr_pat_set)
    transcript2.set_expression_data(expr_pat_set)

    sat = SAT_Factory().make("SAT001")
    sat.set_transcript1(transcript1)
    sat.set_transcript2(transcript2)

    sat_plot.import_sat(sat)

    print sat_plot.output_data()
    print sat_plot.output_batch_line("Datafile")

    sat_plot.gnuplot_line()

