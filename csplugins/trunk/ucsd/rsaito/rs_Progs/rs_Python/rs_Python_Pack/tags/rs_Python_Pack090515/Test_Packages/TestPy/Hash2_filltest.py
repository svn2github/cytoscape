#!/usr/bin/env python

import sys
from Data_Struct.Hash2 import Hash

h_simple = Hash("A")
"""
h_simple.read_file_hd(sys.argv[1],
                      Key_cols_hd = [ "Col1" ],
                      Val_cols_hd = [ "Col2" ],
                      Fil_cols_hd = [ "Col2" ])
"""
h_simple.read_file(sys.argv[1],
                   Key_cols = [ 0 ],
                   Val_cols = [ 1 ],
                   Fil_cols = [ 1 ])
print h_simple.all_data()