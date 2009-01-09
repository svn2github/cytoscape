#!/bin/csh
if ($#argv == 0) then
  echo "USAGE: javadoc.sh <directory>"; echo
  echo "ex: javadoc.sh /common/javadoc"; echo
else
  cd $1
  mkdir temp
  cd $1/temp
  cvs co cytoscape
  cvs co csplugins
  javadoc -sourcepath $1/temp \
    -breakiterator \
    -d $1/cytodocs -use -doctitle "Cytoscape Core & Plugin API" \
    -splitIndex -group "Core Packages" "cytoscape*" \
    -group "Plugin Packages" "csplugins*" \
    -link "http://java.sun.com/j2se/1.4/docs/api/" \
    -link "http://www.yworks.de/products/yfiles/doc/api/" \
    -link "http://www.jdom.org/docs/apidocs/" \
    -classpath /common/junit3.7/junit.jar:/common/junit:/common/packages/yfiles-2.0.1/y.jar:/common/visad/visad.jar:$1/temp/cyto-aux.jar \
    -J-Xmx180m \
    -subpackages cytoscape \
    >& $1/javadoc.out
  cd $1/temp
  cvs -Q release cytoscape
  cvs -Q release csplugins
  cd $1
  rm -r temp
endif
