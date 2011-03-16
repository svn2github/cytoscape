import copy
import sys
import string

class Test:
    def __init__(self):
        self.proteinDic={}
        self.mmiDic = {}
        
        
        self.proteinDic["5159"] =["Pkinase_Tyr"]
        self.proteinDic["2885"] =["SH3_1", "SH2"]
        self.proteinDic["5295"] =["SH3_1", "SH2"]
        self.proteinDic["1956"] =["Pkinase_Tyr"]
        self.proteinDic["5747"] =["Pkinase_Tyr"]
        self.proteinDic["6714"] =["Pkinase_Tyr", "SH3_1", "SH2"]
        
        self.mmiDic["Pkinase_Tyr"] = {"SH2":0,"SH3_1":0}
        self.mmiDic["SH2"] = {"Pkinase_Tyr":0,"SH3_1":0}
        self.mmiDic["SH3_1"] = {"Pkinase_Tyr":0,"SH2":0}
        
        """
        self.proteinDic["p1"] = ["m1","m2","m3"]
        self.proteinDic["p2"] = ["m4","m5"]
        self.proteinDic["p3"] = ["m6","m7"]
        self.proteinDic["p4"] = ["m8","m9"]

        self.mmiDic["m1"] = {"m5":0,"m8":0}
        self.mmiDic["m2"] = {"m4":0,"m7":0,"m9":0}
        self.mmiDic["m3"] = {"m5":0,"m7":0}
        self.mmiDic["m4"] = {"m2":0}
        self.mmiDic["m5"] = {"m1":0,"m3":0,"m6":0}
        self.mmiDic["m6"] = {"m5":0,"m9":0}
        self.mmiDic["m7"] = {"m2":0,"m3":0}
        self.mmiDic["m8"] = {"m1":0}
        self.mmiDic["m9"] = {"m6":0,"m2":0}
        """



        """
        self.mmiDic["m1"] = {"m3":0,"m4":0,"m1":0}
        self.mmiDic["m2"] = {"m2":0,"m1":0,"m3":0}
        self.mmiDic["m3"] = {"m1":0,"m5":0,"m2":0}
        self.mmiDic["m4"] = {"m5":0,"m1":0}
        self.mmiDic["m5"] = {"m4":0,"m3":0,"m6":0}
        self.mmiDic["m6"] = {"m5":0}
        
        self.proteinDic["p1"] = ["m1","m2"]
        self.proteinDic["p2"] = ["m3","m4"]
        self.proteinDic["p3"] = ["m5","m2","m4","m1"]
        self.proteinDic["p4"] =["m2","m1"]
        self.proteinDic["p5"] = ["m6"]
        """

        self.independencyCheckDic={}
        self.independentMMI_checkDic={}

        self.allMMI = {}         
        self.independentMMI={}
 
    def _getVariation( self ,returnList,dic,keyList,length,i=0,list=[]):
        
        if i<length:
            for part in dic[keyList[i]]:
                if i == 0:
                    list =[]     
                if (i+1)==length:
                    tmpList = copy.copy(list)
                    tmpList.append(part)
                    returnList.append(tmpList)
                    
                else:
                    if len(list)<=i:
                        list.append(part)
                    else:
                        list[i]=part 
                self._getVariation(returnList,dic,keyList,length,i+1,list)
    
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
        for p1 in self.independencyCheckDic:
            for motif1 in self.independencyCheckDic[p1]:
                linkedProteins = self.independencyCheckDic[p1][motif1].keys()
                if len(linkedProteins) == 1:
                    p2 = linkedProteins[0]
                    for motif2 in self.independencyCheckDic[p1][motif1][p2]:
                        if len(self.independencyCheckDic[p2][motif2])==1:
                            print "verified",p1,p2,motif1,motif2
                            mmiPair= MMIPair(motif1,motif2,p1,p2)
                            if p1 in self.independentMMI_checkDic:
                                if p2 in self.independentMMI_checkDic[p1]:
                                    self.independentMMI_checkDic[p1][p2].append(mmiPair)
                                else:
                                    self.independentMMI_checkDic[p1][p2]=[mmiPair]
                            else:
                                self.independentMMI_checkDic[p1]={p2:[mmiPair]}
        self.independencyCheckDic.clear()
                                
        
    def getALLMMIVariation(self):
        keys =self.proteinDic.keys()
        length =len(keys)
        
        for i in range(length):
            p1 = keys[i]
            for j in range(length-(i+1)):
                p2 = keys[j+i+1]
                if p1 !=p2:
                    selfMotifs = self.proteinDic[p1]
                    for motif1 in selfMotifs:
                        otherMotifs = self.proteinDic[p2]
                        for motif2 in otherMotifs:
                            if self._isExist(motif1,motif2):
                                self._putIndependentCheckDic(p1, p2, motif1, motif2)
                                mmiPair = MMIPair(motif1,motif2,p1,p2)
                                if p1 in self.allMMI:
                                    if p2 in self.allMMI[p1]:
                                        self.allMMI[p1][p2].append(mmiPair)        
                                    else:
                                        self.allMMI[p1][p2] = [mmiPair]  
                                else:
                                    self.allMMI[p1] = {p2:[mmiPair]}  

        print self.independencyCheckDic
    
    def _getMMIListDic(self):
        mmiListDic = {}
        for p1 in self.allMMI:
            for p2 in self.allMMI[p1]:
                if p1 in self.independentMMI_checkDic:
                    if p2 in self.independentMMI_checkDic[p1]:
                        for mmiPair in self.independentMMI_checkDic[p1][p2]:
                            key=string.join([p1,p2],"")    
                            if key in self.independentMMI:
                                self.independentMMI[key].append(mmiPair)
                            else:
                                self.independentMMI[key]=[mmiPair]
                            print "Independent",key,mmiPair
                else:
                        
                    for mmiPair in self.allMMI[p1][p2]:
                    #print p1,p2,mmiPair.getMotif1().getMotifName(),mmiPair.getMotif2().getMotifName()
                        key=string.join([p1,p2],"")
                        if key in mmiListDic:
                            mmiListDic[key].append(mmiPair)
                        else:
                            mmiListDic[key]=[mmiPair]
                            
                        print key,mmiPair
                    
        self.allMMI.clear()
        self.independentMMI_checkDic.clear()
        #self._sortMMIListDic(mmiListDic)
        return mmiListDic
    
    
    
    def _sortMMIListDic(self,mmiListDic):
        mmiListList =[]
        for key in mmiListDic:
            mmiList = MMIList(key,mmiListDic[key])
            mmiListList.append(mmiList)

        mmiListDic.clear()
        #mmiListList.sort()
        mmiListList.reverse()
        for mmiList in mmiListList:
            print mmiList
            mmiListDic[mmiList.key] = mmiList.mmiList    

        for key in mmiListDic:
            print key,mmiListDic[key]

        
        
    def _isExist(self,motif1,motif2):
        flag = False
        if motif1 in self.mmiDic:
            if motif2 in self.mmiDic[motif1]:
                flag = True
        
        return flag

        
