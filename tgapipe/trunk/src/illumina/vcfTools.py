# Tool for parsing illumina generated VCF files
# VCF 4.1 specification:
# http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-41

import sys

class VCFReader(object):

    def __init__(self, fname, vcfType="clia"):
        self.fh = file(fname, 'r')
        self.vcfType = vcfType

    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            if self.vcfType == "clia":
                if not line.startswith("#"):
                    return VCFVariant(line.split("\t"))
                elif line.startswith("##"):
                    return VCFHeaderLine(line)
                else:
                    # FixMe - does this generalize to all VCFs ? 
                    VCFVariant.fields = line.split("\t")
                    VCFVariant.sampleIds = VCFVariant.fields[9:]
            else:
                if not line.startswith("#"):
                    return VCFVariantStd(line.split("\t"))
                elif line.startswith("##"):
                    return VCFHeaderLine(line)
                else:
                    # FixMe - does this generalize to all VCFs ? 
                    VCFVariantStd.fields = line.split("\t")
                    #VCFVariant.sampleIds = VCFVariant.fields[9:]

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
            self.end = str(int(self.start)+self.size-1)
        # INSERTIONS, DELETIONS, BLOCK SUBSTITUTION
        elif len(self.refNuc) > 1 or len(self.altNuc) > 1:
            self.size = abs(len(self.altNuc) - len(self.refNuc))
            # insertion
            if len(self.refNuc) == 1 and len(self.altNuc) > 1:
                self.type = "Ins"
                self.end = str(int(self.start)+1)
                # test for duplication event? - requires pulling surrounding DNA with twoBitToFa - should really get left & right flanking anyway
            # deletion
            elif len(self.refNuc) > 1 and len(self.altNuc) == 1:
                self.type = "Del"
                self.end = str(int(self.start)+self.size-1)
            # block subst
            elif len(self.refNuc) == len(self.altNuc):
                self.size = len(self.refNuc)
                self.end = str(int(self.start)+self.size-1)

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
            alt = alt[1:]
        if self.type == "Del":
            alt = "-"
            ref = ref[1:]
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
    codes = {"1/1":"hom", "0/1":"het", "1/0":"het", "1/2":"het","2/1":"het", None:"None","1|1":"hom","0|1":"het","1|0":"het","1|2":"het","2|1":"het"}
    phases = {"1|1":"1,2","0|1":"2","1|0":"1","1|2":"1,2","2|1":"1,2"}
    fields = []
    sampleIds = []
    def __init__(self, row):
        self.index = VCFVariant.index
        VCFVariant.index += 1
        self.isVariant = True
        self.isSNV = False
        self.chrom = row[0]
        self.pos = row[1]
        self.ID = row[2]
        self.refNuc = row[3]
        self.altNuc = row[4]
        self.qual = row[5]
        self.filter = row[6]
        self.info = row[7]
        self.format = row[8]
        # Illumina data includes clia_GTmax and clia_poly
        # Unless only interested in select polymorphic sites, use clia_GTmax
        self.clia_MAXGT = row[9]
        self.cliaPOLY = row[10]
        self.samples = row[9:]
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
        self.infoDict = {}
        self.flags = []
        self.__processInfo()
        self.__processSamples()
        self.__getZygosity()
        
    def __checkVariant(self):
        """Fail if VCF file contains cases not currently handled"""
        if self.refNuc.find(",") != -1:
            raise Exception("Two reference alleles detected. Check VCF file for errors")

    def __processInfo(self):
        """Create variant info lookup"""
        info = self.info.split(";")
        self.infoDict = dict([[x.split("=")[0],x.split("=")[1]] for x in info if x.find("=") != -1])
        self.flags = [x for x in info if x.find("=") == -1]

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
        for alt in self.altNuc.split(","):
            if len(self.refNuc) == 1:
                sizes[alt] = len(alt) - 1
            elif len(self.refNuc) > 1 and len(alt) == 1:
                sizes[alt] = len(self.refNuc) - 1
            else:
                # If both an insertion and a deletion occured
                # what is the net number or bases inserted/deleted?
                sizes[alt] = abs(len(self.refNuc)-len(alt))
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
            variants.append("\t".join([str(self.index), chrom, str(int(self.pos)-1), self.pos, self.strand, var.refNuc, var.altNuc]))
        return "\n".join(variants)

    def formatForAnnovar(self):
        """Return fields needed for snvget as well as zygosity for each sample"""
        chrom = self.chrom
        if chrom.startswith("chr"):
            chrom = chrom[3:]
        variants = []
        for var in self.vars:
            ref, alt = var.forAnnovar()
            variants.append("\t".join([chrom, self.pos, self.pos, ref, alt, self.zygosity, str(self.index)]))
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

    def getGenotype(self):
        gtypes = {}
        for sample in self.samples:
            gtypes[sample] = self.sampleInfo.get(sample).get("GT")
        return gtypes

    def __getZygosity(self):
        """Shouldn't hard code this"""
        self.zygosity = VCFVariant.codes.get(self.sampleInfo.get("clia_MAXGT").get("GT"))
        

    #def __getZygosity(self):
    #    """This is kind of a hack. Make it better """
    #    gtpred = self.infoDict.get("SGT")
    #    zygosity = gtpred.split("->")[1]
    #    if zygosity in ["het","hom","ref"]:
    #        self.zygosity = zygosity
    #    else:
    #        if zygosity[0] == zygosity[1]:
    #            self.zygosity = "hom"
    #        else:
    #            self.zygosity = "het"


