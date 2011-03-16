library(mva)

table.with.header.file.to.matrix <- function(filename){

   tb <- read.table(filename)

   m <- length(tb[[1]])
   n <- length(tb)

   row.label <- dimnames(tb)[[1]]
   col.label <- dimnames(tb)[[2]]

   plain.vec <- c()

   for(i in 1:n){

      plain.vec <- c(plain.vec, tb[[i]])

   }

   mat <- matrix(plain.vec, m, n)

   return(list(M=mat, r.label=row.label, c.label=col.label))

}

PCA2.calc <- function(F){

   PCA.res <- prcomp(F)   

   P <- PCA.res$rotation
   Z <- PCA.res$x
   Fc <- t(t(F) - apply(F, 2, mean))
   L <- t(P) %*% t(Fc) %*% Fc %*% P

   return(list(F=F, P=P, Z=Z, Fc=Fc, L=L))

}

PCA2.call <- function(infile, outfile="PCA2_tmp.txt"){

   mat.with.labels <- table.with.header.file.to.matrix(filename=infile)
   res <- PCA2.calc(mat.with.labels$M)
   dimnames(res$Z)[[1]] <- mat.with.labels$r.label
   rownames(res$F) <- mat.with.labels$r.label
   rownames(res$Fc) <- mat.with.labels$r.label
   l <- diag(res$L)

   write(t(res$Z), file=outfile, ncolumns=ncol(res$Z))
   print(l / sum(l))

   return(res)
}

CA2.calc <- function(F){

   f.. <- sum(F)
   S <- diag(apply(F, 1, sum))
   C <- diag(apply(F, 2, sum))
   Srsr <- diag(apply(F, 1, sum)^(-1/2)) # Reverse Square Root of S
   Crsr <- diag(apply(F, 2, sum)^(-1/2)) # Reverse Square Root of C
   H <- Srsr %*% F %*% Crsr

   SVD.res <- svd(H)

   U <- SVD.res$u
   d <- diag(SVD.res$d)
   V <- SVD.res$v

   if(U[1,1] < 0){ U <- -U; V <- -V }

   X <- f..^(1/2) * Srsr %*% U
   Y <- f..^(1/2) * Crsr %*% V

   return(list(F=F, H=H, X=X, Y=Y, S=S, C=C, U=U, d=d, V=V))

}

CA2.call <- function(infile, outfile1="CA2_tmpX.txt", 
                             outfile2="CA2_tmpY.txt"){

   mat.with.labels <- table.with.header.file.to.matrix(filename=infile)
   res <- CA2.calc(mat.with.labels$M)
   rownames(res$X) <- mat.with.labels$r.label
   rownames(res$Y) <- mat.with.labels$c.label

   write(t(res$X), file=outfile1, ncolumns=ncol(res$X))
   write(t(res$Y), file=outfile2, ncolumns=ncol(res$Y))

   return(res)
}


