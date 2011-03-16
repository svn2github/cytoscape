#!/usr/bin/env python

from math import log

from Expr_Packages.Agilent.Feature_Extraction1 import Feature_Ext
from Expr_Packages.Agilent.Feature_Ext_Set1 import Feature_Ext_Set

from Calc_Packages.ListCalc.ListCalcI import big_diff
from Calc_Packages.Stats.StatsI import mean, sd

from Seq_Packages.Homology.read_BLAT_psl4 import BLAT_psl

from Data_Struct.Hash2 import Hash

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
map_info = RSC_II("UCSC_MapInfo")
BLAT_map = RSC_II("BLAT_map")

from BioData_Packages.Misc.header import parse_FASTA_header_ids

import Usefuls.Table_maker


def get_map(accession, map_GenBank, map_RefSeq): 
    acc_parsed = parse_FASTA_header_ids(accession)

    qid = ""
    chr = ""
    strand = ""
    pos1 = ""
    pos2 = ""

    id_set = False

    if "gb" in acc_parsed and not id_set:
        for id in acc_parsed["gb"]:
            if id in map_GenBank:
                qid = id
                hit = map_GenBank[ id ][0]
                chr = hit.get_subject_id()
                strand = hit.strand
                pos1 = hit.subject_start
                pos2 = hit.subject_end
                id_set = True
                break
            
    if "ref" in acc_parsed and not id_set:
        for id in acc_parsed["ref"]:
            if id in map_RefSeq:
                qid = id
                hit = map_RefSeq[ id ][0]
                chr = hit.get_subject_id()
                strand = hit.strand
                pos1 = hit.subject_start
                pos2 = hit.subject_end
                id_set = True
                break
            
    
    return qid, chr, strand, pos1, pos2
        
def get_map2(accession, map_GenBank, map_RefSeq):
     
    qid = ""
    chr = ""
    strand = ""
    pos1 = ""
    pos2 = ""
     
    if accession in map_RefSeq:
        qid = accession
        hit = map_RefSeq[ accession ][0]
        chr = hit.get_subject_id()
        strand = hit.strand
        pos1 = hit.subject_start
        pos2 = hit.subject_end
        
    elif accession in map_GenBank:
        qid = accession
        hit = map_GenBank[ accession ][0]
        chr = hit.get_subject_id()
        strand = hit.strand
        pos1 = hit.subject_start
        pos2 = hit.subject_end    

    return qid, chr, strand, pos1, pos2

def get_map_simple(accession, smap):
    
    qid = ""
    chr = ""
    strand = ""
    pos1 = ""
    pos2 = ""
    
    if accession in smap:
        qid = accession
        hit = smap[ accession ][0]
        chr = hit.get_subject_id()
        strand = hit.strand
        pos1 = hit.subject_start
        pos2 = hit.subject_end   
        
    return qid, chr, strand, pos1, pos2


map_ES_Neuro_ctrl = BLAT_psl(BLAT_map.mm9_ES_Neuro_ctrl, bestonly = True, UCSC_map = False,
                             MultiID = True, Version = False)

ES_Neuro_ctrl = Hash("S")
ES_Neuro_ctrl.read_file_hd(filename = rsc.ES_Neuro_control,
                           Key_cols_hd = [ "tmp ID" ],
                           Val_cols_hd = [ "Accession", "Gene Name", "Comment" ])

ES_Neuro_ctrl_IDs = ES_Neuro_ctrl.keys()
ES_Neuro_ctrl_IDs.sort(lambda x, y:int(x[1:]) - int(y[1:]))


arrays = [ 
          "CMB4_D0",
          "CMB4_D4",
          "CMB4_D8",
          "CMB4_N5",
          "CMB4_N9",
          "CMB4_N9_K"
          ]

kf = {}
for array in arrays:
    kf[ array ] = rsc.__dict__[ array ]
# print kf

output = Usefuls.Table_maker.Table_row()

sysname_count = {}

