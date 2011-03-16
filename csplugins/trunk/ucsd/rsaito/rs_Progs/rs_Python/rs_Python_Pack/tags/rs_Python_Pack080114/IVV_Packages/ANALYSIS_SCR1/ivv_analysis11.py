#!/usr/bin/env python

import sys
import os

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

import string

import Data_Struct.Hash2
from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from IVV_Packages.IVV_Info.Prey_info_ext1 import Prey_info_ext
from IVV_Packages.IVV_Info.Bait_info_ext1 import Bait_info_ext
from Seq_Packages.Homology.Interolog import Interolog
from IVV_Packages.IVV_Info.IVV_info1 import IVV_info
from IVV_Packages.IVV_Info.IVV_Conv import IVV_Conv
from IVV_Packages.IVV_Info.IVV_seq import IVV_seq
from IVV_Packages.IVV_Info.Check_Region import Check_Source_Region
import Usefuls.Usefuls_I
import Usefuls.Table_maker
import Usefuls.Usefuls_dict1

from IVV_Packages.IVV_Motif.Motif_info1 import Motif_info
from IVV_Packages.IVV_Motif.Motif_Sprot_Pfam_info \
     import Motif_Sprot_Pfam_info_Prey, Motif_Sprot_Pfam_info_Bait
from Seq_Packages.Motif.SwissPfam import Motif_swiss_set
from IVV_Packages.IVV_Homology.RefSeq_based_map3 import RefSeq_based_map
from Seq_Packages.Motif.MMI2 import MMI

Motif_O_Thres = 5

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Data_Struct.Hash2.Hash("A")
reported_ppi.read_file_hd(filename = rsc.KnownPPI_Hsap,
                       Key_cols_hd = ["Gene ID 1", "Gene ID 2"],
                       Val_cols_hd = ["PubMed ID"])

sys.stderr.write("Reading wanted proteins...\n")
wanted_prot = Data_Struct.Hash.Hash_filt("A")
wanted_prot.read_file(filename = rsc.Wanted,
                      Key_cols = [0],
                      Val_cols = [1])


sys.stderr.write("Reading gene info...\n")
refseq2gene = Data_Struct.Hash.Hash_filt("S")
refseq2gene.read_file(filename = rsc.Gene2RefSeq,
                      Key_cols = [6],
                      Val_cols = [1])

sys.stderr.write("Reading IVV information...\n")
ivv_info = IVV_info(rsc.IVVInfo)
bfilt_dict = Usefuls.Usefuls_dict1.file_to_dict_simple(rsc.PreyFilter)

sys.stderr.write("Reading homology information...\n")
homology = HomologyDescr(rsc.HomolIVVRefSeq)

sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_gene = IVV_Conv(ivv_info)
ivv_gene.ivv_to_convid()


""" Motif related settings """
sys.stderr.write("Reading Motif information...\n")

motif_info = Motif_info(rsc.MotifInfo)

swiss_pfam_info = Motif_swiss_set()
swiss_pfam_info.load_motif_info(rsc.SwissPfam_save)

bait_to_swiss = HomologyDescr(rsc.HomolIVVBait_Sprot)

refseq_based_map = RefSeq_based_map(ivv_info,
                                    rsc.HomolIVVRefSeq_Ssearch,
                                    rsc.HomolIVVRefSeq_Sprot)

motif_sprot_pfam_info_bait = Motif_Sprot_Pfam_info_Bait(
    ivv_info, swiss_pfam_info, bait_to_swiss)

motif_sprot_pfam_info_prey = Motif_Sprot_Pfam_info_Prey(
    ivv_info, swiss_pfam_info, refseq_based_map)

iPfam = MMI(rsc.iPfam)

""" Motif related settings (END) """


gene_count = {}

