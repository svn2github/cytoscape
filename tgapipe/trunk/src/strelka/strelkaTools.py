# Tool for parsing Strelka VCF files
# VCF 4.1 specification:
# http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-41

# from .seqTools import SeqFetcher

class VCFReader(object):

    def __init__(self, fname):
        self.fh = file(fname, 'r')
        
    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            if not line.startswith("#"):
                return VCFVariant(line.split("\t"))
            elif line.startswith("##"):
                return VCFHeaderLine(line)
            else:
                # FixMe - does this generalize to all VCFs ? 
                VCFVariant.fields = line.split("\t")
                VCFVariant.sampleIds = VCFVariant.fields[9:]

class VCFHeaderLine(object):
    """
    Parse information from a line of 
    a VCF header file
    """
    def __init__(self, line):
        self.isVariant = False

class VCFHeader(object):
    """
    Allow retrieval of detailed information
    from VCF header
    """
    def __init(self, headerlines):
        pass


class Variant(object):
    """Represent a single subsitution"""
    rcDict = {"A":"T","T":"A","G":"C","C":"G","N":"N","-":"-"}

    def __init__(self, chrom, start, refNuc, altNuc, strand):
        self.chrom = chrom
        self.start = start
        self.end = str(int(self.start)+1)
        self.strand = strand
        self.refNuc = refNuc
        self.altNuc = altNuc
        self.type = ""
        self.size = ""
        self.__process()

    def __process(self):
        """Determine type and size of substitution"""
        # SNVs
        if len(self.refNuc) == 1 and len(self.altNuc) == 1:
            self.type = "SNV"
            self.size = 0
        # INDELS
        elif len(self.refNuc) > 1 and len(self.altNuc) > 1:
            self.type = "Indel"
            self.size = max(len(self.refNuc),len(self.altNuc))
            self.end = str(int(self.start)+self.size)
        # INSERTIONS, DELETIONS, BLOCK SUBSTITUTION
        elif len(self.refNuc) > 1 or len(self.altNuc) > 1:
            # insertion
            if len(self.refNuc) == 1 and len(self.altNuc) > 1:
                self.type = "Ins"
                self.end = str(int(self.start)+1)
                # test for duplication event? - requires pulling surrounding DNA with twoBitToFa - should really get left & right flanking anyway
            # deletion
            elif len(self.refNuc) > 1 and len(self.altNuc) == 1:
                self.size = len(self.refNuc)
                self.type = "Del"
                self.end = str(int(self.start)+self.size-1)
            # block subst
            elif len(self.refNuc) == len(self.altNuc):
                self.size = abs(len(self.altNuc) - len(self.refNuc))
                self.size = len(self.refNuc)
                self.end = str(int(self.start)+self.size)
        if not self.type == "SNV":
            if self.refNuc[0] == self.altNuc[0]:
                self.refNuc = self.refNuc[1:]
                self.altNuc = self.altNuc[1:]
                #self.end = str(int(self.end)-1)
                if self.refNuc == "":
                    self.refNuc = "-"
                if self.altNuc == "":
                    self.altNuc = "-"

    def reverseComplement(self):
        pass

    def forAnnovar(self):
        """Reformat for annovar """
        ref = self.refNuc
        alt = self.altNuc
        if ref == ".":
            ref = "-"
        if alt == ".":
            alt = "-"
        if self.type == "Ins":
            ref = "-"
            #alt = alt[1:]
        if self.type == "Del":
            alt = "-"
            #ref = ref[1:]
        if self.type == "Indel":
            pass
        return ref, alt


