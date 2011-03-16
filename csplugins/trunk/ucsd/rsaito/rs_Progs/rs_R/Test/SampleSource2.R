
# source("SampleSource2.R")

tb = read.table("SampleData1", header = TRUE, sep = "\t",
                row.names = 1, as.is = TRUE)
tb = tb * 1.0 # Conversion of integer to numeric

col_names = names(tb)
col_Cond = grep("Cond", col_names)
col_Ctrl = grep("Ctrl", col_names)
tb_extr = tb[, c(col_Cond, col_Ctrl)]

mat = as.matrix(tb_extr)

col_names_mat = dimnames(mat)[[2]]
col_mat_Cond = grep("Cond", col_names_mat)
col_mat_Ctrl = grep("Ctrl", col_names_mat)
col_names_mat[ col_mat_Cond ] = "Sample"
col_names_mat[ col_mat_Ctrl ] = "Negative"

hist(mat[, col_mat_Ctrl], xlim=c(0, 200), ylim=c(0,20), breaks = 20, col = "grey", border = "white")
hist(mat[, col_mat_Cond], xlim=c(0, 200), ylim=c(0,20), breaks = 20, add = TRUE)
lines(x = c(0,200), y = c(0,0), col = "black")

library("genefilter")
tt = rowttests(mat, factor(col_names_mat))