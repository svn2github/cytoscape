Cytoscape is a software 


How to install RDFScape
---------------------------------

At the moment an installation utility is not present, and the installation process is not immediate.

First, you need to download Cytoscape, Jena and Pellet.

Cytoscape: http://www.cytoscape.org (v2.4 and 2.4.1 are tested)

Jena: http://jena.sourceforge.net use v2.5.2

Pellet: jena v.2.5.2 rquires that, for the time being, you use the latest svn version of Pellet.
If this is a problem, you can try v2.5.1 of Jena and Pellet 1.4 (not tested, but it should work).

for instructions on how to build Pellet, refer to http://pellet.owldl.com


An ent file is provided to build RDFScape, but you can also use the .jar file in dist/plugin
(each tarball includes this binary)


put RDFSCape.jar in the Cytoscape plugin directory.

make sure that Cytoscape can see at start time all the libraries that RDFScape needs.

On macosx systems, this can be easily achieved by:

move to the directory where pellet is, then:
cd lib
cp pellet.jar /Library/Java/Extensions/

move to the directory where Jena is, then:
cd lib
cp *.jar /Library/Java/Extnsions/


It is also possible to change the cytoscape.sh / cytoscape.bat script 
and add all these classes in the Jaba -Djava.ext.dirs=class.jar:class,jar...

(Some multi platform script will be provided soon)


Finally copy the directory rdfscapecontexts in extras (with all its subdirectories) in the Cytoscape directory.

----------------------------------

Running RDFSCape


Run Cytscape and select the RDFScape plugin.
Read the RDFSCape vizmap (File->import_vizmap) : rdfscapeVizmap in rdfscape/extras

Refer to the manual for how to use it (manual will come).


