#!/usr/bin/env python

import sys

import IVV_info.IVV_Conv
import IVV_info.IVV_filter
import Homology.HomologyDescr2
import Data_Struct.Hash
import Data_Struct.NonRedSet
import Motif.Motif_info1

class PPIPredFromIVVMMI1(IVV_info.IVV_Conv.IVV_Conv):
    """ This class relates specific type of ID pairs (Here, referred to
    as "Converted ID pairs" to IVV pairs and vice versa """

    """ The followging 4 methods can be overridden so that
    the inherited class can perform predictions of PPIs according
    to interologs """

    def bait2convid(self, bait_ID):
        """ bait ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """

        motifs = self.motif_info.get_motif(bait_ID, self.eval)
        ret_geneids = Data_Struct.NonRedSet.NonRedSet()

        for motif in motifs:
            geneids = self.motif_to_geneid.val_force(motif)
            if geneids:
                ret_geneids.append_list(geneids)

        # print "BaitID", bait_ID, "Motifs:", motifs, "GeneIDs", ret_geneids.ret_set()
        return ret_geneids.ret_set()


    def prey2convid(self, prey_ID):
        """ prey ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """

        motifs = self.motif_info.get_motif(prey_ID, self.eval)
        ret_geneids = Data_Struct.NonRedSet.NonRedSet()

        for motif in motifs:
            geneids = self.motif_to_geneid.val_force(motif)
            if geneids:
                ret_geneids.append_list(geneids)

        # print "PreyID", prey_ID, "Motifs:", motifs, "GeneIDs", ret_geneids.ret_set()
        return ret_geneids.ret_set()


    def convid2baits(self, convid):
        """ gene ---> set of Bait IDs
        Return [] if no corresponding IDs.
        """

        motifs = self.geneid_to_motif.val_force(convid)
        if not motifs: return []

        ret_baitids = {}
        for motif in motifs:
            ivvids = self.motif_info.get_seqid_from_motif(motif,
                                                          self.eval)
            for ivvid in ivvids:
                if self.ivv_info.ID_Type(ivvid) == "Bait":
                    ret_baitids[ ivvid ] = ""

        # print "Gene ID", convid, "Motifs", motifs, "Bait_ID", ret_baitids.keys()

        return ret_baitids.keys()

    def convid2preys(self, convid):
        """ gene ---> set of Prey IDs
        Return [] if no corresponding IDs.
        """

        motifs = self.geneid_to_motif.val_force(convid)
        if not motifs: return []

        ret_preyids = {}
        for motif in motifs:
            ivvids = self.motif_info.get_seqid_from_motif(motif,
                                                          self.eval)
            for ivvid in ivvids:
                if self.ivv_info.ID_Type(ivvid) == "Prey":
                    ret_preyids[ ivvid ] = ""

        # print "Gene ID", convid, "Motifs", motifs, "Prey_ID", ret_preyids.keys()
        return ret_preyids.keys()


    """ ----------------------------------------------- """


    def set_mapping(self, motif_info,
                    GeneIDPfam_list_file, eval):

        if not isinstance(motif_info,
                          Motif.Motif_info1.Motif_info):
            raise "Instance type mismatch"

        self.motif_info = motif_info

        self.motif_to_geneid = Data_Struct.Hash.Hash("A")
        self.motif_to_geneid.read_file(filename = GeneIDPfam_list_file,
                                       Key_cols = [1],
                                       Val_cols = [0])

        self.geneid_to_motif = Data_Struct.Hash.Hash("A")
        self.geneid_to_motif.read_file(filename = GeneIDPfam_list_file,
                                       Key_cols = [0],
                                       Val_cols = [1])

        self.eval = eval


def test():
    import string

    ivv_info_file = "../../IVV/ivv_human8.0_info"
    ivv_prey_filter = "../../IVV/basic_filter_list2"

    motif_info_file = "../../Motifs/Pfam_ivv_human8.0_motif_info"
    GeneIDPfam_list_file = "../../Motifs/GeneIDPfam_list"


    filter = IVV_info.IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)

    sys.stderr.write("Reading IVV information...\n")
    ivv_info = IVV_info.IVV_info.IVV_info(ivv_info_file, filter)

    sys.stderr.write("Reading motif information...\n")
    motif_info = Motif.Motif_info1.Motif_info(motif_info_file)

    sys.stderr.write("Reading ID Conversion files...\n")
    ivv_gene = PPIPredFromIVVMMI1(ivv_info, mode = "S")
    ivv_gene.set_mapping(motif_info, GeneIDPfam_list_file, 1.0e-3)

    sys.stderr.write("IVV -> Motif Calculation...\n")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_convid()


    spoke = ivv_gene.get_spoke()
    for p1 in spoke:
        for p2 in spoke[p1]:
            print string.join([p1, p2, `spoke[p1][p2]`], "\t")

    """
    matrix = ivv_gene.get_matrix()
    for p1 in matrix:
        for p2 in matrix[p1]:
            print "Matrix", p1, p2, matrix[p1][p2]
    """

    """
    print "Search..."
    # source = ivv_gene.gene_to_ivv_common_bait_descr('3725', '2353')
    # source = ivv_gene.gene_to_ivv_common_bait_descr('6263', '7469')
    # source = ivv_gene.gene_to_ivv_common_bait_descr('201516', '23270')
    source = ivv_gene.gene_to_ivv_common_bait_descr('474', '1030')


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
    """

if __name__ == "__main__":
    test()





