#!/usr/bin/env python

import sys
import string

# from IVV_Packages.IVV_Info.IVV_Conv import IVV_Conv
from IVV_Packages.PPI_Pred.PPIPred4 import PPIPred
import IVV_Packages.IVV_Info.IVV_info1 as IVV_info
import IVV_Packages.IVV_Info.IVV_filter1 as IVV_filter
import IVV_Packages.IVV_Info.Prey_info1 as Prey_info
import IVV_Packages.IVV_Info.PullDown1 as PullDown

from IVV_Packages.IVV_Motif.Motif_info1 import Motif_info
from Seq_Packages.Motif.MMI2 import MMI
from Seq_Packages.Motif.RefSeq_SwissPfam import RefSeqProt_SwissPfam
import Data_Struct.Hash_A
import Usefuls.DictProc1
import Data_Struct.NonRedSet1

import Seq_Packages.Homology.Homology_descr4 as Homology_descr
from IVV_Packages.IVV_Homology.Prey_intra_homol1 \
    import Prey_intra_homol, Prey_redund_level
from Seq_Packages.Homology.Homol_measure import HM

import Seq_Packages.Seq.MultiFasta2 as MultiFasta
import Seq_Packages.Seq.Align_MultiPair as Align_MultiPair

from Usefuls.rsConfig import RSC_II

rsc = RSC_II("rsIVV_Config")

bait_hm  = HM(1.0e-3)
prey_hm  = HM(1.0e-1, 0.7, 0, 10)

bait_motif_thres = 1.0e-3
prey_motif_thres = 1.0e-3

mmi_sep = ":-:"

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Data_Struct.Hash_A.Hash_headf_A()
reported_ppi.read_file(filename = rsc.KnownPPI_Hsap,
                       Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                       Val_cols_hd = ["PubMed ID"])

sys.stderr.write("Reading IVV information...\n")
filter = IVV_filter.IVV_filter()
filter.set_Prey_filter_file(rsc.PreyFilter)
ivv_info = IVV_info.IVV_info(rsc.IVVInfo) #, filter)
bfilt_dict = Usefuls.DictProc1.file_to_dict_simple(rsc.PreyFilter)
pulldown = PullDown.PullDown(rsc.PullDown)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif_info(rsc.MotifInfo)
iPfam = MMI(rsc.iPfam)

sys.stderr.write("Reading Gene information...\n")
geneid_to_refseq = Data_Struct.Hash_A.Hash_filt_A()
geneid_to_refseq.set_filt([0, "9606"])
geneid_to_refseq.read_file(filename = rsc.Gene2RefSeq,
                           Key_cols = [1],
                           Val_cols = [6])

sys.stderr.write("Reading homology information...\n")
homol_ivv_to_refseq = Homology_descr.HomologyDescr(
    rsc.HomolIVVRefSeq_cDNA_NF)

refseqprot_swisspfam = RefSeqProt_SwissPfam(
    rsc.HomolRefSeqProtSprot100,
    rsc.SwissPfam_save)

sys.stderr.write("Reading self-homology information...\n")
homol_prey_self = Prey_intra_homol()
homol_prey_self.load_shelve(rsc.IntraPreyHomol)

# ivv_conv = IVV_Conv(ivv_info, mode = "S")
ivv_pred = PPIPred(ivv_info, mode = "S")

sys.stderr.write("Reading ID Conversion files...\n")
ivv_pred.set_mapping(homol_ivv_to_refseq,
                     rsc.Gene2RefSeq,
                     bait_hm, prey_hm)


sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_pred.set_reprod_thres(1)
ivv_pred.ivv_to_convid()

print
print

while True:

    geneid1 = raw_input("Input Gene ID 1: ")
    geneid2 = raw_input("Input Gene ID 2: ")
    print
    print "******* IVV Source Tracker *******"

    source = ivv_pred.gene_to_ivv_common_bait_descr(geneid1,
                                                    geneid2)


    print
    print "All preys for Bait:%s ---> Prey:%s" % (geneid1, geneid2)

    all_BP_preys = source.Bait_Prey_preys()
    print "All BP preys:", string.join(all_BP_preys, ",")

    if all_BP_preys != []:
        prey_redu_level = Prey_redund_level(all_BP_preys,
                                            homol_prey_self).redund_level()
    else:
        prey_redu_level = "-"
    print "Prey redundant level:", prey_redu_level
    print


    ref_mf = MultiFasta.MultiFasta(rsc.RefSeq_Prot_Human)
    ivv_mf = MultiFasta.MultiFasta(rsc.IVVSeq)

    refseqs_redund = geneid_to_refseq.val_force(geneid2)
    refseqids = Usefuls.ListProc1.NonRedList(refseqs_redund)

    for refseqid in refseqids:
        print "---", refseqid, "---"

        motifdescr =  refseqprot_swisspfam.ret_motif_info(refseqid)

        Align_MultiPair.Align_MultiFasta_motif(ref_mf, ivv_mf,
                                               refseqid, all_BP_preys,
                                               motifdescr)
        print


    print "Bait:%s ---> Prey:%s" % (geneid1, geneid2)
    for src in source.Bait_Prey():

        bait = src.get_bait()
        print "Bait:", bait
        motifs_bait = motif_info.get_motif(bait, bait_motif_thres)
        print "Bait motifs:", motifs_bait

        prey_set = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                      src.get_preys())
        for prey in prey_set.get_Preys():
            print prey.preyID(), prey.geneid(), prey.motifs(motif_info, prey_motif_thres),
            print "PullDown:", prey.pulldown(pulldown),
            print "ORF:", prey.qual_force("orf"),
            print "Mock:", prey.qual_force("mock"),
            print "Filt:", prey.dict_check(bfilt_dict)
        print

    print "Prey:%s <--- Bait:%s" % (geneid1, geneid2)
    for src in source.Prey_Bait():

        bait = src.get_bait()
        print "Bait:", bait
        motifs_bait = motif_info.get_motif(bait, bait_motif_thres)
        print "Bait motifs:", motifs_bait

        prey_set = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                      src.get_preys())
        for prey in prey_set.get_Preys():
            print prey.preyID(), prey.motifs(motif_info, prey_motif_thres)
        print

    print
    print "Prey:%s ---- Prey:%s" % (geneid1, geneid2)
    for src in source.Prey_Prey():
        bait = src.get_bait()
        print "Bait:", bait

        prey_set1 = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                       src.get_preys1())
        prey_set2 = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                       src.get_preys2())

        print "Prey %s:" % geneid1
        for prey in prey_set1.get_Preys():
            print prey.preyID(), prey.motifs(motif_info, prey_motif_thres)
        print "Prey %s:" % geneid2
        for prey in prey_set2.get_Preys():
            print prey.preyID(), prey.motifs(motif_info, prey_motif_thres)
        print

    print
    print "******* ------------------ *******"
    print
