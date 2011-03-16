#!/usr/bin/env python

import string

from General_Packages.Data_Struct.Hash2 import Hash
import General_Packages.Data_Struct.NonRedSet1 as NonRedSet

class Motif_info:
    def __init__(self, motif_file):
        motif = Hash("A")
        motif.set_filt([0, "[ Motif ]"])
        motif.read_file(filename = motif_file,
                        Key_cols = [1],
                        Val_cols = [2,3])

        mmi = Hash("N")
        mmi.set_filt([0, "[ MMI ]"])
        mmi.read_file(filename = motif_file,
                      Key_cols = [1,2],
                      Val_cols = [])

        self.motif = motif
        self.mmi = mmi

        self.motif2seqid = False

    def get_motif(self, seqid, thres):
        motifs_ret = []
        if self.motif.has_key(seqid):
            motifs = self.motif.val(seqid);
            for m in motifs:
                [ mid, eval ] = m.split("\t")
                if string.atof(eval) <= thres:
                    motifs_ret.append(mid)
        return motifs_ret

    def get_seqid_from_motif(self, motif, thres):

        seqids_ret = NonRedSet.NonRedSet()

        if self.motif2seqid == False:
            self.motif2seqid = NonRedSet.NonRedSetDict()
            for seqid in self.motif.keys():
                motifs = self.motif.val(seqid);
                for m in motifs:
                    [ mid, eval ] = m.split("\t")
                    self.motif2seqid.append_Dict(mid,
                                                 string.
                                                 join([ seqid,eval ], "\t"))

        seqids = self.motif2seqid.ret_set_Dict(motif)
        for seqinfo in seqids:
            seqid, eval = seqinfo.split("\t")
            if string.atof(eval) <= thres:
                seqids_ret.append(seqid)

        return seqids_ret.ret_set()

    def get_mm_pair(self, seqid1, seqid2, thres, sep, both=False):

        motifs1 = self.get_motif(seqid1, thres)
        motifs2 = self.get_motif(seqid2, thres)
        return self.get_mm_pair_from_motifs(motifs1, motifs2, sep, both)

    def get_mm_pair_from_motifs(self, motifs1, motifs2, sep, both=False):

        mmi = {}
        for m1 in motifs1:
            for m2 in motifs2:
                mmi[ m1 + sep + m2 ] = ""
                if both:
                    mmi[ m2 + sep + m1 ] = ""
        return mmi.keys()


    def get_mmi(self, seqid1, seqid2, thres, sep, both=False):
        """ Returns known motif-motif pairs """

        motifs1 = self.get_motif(seqid1, thres)
        motifs2 = self.get_motif(seqid2, thres)
        return self.get_mmi_from_motifs(motifs1, motifs2, sep, both)

    def get_mmi_from_motifs(self, motifs1, motifs2, sep, both=False):

        mmi = {}
        for m1 in motifs1:
            for m2 in motifs2:
                if self.mmi.has_pair(m1, m2):
                    mmi[ m1 + sep + m2 ] = ""
                    if both:
                        mmi[ m2 + sep + m1 ] = ""
        return mmi.keys()

    def mmi_has_pair(self, m1, m2):
	return self.mmi.has_pair(m1, m2)


if __name__ == "__main__":
    motif_file = "../../Motifs/Pfam_ivv_human7.3_motif_info"
    motif_info = Motif_info(motif_file)
    print motif_info.get_seqid_from_motif("Bradykinin", 1.0)
    print motif_info.get_motif("S20051122_E02_01_G02.seq", 0.1)
    print motif_info.get_mmi("2353_all",
                             "3725_all",
                             0.1, "\t")
    print motif_info.get_mmi("S20051122_E02_01_G02.seq",
                             "T050511_G01_C10.seq",
                             0.1, "\t")
