#!/usr/bin/env python

import sys
import VerifyResult
import Graph_Packages.PPI.PPi2 as PPi2
import GeneID2GO
import string
import TFDic
import GeneID2GeneName
import FileReader
import os

import IVVInfo2Motif
import MMIRevolver2
import PPIFilter

from Usefuls.rsConfig import RSC_II
yoc = RSC_II("yoIVV_Config")

class LocalizationDic:
    def __init__(self):

        fileName= yoc.hprd_Local
        #fileName= "../cluster/tmp/hprdLocalization.out"
        self.primaryDic={}
        self.alternateDic={} 
        infoFile = FileReader.Tabfile( fileName ,"\t")
        count =0
        while True:
            line = infoFile.readline()     
            if line == False:
                break  
            else:
                flag = line[0]
                geneID =line[1]
                localization=line[2]
            
                if flag == "primary":
                    self.primaryDic[geneID]=localization
                elif flag == "alternate":
                    self.alternateDic[geneID]=localization
                #print flag,geneID,localization
class LocalizationCalc:
    def __init__(self,geneIDSet,localDic):
        self.geneIDSet = geneIDSet
        self.scoreDic={}
        self.primaryDic = localDic.primaryDic
        self.alternateDic = localDic.alternateDic
        
    
    def calcLocalizationScore(self):
        """
        for i in range(len(self.geneIDSet)):
            geneID1 =self.geneIDSet[i]
            for j in range(len(self.geneIDSet)-(i+1)):
                geneID2 =self.geneIDSet[j+i+1]
                self.isSameLocalization(geneID1, geneID2)
        """
        for geneID in self.geneIDSet:
            primary = None
            alternate = None
            if geneID in self.primaryDic:
                primary=self.primaryDic[geneID]
            if geneID in self.alternateDic:
                alternate=self.alternateDic[geneID]
                
                if primary in self.scoreDic:
                    self.scoreDic[primary] +=1
                else:
                    self.scoreDic[primary] =1
                if primary != alternate and primary and alternate:
                    if alternate in self.scoreDic:
                        self.scoreDic[alternate] +=1
                    else:
                        self.scoreDic[alternate] =1
            
        bestScore =0
        for k in self.scoreDic:
            if self.scoreDic[k] >bestScore:
                 #print k ,self.scoreDic[k]
                bestScore = self.scoreDic[k]
        
        return (float)(bestScore)/(float)(len(self.geneIDSet))
    def isSameLocalization(self,geneID1,geneID2):
        lgeneID1 =[]
        lgeneID2=[]
        if geneID1 in self.primaryDic:
            lgeneID1.append(self.primaryDic[geneID1])
        if geneID1 in self.alternateDic:
            if lgeneID1[0] !=self.alternateDic[geneID1]:
                lgeneID1.append(self.alternateDic[geneID1])
        if geneID1 in self.primaryDic:
            lgeneID2.append(self.primaryDic[geneID2])
        if geneID2 in self.alternateDic:
            if lgeneID2[0] !=self.alternateDic[geneID2]:
                lgeneID2.append(self.alternateDic[geneID2])  
    
        for l1 in lgeneID1:
            for l2 in lgeneID2:
                if l1 == l2:
                    if l1 in self.scoreDic:
                        self.scoreDic[l1] +=1
                    else:
                        self.scoreDic[l1] =1

def readNonVerifiedResults(localDic):
    mclprefix = "/Users/yo/Documents/workspace/2007python/data/mclResults"
    ccprefix = "/Users/yo/Documents/workspace/2007python/data/ccResults"
    cliqueprefix = "/Users/yo/Documents/workspace/2007python/data/cliqueResults"
    mcodeprefix = "/Users/yo/Documents/workspace/2007python/data/mcodeResults"

    fileList=os.listdir(mcodeprefix)
    for file in fileList:
        scorer = ClusterScorer()
        filename = string.join([mcodeprefix,file],"/")        
        complexDic = scorer.fileReader.readMcodeResult(filename)
    
    
    fileList=os.listdir(mclprefix)
    for file in fileList:
        scorer = ClusterScorer()
        filename = string.join([mclprefix,file],"/")
        complexDic = scorer.fileReader.readMCLResult(filename)
    
    fileList=os.listdir(ccprefix)
    for file in fileList:
        scorer = ClusterScorer()
        filename = string.join([ccprefix,file],"/")
        complexDic = scorer.fileReader.readCCResult(filename)
    
    fileList=os.listdir(cliqueprefix)
    for file in fileList:
        scorer = ClusterScorer()
        filename = string.join([cliqueprefix,file],"/")
        complexDic = scorer.fileReader.readCliqueResult(filename)

