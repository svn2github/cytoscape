# Tools to work with HPRD data

#class HPRDDBReader(object):

class HPRDFlatReader(object):

    def __init__(self, filename, filetype=""):
        self.fh = file(filename,'r')
        self.filetype = filetype

    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            if self.filetype == "ptm":
                return PTM(line.split("\t"))
            elif self.filetype == "lookup":
                return HPRDXref(line.split("\t"))
            elif self.filetype == "arch":
                return Architecture(line.split("\t"))

class Architecture(object):

    def __init__(self, row):
        self.row = row
        self.protcol = 2
        self.ID = row[0]
        self.ID2 = row[1]
        self.refProt = row[2]
        self.hgnc = row[3]
        self.arch = row[4]
        self.archtype = row[5]
        self.start = row[6]
        self.end = row[7]
        self.source = row[8]
        self.sourceID = row[9]

    def formatForProteinPlot(self):
        return "\t".join([self.arch, self.start, self.end])

class PTM(object):

    def __init__(self, row):
        self.row = row
        self.protcol = 3
        self.ID = row[0]
        self.gene = row[1]
        self.ID2 = row[2]
        self.refProt = row[3]
        self.pos = row[4]
        if self.pos.find(";") != -1:
            self.pos = self.pos.split(";")[0]
        self.aa = row[5]
        self.enzyme = row[6]
        self.enzymeID = row[7]
        self.activity = row[8]
        self.experiment = row[9]
        self.pmid = row[10]

    def formatForProteinPlot(self):
        return self.pos

class HPRDXref(object):

    def __init__(self, row):
        self.row = row

    def getInfo(self, rowIndex):
        return self.row[rowIndex]

class HPRDdict(object):

    cols = {"hprd":0,"hgnc":1,"refseq":2,"refprot":3,"entrez":4,"":5,"uniprot":6,"desc":7}

    def __init__(self, filename):
        self.hgncDict = {}
        self.NMDict = {}
        for entry in HPRDFlatReader(filename, "lookup"):
            self.hgncDict[entry.getInfo(HPRDdict.cols.get("hgnc"))] = entry

    def hgncConvert(self, hgnc, idtype):
        """Convert hgnc to other identifier"""
        if idtype not in HPRDdict.cols.keys():
            raise Exception("IDtype not supported by HPRD.")
        val = ""
        xref = self.hgncDict.get(hgnc)
        if xref != None:
            val = xref.getInfo(HPRDdict.cols.get(idtype))
        return val


class HPRDTbl(object):

    def __init__(self, filename, filetype):
        self.rowByNP = {}
        for entry in HPRDFlatReader(filename, filetype):
            self.__addEntry(entry)

    def __addEntry(self, entry):
        NPID = entry.row[entry.protcol]
        if self.rowByNP.has_key(NPID):
            self.rowByNP[NPID].append(entry)
        else:
            self.rowByNP[NPID] = [entry]

    def getProtData(self, protID):
        return self.rowByNP.get(protID)


