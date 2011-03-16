#!/usr/bin/env python

import StatsI
import Vector1
import MathI

MIN = 99999999

def select_abs_min(*ilist):

    for i in range(len(ilist) - 1):
        if len(ilist[i]) != len(ilist[i+1]):
            raise "List size error ..."

    ret = []
    for j in range(len(ilist[0])):
        min = MIN
        for i in range(len(ilist)):
            if abs(ilist[i][j]) < abs(min):
                min = abs(ilist[i][j])
        ret.append(min)

    return ret

def select_abs_min_sgn(a1, a2):

    min_a = select_abs_min(a1, a2)
    prodt = Vector1.prod_abt(a1, a2)
    sgn_a = MathI.sgn_a(prodt)

    return Vector1.prod_abt(min_a, sgn_a)

def select_z_abs_min_sgn(a1, a2):
    za1 = StatsI.norm(a1)
    za2 = StatsI.norm(a2)

    return select_abs_min_sgn(za1, za2)


if __name__ == "__main__":
    print select_abs_min([1, 3, +2, +7],
                         [4, 2, -1, +9],
                         [7, 5, -7, -4])

    print select_abs_min_sgn([+1, -2, +3, -4],
                             [-2, +1, -2, +3])

    print select_z_abs_min_sgn([+1, +0, -1],
                               [-2, +0, +2])
                             
    """
    diff_score  = select_abs_min_sgn(a1 - a2, b1 - b2)
    ratio_score = select_abs_min_sgn(a1 - b1, a2 - b2)


    """
