
library("affy")
library("annotate")
library("hgu133a2.db")

data = ReadAffy()
calls = mas5calls(data)
probeids = rownames(exprs(calls))
toGeneIDs = getEG(probeids, "hgu133a2.db")
toSymbols = getSYMBOL(probeids, "hgu133a2.db")
PMATable = data.frame(toGeneIDs, toSymbols, exprs(calls))
write.table(PMATable, file = "PMATable.txt")

# ls()
# attributes(calls)
# mode(calls)
