Welcome  to the New Cytoscape Build Process
--------------------------------------------

I.  Doing a Clean Checkout
==========================

If this is the first time reading this, make sure that you have done a clean checkout.  
You will need to have subversion installed (http://subversion.tigris.org).

Then, do a clean check-out:

svn checkout file:///cellar/common/svn/cytoscape/trunk cytoscape 


II.  New Directory Structure
============================

Cytoscape now contains the following directory structure:

cytoscape
   +
   +---corelibs/       This directoy contains all libraries written by the  
   +                   Cytoscape project and used in the Cytoscape Application.
   +                   (is 
   +
   +---application/    This directoy code for the Cytoscape application.
   +
   +---coreplugins/    This directoy contains all plugins delivered as part 
   +                   of the Cytoscape Application.
   +
   +---distribution/    Contains a maven project that assembles the Cytoscape
   +                    distribution based on the core plugins and the jar file
   +                    built by application.  This directory includes all shell
   +                    scripts, sample data, and licenses for the distribution.
   +
   +---release-bundles/ This directoy contains a maven pom file that creates
   +                    Install4j release bundles.
   +
   +---webstart/        This directoy contains a maven pom file that creates
                        a webstart (JNLP) distribution of Cytoscape. 


III. New Build Process
======================

To build cytoscape:

1.  Download and install Apache Maven: http://maven.apache.org/

2.  To compile everything: 
 a. in the top level directory: 

	mvn install

3.  To run cytoscape (assuming you've run mvn install):
 a. you'll find the normal cytoscape distribution directory here:

	cd distribution/target/distribution-${version}-null.dir/distribution-${version} 


For questions, email Mike Smoot:  msmoot@ucsd.edu 


