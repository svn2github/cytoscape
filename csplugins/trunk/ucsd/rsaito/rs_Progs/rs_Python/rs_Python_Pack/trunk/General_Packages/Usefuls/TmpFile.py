#!/usr/bin/python

import os
import tempfile
import string
import re

def trim_f_line(string):
    first_ret_pos = string.index("\n")
    return string[first_ret_pos + 1:]

def grep_char_line(char, str):
    lines = str.split("\n")
    lines_out = []
    for r in lines:
        if char in r:
            lines_out.append(r)
    return string.join(lines_out, "\n") + "\n"

class TmpFile:
    def __init__(self):
        self.tmpfile = tempfile.mktemp()
        self.fileobj = open(self.tmpfile, "w")

    def fh(self):
        return self.fileobj

    def write(self, istr):
        self.fileobj.write(istr)

    def read_mode(self):
        self.fileobj.close()
        self.fileobj = open(self.tmpfile, "r")

    def __del__(self):
        self.fileobj.close()
        os.remove(self.tmpfile)

def make_TmpFile_obj(delim, text):
    tmpfile_obj = TmpFile()
    tmpfile_obj.fh().write(grep_char_line(delim, text))
    tmpfile_obj.fh().flush()
    tmpfile_obj.read_mode()
    return tmpfile_obj


class TmpFile_II:
    def __init__(self, text, trim_f_line_flag = False):
        self.tmpfile = tempfile.mktemp()
        fileobj = open(self.tmpfile, "w")
        if trim_f_line_flag:
            fileobj.write(trim_f_line(text))
        else:
            fileobj.write(text)
        fileobj.flush()
        fileobj.close()

    def filename(self):
        return self.tmpfile

    def __del__(self):
        os.remove(self.tmpfile)

class TmpFile_III(TmpFile_II):
    def __init__(self, text, tospace = None):
        searc_obj = re.compile("\S")
        subst_obj = re.compile(" +")
        lines = text.split("\n")
        lines_out = []
        for r in lines:
            # print "Reading", r
            # print " " in r
            # print searc_obj.search(r)
            if " " in r and searc_obj.search(r):
                r_out = subst_obj.sub("\t", r)
                if tospace is not None:
                    r_out = r_out.replace(tospace, " ")
                lines_out.append(r_out)

        TmpFile_II.__init__(self,
                            string.join(lines_out, "\n") + "\n")

if __name__ == "__main__":
    tmp_obj = TmpFile_III("""

Apple   Ringo@Oishii
Orange  Mikan

""", tospace = "@")

    print tmp_obj.filename()

    fh = open(tmp_obj.filename(), "r")
    print fh.readlines()

