#!/usr/bin/env python

import elementtree.ElementTree as ET

# build a tree structure
root = ET.Element("html")

head = ET.SubElement(root, "head")

title = ET.SubElement(head, "title")
title.text = "Page Title"

body = ET.SubElement(root, "body")
body.set("bgcolor", "#ffffff")

body.text = "Hello, World!"

# wrap it in an ElementTree instance, and save as XML
tree = ET.ElementTree(root)
tree.write("page.xhtml")

tree = ET.parse("page.xhtml")

# the tree root is the toplevel html element
print tree.findtext("head/title")

# if you need the root element, use getroot
root = tree.getroot()

# ...manipulate tree...

tree.write("out.xml")
