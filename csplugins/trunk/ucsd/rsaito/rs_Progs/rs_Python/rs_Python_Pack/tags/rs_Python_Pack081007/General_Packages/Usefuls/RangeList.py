#!/usr/bin/env python

class RangeList:
    def __init__(self, *range_list):
        self.range_list = range_list
    
    def check_overlap(self):
        """ ex: range_list = ((10, 20), (15, 21), (17, 30)) """
        l_common = -10000000
        r_common =  10000000

        for range in self.range_list:
            l, r = range
#            print "Checking", range
            if l > l_common: l_common = l
            if r < r_common: r_common = r
            
        return (l_common, r_common)

    def overlap_length(self):
        l, r = self.check_overlap()
        return r - l
        
if __name__ == "__main__":
    rl = RangeList((10, 25), (15, 21), (17, 30))
    print rl.check_overlap()
    print rl.overlap_length()
