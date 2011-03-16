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

from Usefuls.rsConfig import RSC_II
yoc = RSC_II("yoIVV_Config")

class ClusterScorer:
    def __init__(self):
        tfDic = TFDic.TFDic()
        self.tfSet = tfDic.getTFSet()
        self.fileReader = FileReader.FileReader()
        self.overlapThreshold = 0.25
        
        self.yeastComplexDic=self.fileReader.getYeastComplexDic()
        self.humanComplexDic=self.getHumanComplexDic()
        self.humanMatchSet = set()
        self.yeastMatchSet =set()
    
    def getHumanComplexDic(self):
        complexDic = {}
        humanComplexSet=set()
        bindDic = self.getGeneIDTranslateBINDComplexDic()
        bindSet = bindDic.values()
        hprdDic = self.fileReader.getHPRDComplexDic()
        hprdSet = hprdDic.values()
        mipsDic = self.fileReader.getMIPSComplexDicForHuman()
        mipsSet = mipsDic.values()
        
        self._getNoRedundantSet(humanComplexSet, bindSet)
        self._getNoRedundantSet(humanComplexSet, hprdSet)
        self._getNoRedundantSet(humanComplexSet, mipsSet)
        count = 1
        
        for geneIDSet in humanComplexSet:
            complexDic[count] = geneIDSet
            count+=1
        return complexDic
    
    def _getNoRedundantSet(self,nrSet,currentSet):
        for geneIDSet in currentSet:
            nrSet.add(frozenset(geneIDSet))
       
    def getGeneID2GeneNameAndDescription(self):
        self.fileReader.readHDList()
        
        return self.fileReader.geneID2proteinNameAndDescription

    

    def ivvScore(self,geneIDSet):
        ivvScore = 0
        geneIDList = list(geneIDSet)
        for i in range(len(geneIDList)):
            geneID1 = geneIDList[i]
            for j in range(len(geneIDList)-(i+1)):
                geneID2 = geneIDList[j+i+1]
                if geneID1 != geneID2:
                
                    if geneID1 in self.ivvppiDic:
                        if geneID2 in self.ivvppiDic[geneID1]:
                            ivvScore +=1
        return ivvScore
    

    def getUnknownFromHDList(self):
        self.fileReader.readUnknownHDList()
        diffset = self.fileReader.unknownSetFromHDList.difference(self.tfSet)

        return diffset
  

    def tfScore(self,geneIDSet):
        tfScore = 0
        for geneID in geneIDSet:
            if geneID in self.tfSet:
                tfScore +=1
        return tfScore
    def tfMember(self,geneIDSet):
        tfList =[]
        for geneID in geneIDSet:
            if geneID in self.tfSet:
                tfList.append(geneID)
        return tfList
    
    def unknownScoreFromHDList(self,geneIDSet):
        score = 0
        for geneID in geneIDSet:
            if geneID in self.unknownSetFromHDList:
                score +=1
        return score
    def getGeneIDTranslateBINDComplexDic(self):
        self.fileReader.readGeneAccession()
        complexDic =self.fileReader.readBINDFile(yoc.BIND_Complex_Info_hs)   
        translator = FileReader.IDTranslater()
        return translator.translate(self.fileReader.geneIndex2geneIDDict, complexDic)
        
            
    
    def compareHumanComplexSet(self,complexID,geneIDSet):
        return self._comareComplexSet(complexID, geneIDSet, self.humanMatchSet, self.humanComplexDic)
    
    
    def compareYeastComplexSet(self,complexID,geneIDSet):
        return self._comareComplexSet(complexID, geneIDSet, self.yeastMatchSet, self.yeastComplexDic)
        

    def _comareComplexSet(self,complexID,geneIDSet,matchedSet,knownDic):
        flag = False
        perfectFlag =False
        
        for id in knownDic:
            knownSet = set(knownDic[id])
            
            intersectionSet = geneIDSet.intersection(knownSet)
            
            if len(knownSet)>=3:
                denominator = len(knownSet)+len(geneIDSet)
                numerator = len(intersectionSet)*len(intersectionSet)
                if (geneIDSet.issubset(knownSet) and knownSet.issubset(geneIDSet)):
                    perfectFlag =True
                    matchedSet.add(frozenset(knownSet))
                elif ((float)(numerator)/(float)(denominator))>=self.overlapThreshold:
                
                    flag = True
                    matchedSet.add(frozenset(knownSet))

        if perfectFlag == True:
            return 2
        elif flag == True:
            return 1
        else:
            return 0
        
        
    def countDicValueSize(self,dic):
        idSet = set()
        for id in dic:
            for complexID in dic[id]:
                idSet.add(complexID)
        return len(idSet)
    def clear(self):
        self.humanComplexDic.clear()
        self.humanMatchSet.clear()

        self.yeastComplexDic.clear()
        self.yeastMatchSet.clear()
    def reset(self):
        self.humanMatchSet.clear()
        self.yeastMatchSet.clear()
        
        




