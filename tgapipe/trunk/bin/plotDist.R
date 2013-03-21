argv <- function(x) {
         args <-  commandArgs()
         return(args[x])
}

library(ggplot2)

valuefile = argv(4)
labelfile = argv(5)
logval = argv(6)
outfile = argv(7)

vals <- scan(valuefile, what=integer())
labels <- scan(labelfile, what=character())
x11(width=15)
qplot(labels, log(vals,as.numeric(logval)))
dev.copy2eps(file=outfile)
dev.off()
