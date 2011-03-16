#!/usr/bin/python

from Data_Struct.Hash2 import Hash
from Data_Struct.NonRedSet1 import NonRedSet
from Usefuls.ListProc1 import *
from Usefuls.Combi1 import *
from Usefuls.Counter import *
from Usefuls.Instance_check \
     import instance_class_check

import IVV_filter1
import Bait_info1

import IVV_Packages.IVV_Motif.Motif_info1 as IVV_Motif_info
from Seq_Packages.Motif.MMI2 import MMI

import PullDown1

class Prey_info:
    def __init__(self, ivv_info_file,
		 filter = IVV_filter1.IVV_filter()):

        self.ivv_info_file = ivv_info_file
	self.filter = filter

        prey_info = Hash("L")
        prey_info.set_filt([0, "[ Prey Info ]"])

        p_filter = []
        if filter.get_Prey_filter():
	    p_filter = map(lambda prey: [ 1, prey ],
                           filter.get_Prey_filter())
            prey_info.set_filt_OR(*p_filter)

        b_filter = []
	if filter.get_Bait_filter():
	    b_filter += map(lambda bait: [ 9, bait ],
			    filter.get_Bait_filter())
            prey_info.set_filt_OR(*b_filter)

        if filter.get_Bait_ID_filter():
	    b_filter += map(lambda bait: [ 4, bait ],
			    filter.get_Bait_ID_filter())
	    prey_info.set_filt_OR(*b_filter)

        prey_info.read_file(filename = ivv_info_file,
                            Key_cols = [1],
                            Val_cols = [2])


	""" All experimental data will be read, regardless of filter """

        exp_header = Hash("L")
        exp_header.set_filt([0, "[ Exp quality header ]"])
        exp_header.read_file(filename = ivv_info_file,
                             Key_cols = [1],
                             Val_cols = [2])

        self.prey_info = prey_info
        self.exp_header = exp_header

    def preys(self):
        return self.prey_info.keys()

    def get_qual(self, preyID, key):
	prey_qual = self.prey_info.val(preyID).split("\t")
	exp = prey_qual[ 3 ]
	header = self.exp_header.val(exp).split("\t")
	idx = header.index(key)
	val = prey_qual[ idx + 6 ]
	return val

    def get_qual_noerror(self, preyID, key):
	prey_qual = self.prey_info.val(preyID).split("\t")
	exp = prey_qual[ 3 ]
	header = self.exp_header.val(exp).split("\t")
	idx = header.index(key)
        if idx + 6 < len(prey_qual):
            val = prey_qual[ idx + 6 ]
        else:
            val = ""
	return val

    def geneid(self, preyID):
	prey_qual = self.prey_info.val(preyID).split("\t")
        return prey_qual[0]

    def genesymbol(self, preyID):
	prey_qual = self.prey_info.val(preyID).split("\t")
        return prey_qual[1]

    def bait_ID(self, preyID):
	prey_qual = self.prey_info.val(preyID).split("\t")
        return prey_qual[2]

    def expno(self, preyID):
	prey_qual = self.prey_info.val(preyID).split("\t")
        return prey_qual[3]


class Prey:
    def __init__(self, prey_info, preyID):
        if not isinstance(prey_info, Prey_info):
            raise "Instance type mismatch"

        self.prey_info = prey_info
        self.ID = preyID

    def Prey_info(self):
        return self.prey_info

    def preyID(self):
        return self.ID

    def geneid(self):
        return self.prey_info.geneid(self.ID)

    def qual(self, key):
        return self.prey_info.get_qual(self.ID, key)

    def qual_force(self, key):
        return self.prey_info.get_qual_noerror(self.ID, key)

    def genesymbol(self):
        return self.prey_info.genesymbol(self.ID)

    def baitID(self):
        return self.prey_info.bait_ID(self.ID)

    def expno(self):
        return self.prey_info.expno(self.ID)

    def motifs(self, motif_info, evalue):
        if not isinstance(motif_info, IVV_Motif_info.Motif_info):
            raise "Instance type mismatch."

        """
        if motif_info.get_motif(self.ID, evalue):
            print self.ID, motif_info.get_motif(self.ID, evalue)
        """

        return motif_info.get_motif(self.ID, evalue)

    def pulldown(self, pullDown):
        if not isinstance(pullDown, PullDown1.PullDown):
            raise "Instance type mismatch."
        return pullDown.pd_check(self.ID)

    def dict_check(self, idict):
        return idict.has_key(self.ID)


