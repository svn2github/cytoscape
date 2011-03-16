dicep <- function(x){

   if(x < 1){ return(0) }
   else if(1 <= x && x <= 6){ return(1.0*floor(x)/6) }
   else { return(1) }

}

dicep.roulette <- function(x){

   if(x < 0){ return(0) }
   else if(0 <= x && x < 6){ return(1.0*x/6) }
   else { return(1) }
}

dicep.vec <- function(x){

   return(sapply(x, dicep))

} 

dicep.roulette.vec <- function(x){

   return(sapply(x, dicep.roulette))

}
