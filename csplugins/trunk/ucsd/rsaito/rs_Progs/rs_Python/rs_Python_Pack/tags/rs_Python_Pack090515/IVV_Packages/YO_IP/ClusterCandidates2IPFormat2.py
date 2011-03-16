#!/usr/bin/env python

""" Integer Programming (Main) """

import IVV_Packages.YO_Cluster.MMIDic as MMIDic
import IVV_Packages.YO_Cluster.MMIRevolver2 as MMIRevolver2
import sys
import string
import LPMaker
from lpsolve55 import *

class PPIFilter:
    def __init__( self):
        #sys.stderr.write("create PPI filter\n")
        self.ppiDic = {}
        
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
                return True
        return False
    
class ClusterCandidate:
    def __init__(self):
        self.ppiFilter = PPIFilter()
        self.protein2MotifDic={}
        self.mmiDic=MMIDic.MMI()
        self.lpMaker= LPMaker.LPMaker()
        
        self.lp=None
        self.ppi2mmiDic={}
        self.mmi2ppiDic={}
        self.variables2ppiOrMmi={}
        self.ppiOrMmi2Variables={}
        self.ppiVars=set()
        self.mmiVars=set()
        
        self.ppiFilter._putDictionary("a", "b")
        self.ppiFilter._putDictionary("a", "c")
        self.ppiFilter._putDictionary("b", "c")
        
        self.proteinList =["a","b","c"]

        self.protein2MotifDic["a"]=["a1","a2"]
        self.protein2MotifDic["b"]=["b1","b2"]
        self.protein2MotifDic["c"]=["c1","c2"]

        self.mmiDic.addMMI("a1","b1")
        self.mmiDic.addMMI("a2","b1")
        self.mmiDic.addMMI("a2","c1")
        self.mmiDic.addMMI("b2","c2")
        
    def checkMMI(self):
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
                                mmiPair = MMIRevolver2.MMIPair(motif1,motif2,p1,p2,"test")
                                self._putPPI2MMIDic(p1,p2,motif1,motif2,str(mmiPair))
                                self._putMMI2PPIDic(p1, p2, motif1, motif2,str(mmiPair))
                                
    def getDomainBasedVariables(self):
        for motif in self.mmi2ppiDic:
            if len(self.mmi2ppiDic[motif])>1:
                for mmiPair in self.mmi2ppiDic[motif]:
                    self.mmiVars.add(str(mmiPair))
            if len(self.mmi2ppiDic[motif])>0:
                for mmiPair in self.mmi2ppiDic[motif]:
                    self.mmiVars.add(str(mmiPair))
    def addDomainBasedConstraint(self):
        for motif in self.mmi2ppiDic:
            if len(self.mmi2ppiDic[motif])>1:
                params=self._getMMIParams(self.mmi2ppiDic[motif])
                self.lpMaker.addDomainBasedConstraints(self.lp,params)
    
    def getInteractionBasedVariables(self):
        for p1 in self.ppi2mmiDic:
            for p2 in self.ppi2mmiDic[p1]:
                ppi=self._getPPI(p1, p2) 
                mmiList=self.ppi2mmiDic[p1][p2]
                self.ppiVars.add(ppi)
                #self._getPPIAndCandidateMMI(ppi, mmiList)
                #self._setSos(mmiList)
    def addInteractionBasedConstraints(self):
        for p1 in self.ppi2mmiDic:
            for p2 in self.ppi2mmiDic[p1]:
                ppi=self._getPPI(p1, p2) 
                mmiList=self.ppi2mmiDic[p1][p2] 
                params = self._getMMIAndPPIParams(ppi,mmiList)
                self.lpMaker.addInteractionsBasedConstraints(self.lp,params)
                self._getExclusiveParams(mmiList)
                params2= self._getExclusiveParams(mmiList)
                #self.lpMaker.addSos(self.lp,params2)
    def mergeVars(self):
        i=0
        for ppiVar in self.ppiVars:
            #print ppiVar
            self.ppiOrMmi2Variables[ppiVar]=i
            self.variables2ppiOrMmi[i]=ppiVar
            i+=1
        for mmiVar in self.mmiVars:
            #print mmiVar
            self.ppiOrMmi2Variables[mmiVar]=i
            self.variables2ppiOrMmi[i]=mmiVar
            i+=1
    def getProteinSet(self,vars):
        ppiNum = len(self.ppiVars)
        i=0
        proteinSet = set()
        
        for var in vars:
            if var ==1.0 and i<ppiNum:
                parsedPPI=self._parsePPI(self.variables2ppiOrMmi[i])
                for p in parsedPPI:
                    proteinSet.add(p)
            i+=1
        return frozenset(proteinSet)
    def getConfirmedMMI(self,vars):
        ppiNum = len(self.ppiVars)
        i=0
        mmiSet = set()
        
        for var in vars:
            if var ==1.0 and i>=ppiNum:
                mmi=self.variables2ppiOrMmi[i]
                mmiSet.add(mmi)
            i+=1
        return mmiSet
    def _parsePPI(self,ppi):
        return ppi.split("_")
    def createLP(self):
        self.lp=self.lpMaker.createLP(self.ppiOrMmi2Variables.keys())
        self.lpMaker.setVarConstraint(self.lp)
        self.lpMaker.setObjectives(len(self.ppiVars),self.ppiOrMmi2Variables.keys(),self.lp)
    def _getMMIParams(self,mmiList):
        params=[]
        
        for i in range(len(self.variables2ppiOrMmi.keys())):
            
            if self.variables2ppiOrMmi[i] in mmiList:
                params.append(1)
            else:
                params.append(0)
        return params
    def _getMMIAndPPIParams(self,ppi,mmiList):
        params=[]
        
        for i in range(len(self.variables2ppiOrMmi.keys())):
            if self.variables2ppiOrMmi[i] == ppi:
                params.append(-1)
            elif self.variables2ppiOrMmi[i] in mmiList:
                params.append(1)
            else:
                params.append(0)
        return params
    def _getExclusiveParams(self,mmiList):
        params=[]
        
        for mmi in mmiList:
            params.append(self.ppiOrMmi2Variables[mmi]+1)
            
        return params
    
    def _putMMI2PPIDic(self,p1,p2,motif1,motif2,mmiPair):
        pMotif1=self._getPmotif(p1, motif1)
        pMotif2=self._getPmotif(p2, motif2)

        if pMotif1 in self.mmi2ppiDic:
            self.mmi2ppiDic[pMotif1].append(mmiPair)
        else:
            self.mmi2ppiDic[pMotif1]=[mmiPair]

        if pMotif2 in self.mmi2ppiDic:
            self.mmi2ppiDic[pMotif2].append(mmiPair)
        else:
            self.mmi2ppiDic[pMotif2]=[mmiPair]
    def _getPmotif(self,p,motif):
        return string.join([p,motif], ".")
    def _getPPI(self,p1,p2):
        return string.join([p1,p2], "_")
    
    def _putPPI2MMIDic(self,p1,p2,motif1,motif2,mmiPair):
        if p1 in self.ppi2mmiDic:
            if p2 in self.ppi2mmiDic[p1]:
                self.ppi2mmiDic[p1][p2].append(mmiPair)        
            else:
                self.ppi2mmiDic[p1][p2] = [mmiPair]  
        else:
            self.ppi2mmiDic[p1] = {p2:[mmiPair]}  
   

    def _isExist(self,motif1,motif2):
        flag = False
        if motif1 in self.mmiDic.mmi:
            if motif2 in self.mmiDic.mmi[motif1]:
                flag = True
        
        return flag
if __name__ == "__main__":
    cluster = ClusterCandidate()
    cluster.checkMMI()
    cluster.getDomainBasedVariables()
    cluster.getInteractionBasedVariables()
    cluster.mergeVars()
    cluster.createLP()
    
    cluster.addDomainBasedConstraint()
    cluster.addInteractionBasedConstraints()
    # cluster.lpMaker.writeLP(cluster.lp,"lp_intermediate_file.lp")
    
    lpsolve('solve', cluster.lp)
    score=lpsolve('get_objective', cluster.lp)
    vars=lpsolve('get_variables', cluster.lp)[0]
    proteinSet =cluster.getProteinSet(vars)
    mmiSet = cluster.getConfirmedMMI(vars)
    print proteinSet,score,mmiSet
    