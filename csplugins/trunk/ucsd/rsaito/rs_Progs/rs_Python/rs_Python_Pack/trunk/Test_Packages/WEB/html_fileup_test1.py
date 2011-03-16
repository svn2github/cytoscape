#!/usr/bin/python
# -*- coding: utf-8 -*-
'''
File uploading
'''
html = '''Content-Type: text/html

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
  <title>File Uploading</title>
</head>
<body>
<h1>File Uploading</h1>
<p>%s</p>
<form action="test15.cgi" method="post" enctype="multipart/form-data">
  <input type="file" name="file" />
  <input type="submit" />
</form>
</body>
</html>
'''

import cgi
import os, sys

""" Uploads file in binary mode. This works only on Windows. """
try:
    import msvcrt
    msvcrt.setmode(0, os.O_BINARY)
    msvcrt.setmode(1, os.O_BINARY)
except ImportError:
    pass

result = ''
form = cgi.FieldStorage()
if form.has_key('file'):
    item = form['file']
    if item.file:
        fout = file(os.path.join('/tmp', item.filename), 'wb')
        while True:
            chunk = item.file.read(1000000)
            if not chunk:
                break
            fout.write(chunk)
        fout.close()
        result = 'Uploaded successfully'

print html % result
