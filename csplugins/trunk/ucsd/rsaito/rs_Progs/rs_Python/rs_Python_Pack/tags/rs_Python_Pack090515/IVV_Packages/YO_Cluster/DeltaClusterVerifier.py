#!/usr/bin/env python

""" Complex prediction by exhaustive DDI combination search """

import IVVInfo2Motif
import Gene_to_SPMotif1
import Gene_to_SPMotif3
import sys
import MMIRevolver2
import string
import re
import TabFileReader
import Cluster
import time
import ClusterScorer
import GeneID2GeneName
import PPIFilter
#import psyco
#psyco.profile()


class TmpCluster:
    def __init__(self,cluster,proteinList,gtsp,info2motif,ppiFilter):
        self.protein2MotifDic ={}
        self.allMMI={}
        self.mmiDic =info2motif.mmiDic.mmi
        self.cluster = cluster
        self.ppiFilter = ppiFilter
        
        self.independencyCheckDic={}
        self.independentMMI_checkDic={}
        
        self.resultDic ={}
               
        self.independentMMI={}
        self.proteinList = proteinList
        geneID2IRDic = info2motif.geneID2IRDic
        self.interactionExistingMotifSet = set()
        #print self.proteinList
        for protein in proteinList:
            irList=[]
            motifList = gtsp.geneid_to_motif(protein)
            if motifList:
                self.protein2MotifDic[protein] = motifList
            if protein in geneID2IRDic:
                irList = geneID2IRDic[protein]
            if irList:
                for ir in irList:
                    if protein in self.protein2MotifDic:
                        self.protein2MotifDic[protein].append(ir)
                    else:
                        self.protein2MotifDic[protein]=[ir]
            if motifList or irList:
                sys.stderr.write("TmpCluster init "+protein+":"+str(len(self.protein2MotifDic[protein]))+"\n")
        #print
        
    def getIDScore(self):
        score =0
        for key in self.independentMMI:
            score+=1
        return score
        
    def getMMIListDic(self):
        mmiListDic = {}
        """
        if self.allMMI:
            print "allMMI",self.allMMI
        """
        for p1 in self.allMMI:
            for p2 in self.allMMI[p1]:
                if p1 in self.independentMMI_checkDic:
                    if p2 in self.independentMMI_checkDic[p1]:
                        for mmiPair in self.independentMMI_checkDic[p1][p2]:
                            key=string.join([p1,p2],":")    
                            if key in self.independentMMI:
                                self.independentMMI[key].append(mmiPair)
                            else:
                                self.independentMMI[key]=[mmiPair]
                            #print "I",key,mmiPair
                else:
                        
                    for mmiPair in self.allMMI[p1][p2]:
                    #print p1,p2,mmiPair.getMotif1().getMotifName(),mmiPair.getMotif2().getMotifName()
                        key=string.join([p1,p2],":")
                        if key in mmiListDic:
                            mmiListDic[key].append(mmiPair)
                        else:
                            mmiListDic[key]=[mmiPair]
                            
                        #print "allmmi",key,mmiPair
        self.allMMI.clear()
        self.independentMMI_checkDic.clear()
        self.independentMMIList =[]
        for key in self.independentMMI:
           self.independentMMIList.append(self.independentMMI[key])
        return mmiListDic

    def _getALLMMIVariation(self):
        #sys.stderr.write("getALLMMIVariation\n")
        keys =self.protein2MotifDic.keys()
        length =len(keys)
        
        for i in range(length):
            p1 = keys[i]
            
            for j in range(length-(i+1)):
                p2 = keys[j+i+1]
                if p1 !=p2 and self.ppiFilter.isExist(p1,p2):
                    selfMotifs = self.protein2MotifDic[p1]
                    for motif1 in selfMotifs:
                        otherMotifs = self.protein2MotifDic[p2]
                        for motif2 in otherMotifs:
                            if self._isExist(motif1,motif2):
                                sourceID =self.mmiDic[motif1][motif2]
                                mmiPair = MMIRevolver2.MMIPair(motif1,motif2,p1,p2,sourceID)
                                self._putIndependentCheckDic(p1, p2, motif1, motif2)
                                self._putInteractionExistingMotifList(p1,p2,motif1,motif2)
                                if p1 in self.allMMI:
                                    if p2 in self.allMMI[p1]:
                                        self.allMMI[p1][p2].append(mmiPair)        
                                    else:
                                        self.allMMI[p1][p2] = [mmiPair]  
                                else:
                                    self.allMMI[p1] = {p2:[mmiPair]}  
    def _putInteractionExistingMotifList(self,p1,p2,motif1,motif2):
        
        self.interactionExistingMotifSet.add(string.join([p1,motif1],"."))
        self.interactionExistingMotifSet.add(string.join([p2,motif2],"."))
        
    def isEnoughToSatisfyMinScore(self,minScore):
        if (minScore*2) > len(self.interactionExistingMotifSet):
            return False
        else:
            return True
    def _putIndependentCheckDic(self,p1,p2,motif1,motif2):
        if p1 in self.independencyCheckDic:
            if motif1 in self.independencyCheckDic[p1]:
                if p2 in self.independencyCheckDic[p1][motif1]:
                    self.independencyCheckDic[p1][motif1][p2].append(motif2)
                else:
                    self.independencyCheckDic[p1][motif1][p2]=[motif2]        
            else:
                self.independencyCheckDic[p1][motif1]={p2:[motif2]}
        else:
            self.independencyCheckDic[p1] ={motif1:{p2:[motif2]}}
            

        if p2 in self.independencyCheckDic:
            if motif2 in self.independencyCheckDic[p2]:
                if p1 in self.independencyCheckDic[p2][motif2]:
                    self.independencyCheckDic[p2][motif2][p1].append(motif1)
                else:
                    self.independencyCheckDic[p2][motif2][p1]=[motif1]        
            else:
                self.independencyCheckDic[p2][motif2]={p1:[motif1]}
        else:
            self.independencyCheckDic[p2] ={motif2:{p1:[motif1]}}
            
    def checkIndependency(self):
        self._getALLMMIVariation()
        for p1 in self.independencyCheckDic:
            for motif1 in self.independencyCheckDic[p1]:
                linkedProteins = self.independencyCheckDic[p1][motif1].keys()
                if len(linkedProteins) == 1:
                    p2 = linkedProteins[0]
                    for motif2 in self.independencyCheckDic[p1][motif1][p2]:
                        if len(self.independencyCheckDic[p2][motif2])==1:
                            #print "verified",p1,p2,motif1,motif2
                            sourceID =self.mmiDic[motif1][motif2]
                            mmiPair = MMIRevolver2.MMIPair(motif1,motif2,p1,p2,sourceID)
  
                            if p1 in self.independentMMI_checkDic:
                                if p2 in self.independentMMI_checkDic[p1]:
                                    self.independentMMI_checkDic[p1][p2].append(mmiPair)
                                else:
                                    self.independentMMI_checkDic[p1][p2]=[mmiPair]
                            else:
                                self.independentMMI_checkDic[p1]={p2:[mmiPair]}
        self.independencyCheckDic.clear()

    def _isExist(self,motif1,motif2):
        flag = False
        if motif1 in self.mmiDic:
            if motif2 in self.mmiDic[motif1]:
                flag = True
        
        return flag

    def getScore(self,returnedList):
        score = len(returnedList)
        score+= self.getIDScore()

        return score
    
    def getMaxLinks(self):
        score = 0
        for p1 in self.proteinList:
            for p2 in self.proteinList:
                if p1 !=p2 and self.ppiFilter.isExist(p1,p2):
                        score +=1
        return score/2
        
    
    def _putDictionay(self,p1,p2,scoreDic):
        if p1 in scoreDic:
            scoreDic[p1].add(p2)
        else:
            scoreDic[p1]=set(p2)
        if p2 in scoreDic:
            scoreDic[p2].add(p1)
        else:
            scoreDic[p2]=set(p1)
