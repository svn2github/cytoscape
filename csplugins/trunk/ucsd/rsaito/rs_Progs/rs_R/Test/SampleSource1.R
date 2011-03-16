
# source("SampleSource1.R")

tb = read.table("SampleData1", header = TRUE, sep = "\t",
                row.names = 1, as.is = TRUE)
tb = tb * 1.0 # Conversion of integer to numeric

print(class(tb))
print(attributes(tb))
print(tb$Cond.3)
print(objects())

print(names(tb))

tb2 = tb[, grep("C", names(tb))]
print(tb2)
print(names(tb2))
tb3 = as.matrix(tb2)
tb3names = dimnames(tb3)[[2]]
tb3names[ grep("Cond", tb3names) ] = "Sample"
tb3names[ grep("Ctrl", tb3names) ] = "Negative"
print(tb3names)
print(factor(tb3names))
