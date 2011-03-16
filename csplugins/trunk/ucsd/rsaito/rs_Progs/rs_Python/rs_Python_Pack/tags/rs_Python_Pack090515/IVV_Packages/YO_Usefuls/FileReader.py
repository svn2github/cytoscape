#!/usr/bin/env python

import sys
import re
import string
import GeneID2GeneName

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")
yoc = RSC_II("yoIVV_Config")

class Tabfile:
       def __init__( self, filename ,deliminator = "\t"):
           self.filename = filename
           self.fh = open( self.filename, "r" )
           self.deliminator = deliminator
       def readline( self ):
           line = self.fh.readline()
           
           if line == "":
               return False
           else:
               
               return line.split( self.deliminator )
       def __del__( self ):
           self.fh.close()
class HPRDComplexfile:
       def __init__( self, filename ):
           self.filename = filename
           self.fh = open( self.filename, "r" )
       def readline( self ):
           line = self.fh.readline()
           if line == "":
               return False
           else:
               return line.split( "#" )
       def __del__( self ):
           self.fh.close()
class TFFile:
       def __init__( self, filename ):
           self.filename = filename
           self.fh = open( self.filename, "r" )
       def readline( self ):
           line = self.fh.readline()
           id = re.sub("\n","", line)
           if line == "":
               return False
           else:
               return id
       def __del__( self ):
           self.fh.close()

