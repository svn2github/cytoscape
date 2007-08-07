setup <- function()
  {
    library(CarbonEL)
    quartz()
  }

remove.outlier <- function(x, thresh=5)
  {
    return(x[x < thresh])
  }

#dir <- "paralog"
#subt.file <- "9.GAL.HXT_subtelomeric.fasta.kaks"
#chrom.file <- "9.GAL.HXT_chromosomal.fasta.kaks"

#group.name <- strsplit(subt.file, "_")[[1]][1]

#plot.kaks(paste("paralog", c(subt.file, chrom.file), sep="/"),
#          names=c("Subt", "Chrom"),
#          group.name=group.name)

plot.kaks <- function(files, names, group.name=NA)
  {
    dat <- list()
    for(x in 1:length(files))
      {
        dat[[x]] <- read.table(file=files[x], header=F, as.is=T)
      }
#c.dat <- read.table(file=paste(dir, chrom.file, sep="/"), header=F, as.is=T)

    if(is.na(group.name))
      {
        group.name <- strsplit(files[1], "_")[[1]][1]
      }

    lr <- list()
    w <- list()
    for(x in 1:length(dat))
      {
        lr[[x]] <- dat[[x]]$V2
        w[[x]] <- remove.outlier(dat[[x]]$V4)
      }
#s.lr <- s.dat$V2
#c.lr <- c.dat$V2
#s.w <- remove.outlier(s.dat$V4)
#c.w <- remove.outlier(c.dat$V4)

    par(mfcol=c(1,2))
    boxplot(lr, names=names, main=paste(group.name, "Log-Likelihood ratio", sep="\n"),
            las=1, col="grey")
    boxplot(w, names=names, log="y", main=paste(group.name, "Ka/Ks", sep="\n"),
            las=1, col="grey")

    return(list(lr=lr, w=w))
  }

plot.summary <- function(data, names, col.names)
  {
    par(mfcol=c(1,1))
    rng <- range(data)
    plot(data, xlab=col.names[1], ylab=col.names[2], las=1, pch=16, bty="L", xlim=rng, ylim=rng)
    abline(0, 1, lty=2, col="grey")
    text(data, labels=names, pos=4, cex=0.7)
  }
