#examples.py
# some example methods

#sys.path.append("/users/dtenenba/workspace/csplugins5/csplugins/isb/dtenenbaum/jython/test/scripts") 


#print "here are the methods in this package: bla bla"

def hideSomeEdges(threshold=4):
    "Hides all nodes with more than %s edges." % threshold
    import cytoscape as cy
    network = cy.Cytoscape.getCurrentNetwork()
    netview = cy.Cytoscape.getCurrentNetworkView()
    node_count = network.getNodeCount()
    if node_count == 0:
        print "You must load a network in order to run this example."
        return;
    print "Hiding all nodes with more than %s edges..." % threshold
    for i in range(1, node_count+1):
        node = network.getNode(i)
        edgeList = network.getAdjacentEdgesList(node,1,1,1)
        indicesList = network.getAdjacentEdgeIndicesArray(i,1,1,1)
        if len(edgeList) > threshold:
            # first hide the node
            nodeView = netview.getNodeView(i)
            netview.hideGraphObject(nodeView)
            for index in indicesList:
                edgeView = netview.getEdgeView(index)
                netview.hideGraphObject(edgeView)
    netview.redrawGraph(0,0)
    print "Done"
            

def unhideAll():
    "Unhides all nodes and edges"
    import cytoscape.actions as actions
    import cytoscape as cy
    netview = cy.Cytoscape.getCurrentNetworkView()
    print "Unhiding all nodes and edges..."
    actions.GinyUtils.unHideAll(netview)
    netview.redrawGraph(0,0)
