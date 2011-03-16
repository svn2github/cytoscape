#!/usr/bin/env python

import sys
sys.path.append("../")
import os

from IVV_info.Bait_info import Bait_info

fastacmd_EXEC = "/pub/software/BLAST/bin/fastacmd"
ivv_info_file = "../../IVV/ivv_human7.3_info"
ivv_seq_file = "../../IVV/ivv_human7.3.tfa"

bait_info = Bait_info(ivv_info_file)

for bait in bait_info.baits():
    if bait_info.bait_is_protein(bait):
        fasta_input = "%s -d %s -s %s " % (fastacmd_EXEC,
                                           ivv_seq_file,
                                           bait)
        os.system(fasta_input)