def calcLocalizationForExistingMethods(complexDic,localDic):
    for id in complexDic:
        geneIDSet = complexDic[id] 
        score = LocalizationCalc(complexDic,localDic)
        fscore = (float)(score)/(float)(len(geneIDSet))
        print fscore
        
    
def translateGeneDesc(geneIDSet,fileReader):
    tmp = ""
    for geneID in geneIDSet:
        geneName=fileReader.geneID2proteinNameDict[geneID]
        tmp = string.join([tmp,geneName],":")
    tmp2 = tmp.lstrip(":")
    tmp3 = tmp2.replace(",",";")
    return tmp3

def readVerifiedResults(localDic,filereader):
    #prefix = "/Users/yo/Documents/workspace/2007python/cluster/result1208/ivvall"
#    prefix = "/Users/yo/Documents/workspace/2007python/cluster/tmp"
    prefix = "../data/ipResults/"
 
    
    fileList=os.listdir(prefix)
    
    for file in fileList:
        fileName = string.join([prefix,file],"/")
        calcLocalization(fileName,prefix,filereader)
def calcLocalization(fileName,prefix,filereader):
    fh = open(fileName, "r" )
    count =0
    outputDic = {}
    redundantFlag = True
         
    while True:
        line = fh.readline()
        if line == "":
            break  
        else:
            count +=1
            nline =line.replace("\n","")
            
            tabLine = nline.split(",")
            geneIDLine =tabLine[0]
            geneSymbols =tabLine[1]
            originalMember =tabLine[2]
            score =tabLine[3]
            structure =tabLine[4]
            hprdMatch =tabLine[5]
            bindMatch =tabLine[6]
            ivvFlag =tabLine[7]
            tfMember =tabLine[9]
            tfScore =tabLine[8]
            expFlag =tabLine[10]
            
            geneIDs = tabLine[0].split(" ")
            geneIDSet = []
            
            
            for geneID in geneIDs:
                if geneID.isdigit():
                    geneIDSet.append(geneID)
            if len(geneIDSet) >0:
                fset = frozenset(geneIDSet)
      
                geneDesc = translateGeneDesc(geneIDSet, filereader)
                calc =LocalizationCalc(geneIDSet,localDic)
                score =calc.calcLocalizationScore()
                #print geneIDSet,score
                #printline = line.replace("\n","")
                method=fileName.replace(prefix,"")
                method=method.replace("/","")
                method=method.replace(".csv","")
                tmp=string.join([geneIDLine,geneSymbols,geneDesc,str(score),structure,hprdMatch,bindMatch,ivvFlag,tfScore,tfMember,expFlag, str(score),str(len(geneIDSet)),method],",")
                if redundantFlag == True:
                    sys.stdout.write(tmp+"\n")
                else:
                    outputDic[fset] = tmp
                        
            else:
                printline = line.replace("\n","")
                printline = printline.replace(",,",",")
                tmp=string.join([geneID,geneSymbols,"gene description",score,structure,hprdMatch,bindMatch,ivvFlag,tfScore,tfMember,expFlag, "localization","size","method"],",")
                sys.stdout.write(tmp+"\n")
                   
    if len(outputDic)>0:
        for k in outputDic:
            sys.stdout.write(outputDic[k]+"\n")
             
if __name__ == "__main__": 
    localDic = LocalizationDic() 
    filereader = FileReader.FileReader()
    filereader.readGeneInfo() 
    readVerifiedResults(localDic,filereader)
    #readNonVerifiedResults(localDic)
    
    #calcLocalization(sys.argv[1],sys.argv[2],filereader)

    