class Prey_Set:
    """ Be careful of the cases where Object IDs are different but
    prey IDs are the same. """
    def __init__(self, prey_info, preyIDs = None):
        self.prey_info = prey_info
        if preyIDs is None:
            preyIDs = []
        self.preys = []
        for preyID in preyIDs:
            self.preys.append(Prey(prey_info, preyID))

    def set_Preys(self, preys):
        self.preys = preys

    def get_Preys(self):
        return self.preys

    def get_PreyIDs(self):
        preyIDs = []
        for prey in self.get_Preys():
            preyIDs.append(prey.preyID())
        return preyIDs

    def __getitem__(self, i):
        return self.get_Preys()[i]

    def __len__(self):
        return len(self.preys)

    def Prey_info(self):
        return self.prey_info

    def check_MOCK(self, bait_info):
        instance_class_check(bait_info, Bait_info.Bait_info)

        bait_IDs = self.get_info('baitID')
        for bait_ID in bait_IDs:
            if bait_info.bait_type(bait_ID) == "Mock":
                return True

        return False

    def count_bait_geneids(self, reprod_thres, bait_info):
        instance_class_check(bait_info, Bait_info.Bait_info)

        bait_IDs = self.get_info('baitID')
        ct = Count2()
        for bait_ID in bait_IDs:
            if bait_info.bait_is_protein(bait_ID):
                bait_geneid = bait_info.geneid(bait_ID)
                ct.count_up(bait_geneid)
        ans = 0
        for bait_geneid in ct.get_elems():
            if ct.get_counter(bait_geneid) >= reprod_thres:
                ans += 1

        return ans

    def get_info(self, func_name, *param):
        ret = []
        for prey in self.preys:
            ret.append(Prey.__dict__[ func_name ](prey, *param))

        return ret

    def get_info_squash(self, func_name, *param):
        # Returned list may contain redundant elements, but
        # nulls are eliminated.
        ret = []
        for prey in self.preys:
            ret += Prey.__dict__[ func_name ](prey, *param)

        return ret

    def intersection(self, i_prey_set):
        if not isinstance(i_prey_set, Prey_Set):
            raise "Instance type mismatch."
        if not self.Prey_info() is i_prey_set.Prey_info():
            raise "Prey information object not identical."

        intersection = {}

        r_preyID_set = {}
        for r_prey in self.get_Preys():
            r_preyID_set[ r_prey.preyID() ] = ""

        for i_prey in i_prey_set.get_Preys():
            if i_prey.preyID() in r_preyID_set:
                intersection[ i_prey ] = ""

        ret_ps = Prey_Set(self.prey_info)
        ret_ps.set_Preys(intersection.keys())
        return ret_ps


