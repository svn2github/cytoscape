
Introduction
------------

This is a pseudo project that relies on the svn:externals property to pulls 
together many independently versioned projects into a coherent whole that
is easy to build and manipulate.


Running Maven 
-------------

To build the a project (e.g. "model-api") you can enter the project directory 
and run whatever commands you want or from the top level directory, use the 
maven "project list" feature:

	mvn install -pl model-api

To build the "model-api" project as well as everything that depends on it,
run the command:

	mvn install -pl model-api -amd

This will result in a large number of projects building because nearly
all projects depend on the classes and interfaces in model-api.  Use
this command when you want to be sure that you've updated everything
that depends on the project you changed.

To build the "model-api" project and everything that it requires to work:

	mvn install -pl model-api -am 

This command should result in maven first building the event-api project
followed by model-api.  This is because model-api has a dependency on
event-api.


Subversion
----------

Despite the fact that all each directory listed here represents a independent
subversion repository with its own trunk/branches/tags triple, most subversion
commands can be run on all projects at once.  This means a change that touches
multiple projects can be committed as a single change. However, there are a
few details to pay attention to.

Updating:  This works as normal.  Running "svn update" will recursively
update all projects listed.

Status: Running "svn status" will also recurse as expected, but so much
extraneous information is presented, we've provided the "status" shell
script in the top level directory to run "svn status" but omit blank lines 
and other non-essential information.

Committing: The svn commit command works slightly differently.  To commit
changes in multiple project subdirectories, you should run:

	svn commit -m "a useful message" *

If you forget the '*' you'll only commit the files in the top level 
directory.






