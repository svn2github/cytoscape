IH3 <- function(x){ 
	
	if(x < 0){ f = 0}
	else if(0 <= x & x < 6){
		f = 1.0/432*x**2
		}
	else if(6 <= x & x < 12){
		f = 1.0/216 * (-1*x**2 + 18*x -54)
		}
	else if(12 <= x & x < 18){
		f = 1.0/432 * (x**2 - 36*x + 324)
		}		
	else if(x >= 18){
		f = 0
		}
	return(f)
}

IH3_vec <- function(x){

	return(sapply(x, IH3))	
	
}