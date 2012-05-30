#make your igraph
g1 <- graph.ring(100000)

#The Cytobridge class
setClass(Class="cytobridge", 
	   representation=representation(a="ANY", s="ANY"),
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
 		    socket <- make.socket("localhost", "4444")
    		    on.exit(close.socket(socket))
		    write.socket(socket, paste(as.character(x@s),toString(x@a),"endRSend"))
		    close.socket(socket)
        })

setGeneric(name="connect", def=function(x) standardGeneric("connect"))
        setMethod(f="connect", signature="cytobridge",
            definition=function(x) {
		    x@s <- 1234 #some unique id
		    return(x)
        })

setGeneric(name="close", def=function(x) standardGeneric("close"))
        setMethod(f="close", signature="cytobridge",
            definition=function(x) {
		    socket <- make.socket("localhost", "4444")
    		    on.exit(close.socket(socket))
		    write.socket(socket, "die")
		    close.socket(socket)
                x@s <- 0 #resets id
        })

test <- new("cytobridge",a=g1)
test <- connect(test)
update(test)