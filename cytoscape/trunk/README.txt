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
   +---application/ This directoy contains a maven project that builds and
   +                assembles the Cytoscape application jar.
   +
   +---distribution/ Contains a maven project that assembles the Cytoscape
                     distribution based on the core plugins and the jar file
                     built by application.  This directory includes all shell
                     scripts, sample data, and licenses for the distribution.


III. New Build Process
======================

To build cytoscape:

1.  Download and install Apache Maven: http://maven.apache.org/

2.  To compile the Cytoscape:
 a. go into the application directory:

	cd application

 b. execute maven:

	mvn install

3.  To run cytoscape:
 a. go into the distribution directory:

	cd distribution 

 b. build the maven assembly:

	mvn assembly:assembly

 c. go into the target directory:

	cd target/asdf/asdf

 d. This will include the basic Cytoscape distribution.


For questions, email Mike Smoot:  msmoot@ucsd.edu 


IV.  Doing a Full Cytoscape Release
===================================
???
