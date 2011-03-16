#!/usr/bin/env python

import sys
sys.path.append("../")

import string

from PPI_Pred.PPIPred4 import PPIPred4
import IVV_info.IVV_info
import IVV_info.Prey_info
import IVV_info.PullDown

from Motif.Motif_info1 import Motif_info
from Motif.MMI2 import MMI2
from Motif.RefSeq_SwissPfam import RefSeqProt_SwissPfam
import Data_Struct.Hash_A
import Usefuls.DictProc1
import Data_Struct.NonRedSet

import Homology.Homology4_descr
from Homology.Prey_intra_homol import Prey_intra_homol1, Prey_redund_level
from Homology.Homol_measure import HM

import Seq.MultiFasta2
import Seq.Align_MultiPair

from Usefuls.rsConfig import RSC

config_file = "../../../rsIVV_Config"
rsc = RSC(config_file)

prey_hm  = HM(1.0e-1, 0.7, 0, 10)

bait_motif_thres = 1.0e-3
prey_motif_thres = 1.0e-3

mmi_sep = ":-:"

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Data_Struct.Hash_A.Hash_headf_A()
reported_ppi.read_file(filename = rsc.KnownPPI,
                       Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                       Val_cols_hd = ["PubMed ID"])

sys.stderr.write("Reading IVV information...\n")
filter = IVV_info.IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(rsc.PreyFilter)
ivv_info = IVV_info.IVV_info.IVV_info(rsc.IVVInfo) #, filter)
bfilt_dict = Usefuls.DictProc1.file_to_dict_simple(rsc.PreyFilter)
pulldown = IVV_info.PullDown.PullDown1(rsc.PullDown)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif_info(rsc.MotifInfo)
iPfam = MMI2(rsc.iPfam)

sys.stderr.write("Reading Gene information...\n")
geneid_to_refseq = Data_Struct.Hash_A.Hash_filt_A()
geneid_to_refseq.set_filt([0, "9606"])
geneid_to_refseq.read_file(filename = rsc.Gene2RefSeq,
                           Key_cols = [1],
                           Val_cols = [6])

sys.stderr.write("Reading homology information...\n")
homol_ivv_to_refseq = Homology.Homology4_descr.HomologyDescr4(
    rsc.HomolIVVRefSeq)
homol_ivv_to_refseq.enable_reverse()

refseqprot_swisspfam = RefSeqProt_SwissPfam(
    rsc.HomolRefSeqProtSprot100,
    rsc.SwissPfam_save)

sys.stderr.write("Reading self-homology information...\n")
homol_prey_self = Prey_intra_homol1()
homol_prey_self.load_shelve(rsc.IntraPreyHomol)

print

while True:

    geneid = raw_input("Input Gene ID: ")

    ref_mf = Seq.MultiFasta2.MultiFasta(rsc.RefSeq_Prot_Human)
    ivv_mf = Seq.MultiFasta2.MultiFasta(rsc.IVVSeq)

    refseqs_redund = geneid_to_refseq.val_force(geneid)
    refseqids = Data_Struct.NonRedSet.NonRedList(refseqs_redund)

    for refseqid in refseqids:
        hits = homol_ivv_to_refseq.reverse_query_ID_hm_thres(
            refseqid, prey_hm)
        hit_preys = []
        for hit in hits:
            if ivv_info.ID_Type(hit) == "Prey":
                hit_preys.append(hit)

        print "---", refseqid, "---"
        print hit_preys

        motifdescr =  refseqprot_swisspfam.ret_motif_info(refseqid)

        Seq.Align_MultiPair.Align_MultiFasta_motif(ref_mf, ivv_mf,
                                                   refseqid, hit_preys,
                                                   motifdescr)
        print


        prey_set = IVV_info.Prey_info.Prey_Set(ivv_info.Prey_info(),
                                               hit_preys)
        for prey in prey_set.get_Preys():
            print prey.preyID(), prey.geneid(), prey.motifs(motif_info, prey_motif_thres),
            print "PullDown:", prey.pulldown(pulldown),
            print "ORF:", prey.qual_force("orf"),
            print "Mock:", prey.qual_force("mock"),
            print "Filt:", prey.dict_check(bfilt_dict)
        print


