#!/usr/bin/env python

class Synonyms:
    def __init__(self, filename, main_p = 0, 
                 ignore_ps = None, case_mode = True):

        if ignore_ps is None:
            ignore_ps = []

        self._case_mode = case_mode
        self._syn_table = {}
        self.read_file(filename, main_p, ignore_ps, case_mode)

    def read_file(self, filename, main_p = 0,
                  ignore_ps = None, case_mode = True):

        if ignore_ps is None:
            ignore_ps = []
            
        fh = open(filename, "r")
        for rline in fh:
            sline = rline.rstrip()
            if sline[0] == "#":
                continue
            r = sline.split("\t")
            main_term = r[main_p]
            for i in range(len(r)):
                if i not in ignore_ps and r[i]:
                    if case_mode:
                        syno_term = r[i]
                    else:
                        syno_term = r[i].lower()
                    self._syn_table[ syno_term ] = main_term
        fh.close()
    
    def to_main(self, term):

        if self._case_mode is False:
            term = term.lower()
        return self._syn_table.get(term, None)

    def to_main_force(self, term):
        
        if self._case_mode is False:
            term = term.lower()
        return self._syn_table.get(term, term)

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

1 Number Ichi Hito One
2 Number Ni   Futa
3 Number San  Mi Three
A Alphabet e--
B Alphabet bi--

""")
    
    syn = Synonyms(syno_test.filename(), 0, [1], True)
    
    print syn._syn_table
    print syn.to_main("Three")
    print syn.to_main_force("Five")

    syn2 = Synonyms(syno_test.filename(), 2, [1], False)
    print syn2._syn_table
    print syn2.to_main("three")
    print syn2.to_main_force("Five")
    print syn2.get_all_synonyms("San")
