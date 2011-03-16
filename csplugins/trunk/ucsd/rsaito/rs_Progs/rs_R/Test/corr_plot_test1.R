x <- runif(100, min=-10,max=10)
y <- -x + runif(100, min=-5,max=5)
cor(x, y, method="pearson")
plot(x,y)
