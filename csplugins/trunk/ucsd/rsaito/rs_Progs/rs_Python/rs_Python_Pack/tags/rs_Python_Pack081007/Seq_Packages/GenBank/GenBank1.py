#!/usr/bin/env python

import Header1
import Features1
import Origin1

class GenBank_single:
    def __init__(self, gbkfile):
        self.gbkfile = gbkfile
        self.fh = open(gbkfile, "r")

    def reader(self):
        self.header = Header1.Header()
        if self.header.reader(self.fh) is None:
            return None
        self.features = Features1.Features()
        if self.features.reader(self.fh) is None:
            return None
        self.origin = Origin1.Origin()
        if self.origin.reader(self.fh) is None:
            return None

        self.locus = self.header.locus
        self.accession = self.header.accession
        return self.header.locus

    def get_locus(self):
        return self.locus
    
    def get_accession(self):
        return self.accession

    def get_header(self):
        return self.header
    
    def get_features(self):
        return self.features
    
    def get_origin(self):
        return self.origin

    def __del__(self):
        self.fh.close()

if __name__ == "__main__":

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("GenBank")

    gbk = GenBank_single(rsc.Primates_test)
    while(gbk.reader()):
        print gbk.get_locus()
        print len(gbk.get_origin().get_seq()) 
        print gbk.get_accession()
        features = gbk.get_features()
        for cds in features.get_feature_set_by_key("CDS"):
            print cds.get_both_ends_simple(), cds.get_lines()[0]


