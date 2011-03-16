#!/usr/bin/env python

import sys

from General_Packages.Usefuls.Instance_check import instance_class_check
from General_Packages.Data_Struct.NonRedSet1 import NonRedSetDict
from General_Packages.Data_Struct.MultiDimDict1 import MultiDimDict
from General_Packages.Usefuls.String_I import matcher
from Seq_Packages.Seq.SingleSeq2 import SingleSeq
from Seq_Packages.Seq.MultiFasta2 import MultiFasta
import Modification1 as M1


class Modified_Seq:
    def __init__(self, mod_seq):

        instance_class_check(mod_seq, SingleSeq)
        self.mod_seq = mod_seq
        self.modifications = []

    def add_modification(self, site, mtype):
        self.modifications.append(M1.Modification(
                self,
                site,
                mtype))

    def add_modification_obj(self, modification):
        self.modifications.append(modification)

    def set_modification_objs(self, modifications):
        self.modifications = modifications[:]

    def set_hit_entries(self, hit_entries):
        self.hit_entries = hit_entries

    def set_hit_seqdb(self, seqdb):
        self._seqdb = seqdb

    def get_mod_seq(self):
        return self.mod_seq

    def get_modifications(self):
        return self.modifications

    def get_hit_entries(self):
        return self.hit_entries

    def get_hit_entry_seqs(self):

        seqs = []
        mf = MultiFasta(self._seqdb)

        for entry in self.get_hit_entries():
            sfasta = mf.get_singlefasta(entry)
            if sfasta is None:
                sys.stderr.write(entry + " not found.\n")
            else:
                seq = sfasta.get_singleseq()
                seqs.append(seq)

        return seqs


    def map_mod_seq_to_hit_entries(self):
        """ Format of returned list:
        [[ mod_seq_hit1_pos1, mod_seq_hit1_pos2, ... ]
         [ mod_seq_hit2_pos1, mod_seq_hit2_pos2, ... ]
         :
         :
        ] """

        hit_modss = []

        for hit_seq in self.get_hit_entry_seqs():
            hit_positions = hit_seq.matcher(self.get_mod_seq())
            hit_mods = []
            for hit_position in hit_positions:
                hit_mod = Modified_Seq(hit_seq)

                for mod in self.get_modifications():
                    mpos = mod.get_site()
                    mtyp = mod.get_mtype()
                    hit_mod.add_modification(
                        mpos + hit_position,
                        mtyp)
                hit_mods.append(hit_mod)
            hit_modss.append(hit_mods)

        return hit_modss

    def __repr__(self):

        expseq = self.get_mod_seq().get_seq()
        mods = []
        for mod in self.get_modifications():
            mods.append(mod.get_mtype() + "@" + `mod.get_site()`)

        return expseq + " " + ",".join(mods)


