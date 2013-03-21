# Tools to parse annovar report files

import sys

class AnnovarReader(object):
    
    def __init__(self, filename, anntype="region", dbname=None):
        self.fh = file(filename, 'r')
        self.anntype = anntype
        self.dbname = dbname

    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            return Annotation(line.split("\t"), self.anntype, self.dbname)

class Annotation(object):
    
    def __init__(self, row, anntype="region",dbname=None):
        self.row = row
        self.anntype = anntype
        if anntype == "gene":
            self.__parseMapping(row, dbname)
        elif anntype == "region":
            self.__parseRegion(row, dbname)
        elif anntype == "filter":
            self.__parseFilter(row, dbname)
        elif anntype == "function":
            self.__parseFunction(row, dbname)
        else:
            raise Exception("Annotation type " + str(anntype) + " not supported")

    def __parseMapping(self, row, dbname):
        self.dbname = row[0] if dbname == None else dbname
        self.varclass = row[1]
        self.annotation = "_".join(row[1].split(" ")) + "=" + row[2]
        self.chrom = row[3]
        self.start = str(int(row[4])-1)
        self.end = row[5]
        self.ref = row[6]
        self.alt = row[7]
        self.zyg = row[8]
        self.index = row[9]
        # Need a code to uniquely identify each variant
        self.uid = "_".join([self.chrom,self.start,self.ref,self.alt])

    def __parseRegion(self, row, dbname):
        self.dbname = row[0] if dbname == None else dbname
        self.annotation = row[1]
        self.chrom = row[2]
        self.start = str(int(row[3])-1)
        self.end = row[4]
        self.ref = row[5]
        self.alt = row[6]
        self.zyg = row[7]
        self.index = row[8]
        # Need a code to uniquely identify each variant
        self.uid = "_".join([self.chrom,self.start,self.ref,self.alt])

    def __parseFilter(self, row, dbname):
        self.dbname = row[0] if dbname == None else dbname
        self.annotation = row[1]
        self.chrom = row[2]
        self.start = str(int(row[3])-1)
        self.end = row[4]
        self.ref = row[5]
        self.alt = row[6]
        self.zyg = row[7]
        self.index = row[8]
        # Need a code to uniquely identify each variant
        self.uid = "_".join([self.chrom,self.start,self.ref,self.alt])

    def __parseFunction(self, row, dbname):
        self.dbname = row[0] if dbname == None else dbname
        self.annotation = row[0]+"="+row[1]
        self.chrom = row[2]
        self.start = str(int(row[3])-1)
        self.end = row[4]
        self.ref = row[5]
        self.alt = row[6]
        self.zyg = row[7]
        self.index = row[8]
        # Need a code to uniquely identify each variant
        self.uid = "_".join([self.chrom,self.start,self.ref,self.alt])

    def __repr__(self):
        return "\t".join([self.anntype, str(self.dbname), "\t".join(self.row)])

class Variant(object):
    """Store all annotations for the same variant """

    def __init__(self, ann):
        self.index = ann.index
        self.chrom = ann.chrom
        self.start = ann.start
        self.end = ann.end
        self.ref = ann.ref
        self.alt = ann.alt
        self.zyg = ann.zyg
        # Code to uniquely identify each variant
        self.uid = "_".join([self.chrom,self.start,self.ref,self.alt])
        self.annotations = {}

    def addAnnotation(self, ann):
        self.annotations[ann.dbname] = ann.annotation

    def setAnnotationOrder(self, dbnames):
        self.annstring = "\t".join([self.annotations.get(x) if x in self.annotations.keys() else "" for x in dbnames])

    def __repr__(self):
        return "\t".join([str(self.index), self.chrom, self.start, self.end, self.ref, self.alt, self.annstring])


class AnnTable(object):
    """Associate mutliple annotations with variant objects"""

    def __init__(self):
        self.varsByUid = {}
        self.annotationdbs = []
        
    def addAnnotation(self, filename, anntype, dbname=None):
        """Add annotations from file"""
        ann = None
        for ann in AnnovarReader(filename, anntype, dbname):
            self.__addAnnotation(ann)
        if not ann == None:
            self.annotationdbs.append(ann.dbname)

    def __addAnnotation(self, ann):
        """Create variant or add annotation to existing variant"""
        if not self.varsByUid.has_key(ann.uid):
            self.varsByUid[ann.uid] = Variant(ann)
        self.varsByUid[ann.uid].addAnnotation(ann)

    def printVars(self):
        print "Index\tchrom\tstart\tend\tref\talt\t" + "\t".join(self.annotationdbs)
        for var in self.varsByUid.values():
            var.setAnnotationOrder(self.annotationdbs)
            print var


class AnnovarSummaryReader(object):
    
    def __init__(self, filename):
        self.fh = file(filename, 'r')
        self.header = self.fh.readline().rstrip().split("\t")

    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            return AnnotatedVar(line.split("\t"), self.header)

class AnnotatedVar(object):

    order = ["Qvalue","1000g2012apr_eur","esp6500_all","snp135","gwas","variant_function","exonic_function","segdup","dgv","tfbs","tarbase","wgtRNA","wgRNA","mce46way","ljb_all"]

    def __init__(self, row, rownames, zerobased = True, order=None):
        self.index = row[0]
	self.chrom = row[1]
	self.start = row[2]
	self.end = row[3]
	self.ref = row[4]
	self.alt = row[5]
        if zerobased:
            #Assume that coordinates are zero-based 
            # half open unless specified otherwise
            self.pos = str(int(self.start)+1) 
        else:
            self.pos = self.start
        if not self.chrom.startswith("chr"):
            self.chrom = "chr" + self.chrom
        self.uid = self.chrom+"_"+self.pos+"_"+self.ref+"_"+self.alt
        self.annotations = dict([[rownames[x], row[x]] for x in xrange(6,len(row))])
        if order != None:
            AnnotatedVar.order = order
        self.order = AnnotatedVar.order
        self.printHeader = False

    def getAnnotations(self):
        return self.annotations.keys()

    def addAnnotation(self, key, value, pos=None):
        self.annotations[key] = value
        if key not in self.order:
            if pos == None:            
                self.order.append(key)
            else:
                self.order.insert(int(pos)-1, key)

    def __repr__(self):
        order = [x for x in self.order if self.annotations.has_key(x)]
        if self.printHeader == True:
            print "index\tchrom\tstart\tend\tref\talt\t" + "\t".join(order)
        valstr = "\t".join([self.annotations.get(x) for x in order])
        return "\t".join([self.index,self.chrom,self.start,self.end,self.ref,self.alt,valstr])
                         

class AnnoVarTable(object):

    def __init__(self, filename):
        self.varsByUid = {}
        for var in AnnovarSummaryReader(filename):
            self.varsByUid[var.uid] = var

    def getVars(self):
        return self.varsByUid.values()

if __name__ == "__main__":
    pass
