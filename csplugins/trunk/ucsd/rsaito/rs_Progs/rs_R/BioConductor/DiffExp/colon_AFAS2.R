
# source("colon_AFAS1.R")

datafile = "Colon_AFAS6_II.txt"

tb = read.table(datafile, header = TRUE, sep = "\t",
                row.names = 5, as.is = TRUE)

col_names = names(tb)
col_Cond = grep("Rd.A.Colon_C", col_names)
col_Ctrl = grep("Rd.A.Colon_N", col_names)
tb_extrct_Cond = tb[, col_Cond] * 1.0
tb_extrct_Ctrl = tb[, col_Ctrl] * 1.0
tb_diff = tb_extrct_Cond - tb_extrct_Ctrl
mat = as.matrix(tb_diff)

library("genefilter")
tt = rowttests(mat)

library("multtest")
mt = mt.rawp2adjp(tt$p.value, proc="BH")
