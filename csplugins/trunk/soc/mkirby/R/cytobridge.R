#------------------------------
#CytoBridge -------------------
#Author: Michael Kirby --------
#Depends On: iGraph, rjson ----
#------------------------------

#Pushes the given iGraph to Cytoscape and returns the iGraph with extra CytoBridge
#Consistency attributes.
#name: The name to link this graph to with CytoBridge.
#g: The iGraph to push.
#tables: TRUE if push tables also, FALSE otherwise.
pushNetwork <- function (name,g, tables=TRUE) {
	isGN <- FALSE
	if (class(g) == "graphNEL") {
		g <- igraph.from.graphNEL(g)
		isGN <- TRUE
	}
	if (!('cytobid' %in% list.graph.attributes(g))) {
		cytob.suid <- 0
		V(g)$cytobid <- seq(cytob.suid,cytob.suid+length(V(g))-1)
		cytob.suid <- cytob.suid + length(V(g))-1
		E(g)$cytobid <- seq(cytob.suid,cytob.suid+length(E(g))-1)
		cytob.suid <- cytob.suid + length(E(g))-1
		g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
	} else {
		cytob.suid <<- get.graph.attribute(g, "cytobid")

		V(g)$cytobid <- lapply(V(g)$cytobid,testm)

		E(g)$cytobid <- lapply(E(g)$cytobid,testm)

		g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
	}

	
	socket <- make.socket("localhost", "4444")
	on.exit(close.socket(socket))
	system.time(write.socket(socket, toJSON(list(type="JSONNetwork", network_name=name, node_cytobridge_ids=get.vertex.attribute(g, 'cytobid'), edge_cytobridge_ids=get.edge.attribute(g, 'cytobid'),  edge_source_cytobridge_ids=get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,1]), edge_target_cytobridge_ids=get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,2])))))
	close.socket(socket)

	if (tables) { pushTables(name, g) }

	if (isGN) { g <- igraph.to.graphNEL(g) }

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
		pushNetworkTable(name, g)
	}
	if (node) {
		pushNodeTable(name, g)
	}
	if (edge) {
		pushEdgeTable(name, g)
	}
}

pushNetworkTable <- function (name, g) {
	gdata <- c()
	for(i in 1:length(list.graph.attributes(g))) {
		gdata <- append(gdata, get.graph.attribute(g,list.graph.attributes(g)[i]))		
	}

	socket <- make.socket("localhost", "4444")
	on.exit(close.socket(socket))
	system.time(write.socket(socket, toJSON(list(type="JSONNetworkTable", network_name=name, table_headings=as.vector(list.graph.attributes(g)), table_data=as.character(gdata)))))
	close.socket(socket)
}

pushNodeTable <- function (name, g) {
	vdata <- c()
	for(v in 1:length(list.vertex.attributes(g))) {
		vdata <- append(vdata, get.vertex.attribute(g,list.vertex.attributes(g)[v]))
	}

	socket <- make.socket("localhost", "4444")
	on.exit(close.socket(socket))
	system.time(write.socket(socket, toJSON(list(type="JSONNodeTable", network_name=name, table_headings=as.vector(list.vertex.attributes(g)), node_cytobridge_ids=get.vertex.attribute(g, 'cytobid'), table_data=as.character(vdata)))))
	close.socket(socket)
}

pushEdgeTable <- function(name, g) {
	edata <- c()
	for(e in 1:length(list.edge.attributes(g))) {
		edata <- append(edata, get.edge.attribute(g,list.edge.attributes(g)[e]))
	}

	socket <- make.socket("localhost", "4444")
	on.exit(close.socket(socket))
	system.time(write.socket(socket, toJSON(list(type="JSONEdgeTable", network_name=name, table_headings=as.vector(list.edge.attributes(g)), edge_cytobridge_ids=get.edge.attribute(g, 'cytobid'), table_data=as.character(edata)))))
	close.socket(socket)
}

#Pushes the specified dataframe as a table to Cytoscape.
pushTable <- function (name, df) {	
	data <- c()
	for(i in 1:length(names(df))) {
		data <- append(data, as.vector(t(df[i])))
	}
	xml.rpc('localhost:9000', 'Cytoscape.pushTable', name, as.vector(names(df)), as.character(data))
}



testm <- function(x) {
	if (is.na(x)) {
		cytob.suid <<- cytob.suid +  1
		as.integer(cytob.suid)
	} else {
		x
	}
}