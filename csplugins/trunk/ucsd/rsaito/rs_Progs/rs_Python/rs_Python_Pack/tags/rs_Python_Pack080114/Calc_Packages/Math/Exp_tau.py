#!/usr/bin/env python

def tau(numbers):
    """ Calculates tissue specificity
    of expression data. """

    max_num = max(numbers)
    total = 0
    for x in numbers:
        y = 1.0 * x / max_num
	total = total + (1-y)
    
    tau = 1.0 * total / (len(numbers)-1)
    return tau
    

if __name__ == "__main__":
    
    test = (0,8,0,0,0,2,0,2,0,0,0,0)
    tau_res = tau(test)
    print tau_res