class MMIList:
    def __init__(self,key,mmiList):
        self.key = key
        self.mmiList = mmiList
        self.length = len(mmiList)
    def __repr__(self):
        return str(self.key)+":"+str(self.length)
    def __cmp__(self, other):
        return cmp(self.length,other.length)
    
class ResultSet:
    def __init__( self,memberSet):
        self.memberSet = frozenset(memberSet)
        self.returnedListList=[]
        self.motifHashList =[]
    def isNewMotifs(self,returnedList):
        if len(self.motifHashList) ==0:
            newHashSet = set()
            for mmi in returnedList:
                hash =mmi.__repr__().__hash__()
                newHashSet.add(hash)
            self.motifHashList.append(newHashSet)
            return True
        else:
            newHashSet = set()
            dellist = []
            for hashSet in self.motifHashList:
                if self._isAllInTheHash(newHashSet,hashSet, returnedList):
                    return False
                else:
                    #print newHashSet,hashSet
                    if newHashSet.issuperset(hashSet):
                        dellist.append(hashSet)
                        #print "dellist",hashSet,returnedList

            for hashSet in dellist:
                index = self.motifHashList.index(hashSet)
                self.motifHashList.remove(hashSet)
                #print "deleted",self.returnedListList[index]
                self.returnedListList.remove(self.returnedListList[index])
            self.motifHashList.append(newHashSet)
            return True
                
    def _isAllInTheHash(self,newHashSet,hashSet,returnedList):
        for mmi in returnedList:
            hash =mmi.__repr__().__hash__()
            newHashSet.add(hash)
            
            if hash not in hashSet:
                 return False            
        return True
           
