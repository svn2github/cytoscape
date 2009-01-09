
To build:
=========

% ant all
% build-cytoscape-osgi.sh


To run with Equinox:
====================

Then cd into build/equinox-cytoscape. Now run:

% cytoscape -clean -console

Then type

osgi> ss

This will list all available bundles.  Find the SBMLReader bundle and start
it using its id.

osgi> start 12

Next find the cytoscape bundle and start it:

osgi> start 13


That should get Cytoscape up and running.

To run with Felix:
==================

cd into build/felix-cytoscape.  Now run:

% cytoscape.sh




Configuration:
==============

The build-cytoscape-osgi.sh script makes cytoscape osgi compatible. It accomplishes
this primarily by running the bnd utility which basically just adds a manifest to
a jar file which exports all classes in the jar and imports everything the jar
needs.  This is run on each library jar in cytoscape.  This is also run on the
cytoscape jar.  

A few jars need to run bnd in a specific way.  The configuration files for these
are found in the bnd directory.  The most important of these are for the JAXB
api, implementation, and cytoscape to share their classpath.  This is necessary
for jaxb reflection to work.


Once bnd has been run, then the contents of the osgi/*-template directories are copied
into place.  Then the wrapped libraries are copied into place.  Then cytoscape is ready 
to run.



osgi/equinox-template
=====================

plugins - This dir contains the osgi launcher bundles and osgi framework bundles.
This includes the configurator for automatically launching all plugins from the
plugins directory.  This dir also contains the SBMLReader plugin as it is now
an osgi plugin and not a CytoscapePlugin.

old_plugins - This contains old cytoscape plugins that are NOT osgi compatible.

configuration - Contains the config.ini.  This just describes which bundles are
started at startup.  If we wanted to avoid the osgi console at startup, this is
where we'd list the bundles to automatically start.  This also includes any
old command line parameters needed by the application.

cytoscape - Actually the eclipse executable renamed as cytoscape.  All it does
is start osgi according to the .ini files.

cytoscape.ini - A specific ini file needed to java VM arguments and to add certain
classes to the bootclasspath.  The look+feel code needed to be accessed from many
different bundles, so it seemed best to put it here.  There may be better ways to
to this.  We're not sure why this wouldn't work in config.ini and why a separate
.ini was needed.



code:
=====

cytoscape.plugin.HostActivator - Is the activator that starts cytoscape.  In this 
hacked up version, it simply calls CyMain.main().

cytoscape.ServiceHandler - Is the class that looks for any GraphFileReader 
services that osgi has registered.  In this case, it should find SBMLReader
(assuming that you remembered to start that bundle). This class is instantiated
right after CyMain.main() is called. 


