
This project generates a JNLP file and associated jars suitable for running
Cytoscape as a webstart. To generate things run:

	mvn webstart:jnlp-inline -Dwebstart.url=http://example.com

Note that the "-Dwebstart.url=http://example.com" argument is manadatory to 
provide the jnlp file with a codebase.
