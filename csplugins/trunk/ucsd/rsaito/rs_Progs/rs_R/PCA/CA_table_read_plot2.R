table.file.to.matrix <- function(filename){

   tb <- read.table(filename, header=FALSE)
   m <- length(tb[[1]])
   n <- length(tb)

   plain.vec <- c()

   for(i in 1:n){

      plain.vec <- c(plain.vec, tb[[i]])

   }

   mat <- matrix(plain.vec, m, n)
   return(mat)

}

table.with.header.file.to.matrix <- function(filename){

   data.f <- read.table(filename, header=TRUE, row.names=1)

   m <- length(data.f[[1]])
   n <- length(data.f)

   row.label <- dimnames(data.f)[[1]]
   col.label <- dimnames(data.f)[[2]]

   mat <- as.matrix(data.f)

   return(list(M=mat, r.label=row.label, c.label=col.label))

}

CA2.plot <- function(filename){

   F.list <- table.with.header.file.to.matrix(filename)
   CA.res <- CA1(F.list$M)

   dim1.coord <- c(CA.res$X[,2], CA.res$Y[,2])
   dim2.coord <- c(CA.res$X[,3], CA.res$Y[,3])
   label.v <- c(F.list$r.label, F.list$c.label)
   color.v <- conv.label.color.orig(label.v)

   plot(dim1.coord, dim2.coord, col=color.v)
   text(CA.res$Y[,2], CA.res$Y[,3], F.list$c.label,
	col=conv.label.color.orig(F.list$c.label))

   return(list(dim1.coord, dim2.coord, label.v, color.v))

}


CA1.plot <- function(filename){

   F.list <- table.with.header.file.to.matrix(filename)
   CA.res <- CA1(F.list$M)

   dim1.coord <- c(CA.res$X[,2], CA.res$Y[,2])
   dim2.coord <- c(CA.res$X[,3], CA.res$Y[,3])
   label.v <- c(F.list$r.label, F.list$c.label)
   color.v <- conv.label.color.orig(label.v)

   plot(dim1.coord, dim2.coord, type="n")
   text(dim1.coord, dim2.coord, label.v, col=color.v)

   return(list(dim1.coord, dim2.coord, label.v, color.v))

}

CA1 <- function(F){

   f.. <- sum(F)
   S <- diag(apply(F, 1, sum))
   C <- diag(apply(F, 2, sum))
   Sp12 <- diag(apply(F, 1, sum)^(-1/2))
   Cp12 <- diag(apply(F, 2, sum)^(-1/2))
   H <- Sp12 %*% F %*% Cp12

   SVD.res <- svd(H)
#   print(SVD.res)

   U <- SVD.res$u
   d <- diag(SVD.res$d)
   V <- SVD.res$v

   X <- f..^(1/2) * Sp12 %*% U
   Y <- f..^(1/2) * Cp12 %*% V

#   print(U)
#   print(d)
#   print(V)
#   print(U %*% d %*% t(V))
#   print(f..)
#   print(X)
#   print(Y)

   return(list(X=X,Y=Y,F=F,S=S,C=C,H=H))

}

CA1.orig <- function(infile, outfile="tmp2R.out"){

   F <- table.file.to.matrix(infile)
   f.. <- sum(F)
   S <- diag(apply(F, 1, sum))
   C <- diag(apply(F, 2, sum))
   Sp12 <- diag(apply(F, 1, sum)^(-1/2))
   Cp12 <- diag(apply(F, 2, sum)^(-1/2))
   H <- Sp12 %*% F %*% Cp12

   SVD.res <- svd(H)
#   print(SVD.res)

   U <- SVD.res$u
   d <- diag(SVD.res$d)
   V <- SVD.res$v

   X <- f..^(1/2) * Sp12 %*% U
   Y <- f..^(1/2) * Cp12 %*% V

#   print(U)
#   print(d)
#   print(V)
#   print(U %*% d %*% t(V))
#   print(f..)
#   print(X)
#   print(Y)

   return(list(X=X,Y=Y,F=F,S=S,C=C,H=H))

}

PCA1.orig <- function(infile, outfile="tmpR.out"){

   library(mva)
   mat <- table.file.to.matrix(infile)
   pres <- prcomp(mat)
   write(t(pres$x), file=outfile, ncolumns=ncol(pres$x))
   ccorrect <- apply(mat, 2, mean) %*% pres$rotation
   dimnames(ccorrect) <- NULL
   ccorrect <- ccorrect[1,]
   print("Variance")
   print(pres$sdev^2/sum(pres$sdev^2))
   print("Correction")
   print(ccorrect)
   return(pres)

}

conv.label.color.orig <- function(label.v){

   color.v <- c()
   for (label in label.v){
         color <-
               switch(label,
		A1="black",
		A2="black",
		L ="black",
		F ="black",
		D ="black",
		"Default"
               )
	 if(color == "Default"){
	        if(substring(label, 1, 1) == "R"){ 
		   color <- "red"
		}	
         	else { color <- "purple" }
	 }
         color.v <- c(color.v, color)
   }
   return(color.v)
}




