#!/usr/bin/env python

import Usefuls.rsConfig
from Data_Struct.Hash2 import Hash
import SAT_Packages.SAT44K_Info.sas
import cPickle
import Usefuls.Table_maker

class Hash_without_version_k(Hash):
    def conv_key(self, k):
        return k.split(".")[0]

rsc_antis = Usefuls.rsConfig.RSC_II("rsSAT_Config")
rsc_gene  = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

accession2geneid = Hash_without_version_k("S")
accession2geneid.read_file(rsc_gene.gene2accession_hs,
                           Key_cols = [3],
                           Val_cols = [1])

refseq2geneid = Hash_without_version_k("S")
refseq2geneid.read_file(filename = rsc_gene.gene2refseq_hs,
                        Key_cols = [3],
                        Val_cols = [1])


def output_info(input_f, output_f):

    all_info = cPickle.load(open(input_f, "r"))
    
    output = Usefuls.Table_maker.Table_row(output_f)
    
    count = 0
    for sas in all_info.get_all_SAT():
        #if count == 0:
        #    print dir(sas)
        
        plus_isoforms  = sas.get_cluster('plus').get_all_isoforms()
        minus_isoforms = sas.get_cluster('minus').get_all_isoforms()
        plus_ids = map(lambda x: x.get_ID(), plus_isoforms)
        minus_ids = map(lambda x: x.get_ID(), minus_isoforms)

        plus_geneids  = {}
        for id in plus_ids:
            print "Checking", id, "on plus"
            if accession2geneid.has_key(id):
                plus_geneids[ accession2geneid[id] ] = ""
                print "accession", accession2geneid[id]
            if refseq2geneid.has_key(id):
                plus_geneids[ refseq2geneid[id] ] = ""
                print "refseq", refseq2geneid[id]

        minus_geneids = {}
        for id in minus_ids:
            print "Checking", id, "on minus"
            if accession2geneid.has_key(id):
                minus_geneids[ accession2geneid[id] ] = ""
                print "accession", accession2geneid[id]
            if refseq2geneid.has_key(id):
                minus_geneids[ refseq2geneid[id] ] = ""
                print "refseq", refseq2geneid[id]
        
        output.append("SAT ID", sas.get_ID())
        output.append("Chromosome", sas.get_chromosome())
    
        is_exon_overlapping = ""
        if sas.is_exon_overlapping() is True:
            is_exon_overlapping = "Y"
        output.append("Overlapping", is_exon_overlapping)   
        output.append("Overlapping length", `sas.get_overlapping_length()`)
    
        output.append("Start of cluster on plus strand", `sas.get_cluster('plus').start`)
        output.append("End of cluster on plus strand", `sas.get_cluster('plus').end`)              
        output.append("Start of cluster on minus strand", `sas.get_cluster('minus').start`)
        output.append("End of cluster on minus strand", `sas.get_cluster('minus').end`)  
    
        output.append("Representative isoform on plus strand", sas.get_cluster('plus').get_rep_ID())
        output.append("Representative isoform on minus strand", sas.get_cluster('minus').get_rep_ID())
    
        output.append("Isoforms on plus strand", ",".join(plus_ids))
        output.append("Isoforms on minus strand", ",".join(minus_ids))
        output.append("Gene IDs on plus strand", ",".join(plus_geneids.keys()))
        output.append("Gene IDs on minus strand", ",".join(minus_geneids.keys()))
                      
        
        output.output("\t")
        
        count += 1
        # if count >=10 : break


if __name__ == "__main__":

    output_info(rsc_antis.human44k_str, rsc_antis.human44k_simple_map)
    output_info(rsc_antis.mouse44k_str, rsc_antis.mouse44k_simple_map)