class Modified_Seq_Set:

    def __init__(self):

        self.mod_seqs = []
        self.mod_seq_to_inst = NonRedSetDict()
        self.inst_to_mod_seq = NonRedSetDict()

    def add_mod_seq(self, mod_seq_inst,
                     modifications, hits):

        mod_seq_inst.set_modification_objs(modifications)
        mod_seq_inst.set_hit_entries(hits)

        self.mod_seqs.append(mod_seq_inst)
        mod_seq = mod_seq_inst.get_mod_seq()
        self.mod_seq_to_inst.append_Dict(mod_seq,
                                          mod_seq_inst)
        self.inst_to_mod_seq.append_Dict(mod_seq_inst,
                                          mod_seq)


    def read_kinome_file1(self, filename, hit_seqdb):

        fh = open(filename, "r")
        dummy = fh.readline()

        for line in fh:
            mod_seq, mod_info, hit_redund, hits = \
                      line.rstrip().split("\t")
            mod_seq = SingleSeq(mod_seq)
            mod_info = mod_info.split(",")
            hits = hits.split(",")

            # print mod_seq, mod_info, hit_redund, hits

            mod_seq_inst = Modified_Seq(mod_seq)
            mod_seq_inst.set_hit_seqdb(hit_seqdb)

            modifications = []
            for each_mod in mod_info:
                mtype, resid_site = each_mod.split("@")
                residue, site = resid_site.split(":")
                site = int(site) - 1
                if residue != mod_seq[site]:
                    raise "Residue Mismatch: " + \
                          residue + " in position " + `site` +\
                          " of " + mod_seq

                modifications.append(M1.Modification(mod_seq_inst,
                                                     site,
                                                     mtype))

            self.add_mod_seq(mod_seq_inst,
                             modifications,
                             hits)


    def get_all_mod_seq_inst(self):

        return self.mod_seqs
        # return self.inst_to_mod_seq.keys()

    def get_all_mod_seq(self):

        return self.mod_seq_to_inst.keys()

    def get_mod_seq_insts_from_mod_seq(self, mod_seq):

        return self.mod_seq_to_inst.ret_set_Dict(mod_seq)

    def get_seq_fragment_info(self, uprange, dnrange):
        """ Modification Type -> Modified Base ->
        Surrounding Sequence -> Original Modified Sequence ->
        Hit Entry -> Fragment Number """

        result1 = MultiDimDict(6, 0)

        mod_seq_count = 0
        for mod_seq_inst in self.get_all_mod_seq_inst():

            hit_entries = mod_seq_inst.get_hit_entries()
            hit_seqs = mod_seq_inst.get_hit_entry_seqs()
            hit_mod_seq_instss = mod_seq_inst.map_mod_seq_to_hit_entries()

            for i in range(len(hit_entries)):
                hit_entry = hit_entries[i]
                hit_seq   = hit_seqs[i]
                hit_mod_seq_insts = hit_mod_seq_instss[i]

                for j in range(len(hit_mod_seq_insts)):
                    hit_mod_seq_inst = hit_mod_seq_insts[j]

                    for modif in hit_mod_seq_inst.get_modifications():

                        upseq = hit_mod_seq_inst.get_mod_seq().\
                                get_seq_frag(modif.get_site() - uprange,
                                             modif.get_site() - 1)

                        mdseq = hit_mod_seq_inst.\
                                get_mod_seq()[ modif.get_site() ]

                        dnseq = hit_mod_seq_inst.get_mod_seq().\
                                get_seq_frag(modif.get_site() + 1,
                                             modif.get_site() + dnrange)

                        result1.plus_val((modif.get_mtype(),
                                          mdseq,
                                          upseq + mdseq + dnseq,
                                          mod_seq_inst,
                                          hit_entry,
                                          j), 1)

            mod_seq_count += 1
            # print "Registered", `mod_seq_count`

        return result1


if __name__ == "__main__":

    from General_Packages.Usefuls.rsConfig import RSC
    rsc = RSC("../../../rs_Python_Config/rsKinome_Config")

    sfs = Modified_Seq_Set()
    sfs.read_kinome_file1(rsc.Plasmo_rat_eryth_test, rsc.IPI_MOUSE_RAT_DB)

    for mod_seq_inst in sfs.get_all_mod_seq_inst():
        print mod_seq_inst

        hit_seqs = mod_seq_inst.get_hit_entry_seqs()
        hit_entries = mod_seq_inst.get_hit_entries()
        hit_mod_seq_instss = mod_seq_inst.map_mod_seq_to_hit_entries()

        print "*****", mod_seq_inst.get_mod_seq().get_seq(), "*****"

        for i in range(len(hit_entries)):
            hit_entry = hit_entries[i]
            hit_seq   = hit_seqs[i]
            hit_mod_seq_insts = hit_mod_seq_instss[i]

            print "--->", hit_entry

            hit_pos_count = 0
            for hit_mod_seq_inst in hit_mod_seq_insts:
                for modif in hit_mod_seq_inst.get_modifications():
                    print hit_pos_count, modif.get_mtype(), modif.get_site(),
                    print hit_mod_seq_inst.get_mod_seq().get_seq_frag(
                        modif.get_site() - 10,
                        modif.get_site() + 10)
                hit_pos_count += 1

        print

    print sfs.get_seq_fragment_info(5, 5).get_all_data()

    # for mod_seq in sfs.get_all_mod_seq():
    #     print mod_seq, sfs.get_mod_seq_insts_from_mod_seq(mod_seq)
