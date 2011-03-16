#!/usr/bin/env python

class Synonyms:
    def __init__(self, filename, case_mode = True):

        self._case_mode = case_mode
        self._syn_table = {}
        self.read_file(filename, case_mode)
        self._main_table = {}
        for val in self._syn_table.values():
            self._main_table[ val ] = ""

    def read_file(self, filename, case_mode = True):
            
        fh = open(filename, "r")
        for rline in fh:
            sline = rline.rstrip()
            if sline[0] == "#":
                continue
            r = sline.split("\t")
            main_term = r[0]
            for i in range(len(r)):
                if r[i] and not r[i].isspace():
                    if case_mode:
                        syno_term = r[i]
                    else:
                        syno_term = r[i].lower()
                    self._syn_table[ syno_term ] = main_term
        fh.close()
    
    def to_main(self, term):
        if term in self._main_table:
            return term

        if self._case_mode is False:
            term = term.lower()
        return self._syn_table.get(term, None)

    def to_main_force(self, term):
        if term in self._main_table:
            return term
        
        if self._case_mode is False:
            term_i = term.lower()
        else:
            term_i = term
            
        return self._syn_table.get(term_i, term)

    def get_all_synonyms(self, term):
        if self._case_mode is False:
            term = term.lower()
        main_term = self.to_main_force(term)
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

""")
    
    syn = Synonyms(syno_test.filename(), True)
    
    print syn._syn_table
    print syn.to_main("Three")
    print syn.to_main_force("Five")

    syn2 = Synonyms(syno_test.filename(), False)
    print syn2._syn_table
    print syn2.to_main("three")
    print syn2.to_main_force("Five")
    print syn2.get_all_synonyms("San")
    print syn2.to_main_force("3")
