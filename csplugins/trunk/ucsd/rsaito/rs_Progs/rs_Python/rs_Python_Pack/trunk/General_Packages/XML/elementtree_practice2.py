#!/usr/bin/env python

import Usefuls.TmpFile

import elementtree.ElementTree as ET

tmp_obj = Usefuls.TmpFile.TmpFile_II("""

<ItemList>
<item>Spring</item>
<item>Summer</item>
<item>Fall</item>
<item>Winter</item>
</ItemList>

""")


tree = ET.parse(tmp_obj.filename())

# the tree root is the toplevel html element
item = tree.findtext("item")
print item

for item in tree.getiterator("item"):
    print item.text