class VCFVariantStd(object):
    """
    Store information about somatic events predicted by Strelka
    """
    index = 1
    # GT notes: 1/1 indicates both alleles are alt, 1/2 indicates one allele is altNuc 1 other allele is altNuc2
    #           0/1 means 1 alt allele and 1 ref allele. If vertical bar is used, the genotypes are phased
    codes = {"1/1":"hom", "0/1":"het", "1/0":"het", "1/2":"het","2/1":"het", None:"None","1|1":"hom","0|1":"het","1|0":"het","1|2":"het","2|1":"het","1":"hom"} # is GT="1" a typo? Based on reads, examples are homozygous for the variant.
    phases = {"1|1":"1,2","0|1":"2","1|0":"1","1|2":"1,2","2|1":"1,2"}
    fields = []
    sampleIds = []
    def __init__(self, row):
        self.index = VCFVariantStd.index
        VCFVariantStd.index += 1
        self.isVariant = True
        self.isSNV = False
        self.chrom = row[0]
        self.pos = row[1]
        self.ID = row[2]
        self.refNuc = row[3]
        self.altNuc = row[4]
        self.qual = row[5]
        self.filter = row[6]
        self.info = row[7]
        self.format = row[8]
        self.sample = row[9]
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
        self.infoDict = {}
        self.flags = []
        self.__processInfo()
        self.__processSamples()
        self.__getZygosity()
        
    def __checkVariant(self):
        """Fail if VCF file contains cases not currently handled"""
        if self.refNuc.find(",") != -1:
            raise Exception("Two reference alleles detected. Check VCF file for errors")

    def __processInfo(self):
        """Create variant info lookup"""
        info = self.info.split(";")
        self.infoDict = dict([[x.split("=")[0],x.split("=")[1]] for x in info if x.find("=") != -1])
        self.flags = [x for x in info if x.find("=") == -1]

    def getInfoFields(self):
        """Get fields of VCF"""
        return self.infoDict.keys()

    def __processSamples(self):
        """Create sample data lookup for fields described by format"""
        if len(self.format.split(":")) != len(self.sample.split(":")):
            raise Exception("Format fields do not match sample fields for variant "+str(self.index)+".")
        self.sampleInfo = dict(zip(self.format.split(":"), self.sample.split(":")))

    def getVarSize(self):
        """Get nucleotide length of variants"""
        sizes = {}
        for alt in self.altNuc.split(","):
            if len(self.refNuc) == 1:
                sizes[alt] = len(alt) - 1
            elif len(self.refNuc) > 1 and len(alt) == 1:
                sizes[alt] = len(self.refNuc) - 1
            else:
                # If both an insertion and a deletion occured
                # what is the net number or bases inserted/deleted?
                sizes[alt] = abs(len(self.refNuc)-len(alt))
        return sizes

    def estimateAF(self):
        """Return # reads for each base / total reads """
        baseCallCounts = self.sampleInfo.get("AU")
        afEstimates = []
        if baseCallCounts != None:
            baseCallCounts = dict(zip(["A","C","G","T"],baseCallCounts.split(",")))
            total = sum(map(int, baseCallCounts.values()))
            afEstimates = [int(baseCallCounts.get(x))/total for x in ["A","C","G","T"]]
        return afEstimates

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
            variants.append("\t".join([str(self.index), chrom, str(int(self.pos)-1), self.pos, self.strand, var.refNuc, var.altNuc]))
        return "\n".join(variants)

    def formatForAnnovar(self):
        """Return fields needed for snvget as well as zygosity for each sample"""
        chrom = self.chrom
        if chrom.startswith("chr"):
            chrom = chrom[3:]
        variants = []
        for var in self.vars:
            ref, alt = var.forAnnovar()
            variants.append("\t".join([chrom, self.pos, self.pos, ref, alt, self.zygosity, str(self.index)]))
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

    def getGenotype(self):
        gtypes = {}
        for sample in self.samples:
            gtypes[sample] = self.sampleInfo.get("GT")
        return gtypes

    def __getZygosity(self):
        """Shouldn't hard code GT"""
        self.zygosity = VCFVariantStd.codes.get(self.sampleInfo.get("GT"))
        if self.zygosity == None:
            GT = self.sampleInfo.get("GT")
            sys.stderr.write("Unrecognized genotype: " + GT +"\n")
            sys.exit(1)