class FileReader:
    def __init__(self):
        self.geneIndex2geneIDDict ={}
        self.geneIndexSet =[]
        self.geneIDSet=[]
        self.geneID2proteinNameDict ={}
        self.geneID2proteinNameAndDescription ={}
        self.unknownSetFromHDList =set()
        self.hprdTransDic = {}
        
        self.pulldownDic ={}
        
    def readBINDFile(self, filename ):
       complexDic={}
       resultFile = Tabfile( filename )
       
       while True:
           tabline = resultFile.readline()
           if tabline == False:
               break
           
           else: 
               geneIDSet = set()    
               complexID= tabline[1]
               length = len(tabline)
               for i in range(3,length):
                   id = re.sub("\n","", tabline[i])
                   geneIDSet.add(id)
               complexDic[complexID] = geneIDSet
       return complexDic
    
    def getMIPSComplexDicForHuman(self):
        translator =GeneID2GeneName.TranslateGeneID2GeneName()
        translator.readGeneInfoFile()
        
        complexFile = Tabfile(yoc.MIPS_Complex)
        complexDic={}
        count = 1
        while True:
            tabline = complexFile.readline()
            if tabline == False:
                break
            else: 
                geneIDSet = set()
                organism = tabline[3]
                geneNames = tabline[5].split(",")
                for geneName in geneNames:
                    geneID=translator.reverseTranslate(geneName)
                    geneIDSet.add(geneID)
                if organism =="Human" and len(geneIDSet) > 2:
                    complexDic[count] = geneIDSet
                    count+=1
                    
        return complexDic    
    def getMIPSComplexDicForYeast(self):
        translator =GeneID2GeneName.TranslateGeneID2GeneName()
        translator.readGeneInfoFile()
        
        complexFile = Tabfile(yoc.MIPS_Complex)
        complexDic={}
        count = 1
        while True:
            tabline = complexFile.readline()
            if tabline == False:
                break
            else: 
                geneIDSet = set()
                organism = tabline[3]
                geneNames = tabline[5].split(",")
                for geneName in geneNames:
                    geneID=translator.reverseTranslate(geneName)
                    geneIDSet.add(geneID)
                if organism =="Yeast" and len(geneIDSet) > 2:
                    complexDic[count] = geneIDSet
                    count+=1
                    print count,geneIDSet
                    
        return complexDic                      
    
    def getHPRDComplexDic(self):
        resultFile = Tabfile(yoc.HPRD_Complex_Info)
        doneSet = set()
        complexDic={}
        while True:
            tabline = resultFile.readline()
            if tabline == False:
                break
           
            else: 
                geneIDSet = set()    
                complexID= tabline[0]
                length = len(tabline)
                
                for i in range(1,length):
                    id = re.sub("\n","", tabline[i])
                    geneIDSet.add(id)
                    
                fSet = frozenset(geneIDSet)
                if fSet not in doneSet:
                    complexDic[complexID] = geneIDSet
                    doneSet.add(fSet)
                
        return complexDic
    def _addYeastComplexDic(self,id,core,atatches,dic,translator):
        idSet = set()
        for mem in core:
            if mem:
                tmp = mem.upper()
                query=tmp.replace("\"","")
                geneID = translator.reverseTranslate(query)
                idSet.add(geneID)
        count = 0
        for mem in atatches:
            
            if mem and not count == 0 and mem !="\"":
                tmp = mem.upper()                
                query=tmp.replace("\"","")
                geneID = translator.reverseTranslate(query)
                idSet.add(geneID)
            count +=1
            
        dic[id]=idSet
            
        
        
    def getYeastComplexDic(self):
        dic ={}
        file = Tabfile(yoc.Suppl2,"\r")
        translator =GeneID2GeneName.TranslateGeneID2GeneName()
        translator.readGeneInfoFile()
        
        while True:
            lines = file.readline()
           
            if lines == False:
                break  
            else:
                baseID=0
                core = []
                complexName =""
                for line in lines:
                    tmp = line.split("\t")
                    if tmp[0].isdigit():
                        baseID= tmp[0]
                        complexName = tmp[1]
                        core = tmp[2].split(" ")
                        if not tmp[3].isspace():
                            atatches = tmp[3].split(" ")
                            complexID = baseID +"-" +atatches[0].replace("\"","")
                            self._addYeastComplexDic(complexID, core, atatches, dic,translator)
                        else:
                            atatches =[]
                            complexID = baseID + "-0"
                            self._addYeastComplexDic(complexID, core, atatches, dic,translator)
                            
                    else:
                        atatches = line.split(" ")
                        complexID = baseID + "-"+ atatches[0].replace("\"","")
                        self._addYeastComplexDic(complexID, core, atatches, dic,translator)
        keys = dic.keys()
        keys.sort()
        #for id in keys:
        #    print id,dic[id]
        return dic
    
                    
    def getHPRDComplexDic_old(self):
        self.readHPRDIDTransDic()
        #accessionFile = Tabfile( "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/HPRDID2geneID.out"  )
        accessionFile = HPRDComplexfile( "../data/psimi_single_final.xml_cpx"  )
        
        
        hprdComplexDic={}
        while True:
           line = accessionFile.readline()
           if line == False:
               break  
           else:
               tmpLine = line[0].split( "\t" )
               i = 0

               geneIDs =[]
               for id in tmpLine:
                   if  i ==0:
                       
                       complexID = id
                       #print complexID
                   else:
                       #print complexID
                       if id in self.hprdTransDic :
                           
                           geneID = self.hprdTransDic[id]
                           #print id, geneID
                           geneIDs.append(geneID)
                   i +=1
               if len(geneIDs)>2:
                   hprdComplexDic[complexID] = geneIDs
               print tmpLine
               print geneIDs
                   #print complexID,geneIDs
        #print len(hprdComplexDic)
        return hprdComplexDic
    def readHPRDIDTransDic(self):
        accessionFile = Tabfile( "../data/HPRDID2geneIDHs.out"  )
        while True:
           line = accessionFile.readline()
           if line == False:
               break  
           else:
               hprdID= line[0].replace( "_9606","" )
               geneID = line[1].rstrip()
               self.hprdTransDic[hprdID] = geneID
               
    def readGeneAccession( self):
        accessionFile = Tabfile( rsc.Gene2Accession  )
       # accessionFile = Tabfile( "../data/testAccession"  )
        
        while True:
           line = accessionFile.readline()
           if line == False:
               break  
           else:
               if len(line) > 6:
                   geneIndex = line[6]
    
                   geneID = line[1]
                   self.geneIndex2geneIDDict[geneIndex] = geneID
                   self.geneIDSet.append(geneID)
    def readHDList(self):
        infoFile = Tabfile( "../data/HsIDList20060510.txt"  )
        #infoFile = Tabfile( "../data/test_info"  )
        while True:
           line = infoFile.readline()

           if line == False:
               break  
           else:
               geneID = line[0]
               #geneIDSet = self.geneIndex2geneIDDict.values
    
               proteinName = line[1]
               if len(line) >2:
                   description =line[2]
                   tmp = string.join([line[1],line[2]]," ")
               else:
                   tmp = line[1]
               self.geneID2proteinNameAndDescription[geneID] = tmp   
    def readUnknownHDList(self):
        infoFile = Tabfile( "C:/home/yo/Python3/data/unknownListFromHsIDList.out"  )
        #infoFile = Tabfile( "../data/test_info"  )
        while True:
           line = infoFile.readline()
           if line == False:
               break  
           else:
               geneID = line[0]

               self.unknownSetFromHDList.add(geneID)
    def readGeneInfo( self ):
        infoFile = Tabfile( "../data/gene_info_hs_sc"  )
        #infoFile = Tabfile( "../data/test_info"  )
        while True:
           line = infoFile.readline()
           if line == False:
               break  
           else:
               if len(line)>8:
                   geneID = line[1]
                   #geneIDSet = self.geneIndex2geneIDDict.values
    
                   proteinName = line[8]
                   self.geneID2proteinNameDict[geneID] = proteinName
                   
    def readVerifiedComplexDic(self,fileName):
        infoFile = Tabfile( fileName ,",")
        complexDic ={}
        count =0
        doneSet =set()
        while True:
            line = infoFile.readline()     
            if line == False:
                break  
            else:
                geneIDs = line[0].split(" ")
                geneIDSet = set()
                for geneID in geneIDs:
                    if geneID.isdigit():
                        geneIDSet.add(geneID)
                fSet = frozenset(geneIDSet)
                if fSet not in doneSet and len(fSet)>0:
                    count +=1
                    #print count,fSet

                    complexDic[count]=geneIDSet
                    doneSet.add(fSet)
        return complexDic



    def readMcodeResult(self,fileName):
        tabFile=Tabfile(fileName)
        complexDic ={}
        while True:
            line = tabFile.readline()
            if line == False:
                break  
            else:
                if line[0].isdigit():
                    complexID=line[0]
                    numProteins=line[2]
                    tmp = line[4].replace("\n","")
                    tmp = tmp.replace(" ","")
                    geneIDLine = tmp.split(",")
                    geneIDSet = set()
                    if len(geneIDLine) <= 20:
                        for geneID in geneIDLine:
                            if geneID.isdigit():
                                geneIDSet.add(geneID)
                        complexDic[complexID] =geneIDSet
        return complexDic
    def readMCLResult(self,filename):
        #sys.stderr.write("Read clusters from MCL result file: "+filename+"\n")
    
        tabFile=Tabfile(filename)
        line = tabFile.readline()
        complexID=0
        resultDic={}
        while True:
            line = tabFile.readline()
            if line == False:
                break  
            else:
                if line[0].isdigit():
                    geneIDSet = set()
    
                    complexID+=1
                    if len(line) <= 20 and len(line)>2:
                        for tmp in line:
                            geneID =tmp.replace("\n","")
                            if geneID.isdigit():
                      
                                geneIDSet.add(geneID)
                        resultDic[complexID] =geneIDSet
        return resultDic
    
    def readCliqueResult(self,filename):
        tabFile = Tabfile(filename)
        clusterMap ={}
        while True:
            line = tabFile.readline()
            #sys.stderr.write(str(line)+"\n")
      
            if line == False:
                break
            else: 
                clusterID = line[0]
                length = len(line)
                proteinSet = set()
                for i in range(length):
                    if i != 0:    
                        geneID =line[i].replace("\n","")    
                        if geneID.isdigit():
                            proteinSet.add(geneID)
                
                clusterMap[clusterID]=proteinSet
        return clusterMap
    def readCCResult(self,filename):
    
        tabFile=Tabfile(filename)
        line = tabFile.readline()
        complexID=0
        resultDic={}
        while True:
            line = tabFile.readline()
            if line == False:
                break  
            else:
                if line[0].isdigit():
                    geneIDSet = set()
    
                    complexID+=1
                    if len(line) <= 20 and len(line)>2:
                        for tmp in line:
                            geneID =tmp.replace("\n","")
                            if geneID.isdigit():
    
                                geneIDSet.add(geneID)
                        resultDic[complexID] =geneIDSet
        return resultDic
    def readPulldown(self):
        infoFile = Tabfile( "C:/home/yo/Python3/data/PullDownSheet.txt"  )
        #infoFile = Tabfile( "../data/test_info"  )
        while True:
           line = infoFile.readline()
           if line == False:
               break  
           else:
               pbresult = line[2]
               ppresult = line[3]
               pullresult = line[4]
               if pbresult =="OK":
                   if ppresult !="NG" or ppresult == "ND":
                       if pullresult !="NG" or ppresult == "ND":

                    
                   #if ppresult =="OK" or ppresult == :
                    #   if pullresult =="OK" or ppresult == '':
                       
                           geneID1 = line[0]
                           geneID2 = line[1]
                           if geneID1 in self.pulldownDic:
                               self.pulldownDic[geneID1][geneID2] = "OK"
                           else:
                               self.pulldownDic[geneID1]={geneID2: "OK"}
                           
                           if geneID2 in self.pulldownDic:
                               self.pulldownDic[geneID2][geneID1] = "OK"
                           else:
                               self.pulldownDic[geneID2]={geneID1: "OK"}
                                 
               #geneIDSet = self.geneIndex2geneIDDict.values

               
