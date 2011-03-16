#!/usr/bin/env python

from General_Packages.Data_Struct.NonRedSet1 import NonRedSetDict

class MotifDescr:
    def __init__(self):

        self.protein_ID = False
        self.motif = NonRedSetDict()

    def set_Protein_ID(self, ID):

        self.protein_ID = ID

    def set_motif(self, motif, pos1, pos2):

        self.motif.append_Dict(motif, (pos1, pos2))

    def get_protein_ID(self):

        return self.protein_ID

    def get_motif(self):

        return self.motif.keys()

    def get_motif_pos(self, motif):

        return self.motif.ret_set_Dict(motif)

    def __str__(self):
        ret = "[%s] " % self.get_protein_ID()
        tmp = []
        for each_motif in self.get_motif():
            pos_str1 = map(lambda pos: "%d-%d" % (pos[0], pos[1]), self.get_motif_pos(each_motif))
            pos_str2 = ",".join(pos_str1)
            tmp.append("%s:%s" % (each_motif, pos_str2))

        return ret + "; ".join(tmp)

if __name__ == "__main__":

    motifdescr = MotifDescr()
    motifdescr.set_Protein_ID("ProtA")
    motifdescr.set_motif("Motif1", 100, 200)
    motifdescr.set_motif("Motif2", 300, 400)
    motifdescr.set_motif("Motif1", 500, 600)
    motifdescr.set_motif("Motif1", 300, 400)
    motifdescr.set_motif("Motif1", 100, 200)
    
    print motifdescr

    print motifdescr.get_motif()
    for each_motif in motifdescr.get_motif():
        print each_motif, motifdescr.get_motif_pos(each_motif)


