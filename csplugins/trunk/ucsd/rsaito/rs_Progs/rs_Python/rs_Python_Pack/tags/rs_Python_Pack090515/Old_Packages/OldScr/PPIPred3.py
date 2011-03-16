#!/usr/bin/env python

import sys
sys.path.append("../")

import IVV_info.IVV_Conv
import IVV_info.IVV_filter
import Homology.Homology3_descr
import Usefuls.Hash
import Usefuls.NonRedSet

class PPIPred2(IVV_info.IVV_Conv.IVV_Conv):
    """ This class relates specific type of ID pairs (Here, referred to
    as "Converted ID pairs" to IVV pairs and vice versa """

    """ The followging 4 methods can be overridden so that
    the inherited class can perform predictions of PPIs according
    to interologs """

    def bait2convid(self, bait_ID):
        """ bait ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """
        refseq_hits = self.ivv_to_refseq.subject_ID_evalue_thres(
            bait_ID, self.bait_evalue)

        ret_geneids = Usefuls.NonRedSet.NonRedSet()
        # print "Bait ID -> RefSeq IDs", bait_ID, refseq_hits
        
        for refseq_hit in refseq_hits:
            geneids = self.refseq_to_geneid.val_force(refseq_hit)
            ret_geneids.append_list(geneids)

        # print "Bait ID -> Gene IDs :", bait_ID, ret_geneids.ret_set()
        return ret_geneids.ret_set()

    def prey2convid(self, prey_ID):
        """ prey ID ---> Set of Converted IDs
        Return [] if no corresponding IDs.
        """

        refseq_hits = self.ivv_to_refseq.subject_ID_evalue_thres(
            prey_ID, self.prey_evalue)
        ret_geneids = Usefuls.NonRedSet.NonRedSet()

        for refseq_hit in refseq_hits:
            geneids = self.refseq_to_geneid.val_force(refseq_hit)
            ret_geneids.append_list(geneids)

        # print "Prey ID -> Gene IDs :", prey_ID, ret_geneids.ret_set()
        return ret_geneids.ret_set()

    def convid2baits(self, convid):
        """ gene ---> set of Bait IDs
        Return [] if no corresponding IDs.
        """

        refseqs = Usefuls.NonRedSet.NonRedList(
            self.geneid_to_refseq.val_force(convid))
        ret_baitids = {}

        # print "Gene ID --> RefSeqs", convid, refseqs
        for refseq in refseqs:
            bait_and_preyid_hits = self.ivv_to_refseq.reverse_query_ID(refseq)
            # print "RefSeq --> IVV hits:", refseq, bait_and_preyid_hits
            for ivv_hit in bait_and_preyid_hits:
                if (self.ivv_info.ID_Type(ivv_hit) == "Bait" and
                    self.ivv_to_refseq.e_value(ivv_hit, refseq)
                    <= self.bait_evalue):
                    ret_baitids[ ivv_hit ] = ""
        # print "Gene ID --> Bait IDs", convid, refseqs, bait_and_preyid_hits, ret_baitids.keys()
        return ret_baitids.keys()


    def convid2preys(self, convid):
        """ gene ---> set of Prey IDs
        Return [] if no corresponding IDs.
        """

        refseqs = Usefuls.NonRedSet.NonRedList(
            self.geneid_to_refseq.val_force(convid))
        ret_preyids = {}


        # print "Gene ID --> RefSeqs (P):", convid, refseqs
        for refseq in refseqs:
            bait_and_preyid_hits = self.ivv_to_refseq.reverse_query_ID(refseq)
            # print "RefSeq --> IVV hits (P):", refseq, bait_and_preyid_hits
            for ivv_hit in bait_and_preyid_hits:
                if (self.ivv_info.ID_Type(ivv_hit) == "Prey" and
                    self.ivv_to_refseq.e_value(ivv_hit, refseq)
                    <= self.prey_evalue):
                    ret_preyids[ ivv_hit ] = ""
        # print "Gene ID --> prey IDs (P)", convid, ret_preyids.keys()
        # print convid, refseqs, bait_and_preyid_hits, ret_preyids.keys()

        return ret_preyids.keys()


    """ ----------------------------------------------- """


    def set_mapping(self,
                    ivv_to_refseq_homol,
                    refseq_to_geneid_file,
                    bait_evalue, prey_evalue,
                     taxonid = "9606"):
        
        if not isinstance(ivv_to_refseq_homol,
                          Homology.Homology3_descr.HomologyDescr3):
            raise "Instance type mismatch"
        
        self.ivv_to_refseq = ivv_to_refseq_homol
        self.ivv_to_refseq.enable_reverse()

        self.refseq_to_geneid = Usefuls.Hash.Hash_filt("A")
        self.refseq_to_geneid.set_filt([0, taxonid])
        self.refseq_to_geneid.read_file(filename = refseq_to_geneid_file,
                                       Key_cols = [6],
                                       Val_cols = [1])

        self.geneid_to_refseq = Usefuls.Hash.Hash_filt("A")
        self.geneid_to_refseq.set_filt([0, taxonid])
        self.geneid_to_refseq.read_file(filename = refseq_to_geneid_file,
                                       Key_cols = [1],
                                       Val_cols = [6])

        self.bait_evalue = bait_evalue
        self.prey_evalue = prey_evalue

        
def test():
    import string

    ivv_info_file = "../../IVV/ivv_human8.0_info"
    ivv_prey_filter = "../../IVV/basic_filter_list2"

    ivv_to_refseq_homol_file = "../../Homology/homol_ivv_human8.0-human_refseq"
    refseq_to_geneid_file = "../../../../../../../Gene_info/gene2refseq_hs"
    
    filter = IVV_info.IVV_filter.IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)

    sys.stderr.write("Reading IVV information...\n")
    ivv_info = IVV_info.IVV_info.IVV_info(ivv_info_file, filter)
    
    sys.stderr.write("Reading homology information...\n")
    homology = Homology.Homology3_descr.HomologyDescr3(
        ivv_to_refseq_homol_file)

    sys.stderr.write("Reading ID Conversion files...\n")
    ivv_gene = PPIPred2(ivv_info, mode = "S")
    ivv_gene.set_mapping(homology,
                         refseq_to_geneid_file,
                         1.0e-30, 1.0e-3)

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
    # source = ivv_gene.gene_to_ivv_common_bait_descr('3725', '2353')
    # source = ivv_gene.gene_to_ivv_common_bait_descr('6263', '7469')
    source = ivv_gene.gene_to_ivv_common_bait_descr('7464', '51088')


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





