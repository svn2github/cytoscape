Welcome  to the New Cytoscape Build Process
--------------------------------------------

I.  Doing a Clean Checkout
==========================

If this is the first time reading this, make sure that you have done a clean checkout.  
You will need to have subversion installed (http://subversion.tigris.org).

Then, do a clean check-out:

	svn checkout file:///cellar/common/svn/cytoscape/trunk cytoscape 

or if you're remote:

	svn checkout svn+ssh://grenache.ucsd.edu/cellar/common/svn/cytoscape/trunk cytoscape 


II.  New Directory Structure
============================

Cytoscape now contains the following directory structure:

cytoscape
   +
   +---corelibs/       This directoy contains all libraries written by the  
   +                   Cytoscape project and used in the Cytoscape Application.
   +
   +---application/    This directoy contains the code for the Cytoscape 
   +                   application.
   +
   +---coreplugins/    This directoy contains all plugins delivered as part 
   +                   of the Cytoscape Application.
   +
   +---distribution/   Contains a maven project that assembles the Cytoscape
   +                   distribution based on the core plugins and the jar file
   +                   built by application.  This directory includes all shell
   +                   scripts, sample data, and licenses for the distribution.
   +
   +---javadoc/        This directory contains a pom that creates a Javadoc jar
   +                   file that only includes javadocs for corelibs and 
   +                   application, i.e. the public Cytoscape API.
   +
   +---packaging/      This directoy contains a maven pom file that creates
   +                   Install4j release bundles and then puts the distribution
   +                   zip file and javadocs in the same directory. Note that
   +                   this packaging ONLY happens in the deploy phase.
   +
   +---webstart/       This directoy contains a maven pom file that creates
   +                   a webstart (JNLP) distribution of Cytoscape. The webstart 
   +                   bundle is not created as part of the normal maven life
   +                   cycle.  See the README.txt in that directory for
   +                   instructions on building a webstart distribution.
   +
   +---legacy/         Contains old and currently unused test data and resources. 


III. Build Process
==================

To build cytoscape:

1.  Download and install Apache Maven: http://maven.apache.org/

2.  To compile everything: 
 a. in the top level directory: 

	mvn install

3.  To run cytoscape (assuming you've run mvn install):
 a. you'll find the normal cytoscape distribution directory here:

	cd distribution/target/distribution-${version}-null.dir/distribution-${version} 


For questions, email Mike Smoot:  msmoot@ucsd.edu 


IV. Release Process
===================

A release is created using the Maven Release plugin. So, incrementing the 
version numbers, tagging the release, incrementing the version numbers again, 
and then building the tagged version of the release and deploying properly 
versioned artifacts is accomplished as follows:

	mvn release:prepare
	mvn release:perform

The next step is to copy the downloadable artifacts to the Cytoscape website.
Everything to be copied over can be found in the packaging/target/install4j 
directory.
