#!/usr/bin/env python

""" Complex prediction by Integer Programming """

import sys
import string
import Cluster
import ClusterCandidateResultReader
import IVV_Packages.YO_Usefuls.GeneID2GeneName as GeneID2GeneName
import Seq_Packages.Motif.Gene_to_SPMotif2 as Gene_to_SPMotif
import IVVInfo2Motif
import PPIFilter
import IVV_Packages.YO_IP.ClusterCandidates2IPFormat3 as ClusterCandidates2IPFormat
import IVV_Packages.YO_Usefuls.TFDic as TFDic
import IVV_Packages.YO_Usefuls.LocalizationDic as LocalizationDic
import IVV_Packages.YO_Usefuls.ClusterScorer3 as ClusterScorer
from lpsolve55 import *

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")
yoc = RSC_II("yoIVV_Config")

class IPVerifyer:
        
    def __init__( self ,clusterMap,basicFilterFlag,ppiFile,verifyID,ivvFlag,yeastFlag):
        sys.stdout.write("IPVerifyer init start\n")
        
        self.verifyID = verifyID
        self.clusterMap=clusterMap
        self.allClusterNum = len(self.clusterMap)
        
        tfDic = TFDic.TFDic()
        self.tfSet = tfDic.getTFSet()
        
        swisspfam_file = rsc.SwissPfam_save        
        swissprot_file = rsc.SProt_Human
        gene2accession_file = rsc.Gene2Accession
        
    
        if yeastFlag:
            sys.stderr.write("yeast mode")
            swisspfam_file = rsc.SwissPfam_save_yeast
            swissprot_file = rsc.SProt_Yeast

        self.gtsp = Gene_to_SPMotif.Gene_to_SPMotif(swissprot_file,gene2accession_file,swisspfam_file)
        sys.stdout.write("Gene_to_SPMotif init done\n")
        ivv_info_file = rsc.IVVInfo
        self.info2motif = IVVInfo2Motif.IVVInfo2Motif(ivv_info_file,basicFilterFlag)
        sys.stdout.write("IVVInfo2Motif init done\n")

        if ivvFlag:
        
            #info2motif.parseIVV_info()
            self.info2motif.getIVV_IR()
            sys.stdout.write("IVVInfo2Motif IR parse done\n")

        self.info2motif.getiPfamMMI()      
        #self.info2motif.getInterdomMMI()
        self.threshold = 0.6
        self.resultDic={}
            
        self.ppiFilter = PPIFilter.PPIFilter(ppiFile)
        self.geneNameDic = GeneID2GeneName.TranslateGeneID2GeneName()
        self.geneNameDic.readGeneInfoFile()
        sys.stdout.write("GeneID2GeneName init done\n")
    
        self.localDic = LocalizationDic.LocalizationDic()
        self.clusterScorer = ClusterScorer.ClusterScorer()    
        
    def verifyClusters(self,clique = False):
        sys.stdout.write("verify clusters\n")
        sys.stderr.write("cluster member geneID,"+
                         "cluster member name,"+
                         "original member geneID,"+
                         "score,"+
                         "structure,"+
                         #"HPRD match,"+
                         #"BIND match,"+
                         "human match,"+
                         "yeast match,"+
                         "IVV flag,"+
                         "TF score,"+
                         "TF member,"+
                         "localization,"+
                         "size,"+
                         "verifyID,"+
                         "\n")
        for clusterID in self.clusterMap:
            self.allClusterNum -=1
            cluster = self.clusterMap[clusterID]
            print self.clusterMap[clusterID]
            candidate = ClusterCandidates2IPFormat.ClusterCandidate(cluster,self.ppiFilter,self.info2motif.geneID2IRDic,self.gtsp,self.info2motif.mmiDic.mmi)
            
            ppiNum = candidate.checkMMI()
            sys.stdout.write(clusterID+" "+str(ppiNum)+"\n")
            if ppiNum >1:
                maxLinks = candidate.getMaxLinks();
                candidate.getDomainBasedVariables()
                candidate.getInteractionBasedVariables()
                candidate.mergeVars()
                candidate.createLP()
                
                candidate.addDomainBasedConstraint()
                candidate.addInteractionBasedConstraints()
                fileName = self.verifyID+"_"+clusterID+".lp"
                #candidate.lpMaker.writeLP(candidate.lp,fileName)
                
                lpsolve('solve', candidate.lp)
                score=lpsolve('get_objective', candidate.lp)
                vars=lpsolve('get_variables', candidate.lp)[0]
                proteinSet =candidate.getProteinSet(vars)
                mmiSet = candidate.getConfirmedMMI(vars)
                tfSet = self.tfMember(proteinSet)
                tfScore = len(tfSet)
                candidate.printMMIs()
                if len(proteinSet) >=3:
                    #hprdPartialMatchCode = self.clusterScorer.comareHPRDComplexSet(candidate.cluster.clusterId,proteinSet )
                    #bindPartialMatchCode = self.clusterScorer.compareKnownComplexSet(candidate.cluster.clusterId, proteinSet)
                    humanPartialMatchCode = self.clusterScorer.compareHumanComplexSet(candidate.cluster.clusterId, proteinSet)
                    yeastPartialMatchCode = self.clusterScorer.compareYeastComplexSet(candidate.cluster.clusterId, proteinSet)
                    
                    localCalc = LocalizationDic.LocalizationCalc(proteinSet, self.localDic)
                    localScore = localCalc.calcLocalizationScore()
                    
                    geneNameSet = self._translateGeneID2GeneName(proteinSet)
                    sys.stderr.write(self._formatSetOrList2Str(proteinSet)+",")
                    sys.stderr.write(self._formatSetOrList2Str(geneNameSet)+",")
                    sys.stderr.write(self._formatSetOrList2Str(candidate.cluster.proteinList)+",")
                    sys.stderr.write(str(int(score))+":"+str(maxLinks)+",")
                    sys.stderr.write(self._formatSetOrList2Str(mmiSet)+",")
                    sys.stderr.write(self._translateMatchCode(humanPartialMatchCode)+",")
                    
                    #sys.stderr.write(self._translateMatchCode(hprdPartialMatchCode)+",")
                    #sys.stderr.write(self._translateMatchCode(bindPartialMatchCode)+",")
                    sys.stderr.write(self._translateMatchCode(yeastPartialMatchCode)+",")
                    sys.stderr.write(str(self._includeIVV(mmiSet))+",")
                    sys.stderr.write(str(tfScore)+",")
                    sys.stderr.write(self._formatSetOrList2Str(tfSet)+",")
                    sys.stderr.write(str(localScore)+",")
                    sys.stderr.write(str(len(proteinSet))+",")
                    sys.stderr.write(self.verifyID+"\n")

    def _includeIVV(self,mmiSet):
        for mmi in mmiSet:
            if mmi.find("IVV")!=-1:
                return True
        return False
    def _translateMatchCode(self,code):
        if code ==2:
            return"complete match"
        elif code == 1:
            return "partial match"
        elif code ==0:
            return "miss"
        else:
            return "error"
    def _formatSetOrList2Str(self,list):
        seq= ""
        for elem in list:
            seq =string.join([str(elem),seq], " ")
        return seq
    def _translateGeneID2GeneName(self,geneIDSet):
        geneNameSet=set()
        for geneID in geneIDSet:
            geneName = self.geneNameDic.translate(geneID)
            geneNameSet.add(geneName)
        return geneNameSet
    
    def tfMember(self,geneIDSet):
        tfList =[]
        for geneID in geneIDSet:
            if geneID in self.tfSet:
                tfList.append(geneID)
        return tfList

