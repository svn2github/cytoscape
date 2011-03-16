#!/usr/bin/env python

import os
import subprocess

def findfile(spath, match):
    cmd = ("find %s -name %s -print" % (spath, match)).split()

    output = subprocess.Popen(cmd, 
                              stdout=subprocess.PIPE).communicate()
    
    dirfiles = output[0].rstrip().split("\n")
    # output[1] harbors error messages.

    files = []
    for dirfile in dirfiles:
        if os.path.isfile(dirfile):
            files.append(dirfile)

    return files


if __name__ == "__main__":
    import sys
    print findfile(sys.argv[1], sys.argv[2])
