#!/usr/bin/env python

import os
from Histogram import Hist
import TmpFile

class GNUplot_hist:
    def __init__(self, *histograms):
        self.histograms = histograms

    def output_data(self):
        ret = ""
        for hist in self.histograms:
            ret += "# " + hist.get_name() + "\n\n"
            ret += hist.get_display_rate() + "\n\n"

        return ret

    def output_batch(self, datafile):

        ret_a = []
        count = 0
        for hist in self.histograms:
            ret_s = ""
            ret_s += '"' + datafile + '" '
            ret_s += "index " + `count` + ":" + `count` + \
                " using 1:2 with boxes "
            ret_s += "title " + '"' + hist.get_name() + '"'
            ret_a.append(ret_s)
            count += 1
        return "plot " + ", ".join(ret_a) + "\n\npause -1"

    def gnuplot(self):
        datafile_obj =  TmpFile.TmpFile_II(self.output_data())
        batchfile_obj = TmpFile.TmpFile_II(self.output_batch(
            datafile_obj.filename()))
        os.system("gnuplot " + batchfile_obj.filename())

if __name__ == "__main__":
    hist1 = Hist(0, 100, 10)
    hist1.set_name("Test 1")
    hist1.add(15)
    hist1.add(25)
    hist1.add(55)

    hist2 = Hist(0, 100, 10)
    hist2.set_name("Test 2")
    hist2.add(15)
    hist2.add(25)
    hist2.add(25)
    hist_plot = GNUplot_hist(hist1, hist2)

    print hist_plot.output_data()
    print hist_plot.output_batch("samplefile")
    hist_plot.gnuplot()
