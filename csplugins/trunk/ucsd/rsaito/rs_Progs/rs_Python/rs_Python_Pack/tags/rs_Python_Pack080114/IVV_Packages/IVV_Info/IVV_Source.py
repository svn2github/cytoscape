#!/usr/bin/env python

import sys
import IVV_info1
import Data_Struct.NonRedSet1
import Usefuls.Usefuls_I
import IVV_filter1

from Seq_Packages.Homology.Homology_descr4 import HomologyDescr
from Seq_Packages.Motif.MotifDescr1 import MotifDescr
from Seq_Packages.Homology.Interolog import Interolog, Interolog_Pack1

B = "B"
P = "P"

class IVV_Source:
    """ IVV source of interaction between specific type of ID pairs,
    namely convid1 and convid2

    A model for IVV_Source_set:

            convid1     convid2
    Bait1   id1 [B]     id2, id3
    Bait2   id4, id5    id6 [B]
    Bait3   id7, id8    id9, id10, id11

    Information of each row is stored in a instance of IVV_Source.
    id1 and id6 are bait proteins.
    """

    def __init__(self, convid1, convid2,
                 convid1_stype, convid2_stype,
		 bait, preys, preys_add = False):

	self.convid1 = convid1
	self.convid2 = convid2
	self.convid1_stype = convid1_stype
	self.convid2_stype = convid2_stype
	self.bait = bait
	self.preys = preys
	self.preys_add = preys_add

    def get_stype1(self):
	return self.convid1_stype

    def get_stype2(self):
	return self.convid2_stype

    def get_stype(self):
        return self.get_stype1() + self.get_stype2()

    def get_bait(self):
	return self.bait

    def get_preys(self):
	return self.preys

    def get_preys1(self):
        if self.convid1_stype == P and self.convid2_stype == P:
            return self.preys
        else:
            return False

    def get_preys2(self):
        if self.convid1_stype == P and self.convid2_stype == P:
            return self.preys_add
        else:
            return False

    def get_source1(self):
        if self.get_stype1() == B:
            return (self.get_bait(),)
        elif self.get_stype2() == B:
            return self.get_preys()
        else:
            return self.get_preys1()

    def get_source2(self):
        if self.get_stype2() == B:
            return (self.get_bait(),)
        elif self.get_stype1() == B:
            return self.get_preys()
        else:
            return self.get_preys2()

    def get_bait_prey_reprod(self):
        if self.get_stype1() == B or self.get_stype2() == B:
            return len(self.get_preys())
        else:
            return False

