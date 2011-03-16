#!/usr/bin/env python

import sys

from Data_Struct.Data_Sheet3 import Data_Sheet
from Get_mm9_Genome_Frag2 import get_AFAS_mm9
from Seq_Packages.Seq.MultiFasta2 import FASTACMD_ERROR

sheet = Data_Sheet(sys.argv[1])

""" pos1 < pos2. From pos_start to pos_end - 1. Positional origin is 0."""

for key in sheet.row_labels():
    chr    = sheet.get_datum(key, "Chromosome")
    strand = sheet.get_datum(key, "Strand")
    pos1   = sheet.get_datum(key, "Pos1")
    pos2   = sheet.get_datum(key, "Pos2")
    # info   = sheet.get_datum(key, "Info")
    info = "."
    try:
        get_AFAS_mm9(chr, int(pos1), int(pos2), strand, 1000, 10000, key, info)
    except FASTACMD_ERROR, error:
        sys.stderr.write(error.descr())
        