#!/usr/bin/env python

import sys
import string

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

import IVV_Packages.IVV_Info.Prey_info1 as Prey_info

import Seq_Packages.Homology.Homology_descr4 as Homology_descr
from IVV_Packages.IVV_Homology.Prey_intra_homol1 \
     import Prey_intra_homol, Prey_redund_level
from Seq_Packages.Homology.Homol_measure import HM

import Data_Struct.Hash2

import Usefuls.Table_maker
import Usefuls.Counter
import Usefuls.DictProc1
from Usefuls.ListProc1 import NonRedList

from Usefuls.rsConfig import RSC

import IVV_Packages.Integration.IVV_Global_Center as IVV_Global
import IVV_Packages.Integration.IVV_Global_Center_II as IVV_Global_II

import IVV_Packages.PPI_Pred.Display_Pred_Source2

bait_hm  = HM(1.0e-30)
prey_hm  = HM(1.0e-2, 0.7, 0, 10)
mode = "S"
rep_thres = 1

bait_motif_thres = 1.0e-3
prey_motif_thres = 1.0e-3

mmi_sep = ":-:"

reported_ppi    = IVV_Global.get_reported_ppi()
gene_info       = IVV_Global.get_gene_info()
wanted_genes    = IVV_Global.get_wanted_genes()
ivv_info        = IVV_Global.get_ivv_info(filter_mode = False)
pulldown        = IVV_Global.get_pulldown()
motif_info      = IVV_Global.get_motif_info()
iPfam           = IVV_Global.get_iPfam()
homol_prey_self = IVV_Global.get_homol_prey_self()

bfilt_dict = Usefuls.DictProc1.file_to_dict_simple(rsc.PreyFilter)

yclone = Data_Struct.Hash2.Hash("A")
yclone.read_file_hd(rsc.Yanag_clone_hs,
                    Key_cols_hd = ["Gene ID"],
                    Val_cols_hd = ["Source",
                                   "Accession No.",
                                   "Vector"])

ivv_pred = IVV_Global_II.calc_ppipred(bait_hm, prey_hm, mode, rep_thres)

tb = Usefuls.Table_maker.Table_row()

