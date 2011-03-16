#!/usr/bin/env python

import re

mspaces = re.compile(r" +")

class ATOM:
    def __init__(self, infoline):
        self.info = re.split(mspaces,
                             infoline)
        
    def get_info(self):
        return self.info

class PDB_entry:
    def __init__(self, fh):
        self.atoms = []
        for line in fh:
            if line.startswith("ATOM"):
                self.atoms.append(ATOM(line.rstrip()))
    def get_atoms(self):
        return self.atoms
           
if __name__ == "__main__":
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsTest_Config")

    pdb_entry = PDB_entry(open(rsc.PDB_sample1))
    for atom in pdb_entry.get_atoms():
        print atom.get_info()
        