class IVV_Source_set:
    def __init__(self, convid1, convid2, ivv_info):

	self.gid1 = convid1
	self.gid2 = convid2
        self.ivv_info = ivv_info

	self.source_list = []

    def add_Bait_Prey(self, bait, preys):
	self.source_list.append(
            IVV_Source(self.convid1(), self.convid2(),
                       convid1_stype = B, convid2_stype = P,
                       bait = bait, preys = preys))

    def add_Prey_Bait(self, bait, preys):
	self.source_list.append(
            IVV_Source(self.convid1(), self.convid2(),
                       convid1_stype = P, convid2_stype = B,
                       bait = bait, preys = preys))

    def add_Prey_Prey(self, bait, preys1, preys2):
	self.source_list.append(
            IVV_Source(self.convid1(), self.convid2(),
                       convid1_stype = P, convid2_stype = P,
                       bait = bait,
                       preys = preys1,
                       preys_add = preys2))

    def convid1(self):
        return self.gid1

    def convid2(self):
        return self.gid2

    def ret_ivv_info(self):
        return self.ivv_info

    def ret_source_list(self):
        return self.source_list

    def common_baits(self):
        c_baits = Data_Struct.NonRedSet1.NonRedSet()
        for source in self.source_list:
            c_baits.append(source.get_bait())
        return c_baits.ret_set()

    def count_common_baits(self):
        c_baits = self.common_baits()
        count = 0
        for c_bait in c_baits:
            if self.ivv_info.Bait_info().bait_is_protein(c_bait):
                count += 1
        return count


    def common_baits_BP(self):
        c_baits = Data_Struct.NonRedSet1.NonRedSet()
        for source in self.Bait_Prey():
            c_baits.append(source.get_bait())
        return c_baits.ret_set()

    def count_common_baits_BP(self):
        c_baits = self.common_baits_BP()
        count = 0
        for c_bait in c_baits:
            if self.ivv_info.Bait_info().bait_is_protein(c_bait):
                count += 1
        return count

    def common_baits_PB(self):
        c_baits = Data_Struct.NonRedSet1.NonRedSet()
        for source in self.Prey_Bait():
            c_baits.append(source.get_bait())
        return c_baits.ret_set()

    def count_common_baits_PB(self):
        c_baits = self.common_baits_PB()
        count = 0
        for c_bait in c_baits:
            if self.ivv_info.Bait_info().bait_is_protein(c_bait):
                count += 1
        return count

    def Bait_Prey(self):

	ret = []
	for source in self.source_list:
            if (source.get_stype1() == B and
                source.get_stype2() == P):
		ret.append(source)
	return ret

    def Prey_Bait(self):
	ret = []
	for source in self.source_list:
            if (source.get_stype1() == P and
                source.get_stype2() == B):
		ret.append(source)
	return ret


    def Prey_Prey(self):
	ret = []
	for source in self.source_list:
            if (source.get_stype1() == P and
                source.get_stype2() == P):
		ret.append(source)
	return ret


    def Bait_Prey_preys(self):

        sources = self.Bait_Prey()
	ret = []
	for source in sources:
            ret += source.get_preys()
	return ret

    def get_quals_spoke(self, key):
	""" Returns a list of quality values for the given key
	in spoke PPIs """

        ivv_info = self.ret_ivv_info()
        sources = self.Bait_Prey() + self.Prey_Bait()

        qual_list = []

        for source in sources:
            preys = Data_Struct.NonRedSet1.NonRedList(source.get_preys())
            for prey in source.get_preys():
                qual_list.append(ivv_info.Prey_info().
                                 get_qual_noerror(prey, key))
        return qual_list

    def get_quals_matrix(self, key):
	""" Returns a list of quality values for the given key
	in matrix PPIs """

        ivv_info = self.ret_ivv_info()

        qual_list_list = []
        sources = self.Prey_Prey()
        for source in sources:
            preys1 = source.get_preys1()
            preys2 = source.get_preys2()
            bait = source.get_bait()
            qual_list1 = []
            qual_list2 = []
            for prey in preys1:
                qual_list1.append(ivv_info.Prey_info().
                                  get_qual_noerror(prey, key))
            for prey in preys2:
                qual_list2.append(ivv_info.Prey_info().
                                  get_qual_noerror(prey, key))
            qual_list_list.append((qual_list1, qual_list2, bait))

        return qual_list_list

    def eval_quals_spoke(self, key, val):
	""" Returns number of values which take "val", and
	number of values for the given "key" """

        ivv_info = self.ret_ivv_info()

        qlist = self.get_quals_spoke(key)
        return (Usefuls.Usefuls1.count_key_list(val, qlist),
                len(qlist))

    def eval_quals_matrix(self, key, val):

        ivv_info = self.ret_ivv_info()

        qlist_list = self.get_quals_matrix(key)
        count = 0
        total = 0
        for qlist in qlist_list:
            qlist1, qlist2, bait = qlist
            if self.ivv_info.Bait_info().bait_is_protein(bait):
                qlist1_hit = Usefuls.Usefuls1.count_key_list(val, qlist1)
                qlist2_hit = Usefuls.Usefuls1.count_key_list(val, qlist2)
                count += qlist1_hit * qlist2_hit
                total += len(qlist1) * len(qlist2)
        return (count, total)

    def get_interolog(self, homology, refseq2gene, reported_ppi,
                      mode = "S"):

        best_eval = 100.0
        best_itr = False

        spoke_set = self.Bait_Prey() + self.Prey_Bait()
        matrix_set = self.Prey_Prey()

        if mode == "A":
            source_list = (spoke_set, matrix_set)
        elif mode == "M":
            source_list = (matrix_set,)
        else:
            source_list = (spoke_set,)

	for source_set in source_list:
            for source in source_set:
                src1 = source.get_source1()
                src2 = source.get_source2()
                cbait = source.get_bait()
                if not self.ivv_info.Bait_info().bait_is_protein(cbait):
                    continue

                for s1 in src1:
                    for s2 in src2:
                        itr = Interolog_Pack1(s1, s2,
                                              homology,
                                              refseq2gene,
                                              reported_ppi)

                        if itr.get_interolog() == []: continue

                        eval1 = itr.get_best_eval()[0]
                        eval2 = itr.get_best_eval()[1]
                        if best_eval > eval1 * eval2:
                            best_eval = eval1 * eval2
                            best_itr = itr

            if best_itr: return best_itr

        return best_itr

    def get_motifs(self, motif_info, mode = "S"):

        thres = 0.001
        sep = ":-:"

        motif1_all = Data_Struct.NonRedSet1.NonRedSet()
        motif2_all = Data_Struct.NonRedSet1.NonRedSet()
        mmi_all = Data_Struct.NonRedSet1.NonRedSet()

        spoke_set = self.Bait_Prey() + self.Prey_Bait()
        matrix_set = self.Prey_Prey()

        if mode == "A":
            source_set = spoke_set + matrix_set
        elif mode == "M":
            source_set = matrix_set
        else:
            source_set = spoke_set

        for source in source_set:
            src1 = source.get_source1()
            src2 = source.get_source2()
            sbait = source.get_bait()

            if not self.ivv_info.Bait_info().bait_is_protein(sbait):
                continue

            for s1 in src1:
                motif_s1 = motif_info.get_motif(s1, thres)
                if motif_s1  == []: continue
                for m in motif_s1:motif1_all.append(m)

                for s2 in src2:
                    motif_s2 = motif_info.get_motif(s2, thres)
                    for m in motif_s2:motif2_all.append(m)

                    mmi = motif_info.get_mmi(s1, s2, thres, sep)
                    for mm in mmi: mmi_all.append(mm)

        return (motif1_all.ret_set(),
                motif2_all.ret_set(),
                mmi_all.ret_set())