class ClusterVerifier:
    
    def __init__( self ,clusterMap,basicFilterFlag,ppiFile,verifyID,ivvFlag):
        self.verifyID = verifyID
        self.clusterMap=clusterMap
        self.allClusterNum = len(self.clusterMap)
        
        swissprot_file = "../data/uniprot_sprot_human.dat"
        gene2accession_file = "../data/gene2accession"
        swisspfam_file = "../data/swisspfam_save"
        
        yeastFlag =False
        if yeastFlag:
            sys.stderr.write("yeast mode")
            swisspfam_file = "../data/yeast_data/swisspfam_save_yeast"
            swissprot_file = "../data/yeast_data/uniprot_yeast.txt"


        #self.gtsp = Gene_to_SPMotif1.Gene_to_SPMotif1(swissprot_file,gene2accession_file,swisspfam_file)
        self.gtsp = Gene_to_SPMotif3.Gene_to_SPMotif(swissprot_file,gene2accession_file,swisspfam_file)
        
        ivv_info_file = "../data/ivv_human8.0_info"
        self.info2motif = IVVInfo2Motif.IVVInfo2Motif(ivv_info_file,basicFilterFlag)
        if ivvFlag:
            #info2motif.parseIVV_info()
            self.info2motif.getIVV_IR()
        self.info2motif.getiPfamMMI()      
         
        
        #self.mmiDic = info2motif.mmiDic.mmi
        
        self.mmiCounter={}
        
        
        #keys= self.mmiDic.keys()
        #keys.sort()
        #for m1 in keys:
            #for m2 in self.mmiDic[m1]:
        #    print "mmiDic",m1,self.mmiDic[m1]
        
   
        self.threshold = 0.6
        self.resultDic={}
    
        self.tmpCounter =0
        
        
        self.clusterScorer = ClusterScorer.ClusterScorer()    
        self.geneNameDic = GeneID2GeneName.TranslateGeneID2GeneName()
        self.geneNameDic.readGeneInfoFile()
        
        self.ppiFilter = PPIFilter.PPIFilter(ppiFile)
        
        self.resultSummarySet=set()
        self.resultSummaryByScore={}
        self.allVerifiedPatternCount = 0
        self.verifiedMatchCount =0
        
        self.notVerifiedMatchCount =0
        
        self.ivvResultSummarySet=set()
        
    def verifyClusters(self,clique = False):
        sys.stderr.write("verify clusters\n")
        self._printFirstLine()
        for clusterID in self.clusterMap:
            self.allClusterNum -=1

            
            cluster = self.clusterMap[clusterID]
            
            hprdMatchCode = self.clusterScorer.comareHPRDComplexSet(cluster.clusterId, cluster.proteinList)
            bindMatchCode = self.clusterScorer.compareKnownComplexSet(cluster.clusterId, cluster.proteinList)
            yeastMatchCode = self.clusterScorer.compareYeastComplexSet(cluster.clusterId, cluster.proteinList)
            if hprdMatchCode!=0 or bindMatchCode !=0 or yeastMatchCode !=0:
                self.notVerifiedMatchCount +=1
          
            tmp=""
            for protein in cluster.proteinList:
                tmp = string.join([protein,tmp],"\t")
            sys.stderr.write(str(clusterID)+"\t"+tmp+"\n")
            
            #print "clusterID=",clusterID,cluster.proteinList

            
            tmpCluster = TmpCluster(cluster,cluster.proteinList,self.gtsp,self.info2motif,self.ppiFilter)
            tmpCluster.checkIndependency()
            mmiListDic = tmpCluster.getMMIListDic()
            minScore =2
            maxLinks =tmpCluster.getMaxLinks()
            idScore =tmpCluster.getIDScore()
            n = 1
            m = 1
            for key in mmiListDic:
                n *=len(mmiListDic[key])
                m *=2
                sys.stderr.write(key+":"+self._formatSetOrList2Str(mmiListDic[key])+"\n")
            n = n *m
            sys.stderr.write(str(self.allClusterNum)+":"+str(n)+"\n")            
            #print "minScore",minScore
            #print "len motif length", tmpCluster.interactionExistingMotifSet
            #if len(mmiListDic)>0 and tmpCluster.isEnoughToSatisfyMinScore(minScore):
            if (len(mmiListDic)>0):    
            #if (len(mmiListDic)>0) and (len(cluster.proteinList)<=10):    
                mmiListRevolver = MMIRevolver2.DeltaMMIListRevolver(mmiListDic)
                mmiRevolver =None
                while True:
                    mmiList = mmiListRevolver.next()
                    #print "mmiListRevolver.next()",next2-next1
                    cacheDic = None
                    if mmiRevolver:
                        cacheDic =mmiRevolver.getCacheDic()
                    else:
                        cacheDic = {}
                        
                    if mmiList == False:
                        break  
                    else:
                        mmiRevolver = MMIRevolver2.DeltaMMIRevolver(mmiList,minScore,cacheDic)
                        mmiRevolver.setCacheLevel(mmiList.cacheLevel)
                        repeat = mmiRevolver.repeatNum() -1
