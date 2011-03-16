#!/usr/bin/env python

import sys
sys.path.append("../")
import string

import PPI.PPi5
import Homology.GeneID_to_IVV1
import Homology.Homology4_descr
import Usefuls.rsConfig
import Data_Struct.Hash
import IVV_info.IVV_info

from Homology.Homol_measure import HM

rsc = Usefuls.rsConfig.RSC("../../../rsIVV_Config")

ppi_hash = Data_Struct.Hash.Hash_headf("S")
ppi_hash.read_file(filename = rsc.KnownPPI_Hsap,
                   Key_cols_hd = [ "Gene ID 1", "Gene ID 2" ],
                   Val_cols_hd = [ "PubMed ID" ])

ivv_info = IVV_info.IVV_info.IVV_info(rsc.IVVInfo)
homol_refseq2ivv = Homology.Homology4_descr.HomologyDescr4(rsc.HomolIVVRefSeq_cDNA_NF)

geneid_to_refseq = Data_Struct.Hash_A.Hash_filt_A()
geneid_to_refseq.set_filt([0, "9606"])
geneid_to_refseq.read_file(filename = rsc.Gene2RefSeq,
                           Key_cols = [1],
                           Val_cols = [6])

geneid2ivv = Homology.GeneID_to_IVV1.GeneID_to_IVV(ivv_info, geneid_to_refseq, homol_refseq2ivv)

ppi = PPI.PPi5.PPi5()
ppi.read_hash_tab(ppi_hash)
ppi.both_dir()

hm = HM(1.0e-1, 0.0, 0.7, 10)

# Homo dimer?
for protein in ppi.get_protein_set():
    print "\t".join((protein.get_protein_name(),
                     `len(ppi.interactor(protein))`,
                     `geneid2ivv.count_average_bait_geneids(
        protein.get_protein_name(), hm, reprod = 2)`,
                     `geneid2ivv.count_average_preys(
        protein.get_protein_name(), hm)`,
                     `geneid2ivv.check_MOCK(protein.get_protein_name(),
                                          hm)`))

