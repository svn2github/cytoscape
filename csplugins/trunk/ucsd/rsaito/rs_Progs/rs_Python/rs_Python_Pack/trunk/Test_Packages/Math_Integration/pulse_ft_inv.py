#!/usr/bin/env python

from math import pi, sin, cos
from scipy.integrate import quad, romberg, tplquad

from Calc_Packages.Math.rsInteg1 import rsInteg_I

def pulse_ft_inv(w, t, u):
    # print "---", w, t, u
    return sin(u*w)*cos(w*t)/(2*pi*u*w)

"""
for i in range(100):
    print pulse_ft_inv(i+1, 1, 1)

"""
print quad(sin, 0, pi)
print quad(pulse_ft_inv, 0.001, 10000, args=(0.2, 1))[0]*2
print rsInteg_I(pulse_ft_inv, 0.001, 100, 0.001, 0.1, 1)*2

t = -5
while t <= 5:
    print t, rsInteg_I(pulse_ft_inv, 0.001, 100, 0.001, t, 1)*2
    t = t + 0.1 

