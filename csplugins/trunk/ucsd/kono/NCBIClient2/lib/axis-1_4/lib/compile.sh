AXIS_LIB=.
export AXIS_LIB

AXISCP=.:$AXIS_LIB/axis.jar:$AXIS_LIB/commons-discovery-0.2.jar:$AXIS_LIB/commons-logging-1.0.4.jar:$AXIS_LIB/jaxrpc.jar:$AXIS_LIB/saaj.jar:$AXIS_LIB/log4j-1.2.8.jar:$AXIS_LIB/wsdl4j-1.5.1.jar

export AXISCP

javac -J-ms20m -J-mx1000m -classpath $AXISCP /cellar/users/kono/workspace/WebServicePlugin/lib/axis-1_4/lib/uk/ac/ebi/www/ws/services/WSDbfetch/*.java
