#!/usr/bin/env python

from Data_Struct.StrDict import StrDict
from Data_Struct.NonRedSet1 import NonRedSetDict

class SwissProt:
    def __init__(self, filename):
        self.filename = filename

    def set_keys(self, *lkeys):
        self.lkeys = lkeys

    def load(self):

        self.loaded = {}
        fh = open(self.filename, "r")

        hit = False

        for line in fh:
            hd = line[0:2]
            if hd == "//":
                if hit is False:
                    del self.loaded[ id ]
                hit = False

            if hd == "  ":
                hd = "SEQ"
            ds = line[5:]
            if hd == "ID":
                id = ds[:ds.index(" ")]
                self.loaded[id] = StrDict()
            if hd in self.lkeys:
                hit = True
                self.loaded[id].append_dict(hd, ds)

    def get_IDs(self):
        return self.loaded.keys()

    def get_DS(self, id, hd):
        if id in self.loaded:
            return self.loaded[ id ].get_elem(hd)
        else:
            return False

    def get_accession(self, id):
        if not "AC" in self.lkeys:
            raise r'Key "AC" not set'
        acc_str = self.get_DS(id, "AC").replace("\n", " ")
        acc_null = acc_str.split("; ")
        return acc_null[:-1]

    def accession_to_id(self):
        ret = NonRedSetDict()
        for id in self.get_IDs():
            for ac in self.get_accession(id):
                ret.append_Dict(ac, id)
        return ret


if __name__ == "__main__":
    import sys

    swissprot_file = sys.argv[1] # "../../../../../UniProt/uniprot_sprot_human.dat"
    sprt = SwissProt(swissprot_file)
    sprt.set_keys("AC", "DE", "SEQ")
    sprt.load()
    # for id in sprt.get_IDs():
    #   print id, sprt.get_accession(id)
    # ac_to_id = sprt.accession_to_id()
    # for ac in ac_to_id.keys():
    #    idset = ac_to_id.ret_set_Dict(ac)
    #    print ac, idset
    for id in sprt.get_IDs():
        ac = sprt.get_DS(id, "AC").replace("\n", " ")
        ds = sprt.get_DS(id, "DE").replace("\n", " ")
        sq = sprt.get_DS(id, "SEQ").replace(" ", "")
        print ">lcl|%s %s %s\n%s" % (id, ac, ds, sq)



