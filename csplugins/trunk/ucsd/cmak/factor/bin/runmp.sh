
rm dfs.kos
rm dfspath.out

$JAVA_HOME/bin/java  -Xmx512m -Djava.util.logging.config.file=./logging.properties \
fgraph.MPMain \
$1
