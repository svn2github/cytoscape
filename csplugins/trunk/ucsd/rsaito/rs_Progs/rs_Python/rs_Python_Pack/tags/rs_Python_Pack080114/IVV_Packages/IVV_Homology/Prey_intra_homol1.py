#!/usr/bin/env python

import sys
import string
import math

import Usefuls.IntraRelat_compact1 as IRc
import Usefuls.Redund_level1 as Rl

class Prey_intra_homol(IRc.IntraRelat_compact):

    def treat_value(self, value):
        val = string.atof(value)
        if val == 0:
            return -200
        else:
            return int(math.log10(val))

    def ret_value(self, item1, item2):
        val = IRc.IntraRelat_compact.ret_value(self, item1, item2)
        if val is False:
            return False
        else:
            return 10 ** val

class Prey_redund_level(Rl.Redund_level):

    def __init__(self, ilist, irc):
        Rl.Redund_level.__init__(self, ilist)
        self.irc = irc

    def _redund_check(self, item1, item2):
        if self.irc.ret_value(item1, item2) is False:
            return 0
        else:
            return 1

if __name__ == "__main__":

    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

Kazuya Yoshi    0.0015
Jack  Heihachi  0.002
Heihachi Jack   0.00023
Kazuya Jack     0.00012
Jack   Kuni     0.000002
Kazuya Jun      0.00

""")

    shelve_file = "../../../Large_Data/intrahomol_ivv_human8.0_prey_cDNA_NF.bin"

    irc = Prey_intra_homol()
    # irc.read_tab_file(tmp_obj.filename(), False)
    irc.read_tab_file(sys.argv[1], True)
    irc.save_shelve(shelve_file)
    # irc.load_shelve(shelve_file)

    """
    print irc.ret_value("T060117_B12_C01.seq",
                        "T060117_D1_B05.seq")
    print irc.ret_value("T060117_F5_C18.seq",
                        "T060407_F05_E12.seq")

    # irc.display()


    prl = Prey_redund_level(["T060117_B12_C01.seq",
                             "T060407_B12_M17.seq",
                             "T051018_B12_G08.seq",
                             "XXXXX", "YYYYY"], irc)
    print prl.redund_level()
    """
