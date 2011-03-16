#!/usr/bin/env python

import string
import Data_Struct.NonRedSet1

class Probe_Info:
    def __init__(self, filename):
        fh = open(filename, "r")
        for line in fh:
            l = string.rstrip(line)
            if l[0] != "#":
                break
        self.header = l.split("\t")
        # print self.header

        self.probeID_to_RefSeqs = {}
        self.probeID_to_GeneIDs = {}
        self.GeneID_to_probeIDs = Data_Struct.NonRedSet1.NonRedSetDict()

        for line in fh:
            l = string.rstrip(line).split("\t")

            # Adds extra empty columns
            if len(l) < len(self.header):
                l += ("", ) * (len(self.header) - len(l))

            probeID = l[ self.header.index("ID") ]
            RefSeqs = (l[ self.header.index("RefSeq Transcript ID") ]
                       .split(" /// "))
            GeneIDs = l[ self.header.index("Entrez Gene") ].split(" /// ")

            if probeID.isspace() or probeID == "":
                continue

            self.probeID_to_RefSeqs[ probeID ] = RefSeqs

            for GeneID in GeneIDs:
                if not GeneID.isspace() and GeneID != "":
                    if not probeID in self.probeID_to_GeneIDs:
                        self.probeID_to_GeneIDs[ probeID ] = []
                    self.probeID_to_GeneIDs[ probeID ].append(GeneID)
                    self.GeneID_to_probeIDs.append_Dict(GeneID, probeID)

            #  print "--->", string.join([ probeID, RefSeq, GeneID ], "\t"), "<---"

        fh.close()

    def conv_probeID_to_RefSeqs(self, probeID):
        return self.probeID_to_RefSeqs[ probeID ]

    def conv_probeID_to_GeneIDs(self, probeID):
        if (probeID in self.probeID_to_GeneIDs and
            self.probeID_to_GeneIDs[ probeID ]):
            return self.probeID_to_GeneIDs[ probeID ]
        else:
            return []

    def conv_GeneID_to_probeIDs(self, geneID):
        if self.GeneID_to_probeIDs.has_key(geneID):
            return self.GeneID_to_probeIDs.ret_set_Dict(geneID)
        else:
            return []

    def get_probeIDs(self):
        return self.probeID_to_GeneIDs.keys()

    def get_genes(self):
        return self.GeneID_to_probeIDs.keys()

if __name__ == "__main__":

    import Usefuls.rsConfig
    testfiles = Usefuls.rsConfig.RSC_II("GEO_test")
    from GEO_exp1 import GEOexp_set

    pi = Probe_Info(testfiles.GPL96_14367)
    geo_exp_set = GEOexp_set()
    geo_exp_set.set_exp_file(testfiles.GSM28995, "EXP0")
    geo_exp_set.set_exp_file(testfiles.GSM28998, "EXP1")
    geo_exp_set.set_exp_file(testfiles.GSM29001, "EXP2")
    geo_exp_set.set_exp_file(testfiles.GSM29004, "EXP3")

    header = [ "Probe ID", "Genes" ] + geo_exp_set.conditions()
    print string.join(header, "\t")

    for probe in geo_exp_set.genes():
        genes = string.join(pi.conv_probeID_to_GeneIDs(probe), ";")
        expr = []
        for e_float in geo_exp_set.expression_pat(probe):
            if e_float:
                expr.append(`e_float`)
            else:
                expr.append("")

        print string.join([probe, genes ] + expr, "\t")

    """
    print pi.conv_GeneID_to_probeIDs("3725")
    print pi.get_probeIDs()
    refseq = pi.conv_probeID_to_RefSeqs("AFFX-HSAC07/X00351_M_at")
    GeneID = pi.conv_probeID_to_GeneIDs("AFFX-HSAC07/X00351_M_at")
    print refseq, GeneID
    """
