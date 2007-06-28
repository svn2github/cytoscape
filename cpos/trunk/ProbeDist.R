
filterGreyTicks <- function(greyTicks, blackTicks)
  {
    ok <- rep(T, length=length(greyTicks))
    for(i in blackTicks)
      {
        x <- which.min(abs(greyTicks - i))
        ok[x] <- F
        ##print(paste(x, greyTicks[x], "is closest to", i))
      }
    return(ok)
  }

plot.yeast <- function(dat, mtd.thresh, ...)
  {
    par(mfrow=c(2,1))
    L <- plot.hist.modular(dat, mtd.thresh=mtd.thresh, ...)
    plot.chr(dat, mtd.thresh=mtd.thresh)
    return(L)
  }

read.chr.data <- function(file)
  {
    dat <- read.table(file, header=T, fill=T, comment.char="$")
  }

viz.chr <- function(dat, mtd.thresh, allchr=1:16,
                    chr.data)
  {
    if(nrow(dat) == 0) { return (NA) }
    breaks <- seq(min(allchr)-0.5, max(allchr)+0.5, 1)
    plot(x=allchr, y=rep(1, length(allchr)),
         ylim=c(0, 1), xlim=c(min(allchr)-0.5, max(allchr)+0.5),
         type="n", bty="n",
         xaxt="n", yaxt="n", ylab="", xlab="Chromosome")
    axis(1, at=allchr[c(1, which(allchr %% 4 == 0))], tick=F, line=-1)
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
               y=y.subt/M, pch=18, cex=1, col="red")
        points(x=rep(i + 0.1, times=length(y.other)),
               y=y.other/M, pch="-", cex=1.5, col="black")

      }
  }

get.counts <- function(data, size, breaks)
  {

    if(length(data) == 0) {
      counts <- rep(0, times=size)
    }
    else {
      h <- hist(data, breaks=breaks, plot=F)
      counts <- h$counts
    }
    return(counts)
  }

plot.chr <- function(dat, mtd.thresh, allchr=1:16, main="mtd thresh")
{
  if(nrow(dat) == 0) { return (NA) }
  subt <- dat$mtd <= mtd.thresh

  breaks <- seq(min(allchr)-0.5, max(allchr)+0.5, 1)

  subt.L <- subt & (dat$mtd == dat$mid)
  subt.R <- subt & !subt.L
  
  S <- length(allchr)
  
  subt.L.counts <- get.counts(data=dat$chr[subt.L],
                              size=S, breaks=breaks)

  subt.R.counts <- get.counts(data=dat$chr[subt.R],
                              size=S, breaks=breaks)
  
  barplot(rbind(subt.L.counts,
                subt.R.counts),
          beside=F,
          horiz=T,
          names.arg=allchr,
          xaxt="n",
          ylab="Chromosome",
          col=c("black", "grey"),
          border=c("black", "grey"),
          main=paste(main, ":", sprintf("%.2e", mtd.thresh)),
          sub="left-arm(black), right-arm(grey)",
          xlab="Number of subtelomeric genes",
          las=1, cex.axis=0.7)

  y.range <- range(c(subt.L.counts + subt.R.counts))
  T <- axTicks(1)
  is.digit <- sapply(T, function(x) { x == round(x) })
  filter <- filterGreyTicks(T[is.digit], y.range)
  axis(1, at=T[is.digit & filter], las=1, col="black", col.axis="black")
  axis(1, at=y.range, las=1, lwd=2)
}


plot.chr.allgenes <- function(dat, mtd.thresh, allchr=1:16, main="mtd thresh")
{
  if(nrow(dat) == 0) { return (NA) }
  subt <- dat$mtd <= mtd.thresh
  cen <- (dat$cd <= mtd.thresh) & !subt
  breaks <- seq(min(allchr)-0.5, max(allchr)+0.5, 1)

  h <- hist(dat$chr, breaks=breaks, plot=F)

  if(sum(subt) == 0) {
    subt.counts <- rep(0, times=length(h$counts))
  }
  else {
    h.subt <- hist(dat$chr[subt], breaks=breaks, plot=F)
    subt.counts <- h.subt$counts
  }
  
  if(sum(cen) == 0) {
    cen.counts <- rep(0, times=length(h$counts))
  } else {
    h.cen <- hist(dat$chr[cen], breaks=breaks, plot=F)
    cen.counts <- h.cen$counts
  }
  
  barplot(rbind(subt.counts,
                cen.counts,                
                h$counts - cen.counts - subt.counts),
          beside=F,
          names.arg=allchr,
          yaxt="n",
          xlab="Chromosome",
          col=c("black", "grey", "white"),
          main=paste(main, ":", sprintf("%.2e", mtd.thresh)),
          sub="black:telomere, gray:cen, white:other")
  filter <- filterGreyTicks(axTicks(2), range(h$counts))
  axis(2, at=axTicks(2)[filter], las=2, col="grey", col.axis="grey")
  axis(2, at=range(h$counts), las=2, lwd=2)
}

