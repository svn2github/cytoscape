#!/usr/bin/python

import sys
import string

sys.path.append("./rsIVV_Python2")

import Usefuls.Hash
import Homology
import Motif
import Prey_info_ext1
import Bait_info_ext1
import Interolog
import IVV_info
import IVV_Gene
import Usefuls.Usefuls1
import Usefuls.Table

ivv_info_file = "/pub/IVV_data/IVV7.3/ivv_human7.3_info"
homology_file = "/pub/Warehouse/rsaito/PPI_IVV/Homology/homol_ivv_human7.3.tfa-human.protein.faa_simp_res"
motif_file = "/pub/Warehouse/rsaito/PPI_IVV/Motifs/Pfam_ivv_human7.3_motif_info"
reported_ppi_file = "/pub/Warehouse/rsaito/PPI_IVV/PPI_public/ppi_ncbi.txt"
wanted_prot_file = "/pub/Warehouse/rsaito/PPI_IVV/PPI_public/wanted_list3.txt"
refseq2gene_file = "/pub/Warehouse/rsaito/PPI_IVV/Gene_info/gene2refseq_hs_mm"

sys.stderr.write("IVV information...\n")
ivv_info = IVV_info.IVV_info(ivv_info_file)

sys.stderr.write("Reading homology information...\n")
homology = Homology.Homology(homology_file)

sys.stderr.write("Reading Motif information...\n")
motif_info = Motif.Motif_info(motif_file)

sys.stderr.write("Reading reported PPIs...\n")
reported_ppi = Usefuls.Hash.Hash_filt("S")
reported_ppi.read_file(filename = reported_ppi_file,
                       Key_cols = [0,1],
                       Val_cols = [2])

sys.stderr.write("Reading wanted proteins...\n")
wanted_prot = Usefuls.Hash.Hash_filt("A")
wanted_prot.read_file(filename = wanted_prot_file,
                      Key_cols = [0],
                      Val_cols = [1])


sys.stderr.write("Reading gene info...\n")
refseq2gene = Usefuls.Hash.Hash_filt("S")
refseq2gene.read_file(filename = refseq2gene_file,
                      Key_cols = [6],
                      Val_cols = [1])

sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_gene = IVV_Gene.IVV_Gene(ivv_info)
ivv_gene.ivv_to_gene()

gene_count = {}

first_line = True
for preyID in ivv_info.Prey_info().preys():
    output = Usefuls.Table.Table_row()
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
    else:
        combait = ivv_gene.gene_to_ivv_common_bait(prey_geneid,
                                                   bait_geneid)
    output.append("Detectable baits", string.join(combait, ","))

    count = 0
    for c in combait:
        if c <> "Initial_Initial" and c <> "Mock_Mock":
            count = count + 1
    output.append("Number of detectable baits", `count`)

    ppi_ncbi = reported_ppi.pair_val(bait_geneid, prey_geneid)
    if not ppi_ncbi: ppi_ncbi = ""
    output.append("PubMed", ppi_ncbi)

    motifs_prey = motif_info.get_motif(preyID, 1.0e-3)
    output.append("Motif(Prey)", string.join(motifs_prey, ","))

    motifs_bait = motif_info.get_motif(baitID, 1.0e-3)
    output.append("Motif(Bait)", string.join(motifs_bait, ","))

    mmi = motif_info.get_mmi(preyID, baitID, 1.0e-3, ":-:")
    output.append("MMI", string.join(mmi, ","))

    itr = Interolog.Interolog(p1 = preyID, p2 = baitID)
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

    
    if first_line:
        print string.join(output.return_header(), "\t")
    print string.join(output.return_row(), "\t")
    first_line = False

#    print
