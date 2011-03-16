#!/usr/bin/env python

import re
from Data_Struct.DictSet1 import DictSet

mspaces = re.compile(r" +")

class ATOM:
    def __init__(self, infoline):
        self.info = re.split(mspaces,
                             infoline)[1:]
        self.info[0] = int(self.info[0])
        self.info[4] = int(self.info[4])
        self.info[5] = float(self.info[5])
        self.info[6] = float(self.info[6])
        self.info[7] = float(self.info[7])
        print self.info[8]
        self.info[8] = float(self.info[8])
        # self.info[9] = float(self.info[9])
        
    def get_info(self):
        return self.info

class PDB_entry:
    def __init__(self, fh):
        self.entry = DictSet()
        for line in fh:
            line_key = line.split(" ")[0]
            self.entry.append(line_key, line.rstrip())
    
    def get_entry(self):
        return self.entry

    def get_atoms(self):
        if not 'atoms' in vars(self):
            self.atoms = []
            for line in self.get_entry()["ATOM"]:
                self.atoms.append(ATOM(line))
        
        return self.atoms
           
if __name__ == "__main__":
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsTest_Config")

    pdb_entry = PDB_entry(open(rsc.PDB_sample1))
    for atom in pdb_entry.get_atoms():
        print atom.get_info()
        
