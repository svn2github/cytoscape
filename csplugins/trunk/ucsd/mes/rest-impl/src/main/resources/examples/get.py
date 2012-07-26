#!/usr/bin/python
import httplib, urllib
conn = httplib.HTTPConnection("127.0.0.1:2609")
conn.request("GET", "/cytoscape/network/load-file/file=/Users/mes/galFiltered.sif")
response = conn.getresponse()
print response.status, response.reason
data = response.read()
conn.close()
