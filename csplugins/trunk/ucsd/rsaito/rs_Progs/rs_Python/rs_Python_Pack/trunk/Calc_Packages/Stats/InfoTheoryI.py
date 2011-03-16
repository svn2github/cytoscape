#!/usr/bin/env python

from math import log

def log2(x):
    
    return log(1.0*x)/log(2.0)

def ratelist(ilist):

    return map(lambda x: 1.0*x / sum(ilist), ilist)

def info(x):
    
    return -log2(1.0*x)

def entropy(ilist):
    
    ratel = ratelist(ilist)
    ret = 0
    for x in ratel:
        if x > 0:
            ret += x * info(x)
        
    return ret

def rel_entropy(ilist1, ilist2):
    if len(ilist1) != len(ilist2):
        raise "Length of list not identical: (%s %s)" % (ilist1, ilist2)
    ratelist1 = ratelist(ilist1)
    ratelist2 = ratelist(ilist2)
    
    ret = 0
    for i in range(len(ratelist1)):
        ret += ratelist1[i] * log2(1.0*ratelist1[i]/ratelist2[i])
    
    return ret

def mutual_info(ilistlist):
    rows = len(ilistlist)
    for row in ilistlist:
        if len(row) != rows:
            raise "Not a square list: %s" % (ilistlist,)
    cols = rows
    
    row_sums = map(lambda row: sum(row), ilistlist)
    col_sums = []
    for j in range(cols):
        col_sum = 0
        for i in range(rows):
            col_sum += ilistlist[i][j]
        col_sums.append(col_sum)
    total = sum(row_sums)
    
    rate_row_sums = ratelist(row_sums)
    rate_col_sums = ratelist(col_sums)
    rate_ilistlist = []
    for i in range(rows):
          rate_ilistlist.append(map(lambda x: 1.0*x/total, ilistlist[i]))
    
    ret = 0
    for i in range(rows):
        for j in range(cols):
            ret += (rate_ilistlist[i][j] *
                    log2(rate_ilistlist[i][j]
                         / (rate_row_sums[i] * rate_col_sums[j])))
                         
    print row_sums, col_sums
    print rate_ilistlist
    return ret
    

if __name__ == "__main__":
    print ratelist([10,20,40,30])
    print log2(1024)
    print entropy((10,20,30,40))
    print rel_entropy((1,2,3), (4,5,6))
    
    print mutual_info(([1,2,3],[4,5,6],[7,8,9]))
    