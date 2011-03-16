#!/usr/bin/env python

class Pfam_fs_entries:
    def __init__(self, db_file):
        self.acc2name = {}
        self.name2acc = {}
        
        fh = open(db_file, "r")
        for line_c in fh:
            line = line_c.rstrip()
            if line[:6] == "NAME  ":
                name = line[6:]
            if line[:6] == "ACC   " and name:
                accv  = line[6:]
                acc   = accv.split(".")[0]
                self.acc2name[ acc  ] = name
                self.name2acc[ name ] = acc
                name = None

    def get_name_from_acc(self, acc):
        return self.acc2name.get(acc, "")
                
    def get_acc_from_name(self, name):
        return self.name2acc.get(name, "")
                
if __name__ == "__main__":

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsIVV_Config")

    pfam_fs_db = Pfam_fs_entries(rsc.Pfam_fs)
    
    print pfam_fs_db.get_name_from_acc("PF00244")
    print pfam_fs_db.get_name_from_acc("PF00389")
    print pfam_fs_db.get_name_from_acc("PF04510")
    print pfam_fs_db.get_acc_from_name("2-oxoacid_dh")
    
    
