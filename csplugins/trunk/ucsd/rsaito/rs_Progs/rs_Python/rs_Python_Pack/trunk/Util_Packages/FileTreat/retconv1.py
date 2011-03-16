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
    import os
    import shutil

    import tempfile
    
    from optparse import OptionParser

    usage = "usage: %prog [options]"
    parser = OptionParser(usage)
    parser.add_option("-p", dest = "spath",
                      help = "search path (directory)")
    parser.add_option("-f", dest = "matchfilep",
                      help = "match file name pattern (ex. '*_result')")
    parser.add_option("-r", dest = "retcode",
                      default = "mac",
                      help = "return code (mac or win)")
    parser.add_option("-e", dest = "fext",
                      default = ".txt",
                      help = "file name extention")
    parser.add_option("-d", dest = "delete",
                      action  = "store_true",
                      default = False,
                      help = "delete original files")
    
    (options, args) = parser.parse_args()
    
    spath          = options.spath
    match_filename = options.matchfilep
    
    if not spath:
        raise Exception, "Search path not given."
    if not match_filename:
        raise Exception, "File name pattern not given."
    
    retcode = { "unix" : "\n",
                "mac"  : "\r",
                "win"  : "\r\n" }[ options.retcode ]

    files = findfile(spath, match_filename)

    for file in files:
        if options.fext:
            wfile = file + options.fext
        else:
            wfile = tempfile.mktemp()
        retconv(file, wfile, retcode)
        if options.delete:
            os.unlink(file)
        if not options.fext:
            shutil.copyfile(wfile, file)
            os.unlink(wfile)
            

