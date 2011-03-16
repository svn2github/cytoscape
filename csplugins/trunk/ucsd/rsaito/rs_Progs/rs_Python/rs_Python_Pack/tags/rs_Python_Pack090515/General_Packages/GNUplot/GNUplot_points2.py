#!/usr/bin/env python

import os
import Usefuls.TmpFile as TmpFile
from Usefuls.Instance_check import instance_class_check

class Points_Set:
    def __init__(self, points_set = None):
        """ The format of points_set should be
        { "Data label 1" : [[x11, y11], [x12, y12], ...],
          "Data label 2" : [[x21, y21], [x22, y22], ...],
          :
        } """

        self.labels = []
        self.points_set = {}
        
        if points_set is not None:
            self.points_set = points_set
            for label in points_set:
                if label not in self.labels:
                    self.labels.append(label)

    def add_points(self, label, points):
        """ The format of points should be
        [[ x1, y1 ], [x2, y2], ... ] """

        if label in self.points_set:
            self.points_set[ label ] += points
        else:
            self.points_set[ label ] = points
            self.labels.append(label)


    def add_point(self, label, point):
        self.add_points(label, [ point ])

    def get_labels(self):
        return self.labels

    def __iter__(self):
        return self.labels.__iter__()

    def __getitem__(self, label):
        return self.points_set[label]


class GNUplot_points:
    def __init__(self, points_set):
        instance_class_check(points_set, Points_Set)
        self.points = points_set

    def output_data(self):
        ret = []
        for datalabel in self.points:
            ret.append("\n# %s" % datalabel)
            for point in self.points[datalabel]:
                x, y = point
                ret.append("%f\t%f" % (x, y))
            ret.append("")

        return "\n".join(ret) + "\n"

    def output_batch(self, datafile):
        """ Addition of header is temporal. """
        header = """
set xrange [-2:2]
set yrange [-2:2]
"""
        plot = []
        for i in range(len(self.points.get_labels())):
            dataname = self.points.get_labels()[i]
            if i == 0:
                head = "plot"
            else:
                head = "    "
            each_plot  = '%s "%s" index %d:%d using 1:2' % \
                (head, datafile, i,i)
            each_plot += ' title "%s"' % dataname
            plot.append(each_plot)
        plot_out = header + ", \\\n".join(plot) + "\n"

        return plot_out + "\npause -1"

    def gnuplot(self):
        datafile_obj =  TmpFile.TmpFile_II(self.output_data())
        batchfile_obj = TmpFile.TmpFile_II(self.output_batch(
            datafile_obj.filename()))
        os.system("gnuplot " + batchfile_obj.filename())


if __name__ == "__main__":

    pset = Points_Set({ "Data #1" : [[1, 10], [2, 11], [-1,17]],
                        "Data #2" : [[2, 10], [3, 11], [5,17]],
                        "Data #3" : [[3, 10], [2, 18], [3,17]] })
    pset.add_points("Data #4", [[4, 20],[5, 23],[2, 11],[6,11]])
    pset.add_point("Data #3", [ 100, 102 ])
    pset.add_point("Data #5", [ 10, 102 ])
    pset.add_points("Data #4", [[-1, -1], [-2, -3]])
    print pset.get_labels()

    for label in pset:
        print label, pset[label]
    
    gnuplot = GNUplot_points(pset)
    print gnuplot.output_data()
    print gnuplot.output_batch("gnuplot_tmp")
    gnuplot.gnuplot()

