COMMAND LINE CYTOSCAPE
======================

To build things:
----------------

	% mvn clean package assembly:directory

To run things:
--------------

	% cd target/cmdline-application-1.0-SNAPSHOT-app.dir/cmdline-application/
	% java -jar headless-cytoscape.jar

Running this command should give you a list of possible Tasks to execute.  Specify
a Task as an argument to see all of the options for the Task.

	% java -jar headless-cytoscape.jar -LoadNetworkFileTask

Tasks can be run sequentially:

	% java -jar headless-cytoscape.jar -LoadNetworkFileTask -file homer.sif -SelectAllNodesTask


Adding plugins:
---------------

To add a plugin bundle jar to the application, simply copy the jar into the "load"
directory. OSGi will attempt to load all jars found in the "load" directory as OSGi bundles.

Deleting a jar from the "load" directory will stop the bundle.


Dependencies:
-------------

Don't add any new modules or any code to cmdline-launcher!  
Instead add any new bundles that you'd like to load to the
list of dependencies found in the cmdline-provision/pom.xml.
All dependencies (and transitive dependencies) from this
pom will be copied into the load dir of the assembly.

