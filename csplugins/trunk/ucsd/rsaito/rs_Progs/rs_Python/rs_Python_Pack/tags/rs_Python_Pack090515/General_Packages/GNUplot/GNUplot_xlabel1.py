#!/usr/bin/env python

import sys
import os

import Usefuls.TmpFile

class GNUplot:
    def __init__(self):
        self.conditions = []
        self.datanames = []
        self.data = []
    
    def set_conditions(self, conditions):
        self.conditions = conditions

    def get_conditions(self):
        return self.conditions

    def set_data(self, dataname, data):
        if len(self.get_conditions()) != len(data):
            raise "Data length error"
        
        self.datanames.append(dataname)
        self.data.append(data)

    def get_data(self):
        return self.data

    def get_datanames(self):
        return self.datanames

    def output_data(self):

        res = ""
        for j in range(len(self.get_datanames())):
            dataname = self.get_datanames()[j]
            res += "# " + dataname + "\n\n"
            for i in range(len(self.conditions)):
                res += `i+1` + "\t" + `self.get_data()[j][i]` + "\n"
            res += "\n"

        return res

    def output_batch(self, datafile):

        pos_width = 0.2
        lin_width = 5

        xtics = []
        position = 1
        for condition in self.get_conditions():
            xtics.append('"%s" %d' % (condition, position))
            position += 1
        xtics_out = "(" + ", ".join(xtics) + ")"

        plot = []
        for i in range(len(self.get_datanames())):
            dataname = self.get_datanames()[i]
            if i == 0:
                head = "plot"
            else:
                head = "    "
            pos_mod = (i * pos_width / (len(self.get_datanames()) - 1)
                       - pos_width / 2)
            each_plot  = '%s "%s" index %d:%d using ($1%+.2lf):2' % \
                (head, datafile, i,i, pos_mod)
            each_plot += ' with impulse lw %d title "%s"' % \
                (lin_width, dataname)
            plot.append(each_plot)
        plot_out = ", \\\n".join(plot)

        return """
set xrange [0:%d]
# set logscale y
set xtics %s

%s

pause -1
""" % (position, xtics_out, plot_out)

    def output_batch_line(self, datafile):

        xtics = []
        position = 1
        for condition in self.get_conditions():
            xtics.append('"%s" %d' % (condition, position))
            position += 1
        xtics_out = "(" + ", ".join(xtics) + ")"

        plot = []
        for i in range(len(self.get_datanames())):
            dataname = self.get_datanames()[i]
            if i == 0:
                head = "plot"
            else:
                head = "    "
            each_plot  = '%s "%s" index %d:%d using 1:2' % \
                (head, datafile, i,i)
            each_plot += ' with line title "%s"' % dataname
            plot.append(each_plot)
        plot_out = ", \\\n".join(plot)

        return """
set xrange [0:%d]
# set logscale y
set xtics %s

%s

pause -1
""" % (position, xtics_out, plot_out)

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

    gp = GNUplot()
    gp.set_conditions(["cond A", "cond B", "cond C"])
    gp.set_data("Data #1", [1,10,7])
    gp.set_data("Data #2", [3,12,5])
    gp.set_data("Data #3", [3,12,8])
    print gp.output_data()
    print gp.output_batch("DataFile")
    gp.gnuplot()

    print gp.output_batch_line("DataFile")
    gp.gnuplot_line()
