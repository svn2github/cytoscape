IntegRect <- function(lower, upper, step, func, ...){
	
	areas <- NULL
	delta.reg <- NULL
	lowers <- seq(lower, upper - step, step)
	for (tlower in lowers){
	   deltax <- integrate(func, 
	                      lower = tlower, upper = tlower + step,
	                      ...)$value
		areas <- c(areas, deltax / step)
		delta.reg <- c(delta.reg, 
		                     paste("[", tlower, ",", tlower+step, "]", sep=""))
	}

   names(areas) <- delta.reg
	return(areas)
	
}