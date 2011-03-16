#!/usr/bin/env python

from cluster import HierarchicalClustering

def Euclean_Distance(x, y):
    sum = 0
    for i in range(len(x)):
        sum += 1.0 * (x[i] - y[i])**2
    return sum ** 0.5

data = [[12,34,23],
        [32,46,96],
        [ 9, 1, 2],
        [ 1, 2, 3],
        [10,31,32]]

clustering = HierarchicalClustering(data, lambda x,y: Euclean_Distance(x, y))

i = 0
for cluster in clustering.getlevel(10):
    print "Cluster #" + `i` + ":",
    print cluster
    i += 1
    
    

