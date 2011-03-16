#!/usr/bin/env python

import sys

# if not in
sys.path.append("/home/osada/PyMod")

# judge_hg17.txt and judge_mm6.txt are required for coding information.

import SAT_Packages.SAT44K_Info.basic as basic
import SAT_Packages.SAT44K_Info.sas as sas

numata44k_str_human_file = "/home/osada/DB/AllPairs.H.nst"
numata44k_str_mouse_file = "/home/osada/DB/AllPairs.M.nst"

save_human_pickle_file = "/home/rsaito/Antisense/UPSET/AllPairs44K.H.pkl"
save_mouse_pickle_file = "/home/rsaito/Antisense/UPSET/AllPairs44K.M.pkl"

ap = sas.load_nst(numata44k_str_human_file)
basic.dump(ap, save_human_pickle_file, 0)

ap = sas.load_nst(numata44k_str_mouse_file)
basic.dump(ap, save_mouse_pickle_file, 0)

