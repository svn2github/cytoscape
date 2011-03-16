#!/usr/bin/env python

# This module uses many global variables.
# This module should be imported by "import",
# rather than by "from"

# set_filter_mode must be called as soon as possible.

import sys

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

import IVV_Packages.IVV_Info.IVV_info1 as IVV_info
import IVV_Packages.IVV_Info.IVV_filter1 as IVV_filter
import IVV_Packages.IVV_Info.PullDown1 as PullDown
from IVV_Packages.IVV_Motif.Motif_info1 import Motif_info
from Seq_Packages.Motif.MMI2 import MMI

import Seq_Packages.Homology.Homology_descr4 as Homology_descr
from IVV_Packages.IVV_Homology.Prey_intra_homol1 \
     import Prey_intra_homol

import Seq_Packages.Motif.RefSeq_SwissPfam as RefSeqProt_SwissPfam

import Data_Struct.Hash2

def get_reported_ppi():

    global reported_ppi

    if not 'reported_ppi' in globals():
        sys.stderr.write("Reading reported PPIs...\n")
        reported_ppi = Data_Struct.Hash2.Hash("A")
        reported_ppi.read_file_hd(filename = rsc.KnownPPI_Hsap,
                                  Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                                  Val_cols_hd = ["PubMed ID"])
    return reported_ppi


def get_wanted_genes():

    global wanted_genes

    if not 'wanted_genes' in globals():
        sys.stderr.write("Reading wanted genes...\n")
        wanted_genes = Data_Struct.Hash2.Hash("A")
        wanted_genes.read_file(filename = rsc.Wanted,
                               Key_cols = [0], Val_cols = [1])
    return wanted_genes


def set_filter_mode(flag):

    global filter_mode

    if 'filter_mode' not in globals():
        filter_mode = flag
    else:
        raise "Re-setting ivv filtering mode ..."

def get_filter_mode():

    global filter_mode
    
    if 'filter_mode' in globals():
        return filter_mode
    else:
        return None

def get_prey_filter():

    global prey_filter

    if not 'prey_filter' in globals():
        prey_filter = IVV_filter.IVV_filter()
        prey_filter.set_Prey_filter_file(rsc.PreyFilter)    
    
    return prey_filter


def get_ivv_info():
    
    global ivv_info

    if not 'ivv_info' in globals():

        if get_filter_mode():
            outinfo = "ON"
        else:
            outinfo = "OFF"

        sys.stderr.write("Reading IVV information (Filter = %s) ...\n" 
                         % (outinfo))

        if get_filter_mode():
            ivv_info = IVV_info.IVV_info(rsc.IVVInfo, get_prey_filter())
        else:
            ivv_info = IVV_info.IVV_info(rsc.IVVInfo)

    return ivv_info


def get_pulldown():

    global pulldown

    if not 'pulldown' in globals():
        pulldown = PullDown.PullDown(rsc.PullDown)

    return pulldown


def get_motif_info():

    global motif_info

    if not 'motif_info' in globals():
        sys.stderr.write("Reading Motif information...\n")
        motif_info = Motif_info(rsc.MotifInfo2)

    return motif_info

def get_homol_ivv_to_refseq():

    global homol_ivv_to_refseq

    if not 'homol_ivv_to_refseq' in globals():
        
        sys.stderr.write("Reading homology information...\n")
        homol_ivv_to_refseq = Homology_descr.HomologyDescr(
            rsc.HomolIVVRefSeq_cDNA_NF2)

    return homol_ivv_to_refseq

def get_iPfam():

    global iPfam

    if not 'iPfam' in globals():
        iPfam = MMI(rsc.iPfam)

    return iPfam

def get_homol_prey_self():

    global homol_prey_self

    if not 'homol_prey_self' in globals():
        sys.stderr.write("Reading self-homology information...\n")
        homol_prey_self = Prey_intra_homol()
        homol_prey_self.load_shelve(rsc.IntraPreyHomol2)

    return homol_prey_self


def get_gene_info():

    global gene_info

    if not 'gene_info' in globals():
        sys.stderr.write("Reading NCBI Gene information...\n")
        gene_info = Data_Struct.Hash2.Hash("S")
        gene_info.read_file(rsc.GeneInfo,
                            Key_cols = [1],
                            Val_cols = [2,8])

    return gene_info

def get_geneid_to_refseq(taxid = "9606"):

    global geneid_to_refseq

    if not 'geneid_to_refseq' in globals():
        sys.stderr.write("Reading Gene ID -> RefSeq information...\n")
        geneid_to_refseq = Data_Struct.Hash2.Hash("A")
        geneid_to_refseq.set_filt([0, taxid])
        geneid_to_refseq.read_file(filename = rsc.Gene2RefSeq,
                                   Key_cols = [1],
                                   Val_cols = [6])

    return geneid_to_refseq

def get_refseqprot_swisspfam():
    
    global refseqprot_swisspfam
    
    if not 'refseqprot_swisspfam' in globals():
        refseqprot_swisspfam = RefSeqProt_SwissPfam.RefSeqProt_SwissPfam(
            rsc.HomolRefSeqProtSprot,
            rsc.SwissPfam_save)

    return refseqprot_swisspfam

if __name__ == "__main__":

    gene_info_tmp    = get_gene_info()
    print gene_info_tmp.val_force("2353")

    reported_ppi_tmp        = get_reported_ppi()
    wanted_genes_tmp        = get_wanted_genes()
    prey_filter_tmp         = get_prey_filter()
    ivv_info_tmp            = get_ivv_info()
    pulldown_tmp            = get_pulldown()
    motif_info_tmp          = get_motif_info()
    homol_ivv_to_refseq_tmp = get_homol_ivv_to_refseq()
    iPfam_tmp               = get_iPfam()
    homol_prey_self_tmp     = get_homol_prey_self()
    gene_info_tmp           = get_gene_info()
    geneid_to_refseq_tmp    = get_geneid_to_refseq()

