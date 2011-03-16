library("Biobase")
library("genefilter")
library("ALL")
data("ALL")
bcell = grep("^B", as.character(ALL$BT))
moltyp = which(as.character(ALL$mol.biol) 
               %in% c("NEG", "BCR/ABL"))
ALL_bcrneg = ALL[, intersect(bcell, moltyp)]
ALL_bcrneg$mol.biol = factor(ALL_bcrneg$mol.biol)
sds = rowSds(exprs(ALL_bcrneg))
sh = shorth(sds)
Allsfilt = ALL_bcrneg[ sds >= sh ]
tt = rowttests(ALLsfilt, "mol.biol")
