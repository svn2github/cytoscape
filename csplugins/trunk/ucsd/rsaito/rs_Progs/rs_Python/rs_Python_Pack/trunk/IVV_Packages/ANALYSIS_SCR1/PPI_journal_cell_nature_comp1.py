#!/usr/bin/python

import sys
sys.path.append("./rsIVV_Python2")

import string
import Usefuls.Sheet
import Data_Struct.Hash
import PPI.ppi2

cell_file = "./PPI_public/HT_data/PPI_human05_cell_raw.txt"
nature_file = "./PPI_public/HT_data/PPI_human05_nature_raw.txt"

cell_sheet = Usefuls.Sheet.Sheet_tab(cell_file)
cell_ppi = Data_Struct.Hash.Hash_headf("N")
cell_ppi.read_file(
    filename = cell_file,
    Key_cols_hd = [ "LocID_b", "LocID_p" ],
    Val_cols_hd = [])
cell_PPI = PPI.ppi2.ppi2()
cell_PPI.read_hash_tab2(cell_ppi, "Cell")

nature_sheet = Usefuls.Sheet.Sheet_tab(nature_file)
nature_ppi = Data_Struct.Hash.Hash_headf("N")
nature_ppi.read_file(
    filename = nature_file,
    Key_cols_hd = [ "EntrezGeneIDA", "EntrezGeneIDB" ],
    Val_cols_hd = [])
nature_PPI = PPI.ppi2.ppi2()
nature_PPI.read_hash_tab2(nature_ppi, "Nature")

header = nature_sheet.read_line()
"""
print "CELL" + "\t" + string.join(header, "\t")
while True:
    r = nature_sheet.read_line()
    if r == False: break
    p1, p2 = r[0], r[2]
    hit = "---"
    if cell_ppi.has_pair(p1, p2) <> False:
        hit = "HIT"

    print hit + "\t" +  string.join(r, "\t")

"""
cell_PPI.add_ppis(nature_PPI)
cell_PPI.both_dir()
for interaction in cell_PPI.get_non_redu_ppi():
    p1, p2, source = interaction
    source_str = string.join(source, ",")
    print string.join((p1, p2, source_str), "\t")

