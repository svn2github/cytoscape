#!/usr/bin/env python

import sys
import string
import Graph_Packages.PPI.PPi2 as PPi2
import GeneID2GeneName
import FileReader

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
           
       
class Verifier:
    def __init__(self):
        self.fileReader = Cluster3ReviewedReader.FileReader()
        self.translatedDic =self.getGeneIDTranslateDic()
        self.geneID2GeneName = GeneID2GeneName.TranslateGeneID2GeneName()
        self.geneID2GeneName.readGeneInfoFile()
        self.compareKnownSetResultDic={}
        self.translatedGeneNameDic={}
        
    def verifyPPI(self,complexProteinSet,ppiDic):
        verifiedPPIDic ={}
        for p1 in complexProteinSet:
            for p2 in complexProteinSet:
                if p1 != p2:
                    if p1 in ppiDic:
                        if p2 in ppiDic[p1]:
                            if p1 not in verifiedPPIDic and p2 not in verifiedPPIDic:
                                verifiedPPIDic[p1] = [p2]
                            else:
                                if p1 in verifiedPPIDic:
                                    if p2 not in verifiedPPIDic[p1]:
                                        verifiedPPIDic[p1].append(p2)
                                elif p2 in verifiedPPIDic:
                                    if p1 not in verifiedPPIDic[p2]:
                                        verifiedPPIDic[p2].append(p1)
        return verifiedPPIDic
                    
    def verifyGeneID(self,complexProteinSet,geneIDDicSet):
        intersection = complexProteinSet.intersection(geneIDDicSet)
        return intersection
    
    def translateGeneID2GeneName(self,complexID,complexProteinSet):
        geneNameSet =set()
        for id in complexProteinSet:
            geneName = self.geneID2GeneName.translate(id)
            geneNameSet.add(geneName)
        self.translatedGeneNameDic[complexID] = geneNameSet
        
    def getGeneIDTranslateDic(self):
        self.fileReader.readGeneAccession()
        self.fileReader.readBINDFile("C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/bind_homo_complex.info")   
        translator = FileReader.IDTranslater()
        translator.translate(self.fileReader.geneIndex2geneIDDict, self.fileReader.complexDic)
        return translator.translatedDic
    def compareKnownComplexSet(self,complexID,clusterSet):

        flag = False
        for id in self.translatedDic:
            intersectionSet = clusterSet.intersection(self.translatedDic[id])
            #if (clusterSet.issubset(self.translatedDic[id]) or self.translatedDic[id].issubset(clusterSet)) and len(intersectionSet)>1:
            if len(intersectionSet)>1:
                flag = True
                if complexID in self.compareKnownSetResultDic:
                    self.compareKnownSetResultDic[complexID].append(id)
                else:
                    self.compareKnownSetResultDic[complexID]=[id]
        return flag
            
class McodeResult:
    def __init__(self):
        self.mcodeResultDic={}
    def readMCODEResult(self,fileName):
        tabFile=Tabfile(fileName)
        line = tabFile.readline()
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
                   for geneID in geneIDLine:
                       geneIDSet.add(geneID)
                   self.mcodeResultDic[complexID] =geneIDSet

          
class PPIDic:
    def __init__(self,ivvPPIFile):
        self.ivvPPIDic = self.readIVVPPIFile(ivvPPIFile)
        self.ncbiDic =self.readNCBIPPIFile()
    def readIVVPPIFile(self,fileName):
        ppi2 = PPi2.PPi2()
        
        ppi2.read_from_file2( fileName, 0, 1, "rin" );
        ppi2.both_dir()
        return ppi2.ppi
    def readNCBIPPIFile(self):
        ppi2 = PPi2.PPi2()
        
        fileName = "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/ppi_ncbi_human.txt"     
        ppi2.read_from_file2( fileName, 0, 1, "rin" );
        ppi2.both_dir()
        return ppi2.ppi

    
class GeneIDDic:
    def __init__(self,ivvPPIFile):
        self.ivvSet =set()
        self.ncbiSet = set()
        self.readIVV(ivvPPIFile)
        self.readNCBI()
        self.tfSet=self.getTFSet()
    def readIVV(self,fileName):
         #fileName = "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/ivvSpoke.txt"     
        
        tabFile=Tabfile(fileName)
        line = tabFile.readline()
        while True:
           line = tabFile.readline()
           if line == False:
               break  
           else:
               geneID1=line[0].replace("\n","")
               geneID2=line[1].replace("\n","")
               self.ivvSet.add(geneID1)
               self.ivvSet.add(geneID2)
               
               
    def readNCBI(self):
        fileName = "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/ppi_ncbi_human.txt"  
        tabFile=Tabfile(fileName)
        line = tabFile.readline()
        while True:
           line = tabFile.readline()
           if line == False:
               break  
           else:
               geneID1=line[0].replace("\n","")
               geneID2=line[1].replace("\n","")
               self.ncbiSet.add(geneID1)
               self.ncbiSet.add(geneID2)
    def getTFSet(self):
        tfFile = FileReader.TFFile("C:/home/yo/Python3/src/rsIVV_Python3/PPI/tf.list")
        tfSet = set()
        while True:
           line = tfFile.readline()
           if line == False:
               break
           else: 
               tfSet.add(line)
        return tfSet
