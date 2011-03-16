#!/usr/bin/env python

from Kinome_Packages.Data_Info.Modified_Seq1 import Modified_Seq_Set
from General_Packages.Data_Struct.MultiDimDict1 import MultiDimDict
from Seq_Packages.Seq.Surround_Seqs1 import Surround_Seqs
from Seq_Packages.Seq.SingleSeq2 import SingleSeq

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsKinome_Config")

uprange = 10
dnrange = 10

sfs = Modified_Seq_Set()
sfs.read_kinome_file1(rsc.Plasmo_rat_eryth_noninf, rsc.IPI_MOUSE_RAT_DB)

seq_frag_h = sfs.get_seq_fragment_info(uprange, dnrange).get_all_data()

outseqs = {}

for modif_type in seq_frag_h:
    for mdseq in seq_frag_h[modif_type]:
        outseqs[ modif_type + "-" + mdseq ] = Surround_Seqs(uprange)

seq_count = 0
for modif_type in seq_frag_h:
    for mdseq in seq_frag_h[modif_type]:
        for seq in seq_frag_h[modif_type][mdseq]:
            seqinst = SingleSeq(seq, wash=False)
            seqid = "ID" + `seq_count`
            source_mod_seq_str = []
            for source_mod_seq in seq_frag_h[modif_type][mdseq][seq]:
                source_mod_seq_str.append(`sfs.
                                          get_all_mod_seq_inst().
                                          index(source_mod_seq)` + "-" +
                                          source_mod_seq.__repr__())

            seqinst.set_ID("ID" + `seq_count` + " " +
                           "; ".join(source_mod_seq_str))
            outseqs[ modif_type + "-" + mdseq ].add_seq(seqinst)
            seq_count += 1

for seqsetname in outseqs:
    fh = open("Kino_" + seqsetname, "w")
    fh.write(outseqs[ seqsetname ].ret_display1())
    fh.close()
