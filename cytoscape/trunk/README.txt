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
   +---src/ This directoy contains the source code for the Cytoscape application. 
   +
   +---pom.xml The maven configuration file for the Cytoscape application.


III. New Build Process
======================

To build cytoscape:

1.  Download and install Apache Maven: http://maven.apache.org/

2.  To compile the Cytoscape execute maven:

	% mvn install

3.  To run cytoscape:

	% cd target/application-${version}-cytoscape/application-${version}
	% cytoscape.sh


For questions, email Mike Smoot:  msmoot@ucsd.edu 


IV.  Doing a Full Cytoscape Release
===================================
???
