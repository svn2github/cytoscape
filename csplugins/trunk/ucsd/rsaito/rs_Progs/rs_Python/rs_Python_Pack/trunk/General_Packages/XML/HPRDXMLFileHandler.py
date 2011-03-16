import os
import string
import xml.etree.ElementTree as ElementTree

class GeneLocallization:
    def __init__(self):
        self.localDic = {}
    def findProteinNodes(self,file):
        tree = ElementTree.parse(file)
        query = "//{org:hprd:dtd:hprd}protein"
        nodes = tree.findall(query)   
        return nodes
    def findall(self,hprdDir):
        
        fileList =os.listdir(hprdDir)
        
        for fileName in fileList:
            if fileName.endswith("xml"):
                _file = string.join([hprdDir,fileName],"/")
                proteinNodes = self.findProteinNodes(_file)
                for proteinNode in proteinNodes:
                    entreGeneNode = proteinNode.find(".//{org:hprd:dtd:hprd}EntrezGene")
                    geneID = entreGeneNode.text
                    cellularCNodes = proteinNode.findall(".//{org:hprd:dtd:hprd}cellular_component")
                    for cnode in cellularCNodes:
                        title = cnode.find(".//{org:hprd:dtd:hprd}title")
                        if geneID in self.localDic:
                            self.localDic[geneID].append(title.text)
                        else:
                            self.localDic[geneID] = [title.text]
        
class HPRDXMLFileHandler:
 
    def findall(self,hprdDir,query):
        elemDic ={}
        fileList =os.listdir(hprdDir)
        
        for fileName in fileList:
            if fileName.endswith("xml"):
                _file = string.join([hprdDir,fileName],"/")
                tree = ElementTree.parse(_file)
                modifier = "//{org:hprd:dtd:hprd}"
                _query = string.join([modifier,query],"")
                nodes = tree.findall(_query)   
                elemDic[fileName] = nodes
        return elemDic
 
    def find(self,query,file):
      
        tree = ElementTree.parse(file)
        modifier = "//{org:hprd:dtd:hprd}"
        _query = string.join([modifier,query],"")
        nodes = tree.findall(_query)   
        return nodes
    
    
if __name__ == "__main__":
    #handler = HPRDXMLFileHandler()
    handler = GeneLocallization()
    hprdDir = "C:/home/yo/Python3/data/HPRD_XML"
    
    handler.findall(hprdDir)
    localDic = handler.localDic
    for id in localDic:
        for local in localDic[id]:
            line = string.join([id,local],"\t")
            print line
    
    #query = "protein_interaction"

    
    """
    file = "C:/home/yo/Python3/data/HPRD_XML/01550.xml"
    proteinNodes = handler.find(query, file)
    
    print proteinNodes
    entre = proteinNodes[0].find(".//{org:hprd:dtd:hprd}EntrezGene")
    print entre.text
    cellularC = proteinNodes[0].findall(".//{org:hprd:dtd:hprd}cellular_component")
    print cellularC
    title = cellularC[0].findall(".//{org:hprd:dtd:hprd}title")
    print title[0].text
    """
    """
    elemDic = handler.findall(hprdDir, query)
    
    for id in elemDic:
        for node in elemDic[id]:
            print id,node.text
            
    """