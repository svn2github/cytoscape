#!/usr/bin/env python

from Usefuls.ListProc1 import list_identical_size_check

def median(array):
    
    array_local = list(array[:])
    array_local.sort()
    l = len(array_local)
    if l % 2 == 0:
        return 0.5 * (array_local[ l/2 ] + array_local[ l/2 - 1 ])
    else:
        return array_local[ (l-1)/2 ]

def rate_point(array, rate):
    array_local = list(array[:])
    array_local.sort()
    interv = len(array_local) - 1
    return array_local[ int(interv * rate) ]
    
def rate_points(array, lower_rate, upper_rate):
    array_local = list(array[:])
    array_local.sort()
    interv = len(array_local) - 1

    lower = int(interv * lower_rate)
    upper = int(interv * upper_rate)

    points = []    
    for i in range(upper - lower + 1):
        points.append(array_local[i + lower])
    
    return points

def ordering(array):
    """ (15,14,13,12,12,11) -> [5.0, 4.0, 3.0, 1.5, 1.5, 0.0] """
    
    array_sorted = list(array[:])
    array_sorted.sort()
    num_to_order = {}
    for i in range(len(array_sorted)):
        if array_sorted[i] in num_to_order:
            num_to_order[ array_sorted[i] ].append(i)
        else:
            num_to_order[ array_sorted[i] ] = [ i ]
    ret_order = []
    
    for num in array:
        orders = num_to_order[ num ]
        ret_order.append(1.0*sum(orders)/len(orders))        
    
    return ret_order

def ordering_centred(array):
    ordered = ordering(array)
    return map(lambda x: x - (1.0*len(array)-1)/2, ordered)
    
def get_centre(arrays):
    """ Size of all the arrays must be the same """
    list_identical_size_check(arrays)
    
    lowest = None
    lowest_i = []
    centre_ordered_set = map(ordering_centred, arrays)
    
    for i in range(len(centre_ordered_set[0])):
        ords = map(lambda ar: abs(ar[i]), centre_ordered_set)
        if lowest is None or lowest > max(ords):
            lowest = max(ords)
            lowest_i = [i]
        elif lowest == max(ords):
            lowest_i.append(i)
        
    return lowest_i
    
def get_centre_closer_to_median(arrays):
    
    lowest_i_cand = get_centre(arrays)
    lowest_i = []
    
    lowest_distance_to_median = None
    for i in lowest_i_cand:
        dists = map(lambda ar: abs(ar[i] - median(ar)), arrays)
        # print i, dists, max(dists)
        if(lowest_distance_to_median is None or
           lowest_distance_to_median > max(dists)):
            lowest_distance_to_median = max(dists)
            lowest_i = [i]
        elif lowest_distance_to_median == max(dists):
            lowest_distance_to_median = max(dists)
            lowest_i.append(i)
            
    return lowest_i    
        
if __name__ == "__main__":
    a = (1,10,3,6,8,9,12)
    print "Median:", median(a)

    b = (0,1,2,3,4,5,6,7,8,9,10)
    print rate_point(b, 0.0)
    print rate_point(b, 0.25)
    print rate_point(b, 0.3)
    print rate_point(b, 0.5)
    print rate_point(b, 1.0)
    print rate_points(b, 0.2, 0.7)
    print "Ordering"
    print ordering((15,14,13,12,12,11))
    print ordering_centred((15,14,13,12,11,12))
    print
    print get_centre(((0, 1, 2, 3, 4),
                      (0, 3, 5, 1, 4),
                    ))
    

    print get_centre_closer_to_median(((0, 1.0, 2, 3, 4),
                                       (0, 3.1, 4, 1, 2),
                                       ))  
    