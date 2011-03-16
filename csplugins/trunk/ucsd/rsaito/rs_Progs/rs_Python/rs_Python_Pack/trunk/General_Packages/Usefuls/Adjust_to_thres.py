#!/usr/bin/env perl

from ListProc1 import map_extI

def adjust_to_lower_thres(val, thres):
    if val < thres:
        return thres
    else:
        return val
    
def adjust_to_higher_thres(val, thres):
    if val > thres:
        return thres
    else:
        return val
    
def adjust_to_lower_thres_list(vals, thres):
    
    return map_extI(adjust_to_lower_thres,
                    vals, thres)
    """
    ret = []
    for val in vals:
        ret.append(adjust_to_lower_thres(val, thres))
    return ret
    """
def adjust_to_higher_thres_list(vals, thres):
    
    return map_extI(adjust_to_higher_thres,
                    vals, thres)
    
    """
    ret = []
    for val in vals:
        ret.append(adjust_to_higher_thres(val, thres))
    return ret
    """

if __name__ == "__main__":
    print adjust_to_lower_thres_list((1,2,3,4,5), 3)
    print adjust_to_higher_thres_list((1,2,3,4,5), 3)  