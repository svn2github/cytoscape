#!/usr/bin/python

hash = { "Ichi" : { "Ni" : 3, "San":4, "Yon": 5 },
         "Ni":    { "Ni" : 4, "San":5, "Yon": 6 }}

for e1 in hash:
    for e2 in hash[e1]:
        print e1, e2, hash[e1][e2]


