#!/usr/bin/env python

from Data_Struct.Hash2 import Hash

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
fastadb = RSC_II("FastaDB")

import SAT_Packages.SAT_Cluster.SAT_Probe1 as SAT_Probe1
import SAT_Packages.SAT_Cluster.SAT_Transcr1 as SAT_Transcr1
import SAT_Packages.SAT_Cluster.SAT_Clust1 as SAT_Clust1

import SAT_Packages.SAT11K.Human_Cancer11k5 as Cancer11k
from SAT_Packages.SAT11K.Human_Cancer11k_Global import colon_normal_keys, colon_cancer_keys

from Seq_Packages.Map.Map_Info1 import Map_Infos
import Seq_Packages.Homology.Homology_descr4 as Homol
import Seq_Packages.Homology.read_BLAT_psl3 as BLAT


from Seq_Packages.Seq.MultiFasta2 import MultiFasta
from Seq_Packages.Seq.SingleSeq2 import SingleFasta

from Usefuls.String_I import joiner
from Data_Struct.Hash2 import Hash

import os
import sys

import Usefuls.Table_maker
from Usefuls.DirPath import sdp

from SAT_Packages.Integration.SAT_Global_Center import strand_s, strand_o
AFAS_symb = "AFAS-"

def cluster_id2dir(cluster_id):
    """ Returns appropriate directory to save cluster-specific
    files according to cluster ID """
    
    dirpath = sdp("/".join((rsc.CsLabViewerDataOutDir,
                            "divide",
                            cluster_id[:-2],
                            cluster_id)))
    return dirpath
                            

def read_Cancer11k_data():
    """ Reads Human Cancer 11k data into
    global variables Colon_dT and Colon_Rd """
    
    sys.stderr.write("Reading expression data ...\n")
    
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
  

def read_transcr_info():
    
    sys.stderr.write("Reading transcript info ...\n")
    
    map_infos = Map_Infos()
    blatres = BLAT.BLAT_psl(rsc.Human11k_Cancer_ONC_Map_hg17_BLAT_all, bestonly = True)
    map_infos.incorp_BLAT(blatres)

    for id in map_infos:
        map_info = map_infos[id]
        transcript_id = id.split("|")[1]
        transcript = SAT_Transcr1.Transcript_Factory().make(transcript_id)
        afas_transcript = \
            SAT_Transcr1.Transcript_Factory().make(AFAS_symb + transcript_id)
        
        poss = []
        for map in map_info.get_map():
            transcript.set_chromosome(map.get_chr()[3:], map.get_strand())
            start = min(map.s_start(), map.s_end())
            end   = max(map.s_start(), map.s_end())
            poss.append((start, end))
        transcript.set_genomic_map_pos(poss)
        

def read_probe_info():
    """ This should be called after calling
    read_transcr_info """
    
    map_infos = Map_Infos()
    blatres = BLAT.BLAT_psl(rsc.Human11k_Cancer_ONC_Map_hg17_BLAT_all, bestonly = True)
    map_infos.incorp_BLAT(blatres)
        
    homol_probe_seq = Homol.HomologyDescr(rsc.Human11k_Cancer_ONC_AFAS_probes_homol)
    homol_probe_seq.enable_reverse()
    
    for subject_id in homol_probe_seq.subjects():
        probe_ids = homol_probe_seq.reverse_query_ID(subject_id)
        probe_map = {}
        for probe_id in probe_ids:
            try:
                probe = SAT_Probe1.Probe_Factory().make(probe_id)
                if probe_id.startswith(AFAS_symb):
                    transcript_id = AFAS_symb + subject_id
                else:
                    transcript_id = subject_id
                sense_transcript = SAT_Transcr1.Transcript_Factory().make(subject_id)
                transcript = SAT_Transcr1.Transcript_Factory().make(transcript_id)
                transcript.add_probe(probe)
                probe.set_transcript(transcript)
    
                if probe_id.startswith(AFAS_symb):
                    probe_strand = strand_o[ sense_transcript.get_chromosome()[1] ]
                else:
                    probe_strand = strand_s[ sense_transcript.get_chromosome()[1] ]
                probe.set_chromosome(sense_transcript.get_chromosome()[0], probe_strand)
    
                start = homol_probe_seq.subject_start(probe_id, subject_id)
                end   = homol_probe_seq.subject_end(probe_id, subject_id)
                probe_map[ probe_id ] = (start, end)
    
                genomic_probe_map =  map_infos.map_q_segms_to_subj("lcl|" + subject_id, probe_map)
                # print "-->", map_infos["lcl|" + subject_id].get_map(), probe_map, subject_id, genomic_probe_map
                for probe_id in genomic_probe_map:
                    probe.set_genomic_map_pos(genomic_probe_map[ probe_id ])

            except KeyError, msg:
                open(rsc.CsLabViewerDataOutError, "a").write("Mapping for %s not found: %s\n" % (subject_id, msg.message))


