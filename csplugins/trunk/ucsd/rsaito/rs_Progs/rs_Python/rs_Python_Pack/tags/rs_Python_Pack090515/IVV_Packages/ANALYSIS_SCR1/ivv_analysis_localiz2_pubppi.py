#!/usr/bin/env python

import sys
sys.path.append("../")
import string

import Data_Struct.Hash
import Usefuls.Sheet_Analysis

localization_file = "prot_localiz"

localization = Data_Struct.Hash.Hash("A")
localization.read_file(filename = localization_file,
		       Key_cols = [0], Val_cols = [1]);

ppifile = sys.argv[1]
sf = Usefuls.Sheet_Analysis.Sheet_tab(ppifile)

print string.join(("Protein 1", "Protein 2",
                   "Lozalization 1", "Lozalization 2",
                   "Common"), "\t")

while True:
    r = sf.read_line()
    if not r: break

    p1, p2 = r
    localiz1 = localization.val_force(p1)
    if len(localiz1) != 1: continue
    localiz2 = localization.val_force(p2)
    if len(localiz2) != 1: continue

    common = ""
    if localiz1[0] == localiz2[0]:
        common = localiz1[0]

    print string.join((p1, p2, localiz1[0], localiz2[0], common), "\t")