def verifyHuman(code):
    bfFlag = True
    ppiFile = yoc.ppi__ppi_ncbi2_hs_ivvBF_txt
    reader = ClusterCandidateResultReader.ClusterCandidateResultReader()
    if code =="MCODE":
        #dic = reader.getMCODEResultDic(yoc.mcodeResults__Mcodepublic_BF_out)
        dic = reader.getMCODEResultDic(yoc.mcodeResults__Mcodepublic_out)
        
    elif code == "ClusteringCoefficient":
        #dic = reader.getCCResultDic(yoc.ccResults__clusteringCoefficient05Public_BF_out)
        dic = reader.getCCResultDic(yoc.ccResults__clusteringCoefficient05Public_out)
    elif code == "MCL":
        dic= reader.getMCLResultDic(yoc.mclResults__MCLpublic_out)
        #dic= reader.getMCLResultDic(yoc.mclResults__MCLpublic_BF_out)
    elif code == "Clique":
        #dic= reader.getCliqueResultDic(yoc.cliqueResults__cliquePublic_BF_out)
        dic= reader.getCliqueResultDic(yoc.cliqueResults__cliquePublic_out)
    elif code == "test":
        dic= reader.getCliqueResultDic(yoc.test_out)
    ivvFlag = True
    yeastFlag = False
    if dic:
        verifyer = IPVerifyer(dic,bfFlag,ppiFile,code,ivvFlag,yeastFlag)
        verifyer.verifyClusters()
    else:
        print "code error"

def verifyYeast(code):
    bfFlag = False
    ppiFile = yoc.yeast_data__yeast_biogrid_y2h_ms_out
    ivvFlag = False
    yeastFlag = True
    reader = ClusterCandidateResultReader.ClusterCandidateResultReader()
    if code =="MCODE":
        dic = reader.getMCODEResultDic(yoc.yeastResults__yeast_mcode_out)
    elif code == "ClusteringCoefficient":
        dic = reader.getCCResultDic(yoc.yeastResults__yeast_cc_out)
    elif code == "MCL":
        dic= reader.getMCLResultDic(yoc.yeastResults__MCLyeast_biogrid_y2h_ms_out)
    if dic:
        verifyer = IPVerifyer(dic,bfFlag,ppiFile,code,ivvFlag,yeastFlag)
        verifyer.verifyClusters()
    else:
        print "code error"

if __name__ == "__main__":
    #code = "ClusteringCoefficient" # sys.argv[1] # MCL, MCODE, etc.
    code = sys.argv[1]
    #sys.stdout.write(code+"\n")
    #verifyHuman(code)
    verifyYeast(code)
    
    
    
    