
# checkout on test server
svn checkout svn+ssh://grenache.ucsd.edu/cellar/common/svn/new_cytoscape_website/plugin_website/plugins3 /var/www/html/cyto_web/plugins3

# checkout on product server (read only)
svn checkout http://chianti.ucsd.edu/svn/new_cytoscape_website/plugin_website/plugins3 /var/www/html/cyto_web/plugins3

# update
svn update

# Commit the change
svn commit -m "my comments" fileToCommit


//Install Zend lucene search (no required on chianti, since lucene already installed with PHP)
#cp plugins/Zend Zend    ******* from 2.X *******
#chgrp -R apache Zend/
#chown -R apache Zend/

//This is required
#mkdir luceneIndex/index
#chgrp -R apache luceneIndex
#chown -R apache luceneIndex
 
// Give apache permission to run Perl/csh script through PHP
#chmod o+x generate_plugin_xml.pl 
#chown apache plugins3.xml
#chgrp apache plugins3.xml


 
 