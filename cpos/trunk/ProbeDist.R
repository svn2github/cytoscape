plot.hist.modular <- function(dat, main="", ylim=c(0,20),
                              xlim=c(log10(100),log10(1e6)),
                              bkgd.max=NA, all.results=NA, show.ks.pvalue=F)
  {
    if(nrow(dat) == 0) { return (1) }

    ## mtd = min telomere distance
    ## cd = centromere distance
    agg <- cbind(dat$mtd, dat$cd)

    D <- log10(apply(agg, 1, min))
    h <- hist(D,
##              xlim=xlim,
##              ylim=ylim,
              breaks=25,
              xlab="Distance from closest Cen/Tel (bp)",
              ylab="",
              main=main,
              col="grey",
              las=1,
              xaxt="n",
              yaxt="n")
    x.ticks <- axTicks(1)
    axis(side=1, at=x.ticks, labels=sprintf("%.0e", 10^x.ticks), col="grey", col.axis="grey")
    axis(side=1, at=range(h$breaks), labels=sprintf("%.0e", 10^range(h$breaks)), lwd=2)
    axis(2, at=c(axTicks(2)), las=2, col="grey", col.axis="grey")
    axis(2, at=c(range(h$counts)), las=2, lwd=2)
    
    ## this prints a tick at every minor interval
    rug(x=as.vector(sapply(x.ticks, function(x) { x + log10(c(1:10))})),
        ticksize=-0.02)
    
    if(!is.na(bkgd.max))
      {
        abline(v=bkgd.max, col="red", lty=2)
        if(show.ks.pvalue)
        {
          text(x=log10(200), y=mean(ylim), adj=0,
               labels=paste("K-S pvalue =",
               ks.wrapper(all.results$D, D)))
        }
      }
    
    return(list(h=h, D=D))
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

plot.pvalues <- function(outfile, D.list, all.results)
  {
    cats <- c()
    pvals <- c()
    
    for(i in 1:length(D.list))
      {
        cats <- c(cats, D.list[[i]]$cat)
        pvals <- c(pvals, ks.wrapper(all.results$D, D.list[[i]]$D, format=F))
      }
    
    res <- data.frame(cats, pvals)
    o.p <- order(pvals)
    
##    res[o.p,]

    write.table(file=outfile,
                res[o.p,], quote=F, row.names=F)

    barplot(sort(log10(pvals)), las=2, ylab="Log10 P-value")
    abline(h=log10(1e-3), lty=2, col="red")
  }
                     

plot.hist1 <- function(dat, main)
{
  xlab="Chromosome position (percent)"
  hist(dat$chrPercent, breaks=25, xlab=xlab,
       main=main, col="grey")

}

ks.wrapper <- function(v1, v2, format=T)
  {
    if((length(v1) > 0) & (length(v2) > 0))
      {
        pval <- ks.test(v1, v2, alternative="less")$p.value 

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

plot.chromosome.dist <- function(dat, main)
  {
    barplot(tapply(dat$chrNum, as.factor(dat$chrNum), length),
            main=main)
  }

