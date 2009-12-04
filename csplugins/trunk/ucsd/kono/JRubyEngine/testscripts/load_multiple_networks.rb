require 'java'

include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.layout.CyLayouts'
include_class 'cytoscape.layout.LayoutAlgorithm'
include_class 'cytoscape.CytoscapeInit'

include_class 'edu.ucsd.bioeng.coreplugin.tableImport.reader.NetworkTableMappingParameters'
include_class 'edu.ucsd.bioeng.coreplugin.tableImport.reader.NetworkTableReader'
include_class 'java.net.URL'

props = CytoscapeInit.getProperties
props.setProperty("layout.default", "force-directed")

Dir::glob("sampleData/*.table").each {|f|
	puts "Loading: " + f
   Cytoscape.createNetworkFromFile f
       	Cytoscape.getCurrentNetworkView.redrawGraph(false, true)
}