for featureNum in ES_Neuro_ctrl_IDs:
    
    accession  = ES_Neuro_ctrl.val_accord_hd(featureNum, "Accession")

    qid, chr, strand, pos1, pos2 = get_map_simple(accession, map_ES_Neuro_ctrl)   
    
    counter = sysname_count.get(accession, 0)
    sysname_count[ accession ] = sysname_count.get(accession, 0) + 1
    
    output.append("FeatureNum", featureNum)
    output.append("SystematicName", "")
    output.append("Redundant count", `counter`)
    output.append("Accession", accession)
    output.append("Used ID for Mapping", qid)
    output.append("Chromosome", chr)
    output.append("Strand", strand)
    if pos1 != "":
        pos1 = `pos1`
    output.append("Pos1", pos1)
    if pos2 != "":
        pos2 = `pos2`
    output.append("Pos2", pos2)
    output.append("Comment", "%s (%s)" %(ES_Neuro_ctrl.val_accord_hd(featureNum, "Gene Name"),
                                         ES_Neuro_ctrl.val_accord_hd(featureNum, "Comment")))

    """
    output.append("Mean", "")
    output.append("Stdev", "")
    output.append("Min", "")
    output.append("-log(D4/D0)", "")
    """
    
    for array in arrays:
        output.append(array, "")

    # print output.a
    output.output("\t")

# import sys;sys.exit()


# sysname_count = {}

map_GenBank = BLAT_psl(map_info.mm9_all_mRNA,  bestonly = True, UCSC_map = True)
map_RefSeq  = BLAT_psl(map_info.mm9_refSeqAli, bestonly = True, UCSC_map = True)

info_keys = ["ProbeName",
             # "Sequence",
             "accessions",
             "chr_coord",
             "GeneName",
             "SystematicName",
             "gProcessedSignal",
             "gIsPosAndSignif",
             "gIsWellAboveBG",
             "gIsSaturated"]

fe_D0 = Feature_Ext(kf["CMB4_D0"], info_keys)
feset = Feature_Ext_Set()
feset.set_feature_exts(kf, fe_D0, info_keys)

# print feset.get_data_names()
# print feset.get_feature_ext("CMB4_D0").get_genenames()

featureNums = feset.get_feature_ext("CMB4_D0").get_featureNums()
featureNums.sort(lambda x,y:int(x)-int(y))

for featureNum in featureNums:
    
    output.append("FeatureNum", featureNum)  
    systematicName = fe_D0.featureNum_to_info.val_accord_hd(featureNum, "SystematicName")
    accession      = fe_D0.featureNum_to_info.val_accord_hd(featureNum, "accessions")
    qid, chr, strand, pos1, pos2 = get_map(accession, map_GenBank, map_RefSeq)   
    counter = sysname_count.get(systematicName, 0)
    sysname_count[ systematicName ] = sysname_count.get(systematicName, 0) + 1

    output.append("SystematicName", systematicName)
    output.append("Redundant count", `counter`)
    output.append("Accession", accession)
    output.append("Used ID for Mapping", qid)
    output.append("Chromosome", chr)
    output.append("Strand", strand)
    if pos1 != "":
        pos1 = `pos1`
    output.append("Pos1", pos1)
    if pos2 != "":
        pos2 = `pos2`
    output.append("Pos2", pos2)
    output.append("Comment", "")

    exppat = feset.get_exp_pat_feanum(arrays, featureNum)
    inc_rate, inc_diff, dec_rate, dec_diff = big_diff(exppat) 

    """
    output.append("Mean", "%.3f" % (mean(exppat), ))
    output.append("Stdev", "%.3f" % (sd(exppat), ))
    output.append("Min", "%.3f" % (min(exppat),))
    output.append("-log(D4/D0)", "%.3f" % -log(1.0*exppat[1]/exppat[0]))
    """

    for array in arrays:
        output.append(array, "%.3f" % (feset.get_feature_ext(array).get_gProcessedSignal(featureNum), ))

    output.output("\t")

