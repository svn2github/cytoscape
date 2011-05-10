1) Compile the sample plugin with:

   javac -cp cytoscape3-api.jar org/cytoscape/plugin/example/SamplePlugin.java

2) Create the actual plugin jar with:

   jar cfM SamplePlugin.jar META-INF/MANIFEST.MF org/cytoscape/plugin/example/*.class plugin.props

