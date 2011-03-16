#!/usr/bin/env python

import sys
from Data_Struct.Hash2 import Hash

filename = sys.argv[1] 

agilent_designed_probe = Hash("S")
agilent_designed_probe.read_file_hd(filename = rsc.ES_Neuro_control,
                                    Key_cols_hd = [ "TargetID" ],
                                    Val_cols_hd = [ "BPStart", "EndDistance" ]) # "Sequence"

