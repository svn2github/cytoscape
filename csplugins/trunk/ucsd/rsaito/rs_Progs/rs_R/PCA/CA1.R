CA.calc <- function(F){
   f.. <- sum(F)
   S <- diag(apply(F, 1, sum))
   C <- diag(apply(F, 2, sum))
   Srsr <- diag(apply(F, 1, sum)^(-1/2))
   Crsr <- diag(apply(F, 2, sum)^(-1/2))
   H <- Srsr %*% F %*% Crsr
   SVD.res <- svd(H)
   U <- SVD.res$u
   D <- diag(SVD.res$d)
   V <- SVD.res$v
   if(U[1,1] < 0){ U <- -U; V <- -V }
   X <- f..^(1/2) * Srsr %*% U
   Y <- f..^(1/2) * Crsr %*% V
   return(list(F=F, H=H, X=X, Y=Y, S=S, C=C, U=U, D=D, V=V))
}
