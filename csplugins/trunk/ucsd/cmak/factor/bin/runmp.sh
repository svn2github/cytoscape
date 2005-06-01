
rm dfs.kos
rm dfspath.out

java  -Xmx512m -Djava.util.logging.config.file=./logging.properties \
fgraph.MPMain \
$1
