#!/usr/bin/env python

import os

import Data_Struct.Hash3 as Hash

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

conv_onc_afas = Hash.Hash("A")
conv_onc_afas.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                           Key_cols_hd = [ "Gene ID (ONC)" ],
                           Val_cols_hd = [ "Gene ID (AFAS)" ])

conv_afas_onc = Hash.Hash("S")
conv_afas_onc.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                           Key_cols_hd = [ "Gene ID (AFAS)" ],
                           Val_cols_hd = [ "Gene ID (ONC)" ])

sat11k_okay = Hash.Hash("S")
sat11k_okay.read_file_hd(rsc.human11k_okay,
                         Key_cols_hd = [ "id" ],
                         Val_cols_hd = "L")

okay11k_satids = {}
for id in sat11k_okay:
    id_in_rdata = "lcl|" + id
    okay11k_satids[ id_in_rdata ] = ""

# print conv_onc_afas[ "ONC-Z71621" ]

def fe_annotation_mask(fe_file,
                       okay11k_satids = okay11k_satids, 
                       conv_onc_afas = conv_onc_afas, 
                       conv_afas_onc = conv_afas_onc):
    
    file_masked = rsc.FE_Mask_DIR + '/' + os.path.basename(fe_file) + "_Masked.csv"
    file_IDs_masked = rsc.FE_Mask_DIR + '/' + os.path.basename(fe_file) + "_IDs_Masked.txt"
    file_IDs_unmasked = rsc.FE_Mask_DIR + '/' + os.path.basename(fe_file) + "_IDs_unMasked.txt"
    
    fh = open(fe_file, "r")
    fw = open(file_masked, "w")
    
    fw_IDs_masked   = open(file_IDs_masked, "w")
    fw_IDs_unmasked = open(file_IDs_unmasked, "w")
    
    while True:
        line = fh.readline()
        fw.write(line)
        if line.startswith("FEATURES"):
            break
        
    fh.seek(-len(line), 1)
    header = fh.readline().split('\t')
    
    for line in fh:
        mask = True
        r = line.rstrip().split("\t")
        systematicname = r[ header.index("SystematicName") ]
        sname_first_part = "|".join(systematicname.split('|')[0:2]).split(".")[0]
        # print systematicname, sname_first_part
        
        if (systematicname in conv_onc_afas 
            or systematicname in conv_afas_onc):
            mask = False
        elif sname_first_part in okay11k_satids:
            mask = False        
        elif r[ header.index("SubTypeName") ] != "":
            mask = False
                
        if mask:
            fw_IDs_masked.write(r[ header.index("SystematicName") ] + '\n')
            r[ header.index("Sequence") ] = ""
            r[ header.index("GeneName") ] = ""
            r[ header.index("SystematicName") ] = ""
        else:
            fw_IDs_unmasked.write(r[ header.index("SystematicName") ] + '\n')            
    
        fw.write("\t".join(r) + '\n')
        
    fh.close()
    fw_IDs_masked.close()
    fw_IDs_unmasked.close()

    return file_masked, file_IDs_masked, file_IDs_unmasked


if __name__ == "__main__":

    for file_idx in vars(rsc):
        if file_idx.startswith("FE_Human11k_Colon"):
            filename = getattr(rsc, file_idx)
            print "Processing :", getattr(rsc, file_idx)
            file_masked, file_IDs_masked, file_IDs_unmasked = fe_annotation_mask(filename)
            print "Created    :", file_masked
    print "\nFinished."
    
