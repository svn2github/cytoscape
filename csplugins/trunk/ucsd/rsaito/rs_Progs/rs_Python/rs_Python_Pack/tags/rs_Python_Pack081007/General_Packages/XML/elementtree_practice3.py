#!/usr/bin/env python

import Usefuls.TmpFile

import elementtree.ElementTree as ET

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


tree = ET.parse(tmp_obj.filename())

persons = tree.findall("person")

for person in persons:
    for child in person.getchildren():
        print child.tag, child.text

print "XXXXX XXXXX"

root = tree.getroot()

for subtree in root:
    for child in subtree:
        print child.tag, child.text
