#!/usr/bin/env python

import string
import Pair
import Protein2
import Data_Struct.Hash

class PPi:
    def __init__(self):
        self.ppi = {}
        self.ppi_rev = {}
        self.protein_set = Protein2.Protein_Set()

    def get_protein_set(self):

        return self.protein_set

    def set_ppi(self, p1, p2):
        """
        ***************************************************
        Besides __init__, only the following two methods,
        set_ppi and del_ppi, controls ppi, and protein set.
        ***************************************************
        """

        p1 = self.protein_set.add_protein(p1)
        p2 = self.protein_set.add_protein(p2)

        if p1 in self.ppi:
            if p2 in self.ppi[p1]:
                self.ppi[p1][p2] = Pair.Pair(p1, p2) # Overwrite
            else:
                self.ppi[p1][p2] = Pair.Pair(p1, p2)
        else:
            self.ppi[p1] = { p2: Pair.Pair(p1, p2) }

        if p2 in self.ppi_rev:
            if p1 in self.ppi_rev[p2]:
                self.ppi_rev[p2][p1] = self.ppi[p1][p2] # Overwrite
            else:
                self.ppi_rev[p2][p1] = self.ppi[p1][p2]
        else:
            self.ppi_rev[p2] = { p1: self.ppi[p1][p2] }

        return p1, p2

    def del_ppi(self, p1, p2):

        del self.ppi[p1][p2]
        del self.ppi_rev[p2][p1]

        if self.ppi[p1] == {}:
            del self.ppi[p1]
        if self.ppi_rev[p2] == {}:
            del self.ppi_rev[p2]

        if (not self.ppi.has_key(p1)) and (not self.ppi_rev.has_key(p1)):
            self.protein_set.delete_protein(p1)
        if (not self.ppi.has_key(p2)) and (not self.ppi_rev.has_key(p2)):
            self.protein_set.delete_protein(p2)

    def del_ppi_both(self, p1, p2):
        self.del_ppi(p1, p2)
        if not p1 is p2:
            self.del_ppi(p2, p1)

    def set_ppi_val(self, p1, p2, val):

        p1, p2 = self.set_ppi(p1, p2)
        self.ppi[ p1 ][ p2 ].set_val(val)

        return p1, p2

    def set_ppi_by_protein_names(self,
                                 protein_name1,
                                 protein_name2,
                                 val = None):

        p1 = Protein2.Protein(protein_name1)
        p2 = Protein2.Protein(protein_name2)
        self.set_ppi_val(p1, p2, val)

        return p1, p2

    def set_pair(self, pair):

        p1, p2 = pair.get_pair()
        val = pair.get_val()
        self.set_ppi_val(p1, p2, val)

    def get_all_ppi(self):
        ppilist = []
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                ppilist.append(self.ppi[p1][p2])

        return ppilist

    def get_non_redu_ppi(self): # Only one direction.
        ppilist = []
        done = {}
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                pair12 = (p1, p2)
                if pair12 in done: continue
                pair21 = (p2, p1)
                ppilist.append(self.ppi[p1][p2])
                done[ pair12 ] = ""
                done[ pair21 ] = ""
        return ppilist

    def add_ppis(self, other_ppi):

        if not isinstance(other_ppi, PPi):
            raise "Instance type mismatch: PPi expected."

        for p1 in other_ppi.ppi.keys():
            for p2 in other_ppi.ppi[p1].keys():
                self.set_pair(other_ppi.ppi[p1][p2])

    def read_hash_tab(self, hash):
        if not isinstance(hash, Data_Struct.Hash.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            protein_name1, protein_name2 = pair.split("\t")
            p1 = Protein2.Protein(protein_name1)
            p2 = Protein2.Protein(protein_name2)
            self.set_ppi_val(p1, p2, hash.val(pair))

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
            protein_name1, protein_name2 = pair.split("\t")
            p1 = Protein2.Protein(protein_name1)
            p2 = Protein2.Protein(protein_name2)
            self.set_ppi_val(p1, p2, val)

    def read_from_file2(self, filename, col1, col2, val):

        h = Data_Struct.Hash.Hash("N")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [])

        self.read_hash_tab2(h, val)


    def read_dict(self, idict):
	for protein_name1 in idict.keys():
            p1 = Protein2.Protein(protein_name1)
	    for protein_name2 in idict[protein_name1].keys():
                p2 = Protein2.Protein(protein_name2)
                self.set_ppi_val(p1, p2,
                                 idict[protein_name1][protein_name2])

    def read_dict2(self, idict, val):
	for protein_name1 in idict.keys():
            p1 = Protein2.Protein(protein_name1)
	    for protein_name2 in idict[protein_name1].keys():
                p2 = Protein2.Protein(protein_name2)
                self.set_ppi_val(p1, p2, val)

    def both_dir(self):
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                pair = self.ppi[p1][p2]
                val = pair.get_val()
                self.set_ppi_val(p2, p1, val)


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

    def get_protein_by_name(self, protein_name):
        return self.protein_set.get_protein_by_name(protein_name)

    def get_ppi_by_protein_names(self,
                                 protein_name1,
                                 protein_name2):

        p1 = self.protein_set.get_protein_by_name(protein_name1)
        p2 = self.protein_set.get_protein_by_name(protein_name2)

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

    def interactor_rev(self, p):
	if p in self.ppi_rev:
	    return self.ppi_rev[p].keys()
	else:
	    return []

    def ppi_info1(self):
        print "Protein Set:"
        for protein in self.get_proteins():
            print protein, protein.get_protein_name()
        print "PPI:"
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                pair = self.ppi[p1][p2]
                print "Pair", \
                      pair.get_pair()[0].get_protein_name(), \
                      pair.get_pair()[1].get_protein_name(), \
                      "...", pair
                print "Proteins ...", pair.get_pair()
                print "Value", pair.get_val()

    def ppi_info_rev1(self):
        print "Protein Set:"
        for protein in self.get_proteins():
            print protein, protein.get_protein_name()
        print "PPI:"
        for p1 in self.ppi_rev.keys():
            for p2 in self.ppi_rev[p1].keys():
                pair = self.ppi_rev[p1][p2]
                print "Pair", \
                      pair.get_pair()[0].get_protein_name(), \
                      pair.get_pair()[1].get_protein_name(), \
                      "...", pair
                print "Pair rev", p1.get_protein_name(), p2.get_protein_name()
                print "Proteins ...", pair.get_pair()
                print "Value", pair.get_val()


    def ppi_display(self):
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                val = self.ppi[p1][p2].get_val()
                print string.join([p1.get_protein_name(),
                                   p2.get_protein_name(), val], "\t")


    def ppi_cytoscape_simple1(self):
        ppilist = self.get_non_redu_ppi()
        for ppi in ppilist:
            print ppi.get_pair()[0].get_protein_name(), \
                  "pp", \
                  ppi.get_pair()[1].get_protein_name()

    def pair_judge(self):
        return True


    def ppi_filter(self):
        # Judge must be done as a part of PPi4's method because
        # judge may depend on global structure of PPI

        ppi_filtered = PPi()

        for pair in self.get_all_ppi():
            if self.pair_judge(pair) is True:
                ppi_filtered.set_pair(pair)

        return ppi_filtered


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
Protein-A     Protein-C   t


