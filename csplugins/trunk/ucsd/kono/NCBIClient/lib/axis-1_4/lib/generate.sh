AXIS_LIB=/cellar/users/kono/workspace/WebServicePlugin/lib/axis-1_4/lib
export AXIS_LIB

AXISCP=.:$AXIS_LIB/axis.jar:$AXIS_LIB/commons-discovery-0.2.jar:$AXIS_LIB/commons-logging-1.0.4.jar
AXISCP=$AXISCP:$AXIS_LIB/jaxrpc.jar:$AXIS_LIB/saaj.jar:$AXIS_LIB/log4j-1.2.8.jar:$AXIS_LIB/wsdl4j-1.5.1.jar
export AXISCP

java -cp $AXISCP org.apache.axis.wsdl.WSDL2Java --wrapArrays --timeout -1 http://www.ebi.ac.uk/Tools/webservices/wsdl/WSDbfetch.wsdl
