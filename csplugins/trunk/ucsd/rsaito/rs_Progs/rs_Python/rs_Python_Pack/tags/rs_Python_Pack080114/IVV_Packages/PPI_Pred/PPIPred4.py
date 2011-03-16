#!/usr/bin/env python

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

import sys
from Usefuls.Instance_check import instance_class_check
from IVV_Packages.IVV_Info.IVV_Conv import IVV_Conv
from IVV_Packages.IVV_Info.IVV_filter1 import IVV_filter
import Seq_Packages.Homology.Homology_descr4 as Homology_descr
from Seq_Packages.Homology.Homol_measure import HM
import Data_Struct.Hash
import Data_Struct.NonRedSet1

class PPIPred(IVV_Conv):
    """ This class relates specific type of ID pairs (Here, referred to
    as "Converted ID pairs" to IVV pairs and vice versa """

    """ The followging 4 methods can be overridden so that
    the inherited class can perform predictions of PPIs according
    to interologs """

    def bait2convid(self, bait_ID):
        """ bait ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """
        refseq_hits = self.ivv_to_refseq. \
                      subject_ID_hm_thres(bait_ID, self.bait_hm)

        ret_geneids = Data_Struct.NonRedSet1.NonRedSet()
        # print "Bait ID -> RefSeq IDs", bait_ID, refseq_hits

        for refseq_hit in refseq_hits:
            geneids = self.refseq_to_geneid.val_force(refseq_hit)
            ret_geneids.append_list(geneids)

        # if ret_geneids.ret_set():
        #    print "Bait ID -> Gene IDs :", bait_ID, ret_geneids.ret_set()
        return ret_geneids.ret_set()

    def prey2convid(self, prey_ID):
        """ prey ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """

        refseq_hits = self.ivv_to_refseq. \
                      subject_ID_hm_thres(prey_ID, self.prey_hm)

        ret_geneids = Data_Struct.NonRedSet1.NonRedSet()

        for refseq_hit in refseq_hits:
            geneids = self.refseq_to_geneid.val_force(refseq_hit)
            ret_geneids.append_list(geneids)

        # if ret_geneids.ret_set():
        #     print "Prey ID -> Gene IDs :", prey_ID, ret_geneids.ret_set()
        return ret_geneids.ret_set()

    def convid2baits(self, convid):
        """ gene ---> set of Bait IDs
        Return [] if no corresponding IDs.
        """

        refseqs = Data_Struct.NonRedSet1.NonRedList(
            self.geneid_to_refseq.val_force(convid))
        ret_baitids = {}

        # print "Gene ID --> RefSeqs", convid, refseqs
        for refseq in refseqs:
            bait_and_preyid_hits = self.ivv_to_refseq.reverse_query_ID(refseq)
            # print "RefSeq --> IVV hits:", refseq, bait_and_preyid_hits
            for ivv_hit in bait_and_preyid_hits:
                if (self.ivv_info.ID_Type(ivv_hit) == "Bait" and
                    self.bait_hm.eval(self.ivv_to_refseq.hm(ivv_hit, refseq))):
                    ret_baitids[ ivv_hit ] = ""
                    # print ivv_hit, refseq, "OK"

        # if ret_baitids.keys():
        #    print "Gene ID --> Bait IDs", convid, refseqs, bait_and_preyid_hits, ret_baitids.keys()
        return ret_baitids.keys()


    def convid2preys(self, convid):
        """ gene ---> set of Prey IDs
        Return [] if no corresponding IDs.
        """

        refseqs = Data_Struct.NonRedSet1.NonRedList(
            self.geneid_to_refseq.val_force(convid))
        ret_preyids = {}


        # print "Gene ID --> RefSeqs (P):", convid, refseqs
        for refseq in refseqs:
            bait_and_preyid_hits = self.ivv_to_refseq.reverse_query_ID(refseq)
            # print "RefSeq --> IVV hits (P):", refseq, bait_and_preyid_hits
            for ivv_hit in bait_and_preyid_hits:
                if (self.ivv_info.ID_Type(ivv_hit) == "Prey" and
                    self.prey_hm.eval(self.ivv_to_refseq.hm(ivv_hit, refseq))):
                    ret_preyids[ ivv_hit ] = ""
                    # print ivv_hit, refseq, "OK"

        # if ret_preyids.keys():
        #     print "Gene ID --> prey IDs (P)", convid, ret_preyids.keys()
        #     print convid, refseqs, bait_and_preyid_hits, ret_preyids.keys()

        return ret_preyids.keys()


    """ ----------------------------------------------- """


    def set_mapping(self,
                    ivv_to_refseq_homol,
                    refseq_to_geneid_file,
                    bait_hm, prey_hm,
                    taxonid = "9606"):

        instance_class_check(ivv_to_refseq_homol,
                             Homology_descr.HomologyDescr)
        instance_class_check(bait_hm, HM)
        instance_class_check(prey_hm, HM)

        self.ivv_to_refseq = ivv_to_refseq_homol
        self.ivv_to_refseq.enable_reverse()

        self.refseq_to_geneid = Data_Struct.Hash.Hash_filt("A")
        self.refseq_to_geneid.set_filt([0, taxonid])
        self.refseq_to_geneid.read_file(filename = refseq_to_geneid_file,
                                        Key_cols = [6],
                                        Val_cols = [1])

        self.geneid_to_refseq = Data_Struct.Hash.Hash_filt("A")
        self.geneid_to_refseq.set_filt([0, taxonid])
        self.geneid_to_refseq.read_file(filename = refseq_to_geneid_file,
                                       Key_cols = [1],
                                       Val_cols = [6])

        self.bait_hm = bait_hm
        self.prey_hm = prey_hm


