#!/usr/bin/env python

# Try:
# globals()["RIN"] = "TARO"
# print RIN
# but not officially guaranteed.

import sys
import os
import re

PYTHONPATH_KEY1 = re.compile(r'^PYTHONPATH[-_]{0,1}\d*$')
PYTHONPATH_KEY2 = re.compile(r'^PYTHONPATH#.*$')
PYTHONPATH_SEP  = re.compile(r'[,:;]')

class RSC:
    def __init__(self, config_file, set_path = False):
        match_pattern = re.compile(r'\s+')
        fh = open(config_file, "r")
        for line in fh:
            line2 = match_pattern.sub(r'\t', line)
            if line2.isspace():
                continue
            if line2[0] == "#":
                continue
            varname = line2.split("\t")[0]
            value = line2.split("\t")[1]
            self.__dict__[varname] = value

        fh.close()

        if set_path:
            self.set_pythonpath()

    def set_pythonpath(self):
        for keystr in self.__dict__:
            if (PYTHONPATH_KEY1.search(keystr) or
                PYTHONPATH_KEY2.search(keystr)):
                paths = PYTHONPATH_SEP.split(self.__dict__[ keystr ])
                for path in paths:
                    if path not in sys.path:
                        sys.path.append(path)


class RSC_II(RSC):
    def __init__(self, config_file, set_path = False):
        config_file_abs = (os.environ.get("PYTHON_RS_CONFIG") +
                           "/" + config_file)
        RSC.__init__(self, config_file_abs, set_path)


if __name__ == "__main__":
    import TmpFile
    tmpfile_obj = TmpFile.TmpFile_II("""

# This is a test configuration.

DATE     June_2_2007
PLACE    Shonandai
NAME     Rintaro

PYTHONPATH   /TMP/rin
PYTHONPATH1  /TMP/rin1,/TMP/rin2
PYTHONPATH-2 /TMP/rsaito;/TMP/tmp
PYTHONPATH#Usefuls /TMP/Usefuls1,/TMP/Usefuls2
PYTHONPATH_IGNORED /TMP/dummy

""")


    rsc = RSC(tmpfile_obj.filename())
    print rsc.NAME
    print rsc.PLACE
    print rsc.DATE
    print sys.path

    rsc = RSC_II("rsIVV_Config")
    print sys.path