#                            sys.stderr.write("cacheCreate,"+str(repeat)+"\n")

                        self.tmpCounter +=repeat
                        
                        while repeat >0:
                            repeat -=1
#                        for i in range(repeat):
                            returnedList = mmiRevolver.next()
                            #print returnedList
                            if returnedList:                                    
                                numLinks = tmpCluster.getScore(returnedList)                   
                                score =float(numLinks)/float(maxLinks)
                             #   print numLinks,(minScore+idScore)
                                #if score >= self.threshold:

                                if numLinks>=(minScore+idScore):
                                    #print "verified",score,returnedList
                                    self._parseResult2(tmpCluster,returnedList)
                            
  
                #self._printResult(tmpCluster,maxLinks,idScore,mmiListDic)
                self._printResultExcelFormat(tmpCluster, maxLinks, idScore, mmiListDic)
        #for mmi in self.mmiCounter:
        #    print mmi,",",self.mmiCounter[mmi]  
        self._writeSummary()
    def _countMMI(self,returnedList):
        for mmiPair in returnedList:
            tmplist=[]
            motif1 =mmiPair.getMotif1().getMotifName()
            motif2 =mmiPair.getMotif2().getMotifName()
            tmplist.append(motif1)
            tmplist.append(motif2)
            tmplist.sort()
            mmi = tmplist[0]+":"+tmplist[1]
            if mmi in self.mmiCounter:
                self.mmiCounter[mmi]+=1
            else:
                self.mmiCounter[mmi]=1
                
            
            
    def _getMember(self,returnedList,tmpCluster):
        memberSet =set()
        for mmiPair in returnedList:
            p1 = mmiPair.getMotif1().getParentGeneID()
            p2 = mmiPair.getMotif2().getParentGeneID()
            memberSet.add(p1)
            memberSet.add(p2)
        for key in tmpCluster.independentMMI:
            p1 =tmpCluster.independentMMI[key][0].getMotif1().getParentGeneID()
            p2 =tmpCluster.independentMMI[key][0].getMotif2().getParentGeneID()
            memberSet.add(p1)
            memberSet.add(p2)
        return memberSet

    def getMaxLinks(self,memberSet):
        score = 0
        for p1 in memberSet:
            for p2 in memberSet:
                if p1 !=p2 and self.ppiFilter.isExist(p1,p2):
                        score +=1
        return score/2
   
    def _printFirstLine(self):
        
        #sys.stdout.write("cluster member geneID\t"+
        #                 "cluster member name\t"+
        #                 "original member geneID\t"+
        #                 "score\t"+
        #                 "structure\t"+
        #                 "HPRD match\t"+
        #                 "BIND match\t"+
        #                 "TF member\t"+
        #                 "TF score\t"+"\n")
        
        
        sys.stdout.write("cluster member geneID,"+
                         "cluster member name,"+
                         "original member geneID,"+
                         "score,"+
                         "structure,"+
                         "HPRD match,"+
                         "BIND match,"+
                         "IVV Flag,"+
                         "TF member,"+
                         "TF score,"+
                         "Yeast match"+"\n")
    def _translateMatchCode(self,code):
        if code ==2:
            return"complete match"
        elif code == 1:
            return "partial match"
        elif code ==0:
            return "miss"
        else:
            return "error"
    def _printResultExcelFormat(self,tmpCluster,maxLinks,idScore,mmiListDic):
        if len(tmpCluster.resultDic)>0:
            originalgeneIDs=tmpCluster.cluster.proteinList
            for id in tmpCluster.resultDic:
                
                for resultSet in tmpCluster.resultDic[id]:         
                    verifiedGeneIDSet =resultSet.memberSet
                    geneNames=self._translateGeneID2GeneName(verifiedGeneIDSet)
                    hprdPartialMatchCode = self.clusterScorer.comareHPRDComplexSet(tmpCluster.cluster.clusterId, verifiedGeneIDSet)
                    bindPartialMatchCode = self.clusterScorer.compareKnownComplexSet(tmpCluster.cluster.clusterId, verifiedGeneIDSet)
                    yeastPartialMatchCode = self.clusterScorer.compareYeastComplexSet(tmpCluster.cluster.clusterId, verifiedGeneIDSet)
                    
                    tfMember = self.clusterScorer.tfMember(verifiedGeneIDSet)
                    for returnList in resultSet.returnedListList:
                        for key in tmpCluster.independentMMI:
                            returnList.append(tmpCluster.independentMMI[key][0])
                        score = str(len(returnList)) +":"+str(maxLinks)
                        ivvFlag =self._isIncludeIVV(returnList)
                        
                        sys.stdout.write(self._formatSetOrList2Str(verifiedGeneIDSet)+","+
                                         self._formatSetOrList2Str(geneNames)+","+
                                         self._formatSetOrList2Str(originalgeneIDs)+","+
                                         score+","+
                                         self._formatSetOrList2Str(returnList)+","+
                                         self._translateMatchCode(hprdPartialMatchCode)+","+
                                         self._translateMatchCode(bindPartialMatchCode)+","+
                                         str(ivvFlag)+","+
                                          self._formatSetOrList2Str(tfMember)+","+
                                         str(len(tfMember))+","+
                                         self._translateMatchCode(yeastPartialMatchCode)+","+
                                         "\n")
                        self._countMMI(returnList)
                        self._countSummary(score,verifiedGeneIDSet,hprdPartialMatchCode,bindPartialMatchCode,yeastPartialMatchCode,ivvFlag)

    def _formatSetOrList2Str(self,list):
        seq= ""
        for elem in list:
            seq =string.join([str(elem),seq], " ")
        return seq

    def _translateGeneID2GeneName(self,geneIDSet):
        geneNameSet=set()
        for geneID in geneIDSet:
            geneName = self.geneNameDic.translate(geneID)
            geneNameSet.add(geneName)
        return geneNameSet
    
    def _parseResult2(self,tmpCluster,returnedList):
        memberSet = self._getMember(returnedList, tmpCluster)
        if len(tmpCluster.resultDic) ==0:
            resultSet = ResultSet(memberSet)
            resultSet.returnedListList.append(returnedList)
            resultSet.isNewMotifs(returnedList)
            tmpCluster.resultDic[len(memberSet)] = [resultSet]
        else:
            delList =[]
            appendFlag = False
            numMembers = tmpCluster.resultDic.keys()
            numMembers.reverse()
            for num in numMembers:
                for resultSet in tmpCluster.resultDic[num]:
                    if memberSet.issuperset(resultSet.memberSet) and memberSet.issubset(resultSet.memberSet):
                        if resultSet.isNewMotifs(returnedList):
                            resultSet.returnedListList.append(returnedList)
                    elif memberSet.issuperset(resultSet.memberSet):
                        delList.append(resultSet)
                        appendFlag  = True
            if appendFlag == True:
                newResultSet = ResultSet(memberSet)
                if len(memberSet) in tmpCluster.resultDic:
                    tmpCluster.resultDic[len(memberSet)].append(newResultSet)
                else:
                    tmpCluster.resultDic[len(memberSet)]=[newResultSet]
                for resultSet in delList:
                    key = len(resultSet.memberSet)
                    tmpCluster.resultDic[key].remove(resultSet)
                    if tmpCluster.resultDic[key] ==[]:
                        del tmpCluster.resultDic[key]
    def _countSummary(self,score,memberSet,hprdMatchCode,bindMatchCode,yeastMatchCode,ivvFlag):
        self.allVerifiedPatternCount +=1
        if hprdMatchCode !=0 or bindMatchCode !=0 or yeastMatchCode !=0:
            if memberSet not in self.resultSummarySet:
                self.verifiedMatchCount +=1
                #print "hit",memberSet,self.verifiedKnownCount
        if ivvFlag:
            self.ivvResultSummarySet.add(memberSet)
        self.resultSummarySet.add(memberSet)
        if score in self.resultSummaryByScore:
            self.resultSummaryByScore[score] +=1
        else:
            self.resultSummaryByScore[score] =1

    def _printSummary(self):
        print
        sys.stdout.write("total,"+str(len(self.resultSummarySet))+":"+str(len(self.clusterMap))+"\n")
        print
        for mmi in self.mmiCounter:
            sys.stdout.write(mmi+","+str(self.mmiCounter[mmi])+"\n")
        print
        for score in self.resultSummaryByScore:
            sys.stdout.write(score+","+str(self.resultSummaryByScore[score])+"\n")
            
    def _writeSummary(self):
        totalFile = open("resultSummary0525/total.txt", 'r+')    
        while True:
            line = totalFile.readline()
            if line == "":
                break  
        #totalFile.write(self.verifyID+","+str(len(self.resultSummarySet))+"("+str(self.knownCount)+")"+":"+str(len(self.clusterMap))+"("+str(self.allKnownCount)+")"+","+str(self.allCount)+":"+str(len(self.clusterMap))+"\n")
        
        #recall=len(self.clusterScorer.hprdMatchSet) +len(self.clusterScorer.bindMatchSet)
        #allKnownComplex = len(self.clusterScorer.bindComplexDic)+len(self.clusterScorer.hprdComplexDic)
            
        totalFile.write(self.verifyID+","
                        +str(self.verifiedMatchCount)+","+str(len(self.resultSummarySet))+","
                        +str(self.notVerifiedMatchCount)+","+str(len(self.clusterMap))
                        #+","+str(self.allVerifiedPatternCount)+","+str(len(self.resultSummarySet))
                        +"\n");
                        #+str(len(self.ivvResultSummarySet))+"\n")
        mmiFile= open("resultSummary0525/mmiCount.txt", 'r+')    
        while True:
            line = mmiFile.readline()
            if line == "":
                break 
        mmiFile.write(self.verifyID+"\n") 
        for mmi in self.mmiCounter:
            mmiFile.write(mmi+","+str(self.mmiCounter[mmi])+"\n")
        mmiFile.write("\n")

        scoreFile= open("resultSummary0525/scoreCount.txt", 'r+')    
        while True:
            line = scoreFile.readline()
            if line == "":
                break 
        scoreFile.write(self.verifyID+"\n") 
             
        
        for score in self.resultSummaryByScore:
            scoreFile.write(score+","+str(self.resultSummaryByScore[score])+"\n")       
        scoreFile.write("\n") 
    def _isIncludeIVV(self,returnedList):
        for mmiPair in returnedList:
            if mmiPair.sourceID == "IVV":
                return True
        return False

