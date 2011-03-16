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
space1_match_pattern = re.compile(r'^(\S+)\s+(.*)$')
dir_sep         = re.compile(r'/+')

class RSC:
    def __init__(self, config_file, set_path = False):
        
        self._line_info = []
        
        fh = open(config_file, "r")
        for line in fh:
            match_res = space1_match_pattern.match(line.rstrip())
            if(line and not line.isspace() and not line.startswith('#')
               and match_res):
                line2 = match_res.group(1) + "\t" + match_res.group(2)
                varname  = line2.split("\t")[0]
                value    = line2.split("\t")[1]
                comments = line2.split("\t")[2:]
                self.__dict__[varname] = value
                self._line_info.append((line.rstrip(), varname, value, comments))
            else:
                self._line_info.append((line.rstrip(), None,    None,  None    ))

        fh.close()

        if set_path:
            self.__set_pythonpath()

    def __set_pythonpath(self):
        for keystr in self.__dict__:
            if (PYTHONPATH_KEY1.search(keystr) or
                PYTHONPATH_KEY2.search(keystr)):
                paths = PYTHONPATH_SEP.split(self.__dict__[ keystr ])
                for path in paths:
                    if path not in sys.path:
                        sys.path.append(path)
                        
    def filecopy(self, dst_tgz_file):
        tgz_files = []
        for line_info in self._line_info:
            line, varname, value, comments = line_info
            if varname is not None:
                if os.path.isfile(value):
                    tgz_files.append(value)
                    print "\t".join((varname, value, "\t".join(comments), "# Copied automatically."))
                else:
                    print "\t".join((varname, value, "\t".join(comments)))
                    # Probably just print line is all right.
            else:
                print line
        # print "tar cvfz %s %s" % (dst_tgz_file,
        #                               " ".join(tgz_files))
        os.system("tar cvfz %s %s" % (dst_tgz_file,
                                      " ".join(tgz_files)))
        

class RSC_II(RSC):
    def __init__(self, config_file, set_path = False):
        config_file_abs = (os.environ.get("PYTHON_RS_CONFIG") +
                           "/" + config_file)
        RSC.__init__(self, config_file_abs, set_path)


if __name__ == "__main__":
    import TmpFile
    tmpfile_obj = TmpFile.TmpFile_II("""

# This is a test configuration.

DATE  \t   June_2_2007\tBirthday? Oh, not my birthday.
PLACE \t   Shonandai Just moved.
NAME  \t   Rintaro

PYTHONPATH1  /tmp/rin1,/TMP/rin2
PYTHONPATH-2 /tmp/rsaito;/TMP/tmp
PYTHONPATH#Usefuls /TMP/Usefuls1,/TMP/Usefuls2
PYTHONPATH_IGNORED /TMP/dummy
PATH1   /tmp/test/rsaito_info1\tComment 1
PATH2   /tmp/test/rsaito_info2\tComment 2\tComment2-2
PATH3   /tmp/test/rsaito_info3

""")


    rsc = RSC(tmpfile_obj.filename(), True)
    print rsc.NAME
    print rsc.PLACE
    print rsc.DATE
    print sys.path
    rsc.filecopy("/tmp/testtgz.tgz")




