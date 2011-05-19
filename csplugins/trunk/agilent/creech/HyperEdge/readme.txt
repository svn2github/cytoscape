How To Build
------------

Use Maven to build this project. Note that the tests, when run my Maven, sometimes fail for no given reason. In such cases, you can run without testing or just running specific tests as shown below.

Most of the Maven goals take a my-version parameter as in:

    -Dmy-version=<version>

<version> is the version string to use for this release. For a SNAPSHOT release, it may be something like '2.6x-SNAPSHOT' and for a specific release, it may be something like '2.63'. For example, -Dmy-version=2.63 or -Dmy-version-2.6x-SNAPSHOT.

A few of the important Maven goals are:
   package--compiles, runs tests and stores jar of code in the 'target' directory (uses my-version param).
   install--same as package, but also generates javadocs (in target/apidocs) and places the
                  jar in your local repository (uses my-version param).
   deploy [-Drepo-type=release]--
       same as install but includes placing the jar in the ramblas snapshot or release repository.
       If the optional -Drepo-type=release is present, the jar will be placed in the
       release repository.
       Example usage:
          mvn deploy -Dmy-version=2.6x-SNAPSHOT
          mvn deploy -Dmy-version=2.63 -Drepo-type=release

Running Tests

    mvn install-DskipTests=true -Dmy-version=2.63--don't perform tests.
    mvn test -Dtest=EventTest--only perform test EventTest

Building JavaDocs

   mvn javadoc:javadoc -Dmy-version=2.63--builds javadoc documentation placed in the target/apidocs directory