output = Usefuls.Table_maker.Table_row()
for preyID in ivv_info.Prey_info().preys():

    output.append("Prey ID", preyID)

    prey_geneid = ivv_info.Prey_info().geneid(preyID)
    output.append("Prey geneid", prey_geneid)

    if prey_geneid == "": continue

    preySb = ivv_info.Prey_info().genesymbol(preyID)
    output.append("Prey Symbol", preySb)

    baitID = ivv_info.Prey_info().bait_ID(preyID)
    output.append("BaitID", baitID)

    if baitID == "Mock_Mock": continue
    if baitID == "Initial_Initial": continue

    bait_geneid = ivv_info.Bait_info().geneid(baitID)
    output.append("bait geneid", bait_geneid)

    baitSb = ivv_info.Prey_info().get_qual(preyID, "BaitSymbol")
    output.append("BaitSymBol", baitSb)

    if not baitID in gene_count:
        gene_count[ baitID ] = ivv_info.Prey_info().preys_gene_count(baitID)

    if baitID <> "" and prey_geneid <> "":
        reprod = `gene_count[ baitID ][ prey_geneid ]`
    else:
        reprod = ""
    output.append("Reproducibility", reprod)

    expno = ivv_info.Prey_info().expno(preyID)
    output.append("expno", expno)

    organism = ivv_info.Prey_info().get_qual(preyID, "gene_organism")
    output.append("organism", organism)

    hit_refseqid = ivv_info.Prey_info().get_qual(preyID, "hit_refseqid")
    output.append("hit_refseqid", hit_refseqid)

    ref_identities = ivv_info.Prey_info().get_qual(preyID, "ref_identities")
    output.append("ref_identities", ref_identities)

    hit_ref_position = ivv_info.Prey_info().get_qual(preyID, "hit_ref_position")
    output.append("hit_ref_position", hit_ref_position)

    hit_cds_position = ivv_info.Prey_info().get_qual(preyID, "hit_cds_position")
    output.append("hit_cds_position", hit_cds_position)

    orf = ivv_info.Prey_info().get_qual(preyID, "orf")
    output.append("orf", orf)

    strand = ivv_info.Prey_info().get_qual(preyID, "strand")
    output.append("strand", strand)

    mock = ivv_info.Prey_info().get_qual(preyID, "mock")
    output.append("mock", mock)

    bfilter = ""
    if preyID in bfilt_dict:
        bfilter = "1"
    output.append("Basic Filter", bfilter)

    if prey_geneid == "":
        combait = []
        combait_refined = []
        count = 0
        count_refined = 0

    else:
        source = ivv_gene.gene_to_ivv_common_bait_descr(prey_geneid,
                                                        bait_geneid)
        region_check = Check_Source_Region(source)
        source_refined = region_check.source_scan_region(
            IVV_seq(preyID, ivv_info),
            IVV_seq(baitID, ivv_info, idtype = "Bait"),
            15)
        combait = source.common_baits()
        combait_refined = source_refined.common_baits()
        count = source.count_common_baits()
        count_refined = source_refined.count_common_baits()

    output.append("Detectable baits", string.join(combait, ","))
    output.append("Number of detectable baits", `count`)

    output.append("Detectable baits (R)",
                  string.join(combait_refined, ","))
    output.append("Number of detectable baits (R)", `count_refined`)

    ppi_ncbi = reported_ppi.pair_val(bait_geneid, prey_geneid)
    if not ppi_ncbi: ppi_ncbi = []
    output.append("PubMed", string.join(ppi_ncbi, ","))

    motifs_prey = motif_info.get_motif(preyID, 1.0e-3)
    output.append("Motif (Prey)", string.join(motifs_prey, ","))

    motifs_bait = motif_info.get_motif(baitID, 1.0e-3)
    output.append("Motif (Bait)", string.join(motifs_bait, ","))

    mmi = motif_info.get_mmi(preyID, baitID, 1.0e-3, ":-:")
    output.append("MMI", string.join(mmi, ","))


    motifs_prey_R = motif_sprot_pfam_info_prey.motif_info(preyID,
							  Motif_O_Thres)
    output.append("Motif-R (Prey)", string.join(motifs_prey_R, ","))

    motifs_bait_R = motif_sprot_pfam_info_bait.motif_info(baitID,
							  Motif_O_Thres)
    output.append("Motif-R (Bait)", string.join(motifs_bait_R, ","))

    mmi_R = motif_info.get_mmi_from_motifs(motifs_prey_R,
					   motifs_bait_R,
					   ":-:")
    output.append("MMI-R", string.join(mmi_R, ","))

    mmi_R_iPfam = iPfam.get_mmi_from_motifs(motifs_prey_R,
					    motifs_bait_R,
					    ":-:")
    output.append("MMI-R-iPfam", string.join(mmi_R_iPfam, ","))

    itr = Interolog(p1 = preyID, p2 = baitID)
    itr.set_homology(homol1 = homology, homol2 = homology)
    itr.set_conv(conv1 = refseq2gene, conv2 = refseq2gene)
    itr.set_subj_ppi(reported_ppi)
    itr.calc_interolog()

    pubmed_itr = itr.get_best_ref()
    if not pubmed_itr:
        pubmed_itr = []
    output.append("Interolog Pubmed", string.join(pubmed_itr, ","))

    i1 = itr.get_best()[0]
    output.append("Homolog 1", i1)

    i2 = itr.get_best()[1]
    output.append("Homolog 2", i2)

    eval1 = itr.get_best_eval()[0]
    output.append("Eval 1", `eval1`)

    eval2 = itr.get_best_eval()[1]
    output.append("Eval 2", `eval2`)

    output.output("\t")
