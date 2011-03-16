import MMIDic
import MMIRevolver2
import sys
import string
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
                                self._putPPI2MMIDic(p1,p2,motif1,motif2,mmiPair)
                                self._putMMI2PPIDic(p1, p2, motif1, motif2,mmiPair)
                                
    def getDomainBasedConstraint(self):
        for motif in self.mmi2ppiDic:
            if len(self.mmi2ppiDic[motif])>1:
                print self._formatSetOrList2Str(self.mmi2ppiDic[motif],"+")+"<=1;"   
            if len(self.mmi2ppiDic[motif])>0:
                for mmiPair in self.mmi2ppiDic[motif]:
                    self.mmiVars.add(str(mmiPair))
                    
    def getInteractionBasedConstraint(self):
        i=0
        seq=""
        for p1 in self.ppi2mmiDic:
            for p2 in self.ppi2mmiDic[p1]:
                ppi=self._getPPI(p1, p2) 
                mmiList=self.ppi2mmiDic[p1][p2]
                self._getPPIAndCandidateMMI(ppi, mmiList)
                #self._setSos(mmiList)
                self.ppiVars.add(ppi)
                if i==0:
                    seq=ppi
                else:    
                    seq =string.join([seq,ppi], "+")
                i+=1
        print "max:",seq,";"
    def mergeVars(self):
        i=0
        for ppiVars in self.ppiVars:
            self.ppiOrMmi2Variables[ppiVars]=i
            self.variables2ppiOrMmi[i]=ppiVars
            i+=1
        for mmiVars in self.mmiVars:
            self.ppiOrMmi2Variables[mmiVars]=i
            self.variables2ppiOrMmi[i]=ppiVars
            i+=1
            print mmiVars,"<=1;"
        
    def variableDec(self):
        print "int",self._formatSetOrList2Str(self.ppiOrMmi2Variables, ","),";"
    def _formatSetOrList2Str(self,list,delim):
        seq= ""
        i=0
        for elem in list:
            if i==0:
                seq=str(elem)
            else:
                seq =string.join([seq,str(elem)], delim)
            i+=1
        return seq
        
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
    def _setSos(self,mmiList):
        print "sos",self._formatSetOrList2Str(mmiList, ","),"<=1;"
    def _getPPIAndCandidateMMI(self,ppi,mmiList):
        print ppi,"=",self._formatSetOrList2Str(mmiList, "+"),";"
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
    cluster.getDomainBasedConstraint()
    cluster.getInteractionBasedConstraint()
    cluster.mergeVars()
    cluster.variableDec()
