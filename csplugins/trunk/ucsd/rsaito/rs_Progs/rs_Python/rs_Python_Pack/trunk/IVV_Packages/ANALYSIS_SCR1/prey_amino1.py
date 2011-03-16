#!/usr/bin/env python

from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from Seq_Packages.Seq.MultiFasta2 import MultiFasta
from Seq_Packages.Homology.Fasta_align_pack2 import FastY
import IVV_Packages.Integration.IVV_Global_Center as IVV_Global

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

import Usefuls.Table_maker

import sys

homol_file = rsc.HomolIVVRefSeq_cDNA_NF # Maybe NF2?
nucfile = rsc.IVVSeq
amifile = rsc.RefSeq_Prot_Human

ivv_info = IVV_Global.get_ivv_info()

geneid_to_refseq = IVV_Global.get_geneid_to_refseq()
refseq_to_geneid = geneid_to_refseq.ret_reversed_Hash("A")

homol = HomologyDescr(homol_file)
nucfasta = MultiFasta(nucfile)
amifasta = MultiFasta(amifile)

tb = Usefuls.Table_maker.Table_row()

for queryID in homol:
    
    ivv_type = ivv_info.ID_Type(queryID)
    
    subjectID = homol.subject_ID(queryID)[0]
    geneID    = ",".join(list(set(refseq_to_geneid.val_force(subjectID))))
    
    fy = FastY()
    s1 = nucfasta.get_singlefasta(queryID)
    s1.set_ID(queryID)
    s2 = amifasta.get_singlefasta(subjectID)
    s2.set_ID(subjectID)
    fy.set_fasta_obj(s1, s2)
    
    
            
    fy.exec_fasta()
    tb.append("IVV ID", queryID)
    tb.append("IVV Type", ivv_type)
    tb.append("RefSeq Protein ID", subjectID)
    tb.append("Gene ID", geneID)
    tb.append("FASTA E-Value", `fy.eval()`)
    tb.append("IVV Alignment", fy.get_alignment()[0])
    tb.append("RefSeq Protein Alignment", fy.get_alignment()[1])
    tb.output('\t')
