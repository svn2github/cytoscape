from Seq_Packages.Motif.Pfam_fs1 import Pfam_fs_entries
import TabFileReader
import sys
from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsIVV_Config")
yoc = RSC_II("yoIVV_Config")

class Interdom2Pfam:
    def __init__(self):
        self.pfam_fs = Pfam_fs_entries(rsc.Pfam_fs)
    
    def readInterdom(self):
        tabfile = TabFileReader.Tabfile(yoc.interdom)
        while True:
            line = tabfile.readline()
            if line == False:
                break  
            else:
                interdom1= line[0]
                interdom2 = line[1]
                score = line[6]
                falsePositive = line[8].rstrip()
                
                pfam1 = self.pfam_fs.get_name_from_acc(interdom1)
                pfam2 = self.pfam_fs.get_name_from_acc(interdom2)
                
                #print interdom1,interdom2,falsePositive
                if pfam1 and pfam2:
                    sys.stdout.write(pfam1+"\t"+pfam2+"\t"+score+"\t"+falsePositive+"\n")

if __name__ == "__main__":
    interdom2Pfam = Interdom2Pfam()
    interdom2Pfam.readInterdom()
        