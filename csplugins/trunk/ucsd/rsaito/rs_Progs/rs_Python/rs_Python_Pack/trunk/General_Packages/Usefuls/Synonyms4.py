#!/usr/bin/env python

import sys
import os
import cPickle

from Data_Struct.DictSet1 import DictSet

class Synonyms:
    def __init__(self,
                 filename,
                 case_mode = True,  # case_mode: case distinguish mode
                                    # Once you change this parameter, file denoted by
                                    # filename_binary must be erased. 
                 filename_base_binary = None):
        
        self._case_mode       = case_mode
        self._filename        = filename
        self._syn_table       = DictSet()

        if not filename_base_binary:
            filename_base_binary = self._filename
            
        if self._case_mode:
            mode_str = ""
        else:
            mode_str = "_uppr"
            
        self._filename_binary = filename_base_binary + "_syno" + mode_str + ".pickle"
        
        self.load_data()

        self._main_term       = {}
        for vals in self._syn_table.values():
            for val in vals:
                self._main_term[ self.ccs(val) ] = ""


    def get_filename(self):
        return self._filename

    def get_filename_binary(self):
        return self._filename_binary


    def ccs(self, term):
        """ Considers case mode """
        if self._case_mode:
            return term
        else:
            return term.upper()

    def append(self, syno_term, main_term):
        """ key: syno_term, value: main_term """
        self._syn_table.append(syno_term, main_term) 

    def load_data(self):
        
        filename_binary = self.get_filename_binary()

        if os.path.isfile(filename_binary):
            sys.stderr.write("Reading from binary file (%s)\n" % filename_binary)
            fh = open(filename_binary, "rb")
            self._syn_table = cPickle.load(fh)
        else:
            self.read_file()
            fh = open(filename_binary, "wb")
            cPickle.dump(self._syn_table, fh, True)


    def read_file(self):
        
        filename = self.get_filename()
        
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
                    self.append(syno_term, main_term) # key: syno_term, value: main_term
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

1  One   Ichi Hito Hajime
2  Two   Ni   Futa
3  Three San  Mi
4  Four  Yon  Shi
5  Five  Go   Itsu
6  Six   Roku Mu   Complete
28 Tw8   Complete
""")
    
    syn = Synonyms(syno_test.filename(), True)
    
    print "Dictionary structure:"
    print syn._syn_table
    print syn.to_main("Three"), syn.to_main("three")
    print syn.to_main_force("Ten")
    print syn.to_main_force("Complete")
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
    
    syn2 = Synonyms(syno_test.filename(), True, "/tmp/tmp_pickle123")
    print syn2._syn_table
    