class Prey_Set_pair:
    def __init__(self, prey_set1, prey_set2):
        self.prey_set1 = prey_set1
        self.prey_set2 = prey_set2

    def Prey_set1(self):
        return self.prey_set1

    def Prey_set2(self):
        return self.prey_set2

    def _equal(self, prey1, prey2):
        if prey1.preyID() == prey2.preyID():
            return True
        else:
            return False

    def num_hetero(self):
        count = 0
        for prey1 in self.Prey_set1().get_Preys():
            for prey2 in self.Prey_set2().get_Preys():
                if not self._equal(prey1, prey2):
                    count += 1
        return count

    def all_mmi(self, mmi2, motif_info, evalue, sep):
        if not isinstance(motif_info, IVV_Motif_info.Motif_info):
            raise "Instance type mismatch."
        if not isinstance(mmi2, MMI):
            raise "Instance type mismatch."

        ct_mmi = Count2()
        cb = Combi_iterator(
            ( self.prey_set1.get_Preys(), self.prey_set2.get_Preys()))
        while cb.next():
            prey1, prey2 = cb.current()
            if self._equal(prey1, prey2):
                continue
            motif1 = prey1.motifs(motif_info, evalue)
            motif2 = prey2.motifs(motif_info, evalue)
            mmis = mmi2.get_mmi_from_motifs(motif1, motif2, sep)
            for mmi in mmis:
                ct_mmi.count_up(mmi)
            # print "PP MM pair", prey1.preyID(), motif1, prey2.preyID(), motif2, "MMI:",  mmis
        return ct_mmi.get_elems()


def test():

    import string
    from Usefuls.rsConfig import RSC
    config_file = "../../../rsIVV_Config"
    rsc = RSC(config_file)

    filter = IVV_filter.IVV_filter1()
    # filter.set_Bait_filter(("JUN", "FOS"))
    # filter.set_Prey_filter_file("test_prey_list")
    prey_info = Prey_info(rsc.IVVInfo) # , filter)
    bait_info = Bait_info.Bait_info(rsc.IVVInfo)

    ps0 = Prey_Set(prey_info,
                   ["S20060609_5TH_M4_2_02_E11.seq",
                  #  "050519_m5_4_F03_m5_4_F3_027.seq",
                  #  "T060117_F5_K15.seq",
                    "T051018_C1_I22.seq",
                    "T050726_MJun4_2_C07.seq"])

    print ps0.count_bait_geneids(2, bait_info)
    print ps0.check_MOCK(bait_info)

    """
    motif_info = IVV_Motif.Motif_info1.Motif_info(rsc.MotifInfo)
    pd = PullDown.PullDown1(rsc.PullDown)
    iPfam = Motif.MMI2.MMI2(rsc.iPfam)

    ps1 = Prey_Set(prey_info,
                  ["S20060609_5TH_M4_2_02_E11.seq",
                   "050519_m5_4_F03_m5_4_F3_027.seq",
                   "T051018_C1_I22.seq",
                   "T050726_MJun4_2_C07.seq"])

    ps2 = Prey_Set(prey_info,
                   ["XXXXX",
                    "050519_m5_4_F03_m5_4_F3_027.seq",
                    "YYYYY",
                    "S20060609_7TH_E2_02_F03.seq",
                    "T050726_MJun4_2_C07.seq",
                    "WWWWW"])

    idict = { "S20060609_7TH_E2_02_F03.seq": None,
              "T050726_MJun4_2_C07.seq": None }

    print ps1.get_info("qual", "orf")
    print ps1.get_info("geneid")
    print ps1.get_info_squash("motifs", motif_info, 1.0e-3)
    print ps1.get_info("pulldown", pd)
    print ps1.get_info("dict_check", idict)

    ps_inter = ps1.intersection(ps2)
    for prey in ps_inter.get_Preys():
        print prey.preyID()

    ps_pair = Prey_Set_pair(ps1, ps2)
    all_mmi = ps_pair.all_mmi(iPfam, motif_info, 1.0e-3, ":-:")
    print "Known MMIs", string.join(all_mmi, ",")
    print ps_pair.num_hetero()

    preys = prey_info.preys()
    for prey in preys:
        preyobj = Prey(prey_info, prey)
        print preyobj.preyID(),
        print preyobj.geneid(),
        print preyobj.genesymbol(),
        print preyobj.baitID(),
        print preyobj.expno()
        print preyobj.qual_force("orf")

        print prey_info.bait_ID(prey), prey,
        print prey_info.expno(prey),
        print prey_info.get_qual_noerror(prey, "hit_ref_position")

    """

if __name__ == "__main__":
    test()
