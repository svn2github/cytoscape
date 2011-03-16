#!/usr/bin/env python

from Usefuls.Instance_check import instance_class_check
import Data_Struct.Hash_A
import Data_Struct.NonRedSet
import Homology4_descr
import Homology_map
import IVV_info.IVV_info
import IVV_info.Bait_info
import IVV_info.Prey_info

class RefSeq_to_IVV:
    def __init__(self, ivv_info, homoldescr):
        instance_class_check(ivv_info, IVV_info.IVV_info.IVV_info)
        instance_class_check(homoldescr, Homology4_descr.HomologyDescr4)

        self.ivv_info = ivv_info
        self.homoldescr = homoldescr
        self.homoldescr.enable_reverse()

    def refseq2ivv_Pset(self, refseqid, hm):
        ivvs = self.homoldescr.reverse_query_ID_evalue_thres(refseqid, hm)
        preys = []
        for ivv in ivvs:
            if self.ivv_info.ID_Type(ivv) == "Prey":
                preys.append(ivv)
        return each_RefSeq_to_IVV_Pset(refseqid,
                                       self.ivv_info.Prey_info(),
                                       preys)


class each_RefSeq_to_IVV_Pset:
    def __init__(self, refseqid, prey_info, preys):
        instance_class_check(prey_info, IVV_info.Prey_info.Prey_info)

        self.refseqid = refseqid
        self.prey_set = IVV_info.Prey_info.Prey_Set(prey_info, preys)

    def get_refseqid(self):

        return self.refseqid

    def get_prey_set(self):

        return self.prey_set


class GeneID_to_IVV:
    def __init__(self, ivv_info, geneid_to_refseq, homoldescr):
        instance_class_check(ivv_info, IVV_info.IVV_info.IVV_info)
        instance_class_check(geneid_to_refseq, Data_Struct.Hash_A.Hash_A)
        instance_class_check(homoldescr, Homology4_descr.HomologyDescr4)

        self.ivv_info = ivv_info
        self.geneid_to_refseq = geneid_to_refseq
        self.homoldescr = homoldescr
        self.homol_refseq2ivv = RefSeq_to_IVV(self.ivv_info,
                                              self.homoldescr)


    def geneid2ivv_Pset(self, geneid, hm):

        ret = []
        refseqs_redund = self.geneid_to_refseq.val_force(geneid)
        refseqs = Usefuls.ListProc.NonRedList(refseqs_redund)

        for refseqid in refseqs:
            ret.append(self.homol_refseq2ivv.refseq2ivv_Pset(refseqid, hm))

        return ret

    def count_average_bait_geneids(self, geneid, hm, reprod = 2):

        bait_geneid_count = 0

        refseqs_redund = self.geneid_to_refseq.val_force(geneid)
        refseqids = Usefuls.ListProc.NonRedList(refseqs_redund)
        if refseqids == []:
            return None
        for refseqid in refseqids:
            homology_map_preys = Homology_map.Homology_map1_preys(
                self.homoldescr, refseqid, hm, self.ivv_info)
            if homology_map_preys.get_status():
                # print geneid, refseqid, homology_map_preys.max_count_bait_geneids(reprod)
                bait_geneid_count += \
                                  homology_map_preys. \
                                  max_count_bait_geneids(reprod)
            else:
                pass
                # print "No corresponding hit for", refseqid
        return 1.0 * bait_geneid_count / len(refseqids)



    def count_average_preys(self, geneid, hm):

        preys_count = 0

        refseqs_redund = self.geneid_to_refseq.val_force(geneid)
        refseqids = Usefuls.ListProc.NonRedList(refseqs_redund)
        if refseqids == []:
            return None
        for refseqid in refseqids:
            homology_map_preys = Homology_map.Homology_map1_preys(
                self.homoldescr, refseqid, hm, self.ivv_info)
            if homology_map_preys.get_status():
                # print geneid, refseqid, homology_map_preys.max_count_bait_geneids(reprod)
                preys_count += homology_map_preys. \
                               max_count_preys_invalid_MOCK()
            else:
                pass
                # print "No corresponding hit for", refseqid
        return 1.0 * preys_count / len(refseqids)


    def check_MOCK(self, geneid, hm):

        count_MOCK = 0

        refseqs_redund = self.geneid_to_refseq.val_force(geneid)
        refseqids = Usefuls.ListProc.NonRedList(refseqs_redund)
        if refseqids == []:
            return None
        for refseqid in refseqids:
            homology_map_preys = Homology_map.Homology_map1_preys(
                self.homoldescr, refseqid, hm, self.ivv_info)
            if homology_map_preys.get_status():
                # print refseqid, homology_map_preys.check_MOCK()
                count_MOCK += homology_map_preys.check_MOCK()
            else:
                pass
                #print "No corresponding hit for", refseqid
        return 1.0 * count_MOCK / len(refseqids)


if __name__ == "__main__":
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC("../../../rsIVV_Config")

    ivv_info = IVV_info.IVV_info.IVV_info(rsc.IVVInfo)
    homol_refseq2ivv = Homology4_descr.HomologyDescr4(rsc.HomolIVVRefSeq)

    # refseq2ivv = RefSeq_to_IVV(ivv_info, homol_refseq2ivv)
    # print refseq2ivv.refseq2ivv_Pset("70780357", 0.1).get_Preys()
    # print refseq2ivv.refseq2ivv_Pset("21956645", 0.1)

    geneid_to_refseq = Data_Struct.Hash_A.Hash_filt_A()
    geneid_to_refseq.set_filt([0, "9606"])
    geneid_to_refseq.read_file(filename = rsc.Gene2RefSeq,
                               Key_cols = [1],
                               Val_cols = [6])

    geneid2ivv = GeneID_to_IVV(ivv_info, geneid_to_refseq, homol_refseq2ivv)

    """
    for i in range(100000):
        if geneid2ivv.count_average_bait_geneids(`i`, 0.1) is not None:
            print "###", i, "###"
            print geneid2ivv.count_average_bait_geneids(`i`, 0.1)
            print geneid2ivv.check_MOCK(`i`, 0.1)


    print geneid2ivv.count_average_bait_geneids("2353", 0.1)
    print geneid2ivv.count_average_bait_geneids("3725", 0.1)
    print geneid2ivv.count_average_bait_geneids("XXXX", 0.1)
    print geneid2ivv.check_MOCK("2353", 0.1)

    """
    for each_refseq in geneid2ivv.geneid2ivv_Pset("2353", 0.1):
        print each_refseq.get_refseqid()
        for each_prey in each_refseq.get_prey_set():
            print each_prey.preyID(), each_prey.baitID()

    print geneid2ivv.count_average_bait_geneids("2353", 0.1, reprod = 3)
    print geneid2ivv.count_average_preys("2353", 0.1)
    print geneid2ivv.check_MOCK("2353", 0.1)