class MMIList:
    def __init__(self,key,mmiList):
        self.key = key
        self.mmiList = mmiList
        self.length = len(mmiList)
    def __repr__(self):
        return str(self.key)+":"+str(self.length)
    def __cmp__(self, other):
        return cmp(self.length,other.length)
        
class CacheElement:
    def __init__(self,score,mmiList,hashSet,bit):
        self.score =score
        self.mmiList = mmiList
        self.hashSet = hashSet
        self.bit = copy.copy(bit)
        tmp= []

    def checkIndependency(self,mmiPairList):
        flag = True
        tmpHash = copy.copy(self.hashSet)
        for mmiPair in mmiPairList:
            motif1 = mmiPair.getMotif1()
            motif2 = mmiPair.getMotif2()
            if motif1.hash() in tmpHash or motif2.hash() in tmpHash:
#            if motif1.hash() in self.hashSet or motif2.hash() in tmpHash:
                flag = False
            else:
                tmpHash.add(motif1.hash())
                tmpHash.add(motif2.hash())
        return flag 


class DeltaMMIRevolver:
    def __init__(self,revolvedMMIList,minScore,cacheDic):
        self.mmiList =revolvedMMIList.mmiList
                        
        self.difMMIList = revolvedMMIList.difMMIList
        self.difLength = len(self.difMMIList)
        self.difRepeat = 2**self.difLength 
        self.n = len(self.mmiList)
        self.bit = []
        self.minScore=minScore

        self.cacheDic = cacheDic
        
        self.cacheCreateFlag = True
        self.cacheLevel =0
        self.cacheElementCounter =0
        self.difBitCounter = 0
            
        #print self.mmiList
        #print "dif",self.difMMIList

        if self.difLength > 0 and self.difLength< self.n -1:
            for i in range(self.difLength):
                self.bit.append(0)
        else:
            for i in range(self.n):
                self.bit.append(0)
    def next(self):
        returnList =[]
        tmpSet =set()
        


        if self.cacheLevel == 0 or (self.cacheLevel +1) >= self.n:
            self._countup(0)
            for i in range(self.n):
                #print self.mmiList   
                try: 
                    if self.bit[i] == 1:
                        if self._checkIndependency(self.mmiList[i], tmpSet):
                            returnList.append(self.mmiList[i])
                        else:
                            return None
                except IndexError:
                    print "IndexError!"
                    print "n =",self.n
                    print "difLength=",self.difLength
                    print "bit",self.bit
                    print "cacheLevel", self.cacheLevel
                    
            if returnList:
                score = len(returnList)
            if self.cacheCreateFlag == True:
                self._createCacheDic(score, returnList, tmpSet)
        else:
            #print "cacheUse",self.cacheDic

            cacheElementList = self.cacheDic[self.difLength]
            cacheElement = cacheElementList[self.cacheElementCounter]
    
            self._parseCacheElement(cacheElement)

            difMMI = self._createDifMMI()
            if cacheElement.checkIndependency(difMMI):
                for mmi in difMMI:
                    returnList.append(mmi)
                for mmi in cacheElement.mmiList:
                    returnList.append(mmi)
            elif len(difMMI) ==0:
                for mmi in cacheElement.mmiList:
                    returnList.append(mmi)
            else:
                returnList = [] 
            score = len(returnList)
            if self.cacheCreateFlag == True:
                for mmi in returnList:
                    self._checkIndependency(mmi, tmpSet)
                self._createCacheDic(score, returnList, tmpSet)  
            self._countup(0) 


            
        return returnList
    def _createDifMMI(self):
        difMMI = []
        for i in range(self.difLength):
            if self.bit[i] ==1:
                difMMI.append(self.difMMIList[i])
        return difMMI
    def _parseCacheElement(self,cacheElement):
        self.difBitCounter +=1
    
        if self.difBitCounter == 1:
            self.bit=[]
            for i in range(self.difLength):
                self.bit.append(0)
            tmpCount =0
            for bit in cacheElement.bit:
                if tmpCount >= self.difLength:
                    self.bit.append(bit)
                tmpCount +=1
            #print "cacheElement",cacheElement.bit,cacheElement.mmiList
        elif self.difBitCounter == self.difRepeat:
            self.cacheElementCounter +=1
            self.difBitCounter =0
        #print self.bit
        return cacheElement
    
    def _createCacheDic(self,score,returnList,tmpSet):
        cacheLevel = 0
        if self.cacheLevel == 0:
            cacheLevel = self.n -1
        else:
            cacheLevel = self.cacheLevel
        # if cacheLevel == 0 renew all caches

        keys = range(1,cacheLevel)
        for key in keys:
            if self._isToBeCached(key):
                if score >= self.minScore - key:
                    #print "cacheCreate",returnList,"for",key,self.bit
                    cacheElement = CacheElement(score,returnList,tmpSet,self.bit)
                    if key in self.cacheDic:
                        self.cacheDic[key].append(cacheElement)
                    else:
                        self.cacheDic[key] = [cacheElement]
    def _toBeCountUp(self):
        for bit in self.bit:
            if bit == 0:
                return False
        return True
    def setCacheLevel(self,cacheLevel):
        self.cacheLevel = cacheLevel
        if self.cacheLevel !=1:
            self.cacheCreateFlag = True
    
            keys = range(1,cacheLevel)
            for key in keys:
                if key in self.cacheDic:
                    del self.cacheDic[key]
        else:
            self.cacheCreateFlag = False

    
    def _isToBeCached(self,key):
 #       error !!! to be fixed
        repeat = range(key)
        for i in repeat:
            if self.bit[i] != 0:
                return False
        return True
                
    def getCacheDic(self):
        return self.cacheDic
    def repeatNum(self):
        if self.cacheLevel == 0 or (self.cacheLevel +1) >= self.n:
            return 2**self.n
        else:
            if self.cacheLevel in self.cacheDic:
                cacheLength = len(self.cacheDic[self.cacheLevel])
                return cacheLength*self.difRepeat+1
            else:
                return 0
    def _checkIndependency(self,mmiPair,tmpSet):
        #sys.stderr.write("_checkIndependency\n")
        flag = True
        motif1 = mmiPair.getMotif1()
        motif2 = mmiPair.getMotif2()
        #print motif1,motif1.hash()
        #print motif2,motif2.hash()
        
        
        if motif1.hash() in tmpSet or motif2.hash() in tmpSet:
            flag = False
        else:

            tmpSet.add(motif1.hash())
            tmpSet.add(motif2.hash())
            
        return flag
    def _countup(self,index):
        if self.bit[index] == 0:
            self.bit[index]=1
        else:
            self.bit[index]=0
            if self.n > index+1:
                self._countup(index+1)
        score = 0
        for x in self.bit:
            score += x
        return score


        
