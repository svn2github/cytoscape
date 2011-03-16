PCA.calc <- function(F){
   PCA.res <- prcomp(F)   
   P <- PCA.res$rotation
   Z <- PCA.res$x
   Fc <- t(t(F) - apply(F, 2, mean))
   L <- t(P) %*% t(Fc) %*% Fc %*% P
   return(list(F=F, P=P, Z=Z, Fc=Fc, L=L))
}