if __name__ == "__main__":

    from Usefuls.rsConfig import RSC
    rsc = RSC("../../../rsIVV_Config")

    ivv_info_file = rsc.IVVInfo
    homology_file = rsc.HomolIVVRefSeq_cDNA_NF
    reported_ppi_file = rsc.KnownPPI_Hsap
    refseq2gene_file = rsc.Gene2RefSeq
    motif_file = rsc.MotifInfo

    filter = IVV_filter.IVV_filter1()
    filter.set_Bait_filter(("FOS", "JUN", "SMAD2"))


    sys.stderr.write("IVV information...\n")
    ivv_info = IVV_info.IVV_info(ivv_info_file, filter)

    sys.stderr.write("Reading homology information...\n")
    homology = HomologyDescr4(homology_file)

    sys.stderr.write("Reading Motif information...\n")
    motif_info = Motif_info(motif_file)

    sys.stderr.write("Reading reported PPIs...\n")
    reported_ppi = Data_Struct.Hash.Hash_headf("A")
    reported_ppi.read_file(filename = reported_ppi_file,
                       Key_cols_hd = [ "Gene ID 1", "Gene ID 2" ],
                       Val_cols_hd = [ "PubMed ID" ])

    sys.stderr.write("Reading gene info...\n")
    refseq2gene = Data_Struct.Hash.Hash_filt("S")
    refseq2gene.read_file(filename = refseq2gene_file,
                          Key_cols = [6],
                          Val_cols = [1])

    ivv_src_set = IVV_Source_set(2353, 3725, ivv_info)
    ivv_src_set.add_Bait_Prey("2353_349..633",
                              ["S050511_D1_5TH_C04.seq",
                               "S050511_D1_5TH_F06.seq",
                               "S050511_D1_5TH_F10.seq" ])
    ivv_src_set.add_Prey_Bait("3725_502..957",
                              ["D12_CSHB10-2-22C-JunD12hi1_08.seq",
                                "T050726_MJun4_2_C07.seq" ])
    ivv_src_set.add_Prey_Prey("4087_all",
                              ["S20060609_6TH_B6_04_D12.seq"],
                              ["S20051122_B06_03_G06.seq",
                               "T060407_D05_D12.seq"])

    print "PPI", ivv_src_set.convid1(), "---",  ivv_src_set.convid2()
    print "Common baits:", ivv_src_set.common_baits()
    print "Number of common protein baits: ", ivv_src_set.count_common_baits()

    print "Common baits (BP):", ivv_src_set.common_baits_BP()
    print "Number of common protein baits (BP): ", \
          ivv_src_set.count_common_baits_BP()

    print "Common baits (PB):", ivv_src_set.common_baits_PB()
    print "Number of common protein baits (PB): ", \
          ivv_src_set.count_common_baits_PB()

    print
    print "Bait - Prey source:"
    for src in ivv_src_set.Bait_Prey():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()
        print "Prey reprod:", src.get_bait_prey_reprod()

    print
    print "Prey - Bait source:"
    for src in ivv_src_set.Prey_Bait():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()
        print "Prey reprod:", src.get_bait_prey_reprod()


    print
    print "Prey - Prey source:"
    for src in ivv_src_set.Prey_Prey():
        print "Bait:", src.get_bait()
        print "Prey1:", src.get_preys1()
        print "Prey2:", src.get_preys2()
        print "Prey reprod:", src.get_bait_prey_reprod()


    print
    print "All source:"
    for src in ivv_src_set.ret_source_list():
        print "Type:", src.get_stype()
        print "Bait:", src.get_bait()
        print "Src1:", src.get_source1()
        print "Src2:", src.get_source2()
        print

    print "All preys for type B -> P"
    print ivv_src_set.Bait_Prey_preys()
    print

    itr = ivv_src_set.get_interolog(homology,
                                    refseq2gene,
                                    reported_ppi)
    print "Interolog calculation ..."
    print itr.get_best()
    print itr.get_best_ref()
    print itr.get_best_eval()
    print itr.get_best_conv()
    print

    print ivv_src_set.get_motifs(motif_info)
    print

    print "Quality information ..."
    print ivv_src_set.get_quals_spoke("orf")
    print ivv_src_set.eval_quals_spoke("orf", "0")
    print ivv_src_set.get_quals_matrix("orf")
    print ivv_src_set.eval_quals_matrix("orf", 0)



