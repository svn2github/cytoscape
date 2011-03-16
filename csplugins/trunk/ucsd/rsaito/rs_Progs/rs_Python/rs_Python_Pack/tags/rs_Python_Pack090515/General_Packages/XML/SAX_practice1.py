#!/usr/bin/env python

import Usefuls.TmpFile

tmp_obj = Usefuls.TmpFile.TmpFile_II("""
<root>
<startup>http://www.wxpython.org/</startup>
<frame-width>500</frame-width>
<frame-height>400</frame-height>
</root>
""")

import xml.sax as sax
from xml.sax.saxutils import DefaultHandler

data_dict = {}
class MyHandler(DefaultHandler):

    def __init__(self, root='root'):
        self.root = root
        self.current_node = root

    def startElement(self, name, attrs):
        print name, attrs
        self.current_node = name

    def endElement(self, name):
        self.current_node = self.root

    def characters(self, char):
        if self.current_node != self.root:
            data_dict[self.current_node] = char

def get_serupdata(path):
    parser = sax.make_parser()
    parser.setFeature(sax.handler.feature_namespaces, 0)
    parser.setContentHandler(MyHandler())
    file = open(path, 'r')
    parser.parse(file)

    return data_dict

get_serupdata(tmp_obj.filename())

print data_dict
