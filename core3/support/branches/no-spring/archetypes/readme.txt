File name: readme.txt

This project is an aid to create bundle templates for Cytoscape 3. It can create bundle templates for 
(1) api-provider-plugin
(2) cyaction-plugin
(3) task-plugin

To make use of these archtype plugins, the user should compile this project with the command "mvn clean install" first.

To create a new bundle, follow these steps,

1. Run the interactive command "mvn archetype:generate"
2. Choose a local plugin (say, cyaction-plugin)
3. Input groupId: org.cytoscape.myplugin
4. Input artifactId: cyaction-plugin
5. Input version: 0.0.1-SNAPSHOT
6. Input package: org.cytoscape.myplugin

Wait a few seconds, a new project "cyaction-plugin" will be created automatically.

If the user wants to develop the bundle with Eclipse, he/she should create the project within the Eclipse workspace.
Then use "File->New->Java project" to import the project and provide a project name, say "cyaction-plugin". Browse 
and choose the project he/she just created, say "cyaction-plugin". Now the project should be shown as a regular Maven 
project and is ready to be edited within Eclipse.