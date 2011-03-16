#!/usr/bin/env python

import sys
import os

sys.path += [ 
	"/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python",
	"/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Pack",
	"/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Pack/General_Packages"
	]

os.environ['PYTHON_RS_CONFIG'] = '/Users/rsaito/UNIX/Work/Research/rs_Progs/rs_Python/rs_Python_Config'
os.environ['home'] = "/Users/rsaito"
os.environ['HOME'] = "/Users/rsaito"

import cgi
import Script_Packages.Lec_MolBiol.BioBasic_AutoEMail1 as AutoEMail

form = cgi.FieldStorage()

AutoEMail.set_MAIL_Command("/usr/bin/mail")

AutoEMail.auto_email(input_ID = form['SID'].value)
# AutoEMail.auto_email(input_ID = "79151932")
