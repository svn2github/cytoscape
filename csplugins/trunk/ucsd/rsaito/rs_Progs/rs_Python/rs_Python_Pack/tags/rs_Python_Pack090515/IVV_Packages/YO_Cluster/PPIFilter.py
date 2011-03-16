#!/usr/bin/env python

import IVV_Packages.YO_Usefuls.TabFileReader as TabFileReader
import sys

class PPIFilter:
    def __init__( self,filename):
        sys.stdout.write("create PPI filter\n")
        self.ppiDic = {}
        tabFile = TabFileReader.Tabfile(filename)
        while True:
           line = tabFile.readline()
           if line == False:
               break
           else: 
               p1 =line[0]
               p2=line[1]
               self._putDictionary(p1, p2)
               
    def _putDictionary(self,p1,p2):
        if p1 in self.ppiDic:
            self.ppiDic[p1][p2]=1.0
        else:
            self.ppiDic[p1]={p2:1.0}
        if p2 in self.ppiDic:
            self.ppiDic[p2][p1]=1.0
        else:
            self.ppiDic[p2]={p1:1.0}
    def isExist(self,p1,p2):
        #sys.stderr.write(str(p1)+","+str(p2)+"is exist\n")
        
        if p1 in self.ppiDic:
            if p2 in self.ppiDic[p1]:
               # print p1,p2,"exists"

                return True
        #print p1,p2,"doesnt exist"
        #return True
        return False
        
               