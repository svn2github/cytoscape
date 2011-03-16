#!/usr/bin/env python

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("GenBank")
genbank_file = rsc.BioPython_test1

from Bio import SeqIO
from Bio.SeqRecord import SeqRecord

handle = open(genbank_file)

for seq_record in SeqIO.parse(handle, "genbank") :
    for feature in seq_record.features:
        if feature.type == "gene":
            gene_name = "Undefined"
            if 'qualifiers' in vars(feature) and 'gene' in feature.qualifiers:
                gene_name = ",".join(feature.qualifiers['gene'])
            if feature.location_operator == "join":
                seq = Seq("")
                for sub_feature in feature.sub_features:
                    seq += seq_record.seq[sub_feature.location.start.position:sub_feature.location.end.position]
            else:
                seq = seq_record.seq[feature.location.start.position:feature.location.end.position]
                print SeqRecord(seq, id = gene_name, description = "").format("fasta")
handle.close()