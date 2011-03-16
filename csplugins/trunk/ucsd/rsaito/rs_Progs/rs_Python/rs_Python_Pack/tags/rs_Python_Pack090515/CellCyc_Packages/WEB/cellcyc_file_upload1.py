#!/usr/bin/env python

import cgi
import os, sys

add_path = [ 
	"/home/rsaito/Work/Research/rs_Progs/rs_Python",
	"/home/rsaito/Work/Research/rs_Progs/rs_Python/rs_Python_Pack",
	"/home/rsaito/Work/Research/rs_Progs/rs_Python/rs_Python_Pack/General_Packages"
	]

for path in add_path:
	if path not in sys.path:
		sys.path.append(path)

os.environ['PYTHON_RS_CONFIG'] = '/home/rsaito/Work/Research/rs_Progs/rs_Python/rs_Python_Config'
os.environ['home'] = "/home/rsaito"
os.environ['HOME'] = "/home/rsaito"

from WEB.CGI_BasicI import cgi_html_out

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsCellCyc_Config")

html = open(rsc.Cell_cyc_path_html).read()

try:
    import msvcrt
    msvcrt.setmode(0, os.O_BINARY)
    msvcrt.setmode(1, os.O_BINARY)
except ImportError:
    pass

form = cgi.FieldStorage()
if form.has_key('cellcyc_file'):
    item = form['cellcyc_file']
    if item.file:
        fout = open(rsc.Cell_cyc_path_html_loaded, "w")
        for line in item.file:
        	fout.write(line.rstrip() + "\n")
        fout.close()

print cgi_html_out(html.replace("******", "<font color=#ff0000>File Uploaded</font>"))

