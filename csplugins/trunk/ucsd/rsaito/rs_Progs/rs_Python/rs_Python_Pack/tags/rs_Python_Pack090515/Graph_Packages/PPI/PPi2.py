#!/usr/bin/env python

import string
import Data_Struct.Hash2 as Hash

A = "A"
S = "S"

class PPi2:
    def __init__(self, mode = S):
	self.ppi = {}
	self.mode = mode

    def get_mode(self):
	return self.mode

    def add_ppi_val(self, p1, p2, val):

	if self.get_mode() != A:
	    raise "Illegal mode:", self.get_mode()

	if p1 in self.ppi:
	    if p2 in self.ppi[p1]:
		self.ppi[p1][p2].append(val)
	    else:
		self.ppi[p1][p2] = [ val ]
	else:
	    self.ppi[p1] = { p2 : [ val ] }

    def set_ppi_val(self, p1, p2, val):

        if p1 in self.ppi:
            if p2 in self.ppi[p1]:
                self.ppi[p1][p2] = val # Overwrite
            else:
                self.ppi[p1][p2] = val
        else:
            self.ppi[p1] = { p2: val }

    def add_ppi_vals(self, p1, p2, vals):

	if self.get_mode() != A:
	    raise "Illegal mode:", self.get_mode()

        for val in vals:
            self.add_ppi_val(p1, p2, val)

    def add_ppis(self, other_ppi):
        if self.get_mode() != other_ppi.get_mode():
	    raise ("Mode mismatch: " +
		   self.get_mode() + " " + other_ppi.get_mode())

        for p1 in other_ppi.ppi.keys():
            for p2 in other_ppi.ppi[p1].keys():
		if self.get_mode() == A:
		    vals = other_ppi.ppi[p1][p2]
		    self.add_ppi_vals(p1, p2, vals)
		else:
		    val = other_ppi.ppi[p1][p2]
		    self.set_ppi_val(p1, p2, val)

    def read_hash_tab(self, hash):
        for pair in hash.keys():
            p1, p2 = pair.split("\t")
	    if self.get_mode() == A:
		self.add_ppi_vals(p1, p2, hash.val(pair))
	    else:
		self.set_ppi_val(p1, p2, hash.val(pair))

    def read_hash_tab2(self, hash, val):
	# Same value val is used.
        for pair in hash.keys():
            p1, p2 = pair.split("\t")
            self.set_ppi_val(p1, p2, val)

    def read_dict(self, dict):
	for p1 in dict.keys():
	    for p2 in dict[p1].keys():
		self.set_ppi_val(p1, p2, dict[p1][p2])

    def read_dict2(self, dict, val):
	# Same value val is used.
	for p1 in dict.keys():
	    for p2 in dict[p1].keys():
		self.set_ppi_val(p1, p2, val)

    def read_from_file(self, filename, col1, col2, valcol):
        h = Hash.Hash(self.get_mode())
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [valcol])

        self.read_hash_tab(h)

    def read_from_file2(self, filename, col1, col2, val):

        h = Hash.Hash("N")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [])
        for pair in h.keys():
            p1, p2 = pair.split("\t")
            self.set_ppi_val(p1, p2, val)

    def both_dir(self):

        work_hash = {}
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
		p12 = p1 + "\t" + p2
		p21 = p2 + "\t" + p1
		if self.get_mode() == A:
		    vals = self.ppi[p1][p2]
		else:
		    vals = [ self.ppi[p1][p2] ]
		for val in vals:
		    if p12 in work_hash:
			work_hash[ p12 ][val] = ""
			work_hash[ p21 ][val] = ""
		    else:
			work_hash[ p12 ] = { val: "" }
			work_hash[ p21 ] = { val: "" }

        for pair in work_hash.keys():
            p1, p2 = pair.split("\t")
	    if self.get_mode() == A:
		vals = work_hash[pair].keys()
		self.set_ppi_val(p1, p2, vals)
	    else:
		val = work_hash[pair].keys()[0]
		self.set_ppi_val(p1, p2, val)

    def get_ppi_val(self, p1, p2):
	if p1 in self.ppi and p2 in self.ppi[p1]:
	    return self.ppi[p1][p2]
	else:
	    return False

    def get_all_ppi(self):
	ppilist = []
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                vals = self.ppi[p1][p2]
		ppilist.append((p1, p2, self.ppi[p1][p2]))
	return ppilist

    def get_non_redu_ppi(self):
        ppilist = []
        done = {}
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
                pair12 = p1 + "\t" + p2
                if pair12 in done: continue
                pair21 = p2 + "\t" + p1
                ppilist.append((p1, p2, self.ppi[p1][p2]))
                done[ pair12 ] = ""
                done[ pair21 ] = ""
        return ppilist

    def get_proteins(self):
	protein_list = {}
	for p1 in self.ppi.keys():
	    for p2 in self.ppi[p1].keys():
		protein_list[ p1 ] = ""
		protein_list[ p2 ] = ""

	return protein_list.keys()

    def interactor(self, p):
	if p in self.ppi:
	    return self.ppi[p].keys()
	else:
	    return []

    def ppi_display(self):
        for p1 in self.ppi.keys():
            for p2 in self.ppi[p1].keys():
		if self.get_mode() != A:
		    val = self.ppi[p1][p2]
		else:
		    val = string.join(self.ppi[p1][p2], ",")
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
Protein-A     Protein-B   a


""")

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

Protein-A     Protein-B   t
Protein-A     Protein-B   u
Protein-B     Protein-A   v
Protein-D     Protein-E   w
Protein-E     Protein-F   x
Protein-A     Protein-B   t


""")

    ppi = PPi2("A")
    ppi.read_from_file(tmp_obj.filename(), 0, 1, 2)

    ppi2 = PPi2("A")
    ppi2.read_from_file2(tmp_obj2.filename(), 0, 1, ["x", "y", "z"])

#    ppi.both_dir()
    ppi.ppi_display()
    print ppi.get_ppi_val("Protein-A", "Protein-B")

    print "\n"

    ppi2.both_dir()
    ppi2.ppi_display()
    print ppi2.interactor("Protein-E")

    print "\n"

    ppi.add_ppis(ppi2)
    ppi.both_dir()
    ppi.ppi_display()

    print "\n"

    print ppi.get_all_ppi()
    print ppi.get_proteins()

    ppi3 = PPi2()
    dict = { "P1": { "P2": "V1", "P3": "V2" }, "P4": { "P6": "V6" }}
    ppi3.read_dict(dict)
    ppi3.read_dict2(dict, "Rin")
    ppi3.ppi_display()
