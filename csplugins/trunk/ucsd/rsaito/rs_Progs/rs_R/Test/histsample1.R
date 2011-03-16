tb <- read.table("tmptmp", header = TRUE)
hist(tb[,3], br = 10000, xlim=c(0, 5000), main="D0", xlab="Intensity")
dev.copy(pdf, file="tmp_D0.pdf")
dev.off()