class VarTable(object):

    def __init__(self, filename):
        self.qualByCall = {}
        self.filterByCall = {}
        self.varByCall = {}
        for var in VCFReader(filename, "standard"):
            self.__addVar(var)

    def __addVar(self,var):
        if var.isVariant:
            uid = var.chrom+"_"+var.pos+"_"+var.refNuc+"_"+var.altNuc
            self.qualByCall[uid] = var.qual
            self.filterByCall[uid] = var.filter
            self.varByCall[uid] = var

    def getQuality(self, chrom, pos, ref, alt):
        return self.qualByCall.get(chrom+"_"+pos+"_"+ref+"_"+alt)

    def getFilter(self, chrom, pos, ref, alt):
        return self.filterByCall.get(chrom+"_"+pos+"_"+ref+"_"+alt)

    def getVarInfo(self, chrom, pos, ref, alt):
        return self.varByCall.get(chrom+"_"+pos+"_"+ref+"_"+alt)


    def printMAFheader(self):
        return "\t".join(["Hugo_Symbol","Entrez_Gene_Id","Center","NCBI_Build","Chromosome","Start_position","End_position","Strand","Variant_Classification","Variant_Type","Reference_Allele","Tumor_Seq_Allele1","Tumor_Seq_Allele2","dbSNP_RS","dbSNP_Val_Status","Tumor_Sample_Barcode","Matched_Norm_Sample_Barcode","Match_Norm_Seq_Allele1","Match_Norm_Seq_Allele2","Tumor_Validation_Allele1","Tumor_Validation_Allele2","Match_Norm_Validation_Allele1","Match_Norm_Validation_Allele2","Verification_Status","Validation_Status","Mutation_Status","Sequencing_Phase","Sequence_Source","Validation_Method","Score","BAM_file","Sequencer"])

    def formatAsMAF(self):
        return "\t".join([])



if __name__ == "__main__":
    import sys
    
    # Currently set up to process a VCF for running snvGet 
    # - will need to remove zygosity column prior to calling snvGet
    headerLines = []
    for entry in VCFReader(sys.argv[1]):
        if entry.isVariant:
            formatted = entry.formatForSnvGet()
            #formatted = entry.bedString()
            print formatted
        else:
            headerLines.append(entry)
            
