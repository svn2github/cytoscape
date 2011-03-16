#!/usr/bin/env python

import sys

from Data_Struct.Hash2 import Hash

h = Hash("A")
h.set_miscolumn_permit()
h.read_file_hd("/home/rsaito/TMP/tmptmp",
               Key_cols_hd = ["RID" ],
               Val_cols_hd = ["Substrate 1", "Substrate 2",
                              "Product 1", "Product 2", "Product 3"],
               Fil_cols_hd = ["RID"])

for l in h:
    print l, h[l]
    
