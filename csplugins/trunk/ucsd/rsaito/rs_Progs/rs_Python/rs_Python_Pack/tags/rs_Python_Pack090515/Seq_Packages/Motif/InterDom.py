#!/usr/bin/env python

from Pfam_fs1 import Pfam_fs_entries
from Data_Struct.Hash2 import Hash

class InterDom:
    def __init__(self, interdom_file, pfam_fs_db_file):
        self.pfam_fs_db = Pfam_fs_entries(pfam_fs_db_file)
        self.interdom = Hash("S")
        self.interdom.read_file_hd(
            interdom_file,
            Key_cols_hd = ["domain 1", "domain 2"],
            Val_cols_hd = ["false positive"])

    def entry_check(self, m1, m2):
        a1 = self.pfam_fs_db.get_acc_from_name(m1)
        a2 = self.pfam_fs_db.get_acc_from_name(m2)

        if self.interdom.has_pair(a1, a2):
            key_pair = self.interdom.has_pair(a1, a2)
            # print key_pair, self.interdom.val(key_pair)
            if "yes" in self.interdom.val(key_pair):
                return False
            else:
                return True
        else:
            return None


if __name__ == "__main__":
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")
    interdom = InterDom(rsc.InterDom, rsc.Pfam_fs)
    print interdom.entry_check("CBM_2", "fn3")
    print interdom.entry_check("fn3", "CBM_2")
    print interdom.entry_check("Autoind_bind", "GerE")
    print interdom.entry_check("GerE", "Autoind_bind")
    print interdom.entry_check("ATP-synt_ab", "Subtilisin_N")
    print interdom.entry_check("Taro", "Hanako")

