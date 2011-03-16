import MMIDic
import MMIRevolver2
import sys
import string
import LPMaker
from lpsolve55 import *

    
class ClusterCandidate:
    def __init__(self,cluster,ppiFilter,geneID2IRDic,gtsp,mmiDic):
        self.ppiFilter = ppiFilter
        self.protein2MotifDic={};
        self.mmiDic=mmiDic
        self.lpMaker= LPMaker.LPMaker()
        self.cluster = cluster

        self.lp=None
        self.ppi2mmiDic={}
        self.mmi2ppiDic={}
        self.variables2ppiOrMmi={}
        self.ppiOrMmi2Variables={}
        self.ppiVars=set()
        self.mmiVars=set()
        
        self.wholeProteinMotifSet=set()
        #print "ClusterCandidate init id =",cluster.clusterId
        self.makeProtein2MotifDic(cluster,gtsp,geneID2IRDic)
        
    def makeProtein2MotifDic(self,cluster,gtsp,geneID2IRDic):
        for protein in cluster.proteinList:
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
            if not irList and not motifList:
                wholeMotif= protein+"Whole"
                self.wholeProteinMotifSet.add(wholeMotif)

                if protein in self.protein2MotifDic:
                    self.protein2MotifDic[protein].append(wholeMotif)
                else:
                    self.protein2MotifDic[protein]=[wholeMotif]
            """
            if motifList or irList:
                sys.stderr.write("ClusterCandidate init "+protein+":"+str(len(self.protein2MotifDic[protein]))+"\n")
            else:
                sys.stderr.write("ClusterCandidate init "+protein+":"+"\n")
        print
        """ 
    def getMaxLinks(self):
        score = 0
        for p1 in self.cluster.proteinList:
            for p2 in self.cluster.proteinList:
                if p1 !=p2 and self.ppiFilter.isExist(p1,p2):
                        score +=1
        return score/2
    def checkMMI(self):
        #sys.stderr.write("checkMMI\n")
        keys =self.protein2MotifDic.keys()
        length =len(keys)
        ppiSet =set()
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
                                if motif1 in self.mmiDic and motif2 in self.mmiDic:
                                    sourceID =self.mmiDic[motif1][motif2]
                                else:
                                    sourceID = "psuedo"
                                mmiPair = MMIRevolver2.MMIPair(motif1,motif2,p1,p2,sourceID)
                                self._putPPI2MMIDic(p1,p2,motif1,motif2,str(mmiPair))
                                self._putMMI2PPIDic(p1, p2, motif1, motif2,str(mmiPair))
                                ppiSet.add(p1+p2)
        return len(ppiSet)
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
    
    def printMMIs(self):
        for p1 in self.ppi2mmiDic:
            for p2 in self.ppi2mmiDic[p1]:
                mmiList = self.ppi2mmiDic[p1][p2]
                print(p1,p2,mmiList)
                
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
        if motif1 in self.mmiDic:
            if motif2 in self.mmiDic[motif1]:
                flag = True
        if motif1 in self.wholeProteinMotifSet or motif2 in self.wholeProteinMotifSet:
            flag = True
        
        return flag

    