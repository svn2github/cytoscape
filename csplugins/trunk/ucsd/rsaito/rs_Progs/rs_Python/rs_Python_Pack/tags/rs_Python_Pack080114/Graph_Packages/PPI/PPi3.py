#!/usr/bin/env python

import string
import Pair
import Protein2
import Data_Struct.Hash

class PPi3:
    def __init__(self):
	self.ppi = {}
	self.protein_set = Protein2.Protein_Set()

    def set_ppi(self, protein1, protein2):

        protein1 = self.protein_set.add_protein(protein1)
        protein2 = self.protein_set.add_protein(protein2)

        p1 = protein1.get_protein_name()
        p2 = protein2.get_protein_name()

        if p1 in self.ppi:
            if p2 in self.ppi[p1]:
                self.ppi[p1][p2] = Pair.Pair(protein1, protein2) # Overwrite
            else:
                self.ppi[p1][p2] = Pair.Pair(protein1, protein2)
        else:
            self.ppi[p1] = { p2: Pair.Pair(protein1, protein2) }

    def set_ppi_val(self, protein1, protein2, val):
        p1 = protein1.get_protein_name()
        p2 = protein2.get_protein_name()

        self.set_ppi(protein1, protein2)
        self.ppi[ p1 ][ p2 ].set_val(val)

    def set_pair(self, pair):

        protein1, protein2 = pair.get_pair()
        val = pair.get_val()
        self.set_ppi(protein1, protein2)
        self.set_ppi_val(protein1, protein2, val)

    def get_all_ppi(self):
	ppilist = []
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                vals = self.ppi[p1][p2]
		ppilist.append((p1, p2, self.ppi[p1][p2].get_val()))
	return ppilist

    def get_non_redu_ppi(self): # Only one direction.
        ppilist = []
        done = {}
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                pair12 = p1 + "\t" + p2
                if pair12 in done: continue
                pair21 = p2 + "\t" + p1
                ppilist.append((p1, p2, self.ppi[p1][p2].get_val()))
                done[ pair12 ] = ""
                done[ pair21 ] = ""
        return ppilist


    def add_ppis(self, other_ppi):

        if not isinstance(other_ppi, PPi3):
            raise "Instance type mismatch: PPi3 expected."

        for p1 in other_ppi.ppi.keys():
            for p2 in other_ppi.ppi[p1].keys():
                self.set_pair(other_ppi.ppi[p1][p2])

    def read_hash_tab(self, hash):
        if not isinstance(hash, Data_Struct.Hash.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            p1, p2 = pair.split("\t")
            protein1 = Protein2.Protein(p1)
            protein2 = Protein2.Protein(p2)
            self.set_ppi(protein1, protein2)
            self.set_ppi_val(protein1, protein2, hash.val(pair))

    def read_from_file(self, filename, col1, col2, valcol):
        h = Data_Struct.Hash.Hash("S")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [valcol])

        self.read_hash_tab(h)

    def read_hash_tab2(self, hash, val):
	# Same value val is used.
        if not isinstance(hash, Data_Struct.Hash.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            p1, p2 = pair.split("\t")
            protein1 = Protein2.Protein(p1)
            protein2 = Protein2.Protein(p2)
            self.set_ppi(protein1, protein2)
            self.set_ppi_val(protein1, protein2, val)

    def read_from_file2(self, filename, col1, col2, val):

        h = Data_Struct.Hash.Hash("N")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [])

        self.read_hash_tab2(h, val)


    def read_dict(self, idict):
	for p1 in idict.keys():
            protein1 = Protein2.Protein(p1)
	    for p2 in idict[p1].keys():
                protein2 = Protein2.Protein(p2)
		self.set_ppi(protein1, protein2)
                self.set_ppi_val(protein1, protein2, idict[p1][p2])

    def read_dict2(self, idict, val):
	for p1 in idict.keys():
            protein1 = Protein2.Protein(p1)
	    for p2 in idict[p1].keys():
                protein2 = Protein2.Protein(p2)
		self.set_ppi(protein1, protein2)
                self.set_ppi_val(protein1, protein2, val)


    def both_dir(self):
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                pair = self.ppi[p1][p2]
                protein1, protein2 = pair.get_pair()
                val = pair.get_val()
                self.set_ppi(protein2, protein1)
                self.set_ppi_val(protein2, protein1, val)


    def get_ppi(self, p1, p2):
	if p1 in self.ppi and p2 in self.ppi[p1]:
	    return self.ppi[p1][p2]
	else:
	    return False

    def get_ppi_val(self, p1, p2):
	if p1 in self.ppi and p2 in self.ppi[p1]:
	    return self.ppi[p1][p2].get_val()
	else:
	    return False

    def get_pair(self, protein1, protein2):
        p1 = protein1.get_protein_name()
        p2 = protein2.get_protein_name()
        return self.get_ppi(p1, p2)

    def get_proteins(self):

	return self.protein_set.get_proteins()

    def get_protein_names(self):

	return self.protein_set.get_protein_names()

    def interactor(self, p):
	if p in self.ppi:
	    return self.ppi[p].keys()
	else:
	    return []

    def ppi_display(self):
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                val = self.ppi[p1][p2].get_val()
                print string.join([p1, p2, val], "\t")


    def ppi_cytoscape_simple1(self):
        ppilist = self.get_non_redu_ppi()
        for ppi in ppilist:
            print ppi[0], "pp", ppi[1]


