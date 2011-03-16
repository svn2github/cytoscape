#!/usr/bin/env python

from Data_Struct.Hash2 import Hash

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
fastadb = RSC_II("FastaDB")

import SAT_Probe1
import SAT_Transcr1
import SAT_Clust1

import SAT_Packages.SAT11K.Human_Cancer11k5 as Cancer11k
from SAT_Packages.SAT11K.Human_Cancer11k_Global import colon_normal_keys, colon_cancer_keys

from Seq_Packages.Seq.MultiFasta2 import MultiFasta
from Seq_Packages.Seq.SingleSeq2 import SingleFasta

import os

import Usefuls.Table_maker
from Usefuls.DirPath import sdp

def cluster_id2dir(cluster_id):
    dirpath = sdp("/".join((rsc.CsLabViewerDataOutDir,
                            "divide",
                            cluster_id[:-2],
                            cluster_id)))
    return dirpath
                            

def read_Cancer11k_data():
    
    global Colon_dT
    global Colon_Rd
    
    Colon_dT = Cancer11k.Human_Cancer11k()
    Colon_dT.set_related_files(rsc.Human11k_Cancer_Colon_dT,
                               rsc.Human11k_Cancer_Colon_gIsPosAndSignif_dT,
                               rsc.Human11k_Cancer_Colon_gIsWellAboveBG_dT,
                               rsc.Human11k_Cancer_AFAS_ID_Conv)
    Colon_dT.set_normal_keys(colon_normal_keys)
    Colon_dT.set_cancer_keys(colon_cancer_keys)
    
    Colon_Rd = Cancer11k.Human_Cancer11k()
    Colon_Rd.set_related_files(rsc.Human11k_Cancer_Colon_random,
                               rsc.Human11k_Cancer_Colon_gIsPosAndSignif_random,
                               rsc.Human11k_Cancer_Colon_gIsWellAboveBG_random,
                               rsc.Human11k_Cancer_AFAS_ID_Conv)
    Colon_Rd.set_normal_keys(colon_normal_keys)
    Colon_Rd.set_cancer_keys(colon_cancer_keys)

    
def read_probe_info():

    """ AFAS """
    probe_info = Hash("S")
    probe_info.read_file_hd(rsc.Human11k_Cancer_AFAS_probe_cond,
                            Key_cols_hd = ["AFAS ID"],
                            Val_cols_hd = ["Chromosome",
                                           "Strand",
                                           "Genomic Position"])
    
    for probeid in probe_info:
        probe = SAT_Probe1.Probe_Factory().make(probeid)
        probe.set_chromosome(probe_info.val_accord_hd(probeid, "Chromosome"),
                             probe_info.val_accord_hd(probeid, "Strand"))
        poss_strs = probe_info.val_accord_hd(probeid, "Genomic Position").split(",")
        poss = []   
        
        if poss_strs[0] == '.':
            continue

        while poss_strs:
            start = int(poss_strs.pop(0))
            end   = int(poss_strs.pop(0))      
            poss.append((start, end))
        probe.set_genomic_map_pos(poss)

        transcript_id = probeid.split("-")[3]
        transcript = SAT_Transcr1.Transcript_Factory().make(transcript_id)
        transcript.add_probe(probe)
        probe.set_transcript(transcript)

    """ Sense """
    probe_info = Hash("A")
    probe_info.set_miscolumn_permit()
    probe_info.read_file_hd(rsc.Human11k_Cancer_probe_info,
                            Key_cols_hd = ["ID"],
                            Val_cols_hd = ["Chromosome",
                                           "Strand",
                                           "Position"])

    for probeid in probe_info:
        probe = SAT_Probe1.Probe_Factory().make(probeid)
        chroms  = probe_info.val_accord_hd(probeid, "Chromosome")
        strands = probe_info.val_accord_hd(probeid, "Strand")
        gposss  = probe_info.val_accord_hd(probeid, "Position")
        for i in range(len(chroms)):
            chr    = chroms[i]
            strand = strands[i]
            gposs  = gposss[i]            
       
            if chr and strand and gposs:
                if strand == "plus":
                    strand = "+"
                elif strand == "minus":
                    strand = "-"
                probe.set_chromosome(chr, strand)
                poss_strs = gposs.split(",")               
                poss = []   
                while poss_strs:
                    start = int(poss_strs.pop(0))
                    end   = int(poss_strs.pop(0))      
                    poss.append((start, end))
                probe.set_genomic_map_pos(poss)

        transcript_id = probeid.split("-")[1]
        transcript = SAT_Transcr1.Transcript_Factory().make(transcript_id)
        transcript.add_probe(probe)
        probe.set_transcript(transcript)


