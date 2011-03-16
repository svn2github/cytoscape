#!/usr/bin/env python

import Usefuls.MultiCol_Output as MCO
from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

class Plot:
    def __init__(self):
        self.plots = {}

    def add_point(self, classification, plot):
        if classification in self.plots:
            self.plots[classification].append(plot)
        else:
            self.plots[classification] = [ plot ]
        
    def get_x(self, classification):
        x = []
        for plot in self.plots[ classification ]:
            x.append(plot[0])
        return x

    def get_y(self, classification):
        y = []
        for plot in self.plots[ classification ]:
            y.append(plot[1])
        return y

    def mean(self, classification):
        return (mean(self.get_x(classification)),
                mean(self.get_y(classification)))

    def median(self, classification):
        return (median(self.get_x(classification)),
                median(self.get_y(classification)))

        
    def sd_infer(self, classification):
        return(sd_infer(self.get_x(classification)),
               sd_infer(self.get_y(classification)))
        
    def corr(self, classification):
        return corr(self.get_x(classification),
                    self.get_y(classification))

    def add_sd_cross(self, classification, new_class_name, n_points):
        m_v = self.mean(classification)
        s_v = self.sd_infer(classification)

        for i in range(n_points):
            x_pos = 2.0 * s_v[0] * i / n_points + m_v[0] - s_v[0]
            y_pos = m_v[1]
            self.add_point(new_class_name, (x_pos, y_pos))
        
        for i in range(n_points):
            x_pos = m_v[0] # 2.0 * s_v[0] * i / n_points + m_v[0] - s_v[0]
            y_pos = 2.0 * s_v[1] * i / n_points + m_v[1] - s_v[1]
            self.add_point(new_class_name, (x_pos, y_pos))
       
    def add_md_cross(self, classification, new_class_name, rate_lower, rate_upper):
        m_v = self.median(classification)
        for x in rate_points(self.get_x(classification), rate_lower, rate_upper):
            self.add_point(new_class_name, (x, m_v[1]))
        for y in rate_points(self.get_y(classification), rate_lower, rate_upper):
            self.add_point(new_class_name, (m_v[0], y)) 
            
    def conditional_count_x(self, func_cond,
                            *args, **kwargs):
        ret = {}
        total = 0
        for classification in self.plots.keys():
            sub_total = 0
            for x in self.get_x(classification):
                if apply(func_cond, (x,) + args, kwargs):
                    sub_total += 1
            ret[ classification ] = sub_total
            total += sub_total
        ret[ "Total" ] = total
        return ret

    def output(self, order = None):
        if order is None:
            order = self.plots.keys()
            order.sort()

        out = MCO.MultiCol_Output("\t", "\t")
        for classif in order:
            out.append("\t".join((classif, "")))
            for plot in self.plots[classif]:
                plot_str = map(lambda u: `u`, plot)
                out.append("\t".join(plot_str))
            out.next_col()
        out.output()


if __name__ == "__main__":
    plot = Plot()
    plot.add_point("Class A", (10, -5))
    plot.add_point("Class B", (10.1, -5))
    plot.add_point("Class B", (1, -5))
    plot.add_point("Class A", (10, -7))
    plot.add_point("Class A", (8, -12))
    plot.add_point("Class B", (10, -9))
    plot.add_point("Class C", (10, -9))
    plot.add_point("Class C", (10, -9))
    plot.add_point("Class C", (10, -9))
    plot.add_point("Class C", (10, -9))
    plot.add_sd_cross("Class A", "-+SD for Class A", 10)
    
    plot.output(("Class A", "Class B", "Class C", "-+SD for Class A"))

    print plot.get_x("Class A")
    print plot.get_y("Class A")
    print plot.mean("Class A")
    print plot.sd_infer("Class A")
    print plot.corr("Class A")

    ploto = Plot()
    ploto.add_point("Class A",  (0, 100))
    ploto.add_point("Class A",  (1, 200))
    ploto.add_point("Class A",  (2, 190))   
    ploto.add_point("Class A",  (3, 160))
    ploto.add_point("Class A",  (4, 170))
    ploto.add_point("Class A",  (5, 180))
    ploto.add_point("Class B",  (1, 1))
    ploto.add_point("Class A",  (6, 140))
    ploto.add_point("Class A",  (7, 150))
    ploto.add_point("Class A",  (8, 130))
    ploto.add_point("Class A",  (9, 110))
    ploto.add_point("Class A", (10, 120))
    ploto.add_md_cross("Class A", "60% for Class A", 0.2, 0.8)
    ploto.output()
    def func_cond(x, thres):
        if x >= thres:
            return True
        else:
            return False
    print ploto.conditional_count_x(func_cond, 0)
    