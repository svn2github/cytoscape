
This project generates a JNLP file and associated jars suitable for running
Cytoscape as a webstart. To generate things run:

	mvn webstart:jnlp-inline -Dwebstart.url=http://example.com

Note that the "-Dwebstart.url=http://example.com" argument is manadatory to 
provide the jnlp file with a codebase.  

The actual GenomeSpace command is:

	mvn clean webstart:jnlp-inline -Dwebstart.url=http://chianti.ucsd.edu/genomespace

If this is your first time using Webstart you must create a keystore:

	keytool -genkey -alias cytoscape -keypass secret
	enter "secret" for the password.

The actual keystore file created is assumed to be in:  ${user.home}/.keystore

