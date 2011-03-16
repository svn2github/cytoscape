#!/usr/bin/env python

import Usefuls.MultiCol_Output as MCO
from Dict_Ordered import Dict_Ordered
from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

# x of plot should be plot.get_x(), not plot.x???

class Plot:
    def __init__(self, order = None):
        self.plots = Dict_Ordered(order)

    def add_point(self, classification, plot):
        if classification in self.plots:
            self.plots[classification].append(plot)
        else:
            self.plots[classification] = [ plot ]
        
    def get_plots(self, classification):
        return self.plots[ classification ]
        
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
            
    def conditional_count(self, func_cond,
                          *args, **kwargs):
        
        ret = Dict_Ordered()
        total = 0
        for classification in self.plots.keys():
            sub_total = 0
            for plot in self.get_plots(classification):
                if apply(func_cond, (plot,) + args, kwargs):
                    sub_total += 1
            ret[ classification ] = sub_total
            total += sub_total
        ret[ "Total" ] = total
        return ret
            
    def conditional_count_x(self, func_cond,
                            *args, **kwargs):
        ret = Dict_Ordered()
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

    def conditional_count_y(self, func_cond,
                            *args, **kwargs):
        ret = Dict_Ordered()
        total = 0
        for classification in self.plots.keys():
            sub_total = 0
            for y in self.get_y(classification):
                if apply(func_cond, (y,) + args, kwargs):
                    sub_total += 1
            ret[ classification ] = sub_total
            total += sub_total
        ret[ "Total" ] = total
        return ret

    def output(self):

        out = MCO.MultiCol_Output("\t", "\t")
        for classif in self.plots.keys():
            out.append("\t".join((classif, "")))
            for plot in self.plots[classif]:
                plot_str = map(lambda u: `u`, plot)
                out.append("\t".join(plot_str))
            out.next_col()
        out.output()


if __name__ == "__main__":
    plot = Plot(("Class A", "Class B", "Class C", "-+SD for Class A"))
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
    
    plot.output()

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
    ploto.add_point("Class B",  (1, 5))
    ploto.add_point("Class B",  (10, 5))
    ploto.add_point("Class A",  (6, 140))
    ploto.add_point("Class A",  (7, 150))
    ploto.add_point("Class A",  (8, 130))
    ploto.add_point("Class A",  (9, 5))
    ploto.add_point("Class A", (10, 120))
    ploto.add_md_cross("Class A", "60% for Class A", 0.2, 0.8)
    ploto.output()
    def func_cond(x, thres):
        if x >= thres:
            return True
        else:
            return False

    def func_cond2(plot, thres):
        if plot[0] >= thres and plot[1] >= thres:
            return True
        else:
            return False
        
        
    print ploto.conditional_count_x(func_cond, 5)
    print ploto.conditional_count_y(func_cond, 150)
    print ploto.conditional_count(func_cond2, 5)
    
    