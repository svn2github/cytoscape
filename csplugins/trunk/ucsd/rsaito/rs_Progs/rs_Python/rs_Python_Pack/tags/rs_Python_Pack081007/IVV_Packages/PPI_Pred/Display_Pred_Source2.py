#!/usr/bin/env python

import sys
import string
import os

from Usefuls.DictProc1 import file_to_dict_simple
from Usefuls.ListProc1 import NonRedList
import Seq_Packages.Seq.MultiFasta2 as MultiFasta
import Seq_Packages.Seq.Align_MultiPair as Align_MultiPair

from IVV_Packages.IVV_Homology.Prey_intra_homol1 \
    import Prey_intra_homol, Prey_redund_level

import IVV_Packages.IVV_Info.Prey_info1 as Prey_info

import IVV_Packages.Integration.IVV_Global_Center as IVV_Global
import IVV_Packages.Integration.IVV_Global_Center_II as IVV_Global_II

from Usefuls.rsConfig import RSC_II

rsc = RSC_II("rsIVV_Config")

bait_motif_thres = 1.0e-3
prey_motif_thres = 1.0e-3

reported_ppi         = IVV_Global.get_reported_ppi()
ivv_info             = IVV_Global.get_ivv_info()
bfilt_dict           = file_to_dict_simple(rsc.PreyFilter)
pulldown             = IVV_Global.get_pulldown()
motif_info           = IVV_Global.get_motif_info()
iPfam                = IVV_Global.get_iPfam()
geneid_to_refseq     = IVV_Global.get_geneid_to_refseq()
homol_ivv_to_refseq  = IVV_Global.get_homol_ivv_to_refseq()
homol_prey_self      = IVV_Global.get_homol_prey_self()

refseqprot_swisspfam = IVV_Global.get_refseqprot_swisspfam()



def motif_out(motifs):
    if motifs:
        return "[" + ",".join(motifs) + "]"
    else:
        return "[]"

def Display_Pred_Source(geneid1, geneid2):

    ivv_pred = IVV_Global_II.get_ppi_pred()
    
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
    refseqids = NonRedList(refseqs_redund)

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


def Return_Pred_Source(geneid1, geneid2):

    ivv_pred = IVV_Global_II.get_ppi_pred()

    ret = []
    ret.append("")
    ret.append("******* IVV Prediction Source Tracer *******")

    source = ivv_pred.gene_to_ivv_common_bait_descr(geneid1,
                                                    geneid2)

    ret.append("")
    ret.append("*** Description for Bait:%s ---> Prey:%s ***" % (geneid1, geneid2))

    all_BP_preys = source.Bait_Prey_preys()
    ret.append("All preys: " + string.join(all_BP_preys, ","))

    if all_BP_preys != []:
        prey_redu_level = Prey_redund_level(all_BP_preys,
                                            homol_prey_self).redund_level()
    else:
        prey_redu_level = "-"
    ret.append("Prey redundant level: " + `prey_redu_level`)
    ret.append("")

    ref_mf = MultiFasta.MultiFasta(rsc.RefSeq_Prot_Human)
    ivv_mf = MultiFasta.MultiFasta(rsc.IVVSeq)

    refseqs_redund = geneid_to_refseq.val_force(geneid2)
    refseqids = NonRedList(refseqs_redund)

    for refseqid in refseqids:
        ret.append("--- Refseq sequence " + refseqid + " for gene ID %s ---" % geneid2)

        motifdescr =  refseqprot_swisspfam.ret_motif_info(refseqid)

        ret.append(Align_MultiPair.
                   ret_Align_MultiFasta_motif(ref_mf, ivv_mf,
                                              refseqid, all_BP_preys,
                                              motifdescr))
        # ret.append("")


    ret.append("*** Sequence information for Bait:%s ---> Prey:%s ***" % (geneid1, geneid2))
    for src in source.Bait_Prey():

        bait = src.get_bait()
        ret.append("Bait: " + bait)
        motifs_bait = motif_info.get_motif(bait, bait_motif_thres)
        ret.append("Bait motifs: " + ",".join(motifs_bait))

        ret.append("Prey motifs:")
        prey_set = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                      src.get_preys())
        for prey in prey_set.get_Preys():
            ret.append(" ".join((prey.preyID(),
                                 prey.geneid(),
                                 motif_out(prey.motifs(motif_info,
                                                       prey_motif_thres)),
                                 "PullDown:", `prey.pulldown(pulldown)`,
                                 "ORF:", `prey.qual_force("orf")`,
                                 "Mock:", `prey.qual_force("mock")`,
                                 "Filt:", `prey.dict_check(bfilt_dict)`)))
        ret.append("")

    ret.append("*** Sequence information for Prey:%s <--- Bait:%s ***" % (geneid1, geneid2))
    for src in source.Prey_Bait():

        bait = src.get_bait()
        ret.append("Bait: " + bait)
        motifs_bait = motif_info.get_motif(bait, bait_motif_thres)
        ret.append("Bait motifs: " + ",".join(motifs_bait))

        ret.append("Prey motifs:")
        prey_set = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                      src.get_preys())
        for prey in prey_set.get_Preys():
            ret.append(prey.preyID() + " " +
                       motif_out(prey.motifs(motif_info,
                                             prey_motif_thres)))
        ret.append("")

    ret.append("")
    ret.append("*** Sequence information for Prey:%s ---- Prey:%s ***" % (geneid1, geneid2))
    for src in source.Prey_Prey():
        bait = src.get_bait()
        ret.append("Bait: " + bait)

        prey_set1 = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                       src.get_preys1())
        prey_set2 = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                       src.get_preys2())

        ret.append("Prey %s:" % geneid1)
        for prey in prey_set1.get_Preys():
            ret.append(prey.preyID() + " " +
                       motif_out(prey.motifs(motif_info,
                                             prey_motif_thres)))

        ret.append("Prey %s:" % geneid2)
        for prey in prey_set2.get_Preys():
            ret.append(prey.preyID() + " " +
                       motif_out(prey.motifs(motif_info,
                                             prey_motif_thres)))

        ret.append("")

    ret.append("")
    ret.append("******* ------------------ *******")
    ret.append("")
    return "\n".join(ret)


def Save_Pred_Source(geneid1, geneid2, logdir = None):

    if logdir is None:
        logdir = rsc.Pred_Source_logdir

    res = Return_Pred_Source(geneid1, geneid2)

    save_dir = logdir + "/" + geneid1
    save_file = geneid1 + "-" + geneid2
    save_path = save_dir + "/" + save_file

    os.system("mkdir -p " + save_dir)
    fw = open(save_path, "w")
    fw.write(res)
    fw.close()

if __name__ == "__main__":

    print dir()
    print
    while True:
        geneid1 = raw_input("Input Gene ID 1: ")
        geneid2 = raw_input("Input Gene ID 2: ")
        # Display_Pred_Source(geneid1, geneid2)
        print Return_Pred_Source(geneid1, geneid2)
        # Save_Pred_Source(geneid1, geneid2)