plot.hist.modular <- function(dat, main="", ylim=c(0,20),
                              xlim=c(log10(100),log10(1e6)),
                              bkgd.max=NA, all.results=NA,
                              show.ks.pvalue=F,
                              mtd.thresh=NA)
  {
    if(nrow(dat) == 0) { return (1) }

    ## mtd = min telomere distance
    ## cd = centromere distance
    agg <- cbind(dat$mtd, dat$cd)

    ##D <- log10(apply(agg, 1, min))
    D <- log10(dat$mtd)
    h <- hist(D,
              xlim=xlim,
##              ylim=ylim,
              breaks=20,
              xlab="Distance from closest tel. (bp)",
              ylab="",
              main=main,
              col="black",
              las=1,
              xaxt="n",
              yaxt="n")

    x.ticks <- axTicks(1)
    x.black <- range(h$breaks)
    x.filter <- filterGreyTicks(x.ticks, x.black)
    x.grey <- x.ticks[x.filter]
    axis(side=1, at=x.grey, labels=sprintf("%.0e", 10^x.grey),
         col="grey", col.axis="grey")
    axis(side=1, at=x.black, labels=sprintf("%.0e", 10^x.black), lwd=2)

    ## this prints a tick at every minor interval
    rug(x=as.vector(sapply(x.ticks, function(x) { x + log10(c(1:10))})),
        ticksize=-0.02)
    
    y.ticks <- axTicks(2)
    y.black <- range(h$counts)
    y.filter <- filterGreyTicks(y.ticks, y.black)
    axis(2, at=y.ticks[y.filter], las=2, col="grey", col.axis="grey")
    axis(2, at=y.black, las=2, lwd=2)
    
    if(!is.na(bkgd.max))
      {
        abline(v=bkgd.max, col="blue", lty=2)
        if(show.ks.pvalue)
        {
          text(x=log10(200), y=mean(ylim), adj=0,
               labels=paste("K-S pvalue =",
               ks.wrapper(all.results$D, D)))
        }
      }

    if(!is.na(mtd.thresh))
      {
        abline(v=log10(mtd.thresh), col="red", lty=2)
      }
    return(list(h=h, D=D))
  }


plot.pvalues <- function(outfile, D.list, all.results, mtd.thresh=NA, highlight=c("SWI4", "GAT3", "MSN4"))
  {
    cats <- c()
    pvals <- c()
    fractions <- c()
    
    for(i in 1:length(D.list))
      {
        i.D <- D.list[[i]]$D
        cats <- c(cats, D.list[[i]]$cat)
        pvals <- c(pvals, ks.wrapper(all.results$D, i.D, format=F))
        fractions <- c(fractions, sum(i.D <= log10(mtd.thresh))/length(i.D))
      }
    
    res <- data.frame(cats, pvals, fractions)
    o.p <- order(pvals)
    
    write.table(file=outfile,
                res[o.p,], quote=F, row.names=F)

    cols = rep("black", times=length(pvals))
    hh <- match(highlight, cats)
    if(length(hh) > 0)
      {
        print (paste("plot.pvalues: Highlighting [",
                     paste(cats[hh], collapse=", "), "]"))
        cols[hh] <- "red"
      }
    par(mar=c(5.1, 4.1, 3, 4.1))
    #barplot(log10(pvals[o.p]), las=2, ylab="KS P-value (log10)", col=cols[o.p],
    #        ylim=c(min(log10(pvals), na.rm=T), max(fractions, na.rm=T)))
            
    #abline(h=log10(1e-3), lty=2, col="red")

    #barplot(fractions[o.p], add=T, border="grey", col="grey", yaxt="n")
    #axis(4, at=c(0,round(max(fractions), 2)), las=2)

    sig <- 0.001
    sig.ind <- pvals <= sig
    cols <- rep("grey", times=length(pvals))
    cols[sig.ind] <- "black"
    plot(x=100*fractions, y=(-1 * log10(pvals)),
         pch=16, cex=0.7, col=cols,
         ylab="significance (-log10 P-value)",
         xlab="% subtelomeric")
    
    abline(h=-log10(sig), lty=2, col="grey")
    o.p <- order(pvals)
    show <- pvals <= sig
    text(x=100*fractions[show], y=(-1*log10(pvals[show])), labels=cats[show], cex=0.8, pos=4)
  }

GLOBAL.ks.test.alternative="less"

ks.wrapper <- function(v1, v2, format=T)
  {
    if((length(v1) > 0) & (length(v2) > 0))
      {
        pval <- ks.test(v1, v2, alternative=GLOBAL.ks.test.alternative)$p.value 

        if(format)
          {
            if(pval < 1e-6)
              {
                return(sprintf("%.2e", pval))
              }
            return(sprintf("%.6f", pval))
          }
        return(pval)
      }
    else
      {
        return(1)
      }
  }

plot.hist2 <- function(dat, main, ylim=c(0,20),
                       xlim=c(log10(100),log10(1e6)),
                       bkgd.max=NA, all.results=NA)
  {
    if(nrow(dat) == 0) { return (1) }
    
    agg <- cbind(dat$probeMid,
                 dat$chrSize - dat$probeMid,
                 abs(dat$cenMidpoint - dat$probeMid))
    D <- log10(apply(agg, 1, min))
    h <- hist(D,
              xlim=xlim,
              ylim=ylim,
              breaks=25,
              xlab="Distance from closest Cen/Tel (bp)",
              main=main,
              col="grey",
              las=1,
              xaxt="n")
    x.ticks <- axTicks(1)
    axis(side=1, at=x.ticks, labels=10^x.ticks)

    if(!is.na(bkgd.max))
      {
        abline(v=bkgd.max, col="red", lty=2)
        text(x=log10(200), y=mean(ylim), adj=0,
             labels=paste("K-S pvalue =",
               ks.wrapper(all.results$D, D)))
      }
    
    return(list(h=h, D=D))
  }

plot.hist1 <- function(dat, main)
{
  xlab="Chromosome position (percent)"
  hist(dat$chrPercent, breaks=25, xlab=xlab,
       main=main, col="grey")

}

plot.chromosome.dist <- function(dat, main)
  {
    barplot(tapply(dat$chrNum, as.factor(dat$chrNum), length),
            main=main)
  }