def test():
    import string
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC("../../../rsIVV_Config")

    ivv_info_file = rsc.IVVInfo
    ivv_prey_filter = rsc.PreyFilter

    ivv_to_refseq_homol_file = "HomologyDescr_test2" # rsc.HomolIVVRefSeq
    refseq_to_geneid_file = rsc.Gene2RefSeq

    filter = IVV_filter()
    filter.set_Prey_filter_file(ivv_prey_filter)

    sys.stderr.write("Reading IVV information...\n")
    ivv_info = IVV_Info.IVV_info.IVV_info(ivv_info_file) # , filter)

    sys.stderr.write("Reading homology information...\n")
    homology = Homology_descr.HomologyDescr(
        ivv_to_refseq_homol_file)

    sys.stderr.write("Reading ID Conversion files...\n")
    ivv_gene = PPIPred(ivv_info, mode = "S")
    ivv_gene.set_mapping(homology,
                         refseq_to_geneid_file,
                         HM(0.1, None, None, 300), HM(0.01, None, None, 200))

    sys.stderr.write("IVV -> Gene Calculation...\n")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_convid()

    """
    spoke = ivv_gene.get_spoke()
    for p1 in spoke:
        for p2 in spoke[p1]:
            print string.join([p1, p2, `spoke[p1][p2]`], "\t")
    """
    """
    matrix = ivv_gene.get_matrix()
    for p1 in matrix:
        for p2 in matrix[p1]:
            print "Matrix", p1, p2, matrix[p1][p2]
    """

    print "Search..."
    source = ivv_gene.gene_to_ivv_common_bait_descr('998', '6453')
    # source = ivv_gene.gene_to_ivv_common_bait_descr('6263', '7469')
    # source = ivv_gene.gene_to_ivv_common_bait_descr('7464', '51088')


    print "Common baits"
    print source.common_baits()

    print "Common bait count"
    print source.count_common_baits()

    print "Bait-Prey"
    for src in source.Bait_Prey():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()
    print

    print "Prey-Bait"
    for src in source.Prey_Bait():
        print "Bait:", src.get_bait()
        print "Prey:", src.get_preys()
    print

    print "Prey-Prey"
    for src in source.Prey_Prey():
        print "Bait  :", src.get_bait()
        print "Prey 1:", src.get_preys1()
        print "Prey 2:", src.get_preys2()
    print

    print "Bait-Prey-preys"
    print source.Bait_Prey_preys()
    print "Bait Prey quality"
    print source.get_quals_spoke("orf")
    print source.eval_quals_spoke("orf", "0")
    print "Prey-Prey quality"
    print source.get_quals_matrix("orf")
    print source.eval_quals_matrix("orf", "0")


if __name__ == "__main__":
    test()