class DeltaMMIListRevolver:
    def __init__(self,mmiListDic):
        self.mmiListDic= mmiListDic
        
        self.keys = self._sortKeysByValueLength(mmiListDic)
        
        #for ppi in self.keys:
        #    print "DeltaMMIListRevolver",ppi,mmiListDic[ppi]
        self.indexDic ={}
        
        self.preIndex={}
        
        self.repeat =1
        for key in self.mmiListDic:
            self.indexDic[key]=0
            self.preIndex[key]=0
            self.repeat *= len(self.mmiListDic[key])

        self.n = len(self.keys)
        self.count =0
        self.cacheKey =1
    def _sortKeysByValueLength(self,mmiListDic):
        sortedKeys = []
        mmiListList = []
        for key in mmiListDic:
            mmiList = MMIList(key,mmiListDic[key])
            mmiListList.append(mmiList)
        mmiListList.sort()
        mmiListList.reverse()
        
        for mmiList in mmiListList:
            sortedKeys.append(mmiList.key)
        return sortedKeys
        
    def next(self):
        if self.count < self.repeat: 
            mmiList =[]
            difList=[]
            difBit = self._compareDic()
            self.count+=1
            #print
            #print "index",self.indexDic
            #print "preIndex",self.preIndex
            #print "dif",difBit
            
            i = 0
            cacheLevel = self._getCacheLevel(difBit)
            for key in self.keys:
                mmi = self.mmiListDic[key][self.indexDic[key]]
                mmiList.append(mmi)
                if difBit[i] ==1:
                    difList.append(mmi)
                i+=1
                 
            returnList = RevolvedMMIList(mmiList,difList,cacheLevel)
            
            self._countup(0, self.indexDic)
            if self.count >1:
                self._countup(0, self.preIndex)
            return returnList
        else:
            return False
    def _countup(self,index,dic):
        length = len(self.mmiListDic[self.keys[index]])
        if dic[self.keys[index]] < length-1:
            dic[self.keys[index]] +=1
        else:
            dic[self.keys[index]]=0
            if self.n > index+1:
                self._countup(index+1,dic)
                    
    def _compareDic(self):
        indexBit = []
        for key in self.keys:
            if self.indexDic[key] == self.preIndex[key]:
                indexBit.append(0)
            else:
                indexBit.append(1)
        return indexBit
    def _getCacheLevel(self,difBit):
        key = 0
        for bit in difBit:
            if bit == 1:
                key +=1
        return key

