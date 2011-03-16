#!/usr/bin/python

import sys
import string
import Hash

class Hash_headf(Hash.Hash):
    def read_file(self, filename, Key_cols_hd, Val_cols_hd):

        fh = open(filename, 'r')

	header_line = fh.readline()
	header_c = string.rstrip(header_line)
	header = header_c.split("\t");

	Key_cols = []
	for Kch in Key_cols_hd:
	    Key_cols.append(header.index(Kch))
	Val_cols = []
	for Vch in Val_cols_hd:
	    Val_cols.append(header.index(Vch))

	count = 0
        for line in fh.readlines():
            linec = string.rstrip(line)
            r = linec.split("\t")
	    if self.filt_line(r): continue
	    if self.verbose: print "Reading line #" + `count`, linec
            keys = []
            for col in Key_cols: keys.append(r[col])
            key = string.join(keys, "\t")
	    vals = []
	    for col in Val_cols: vals.append(r[col])
	    val = string.join(vals, "\t")
            if self.get_val_type() == "S": # Scalar
		self._Hash__set_data(key, val)
            elif self.get_val_type() == "A": # Array
		self._Hash__push_data(key, val)
            elif self.get_val_type() == "L": # Scalar, row as value
		self._Hash__set_data(key, string.join(r[Val_cols[0]:], "\t"))
	    elif self.get_val_type() == "N": # Scalar, null
		self._Hash__set_data(key, "")
            else:
                raise "Illegal option", val_type
	    count = count + 1

        fh.close()

if __name__ == "__main__":
    hhf = Hash_headf("S")
    hhf.read_file(filename = sys.argv[1],
		  Key_cols_hd = ["Protein A", "Protein B"],
		  Val_cols_hd = ["Comment"])
    print hhf.keys()
    print hhf.has_pair("Yone", "Pekin")