def make_cluster():
    """ This should be called after calling 
    read_probe_info() and read_transcr_info() """
       
    sys.stderr.write("Forming cluster ...\n")
       
    for id in SAT_Transcr1.Transcript_Factory():
        try:
            if id.startswith(AFAS_symb):
                transcr_id = id[ len(AFAS_symb): ]
            else:
                transcr_id = id
                
            # The cluster name is same as the transcript ID
            if transcr_id in SAT_Transcr1.Transcript_Factory():
                transcript = SAT_Transcr1.Transcript_Factory()[ transcr_id ]
                chrom, strand = transcript.get_chromosome()
                start, end = transcript.get_start_end()
                cluster = SAT_Clust1.Cluster_Factory().make(transcr_id)
                cluster.add_transcript(SAT_Transcr1.Transcript_Factory()[id])
                cluster.def_representative(transcr_id)
                cluster.set_chromosome(chrom)
                cluster.set_start(start)
                cluster.set_end(end)
                # print transcr_id, chrom, strand, start, end, map(lambda x:x.get_id(), transcript.get_probes())
            
        except AttributeError:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((transcr_id, "make cluster" + '\n')))

def make_cluster_II():
    """ Form clusters.
    This should be called after calling 
    read_probe_info() and read_transcr_info() """
    
    conv_onc_afas = Hash("A")
    conv_onc_afas.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                               Key_cols_hd = [ "Gene ID (ONC)" ],
                               Val_cols_hd = [ "Gene ID (AFAS)" ])
    for oncid in conv_onc_afas:
        transcr_id = oncid[4:]
        if transcr_id in SAT_Transcr1.Transcript_Factory():
            transcript = SAT_Transcr1.Transcript_Factory()[ transcr_id ]
            chrom, strand = transcript.get_chromosome()
            start, end = transcript.get_start_end()
            cluster = SAT_Clust1.Cluster_Factory().make(transcr_id)
            cluster.add_transcript(SAT_Transcr1.Transcript_Factory()[transcr_id])
            cluster.def_representative(transcr_id)
            cluster.set_chromosome(chrom)
            cluster.set_start(start)
            cluster.set_end(end)
            afas_transcr_id = AFAS_symb + transcr_id
            if afas_transcr_id in SAT_Transcr1.Transcript_Factory():
                cluster.add_transcript(SAT_Transcr1.Transcript_Factory()[ afas_transcr_id ])
            else:
                open(rsc.CsLabViewerDataOutError, "a").write("Information for " + afas_transcr_id + " (AFAS) not found.\n")
        else:
            open(rsc.CsLabViewerDataOutError, "a").write("Information for " + transcr_id + " (Sense) not found.\n")


def check_info():    

    print "Cluster Information:"
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster = SAT_Clust1.Cluster_Factory()[ cluster_id ]
        print joiner((cluster_id,
                      cluster.get_chromosome(),
                      cluster.get_start(),
                      cluster.get_end(),
                      cluster.get_representative(),
                      ",".join(map(lambda x: x.get_id(), cluster.get_transcripts()))))
    print
    
    print "Transcript Information:"
    for id in SAT_Transcr1.Transcript_Factory():
        transcript = SAT_Transcr1.Transcript_Factory()[ id ]
        chr, strand = transcript.get_chromosome()
        print joiner((id,
                      chr,
                      strand,
                      ",".join(map(lambda x: x.get_id(), transcript.get_probes()))))
    
    