"""

    ivv_src_set2 = IVV_Source_set(3725, 2353, ivv_info)
    ivv_src_set2.add_Prey_Bait("2353_349..633",
                              ["S050519_m5_2_G09_m5_2_G9_068.seq",
                               "S050511_D1_5TH_C04.seq",
                               "S050511_D1_5TH_F06.seq",
                               "S050511_D1_5TH_F10.seq" ])
    ivv_src_set2.add_Bait_Prey("3725_502..957",
                              ["D12_CSHB10-2-22C-JunD12hi1_08.seq",
                                "T050726_MJun4_2_C07.seq" ])
    ivv_src_set2.add_Prey_Prey("4087_all",
                              ["S20051122_B06_03_G06.seq",
                               "T060407_D05_D12.seq"],
                              ["S20060609_6TH_B6_04_D12.seq"])

    print "PPI", ivv_src_set2.convid1(), "---",  ivv_src_set2.convid2()
    print "Common baits:", ivv_src_set2.common_baits()
    print "Number of common protein baits: ", ivv_src_set2.count_common_baits()

    print
    print "Bait - Prey source:"
    for src in ivv_src_set2.Bait_Prey():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()

    print
    print "Prey - Bait source:"
    for src in ivv_src_set2.Prey_Bait():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()

    print
    print "Prey - Prey source:"
    for src in ivv_src_set2.Prey_Prey():
        print "Bait:", src.get_bait()
        print "Prey1:", src.get_preys1()
        print "Prey2:", src.get_preys2()


    print
    print "All source:"
    for src in ivv_src_set2.ret_source_list():
        print "Type:", src.get_stype()
        print "Bait:", src.get_bait()
        print "Src1:", src.get_source1()
        print "Src2:", src.get_source2()
        print

    print "All preys for type B -> P"
    print ivv_src_set2.Bait_Prey_preys()
"""
