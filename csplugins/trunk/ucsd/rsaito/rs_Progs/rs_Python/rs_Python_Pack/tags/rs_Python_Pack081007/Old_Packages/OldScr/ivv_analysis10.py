#!/usr/bin/env python

import sys
sys.path.append("../")
import string

import Data_Struct.Hash
from Homology.Homology import Homology
from IVV_info.Prey_info_ext1 import Prey_info_ext1
from IVV_info.Bait_info_ext1 import Bait_info_ext1
from Homology.Interolog import Interolog
from IVV_info.IVV_info import IVV_info
from IVV_info.IVV_Conv import IVV_Conv
from IVV_info.IVV_seq import IVV_seq
from IVV_info.Check_Region import Check_Source_Region
import Usefuls.Usefuls1
import Usefuls.Table_maker

from Motif.Motif_info1 import Motif_info
from Motif.Motif_Sprot_Pfam_info \
     import Motif_Sprot_Pfam_info_Prey, Motif_Sprot_Pfam_info_Bait
from Motif.SwissPfam import Motif_swiss_set
from Homology.Homology1_descr import Homology1_descr
from Integ_class.RefSeq_based_map2 import RefSeq_based_map2
from Motif.MMI1 import MMI1

Motif_O_Thres = 5

ivv_info_file = "../..//IVV/ivv_human7.3_info"
homology_file = "../../Homology/homol_ivv_human7.3.tfa-human.protein.faa_simp_res"

reported_ppi_file = "../../PPI_public/ppi_ncbi.txt"
wanted_prot_file  = "../../PPI_public/wanted_list3.txt"
refseq2gene_file  = "../../Gene_info/gene2refseq_hs_mm"

""" Motif related """

motif_file = "../../Motifs/Pfam_ivv_human7.3_motif_info"
swiss_pfam_file = "../../Motifs/swisspfam_save"
ivv_to_refseq_file = "../../IVV/ivv_human7.3_refseq_match"
bait_to_swiss_file = "../../Homology/homol_ivv_human7.3_Bait_Sprot_simp_res_1st"
refseq_to_sprot_file = "../../Homology/homol_ivv_human7.3_refseq_uniprot_sprot_simp_res95"
iPfam_file = "../../Motifs/MMI_iPfam"

""" Motif related (END) """

sys.stderr.write("IVV information...\n")
ivv_info = IVV_info(ivv_info_file)

sys.stderr.write("Reading homology information...\n")
homology = Homology(homology_file)

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Data_Struct.Hash.Hash_filt("S")
reported_ppi.read_file(filename = reported_ppi_file,
                       Key_cols = [0,1],
                       Val_cols = [2])

sys.stderr.write("Reading wanted proteins...\n")
wanted_prot = Data_Struct.Hash.Hash_filt("A")
wanted_prot.read_file(filename = wanted_prot_file,
                      Key_cols = [0],
                      Val_cols = [1])


sys.stderr.write("Reading gene info...\n")
refseq2gene = Usefuls.Hash.Hash_filt("S")
refseq2gene.read_file(filename = refseq2gene_file,
                      Key_cols = [6],
                      Val_cols = [1])

sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_gene = IVV_Conv(ivv_info)
ivv_gene.ivv_to_convid()


""" Motif related settings """
sys.stderr.write("Reading Motif information...\n")

motif_info = Motif_info(motif_file)

swiss_pfam_info = Motif_swiss_set()
swiss_pfam_info.load_motif_info(swiss_pfam_file)

bait_to_swiss = Homology1_descr()
bait_to_swiss.read_homol_file(bait_to_swiss_file)

refseq_based_map = RefSeq_based_map2(ivv_info,
                                     ivv_to_refseq_file,
                                     refseq_to_sprot_file)

motif_sprot_pfam_info_bait = Motif_Sprot_Pfam_info_Bait(
    ivv_info, swiss_pfam_info, bait_to_swiss)

motif_sprot_pfam_info_prey = Motif_Sprot_Pfam_info_Prey(
    ivv_info, swiss_pfam_info, refseq_based_map)

iPfam = MMI1(iPfam_file)

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
    if not ppi_ncbi: ppi_ncbi = ""
    output.append("PubMed", ppi_ncbi)

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
    output.append("Interolog Pubmed", pubmed_itr)

    i1 = itr.get_best()[0]
    output.append("Homolog 1", i1)

    i2 = itr.get_best()[1]
    output.append("Homolog 2", i2)

    eval1 = itr.get_best_eval()[0]
    output.append("Eval 1", `eval1`)

    eval2 = itr.get_best_eval()[1]
    output.append("Eval 2", `eval2`)

    output.output("\t")
