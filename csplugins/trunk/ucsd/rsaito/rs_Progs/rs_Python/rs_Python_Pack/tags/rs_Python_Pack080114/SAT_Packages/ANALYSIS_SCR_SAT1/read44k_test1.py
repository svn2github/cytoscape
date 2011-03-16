#!/usr/bin/env python

import sys
import Data_Struct.Hash2

class Hash_RIKEN_BRC_44K(Data_Struct.Hash2.Hash):
    def pre_read_file(self, fh):
        while True:
            line = fh.readline()
            if line[0:8] == "FEATURES":
                break
        fh.seek(-len(line), 1)

if __name__ == "__main__":

    r44k_h = Hash_RIKEN_BRC_44K("S")
    r44k_h.read_file_hd(filename = sys.argv[1],
                        Key_cols_hd = ["ProbeName"],
                        Val_cols_hd = ["Sequence",
                                       "ControlType",
                                       "Description",
                                       "GeneName",
                                       "SystematicName",
                                       "gProcessedSignal",
                                       "gIsPosAndSignif",
                                       "gIsWellAboveBG"])

    for probe in r44k_h.keys():
        print "\t".join([probe,
                         r44k_h.val_accord_hd(probe, "SystematicName"),
                         r44k_h.val_accord_hd(probe, "gProcessedSignal")])