spoke = ivv_pred.get_spoke()
for p1 in spoke:
    for p2 in spoke[p1]:
        tb.append("Gene 1", p1)
        tb.append("Gene 2", p2)

        tb.append("Symbol 1", gene_info.val_force(p1).split("\t")[0])
        tb.append("Symbol 2", gene_info.val_force(p2).split("\t")[0])

        tb.append("Reprod BP", `spoke[p1][p2]`)
        tb.append("Reprod PB", "later")
        tb.append("Reprod PP", "later")

        tb.append("Annotation 1", gene_info.val_force(p1).split("\t")[1])
        tb.append("Annotation 2", gene_info.val_force(p2).split("\t")[1])

        pubmedid = ""
        if reported_ppi.has_pair(p1, p2):
            pubmedid = string.join(reported_ppi.pair_val(p1, p2), ",")
        tb.append("Literature", pubmedid)
        if pulldown.geneid2pd(p1, p2):
            pulldown_match = pulldown.geneid2pd(p1, p2)
        else:
            pulldown_match = ""
        tb.append("PullDown", pulldown_match)

        tb.append("PullDown Exact", "later")

        source = ivv_pred.gene_to_ivv_common_bait_descr(p1, p2)

        tb.append("Common baits",
                  string.join(source.common_baits(), ","))
        tb.append("Common bait count BP",
                  `source.count_common_baits_BP()`)
        tb.append("Common bait count PB",
                  `source.count_common_baits_PB()`)

        tb.append("Gene 2 wanted",
                  string.join(wanted_genes.val_force(p2), ","))

        ct_bp = Usefuls.Counter.Count2()
        ct_prey_motif = Usefuls.Counter.Count2()

        orf_bp_count = 0
        pulldown_exact = 0
        pulldown_count = 0
        mock_count = 0
        bfilt_count = 0

        geneid_match_bait = 0
        geneid_match_prey = 0
        geneid_match_bait_prey = 0

        all_BP_preys = source.Bait_Prey_preys()
        prey_redu_level = `Prey_redund_level(all_BP_preys,
                                             homol_prey_self).redund_level()`
        tb.append("Prey redund level", prey_redu_level)
        # No division by zero expected.

        for src in source.Bait_Prey():
            if src.get_bait_prey_reprod() < rep_thres:
                continue
            bait = src.get_bait()
            prey_set = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                          src.get_preys())

            bait_geneid = ivv_info.Bait_info().geneid(bait)
            prey_geneids = prey_set.get_info("geneid")
            prey_geneid_ct = prey_geneids.count(p2)

            if bait_geneid == p1:
                geneid_match_bait += 1
                prey_geneids = prey_set.get_info("geneid")
                geneid_match_bait_prey += prey_geneid_ct
            geneid_match_prey += prey_geneid_ct

            motifs_bait = motif_info.get_motif(bait, bait_motif_thres)
            motifs_prey = prey_set.get_info_squash("motifs", motif_info,
                                                   prey_motif_thres)
            mmis = iPfam.get_mmi_from_motifs(motifs_bait, motifs_prey,
                                             mmi_sep)
            ct_prey_motif.count_up_list(motifs_prey)
            ct_bp.count_up_list(mmis)

            mock = prey_set.get_info("qual_force", "mock")
            mock_count += mock.count("1")
            orf = prey_set.get_info("qual_force", "orf")
            orf_bp_count += orf.count("0")
            pulldown_res = prey_set.get_info("pulldown", pulldown)
            pulldown_count += pulldown_res.count("OK")

            if (pulldown_res.count("OK") > 0 and
                p1 == ivv_info.Bait_info().geneid(bait)):
                for prey in prey_set:
                    if (prey.pulldown(pulldown) == "OK" and
                        p2 == prey.geneid()):
                        pulldown_exact += 1

            bfilt = prey_set.get_info("dict_check", bfilt_dict)
            bfilt_count += bfilt.count(True)

        tb.append("Mock", `mock_count`)
        tb.append("PullDown Exact", `pulldown_exact`)
        tb.append("Prey PullDown Count", `pulldown_count`)
        tb.append("Bait geneid match", `geneid_match_bait`)
        tb.append("Prey geneid match", `geneid_match_prey`)
        tb.append("Bait and Prey geneid match", `geneid_match_bait_prey`)

        tb.append("orf=0", `orf_bp_count`)
        tb.append("Basic filter", `bfilt_count`)

        prey_motifs = string.join(ct_prey_motif.get_elems(), ",")
        tb.append("Prey motifs", prey_motifs)
        tb.append("Prey motif max", `ct_prey_motif.get_max_count()`)

        mmi_bp = string.join(ct_bp.get_elems(), ",")
        tb.append("BP MMIs", mmi_bp)

        count_pb_reprod = 0
        ct_pb = Usefuls.Counter.Count2()

        for src in source.Prey_Bait():

            bait = src.get_bait()
            motifs_bait = motif_info.get_motif(bait, bait_motif_thres)

            count_pb_reprod += len(src.get_preys())

            prey_set = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                          src.get_preys())

            motifs_prey = prey_set.get_info_squash("motifs", motif_info,
                                                   prey_motif_thres)
            mmis = iPfam.get_mmi_from_motifs(motifs_prey, motifs_bait,
                                             mmi_sep)

            for mmi in mmis:
                ct_pb.count_up(mmi)

            # print "Motifs prey:", motifs_prey, "Motifs bait", motifs_bait, "MMI:", mmis

        tb.append("Reprod PB", `count_pb_reprod`)
        mmi_pb = string.join(ct_pb.get_elems(), ",")
        tb.append("PB MMIs", mmi_pb)

        count_pp_reprod = 0
        ct_pp = Usefuls.Counter.Count2()

        for src in source.Prey_Prey():
            bait = src.get_bait()
            if not ivv_info.Bait_info().bait_is_protein(bait):
                continue
            prey_set1 = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                           src.get_preys1())
            prey_set2 = Prey_info.Prey_Set(ivv_info.Prey_info(),
                                           src.get_preys2())
            pset_pair = (
                Prey_info.Prey_Set_pair(prey_set1, prey_set2)
                )
            count_pp_reprod += pset_pair.num_hetero()
            mmis = pset_pair.all_mmi(iPfam, motif_info, prey_motif_thres,
                                     mmi_sep)
            for mmi in mmis:
                ct_pp.count_up(mmi)

        mmi_pp = string.join(ct_pp.get_elems(), ",")
        tb.append("PP MMIs", mmi_pp)
        tb.append("Reprod PP", `count_pp_reprod`)

        s_clone_geneid1 = yclone.val_accord_hd(key = p1,
                                               val_term = "Source")
        if s_clone_geneid1 is None:
            s_clone_geneid1 = []
        tb.append("Gene ID 1 source clone",
                  ",".join(NonRedList(s_clone_geneid1)))

        s_clone_geneid2 = yclone.val_accord_hd(key = p2,
                                               val_term = "Source")
        if s_clone_geneid2 is None:
            s_clone_geneid2 = []
        tb.append("Gene ID 2 source clone",
                  ",".join(NonRedList(s_clone_geneid2)))
        s_clone_access2 = yclone.val_accord_hd(key = p2,
                                               val_term = "Accession No.")
        if s_clone_access2 is None:
            s_clone_access2 = []
        tb.append("Gene ID 2 source clone accession",
                  ",".join(NonRedList(s_clone_access2)))

        tb.output("\t")

        IVV_Packages.PPI_Pred.Display_Pred_Source2.Save_Pred_Source(p1, p2)
