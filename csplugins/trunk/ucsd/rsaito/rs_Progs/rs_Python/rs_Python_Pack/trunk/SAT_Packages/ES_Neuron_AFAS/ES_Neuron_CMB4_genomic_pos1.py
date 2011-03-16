#!/usr/bin/env python

from Expr_Packages.Agilent.Feature_Extraction1 import Feature_Ext
from Expr_Packages.Agilent.Feature_Ext_Set1 import Feature_Ext_Set

from Calc_Packages.ListCalc.ListCalcI import big_diff
from Calc_Packages.Stats.StatsI import mean, sd

from Seq_Packages.Homology.read_BLAT_psl4 import BLAT_psl

from Data_Struct.Hash2 import Hash

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
map_info = RSC_II("UCSC_MapInfo")

import Usefuls.Table_maker

from BioData_Packages.Misc.header import parse_FASTA_header_ids

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


map_GenBank = BLAT_psl(map_info.mm9_all_mRNA,  bestonly = True, UCSC_map = True)
map_RefSeq  = BLAT_psl(map_info.mm9_refSeqAli, bestonly = True, UCSC_map = True)

ES_Neuro_ctrl = Hash("S")
ES_Neuro_ctrl.read_file_hd(filename = rsc.ES_Neuro_control,
                           Key_cols_hd = [ "tmp ID" ],
                           Val_cols_hd = [ "Accession", "Gene Name", "Comment" ])

ES_Neuro_ctrl_IDs = ES_Neuro_ctrl.keys()
ES_Neuro_ctrl_IDs.sort(lambda x, y:int(x[1:]) - int(y[1:]))

arrays = [ 
          "CMB4_D0",
          ]

kf = {}
for array in arrays:
    kf[ array ] = rsc.__dict__[ array ]

output = Usefuls.Table_maker.Table_row()

sysname_count = {}

for featureNum in ES_Neuro_ctrl_IDs:
    
    accession  = ES_Neuro_ctrl.val_accord_hd(featureNum, "Accession")
    qid, chr, strand, pos1, pos2 = get_map2(accession, map_GenBank, map_RefSeq)   
    
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

    for array in arrays:
        output.append(array, "")

    # print output.a
    output.output("\t")

# import sys;sys.exit()

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

feset = Feature_Ext_Set()
feset.set_feature_exts(kf, None, info_keys)

# print feset.get_data_names()
# print feset.get_feature_ext("CMB4_D0").get_genenames()

featureNums = feset.get_feature_ext("CMB4_D0").get_featureNums()
featureNums.sort(lambda x,y:int(x)-int(y))


sysname_count = {}

for featureNum in featureNums:
    output.append("FeatureNum", featureNum)
    systematicName = feset.get_feature_ext(array).featureNum_to_info.val_accord_hd(featureNum, "SystematicName")
    accession  = feset.get_feature_ext(array).featureNum_to_info.val_accord_hd(featureNum, "accessions")
    qid, chr, strand, pos1, pos2 = get_map(accession, map_GenBank, map_RefSeq)   
    counter = sysname_count.get(systematicName, 0)
    sysname_count[ systematicName ] = sysname_count.get(systematicName, 0) + 1
    output.append("SystematicName", systematicName)
    output.append("Redundant count", `sysname_count[ systematicName ]`)
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

    for array in arrays:
        output.append(array, "%6f" % (feset.get_feature_ext(array).get_gProcessedSignal(featureNum), ))

    output.output("\t")

