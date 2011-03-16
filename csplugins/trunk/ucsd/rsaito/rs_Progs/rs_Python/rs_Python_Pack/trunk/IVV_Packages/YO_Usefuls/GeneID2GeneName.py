#!/usr/bin/env python

import string

from Usefuls.rsConfig import RSC_II
yoc = RSC_II("yoIVV_Config")

class Tabfile:
       def __init__( self, filename ):
           self.filename = filename
           self.fh = open( self.filename, "r" )
       def readline( self ):
           line = self.fh.readline()
           if line == "":
               return False
           else:
               return line.split( "\t" )
       def __del__( self ):
           self.fh.close()

class TranslateGeneID2GeneName:
    def __init__(self):
        self.geneNameDic={}
        self.geneIDDic={}
        self.secondGeneIDDic={}
        self.thirdGeneIDDic={}
    
        
    def readGeneInfoFile(self):
        infoFile = Tabfile( yoc.GeneInfo_hs_sc  )
        #infoFile = Tabfile( "../data/test_info"  )
        infoFile.readline()
        while True:
           line = infoFile.readline()
           if line == False:
               break  
           else:
               taxID = line[0]
               geneID = line[1]
               geneName =line[2]
               secondGeneName = line[3]
               thirdGeneName = line[4]
               #geneIDSet = self.geneIndex2geneIDDict.values
               if taxID == "9606" or taxID =="4932":
                   self.geneNameDic[geneID] = geneName
                   self.geneIDDic[geneName]= geneID
                   self.secondGeneIDDic[secondGeneName]=geneID
                   self.thirdGeneIDDic[thirdGeneName]=geneID
    def translate(self,geneID):
        if geneID in self.geneNameDic:
            return self.geneNameDic[geneID]
        else:
            return "UNKNOWN"
    
    def reverseTranslate(self,geneName):
        if geneName in self.geneIDDic:
            return self.geneIDDic[geneName]
        elif geneName in self.secondGeneIDDic:
            return self.secondGeneIDDic[geneName]
        elif geneName in self.thirdGeneIDDic:
            return self.thirdGeneIDDic[geneName]
        else:
            return "UNKNOWN"
        
    def printDic(self):
        print "canonicalName"

        for geneID in sorted(self.geneNameDic):
            geneName = self.geneNameDic[geneID]
            print string.join([geneID,geneName], " = ")
 
if __name__ == "__main__":
    translater = TranslateGeneID2GODescription()
    translater.readGeneInfoFile()
    
    translater.printDic()
