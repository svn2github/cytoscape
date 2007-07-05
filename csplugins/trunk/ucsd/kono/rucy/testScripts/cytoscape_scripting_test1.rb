require 'java'

include_class 'java.util.HashMap'
include_class 'cytoscape.Cytoscape'
include_class 'cytoscape.layout.CyLayouts'
include_class 'cytoscape.layout.LayoutAlgorithm'
include_class 'cytoscape.CytoscapeInit'
include_class 'java.awt.Color'
include_class 'cytoscape.visual.VisualStyle'
include_class 'cytoscape.visual.VisualPropertyType'
include_class 'java.awt.Frame'

props = CytoscapeInit.getProperties
props.setProperty("layout.default", "force-directed")
puts "Apply Layout Algorithm: " + props.getProperty("layout.default");

colorProp = VisualPropertyType.values[0]
edgeColorProp = colorProp = VisualPropertyType.values[13]
edgeWidthProp = colorProp = VisualPropertyType.values[23]

defVS = Cytoscape.getVisualMappingManager.getVisualStyle
defVS.getNodeAppearanceCalculator.getDefaultAppearance.set(colorProp, Color.red)
defVS.getEdgeAppearanceCalculator.getDefaultAppearance.set(edgeColorProp, Color.white)
defVS.getEdgeAppearanceCalculator.getDefaultAppearance.set(edgeWidthProp, 3)

defVS.getGlobalAppearanceCalculator.setDefaultBackgroundColor(Color.black)

Dir::foreach("../../sample1") {|f|
  if /^\./ =~ f
  	puts("Ignore non-network file: " + f)
  else
  	fileName = "../../sample1/" + f
  	Cytoscape.createNetworkFromFile fileName
  	Cytoscape.getCurrentNetworkView.redrawGraph(false, true)
  end
}