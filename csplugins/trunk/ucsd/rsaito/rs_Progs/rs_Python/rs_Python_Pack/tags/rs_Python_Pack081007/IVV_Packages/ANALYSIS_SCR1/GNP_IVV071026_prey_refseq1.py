#!/usr/bin/env python

import Seq_Packages.Seq.MultiFasta2 as mf

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")

class MMM2(mf.MultiFasta_MEM):
    def extract_id(self, id):
        ids = id.split("|")
        # print ids, ids[3]
        return ids[3]

mmm = MMM2(rsc.GNP_IVV_Prey_RefSeq)

prey_refseq_len = {}

for id in mmm.get_ids():
    print id, len(mmm.get_sequence(id))
    prey_refseq_len[ id ] = len(mmm.get_sequence(id))
    # print mmm.get_sequence(id).return_neatseq(60)

print prey_refseq_len