def viewer_data():
    
    global Colon_dT
    global Colon_Rd

    # os.system("rm -rf " + sdp(rsc.CsLabViewerDataOutDir + "/*"))

    sys.stderr.write("Preparing viewer data ...\n")

    conv_afas_onc = Hash("S")
    conv_afas_onc.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                               Key_cols_hd = [ "Gene ID (AFAS)" ],
                               Val_cols_hd = [ "Gene ID (ONC)" ])  
    conv_onc_afas = Hash("A")
    conv_onc_afas.read_file_hd(rsc.Human11k_Cancer_AFAS_ID_Conv,
                               Key_cols_hd = [ "Gene ID (ONC)" ],
                               Val_cols_hd = [ "Gene ID (AFAS)" ])  

    """ *** pairID_info.txt *** """
    fw = open(sdp(rsc.CsLabViewerDataOutDir + "/" + "pairID_info.txt"), "w")
    for cluster_id in SAT_Clust1.Cluster_Factory():
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            fw.write(joiner((cluster_id,
                             cluster.get_start(),
                             cluster.get_end(),
                             cluster.get_chromosome())) + "\n")
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "pairID_info.txt", msg.message + '\n')))
    fw.close()
    
    """ *** id_info.txt *** """
    fw = open(sdp(rsc.CsLabViewerDataOutDir + "/" + "id_info.txt"), "w")
    for cluster_id in SAT_Clust1.Cluster_Factory():
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                fw.write(joiner((transcript.get_id(),
                                 transcript.get_id(),
                                 cluster_id,
                                 cluster.get_chromosome())) + "\n")
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "id_info.txt", msg.message + '\n')))
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
                    if (probe.get_id() not in conv_afas_onc and 
                        probe.get_id() not in conv_onc_afas):
                        continue
                    chr, strand = probe.get_chromosome()
                    for pos in probe.get_genomic_map_pos():
                        start, end = pos
                        fw.write(joiner((probe.get_id(),
                                         transcript.get_id(),
                                         cluster.get_chromosome(),
                                         "p",
                                         strand,
                                         start,
                                         end)) 
                                         + '\n')
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id,
                                                                    probe.get_id(),
                                                                    "probe_map.txt",
                                                                    msg.message + '\n')))    
        fw.close()
    
    """ *** transcript_map.txt *** """
    for cluster_id in SAT_Clust1.Cluster_Factory():
        cluster_path = cluster_id2dir(cluster_id)
        fw = open(sdp(cluster_path + "/" + "transcript_map.txt"), "w")
        try:
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            for transcript in cluster.get_transcripts():
                chr, strand = transcript.get_chromosome()
                for map_pos in transcript.get_genomic_map_pos():
                    start, end = map_pos
                    fw.write(joiner((transcript.get_id(),
                                     cluster_id,
                                     "h",
                                     "rep",
                                     chr,
                                     strand,
                                     start,
                                     end))
                                     + '\n')

        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "transcript_map.txt", msg.message + '\n')))    
        fw.close()
        
    #####
    
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
                    if (probe.get_id() not in conv_afas_onc and 
                        probe.get_id() not in conv_onc_afas):
                        continue
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_normal_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id,
                                                                    probe.get_id(),
                                                                    "Normal_dT.txt", msg.message + '\n')))     

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
                    if (probe.get_id() not in conv_afas_onc and 
                        probe.get_id() not in conv_onc_afas):
                        continue
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_normal_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id,
                                                                    probe.get_id(),
                                                                    "Normal_Rd.txt", msg.message + '\n')))    

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
                    if (probe.get_id() not in conv_afas_onc and 
                        probe.get_id() not in conv_onc_afas):
                        continue
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_cancer_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id,
                                                                    probe.get_id(),
                                                                    "Cancer_dT.txt", msg.message + '\n')))     

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
                    if (probe.get_id() not in conv_afas_onc and 
                        probe.get_id() not in conv_onc_afas):
                        continue
                    chr, strand = probe.get_chromosome()
                    output.append("probe_id",  probe.get_id())
                    output.append("transcript_id", transcript.get_id())
                    for tissue in colon_cancer_keys:
                        output.append(tissue,
                                      "%.1f" % (okay_sheet.get_datum(probe.get_id(),
                                                                     tissue)))
                    output.output("\t")
                    
        except AttributeError, msg:
            open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, 
                                                                    probe.get_id(),
                                                                    "Cancer_Rd.txt", msg.message + '\n')))    

    """ *** genomeSeq.txt *** """
    for cluster_id in SAT_Clust1.Cluster_Factory():
        try:
            cluster_path = cluster_id2dir(cluster_id)
            cluster = SAT_Clust1.Cluster_Factory()[cluster_id]
            chrom = cluster.get_chromosome()
            if not chrom:
                open(rsc.CsLabViewerDataOutError, "a").write("\t".join((cluster_id, "GenomeSeq", cluster_id + '\n')))
                continue
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
    

open(rsc.CsLabViewerDataOutError, "w") # Empties old file if exists.
read_transcr_info()
read_probe_info()
make_cluster_II()
# check_info()
read_Cancer11k_data()
viewer_data() 
sys.stderr.write("Everything finished.\n")   