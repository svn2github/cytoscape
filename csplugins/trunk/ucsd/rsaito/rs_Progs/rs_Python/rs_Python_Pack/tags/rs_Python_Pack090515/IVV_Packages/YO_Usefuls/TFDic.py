#!/usr/bin/env python

import re

from Usefuls.rsConfig import RSC_II
yoc = RSC_II("yoIVV_Config")

class TFFile:
       def __init__( self, filename ):
           self.filename = filename
           self.fh = open( self.filename, "r" )
       def readline( self ):
           line = self.fh.readline()
           if line == "":
               return False
           else:
               id = re.sub("\n","", line)
               id = re.sub("\r","", id)

               return id
       def __del__( self ):
           self.fh.close()
class TFDic:

    
    def getTFSet(self):
        tfFile = TFFile(yoc.tf_list)
        tfSet = set()
        while True:
           line = tfFile.readline()
           if line == False:
               break
           else: 
               tfSet.add(line)
        return tfSet