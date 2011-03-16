#!/usr/bin/env python

import sys

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

import SAT_Packages.Integration.SAT_Global_Center as SAT_Global_Center

from Data_Struct.Hash2 import Hash
from Data_Struct.DictSet1 import DictSet

from Usefuls.ListProc1 import NonRedList

sys.stderr.write("Reading 44K simple information ...\n")

plusisos2satids_h = Hash("A")
plusisos2satids_h.read_file_hd(rsc.human44k_simple_map,
                               Key_cols_hd = ["Isoforms on plus strand"],
                               Val_cols_hd = ["SAT ID"])
minusisos2satids_h = Hash("A")
minusisos2satids_h.read_file_hd(rsc.human44k_simple_map,
                                Key_cols_hd = ["Isoforms on minus strand"],
                                Val_cols_hd = ["SAT ID"])

plusiso2satids  = DictSet()
minusiso2satids = DictSet()

for plusisos in plusisos2satids_h:
    for isoid in plusisos.split(","):
        for satid in plusisos2satids_h[plusisos]:
            plusiso2satids.append(isoid, satid)
for minusisos in minusisos2satids_h:
    for isoid in minusisos.split(","):
        for satid in minusisos2satids_h[minusisos]:
            minusiso2satids.append(isoid, satid)

satid2represen = Hash("S")
satid2represen.read_file_hd(rsc.human44k_simple_map,
                            Key_cols_hd = ["SAT ID"],
                            Val_cols_hd = ["Representative isoform on plus strand",
                                           "Representative isoform on minus strand" ])

satid2overlap = Hash("S")
satid2overlap.read_file_hd(rsc.human44k_simple_map,
                           Key_cols_hd = ["SAT ID"],
                           Val_cols_hd = ["Overlapping length"])


def get_antis_overlap(accessionid):
    if plusiso2satids.has_key(accessionid):
        satids = plusiso2satids[accessionid]
        antistrand_index = 1
    elif minusiso2satids.has_key(accessionid):
        satids = minusiso2satids[accessionid]
        antistrand_index = 0
    else:
        return None
   
    represents = []
    overlaps = []
    for satid in satids:
        represents.append(
            satid2represen.val(satid).split("\t")[antistrand_index])
        overlaps.append(int(satid2overlap.val(satid)))
    return accessionid, satids, represents, 1.0*sum(overlaps)/len(overlaps)
        
def get_antis_overlap_geneid(geneid):
    geneid2accession = SAT_Global_Center.get_geneid2accession()
    accessions = geneid2accession.val_force(geneid)
    
    ret = []
    tt_length = 0
    count     = 0
    
    if accessions:
        for accession in NonRedList(accessions):
            if get_antis_overlap(accession):
                acc, satids, reprs, length = get_antis_overlap(accession)
                ret.append(acc + "-" + ",".join(reprs))
                tt_length += length
                count += 1
    
    if count:
        return ";".join(ret), 1.0 * tt_length / count
    else:
        return "", 0
    
        
if __name__ == "__main__":
    print get_antis_overlap("Hs#S5496359")
    print get_antis_overlap("NM_024952")
    
    count = 0
    print "Plus -> Minus"
    for accessionid in plusiso2satids:
        print get_antis_overlap(accessionid)
        count += 1
        if count > 50:
            break
    count = 0
    print "Minus -> Plus"
    for accessionid in minusiso2satids:
        print get_antis_overlap(accessionid)
        count += 1
        if count > 50:
            break
        
    geneid2accession = SAT_Global_Center.get_geneid2accession()
    for geneid in geneid2accession:
        print geneid, get_antis_overlap_geneid(geneid)
        
