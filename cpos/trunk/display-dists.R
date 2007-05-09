source("ProbeDist.R")
library(CarbonEL)
quartz()



m2 <- rnorm(100, mean=1e5, sd=1e6)
m1 <- rnorm(100, mean=1e7, sd=1e6)

M <- c(m1, m2)

doit <-function(M)
  {
    par(mfrow=c(2,2))
    plot(M, log="y")
    h <- hist(M, breaks=25)
    cdf <- cumsum(h$density)/sum(h$density)
    plot(cdf, type="b")
    
    dx.dy <- c()
    for( i in 1:(length(cdf) - 1))
      {
        dx.dy <- c(dx.dy, cdf[i+1] - cdf[i])
      }
    plot(dx.dy, type="b")

    x <- 1:length(dx.dy)
    df <- data.frame(x=x, y=dx.dy)
    dd.loess <- loess(y~x, df)
    y.predict <- predict(dd.loess, data.frame(x=x))
    lines(x, y.predict, col="red")
  }

read.harb <- function(tf)
  {
    dat <- read.table(file=paste("output/harbison_YPD/", tf, ".loc", sep=""),
                      header=T)
    return(dat)
  }

show.harb <- function(tf, mtd.thresh)
  {
    dat <- read.harb(tf)
    par(mfrow=c(2,1))
    plot.hist.modular(dat, main=tf)
    abline(v=log10(mtd.thresh), col="blue", lty=2)
    plot.chr.hist(dat, main=tf, mtd.thresh=mtd.thresh)
    ##    doit(sort(log10(apply(cbind(dat$mtd, dat$cd), 1, min))))
  }


plot.chr.hist <- function(dat, mtd.thresh, allchr=1:16, main="")
{
  subt <- dat$mtd <= mtd.thresh
  cen <- (dat$cd <= mtd.thresh) & !subt
  breaks <- seq(min(allchr)-0.5, max(allchr)+0.5, 1)

  h.subt <- hist(dat$chr[subt], breaks=breaks, plot=F)
  h.cen <- hist(dat$chr[cen], breaks=breaks, plot=F)
  h <- hist(dat$chr, breaks=breaks, plot=F)

  barplot(rbind(h.subt$counts, h.cen$counts,
                h$counts - h.cen$counts - h.subt$counts), beside=F,
          names.arg=allchr,
#          xaxt="n",
          yaxt="n",
          xlab="Chromosome",
          col=c("black", "grey", "white"),
          main=paste(main, ":", sprintf("%.2e", mtd.thresh)),
          sub="black:telomere, gray:cen, white:other")
  axis(2, at=c(axTicks(2)), las=2, col="grey", col.axis="grey")
  axis(2, at=c(range(h$counts)), las=2, lwd=2)
  
#  axis(1, at=1:z[2], labels=1:z[2])
}
show.harb("GAT3", mtd.thresh=3e4)

read.chr.data <- function(file)
  {
    dat <- read.table(file, header=T, fill=T, comment.char="$")
  }


sc.chr <- read.chr.data("/Users/cmak/data/sc-chromosome-data.txt")
show.harb2 <- function(tf, mtd.thresh)
  {
    dat <- read.harb(tf)
    par(mfrow=c(2,2))
    plot.hist.modular(dat, main=tf)
    abline(v=log10(mtd.thresh), col="blue", lty=2)
    plot.chr.hist(dat, main=tf, mtd.thresh=mtd.thresh)
    viz.chr(dat, main=tf, mtd.thresh=mtd.thresh, chr.data=sc.chr)
  }

viz.chr <- function(dat, main, mtd.thresh, allchr=1:16,
                    chr.data)
  {
    breaks <- seq(min(allchr)-0.5, max(allchr)+0.5, 1)
    plot(x=allchr, y=rep(1, length(allchr)),
         ylim=c(0, 1), xlim=c(min(allchr)-0.5, max(allchr)+0.5),
         type="n", bty="n",
         xaxt="n", yaxt="n", ylab="", xlab="Chromosome")
    axis(1, at=allchr, tick=F, line=-1)
    ##    chr.data <- sc.chr
    M <- max(chr.data$Length)
    lens <- chr.data$Length/M
    subt <- dat$mtd <= mtd.thresh
    
    segments(x0=allchr, y0=0, x1=allchr, y1=lens, lwd=0.5)
    for(i in allchr)
      {
        genes.on.i <- dat$chr == i
        points(x=i,
               y=mean(chr.data$CenStart[i], chr.data$CenEnd[i])/M,
               pch=1)
        if(length(genes.on.i) == 0) { next }

        y.subt <- dat$mid[genes.on.i & subt]
        y.other <- dat$mid[genes.on.i & !subt]
        points(x=rep(i + 0.1, times=length(y.subt)),
               y=y.subt/M, pch=23, cex=1.5, col="red")
        points(x=rep(i + 0.1, times=length(y.other)),
               y=y.other/M, pch="-", cex=2, col="blue")

      }
  }
show.harb2("GAT3", mtd.thresh=3e4)


doit(c(m1, m2))
doit(m1)
doit(rnorm(1000, mean=5, sd=2.2))

dat <- read.harb("GAT3"); mtd.thresh <- 3e4; allchr<-1:16
swi4[(swi4$cd <=3e4),]

show.harb("YAP5", mtd.thresh=3e4)
show.harb("SWI4", mtd.thresh=3e4)
show.harb("DAT1")
show.harb("NRG1")
