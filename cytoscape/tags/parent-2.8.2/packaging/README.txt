To build the installer bundles, edit pom.xml to change the <executable> 
for install4jc to where your install4j installation is.  Then run:

	mvn install4j:compile

The installers will be created in the target/install4j subdirectory.
