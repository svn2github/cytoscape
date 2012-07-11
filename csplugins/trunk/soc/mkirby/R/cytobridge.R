#------------------------------
#CytoBridge -------------------
#Author: Michael Kirby --------
#Depends On: iGraph, XMLRPC ---
#------------------------------

#Pushes the given iGraph to Cytoscape and returns the iGraph with extra CytoBridge
#Consistency attributes.
#name: The name to link this graph to with CytoBridge.
#g: The iGraph to push.
#tables: TRUE if push tables also, FALSE otherwise.
pushNetwork <- function (name,g, tables=TRUE) {
	if (!('cytobid' %in% list.graph.attributes(g))) {
		cytob.suid <- 0
		V(g)$cytobid <- seq(cytob.suid,cytob.suid+length(V(g))-1)
		cytob.suid <- cytob.suid + length(V(g))-1
		E(g)$cytobid <- seq(cytob.suid,cytob.suid+length(E(g))-1)
		cytob.suid <- cytob.suid + length(E(g))-1
		g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
	} else {
		cytob.suid <- get.graph.attribute(g, "cytobid")
		for(n in 1:length(V(g))) {
			if (is.na(get.vertex.attribute(g,'cytobid',n))) {
				cytob.suid <- cytob.suid +  1
				g <- set.vertex.attribute(g,'cytobid',n,as.integer(cytob.suid))
			}
		}

		for(e in 1:length(E(g))) {
			if (is.na(get.edge.attribute(g,'cytobid',e))) {
				cytob.suid <- cytob.suid +  1
				g <- set.edge.attribute(g,'cytobid',e,as.integer(cytob.suid))
			}
		}
		g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
	}
	xml.rpc('localhost:9000', 'Cytoscape.pushNetwork', name, get.vertex.attribute(g, 'cytobid'), get.edge.attribute(g, 'cytobid'),  get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,1]), get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,2]))
	if (tables) { pushTables(name, g) }
	g
}

#Pushes the given iGraphs attributes to Cytoscape.
#name: The name this graph is linked to with CytoBridge.
#g: The iGraph with the attributes to push.
#net: TRUE to push network table.
#node: TRUE to push node table.
#edge: TRUE to push edge table.
pushTables <- function (name,g, net=FALSE, node=FALSE, edge=FALSE) {
	if (!net && !node && !edge) {
		net = TRUE
		node = TRUE
		edge = TRUE
	}
	if (net) {
		gdata <- c()
		for(i in 1:length(list.graph.attributes(g))) {
			gdata <- append(gdata, get.graph.attribute(g,list.graph.attributes(g)[i]))
		}
		xml.rpc('localhost:9000', 'Cytoscape.pushNetTable', name, as.vector(list.graph.attributes(g)), as.character(gdata))
	}
	if (node) {
		vdata <- c()
		for(v in 1:length(list.vertex.attributes(g))) {
			vdata <- append(vdata, get.vertex.attribute(g,list.vertex.attributes(g)[v]))
		}
		xml.rpc('localhost:9000', 'Cytoscape.pushNodeTable', name, as.vector(list.vertex.attributes(g)), get.vertex.attribute(g, 'cytobid'), as.character(vdata))
	}
	if (edge) {
		edata <- c()
		for(e in 1:length(list.edge.attributes(g))) {
			edata <- append(edata, get.edge.attribute(g,list.edge.attributes(g)[e]))
		}
		xml.rpc('localhost:9000', 'Cytoscape.pushEdgeTable', name, as.vector(list.edge.attributes(g)), get.edge.attribute(g, 'cytobid'), as.character(edata))
	}
}

#Pushes the specified dataframe as a table to Cytoscape.
pushTable <- function (name, df) {	
	data <- c()
	for(i in 1:length(names(df))) {
		data <- append(data, as.vector(t(df[i])))
	}
	xml.rpc('localhost:9000', 'Cytoscape.pushTable', name, as.vector(names(df)), as.character(data))
}