source("http://bioconductor.org/biocLite.R")
biocLite()

biocLite("hgu133a2.db")
library("hgu133a2.db")
library("annotate")
ls()
ls("package:annotate")
getEG("1007_s_at", "hgu133a2.db")
getSYMBOL("1007_s_at", "hgu133a2.db")

library("affy")
data = ReadAffy()
calls = mas5calls(data)
write.exprs(calls, file="calls.txt")

