# Tools to work with breakdancer output

class BDReader(object):

    def __init__(self, filename):
        self.fh = file(filename, 'r')
    
    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            if not line.startswith("#") and not line.startswith("NA"):
                line = line[:-1]
                return BDVar(line.split("\t"))

class BDVar(object):

    def __init__(self, row):
        self.chr1 = row[0]
	self.pos1 = row[1]
	self.orientation1 = row[2]
	self.chr2 = row[3]
	self.pos2 = row[4]
	self.orientation2 = row[5]
	self.type = row[6]
	self.size = row[7]
	self.score = row[8]
	self.numReads = row[9]
	self.numReadsLib = row[10]
        if len(row) < 12:
            self.sample = ""
        else:
            self.sample = row[11]
        self.DGVstatus = ""
        self.DGVtype = ""
        self.somatic = ""
        if len(row) > 12:
            self.DGVstatus = row[12]
            self.DGVtype = row[13]
            self.somatic = row[14]

    def __repr__(self):
        return "\t".join([self.chr1, self.pos1, self.orientation1, self.chr2, self.pos2, self.orientation2, self.type, self.size, self.score, self.numReads, self.numReadsLib, self.sample, self.DGVstatus, self.DGVtype, self.somatic])


class BDTable(object):
    
    def __init__(self, filename):
        self.bdByType = {}
        for var in BDReader(filename):
            self.__addVar(var)

    def __addVar(self, var):
        """Store variants by chromosome to reduce search space"""
        if self.bdByType.has_key(var.type):
            self.bdByType[var.type].append(var)
        else:
            self.bdByType[var.type] = [var]

    def overlaps(self, var):
        """Detect overlap"""
        bdVars = self.bdByType.get(var.type)
        rangeStart = Range(var.pos1) 
        rangeEnd = Range(var.pos2)
        bdOverlap = [x for x in bdVars if var.chr1 == x.chr1 and var.chr2 == x.chr2 and rangeStart.isInRange(x.pos1) and rangeEnd.isInRange(x.pos2)]
        return bdOverlap
    
    def matches(self, var):
        """Detect exact match """
        bdVars = self.bdByType.get(var.type)
        bdMatch = [x for x in bdVars if x.chr1 == var.chr1 and x.chr2 == var.chr2 and x.pos1 == var.pos1 and x.pos2 == var.pos2]
        return bdMatch


class Range(object):

    def __init__(self, pos):
        self.start = int(pos)-1000
        self.end = int(pos)+1000
        
    def isInRange(self, pos):
        return int(pos) <= self.end and int(pos) >= self.start
