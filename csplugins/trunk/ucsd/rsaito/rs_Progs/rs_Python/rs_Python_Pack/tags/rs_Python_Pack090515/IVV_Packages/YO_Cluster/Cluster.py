import string
import sys
import copy
                                    
    
class Cluster:
    
    def __init__( self ,p1,p2,basePPIDic,clusterId):
        self.density =1
        self.basePPIDic = basePPIDic
        #ppi are single direction
         
        self.proteinList = set()
        self.ppiDic = {}
        self._addPPI(p1, p2)
        

        #self.ivvCluster = IvvCluster(self.proteinList)
        self.clusterId = clusterId
        
        
    def _addPPI(self,p1,p2):
        try:
            if p1 != p2:
                if p1 in self.ppiDic:
                    self.ppiDic[p1].append(p2) 
                else:
                    self.ppiDic[p1] = [p2]
                
                if p1 not in self.proteinList:
                    self.proteinList.add(p1)
                        
                if p2 not in self.proteinList:
                    self.proteinList.add(p2)
        except MemoryError:
            print "ppiSize=",len(self.ppiDic)
            

    def isToBeInCluster(self,p1,density):
        tmpPPIList =[]
        d = self._calcDensity(p1,tmpPPIList)
        if d >= density:
            for p2 in tmpPPIList:
                self._addPPI(p1,p2)
            self.density = d
            
    def _calcDensity(self,candidateP,tmpPPIList):
        size = self.nodeSize()
        num_links = self.density * self._calcAllCombination(size)
        for p in self.basePPIDic[candidateP]:
            if p in self.proteinList:
                num_links = num_links +1
                tmpPPIList.append(p)
        try:
            d = float(num_links) / float(self._calcAllCombination(size+1))
        except ZeroDivisionError:
           print "ERROR:",candidateP,num_links,self._calcAllCombination(size+1)
        return d
    
    def _calcAllCombination(self,size):
        ppiSize = size * (size-1) /2
        return ppiSize

    
    def nodeSize(self):
        return self.proteinList.__len__()
    
    def edgeSize(self):
        ppiSize = 0
        for p1 in self.ppiDic.keys():
            for p2 in self.ppiDic[p1]:
                ppiSize = ppiSize +1
        return ppiSize
    
    def printMember(self):
        seq = ""
        for p in self.proteinList:
            seq = string.join([p,seq],"\t")
        return seq        
    
    def printPPI(self):
        for p1 in self.ppiDic: 
            for p2 in self.ppiDic[p2]:
                print p1,p2
            
    def __comp__(self,other):     
        if isinstance(other,Cluster):
            return other.proteinList.issubset(self.proteinList)
        
    def __hash__(self):
        set = frozenset(self.proteinList)
        return set.__hash__();
    
    def hash(self):
        return self.__hash__()
class Cluster2:
    
    def __init__( self ,clusterId,proteinList):
        self.density =1
        #ppi are single direction
         
        self.proteinList = proteinList
        

        #self.ivvCluster = IvvCluster(self.proteinList)
        self.clusterId = clusterId

    def nodeSize(self):
        return self.proteinList.__len__()
    
    def edgeSize(self):
        ppiSize = 0

        nodeSize = self.nodeSize()
        
        ppiSize = nodeSize * (nodeSize-1) /2
        return ppiSize
    
    def printMember(self):
        seq = ""
        for p in self.proteinList:
            seq = string.join([p,seq],"\t")
        return seq           

    def __repr__(self):
        seq = ""
        for p in self.proteinList:
            seq = string.join([p,seq],",")
        return "Cluster.Cluster2"+":"+str(self.clusterId)+"["+seq+"]"

if __name__ == "__main__":
    proteins = ['3725', '1386', '2963', '2353']
    ppiList = {}
    for i in range(len(proteins)):
        for j in range(len(proteins)-1):
            if proteins[i] in self.ppiList:
                self.ppiList[proteins[i]].append(proteins[j]) 
        else:
            self.ppiList[proteins[i]] = [proteins[j]]
    
    ivv_info_file = "C:\Documents and Settings\yo\mt\PPI\data\ppiInfo\ivv_human7.3_info"
    prey_info = Prey_info.Prey_info(ivv_info_file)
