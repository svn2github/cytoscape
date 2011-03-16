pulse_test <- function(n, x){

   return(2/(pi*n)*sin(pi*n/2)*cos(n*x))

}

pulse_sum <- function(x){

   nv = 1:200
   return(sum(sapply(nv, pulse_test, x=x), 0.5))

}

pulse_t <- function(x){
   return(sapply(x, pulse_sum))
}