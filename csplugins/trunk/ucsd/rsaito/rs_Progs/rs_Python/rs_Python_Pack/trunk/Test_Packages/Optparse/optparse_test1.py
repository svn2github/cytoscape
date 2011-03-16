#!/usr/bin/env python

from optparse import OptionParser

def optparse_test():
    """ You can try several options:

./optparse_test1.py -v -f file1
./optparse_test1.py -v -f file1 main_param
./optparse_test1.py -v -f file1,file2 main_param
./optparse_test1.py --help

"""

    usage = "usage: %prog [options] arg"
    parser = OptionParser(usage)
    parser.add_option("-f", "--file", dest="filename",
                      help="Read data from FILENAME")
    parser.add_option("-v", "--verbose",
                      action="store_true", dest="verbose")
    parser.add_option("-q", "--quiet",
                      action="store_false", dest="verbose")
    parser.add_option("-p", "--param", dest="parameter",
                      help="Set Parameters", default="DefaultParam")
    parser.add_option("--param2", dest="parameter2",
                      help="Set Parameters #2", default="DefaultParam")

    (options, args) = parser.parse_args()
    
    print "Options:", options
    print "Args   :", args

if __name__ == "__main__":
    optparse_test()

    
