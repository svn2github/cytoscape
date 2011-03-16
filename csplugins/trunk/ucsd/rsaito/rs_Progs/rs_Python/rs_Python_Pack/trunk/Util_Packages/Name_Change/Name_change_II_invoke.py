#!/usr/bin/env python

import sys
import os
import Name_change1

# ./Name_change1.py ./TESTDIR ".py" "Search_pat" "Before_pat" "After_pat" 

if len(sys.argv) == 1:
    spath  = raw_input("Enter path to search (ex. ../../DIR1): ")
    fext   = raw_input("Enter file ext to search (ex. .py)   : ")
    srcpat = raw_input("Enter search pattern (ex. Keyword)   : ")
    oldpat = raw_input("Enter pattern to replace (ex. Old)   : ")
    newpat = raw_input("Enter new pattern (ex. New pat.)     : ") 

elif len(sys.argv) == 6:
    spath = sys.argv[1]
    fext = sys.argv[2]
    srcpat = sys.argv[3]
    oldpat = sys.argv[4]
    newpat = sys.argv[5]

else:
    raise "Input parameter error."

os.path.walk(spath, Name_change1.change_names_II,
             "\t".join((fext,
                        srcpat,
                        oldpat,
                        newpat)))
