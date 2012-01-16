#!/bin/bash

# checkout the renderer
svn co http://chianti.ucsd.edu/svn/csplugins/trunk/toronto/yuedong/paperwing-impl

# checkout bundles that need to be patched
for i in presentation-impl swing-application-impl vizmap-gui-impl
do
	svn co http://chianti.ucsd.edu/svn/core3/impl/trunk/$i
done

# checkout cytoscape
svn co http://chianti.ucsd.edu/svn/core3/gui-distribution/trunk gui-distribution
