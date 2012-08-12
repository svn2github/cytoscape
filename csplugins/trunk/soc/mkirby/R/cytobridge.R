#------------------------------
#CytoBridge -------------------
#Author: Michael Kirby --------
#Depends On: iGraph, rjson, Rcurl ----
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

		V(g)$cytobid <- lapply(V(g)$cytobid,idUpdate)

		E(g)$cytobid <- lapply(E(g)$cytobid,idUpdate)

		g <- set.graph.attribute(g, "cytobid", as.integer(cytob.suid))
	}

	postForm("http://127.0.0.1:2609/cytobridge/JSONNetwork/",data=toJSON(list(network_name=name, node_cytobridge_ids=get.vertex.attribute(g, 'cytobid'), edge_cytobridge_ids=get.edge.attribute(g, 'cytobid'),  edge_source_cytobridge_ids=get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,1]), edge_target_cytobridge_ids=get.vertex.attribute(g,"cytobid",get.edges(g,E(g))[,2]))), style="post")

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

#Pushes the specified graphs Network Table/Attributes.
pushNetworkTable <- function (name, g) {
	gdata <- c()
	gtypes <- c()
	for(i in 1:length(list.graph.attributes(g))) {
		temp <- get.graph.attribute(g,list.graph.attributes(g)[i])
		gdata <- append(gdata, temp)	
		gtypes <- append(gtypes, type(temp))	
	}

	postForm("http://127.0.0.1:2609/cytobridge/JSONNetworkTable/", data=toJSON(list(network_name=name, table_headings=dummy(list.graph.attributes(g)), table_types=dummy(gtypes), table_data=as.character(dummy(gdata)))),style="post")
}

#Pushes the specified graphs Node Table/Attributes.
pushNodeTable <- function (name, g) {
	vdata <- c()
	vtypes <- c()
	for(v in 1:length(list.vertex.attributes(g))) {
		temp <- get.vertex.attribute(g,list.vertex.attributes(g)[v])
		vdata <- append(vdata, temp)
		vtypes <- append(vtypes,type(unlist(temp)))
	}

	postForm("http://127.0.0.1:2609/cytobridge/JSONNodeTable/", data=toJSON(list(network_name=name, table_headings=dummy(list.vertex.attributes(g)), table_types=dummy(vtypes), node_cytobridge_ids=dummy(get.vertex.attribute(g, 'cytobid')), table_data=as.character(dummy(vdata)))),style="post")
}

#Pushes the specified graphs Edge Table/Attributes.
pushEdgeTable <- function(name, g) {
	edata <- c()
	etypes <- c()
	for(e in 1:length(list.edge.attributes(g))) {
		temp <- get.edge.attribute(g,list.edge.attributes(g)[e])
		edata <- append(edata, temp)
		etypes <- append(etypes,type(unlist(temp)))
	}

	postForm("http://127.0.0.1:2609/cytobridge/JSONEdgeTable/", data=toJSON(list(network_name=name, table_headings=dummy(list.edge.attributes(g)), table_types=dummy(etypes), edge_cytobridge_ids=dummy(get.edge.attribute(g, 'cytobid')), table_data=as.character(dummy(edata)))),style="post")
}

#Pushes the specified dataframe as a table to Cytoscape.
pushTable <- function (name, df) {	
	data <- c()
	types <- c()
	for(i in names(df)) {
		temp <- df[[i]]
		data <- append(data, temp)
		types <- append(types,type(temp))
	}
	postForm("http://127.0.0.1:2609/cytobridge/JSONTable/", data=toJSON(list(table_name=name, table_headings=dummy(names(df)), table_types=dummy(types), row_ids=dummy(row.names(df)), table_data=as.character(dummy(data)))), style="post")
	df
}

#Helper function to append one extra element to a list (since singleton wouldn't be list)
dummy <- function(l) {
	append(l,0)
}

#Helper Function to update a list with CytoBIDs (if unspecified)
idUpdate <- function(x) {
	if (is.na(x)) {
		cytob.suid <<- cytob.suid +  1
		as.integer(cytob.suid)
	} else {
		x
	}
}

#Helper Function to return the Java appropriate type of the R Data
type <- function(l) {
	if (is.logical(l)) {
		"Boolean"
	} else if (is.numeric(l)) {
		if (all.equal(as.integer(l),l)==TRUE) {
			"Integer"
		} else {
			"Double"
		}
	} else {
		"String"
	}
}