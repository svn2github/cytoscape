IrwinHall <- function(x, trials, urange = 1){
# curve(IrwinHall(x, 10, urange=6), 0, 60) will draw distribution.
# IntegRect(lower=0,upper=12,step=1,IrwinHall, trials=2, urange=6) will draw approximate distribution.
	sapply(x/urange, IrwinHalls, trials=trials)/urange	
}


IrwinHalls <- function(x, trials){

   return(1.0/(2*factorial(trials-1))*sum(sapply(0:trials, IrwinHallsub, x=x, trials=trials)))

}

IrwinHallsub <- function(k, x, trials){

   # print(-1**k)
   # print(choose(trials,k))
   # print((x-k)**(trials-1)*sign(x-k))

   return((-1)**k * choose(trials, k) * (x - k)**(trials-1) * sign(x - k))

}

