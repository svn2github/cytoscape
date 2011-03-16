#!/usr/bin/env python

from Bio import SeqIO

handle = open("K00650.1.gbk")

for seq_record in SeqIO.parse(handle, "genbank") :
    print seq_record.id, seq_record.annotations["organism"]
    for feature in seq_record.features:
        print feature.type, feature.location_operator, feature.location.start, feature.location.end
        if feature.location_operator == "join":
            for sub_feature in feature.sub_features:
                print "Sub-feature", sub_feature.location.start, sub_feature.location.end
handle.close()
