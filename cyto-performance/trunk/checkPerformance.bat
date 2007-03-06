@ECHO off
ECHO Start performance test for cytoscape versions
ECHO ---------------------------------------------
REM Set these values according to your configuration
REM This setup based on dir structure basedir--+Cytoversion1
REM                                            +CorePluginsversion1
REM                                            +Cytoversion2
REM                                            +CorePluginsversion2  etc etc
set base=..
set cytoversion_current=\CytoscapeSVNEdit
set cytoversion_2_4=\CytoscapeSVN-2_4
set cytoversion_2_3=\CytoscapeSVN-2_3_2
set corepluginscurrent=\CorePluginsSVN
set coreplugins_2_4=\CorePluginsSVN-2_4
set coreplugins_2_3=\CorePluginsSVN-2_3

ECHO First for current version: %base%%cytoversion_current%
ECHO cleanup; a new run.log is created
CALL ant clean > run.log
ECHO cleaned up -- start compiling
CALL ant -Dcytoscape.dir=%base%%cytoversion_current% -Dplugin.root.dir=%base%%corepluginscurrent% -Dcytoscape.version=current run  >> run.log
ECHO ran ant

ECHO Second for version 2.4: %base%%cytoversion_2_4%
ECHO Cleaning up
CALL ant clean >> run.log
ECHO cleaned up -- start compiling
CALL ant -Dcytoscape.dir=%base%%cytoversion_2_4% -Dplugin.root.dir=%base%%coreplugins_2_4% -Dcytoscape.version=2.4.0 run  >> run.log
ECHO ran ant

ECHO Third for version 2.3.2: %base%%cytoversion_2_3%
ECHO Cleaning up
CALL ant clean >> run.log
ECHO cleaned up -- start compiling
CALL ant -Dcytoscape.dir=%base%%cytoversion_2_3% -Dplugin.root.dir=%base%%coreplugins_2_3% -Dcytoscape.version=2.3.2 run  >> run.log
ECHO ran ant

ECHO Create compare html file
java -cp track.cyperf.jar;ui.cyperf.jar cytoscape.performance.ui.HTMLResults *.perf
ECHO Created html_overview 
java -cp track.cyperf.jar;ui.cyperf.jar cytoscape.performance.ui.AlignedResults *.perf
ECHO Created aligned_results 

ECHO Ended tests
@ECHO on