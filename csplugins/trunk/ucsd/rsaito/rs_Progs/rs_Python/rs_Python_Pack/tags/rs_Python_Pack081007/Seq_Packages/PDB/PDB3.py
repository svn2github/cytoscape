#!/usr/bin/env python

import re
from Data_Struct.DictSet1 import DictSet

mspaces = re.compile(r" +")

class ATOM:
    def __init__(self, infoline):
             
        print infoline[13:16]

        self.atom_num = int(infoline[6:11].replace(" ", ""))
        self.chem1    = infoline[13:16].replace(" ", "")


        print self.chem1

        self.coordinate = [
            float(infoline[30:38].replace(" ", "")),
            float(infoline[39:46].replace(" ", "")),
            float(infoline[47:54].replace(" ", ""))   
                           ]

    def get_coordinate(self):
        return self.coordinate

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
        print atom.get_coordinate()
        
