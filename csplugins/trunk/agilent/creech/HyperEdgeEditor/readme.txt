How To Build
------------

Use Maven to build this project. 

Most of the Maven goals take a my-version parameter as in:

    -Dmy-version=<version>

<version> is the version string to use for this release. For a SNAPSHOT release, it may be something like '2.6x-SNAPSHOT' and for a specific release, it may be something like '2.68'. For example, -Dmy-version=2.68 or -Dmy-version-2.6x-SNAPSHOT.

A few of the important Maven goals are:
   package--compiles, runs tests and stores jar of code in the 'target' directory (uses my-version param). This includes 
            two important jars:
	        target/HyperEdgeEditor-<version>-just-classes.jar -- just the classes that make up the HyperEdgeEditor
	        target/HyperEdgeEditor-<version>-jar-with-dependencies.jar -- everything needed to run the HyperEdgeEditor
                                                                              as a plugin of Cytoscape.
   install--same as package, but also generates javadocs (in target/apidocs) and places the
                  jar in your local repository (uses my-version param).
   deploy [-Drepo-type=release]--
       same as install but includes placing the jars in the ramblas snapshot or release repository.
       If the optional -Drepo-type=release is present, the jar will be placed in the
       release repository.
       Example usage:
          mvn deploy -Dmy-version=2.6x-SNAPSHOT
          mvn deploy -Dmy-version=2.68 -Drepo-type=release

Building JavaDocs

   mvn javadoc:javadoc -Dmy-version=2.68--builds javadoc documentation placed in the target/apidocs directory