def printCytoscapeAtribute():
    ivvPPIAllFile ="C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/ivvSpoke.txt"     
    ivvPPIBFFile = "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/basicFilterPPIList.out"     
  
    ppiDicClass = PPIDic(ivvPPIAllFile)
    #geneIDDicClass = GeneIDDic()
    #mcodeResult.readMCODEResult("C:/home/yo/result/mcode_ivvSpokeNCBI.out")
    
    print "ivvInteraction"
    for p1 in ppiDicClass.ivvPPIDic:
        for p2 in ppiDicClass.ivvPPIDic[p1]:
            if p1 != p2:
                tmp = string.join([p1,p2]," (pp) ")
                print tmp,"=1"
    """
    
    
    print "TF"
    for id in geneIDDicClass.tfSet:
        print string.join([id,"tf"], " = ")
    diffSet = geneIDDicClass.ivvSet.difference(geneIDDicClass.ncbiSet)
     """
    """
    print "ivv"
    for id in geneIDDicClass.ivvSet:
        print string.join([id,"ivv"], " = ")
"""

    """
    print "intersection"
    intersection = geneIDDicClass.ivvSet.intersection(geneIDDicClass.ncbiSet)
    for id in intersection:
        print string.join([id,"intersection"], " = ")
    """
   
def verifyMcodeResult():
    ivvPPIAllFile ="C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/ivvSpoke.txt"     
    ivvPPIBFFile = "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/basicFilterPPIList.out"     
    ppiDicClass = PPIDic(ivvPPIBFFile)
    geneIDDicClass = GeneIDDic(ivvPPIBFFile);
    mcodeResult = McodeResult()
    mcodeResult.readMCODEResult("C:/home/yo/result/mcode_ivvNCBI2.out")
    #mcodeResult.readMCODEResult("C:/home/yo/result/mcode_ivvSpokeNCBI.out")
    verifier = Verifier()
    
    summaryDic ={}
    total = 0
    hits = 0
    for complexID in sorted(mcodeResult.mcodeResultDic):
        complexProteinSet = mcodeResult.mcodeResultDic[complexID]
        
        ivvGeneIDSet = verifier.verifyGeneID(complexProteinSet, geneIDDicClass.ivvSet)
        ncbiGeneIDSet = verifier.verifyGeneID(complexProteinSet, geneIDDicClass.ncbiSet)

        intersectionSet = ivvGeneIDSet.intersection(ncbiGeneIDSet)
        
        diffivvGeneIDSet =  ivvGeneIDSet.difference(intersectionSet)
        difNCBIGeneIDSet = ncbiGeneIDSet.difference(intersectionSet)
        ivvPPIDic = verifier.verifyPPI(complexProteinSet, ppiDicClass.ivvPPIDic)
        ncbiPPIDic = verifier.verifyPPI(complexProteinSet, ppiDicClass.ncbiDic)
        tfSet = complexProteinSet.intersection(geneIDDicClass.tfSet)
        
        ivvSize = len(ivvGeneIDSet)
        tfSize = len(tfSet)
        verifier.translateGeneID2GeneName(complexID, complexProteinSet)
        if verifier.compareKnownComplexSet(complexID, complexProteinSet):
            hits +=1
            print complexID,"\t",complexProteinSet,"\t",verifier.translatedGeneNameDic[complexID],"\t",tfSize,"\t",tfSet,"\t",ivvSize,"\t",ivvGeneIDSet,"\t",verifier.compareKnownSetResultDic[complexID]
        else:
            print complexID,"\t",complexProteinSet,"\t",verifier.translatedGeneNameDic[complexID],"\t",tfSize,"\t",tfSet,"\t",ivvSize,"\t",ivvGeneIDSet
         
        if ivvSize>0:
            #print "complexID:",complexID,complexProteinSet
            #print "IVVSet:",ivvGeneIDSet
            #print "intersection",intersectionSet
            #print "NCBISet:",ncbiGeneIDSet
            #print "TFSet:",tfSet
            #print
            
            if ivvSize in summaryDic:
                summaryDic[ivvSize]+=1
            else:
                summaryDic[ivvSize]=1
            
            total += 1
        
    print "###summary###"
    for n in summaryDic:
        print string.join([str(n),str(summaryDic[n])],"\t")
    print "total\t",total
    print "hits\t",hits
    

        
def verifyCliuqueResult():
    #ivvPPIAllFile ="C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/ivvSpoke.txt"     
    ivvPPIBFFile = "C:/Documents and Settings/yo/mt/PPI/data/ppiInfo/basicFilterPPIList.out"     
    clusterReader = Cluster3ReviewedReader.Cluster3ReviewedReader()
    clusterReader.readFile()
    geneIDDicClass = GeneIDDic(ivvPPIBFFile);
    verifier = Verifier() 
    hits = 0
    
    for complexID in clusterReader.clusterDic:
        
        complexProteinSet = clusterReader.clusterDic[complexID]
        tfSet = complexProteinSet.intersection(geneIDDicClass.tfSet)
        tfSize = len(tfSet)
        verifier.translateGeneID2GeneName(complexID, complexProteinSet)
        if verifier.compareKnownComplexSet(complexID, complexProteinSet):
            hits +=1
            print complexID,"\t",complexProteinSet,"\t",verifier.translatedGeneNameDic[complexID],"\t",tfSize,"\t",tfSet,"\t",verifier.compareKnownSetResultDic[complexID]
        else:
            print complexID,"\t",complexProteinSet,"\t",verifier.translatedGeneNameDic[complexID],"\t",tfSize,"\t",tfSet
         
    print hits
         
    
if __name__ == "__main__":
 printCytoscapeAtribute()
   #verifyMcodeResult()
   #verifyCliuqueResult()
