#!/usr/bin/env python


class IVV_filter:
    def __init__(self):
        self.bait_filter = []    # ex. ("FOS", "JUN")
        self.bait_id_filter = [] # ex. ("2353_all", "3725_all")
        
        self.prey_filter = []
        # ex. ("S20051122_F02_04_XXX.seq", "S20051122_F02_03_YYY.seq")
        self._prey_filter_h = {} # Dictionary version

        self.exp_filter = []     # ex. ("H00013",)

    def set_Bait_filter(self, baits):
	self.bait_filter = baits

    def set_Bait_ID_filter(self, bait_IDs):
	self.bait_id_filter = bait_IDs

    def set_Prey_filter(self, preys):
	self.prey_filter += preys
        for prey in preys:
            self._prey_filter_h[ prey ] = ""
    
    def set_Prey_filter_file(self, filename):

        prey_list = []
        fh = open(filename, "r")
        for line in fh:
            flag = False
            for c in line:
                if c.isalnum():
                    flag = True
                    break
            if flag:
                line = line.rstrip()
                r = line.split("\t")
                prey_list.append(r[0])
        self.set_Prey_filter(prey_list)

    def set_Exp_filter(self, exps):
	self.exp_filter = exps

    def get_Bait_filter(self):
	return self.bait_filter

    def get_Bait_ID_filter(self):
	return self.bait_id_filter

    def get_Prey_filter(self):
        """ Only checks direct prey filter. It does not
        check preys filtered by other ID (ex. bait ID)
        """
	return self._prey_filter_h.keys()

    def get_Exp_filter(self):
	return self.exp_filter

    def check_prey(self, preyid):
        """ Only checks direct prey filter. It does not
        check preys filtered by other ID (ex. bait ID)
        """
        return self._prey_filter_h.has_key(preyid)

if __name__ == "__main__":

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

    filt = IVV_filter()
    filt.set_Bait_ID_filter(("2353_all", "3725_all"))
    filt.set_Prey_filter_file(rsc.PreyFilter)
    filt.set_Prey_filter(("T000XXX.seq",))
    filt.set_Bait_filter(("FOS", "JUN"))
    filt.set_Exp_filter(("H000XXX",))

    print filt.get_Prey_filter()
    print filt.get_Bait_ID_filter()
    print filt.get_Exp_filter()
    print filt.get_Bait_filter()

    print filt.check_prey("T000XXX.seq")
    print filt.check_prey("S20060609_5TH_E5_02_C06.seq")
    print filt.check_prey("XXX")