""")

    print "***** Basic Check *****"
    ppi = PPi()
    proteinA = Protein2.Protein_Factory().make("A")
    proteinB = Protein2.Protein_Factory().make("B")
    proteinC = Protein2.Protein_Factory().make("C")
    proteinD = Protein2.Protein_Factory().make("D")
    ppi.set_ppi_val(proteinA, proteinB, "PPI1")
    ppi.set_ppi_val(proteinB, proteinC, "PPI2")
    ppi.set_ppi_val(proteinB, proteinA, "PPI2")
    ppi.set_ppi_val(proteinD, proteinB, "PPI3")
    ppi.set_ppi_val(proteinD, proteinD, "PPI4")
    print "All ppis:"
    for pair in ppi.get_all_ppi():
        print pair, pair.get_pair()
    print "Non-redundant ppis:"
    print ppi.get_non_redu_ppi()

    ppi.ppi_info1()
    print "Interactors of", proteinB,":"
    print ppi.interactor(proteinB)
    print ppi.interactor_rev(proteinB)
    ppi.del_ppi_both(proteinA, proteinB)
    ppi.del_ppi_both(proteinD, proteinD)
    ppi.ppi_info1()

    print

    print "***** PPI Addition Check *****"
    ppi2 = PPi()
    proteinA2 = Protein2.Protein("A2")
    proteinB2 = Protein2.Protein("B2")
    proteinC2 = Protein2.Protein("C2")
    ppi2.set_ppi_val(proteinA2, proteinB2, "ppi1")
    ppi2.set_ppi_val(proteinB2, proteinC2, "ppi2")
    ppi2.set_ppi_val(proteinB2, proteinA2, "ppi2")
    ppi2.add_ppis(ppi)
    ppi2.ppi_display()

    print

    print "***** Pair Set Check *****"
    pair1 = Pair.Pair(proteinA2, proteinC2)
    pair1.set_val("Yaa")
    ppi2.set_pair(pair1)
    print ppi2.get_all_ppi()
    # print ppi2.get_non_redu_ppi()
    ppi2.ppi_display()

    print

    print "***** File Read Check 1 *****"
    ppi3 = PPi()
    ppi3.read_from_file(tmp_obj.filename(), 0, 1, 2)
    ppi3.ppi_display()
    ppi3.ppi_info1()
    print "All:", ppi3.get_all_ppi()
    print "Non-redu:", ppi3.get_non_redu_ppi()

    print

    print "***** File Read Check 2 *****"
    ppi4 = PPi()
    ppi4.read_from_file2(tmp_obj.filename(), 0, 1, "Test")
    ppi4.ppi_display()

    print

    print "***** Hash *****"

    ppi5_hash = ({
    "A": { "B": "ab", "C": "ac" },
    "C": {"C": "cc", "D": "cd"},
    "E": {"F": "ef" }})

    ppi5 = PPi()
    ppi5.read_dict(ppi5_hash)
    ppi5.both_dir()
    ppi5.ppi_display()

    print

    print "***** Hash 2 *****"

    class ValueClass:
        pass
    vct = ValueClass()

    ppi6 = PPi()
    ppi6.read_dict2(ppi5_hash, "TESTING")
    ppi6.ppi_display()
    print ppi6.get_ppi_by_protein_names("A", "B").get_pair()

    print ppi2.get_ppi(proteinA2, proteinB2)
    print ppi6.get_proteins()
    print ppi6.get_protein_names()

    print proteinA
    print "Interactors:"
    print ppi.interactor(proteinA)
    print ppi.interactor_rev(proteinA)
    print "Before:"
    ppi6.ppi_info1()
    ppi6.set_ppi_by_protein_names("A", "X", vct)
    print "After:"
    ppi6.ppi_info1()
    print
    ppi6.ppi_info_rev1()
    # ppi6.ppi_display()
    # ppi6.ppi_cytoscape_simple1()