"""
class MMIMotifFactory:
    _instance = None
    def __init__(self):
        MMIMotifFactory._instance = self
        self.mmiMotifMap ={}
    def getInstance(self):
        if MMIMotifFactory._instance == None:
            MMIMotifFactory._instance = MMIMotifFactory()
        return MMIMotifFactory._instance
    getInstance = staticmethod(getInstance)
    def create(self,motif,p):
        mmiMotif=None
        if p in self.mmiMotifMap:
            if motif in self.mmiMotifMap[p]:
                mmiMotif=self.mmiMotifMap[p][motif]
            else:
                mmiMotif = MMIMotif(motif,p)
                self.mmiMotifMap[p][motif] = mmiMotif
        else:
            mmiMotif = MMIMotif(motif,p)
            self.mmiMotifMap[p]= {motif:mmiMotif}
        return mmiMotif
""" 
                
                

class MMIPair:
    def __init__(self,motif1,motif2,p1,p2,sourceID):
#        motifFactory = MMIMotifFactory.getInstance(self)
    #      self.motif2=motifFactory.create(motif2,p2)
 
        self.motif1=MMIMotif(motif1,p1)
        self.motif2=MMIMotif(motif2,p2)
        self.sourceID = sourceID
    def getMotif1(self):
        return self.motif1
    def getMotif2(self):
        return self.motif2
    
    
    def __repr__(self):
        #tmp = self.motif1.getMotifName() +"("+self.motif1.getParentGeneID()+")"+":" +self.motif2.getMotifName()+"("+self.motif2.getParentGeneID()+")"
        #tmp = "["+self.sourceID+" "+self.motif1.getParentGeneID()+"("+self.motif1.getMotifName()+"):" +self.motif2.getParentGeneID()+"("+self.motif2.getMotifName()+")"+"]"
        tmp = self.sourceID+":"+self.motif1.getParentGeneID()+"."+self.motif1.getMotifName()+"_" +self.motif2.getParentGeneID()+"."+self.motif2.getMotifName()
        
        return tmp
    
   