def _formatSetOrList2Str(list):
    seq = ""
    for elem in list:
        seq =string.join([elem,seq], "\t")
        print elem
    print seq
    return seq




def readNonVerifiedResults():
    """
    mclprefix = "/Users/yo/Documents/workspace/2007python/data/mclResults"
    ccprefix = "/Users/yo/Documents/workspace/2007python/data/ccResults"
    cliqueprefix = "/Users/yo/Documents/workspace/2007python/data/cliqueResults"
    mcodeprefix = "/Users/yo/Documents/workspace/2007python/data/mcodeResults"
"""
    mclprefix = "C:/Documents and Settings/yo/mt/2007python/data/mclResults"
    ccprefix = "C:/Documents and Settings/yo/mt/2007python/data/ccResults"
    cliqueprefix = "C:/Documents and Settings/yo/mt/2007python/data/cliqueResults"
    mcodeprefix = "C:/Documents and Settings/yo/mt/2007python/data/mcodeResults"
    fileList=os.listdir(mcodeprefix)
    scorer = ClusterScorer()

    for file in fileList:
        filename = string.join([mcodeprefix,file],"/")
        
        complexDic = scorer.fileReader.readMcodeResult(filename)
        compareKnownSet(file, scorer,complexDic)
        scorer.reset()
        #compareKnownSet(file, scorer, complexDic)
    
    fileList=os.listdir(mclprefix)
    for file in fileList:
        filename = string.join([mclprefix,file],"/")
        complexDic = scorer.fileReader.readMCLResult(filename)
        compareKnownSet(file, scorer,complexDic)
        scorer.reset()
    fileList=os.listdir(ccprefix)
    for file in fileList:
        filename = string.join([ccprefix,file],"/")
        complexDic = scorer.fileReader.readCCResult(filename)
        compareKnownSet(file, scorer,complexDic)
        scorer.reset()
    fileList=os.listdir(cliqueprefix)
    for file in fileList:
        filename = string.join([cliqueprefix,file],"/")
        complexDic = scorer.fileReader.readCliqueResult(filename)
        compareKnownSet(file, scorer,complexDic)
        scorer.reset()
def readVerifiedResults():
    prefix = "C:/Documents and Settings/yo/mt/2007python/data/ipResult"
    #prefix = "C:/Documents and Settings/yo/mt/2007python/cluster/test"
    
    #prefix = "/Users/yo/Documents/workspace/2007python/cluster/result1208"
    #prefix = "/Users/yo/Documents/workspace/2007python/cluster/tmp"
    
    
    fileList=os.listdir(prefix)
    scorer = ClusterScorer()
    
    for file in fileList:
        filename = string.join([prefix,file],"/")
        complexDic = scorer.fileReader.readVerifiedComplexDic(filename)
        compareKnownSet(file, scorer,complexDic)
        scorer.reset()
        

def compareKnownSet(file,scorer,complexDic):
    matchCount =0
    for id in complexDic:
        geneIDSet = complexDic[id]
        hprdMatchCode = scorer.comareHPRDComplexSet(id, geneIDSet)
        bindMatchCode = scorer.compareKnownComplexSet(id, geneIDSet)
        if hprdMatchCode !=0 or bindMatchCode !=0:
            matchCount +=1
    
    recall=len(scorer.hprdMatchSet) +len(scorer.bindMatchSet)
    allKnownComplex = len(scorer.bindComplexDic)+len(scorer.hprdComplexDic)
    
    allPredictedComplex = len(complexDic)
    
    print file,",sensitivity,",matchCount,",",allPredictedComplex,",recall,",recall,",",allKnownComplex
    


if __name__ == "__main__":

    
    readVerifiedResults()
    readNonVerifiedResults()
