#make your igraph
g1 <- graph( c( 0,1, 1,2, 2,2, 2,3 ) )

#The Cytobridge class
setClass(Class="cytobridge", 
	   representation=representation(a="ANY"),
	   prototype=prototype(a=graph(c(0,1))),
	   validity=function(object) {
	       if(class(object@a)!="igraph") {
		     return(paste("Expected iGraph but got ", class(object@a)))
		 } else {
		     return(TRUE)
		 }
	   })

#Returns the graph of this CytoBridge
setGeneric(name="getGraph", def=function(x) standardGeneric("getGraph"))
        setMethod(f="getGraph", signature="cytobridge", definition=function(x) {
                return(x@a)
        }) 

#Send the graph data to the communication Layer
setGeneric(name="update", def=function(x) standardGeneric("update"))
        setMethod(f="update", signature="cytobridge",
            definition=function(x) {
                print(V(getGraph(x)))
        })


test <- new("cytobridge",a=g1)