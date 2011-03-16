#!/usr/bin/env python

import Modified_Seq1
from Seq_Packages.Seq.Surround_Seqs1 import Surround_Seqs
from Seq_Packages.Seq.SingleSeq2 import SingleSeq


class Surround_Modified:
    # Additional modification around the site not considered.
    def __init__(self, mtype, uprange, dnrange):
        self.uprange = uprange
        self.dnrange = dnrange
        self.mtype   = mtype

    def set_mod_seq_dict(self, idict):
        self.mod_seq_dict = idict

    def get_original_mod_seqs(self):
        return self.mod_seq_dict.keys()

    def get_hit_entries(self, original_mod_seq):
        return self.mod_seq_dict[ original_mod_seq ].keys()

    def get_hit_fragments(self, original_mod_seq,
                          hit_entry):
        return self.mod_seq_dict[ original_mod_seq ][ hit_entry ].keys()
    

class Surround_Modified_Set:
    def __init__(self, modified_seq_set, uprange, dnrange):
        
        self.h_dict = modified_seq_set.get_seq_fragment_info(
            uprange, dnrange).get_all_data()
        self.uprange = uprange
        self.dnrange = dnrange

    def get_mtype(self):
        return self.h_dict.keys()

    def get_mresidue(self, mtype):
        return self.h_dict[ mtype ].keys()

    def get_surround_seqs(self, mtype, mresidue):
        return self.h_dict[ mtype ][ mresidue ].keys()

    def get_surround_modified(self, mtype, mresidue, surround_seq):

        surround_modified = Surround_Modified(mtype,
                                              self.uprange,
                                              self.dnrange)
        surround_modified.set_mod_seq_dict(
            self.h_dict[ mtype ][ mresidue ][ surround_seq ]
            )
        
        return surround_modified


    def output_SeqLogo1(self, path):

        outseqs = {}

        for modif_type in self.h_dict:
            for mdseq in self.h_dict[modif_type]:
                outseqs[ modif_type + "-" + mdseq ] = \
                         Surround_Seqs(self.uprange)

        seq_count = 0
        for modif_type in self.h_dict:
            for mdseq in self.h_dict[modif_type]:
                for seq in self.h_dict[modif_type][mdseq]:
                    seqinst = SingleSeq(seq, wash=False)
                    seqid = "ID" + `seq_count`
                    source_mod_seq_str = []
                    for source_mod_seq in \
                            self.h_dict[modif_type][mdseq][seq]:
                        source_mod_seq_str.append(
                            `sfs.get_all_mod_seq_inst().
                            index(source_mod_seq)` + "-" +
                            source_mod_seq.__repr__())

                    seqinst.set_ID("ID" + `seq_count` + " " +
                                   "; ".join(source_mod_seq_str))
                    outseqs[ modif_type + "-" + mdseq ].add_seq(seqinst)
                    seq_count += 1

        for seqsetname in outseqs:
            fh = open(path + seqsetname, "w")
            fh.write(outseqs[ seqsetname ].ret_display1())
            fh.close()



if __name__ == "__main__":

    from General_Packages.Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsKinome_Config")
    
    sfs = Modified_Seq1.Modified_Seq_Set()
    sfs.read_kinome_file1(rsc.Plasmo_rat_eryth_test,
                          rsc.IPI_MOUSE_RAT_DB)

    sms = Surround_Modified_Set(sfs, 5, 10)

    """
    for mtype in sms.get_mtype():
        for mresidue in sms.get_mresidue(mtype):
            for sseq in sms.get_surround_seqs(mtype, mresidue):
                smod = sms.get_surround_modified(mtype, mresidue, sseq)

                for omod in smod.get_original_mod_seqs():
                    for hit in smod.get_hit_entries(omod):
                        for frag in smod.get_hit_fragments(omod, hit):
                            print mtype, mresidue, sseq, omod, hit, frag
                            """

    sms.output_SeqLogo1("Kino_")
