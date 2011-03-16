!/usr/bin/env python

for line in open("tmp"):
    r = line.rstrip().split(" ")
    family = r[0][0].upper() + r[0][1:]
    given = r[1][0].upper() + r[1][1:]
    print "\t".join((family, given))