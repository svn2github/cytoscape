import string

class Tabfile:
       def __init__( self, filename ):
           self.filename = filename
           self.fh = open( self.filename, "r" )
       def readline( self ):
           line = self.fh.readline()
           if line == "":
               return False
           else:
               return line.split( "\t" )
       def __del__( self ):
           self.fh.close()

class TranslateGeneID2GODescription:
    def __init__(self):
        self.functionDic={}
        self.processDic={}
        self.componentDic={}
        self.defaultDic={}
        
        self.function = "Function"
        self.component = "Component"
        self.process = "Process"
        self.default = "default"
        
    def readGeneInfoFile(self):
        infoFile = Tabfile( "C:/home/yo/Python3/data/gene2go_human"  )
        #infoFile = Tabfile( "../data/test_info"  )
        infoFile.readline()
        while True:
           line = infoFile.readline()
           if line == False:
               break  
           else:
               taxID = line[0]
               geneID = line[1]
               description =line[5]
               category=line[7].strip()
               #geneIDSet = self.geneIndex2geneIDDict.values
               if taxID == "9606" :
                   self.defaultDic[geneID] = geneID
                   if category == "Function":
                       self.functionDic[geneID]=description
                   elif category == "Process":
                       self.processDic[geneID]=description
                   elif category == "Component":
                       self.componentDic[geneID]=description
       

    def printDic(self,category):
        print "canonicalName"
        if category ==self.process:
            for geneID in translater.processDic:
                description = translater.processDic[geneID]
                print string.join([geneID,description], " = ")
        elif category == self.function:
            for geneID in translater.functionDic:
                description = translater.functionDic[geneID]
                print string.join([geneID,description], " = ")
        elif category ==self.component:
            for geneID in translater.componentDic:
                description = translater.componentDic[geneID]
                print string.join([geneID,description], " = ")
        elif category == self.default:
            for geneID in translater.defaultDic:
                description = geneID
                print string.join([geneID,description], " = ")  


    def getUnknownSet(self):

        infoFile = Tabfile( "C:/home/yo/Python3/data/gene2go_human"  )
            #infoFile = Tabfile( "../data/test_info"  )
        infoFile.readline()
        unknownSet= set()
        notUnknownSet = set()
        while True:
           line = infoFile.readline()
           if line == False:
               return unknownSet
           else:
                   
               geneID = line[1]
               goID = line[2]
               description =line[5]
               category=line[7].strip()
               #geneIDSet = self.geneIndex2geneIDDict.values
               if goID == "GO:0005554" :
                   unknownSet.add(geneID)
                   #print geneID
               else:
                   if category == "Function":
                       if geneID in unknownSet:   
                           unknownSet.remove(geneID)
                       else :
                           notUnknownSet.add(geneID)
        while geneID in notUnknownSet:
           unknownSet.discard(geneID)    
       
       
def getunknownDic():
    translater = TranslateGeneID2GODescription()
    unknownDic = translater.getUnknownDic()
    print len(unknownDic)
    
def getNAFiles():
    translater = TranslateGeneID2GODescription()
    translater.readGeneInfoFile()
    
    translater.printDic(translater.default)


if __name__ == "__main__":
    getunknownDic()                