class MMIMotif:
    def __init__(self,motifName,parentGeneID):
        self.motifName = motifName
        self.parentGeneID = parentGeneID
        self.id = string.join([self.parentGeneID,self.motifName],".")

    def getMotifName(self):
        return self.motifName
    def getParentGeneID(self):
        return self.parentGeneID

    def hash(self):
        return self.id.__hash__()

    def __repr__(self):
        return self.id
    
def deltaTest():
    minScore = 5
    print "deltaTest"
    test = Test()
    
    test.getALLMMIVariation()
    test.checkIndependency()
    dic =test._getMMIListDic()

    revolver = DeltaMMIListRevolver(dic)
    mmiRevolver =None
    while True:
        mmiList = revolver.next()
        cacheDic = None
        if mmiRevolver:
            cacheDic =mmiRevolver.getCacheDic()
        else:
            cacheDic = {}
        if mmiList == False:
           print "end"
           break  
        
        else:
            mmiRevolver = DeltaMMIRevolver(mmiList,minScore,cacheDic)
            #print "difMMI",mmiList.difMMIList
            mmiRevolver.setCacheLevel(mmiList.cacheLevel)
            repeat = mmiRevolver.repeatNum() -1
            for i in range(repeat):
                verifiedMMI = mmiRevolver.next()
                if verifiedMMI:
                    for idmmi in test.independentMMI:
                        verifiedMMI.append(idmmi)
                    if len(verifiedMMI) >=minScore:
                        print "verified",len(verifiedMMI),verifiedMMI
            #print
                
class RevolvedMMIList:
    def __init__(self,mmiList,difMMIList,cacheLevel):
#        self.nextCacheBit = nextCacheBit
        self.mmiList = mmiList
        self.difMMIList = difMMIList
        self.cacheLevel = cacheLevel
    def __repr__(self):
        tmp =""
        for bit in self.difMMIList:
            string.join([tmp,bit], ",")
        return tmp
 
if __name__ == "__main__":
    deltaTest()