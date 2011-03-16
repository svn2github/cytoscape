binom.plot <- function(trials, prob){

   x <- sapply(1:trials, dbinom, trials, prob)
   names(x) <- 1:trials
   barplot(x)	
}
