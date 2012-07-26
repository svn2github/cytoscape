#!/usr/bin/python
import httplib, urllib
params = urllib.urlencode({'data': 'file=/Users/mes/galFiltered.sif'})
headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
conn = httplib.HTTPConnection("127.0.0.1:2609")
conn.request("POST", "/cytoscape/network/load-file/", params, headers)
response = conn.getresponse()
print response.status, response.reason
data = response.read()
conn.close()
