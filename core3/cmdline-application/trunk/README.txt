
Don't add any new modules or any code to cmdline-launcher!  
Instead add any new bundles that you'd like to load to the
list of dependencies found in the cmdline-provision/pom.xml.
All dependencies (and transitive dependencies) from this
pom will be copied into the load dir of the assembly.

To build things:

	% mvn clean package assembly:directory

To run things:

	% cd target/cmdline-application-1.0-SNAPSHOT-app.dir/cmdline-application/
	% java -jar cmdline-launcher-1.0-SNAPSHOT-jar-with-dependencies.jar

All jars found in the "load" directory will attempt to be installed as OSGi 
bundles.  You can dynamically add/remove jars from this directory and they 
will be installed/uninstalled.
