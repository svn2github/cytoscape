
# source("colon_AFAS1.R")

datafile = "Colon_AFAS6_II.txt"

tb = read.table(datafile, header = TRUE, sep = "\t",
                row.names = 5, as.is = TRUE)

col_names = names(tb)
col_Cond = grep("Rd.A.Colon_C", col_names)
col_Ctrl = grep("Rd.A.Colon_N", col_names)
tb_extrct = tb[, c(col_Cond, col_Ctrl)]
tb_extrct = tb_extrct * 1.0 # Conversion of integer to numeric
mat = as.matrix(tb_extrct)

col_names_mat = dimnames(mat)[[2]]
col_mat_Cond = grep("Rd.A.Colon_C", col_names_mat)
col_mat_Ctrl = grep("Rd.A.Colon_N", col_names_mat)
col_name_classes = rep(NULL, length(col_names_mat))
col_name_classes[ col_mat_Cond ] = "Cancer"
col_name_classes[ col_mat_Ctrl ] = "Normal"

library("genefilter")
tt = rowttests(mat, factor(col_name_classes, levels=c("Cancer", "Normal")))

library("multtest")
mt = mt.rawp2adjp(tt$p.value, proc="BH")
