dice.sum.plot.example <- function(trials, faces = 6){

   x <- sapply(trials:(trials*faces), dice.sum.p, trials, faces)
   names(x) <- trials:(trials*faces)
   barplot(x)	
   
}

dice.sum.p <- function(x, trials, faces = 6){

	return(dice.sum(x,trials,faces) /
	       (faces**trials))
	
}

dice.sum <- function(x, trials, faces = 6){
# x is sum of values obtained from dice.
	
	return(sum(sapply(0:floor((x-trials)/faces),
	       dicep.sum_sub, x=x, trials=trials, faces=faces
	      )   )      )
	
}

dicep.sum_sub <- function(i, x, trials, faces = 6){
	
	return(choose(x-faces*i-1, trials-1) * 	       choose(trials, i) * (-1)**i)
	
}