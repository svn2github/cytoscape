#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version
#-------------------------------------------------------------------------------

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

#please edit the following line, replacing strings in capital:
#java  -Xmx1024M  -Dswing.aatext=true -Djava.ext.dirs="extlibs/antlr-2.7.5.jar;extlibs/log4j-1.2.12.jar;extlibs/antlr-runtime-3.0.1.jar;extlibs/lucene-core-2.0.0.jar;extlibs/arq-extra.jar;extlibs/meta-index;extlibs/arq.jar;extlibs/mysql-connector-java-5.0.7-bin.jar;extlibs/aterm-java-1.6.jar;extlibs/pellet.jar;extlibs/commons-logging-1.1.jar;extlibs/relaxngDatatype.jar;extlibs/concurrent.jar;extlibs/servlet.jar;extlibs/dnsns.jar;extlibs/stax-api-1.0.jar;extlibs/icu4j_3_4.jar;extlibs/stringtemplate-3.0.jar;extlibs/iri.jar;extlibs/sunjce_provider.jar;extlibs/jena.jar;extlibs/sunpkcs11.jar;extlibs/jenatest.jar;extlibs/wstx-asl-3.0.0.jar;extlibs/json.jar;extlibs/xercesImpl.jar;extlibs/junit.jar;extlibs/xml-apis.jar;extlibs/localedata.jar;extlibs/xsdlib.jar" -server -jar cytoscape.jar  cytoscape.CyMain -p plugins "$@"

java  -Xmx1024M  -Dswing.aatext=true -Djava.ext.dirs=extlibs -server -jar cytoscape.jar  cytoscape.CyMain -p plugins "$@"
#example:
#java -Xmx1024M -Dswing.aatext=true -server   -jar cytoscape.jar  cytoscape.CyMain -p plugins "$@"

#Use this command line to use organism specific gene annotations by default
#java -Xmx1024M -jar cytoscape.jar cytoscape.CyMain -b annotation/manifest --JLD plugins $*

#java  -Dswing.aatext=true -Dswing.aatext=true -jar cytoscape.jar cytoscape.CyMain -p plugins "$@"