class VCFVariant(object):
    """
    Store information about somatic events predicted by Strelka
    """
    index = 1
    # GT notes: 1/1 indicates both alleles are alt, 1/2 indicates one allele is altNuc 1 other allele is altNuc2
    #           0/1 means 1 alt allele and 1 ref allele. If vertical bar is used, the genotypes are phased
    codes = {"1/1":"homo:1/1", "0/1":"het:0/1", "1/0":"het:1/0", "1/2":"het:1/2","2/1":"het:2/1", None:"None","1|1":"homo:1|1","0|1":"het:0|1","1|0":"het:1|0","1|2":"het:1|2","2|1":"het:2|1"}
    phases = {"1|1":"1,2","0|1":"2","1|0":"1","1|2":"1,2","2|1":"1,2"}
    fields = []
    sampleIds = []
    def __init__(self, row):
        # VCF variant information
        self.index = VCFVariant.index
        VCFVariant.index += 1
        self.chrom = row[0]
        self.pos = row[1]
        self.ID = row[2]
        self.refNuc = row[3]
        self.altNuc = row[4]
        self.qual = row[5]
        self.filter = row[6]
        self.info = row[7]
        self.format = row[8]
        # For Strelka output, samples are tumor and normal
        self.normal = row[9]
        self.tumor = row[10]        
        # QC
        self.__checkVariant()
        # Derived variables
        self.isVariant = True
        self.isSNV = False
        self.phase = None
        self.start = str(int(self.pos)-1)
        self.end = self.pos
        self.strand = "+"
        self.zygosity = ""
        altNucs = self.altNuc.split(",")
        self.vars = []
        for nuc in altNucs:
            self.vars.append(Variant(self.chrom, self.start, self.refNuc, nuc, self.strand))
        types = [x.type for x in self.vars]
        if types.count("SNV") == len(types):
            self.isSNV = True
        self.infoDict = {}
        self.flags = []
        self.__processInfo()
        self.__getZygosity()
        self.__getQuality()
        #self.__processSamples()


    def __checkVariant(self):
        """Fail if VCF file contains cases not currently handled"""
        if self.refNuc.find(",") != -1:
            raise Exception("Two reference alleles detected. Check VCF file for errors")

    def __processInfo(self):
        """Create variant info lookup"""
        info = self.info.split(";")
        self.infoDict = dict([[x.split("=")[0],x.split("=")[1]] for x in info if x.find("=") != -1])
        self.flags = [x for x in info if x.find("=") == -1]

    def __getZygosity(self):
        """This is kind of a hack. Make it better """
        gtpred = self.infoDict.get("SGT")
        zygosity = gtpred.split("->")[1]
        if zygosity in ["het","hom","ref"]:
            self.zygosity = zygosity
        else:
            if zygosity[0] == zygosity[1]:
                self.zygosity = "hom"
            else:
                self.zygosity = "het"
        
    def __getQuality(self):
        if self.isSNV:
            self.qual = self.infoDict.get("QSS")
        else:
            self.qual = self.infoDict.get("QSI")

    def getInfoFields(self):
        """Get fields of VCF"""
        return self.infoDict.keys()

    def __processSamples(self):
        """Create sample data lookup for fields described by format"""
        if len(self.format.split(":")) != len(self.samples[0].split(":")):
            raise Exception("Format fields do not match sample fields for variant "+str(self.index)+".")
        self.sampleInfo = {}
        for i in xrange(len(VCFVariant.sampleIds)):            
            self.sampleInfo[VCFVariant.sampleIds[i]] = dict(zip(self.format.split(":"), self.samples[i].split(":")))

    def getVarSize(self):
        """Get nucleotide length of variants"""
        sizes = {}
        for var in self.vars:
            sizes[var.altNuc] = var.size # Should this be var.size + 1 ? 
        return sizes

    def getInfoCodes(self):
        """Get codes for variant info"""
        return self.infoDict.keys()

    def getInfo(self, code):
        """Get variant info"""
        return self.infoDict.get(code)
            
    def bedString(self):
        """Format as bed"""
        chrom = self.chrom
        if not chrom.startswith("chr"):
            chrom = "chr" + chrom
        return "\t".join([chrom, str(int(self.pos)-1), self.pos, str(self.index) + "_" + self.refNuc+"/"+self.altNuc, "0", self.strand])

    def formatForSnvGet(self):
        """Return fields needed for snvget as well as zygosity for each sample"""
        chrom = self.chrom
        if not chrom.startswith("chr"):
            chrom = "chr" + chrom
        if self.altNuc == ".":
            self.altNuc = self.refNuc
        variants = [] 
        for var in self.vars:
            variants.append("\t".join([str(self.index), chrom, str(int(self.pos)-1), self.pos, self.strand, self.refNuc, var.altNuc]))
        return "\n".join(variants)

    def formatForAnnovar(self):
        """Return fields needed for snvget as well as zygosity for each sample"""
        chrom = self.chrom
        if chrom.startswith("chr"):
            chrom = chrom[3:]
        variants = []
        for var in self.vars:
            ref, alt = var.forAnnovar()
            variants.append("\t".join([chrom, self.pos, var.end, ref, alt, self.zygosity, str(self.index)]))
        return "\n".join(variants)

    def formatForSIFT(self):
        """Write variants in format used by SIFT4.0.3 """
        chrom = chrom[3:] if self.chrom.startswith("chr") else self.chrom
        strand = "1" if self.strand == "+" else "-1"
        variants = []
        for alt in self.altNuc.split(","):
            variants.append(",".join([chrom, self.pos, strand, self.refNuc +"/"+alt,"#"+str(self.index)]))
        return "\n".join(variants) 

    def formatForPPH2mapsnps(self):
        """Write variants in format used by PPH2 mapper"""
        chrom = self.chrom if self.chrom.startswith("chr") else "chr"+self.chrom
        variants = []
        for alt in self.altNuc.split(","):
            variants.append("\t".join([chrom+":"+self.pos, self.refNuc+"/"+alt]))
        return "\n".join(variants)

if __name__ == "__main__":
    import sys
    
    # Currently set up to process a VCF for running snvGet 
    # - will need to remove zygosity column prior to calling snvGet
    headerLines = []
    for entry in VCFReader(sys.argv[1]):
        if entry.isVariant:
            #formatted = entry.formatForSnvGet()
            #formatted = entry.bedString()
            formatted = entry.formatForAnnovar()
            print formatted
        else:
            headerLines.append(entry)
            
