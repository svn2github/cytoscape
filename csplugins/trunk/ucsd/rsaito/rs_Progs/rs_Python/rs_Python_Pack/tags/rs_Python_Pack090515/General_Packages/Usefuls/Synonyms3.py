#!/usr/bin/env python

import sys
from Data_Struct.DictSet1 import DictSet

class Synonyms:
    def __init__(self, filename, case_mode = True):

        self._case_mode = case_mode
        
        self._syn_table = DictSet()
        self.read_file(filename)
        self._main_table = {}
        for vals in self._syn_table.values():
            for val in vals:
                self._main_table[ self.ccs(val) ] = ""

    def ccs(self, term):
        """ Considers case mode """        
        if self._case_mode:
            return term
        else:
            return term.upper()

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
                    self._syn_table.append(syno_term, main_term)
        fh.close()
        
    
    def to_main(self, term):
        if self.ccs(term) in self._main_table:
            return [ self.ccs(term) ]
        elif self._syn_table.has_key(self.ccs(term)):
            return self._syn_table[ self.ccs(term) ]
        else:
            return None

    def to_main_force(self, term):
        if self.ccs(term) in self._main_table:
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