def read_transcr_info():
    transcr_info = Hash("S")
    transcr_info.set_miscolumn_permit()
    transcr_info.read_file_hd(rsc.Human11k_Cancer_Map_Info,
                              Key_cols_hd = ["Transcript ID"],
                              Val_cols_hd = ["Chromosome",
                                             "Strand",
                                             "Position"])
    for transcr_id in transcr_info:
        chrom  = transcr_info.val_accord_hd(transcr_id, "Chromosome").replace("chr", "")
        strand = transcr_info.val_accord_hd(transcr_id, "Strand")
        gposs  = transcr_info.val_accord_hd(transcr_id, "Position")

        if chrom and strand and gposs:
            if strand == "plus":
                strand = "+"
            elif strand == "minus":
                strand = "-"
            transcript = SAT_Transcr1.Transcript_Factory().make(transcr_id)
            transcript.set_chromosome(chrom, strand)
            poss_strs = gposs.split(",")
            poss = []   
            while poss_strs:
                start = int(poss_strs.pop(0))
                end   = int(poss_strs.pop(0))      
                poss.append((start, end))
            transcript.set_genomic_map_pos(poss)          
    
def make_cluster():
    """ This should be called after calling 
    read_probe_info() and read_transcr_info() """
       
    for transcr_id in SAT_Transcr1.Transcript_Factory():
        try:
            cluster = SAT_Clust1.Cluster_Factory().make(transcr_id)
            # The cluster name is same as the transcript ID
            transcript = SAT_Transcr1.Transcript_Factory()[ transcr_id ]
            chrom, strand = transcript.get_chromosome()
            start, end = transcript.get_start_end()
            cluster.add_transcript(transcript)
            cluster.def_representative(transcr_id)
            cluster.set_chromosome(chrom)
            cluster.set_start(start)
            cluster.set_end(end)
            # print transcr_id, chrom, strand, start, end, map(lambda x:x.get_id(), transcript.get_probes())
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((transcr_id, "make cluster" + '\n')))
    
