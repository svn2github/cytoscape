Modifying the Plugin to Point to a Different cPath Server
=========================================================

The cPath2 Plugin provides direct access to any cPath server.  By default, however,
it provides access to PathwayCommons.org.

If you want to point the plugin to a different instance of cPath:

1.  Modify plugin.props (in this directory).  You can change any of the following three properties:

#cpath2.server_name=Mouse Interaction Database
#cpath2.server_url=http://awabi.cbio.mskcc.org/pc-demo/webservice.do
#cpath2.server_blurb=The Mouse Interaction Database is... blah, blah, blah....

Feel free to update the following other properties:

pluginName=PathwayCommons
pluginDescription=Provides connectivity to the Pathway Commons repository (http://www.pathwaycommons.org).
projectURL=http://www.pathwaycommons.org

2.  recreate the jar

ant jar

3.  That's it.  You are Done!