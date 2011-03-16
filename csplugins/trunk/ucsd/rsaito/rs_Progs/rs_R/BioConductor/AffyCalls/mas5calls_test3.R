# Installation of BioConductor
# source("http://bioconductor.org/biocLite.R")
# biocLite()
# 
# biocLite("hgu133a2.db") # Annotation database

library("affy")
library("annotate")
library("hgu133a2.db")

data = ReadAffy()
calls = mas5calls(data)
probeids = rownames(exprs(calls))
toGeneIDs = getEG(probeids, "hgu133a2.db")
toSymbols = getSYMBOL(probeids, "hgu133a2.db")
toAccession = sapply(probeids, function(p) hgu133a2ACCNUM[[p]]) # Takes a long time for unknown reason
CallTable = data.frame(GeneID = toGeneIDs, Symbol = toSymbols, Accession = toAccession, exprs(calls))
write.table(CallTable, file = "CallTable.txt")

# Example of obtaining R information
# ls()
# ls("package:hgu133a2.db")
# attributes(calls)
# mode(calls)
