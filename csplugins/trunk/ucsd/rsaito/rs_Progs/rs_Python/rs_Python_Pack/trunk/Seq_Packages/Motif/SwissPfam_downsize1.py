#!/usr/bin/env python

import sys

class SwissPfam_Reader:
    def __init__( self, ifilename, ofilename = None):
        self.ifilename = ifilename
        self.ofilename = ofilename
        self.fh = open( self.ifilename, "r" )
        self.fw = None

    def output(self, line):
        if self.ofilename:
            if self.fw is None:
                self.fw = open( self.ofilename, "w" )
            output = self.fw.write
        else:
            output = sys.stdout.write
        output(line)

    def downsize(self, keyword):

        flag = False
        while True:
            line = self.fh.readline()
            if line.startswith(">"):
                if line.find(keyword) > 0:
                    flag = True
                else:
                    flag = False
            if flag:
                self.output(line)
            if line == "":
                break
            
if __name__ == "__main__":

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")
    
    spfile = SwissPfam_Reader(rsc.SwissPfam) # , rsc.SwissPfam_Yeast)
    spfile.downsize("YEAST")
    
