#!/usr/bin/env python

import xml.sax as sax
from xml.sax.saxutils import DefaultHandler

class MyHandler(DefaultHandler):

    def __init__(self, iodict):
        self._tagstack = []
        self._atrstack = []
        self.iodict = iodict

    def startElement(self, name, attrs):
        self._tagstack.append(name)
        self._atrstack.append(attrs)
        # print "START:", self._tagstack, name, attrs

    def endElement(self, name):
        if not self._current_chara.isspace():
            self.main_process()
        self._current_chara = None
        self.end_process()
        self._tagstack.pop()
        self._atrstack.pop()

    def characters(self, chara):
        # print "CHARA:", chara
        self._current_chara = chara

    def main_process(self):
        tmp_key = tuple(self._tagstack +  [self.current_name()])
        tmp_val = self._current_chara
        self.iodict[ tmp_key ] = tmp_val

        print self._tagstack, self.current_name(), self._current_chara

    def end_process(self):
        print "Ending", self.current_name()
        pass

    def current_name(self):
        if self._tagstack != []:
            return self._tagstack[-1]
        else:
            return None

    def current_attr(self):
        if self._atrstack != []:
            return self._atrstack[-1]
        else:
            return None


def read_XML_SAX(xml_filename, iodict):
    parser = sax.make_parser()
    parser.setFeature(sax.handler.feature_namespaces, 0)
    parser.setContentHandler(MyHandler(iodict))
    fh = open(xml_filename, 'r')
    parser.parse(fh)
    fh.close()


if __name__ == "__main__":
    import Usefuls.TmpFile

    tmp_obj = Usefuls.TmpFile.TmpFile_II("""
<PersonalList>
<person>
<name>Saito</name>
<age>35</age>
<affiliation>Keio</affiliation>
</person>

<person>
<name>Yano</name>
<age>36</age>
</person>

<person>
<name>Sakai</name>
</person>

<person>
<name>Yukawa</name>
<affiliation>NTT Data</affiliation>
</person>

</PersonalList>
""")

    iodict = {}
    read_XML_SAX(tmp_obj.filename(), iodict)
    print iodict

