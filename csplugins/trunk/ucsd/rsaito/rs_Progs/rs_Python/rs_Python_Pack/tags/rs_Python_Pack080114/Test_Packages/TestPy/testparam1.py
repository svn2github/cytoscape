#!/usr/bin/env python

def paramtest(first, *prm1, **prm2):
    print first
    print prm1
    print prm2

paramtest("First", 1, 2, 3, a = "A", b = "B", c = "C")
    
