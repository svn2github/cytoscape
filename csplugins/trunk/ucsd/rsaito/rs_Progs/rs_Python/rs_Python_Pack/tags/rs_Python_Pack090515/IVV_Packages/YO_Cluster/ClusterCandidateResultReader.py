
import sys
import IVV_Packages.YO_Usefuls.TabFileReader as TabFileReader
import Cluster

class ClusterCandidateResultReader:
    def __init__(self):
        print
        
    def getCliqueResultDic(self,filename):
        sys.stdout.write("Read clusters from clique file:"+filename+"\n")
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
        return clusterMap
    def getCCResultDic(self,filename):
        sys.stdout.write("Read clusters from CC result file: "+filename+"\n")

        tabFile=TabFileReader.Tabfile(filename)
        #line = tabFile.readline()
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
                            geneID =tmp.replace("\n", "")
                            if geneID.isdigit():
    
                                geneIDSet.add(geneID)
                        resultDic[str(complexID)] =Cluster.Cluster2(complexID, geneIDSet)
        return resultDic
    
    def getMCODEResultDic(self,fileName):
        sys.stdout.write("Read clusters from MCODE result file : yo "+fileName+"\n")
        mcodeResultDic ={}
        tabFile=TabFileReader.Tabfile(fileName)
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
                        mcodeResultDic[complexID] =Cluster.Cluster2(complexID,geneIDSet)
        return mcodeResultDic
    
    def getMCLResultDic(self,filename):
        sys.stdout.write("Read clusters from MCL result file: "+filename+"\n")
    
        tabFile=TabFileReader.Tabfile(filename)
        #line = tabFile.readline()
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
                        resultDic[str(complexID)] =Cluster.Cluster2(complexID,geneIDSet)
        return resultDic
    def printDic(self,dic):
        for id in dic:
            print dic[id]
if __name__ == "__main__":
    reader = ClusterCandidateResultReader()
    #dic = reader.getMCODEResultDic("../data/mcodeResults/Mcodepublic.out")
    #dic = reader.getCCResultDic("../data/ccResults/clusteringCoefficient0.5Public.out")
    #dic= reader.getMCLResultDic("../data/mclResults/MCLpublic.out")
    dic= reader.getCliqueResultDic("../data/cliqueResults/cliquePublic.out")
    reader.printDic(dic)
