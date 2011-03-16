#!/usr/bin/env python

from KeyPattern import *

class Header:
    def __init__(self):
        self.lines = []
        self.locus = None
        self.accession = None

    def reader(self, fh):
        while True:
            line = fh.readline()
            if(line == "" or
               line.startswith(basecount) or
               line.startswith(entry_end)):
                return None

            if line.startswith(key_locus):
                break

        self.locus = line.rstrip()
        self.lines.append(line.rstrip())

        while True:
            line = fh.readline()
            if line.startswith(key_acc):
                self.accession = line[12:].rstrip()
            if line.startswith(feastart):
                fh.seek(-len(line), 1)
                break
            self.lines.append(line.rstrip())

        return self.locus

    def get_lines(self):
        return self.lines

if __name__ == "__main__":

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("GenBank")

    header = Header()
    fh = open(rsc.Primates_test, "r")
    header.reader(fh)
    print header.locus
    print header.accession
    print header.get_lines()


