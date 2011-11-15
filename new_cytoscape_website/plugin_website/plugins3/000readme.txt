
#For checkout on test server
svn checkout svn+ssh://grenache.ucsd.edu/cellar/common/svn//new_cytoscape_website/plugin_website/plugins3 /var/www/html/cyto_web/plugins3

#For checkout on product server (read only)
svn checkout http://chianti.ucsd.edu/svn/new_cytoscape_website/plugin_website/plugins3 /var/www/html/cyto_web/plugins3

# For update
svn update
