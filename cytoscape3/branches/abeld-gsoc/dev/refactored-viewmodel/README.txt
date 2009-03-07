Description of sub-directories in this directory:

viewmodel, vizmap:
to be moved under  http://chianti.ucsd.edu/svn/core3

integration_test:
a simple test app to do integration testing

column-oriented-*:
Branches of above which use a column-oriented implementation.  This
will make implementing certain synchronised-viewmodel usecases
simpler.
It is also an experiment to find out whether column-oriented API is
needed to eliminate type-safety warnings.


application, view: copies of cytoscape3 bundles (from
svn+ssh://grenache.ucsd.edu/cellar/common/svn/cytoscape3/trunk) these
have some small local changes, but all changes in these bundles will
be temporary hacks, until I get the vizmap-gui part working.
