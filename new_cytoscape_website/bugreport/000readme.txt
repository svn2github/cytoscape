# install on test server
svn checkout svn+ssh://grenache.ucsd.edu/cellar/common/svn/new_cytoscape_website/bugreport /var/www/html/cyto_web/bugreport

# install on product server (read only)
svn checkout http://chianti.ucsd.edu/svn/new_cytoscape_website/bugreport /var/www/html/cyto_web/bugreport

# update
svn update

# Commit the change
svn commit -m "my comments" fileToCommit



To set up DB 

mysql -u root -p < bugDB_schema.sql
grant all on bugs.* to cytostaff@localhost Identified by 'cytostaff';
grant Select, insert,update on bugs.* to cytouser@localhost Identified by 'cytouser';
