# Objects to work with TCGA Maf Files

class MAFReader(object):
    "Hugo_Symbol	Entrez_Gene_Id	Center	NCBI_Build	Chromosome	Start_position	End_position	Strand	Variant_Classification	Variant_Type	Reference_Allele	Tumor_Seq_Allele1	Tumor_Seq_Allele2	dbSNP_RS	dbSNP_Val_Status	Tumor_Sample_Barcode	Matched_Norm_Sample_Barcode	Match_Norm_Seq_Allele1	Match_Norm_Seq_Allele2	Tumor_Validation_Allele1	Tumor_Validation_Allele2	Match_Norm_Validation_Allele1	Match_Norm_Validation_Allele2	Verification_Status	Validation_Status	Mutation_Status	Sequencing_Phase	Sequence_Source	Validation_Method	Score	BAM_File	Sequencer"

    def __init__(self, filename):
        self.fh = file(filename, 'r')
        header = self.fh.readline()
        if header[:-1].upper() != MafReader.header.upper():
            raise Exception("Maf header mismatch!")

    def __iter__(self):
        return self

    def next(self):
        while True:
            line = self.fh.readline()
            if line == "":
                self.fh.close()
                raise StopIteration
            line = line[:-1]
            return MafEntry(line.split("\t"))

class MafEntry(object):
   index = 1

   def __init__(self, row):
      self.index = MafEntry.index
      MafEntry.index += 1
      self.Hugo_Symbol = row[0]
      self.Entrez_Gene_Id = row[1]
      self.Center = row[2]
      self.NCBI_Build = row[3]
      self.Chromosome = row[4]
      if not self.Chromosome.startswith("chr"):
         self.Chromosome = "chr" + self.Chromosome
      self.Start_position = str(int(row[5])-1)
      self.End_position = row[6]
      self.Strand = row[7]
      self.Variant_Classification = row[8]
      self.Variant_Type = row[9]
      self.Reference_Allele = row[10]
      self.Tumor_Seq_Allele1 = row[11]
      self.Tumor_Seq_Allele2 = row[12]
      self.dbSNP_RS = row[13]
      self.dbSNP_Val_Status = row[14]
      self.Tumor_Sample_Barcode = row[15]
      self.sampleID = self.Tumor_Sample_Barcode[:12]
      self.Matched_Norm_Sample_Barcode = row[16]
      self.Match_Norm_Seq_Allele1 = row[17]
      self.Match_Norm_Seq_Allele2 = row[18]
      self.Tumor_Validation_Allele1 = row[19]
      self.Tumor_Validation_Allele2 = row[20]
      self.Match_Norm_Validation_Allele1 = row[21]
      self.Match_Norm_Validation_Allele2 = row[22]
      self.Verification_Status = row[23]
      self.Validation_Status = row[24]
      self.Mutation_Status = row[25]
      self.Sequencing_Phase = row[26]
      self.Sequence_Source = row[27]
      self.Validation_Method = row[28]
      self.Score = row[29]
      self.BAM_File = row[30]
      self.Sequencer = row[31]
      self.Alt_Allele = ""
      self.__getAltAllele()

   def __getAltAllele(self):
      altAlleles = [self.Tumor_Seq_Allele1, self.Tumor_Seq_Allele2]
      if self.Reference_Allele in altAlleles:
         altAlleles.remove(self.Reference_Allele)
      altAlleles = list(set(altAlleles))
      if len(altAlleles) == 1:
         self.Alt_Allele = altAlleles[0]
      else:
         sys.stderr.write("Compound heterozygote detected\n")
         sys.exit(0)      

   def formatForCHASM(self):
       return "\t".join([self.Tumor_Sample_Barcode, self.Chromosome, self.Start_position, self.End_position, self.Strand, self.Reference_Allele, self.Alt_Allele])

   def formatForCRAVAT(self):
       return "\t".join([str(self.index), self.Chromosome, self.Start_position, self.End_position, self.Strand, self.Reference_Allele, self.Alt_Allele, self.Tumor_Sample_Barcode])

   def __repr__(self):
      return "\t".join([self.Hugo_Symbol, self.Entrez_Gene_Id, self.Center, self.NCBI_Build, self.Chromosome, self.Start_position, self.End_position, self.Strand, self.Variant_Classification, self.Variant_Type, self.Reference_Allele, self.Tumor_Seq_Allele1, self.Tumor_Seq_Allele2, self.dbSNP_RS, self.dbSNP_Val_Status, self.Tumor_Sample_Barcode, self.Matched_Norm_Sample_Barcode, self.Match_Norm_Seq_Allele1, self.Match_Norm_Seq_Allele2, self.Tumor_Validation_Allele1, self.Tumor_Validation_Allele2, self.Match_Norm_Validation_Allele1, self.Match_Norm_Validation_Allele2, self.Verification_Status, self.Validation_Status, self.Mutation_Status, self.Sequencing_Phase, self.Sequence_Source, self.Validation_Method, self.Score, self.BAM_File, self.Sequencer])


class MAFTable(object):
    """
    Allow maf entries to be grouped based on field content for select fields.
    """
    groupings = ["Hugo_Symbol","Entrez_Gene_Id","Center","NCBI_Build","Chromosome","Strand","Variant_Classification","Variant_Type","Reference_Allele","Alt_Allele","Tumor_Sample_Barcode","sampleID","Verification_Status","Validation_Status","Mutation_Status","Sequencing_Phase","Sequence_Source","Validation_Method","Sequencer"]

    def __init__(self, groupBy="sampleID"):
        self.groupDict = {}
        self.grouping = groupBy
        if self.grouping not in MAFTable.groupings:
            raise Exception("Invalid grouping. Please choose from: " + ", ".join(MAFTable.groupings))
        for var in MAFReader(filename):
            self.__addVar(var)

    def __addVar(self, var):
        vargroup = vars(var).get(self.grouping)
        if not self.groupDict.has_key(vargroup):
            self.groupDict[vargroup] = [var]
        else:
            self.groupDict[vargroup].append(var)

    def getGroupType(self):
        return self.grouping

    def getGroupVals(self):
        return self.groupDict.keys()

    def getVars(self, val):
        return self.groupDict.get(val)

if __name__ == "__main__":
    pass