def clique():
    import Clique
    c = Clique.Clique()
    #c.readPPIFile( "C:\Documents and Settings\yo\mt\PPI\data\ppiInfo\ppi_ncbi_tmp.txt" )
    #c.readPPIFile( "C:\Documents and Settings\yo\mt\PPI\data\ppiInfo\ppi_ncbi_human_ivvSpoke.txt" )     
    #c.readPPIFile( "../data/test_ppi.txt" )     
    c.readPPIFile( "../data/ppi/ppi_ncbi2_hs.txt" )     
    c.makeNetwork()
    c.clustering3()
    
    flag = True
    numNodes =3
    while flag == True:
        sys.stderr.write("Merging "+str(numNodes)+" nodes clusters:"+str(len(c.clusterMapByNumNodes[numNodes]))+"\n")
        flag = c._mergeCluster(numNodes)
        numNodes +=1    
    clusterMap = c.getClusterMap()
    for clusterID in clusterMap:

        
        cluster = clusterMap[clusterID]
    
        tmp=""
        for protein in cluster.proteinList:
            tmp = string.join([protein,tmp],"\t")
        sys.stdout.write(str(clusterID)+"\t"+tmp+"\n")  
    verifier = ClusterVerifier(c.getClusterMap(),bfFlag)
    verifier.verifyClusters()

