#!/usr/bin/env python

from Util_Packages.FindFiles.Findfile_subpr import findfile 
import Util_Packages.FindFiles

"""     
    UNIX => Windows

        % perl -pe 's/\n/\r\n/' unixfile > winfile

    UNIX => Macintosh

        % perl -pe 's/\n/\r/' unixfile > macfile

    Win => UNIX

        % perl -pe 's/\r\n/\n/' winfile > unixfile

    Windows => Macintosh

        % perl -pe 's/\r\n/\r/' winfile > macfile

    Macintosh => UNIX

        % perl -pe 's/\r/\n/g' macfile > unixfile

    Macintosh => Windows

        % perl -pe 's/\r/\r\n/g' macfile > winfile
"""

def retconv(rfilename, wfilename, retcode = "\n"):
    
    fr = open(rfilename, "r")
    fw = open(wfilename, "w")
    for line in fr:
        wline = line.rstrip()
        wline += retcode
        fw.write(wline)
    fw.close()
    fr.close()

if __name__ == "__main__":

    import sys

    retcode = "\r\n"
    spath = sys.argv[1]
    match_filename = sys.argv[2]

    files = findfile(spath, match_filename)
    
    for file in files:
        wfile = file + ".txt"
        retconv(file, wfile, retcode)
        
