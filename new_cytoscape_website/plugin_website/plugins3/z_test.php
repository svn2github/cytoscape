<?php

//$jarFile = "/cellar/users/pwang/work2/workspace/cytoscape28/plugins/core/filter.jar";
$jarFile = "./z_filter.jar";
$pluginClassName="filter.model.Filter";

//$cmd = '/cellar/local/java/linux-x86/1.6/bin/javap -v -classpath '.$jarFile.' '.$pluginClassName.'| grep "major version"'; 
$cmd = './javap -v -classpath '.$jarFile.' '.$pluginClassName.'| grep "major version"'; 
print $cmd."\n\n";

$results = shell_exec($cmd);

$version = substr($results,16);
print $results;
print $version;

$result = shell_exec("ls /tmp");
print $result;
?>