def readClusterFromFile(filename,bfFlag,ppiFile,verifyID,ivvFlag,clique = False):
    #sys.stderr.write("Read clusters from file\n")
    tabFile = TabFileReader.Tabfile(filename)
    clusterMap ={}
    while True:
        line = tabFile.readline()
        #sys.stderr.write(str(line)+"\n")
        if line == False:
            break
        else: 
            clusterID = line[0]
            length = len(line)
            proteinList = set()
            for i in range(length):
                if i != 0:    
                    geneID =line[i].replace("\n","")    
                    if geneID.isdigit():
                        proteinList.add(geneID)
            
            clusterMap[clusterID]=Cluster.Cluster2(clusterID,proteinList)

    sys.stderr.write(str(len(clusterMap))+" clusters read\n")
    
    verifier = ClusterVerifier(clusterMap,bfFlag,ppiFile,verifyID,ivvFlag)
    t1 = time.time()
    #verifier.verifyClusters()
    verifier.verifyClusters(clique)
    t2 = time.time()
    
    
if __name__ == "__main__":
    condition = sys.argv[1]
    sys.stderr.write(condition+"\n")
    ivvFlag =True
    if condition == "public:mmi=bf":
        filename="../data/cliqueResults/cliquePublic.out"
        bfFlag =True
        ppiFile = "../data/ppi/ppi_ncbi2_hs.txt"
        verifyID = "Clique PPI:public DDI:BF"
    elif condition == "public:mmi=all":
        filename="../data/cliqueResults/cliquePublic.out"
        bfFlag =False
        ppiFile = "../data/ppi/ppi_ncbi2_hs.txt"
        verifyID = "Clique PPI:public DDI:ivv"

    elif condition == "BF":
        filename="../data/cliqueResults/cliquePublic+BF.out"
        bfFlag =True
        ppiFile = "../data/ppi/ppi_ncbi2_hs+ivvBF.txt"
        verifyID = "Clique PPI:public+BF DDI:BF"

    elif condition =="ivvAll":
        filename="../data/cliqueResults/cliquePublic+ivvAll.out"
        bfFlag =False
        ppiFile = "../data/ppi/ppi_ncbi2_hs+ivvall.txt"
        verifyID = "Clique PPI:public+ivv DDI:ivv"
    elif condition =="noIVV":
        filename="../data/cliqueResults/cliquePublic.out"
        bfFlag =False
        ppiFile = "../data/ppi/ppi_ncbi2_hs.txt"
        mmiFilter ="../data/ppi/ppi_ncbi2_hs.txt"
        ivvFlag=False
        verifyID = "Clique PPI:public DDI:noIVV"
    else:
        #filename="../data/cliqueResults/clique_ivvAll_2353_3725_4087.out"
        #filename="../data/cliqueResults/clique_BF_2353_3725_4087.out"
        filename="../data/cliqueResults/4790_4851_clique.out"
        bfFlag =False
        ppiFile = "../data/ppi/ppi_ncbi2_hs+ivvall.txt"
        verifyID = "Clique PPI:public+ivv DDI:ivv"
    clique =True
    readClusterFromFile(filename,bfFlag,ppiFile,verifyID,ivvFlag,clique)
    #    print "total",t2-t1 
    #except IOError:
     #   clique()