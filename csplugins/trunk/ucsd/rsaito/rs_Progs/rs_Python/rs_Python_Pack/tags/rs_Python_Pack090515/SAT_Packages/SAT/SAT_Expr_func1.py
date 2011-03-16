#!/usr/bin/env perl

from Usefuls.Adjust_to_thres import adjust_to_lower_thres

def extract_valid_minimum_simp(expr1, expr2, min = 0):

    if len(expr1) != len(expr2):
        raise "Expression data number error..."
       
    expr1_out = []
    for e in expr1:
        expr1_out.append(adjust_to_lower_thres(e, min))
    expr2_out = []
    for e in expr2:
        expr2_out.append(adjust_to_lower_thres(e, min))
        
    return expr1_out, expr2_out


def extract_valid_minimum(expr1, expr2,
                          flag1, flag2,
                          min = 0):
    
    if len(expr1) != len(expr2):
        raise "Expression data number error..."
    if len(flag1) != len(flag2):
        raise "Flag data number error..."
    if len(expr1) != len(flag1):
        raise "Expression data number and flag data number error..."
    
    expr1_out = []
    expr2_out = []
    for i in range(len(expr1)):
        e1, e2 = expr1[i], expr2[i]
        f1, f2 = flag1[i], flag2[i]
        
        if e1 < min or not f1:
            e1 = min
        if e2 < min or not f2:
            e2 = min
        
        expr1_out.append(e1)
        expr2_out.append(e2)
        
    return expr1_out, expr2_out

if __name__ == "__main__":
    
    expr1 = [1,2,3,4,5]
    expr2 = [2,4,6,8,10]
    flag1 = [1,1,0,0,1]
    flag2 = [0,1,1,0,1]
    
    print extract_valid_minimum_simp(expr1, expr2, 3)
    print extract_valid_minimum(expr1,
                                expr2,
                                flag1,
                                flag2,
                                2)

        