class IDTranslater :

    def translate(self, geneIndex2geneIDDict,complexDic):
        translatedDic = {}
        for complexID in complexDic:
            geneIndexSet = complexDic[complexID]
            geneIDSet =set() 
            counter =0
            for geneIndex in geneIndexSet:
                if geneIndex in geneIndex2geneIDDict:
                    geneIDSet.add(geneIndex2geneIDDict[geneIndex])
                else:
                    counter+=1
                    #print "ERROR:",geneIndex,"is not in the dic"
            #if len(geneIDSet) == len(geneIndexSet):
            for i in range(counter):
                seq = string.join(["unknown",str(i)])
                geneIDSet.add(seq)
            translatedDic[complexID] = geneIDSet
        return translatedDic
              
if __name__ == "__main__":
    fileReader = FileReader()
    #fileReader.readGeneAccession()
    #fileReader.getYeastComplexDic()
    #fileReader.getMIPSComplexDicForHuman()
    fileReader.getMIPSComplexDicForYeast()
    
    #dic =fileReader.getHPRDComplexDic()
    #fileReader.readPulldown()
   
    """
    fileReader.readGeneInfo()
    for id in dic:
        tmp= id
        #print id,dic[id]
        for geneID in dic[id]:
            if geneID != "UNKNOWN":
                geneName=fileReader.geneID2proteinNameDict[geneID]
                tmp = string.join([tmp,geneName],"\t")
        print id,tmp
                                    
    """