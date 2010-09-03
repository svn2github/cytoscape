nodecharts pie node="YCL067C" labellist="A,B,C,D" valuelist="1.0,2.0,0.5,5" colorlist="#FF0000,#00FF00,#0000FF,#0000FF80"
nodecharts pie node="YKL101W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5"
nodecharts pie node="YHR084W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="north" colorlist="random"
nodecharts pie node="YDR461W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="south" colorlist="rainbow"
nodecharts pie node="YFL026W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="east" colorlist="modulated"
nodecharts pie node="YGL008C" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="west" colorlist="contrasting"
nodecharts pie node="YBR112C" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="northwest"
nodecharts pie node="YCR084C" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="southwest"
nodecharts pie node="YNL145W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="northeast"
nodecharts pie node="YIL015W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="southeast"
nodecharts pie node="YBL069W" labellist="A,B,C,D,E,F" valuelist="3.0,2.0,0.5,5,1,1.5" position="10,10"

nodecharts clear node="YCR084C"

network view update