if __name__ == "__main__":
    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

Protein-A     Protein-B   a
Protein-A     Protein-B   b
Protein-B     Protein-A   c
Protein-D     Protein-E   d
Protein-E     Protein-F   e
Protein-X     Protein-X   *


""")

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

Protein-A     Protein-B   t
Protein-A     Protein-B   u
Protein-B     Protein-A   v
Protein-D     Protein-E   w
Protein-E     Protein-F   x
Protein-A     Protein-B   t


""")

    ppi = PPi3()
    proteinA = Protein2.Protein("A")
    proteinB = Protein2.Protein("B")
    proteinC = Protein2.Protein("C")
    ppi.set_ppi_val(proteinA, proteinB, "PPI1")
    ppi.set_ppi_val(proteinB, proteinC, "PPI2")
    ppi.set_ppi_val(proteinB, proteinA, "PPI2")
    print ppi.get_all_ppi()
    print ppi.get_non_redu_ppi()

    ppi2 = PPi3()
    proteinA2 = Protein2.Protein("A2")
    proteinB2 = Protein2.Protein("B2")
    proteinC2 = Protein2.Protein("C2")
    ppi2.set_ppi_val(proteinA2, proteinB2, "PPI1")
    ppi2.set_ppi_val(proteinB2, proteinC2, "PPI2")
    ppi2.set_ppi_val(proteinB2, proteinA2, "PPI2")

    ppi2.add_ppis(ppi)

    pair1 = Pair.Pair(proteinA2, proteinC2)
    pair1.set_val("Yaa")
    ppi2.set_pair(pair1)
    print "Second"
    print ppi2.get_all_ppi()
    # print ppi2.get_non_redu_ppi()

    ppi3 = PPi3()
    ppi3.read_from_file(tmp_obj.filename(), 0, 1, 2)
    print ppi3.get_all_ppi()

    ppi4 = PPi3()
    ppi4.read_from_file2(tmp_obj.filename(), 0, 1, "Test")
    print ppi4.get_all_ppi()

    ppi5_hash = ({
    "A": { "B": "ab", "C": "ac" },
    "C": {"D": "cd"},
    "E": {"F": "ef" }})

    ppi5 = PPi3()
    ppi5.read_dict(ppi5_hash)
    ppi5.both_dir()
    print ppi5.get_all_ppi()

    ppi6 = PPi3()
    ppi6.read_dict2(ppi5_hash, "TESTING")
    print ppi6.get_all_ppi()
    print ppi6.get_ppi("A", "B")
    print ppi6.get_ppi_val("A", "B")
    print ppi2.get_pair(proteinA2, proteinB2)
    print ppi6.get_proteins()
    print ppi6.get_protein_names()

    print ppi6.interactor("A")
    ppi5.ppi_display()
    ppi5.ppi_cytoscape_simple1()
