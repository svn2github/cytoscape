#!/usr/bin/env python

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

<person>
<name>Jugemu Jugemu Gokoh no surikiri
Kaijari suigyo no suigyo matsu
Fu-rai matsu
Ku-rai matsu
U-rai matsu
Kuu neru tokoro ni sumutokoro
Paipo Paipo Paipo no shuringan
shuringan no
gu-rindai
gu-rindaino popokopi- no ponpokonano
choukyuumeino chosuke
</name>
<age>400</age>
</person>

</PersonalList>
""")

import xml.sax as sax
from xml.sax.saxutils import DefaultHandler

class MyHandler(DefaultHandler):

    def __init__(self):
        self._tagstack = []

    def startElement(self, name, attrs):
        self._tagstack.append(name)
        self._current_attrs = attrs
        print "START:", self._tagstack, name, attrs

    def endElement(self, name):
        self._tagstack.pop()
        print "END:", self._tagstack, name, self._current_chara

    def characters(self, chara):
        print "CHARA:", chara
        self._current_chara = chara

def get_serupdata(path):
    parser = sax.make_parser()
    parser.setFeature(sax.handler.feature_namespaces, 0)
    parser.setContentHandler(MyHandler())
    file = open(path, 'r')
    parser.parse(file)


get_serupdata(tmp_obj.filename())

