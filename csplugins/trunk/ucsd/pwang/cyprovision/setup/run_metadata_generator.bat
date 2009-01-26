#eclipse.exe -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -config /work/sandbox/acme_p2 -metadataRepository file:/work/sandbox/tmp  -artifactRepository file:/work/sandbox/tmp


#eclipse -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator 
#	-config /work/sandbox/acme_p2/
#	-metadataRepository file:C:/work/sandbox/repository/ 
#	-metadataRepositoryName "My Update Site" 
#	-artifactRepository file:C:/work/sandbox/repository/ 
#	-artifactRepositoryName "My Artifacts" 
#	-publishArtifacts 
#	-publishArtifactRepository 
#	-root cytoscape_root   
#	-rootVersion 1.0.0 
#	-noDefaultIUs 
#	-vmargs -Xmx256m

#java -jar cytoscape300/plugins/org.eclipse.equinox.launcher_1.0.200.v20081201-1815.jar -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -config /work_cy3/cytoscape300/ -metadataRepositoryName "Cy3 Repository"  -root cytoscape_root -rootVersion 1.0.0 -noDefaultIUs -vmargs -Xmx256m

java -jar plugins/org.eclipse.equinox.launcher_1.0.200.v20081201-1815.jar -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -source /work_cy3/cytoscape300/cytoscape/ -metadataRepository file:C:/work_cy3/cy3repo/ -metadataRepositoryName "http://tocai.ucsd.edu/updates/cy3repo" -artifactRepository file:C:/work_cy3/cy3repo/ -artifactRepositoryName "http://tocai.ucsd.edu/updates/cy3repo" -publishArtifacts -publishArtifactRepository -root cytoscape300IU -rootVersion 3.0.0  -append  -noDefaultIUs -vmargs -Xmx256m
java -jar plugins/org.eclipse.equinox.launcher_1.0.200.v20081201-1815.jar -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -source /work_cy3/cytoscape310/cytoscape/ -metadataRepository file:C:/work_cy3/cy3repo/ -metadataRepositoryName "http://tocai.ucsd.edu/updates/cy3repo" -artifactRepository file:C:/work_cy3/cy3repo/ -artifactRepositoryName "http://tocai.ucsd.edu/updates/cy3repo" -publishArtifacts -publishArtifactRepository -root cytoscape310IU -rootVersion 3.1.0  -append -noDefaultIUs -vmargs -Xmx256m
