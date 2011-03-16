class MMI:
    def __init__(self):
        self.mmi ={}
        self.motifSet = set()
        
        #self.mmi.setdefault(k,{})
    def getNumMotifs(self):
        return len(self.motifSet)
    def getNumMMI(self):
        return len(self.mmi)/2
    def printDic(self):

        sortedKeyList = sorted(self.mmi.keys())
        count =0
        for motif1 in sortedKeyList:
            count +=1
            #print motif1,self.mmi[motif1]
            for motif2 in self.mmi[motif1]:
                print motif1,"pp" ,motif2
                
    def printMotifs(self):
        tmp = sorted(self.motifSet)
        #for motif in self.motifSet:
        count = 0
        for motif in tmp:
            count +=1
            print count,motif
    def addMMI(self,motif1,motif2,sourceID="default"):
        flag =False
        if motif1 not in self.motifSet:
            self.motifSet.add(motif1)
        if motif2 not in self.motifSet:
            self.motifSet.add(motif2)
        
        """
        if motif1 in self.mmi:
            if motif2 in self.mmi[motif1]:
                print motif1, motif2 
                flag = True
        """
        self.mmi.setdefault(motif1,{motif2:sourceID})
        self.mmi.setdefault(motif2,{motif1:sourceID})
        self.mmi[motif1][motif2]=sourceID
        self.mmi[motif2][motif1]=sourceID
        
        #if flag:
        #    print "###",self.mmi[motif1]
    
        #motifDic= self.mmi.get(motif2,{})
        #motifDic[motif1]=1.0
    
        
if __name__ == "__main__":
    mmi = MMI()
    mmi.addMMI("m1", "m2")
    mmi.addMMI("m2","m3")
    mmi.addMMI("m1","m3")
    
    for m in mmi.mmi:
        print m
        print mmi.mmi[m]
    if "m3" in mmi.mmi["m1"]:
        print "true"
        