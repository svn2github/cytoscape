#!/usr/bin/env python

import sys

r = []
E = []
D = []
C = []

ConMark = "XX"

def alpha(n1, n2):
  
  if ((n1 == 'a' and n2 == 't') or (n1 == 't' and n2 == 'a')):
    return -2
  if ((n1 == 'c' and n2 == 'g') or (n1 == 'g' and n2 == 'c')):
    return -3
  return +100.0;

def d_to_c(i, j):

    if i >= j:
        return
    elif D[i][j] == ConMark:
         C[i][j] = 1
         d_to_c(i + 1, j - 1)
    else:
        d_to_c(i, D[i][j] - 1)
        d_to_c(D[i][j], j)

def ES(i, j):

    if E[i][j] is not None:
        return E[i][j]

    if i >= j:
        return 0.0
    else:
        con = ES(i + 1, j - 1) + alpha(r[i], r[j])
        disj_min = 99999;
        for k in range(i + 1, j + 1):
            disj = ES(i, k - 1) + ES(k, j);
            if disj_min > disj:
                disj_min = disj
                disj_min_k = k

        if con < disj_min: # More priority for disjoint 
            min = con; 
            D[i][j] = ConMark
        else:
            min = disj_min
            D[i][j] = disj_min_k

    E[i][j] = min;
    return min;

def initialize(M):
    for i in range(len(r)):
        M.append([])
        for j in range(len(r)):
            M[i].append(None)

def display(M, blank = "    "):
    for i in range(len(r)):
        for j in range(len(r)):
            if M[i][j] is ConMark:
                sys.stdout.write("%3s " % ConMark)
            elif M[i][j] is not None:
                sys.stdout.write("%3s " % `int(M[i][j])`)
            else:
                sys.stdout.write(blank)
        print



r = list("aggatccact")
initialize(E)
initialize(D)
initialize(C)
ES(0, len(r) - 1)
d_to_c(0, len(r) - 1)

print "* E *"
display(E)
print "* D *"
display(D)
print "* C *"
display(C, "  0 ")


