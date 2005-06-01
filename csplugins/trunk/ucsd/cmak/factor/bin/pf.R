

profile <- function(cntdata, vardata, maxdata, nV, ps=FALSE, out="profile.ps")
{
	cwd <- getwd()
	setwd("/home/cmak/cellar/code/factor/testOut")
	v = read.table(vardata, header=FALSE, sep=",")
	m = read.table(maxdata, header=FALSE, sep=",")
	cn = read.table(cntdata, header=FALSE, sep=",")

	oldpar <- par(no.readonly=TRUE)
	if(ps)
	{
		postscript(out, horizontal=FALSE)
	}
	par(mfcol = c(3, 1),  bty="l")
	plot(v, ylim=c(0, nV), type="s", main="Probability", sub=vardata, xlab="algorithm iteration", ylab="num constant")
	abline(h=nV, col="red")

	plot(m, ylim=c(0, nV), type="s", main="Max", sub=maxdata, xlab="algorithm iteration", ylab="num constant")
	abline(h=nV, col="red")

	plot(cn, ylim=c(0, 150), type="s", main="Count", sub=cntdata, xlab="# times changed", ylab="num vars")

	if(ps)
	{
		dev.off()
	}
	par(oldpar)
	setwd(cwd)
}

#profile("fgtest_fg2_var.pf", "fgtest_fg2_max.pf", 48)