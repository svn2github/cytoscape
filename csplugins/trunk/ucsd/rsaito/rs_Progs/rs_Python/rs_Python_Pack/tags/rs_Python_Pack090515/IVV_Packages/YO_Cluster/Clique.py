import sys
import Graph_Packages.PPI.PPi2 as PPi2
import string
import Cluster
from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")
yoc = RSC_II("yoIVV_Config")

class Clique:
    def __init__( self ):
        self.clusterMap = {}
        self.clusterMapByNumNodes ={}
        self.ppi2 = PPi2.PPi2()
        self.flag = True
        self.counter=0
    def readPPIFile( self, fileName ):
        self.ppi2.read_from_file2( fileName, 0, 1, "rin" );
        self.ppi2.both_dir()
        #self.ppi2.ppi_display()
    def printPPIFile( self, fileName ):
        self.ppi2.read_from_file2( fileName, 0, 1, "rin" );
        #self.ppi2.both_dir()
        self.ppi2.ppi_cytoscape_simple1()
    def makeNetwork( self, k=2 ):
        self.counter += 1
        #print "###round###",self.counter
        #print "keySize",self.ppi2.ppi.keys().__len__()
    
        for p1 in self.ppi2.ppi.keys():   
            #print p1,"->",self.ppi2.ppi[p1]     
            #print "size=", self.ppi2.ppi[p1].keys().__len__()
            if k > self.ppi2.ppi[p1].keys().__len__(): 
                self._deleteNode( p1 )
              
        if self._isEnd( k ):
            
            return
        else :
            self.makeNetwork( k )
            
    def _deleteNode( self, key ):
        #print "delete:",key
        for p1 in self.ppi2.ppi[key].keys():
            del self.ppi2.ppi[p1][key]         
        del self.ppi2.ppi[key]
                
    def _isEnd( self, k ):
        for p1 in self.ppi2.ppi.keys():  
            if k > self.ppi2.ppi[p1].keys().__len__():
                return False
        return True


    def getClusterMap(self):
        self.transDic()
        return self.clusterMap
    
    def getPPI(self):
        return self.ppi2.ppi
    def clustering3(self,density=1,numNodes=3):
        ppiDic = self.getPPI()
        clCounter =0
        for p1 in ppiDic.keys():
            for p2 in ppiDic[p1]: 
                if p1 != p2:

                    candidateList = self._getCandidateProteinList(p1, p2)

                    for candidateP in candidateList:
                        clCounter+=1
                        cluster = Cluster.Cluster(p1,p2,ppiDic,clCounter) 
                        if candidateP != p1 and candidateP != p2:
                            cluster.isToBeInCluster(candidateP, density)
                            if cluster.nodeSize() ==numNodes:
                                key = cluster.hash()
                                if numNodes in self.clusterMapByNumNodes:
                                    self.clusterMapByNumNodes[numNodes][key] = cluster
                                else:
                                    self.clusterMapByNumNodes[numNodes] = {key:cluster}
                            
    
    def _mergeCluster(self,numNodes):
        baseDic = self.clusterMapByNumNodes[numNodes]
        keys = baseDic.keys()
        removeKeySet = set()
        flag =False
        clCounter = 0
        for i in range(len(keys)):
            cluster1 = baseDic[keys[i]]
            for j in range(len(keys)-(i+1)):
                clCounter +=1
                cluster2 = baseDic[keys[j+i+1]]
                self._compareCluster(cluster1, cluster2, numNodes, clCounter,removeKeySet)
                #flag = True
                
        for key in removeKeySet:
            del self.clusterMapByNumNodes[numNodes][key]
        if len(removeKeySet)>0:
            flag = True
        return flag
    
    def transDic(self):        
        clCounter = 0
        for n in self.clusterMapByNumNodes:
            sys.stderr.write(str(n)+","+str(len(self.clusterMapByNumNodes[n].keys())))
            for id in self.clusterMapByNumNodes[n]:
     #           print self.clusterMapByNumNodes[n][id].proteinList
                 self.clusterMap[clCounter] = self.clusterMapByNumNodes[n][id]
                 clCounter+=1
        self.clusterMapByNumNodes.clear()
    
    def _compareCluster(self,cluster1,cluster2,numNodes,clCounter,removeKeySet):
        ppiDic = self.getPPI()
        intersectionSet = cluster1.proteinList.intersection(cluster2.proteinList)
        if len(intersectionSet)==numNodes-1:


           p1 = cluster1.proteinList.difference(intersectionSet).pop()
           p2 = cluster2.proteinList.difference(intersectionSet).pop()
           if p1 in ppiDic:
               if p2 in ppiDic[p1]:
                   cluster = Cluster.Cluster(p1,p2,ppiDic,clCounter)
                   for p in intersectionSet:
                       cluster.isToBeInCluster(p,1)
                   key = cluster.hash()
                   if numNodes+1 in self.clusterMapByNumNodes:
                       self.clusterMapByNumNodes[numNodes+1][key] = cluster
                   else:    
                       self.clusterMapByNumNodes[numNodes+1] = {key:cluster}
                   removeKeySet.add(cluster1.hash())
                   removeKeySet.add(cluster2.hash())

        
                    
    def _getCandidateProteinList(self,p1,p2):
        ppiDic = self.getPPI()
        p1Set = set(ppiDic[p1])
        p2Set = set(ppiDic[p2])
        
        return p1Set.intersection(p2Set)
if __name__ == "__main__":
        c  = Clique()
        #c.readPPIFile( "C:\Documents and Settings\yo\mt\PPI\data\ppiInfo\ppi_ncbi_tmp.txt" )
        #c.readPPIFile( "C:\Documents and Settings\yo\mt\PPI\data\ppiInfo\ppi_ncbi_human_ivvSpoke.txt" )     
        #c.readPPIFile( "../data/ppi/ppi_ncbi2_hs+ivvall.txt" )     
        #c.readPPIFile( "../data/ppi/ppi_ncbi2_hs+ivvBF.txt" )     
        #c.readPPIFile( "../data/ppi/ppi_ncbi2_hs.txt" )  
        #c.readPPIFile( "../data/yeast_data/yeast_biogrid_y2h_ms.out" )  
        #c.readPPIFile("mockPPI.txt")
        #c.readPPIFile( "../data/ppi/ppi_ncbi3_hs+ivvBF.txt" )     
        c.readPPIFile( yoc.yeast_data__yeast_biogrid_y2h_ms_out )     
        
        
        k =2     
        matrixFlag = True    
        c.makeNetwork( k )
        density = 1
        numNodes = 3
        c.clustering3(density, numNodes)
           
        flag = True
        while flag == True:
            sys.stderr.write("Merging "+str(numNodes)+" nodes clusters:"+str(len(c.clusterMapByNumNodes[numNodes]))+"\n")
            flag = c._mergeCluster(numNodes)
            numNodes +=1
        
        c.transDic()
        for clusterID in c.clusterMap:
            sys.stdout.write(str(clusterID)+"\t"+c.clusterMap[clusterID].printMember()+"\n")