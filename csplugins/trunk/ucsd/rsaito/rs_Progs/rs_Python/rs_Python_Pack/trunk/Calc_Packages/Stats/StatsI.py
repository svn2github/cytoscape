#!/usr/bin/env python

import math
import Calc_Packages.Math.Vector1 as Vector1

def mean(array):
    return 1.0*sum(array)/len(array)

def var(array):
    u = mean(array)

    total = 0
    for x in array:
        total += (x - u) ** 2
    return total / len(array)

def var_infer(array):
    u = mean(array)

    total = 0
    for x in array:
        total += (x - u) ** 2
    return total / (len(array) - 1)

def sd(array):
    
    return math.sqrt(var(array))

def sd_infer(array):
    
    return math.sqrt(var_infer(array))

def sem(array):
    return sd_infer(array) / math.sqrt(len(array))

"""
def norm(array):

    u = mean(array)
    s = math.sqrt(var(array))

    norm = []
    for x in array:
        norm.append((x - u) / s)
    
    return norm
"""
    
def norm(array):

    u = mean(array)
    s = math.sqrt(var(array))
    return map(lambda x: (x - u) / s, array)

def corr(a1, a2):
    """ Identical to corr_sd """

    s_a1 = norm(a1)
    s_a2 = norm(a2)

    return Vector1.inner_prod(s_a1, s_a2) / len(a1)

class SamplesI:
    def __init__(self, samples):
        self.samples = samples
        self.n    = len(self.samples)
        self.mean = mean(self.samples)
        self.sd   = sd(self.samples)

    def get_n(self):
        return self.n        
    
    def get_samples(self):
        return self.samples
    
    def get_mean(self):    
        return self.mean
    
    def get_sd(self):
        return self.sd
    
    def get_rank(self, x, reverse = False):
        l = list(self.samples[:])
        l.sort(reverse = reverse)
        
        try:
            ret = l.index(x)
        except ValueError:
            for i in range(len(l)):
                if reverse is False and l[i] >= x:
                    ret = i
                    break
                elif reverse is True and l[i] <= x:
                    ret = i
                    break
        return ret
    
    def calc_zscore(self, x, mult = 1.0, stdp = 0):
        return (x - self.get_mean()) * mult / self.get_sd() + stdp


if __name__ == "__main__":

    from Usefuls.Num_filters import filter_floats

    a = (1,3,6,8,9,10,12)
    b = (1,2,3)

    print "Average:", mean(a)
    print "Variance:", var(a)


    a1 = (1,2,9,4,"-",6,7,8)
    a2 = (2,3,4,"-",6,7,0,9)

    a1_f, a2_f = filter_floats(a1, a2)

    print mean(a1_f)
    print mean(a2_f)
    print var(a1_f)
    print var(a2_f)
    print corr(a1_f, a2_f)

    a3 = (75, 68, 83, 90, 65, 77, 62, 80, 72, 78)
    print mean(a3)
    print var(a3)
    print var_infer(a3)
    print sd(a3)
    print sd_infer(a3)
    print sem(a3)
    
    print "Norm..."
    print norm(a3)
    print norm(a3)
    

    samples = SamplesI((-2,-2,6,6))
    print samples.get_mean()
    print samples.get_sd()
    print samples.calc_zscore(3, 10, 50)
    samples = SamplesI([1,2,3,4,5,6,7])
    print samples.get_rank(5.1, reverse = False)
    print samples.get_rank(5.1, reverse = True)