def viewer_data():
    
    global Colon_dT
    global Colon_Rd

    # os.system("rm -rf " + sdp(rsc.CsLabViewerDataOutDir + "/*"))

    """ *** pairID_info.txt *** """
    fw = open(sdp(rsc.CsLabViewerDataOutDir + "/" + "pairID_info.txt"), "w")
    for cluster_id in SAT_Clust1.Cluster_Factory():
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            fw.write("\t".join((cluster_id,
                                `cluster.get_start()`,
                                `cluster.get_end()`,
                                cluster.get_chromosome())) + "\n")
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "pairID_info.txt" + '\n')))
    fw.close()
    
    """ *** id_info.txt *** """
    fw = open(sdp(rsc.CsLabViewerDataOutDir + "/" + "id_info.txt"), "w")
    for cluster_id in SAT_Clust1.Cluster_Factory():
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                fw.write("\t".join((transcript.get_id(),
                                    transcript.get_id(),
                                    cluster_id,
                                    cluster.get_chromosome())) + "\n")
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "id_info.txt" + '\n')))
    fw.close()

    """ *** probe_map.txt *** """
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        os.system("mkdir -p %s" % cluster_path)
        fw = open(sdp(cluster_path + "/" + "probe_map.txt"), "w")
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                for probe in transcript.get_probes():
                    chr, strand = probe.get_chromosome()
                    fw.write("\t".join((probe.get_id(),
                                        transcript.get_id(),
                                        cluster.get_chromosome(),
                                        "p",
                                        strand) +
                                        tuple(probe.get_genomic_map_pos_squash())) 
                                        + '\n')
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "probe_map.txt" + '\n')))    
        fw.close()
    
    """ *** transcript_map.txt *** """
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        fw = open(sdp(cluster_path + "/" + "transcript_map.txt"), "w")
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                chr, strand = transcript.get_chromosome()
                fw.write("\t".join((transcript.get_id(),
                                    cluster_id,
                                    "h",
                                    "rep",
                                    chr,
                                    strand) +
                                    tuple(transcript.get_genomic_map_pos_squash()))
                                    + '\n')

        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "transcript_map.txt" + '\n')))    
        fw.close()
        
    #####
    
    conv_onc_afas = Hash("A")
    conv_onc_afas.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                               Key_cols_hd = [ "Gene ID (ONC)" ],
                               Val_cols_hd = [ "Gene ID (AFAS)" ])  

    """ *** Normal_dT.txt *** """
    okay_sheet = Cancer11k.Okay_Sheet(rsc.Human11k_Cancer_Colon_dT,
                                      conv_onc_afas)
    
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        dir_path  = sdp(cluster_path + "/" + "ex")
        file_path = sdp(dir_path + "/" + "Normal_dT.txt")
        os.system("mkdir -p %s" % dir_path)
        output = Usefuls.Table_maker.Table_row(file_path)
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                for probe in transcript.get_probes():
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_normal_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "Normal_dT.txt" + '\n')))     

    """ *** Normal_Rd.txt *** """
    okay_sheet = Cancer11k.Okay_Sheet(rsc.Human11k_Cancer_Colon_random,
                                      conv_onc_afas)
    output = Usefuls.Table_maker.Table_row()
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        dir_path  = sdp(cluster_path + "/" + "ex")
        file_path = sdp(dir_path + "/" + "Normal_Rd.txt")
        # os.system("mkdir -p %s" % dir_path)
        output = Usefuls.Table_maker.Table_row(file_path)
        
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                for probe in transcript.get_probes():
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_normal_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "Normal_Rd.txt" + '\n')))    

    """ *** Cancer_dT.txt *** """
    okay_sheet = Cancer11k.Okay_Sheet(rsc.Human11k_Cancer_Colon_dT,
                                      conv_onc_afas)

    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        dir_path  = sdp(cluster_path + "/" + "ex")
        file_path = sdp(dir_path + "/" + "Cancer_dT.txt")
        # os.system("mkdir -p %s" % dir_path)
        output = Usefuls.Table_maker.Table_row(file_path)
        
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                for probe in transcript.get_probes():
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_cancer_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "Cancer_dT.txt" + '\n')))     

    """ *** Cancer_Rd.txt *** """
    okay_sheet = Cancer11k.Okay_Sheet(rsc.Human11k_Cancer_Colon_random,
                                      conv_onc_afas)
    output = Usefuls.Table_maker.Table_row()
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        dir_path  = sdp(cluster_path + "/" + "ex")
        file_path = sdp(dir_path + "/" + "Cancer_Rd.txt")
        # os.system("mkdir -p %s" % dir_path)
        output = Usefuls.Table_maker.Table_row(file_path)
        
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                for probe in transcript.get_probes():
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_cancer_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "Cancer_Rd.txt" + '\n')))    

    """ *** genomeSeq.txt *** """
    for cluster_id in SAT_Clust1.Cluster_Factory():
        try:
            cluster_path = cluster_id2dir(cluster_id)
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            chrom = cluster.get_chromosome()
            chrom_symb = "chr" + chrom
            hgdb = fastadb.hg17DIR + "/" + chrom_symb + ".fa"
            start = cluster.get_start()
            end   = cluster.get_end()
            mf = MultiFasta(hgdb)
            sfasta = mf.get_singlefasta(chrom_symb, start, end)
            fw = open(sdp(cluster_path + "/" + "genomeSeq.txt"), "w")
            fw.write("\t".join((cluster_id,
                                chrom,
                                `start`,
                                `end`,
                                sfasta.get_singleseq().get_seq()
                               )))
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "GenomeSeq" + '\n')))    

open(rsc.CsLabViewerDataOutError, "w")
read_probe_info()
read_transcr_info()
make_cluster()
read_Cancer11k_data()
viewer_data()    