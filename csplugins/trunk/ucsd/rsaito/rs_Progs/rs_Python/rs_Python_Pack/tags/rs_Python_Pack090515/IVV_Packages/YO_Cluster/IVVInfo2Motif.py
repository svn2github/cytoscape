#!/usr/bin/env python

import sys
import IVV_Packages.IVV_Info.IVV_info1 as IVV_info
import IVV_Packages.IVV_Motif.Motif_info1 as Motif_info
import IVV_Packages.YO_Usefuls.TabFileReader as TabFileReader
import MMIDic
import string
import IVV_Packages.IVV_Info.IVV_filter1
#import psyco
#psyco.profile()

import IVV_Packages.IVV_Info.IVV_filter1 as IVV_filter

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")
yoc = RSC_II("yoIVV_Config")
class IVVInfo2Motif:
    def __init__( self,filename,basicFilterFlag=True):
        #sys.stderr.write("Reading IVV information...\n")
        ivv_info_file = rsc.IVVInfo
        
        if basicFilterFlag:
            ivv_prey_filter = rsc.PreyFilter
     
            filter = IVV_filter.IVV_filter()
            filter.set_Prey_filter_file(ivv_prey_filter)
            ivv_info = IVV_info.IVV_info(ivv_info_file, filter)
        else:
            ivv_info = IVV_info.IVV_info(ivv_info_file)

        self.preyInfo = ivv_info.Prey_info()
        self.baitInfo = ivv_info.Bait_info()
        self.mmiDic=MMIDic.MMI()
        self.motifInfo= Motif_info.Motif_info(rsc.MotifInfo2)
        self.geneID2IRDic={}
    def parseIVV_info(self):
        sys.stdout.write("Parsing IVV_info\n")
        for prey in self.preyInfo.preys():
            targetBaitID = self .preyInfo.bait_ID(prey)

            preyMotifIDs=self.motifInfo.get_motif(prey,1.0e-3)
            baitMotifIDs=self.motifInfo.get_motif(targetBaitID,1.0e-3)


            if preyMotifIDs and baitMotifIDs:
                for preyMotifID in preyMotifIDs:
                    for baitMotifID in baitMotifIDs:
                        self.mmiDic.addMMI(preyMotifID,baitMotifID,"IVV")
    def getIVV_IR(self):
        sys.stdout.write("Getting MMI from IVV_IR\n")
       
        file = TabFileReader.Tabfile(yoc.IVV_IR)
        
        while True:
            tmp = file.readline()
            if tmp == False:
                break  
            else:
                if tmp[0].isdigit():
                    baitGeneID= tmp[0]
                    baitDomains = tmp[2]
                    bDomainList =[]
                    
                    preyGeneID= tmp[3]
                    preyDomain = tmp[6]
                    preyIR = tmp[5]
                    if baitDomains.startswith("\""):
                        tmp2=baitDomains.replace("\"", "")
                        bDomainList=tmp2.split(", ")
                        #print bDomainList
                    else:
                        if baitDomains:
                            bDomainList.append(baitDomains)
                    
                    if bDomainList:
                        for baitDomain in bDomainList:
                            if preyDomain:
                                self.mmiDic.addMMI(baitDomain,preyDomain,"IVV_IR")   
                            else:
                                self.mmiDic.addMMI(baitDomain,preyIR,"IVV_IR")                                   
                                if preyGeneID in self.geneID2IRDic:
                                    self.geneID2IRDic[preyGeneID].add(preyIR)
                                else:
                                    self.geneID2IRDic[preyGeneID]=set([preyIR])             
        
    def getiPfamMMI(self):
        #sys.stdout.write("Getting MMI from iPfam\n")
        iPfam_file = rsc.iPfam
        #iPfam_file = "../data/test"
        tabFile = TabFileReader.Tabfile(iPfam_file)
        while True:
           line = tabFile.readline()
           if line == False:
               break
           else: 
               motif1 =line[0]
               motif2=line[1]
               self.mmiDic.addMMI(motif1,motif2,"iPfam")
    def getInterdomMMI(self):
        interdomFile = yoc.interdomIpfam
        tabFile = TabFileReader.Tabfile(interdomFile)
        while True:
           line = tabFile.readline()
           if line == False:
               break
           else: 
               motif1 =line[0]
               motif2=line[1]
               self.mmiDic.addMMI(motif1,motif2,"interdom")
        
    
    def getNumMMI(self):
        return info2motif.mmiDic.getNumMMI()
    def getNumMotifs(self):
        return info2motif.mmiDic.getNumMotifs()
    
if __name__ == "__main__":
    #ppiFile = "../data/ivv_human8.0_info"
    ppiFile="../data/yeast_data/yeast_biogrid_y2h_ms.out"
    
    
    info2motif = IVVInfo2Motif(ppiFile,False)
    #info2motif.getiPfamMMI()
    info2motif.getInterdomMMI()
    #info2motif.getIVV_IR()
    #info2motif.getiPfamMMI()
    #print info2motif.getNumMMI()
    #print info2motif.getNumMotifs()
    info2motif.mmiDic.printDic()
#    print
    #info2motif.mmiDic.printMotifs()
    
    
    
    
        
        
        