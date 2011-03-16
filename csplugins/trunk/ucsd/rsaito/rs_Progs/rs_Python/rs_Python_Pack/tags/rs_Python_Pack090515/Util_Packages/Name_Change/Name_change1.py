#!/usr/bin/env python

import sys
import os

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsPack_Config")

class Name_changer:
    def __init__(self, filename):
        self.filename = filename
        fh = open(self.filename, "r")
        self.lines = []
        for line in fh:
            self.lines.append(line.rstrip())
        fh.close()
        self.replacement = False

    def get_replacement_status(self):
        return self.replacement

    def get_around(self, l, pnn):

        ret = ""
        start = l - pnn
        if start < 0: start = 0
        end = l + pnn + 1
        if end > len(self.lines):
            end = len(self.lines)
        for i in range(start, end):
            if i == l:
                ret += self.lines[i] + " <---*\n"
            else:
                ret += self.lines[i] + "\n"

        return ret

    def scan_lines(self, pat, newpat):
        for i in range(len(self.lines)):
            if self.test_pat(pat, self.lines[i]):
                if self.ask_replacement(pat, newpat, i):
                    self.lines[i] = \
                                  self.replace_pat(pat, newpat, self.lines[i])
                    self.replacement = True

    def ask_replacement(self, pat, newpat, i):
                        
        print '\n*** Pattern found in "' + self.filename +  '" ***\n'
        print self.get_around(i, 2)
        print "Would you like to replace the line as follows:\n"
        print self.lines[i]
        print self.replace_pat(pat, newpat, self.lines[i]), "\n"
        uinput = raw_input("(y/n)? ")
        if uinput[0] == "y" or uinput[0] == "Y":
            return True
        else:
            return False


    def replace_pat(self, pat, newpat, line):
        return line.replace(pat, newpat)


    def test_pat(self, pat, line):
        if pat in line:
            return True
        else:
            return False

    def __del__(self):
        if self.replacement is True:
            fw = open(self.filename, "w")
            fw.write("\n".join(self.lines) + "\n")


class Name_changer_II(Name_changer):
    def scan_lines(self, search_pat, old_pat, new_pat):
        for i in range(len(self.lines)):
            if (self.test_pat(search_pat, self.lines[i]) and 
                self.test_pat(old_pat, self.lines[i])):
                if self.ask_replacement(old_pat, new_pat, i):
                    self.lines[i] = \
                                  self.replace_pat(old_pat,
                                                   new_pat,
                                                   self.lines[i])
                    self.replacement = True
            elif self.test_pat(search_pat, self.lines[i]):
                self.disp_match_info(search_pat, i)

    def disp_match_info(self, pat, i):
                        
        print '\n*** Pattern found in "' + self.filename +  '" ***\n'
        print self.get_around(i, 2)
        print "Input any string to continue:"
        uinput = raw_input("(Blank OK) ")
        

def change_names(tab_args, dirname, dirfilenames):

    fext, pat, newpat = tab_args.split("\t")

    logfile = "%s/repl_log_%s" % (rsc.Name_Changer_Log_DIR,
                                  `os.getpid()`)
    
    fw = open(logfile, "a")

    for dirfilename in dirfilenames:
        cpath = os.path.join(dirname, dirfilename)
        if (os.path.isfile(cpath) and
            len(dirfilename) >= len(fext) and
            dirfilename[-len(fext):] == fext):
            print "Looking", cpath
            nc = Name_changer(cpath)
            nc.scan_lines(pat, newpat)
            if nc.get_replacement_status():
                fw.write(cpath + "\n")

    fw.close()

    
def change_names_II(tab_args, dirname, dirfilenames):

    fext, srcpat, oldpat, newpat = tab_args.split("\t")
    
    logfile = "%s/repl_log_%s" % (rsc.Name_Changer_Log_DIR,
                                  `os.getpid()`)
    
    fw = open(logfile, "a")

    for dirfilename in dirfilenames:
        cpath = os.path.join(dirname, dirfilename)
        if (os.path.isfile(cpath) and
            len(dirfilename) >= len(fext) and
            dirfilename[-len(fext):] == fext):
            print "Looking", cpath
            nc = Name_changer_II(cpath)
            nc.scan_lines(srcpat, oldpat, newpat)
            if nc.get_replacement_status():
                fw.write(cpath + "\n")

    fw.close()


if __name__ == "__main__":
    # ./Name_change1.py ./TESTDIR ".py" "Before_pat" "After_pat" 

    # nc = Name_changer("testfile")
    # nc.scan_lines("pen", "pencil")

    if len(sys.argv) == 1:
        spath  = raw_input("Enter path to search (ex. ../../DIR1): ")
        fext   = raw_input("Enter file ext to search (ex. .py)   : ")
        pat    = raw_input("Enter pattern to replace (ex. Old)   : ")
        newpat = raw_input("Enter new pattern (ex. New pat.)     : ")
    elif len(sys.argv) == 5:
        spath = sys.argv[1]
        fext = sys.argv[2]
        pat = sys.argv[3]
        newpat = sys.argv[4]
    else:
        raise "Input parameter error."

    os.path.walk(spath, change_names, "\t".join((fext, pat, newpat)))

    
