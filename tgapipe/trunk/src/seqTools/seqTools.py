# Tools to work with 2bit file

import sys
import subprocess

class SeqReader(object):
    """Wrapper around twoBitToFa"""

    complement = {"A":"T","T":"A","C":"G","G":"C","N":"N","-":"-"}
    twobit_dict = {"hg19":"~hcarter/Data/databases/gbdb/hg19/hg19.2bit","hg18":"~hcarter/Data/databases/gbdb/hg18/hg18.2bit"}

    def __init__(self, db="hg19", twoBitLoc="/cellar/users/hcarter/Data/gbdb/", twoBitToFaLoc="/cell/users/hcarter/programs/kent/bin/x86_64/twoBitToFa"):
        self.db = db
        twoBitLoc = twoBitLoc if twoBitLoc.endswith("/") else twoBitLoc+"/"
        self.twoBitFile = twoBitLoc + db+"/"+db+".2bit"
        self.twoBit = twoBitToFaLoc

    def getSeq(self, chrom, start, end, strand="+"):
        """Get sequence for specified range"""
        seq = self.__getBases(chrom, start, end)
        if strand == "-":
            seq = self.__reverseComplement(seq)
        return seq

    def getFlanking(self,chrom,start,end,strand="+",n=1):
        """Return n flanking bases on either side of position specified """
        leftFlank = ""
        rightFlank = ""
        if int(start) == int(end):
            start = str(int(start)-1)
        #print str(int(start)-int(n)), start
        #print end, str(int(end)+int(n))
        leftFlank = self.__getBases(chrom, str(int(start)-int(n)), start)
        rightFlank = self.__getBases(chrom, end, str(int(end)+int(n)))
        if strand == "-":
            left = leftFlank
            right = rightFlank
            leftFlank = self.reverseComplement(right)
            rightFlank = self.reverseComplement(left)
        return leftFlank, rightFlank
                                    
    def __getBases(self, chrom, start, end):
        """Pull sequence from two bit file and read from stdout"""
        #TwobitLoc = Twobit_dict.get(self.db)
        cmd = "~hcarter/programs/kent/bin/x86_64/twoBitToFa " + self.twoBitFile + ":" + chrom + ":" + str(start) + "-" + str(end) + " stdout"
        p = subprocess.Popen(cmd,shell=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
        stdout, stderr = p.communicate()
        if stderr != "":
            raise Exception(stderr)
        return stdout.split("\n")[1].upper()

    def getChromSeq(self, chrom):
        cmd = "~hcarter/programs/kent/bin/x86_64/twoBitToFa " + self.twoBitFile + " -seq=" + chrom + " stdout"
        p = subprocess.Popen(cmd,shell=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
        stdout, stderr = p.communicate()
        if stderr != "":
            raise Exception(stderr)
        return "".join(stdout.split("\n")[1:]).upper()

    def __reverseComplement(self, seq):
        """Function to reverse complement DNA sequence"""
        seq = list(seq)
        seq.reverse()
        rcseq = []
        for i in xrange(len(seq)):
            rcseq.append(SeqReader.complement.get(seq[i]))
        return "".join(rcseq)

    def __repr__(self):
        return "\t".join([self.chrom, self.startpos, self.endpos, self.tsID+"_"+self.aaSubst+"_"+self.nucSubst, "0", self.tsStrand])

    def checkSeq(self, chr, start, end, seq, strand="+"):
        """Check for agreement with reference sequence"""
        twoBitSeq = self.__getBases(chr, start, end)
        if  strand == "-":
            twoBitSeq = self.__reverseComplement(twoBitSeq)
        return seq == twoBitSeq


class Spectrum(object):
    """Object to store base substitions detected in DNA sequencing of a single sample (somatic)"""
    bases = ["A","C","G","T"]

    def __init__(self, length = 3):
        self.spectrum = {}
        self.varspectrum = {}
        self.Ncount = 0
        self.seqlen = length
        if length != 3:
            raise Exception("Extend to support sequence lengths other than 3")
        categories = ["".join([x,y,z]) for x in Spectrum.bases for y in Spectrum.bases for z in Spectrum.bases]
        self.spectrum = dict([[x,0] for x in categories])
        self.varspectrum = dict([[x+"."+y,0] for x in categories for y in Spectrum.bases])

    def add(self, seq):
        """Add a count for the category"""
        if len(seq) != self.seqlen:
            raise Exception(seq + " has length " + str(len(seq)) + ". Length of " + str(self.seqlen) + " expected.")
        if seq.count("N") == 0:
            self.spectrum[seq] += 1
        else:
            self.Ncount += 1

    def addSubst(self, seq):
        """Add a count for the category"""
        if seq.count("N") == 0:
            self.varspectrum[seq] += 1
        else:
            self.Ncount += 1

    def reportNcount(self):
        sys.stderr.write(str(self.Ncount) + " triplets contained N's and were discarded.\n")

    def __repr__(self):
        if sum(self.spectrum.values()) > 0:
            return "\n".join([x + " = " + str(self.spectrum.get(x)) for x in self.spectrum.keys()])
        else:
            return "\n".join([x + " = " + str(self.varspectrum.get(x)) for x in self.varspectrum.keys()])

class StrattonSpectrum(object):
    """Object to store base substitions detected in DNA sequencing of a single sample (somatic)"""
    bases = ["A","C","G","T"]
    substDict = {"C>A":"C>A","C>G":"C>G","C>T":"C>T","G>T":"C>A","G>C":"C>G","G>A":"C>T","T>A":"T>A","T>C":"T>C","T>G":"T>G","A>T":"T>A","A>C":"T>G","A>G":"T>C"}
    tripletCountDict = {"ACC":0.0232584677,"ATG":0.0367292197,"ATC":0.0267012626,"ATA":0.0412313418,"CCT":0.0355180168,"CTC":0.0336680968,"ACA":0.0403365103,"ATT":0.0498550254,"CTG":0.0405232527,"CTA":0.0257891028,"ACT":0.0321733722,"ACG":0.0050260235,"CCA":0.0368876099,"CCG":0.0055222874,"CCC":0.0262949689,"CTT":0.0399048304,"TCG":0.0044142454,"TTA":0.0415961817,"TTT":0.0768579198,"TCA":0.0391726913,"GCA":0.02880276,"GTA":0.022687219,"GCC":0.0237968327,"GTC":0.0188918423,"GCG":0.0047604673,"GTG":0.0300603288,"TTC":0.0394318694,"GTT":0.0291549809,"GCT":0.0279604873,"TTG":0.0378867703,"TCC":0.0308726098,"TCT":0.0442334054}

    def __init__(self):
        self.spectrum = {"C>A":[],"C>G":[],"C>T":[],"T>A":[],"T>C":[],"T>G":[]}

    def addSubst(self,l,r,ref,alt):
        """
        Seq is a triplet of which the middle base was replaced with alt
        
        Note: Strand doesn't matter -> all substitutions are folded into 6 categories
        """
        if len(l+ref+r) != 3:
            raise Exception("Spectrum construction requires nucleotide triplets.")
        subst = ref.upper()+">"+alt.upper()
        subst = StrattonSpectrum.substDict.get(subst)
        if subst == None:
            raise Exception("Invalid substitution")
        if "N" not in [l,r,ref,alt]:
            self.spectrum[subst].append([l,r])
        
    def getSubstCounts(self):
        counts = {}
        for subst in ["C>A","C>G","C>T","T>A","T>C","T>G"]:
            counts[subst] = len(self.spectrum.get(subst))
        return counts

    def __repr__(self):
        text = [] #["RefTriplet\tSubst\tBaseFrom\t5p\t3p\tBaseTo\tRawCount\tNormCount"]
        for subst in ["C>A","C>G","C>T","T>A","T>C","T>G"]:
            neighbors = self.spectrum.get(subst)
            for pair in neighbors:
                seq = "".join(pair[0]+subst[0]+pair[1])
                string = seq + "\t" + pair[0] +"\t" + subst +"." + pair[1] + "\t" + str(neighbors.count(pair))+"\t"+ str(neighbors.count(pair)/StrattonSpectrum.tripletCountDict.get(seq))
                #string = seq +"\t"+ subst +"\t"+ seq[1] +"\t"+ seq[0] +"\t"+ seq[2] + "\t" + subst[-1] + "\t" + str(neighbors.count(pair)) +"\t"+ str(neighbors.count(pair)/StrattonSpectrum.tripletCountDict.get(seq))
                if string not in text:
                    text.append(string)                
        return "\n".join(text)


if __name__ == "__main__":
    spectrum = StrattonSpectrum()
    spectrum.addSubst("A","A","C","T")
    spectrum.addSubst("A","A","C","T")
    spectrum.addSubst("A","A","C","T")
    spectrum.addSubst("G","A","G","T")
    spectrum.addSubst("T","A","A","T")
    spectrum.addSubst("G","C","C","T")
    print spectrum

        
    
