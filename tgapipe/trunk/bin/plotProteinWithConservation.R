#!/bin/R
#Tychele N. Turner
#Laboratory of Aravinda Chakravarti, Ph.D.
#Johns Hopkins University School of Medicine
#Protein Plotting Script with Conservation
#Programming Language: R
#Updated 2/26/2013

#Description: This script takes mutation information at the protein level and plots out the mutation above the schematic of the protein. It also plots the domains. This version can also add a track for conservation. If you want to use the conservation track the seqinr package will need to be installed in your R instance: install.packages("seqinr")

#NOTE: All files should be referring to the same isoform of the protein. This is imperative for drawing the plot correctly.

#Package requirements: To use the conservation track the seqinr package will need to be installed in your R instance: install.packages("seqinr")

#Required files:
##Mutation file: tab-delimited file containing 5 columns (ProteinId, GeneName, ProteinPositionOfMutation, ReferenceAminoAcid, AlternateAminoAcid) NO HEADER FOR NEEDED FOR THIS FILE
##Protein architecture file: tab-delimited file containing 3 columns (architecture_name, start_site, end_site). This file NEEDS the header and it is the same as what was previously written. This information can be downloaded from the HPRD (http://hprd.org/). Although the most recent files are quite old so looking in the web browser you can get much more up to date information.
##Post-translational modification file: This is a tab-delimited file with only one column and that is the site. This file NEEDS a header and is as previously written.
##Alignment file: This is an aligned multiple sequence alignment fasta file such as that produced by MUSCLE (http://www.ebi.ac.uk/Tools/msa/muscle/). 


#Usage:
#R --slave --vanilla < plotProteinWithConservation.R mutationFile proteinArchitectureFile postTranslationalModificationFile alignmentFile referenceSequencePositionInFile nameOfYourQuery

#Example:
# R --slave --vanilla < scripts/plotProteinWithConservation.R testFiles/psen1_mutation_file.txt testFiles/psen1_architecture_file.txt testFiles/psen1_post_translation_file.txt testFiles/muscle-I20130227-165316-0600-58424624-pg.fasta 4 Test

#Arguments:
argv <- function(x){
    args <- commandArgs()
    return(args[x])
}

mutationFile <- argv(4) #This is the mutation file
proteinArchitectureFile <- argv(5) #This is the protein architecture file
postTranslationalModificationFile <- argv(6) #This is the post-translation modification file
alignmentFile <- argv(7) #Multiple sequence alignment file
referenceSequencePositionInFile <- argv(8) #This is just the sequence number of your reference in the aligned fasta; for example if human is your reference and its the 5th sequence in the aligned file just say 5
nameOfYourQuery <- argv(9) #Here you can put whatever name you want to show up in the plot

####################ANALYSIS####################
library("seqinr")
#Read in the files
var <- read.table(mutationFile, sep="\t")
pa <- read.table(proteinArchitectureFile, sep="\t", header=TRUE)
pt <- read.table(postTranslationalModificationFile, sep="\t", header=TRUE)

#conservation analysis
a <- read.fasta(alignmentFile)
seq <- list()
for(i in 1:length(a)){
	seq[[i]] <- a[[i]][1:length(a[[i]])]
}
numberOfSeq <- length(seq)

mat <- matrix(0, nrow=length(a), ncol=length(a[[1]]))
for(i in 1:length(seq)){
	mat[i,] <- seq[[i]]
}

df <- as.data.frame(mat)
tdf <- t(df)
referenceSeq <- tdf[which(tdf[,as.numeric(referenceSequencePositionInFile)] != "-"),]
referenceSeq <- as.data.frame(referenceSeq)
write.table(referenceSeq, file="alignment_table", sep="\t", quote=F, row.names=F, col.names=F)

counter <- rep(0, nrow(referenceSeq))
a <- read.table("alignment_table", sep="\t")
a <- data.frame(lapply(a, as.character), stringsAsFactors=FALSE)
for(i in 1:nrow(a)){
	a[i,"consensus"] <- paste(as.character(a[i,]), collapse="")
	}

countBases <- function(string){
 table(strsplit(string, "")[[1]])
}
c <- as.character(a[,"consensus"])
tab <- list()
for(i in 1:length(c)){
	tab[[i]] <- countBases(c[i])
}

score <- rep(0, nrow(a))
for(i in 1:length(tab)){
	for(j in 1:length(tab[[i]])){
		if((names(tab[[i]][j])) == a[,5][i])
		score[i] <- tab[[i]][j]
	}
}
scorePlot <- -(((score/numberOfSeq)))
system("rm alignment_table")

############PLOTTING#############
#x is the input data, y is rpt, z is rpa from HPRD
pdf(paste(as.character(var[1,2]), "_protein_plot.pdf", sep=""), height=7.5, width=10)
par(oma=c(8, 1.2, 8, 1.2))

plot((1:length(scorePlot)), rep(-2, length(scorePlot)), type="l", lwd=5, main=paste("Amino Acid Changes in", " ", as.character(var[1,2]), " ", "(", as.character(var[1,1]), ")", sep=""), xlab="Amino Acid Position", ylab="", xlim=c(-145, length(scorePlot)), ylim=c(0,-4), cex.lab=0.9, cex.main=1, yaxt="n")

#Plot mutations
points(var[,3], rep(-2.5, length(var[,3])), pch=19, col="red", cex=0.7)

#Label mutations
for(i in 1:nrow(var)){
	text(var[i,3], rep(-2.7, length(var[i,3])), paste(as.character(var[i,4]), as.character(var[i,3]), as.character(var[i,5]), sep=""), col="red", cex=0.7, srt=90, adj = 0)
}

#labels
text(-100,-2.5,nameOfYourQuery, col="red", cex=0.9)
for(i in 1:length(pt$site)){
	segments(as.numeric(pt$site[i]), -2, as.numeric(pt$site[i]), -2.25, lwd=2, col="black")
	points(as.numeric(pt$site[i]), -2.25, pch=19, col=6, cex=0.7)
}

for(i in 1:length(pa$start_site)){
	lines(c(as.numeric(pa$start_site[i]):as.numeric(pa$end_site[i])), rep(-2,(as.numeric(pa$end_site[i])- as.numeric(pa$start_site[i]))+1),lwd=10, col=c("purple"))
}
for(i in 1:length(pa$architecture_name)){
	text(median(c(as.numeric(pa$start_site[i]), as.numeric(pa$end_site[i]))), -1.80, pa$architecture_name[i], cex=0.6)
}
lines(scorePlot, col="purple3")
lines((-150:-40), rep(-1, 111), col="purple3")
lines((-150:-40), rep(0, 111), col="purple3")
text(-95, -0.5, "Conservation", col="purple3", cex=0.9)
text(-20,-1, "1", col="purple3", cex=0.9)
text(-20,0, "0", col="purple3", cex=0.9)
legend("topright", c("Protein Domain", "Post-Translational Modification"), fill=c("purple", 6), cex=0.7)
dev.off()


