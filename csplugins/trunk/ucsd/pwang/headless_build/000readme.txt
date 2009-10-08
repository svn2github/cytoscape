
				How to build Cytoscape product?

1. Install Eclipse35 in 'c:/eclipse35', also install the delta package of Eclipse.

2. put all Cytoscape bundles in one folder, say, cy3bundles/plugins/

3. Edit 'cytoscape300.product', set release version number, e.g. 3.0.0, 3.1.0

4. Edit feature.xml, list all bundles, which should be included in the release

5. Edit 'build_feature_config.xml' and 'build_product_config.xml', make appropriate change in setting

6. Run command 'ant -f build_feature_run.xml', then 'ant -f build_product_run.xml'

After the build completion, look at the created ZIP file './build_product/I.CytoscapeBuild/CytoscapeBuild-.zip'.
This is the the release bundle of Cytoscape. contents in './cy3repository' is the update repository of this 
release.

7. Do the following configuration for the release bundle.

Edit the file configurations/config.ini. Add two lines
eclipse.application=cytoscape_product.application
eclipse.product=cytoscape_product.product

Edit file cytoscape/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info
Replace string false to true 