#!/usr/bin/env python

import sys
import os
import cPickle

from Data_Struct.DictSet1 import DictSet

class Synonyms:
    def __init__(self, filename, case_mode = True):
        # case_mode: case distinguish mode

        self._case_mode = case_mode
        
        self._syn_table = DictSet()
        self.load_data(filename)
        self._main_term = {}
        for vals in self._syn_table.values():
            for val in vals:
                self._main_term[ self.ccs(val) ] = ""

    def ccs(self, term):
        """ Considers case mode """
        if self._case_mode:
            return term
        else:
            return term.upper()

    def load_data(self, filename):
        
        if self._case_mode is True:
            mode_str = ""
        else:
            mode_str = "_uppr"

        pickle_file = filename + "_syno" + mode_str + ".pickle"
        if os.path.isfile(pickle_file):
            sys.stderr.write("Reading from pickle file (%s)\n" % pickle_file)
            fh = open(pickle_file, "rb")
            self._syn_table = cPickle.load(fh)
        else:
            self.read_file(filename)
            fh = open(pickle_file, "wb")
            cPickle.dump(self._syn_table, fh, True)


    def read_file(self, filename):
        
        fh = open(filename, "r")
        for rline in fh:
            sline = rline.rstrip()
            if sline[0] == "#":
                continue
            r = sline.split("\t")
            main_term = self.ccs(r[0])
            for i in range(len(r)):
                if r[i] and not r[i].isspace():
                    syno_term = self.ccs(r[i])
                    self._syn_table.append(syno_term, main_term) # key: syno_term, value: main_term
        fh.close()
        
    
    def to_main(self, term):
        """ Returns possible main terms as list """
        if self.ccs(term) in self._main_term:
            return [ self.ccs(term) ]
        elif self._syn_table.has_key(self.ccs(term)):
            return self._syn_table[ self.ccs(term) ]
        else:
            return None

    def to_main_force(self, term):
        if self.ccs(term) in self._main_term:
            return self.ccs(term)
        elif self._syn_table.has_key(self.ccs(term)):
            sterms = self._syn_table[ self.ccs(term) ]
            if len(sterms) > 1:
                sys.stderr.write(" ".join(("Warning: Multiple possible synonyms for",
                                           term,
                                           "...",
                                           "(%s)" % ",".join(sterms))) + "\n")
            return sterms[0]
        else:         
            return self.ccs(term)

    def get_all_synonyms(self, term):

        main_term = self.to_main_force(self.ccs(term))
        res = []
        for each_term in self._syn_table:
            if self.to_main(each_term) == main_term:
                res.append(each_term)
        return res


if __name__ == "__main__":
    import TmpFile

    syno_test = TmpFile.TmpFile_III("""

1 Ichi Hito One
2 Ni   Futa
3 San  Mi Three
X 3
A e--
B bi--
C She
See She
""")
    
    syn = Synonyms(syno_test.filename(), True)
    
    print "Dictionary structure:"
    print syn._syn_table
    print syn.to_main("Three"), syn.to_main("three")
    print syn.to_main_force("Five")
    print syn.to_main_force("3")
    print

    syn2 = Synonyms(syno_test.filename(), False)
    print "Dictionary structure:"    
    print syn2._syn_table
    print syn2.to_main("three")
    print syn2.to_main_force("Five")
    print syn2.get_all_synonyms("san")
    print syn2.to_main_force("2"), syn2.to_main_force("3"), syn2.to_main_force("san")
    print syn2.to_main("She")
    print syn2.to_main_force("She")

    syn2 = Synonyms(syno_test.filename(), False)
    print "Dictionary structure:"   
    print syn2._syn_table
    