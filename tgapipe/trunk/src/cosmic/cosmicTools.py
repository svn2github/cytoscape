class COSMICReader(object):

    def __init__(self, filename):
        self.fh = file(filename, 'r')
        header = self.fh.readline()

    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            return CosmicVar(line.split("\t"))

class CosmicVar(object):

    def __init__(self, row):
        self.row = row
        self.GeneID = row[0]
	self.AccessionNumber = row[1]
	self.GeneCDSlength = row[2]
	self.HGNCNo = row[3]
	self.Samplename = row[4]
	self.ID_sample = row[5]
	self.ID_tumour = row[6]
	self.Primarysite = row[7]
	self.Sitesubtype = row[8]
	self.Primaryhistology = row[9]
	self.Histologysubtype = row[10]
	self.Genomewidescreen = row[11]
	self.MutationID = row[12]
	self.MutationCDS = row[13]
	self.MutationAA = row[14]
        self.refAA = ""
        self.altAA = ""
        self.site = ""
        if self.MutationAA.startswith("p."):
            desc = self.MutationAA[2:]
            if len(desc) >= 3:
                if desc.find("fs*") != -1:
                    loc,size = desc.split("fs*")
                    if loc != "":
                        self.refAA = loc[0]
                        self.site = loc[1:]
                        self.altAA = "fs*"
                    else:
                        self.refAA = ""
                        self.altAA = ""
                        self.site = ""
                elif desc.find("del") != -1:
                    if desc.find("_") != -1:
                        start, end = desc.split("_")
                        self.refAA = start[0]
                        self.site = start[1:]
                        self.atlAA = end[0] +"del"
                    else:
                        self.refAA = ""
                        self.altAA = ""
                        self.site = ""
                else:
                    self.refAA = desc[0]
                    self.altAA = desc[-1]
                    self.site = desc[1:-1]
	self.MutationDescription = row[15]
	self.Mutationzygosity = row[16]
	self.MutationNCBI36genomeposition = row[17]
	self.MutationNCBI36strand = row[18]
	self.MutationGRCh37genomeposition = row[19]
	self.MutationGRCh37strand = row[20]
	self.Mutationsomaticstatus = row[21]
	self.Pubmed_PMID = row[22]
	self.Samplesource = row[23]
	self.Tumourorigin = row[24]
	self.Comments = row[25]
        self.NPID = None

    def formatForProteinPlot(self):
        return "\t".join([self.NPID, self.GeneID, self.site, self.refAA, self.altAA])

    def formatForMuppit(self):
        return "\t".join(["chr" + self.MutationGRCh37genomeposition.split(":")[0],self.MutationGRCh37genomeposition.split(":")[1].split("-")[0]])


class COSMICTbl(object):

    COSMICdump = "/Users/maverick/Lab/projects/pm/src/cosmic/CosmicMutantExport_v63_300113.tsv"

    def __init__(self, filename=None):
        if filename == None:
            filename = COSMICTbl.COSMICdump
        self.varsByGene = {}
        for var in COSMICReader(filename):
            self.__addVar(var)

    def __addVar(self, var):
        if self.varsByGene.has_key(var.GeneID):
            self.varsByGene[var.GeneID].append(var)
        else:
            self.varsByGene[var.GeneID] = [var]

    def getVars(self, gene):
        return self.varsByGene.get(gene)


