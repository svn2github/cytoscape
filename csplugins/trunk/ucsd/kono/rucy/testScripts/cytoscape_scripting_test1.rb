require 'java'
puts "============= Running Ruby Script for Cytoscape ================" 

include_class 'java.util.HashMap'
include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.layout.CyLayouts'
include_class 'cytoscape.layout.LayoutAlgorithm'
include_class 'cytoscape.CytoscapeInit'
include_class 'java.awt.Color'
include_class 'cytoscape.visual.VisualStyle'
include_class 'cytoscape.visual.VisualPropertyType'


props = CytoscapeInit.getProperties
props.setProperty("layout.default", "force-directed")
puts "Apply Layout Algorithm: " + props.getProperty("layout.default");

defVS = Cytoscape.getVisualMappingManager.getVisualStyle
defVS.getNodeAppearanceCalculator.getDefaultAppearance.setFillColor(Color.white)
defVS.getGlobalAppearanceCalculator.setDefaultBackgroundColor(Color.black)

Dir::foreach("../../sample1") {|f|
  puts("File Names = " + f)
  
  if /^\./ =~ f
  	puts("This is dir" + f)
  else
  	fileName = "../../sample1/" + f
  	Cytoscape.createNetworkFromFile fileName
  	Cytoscape.getCurrentNetworkView.redrawGraph(false, true)
  end
} 




puts "================ Done! ================"