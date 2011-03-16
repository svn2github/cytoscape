#!/usr/bin/python

import sys
import string

class Sheet_tab:

    def __init__(self, filename):
	self.filename = filename
	self.ifile = open(filename, 'r')

    def read_line(self):
	line = self.ifile.readline()
	if line == "": return False
	line = string.rstrip(line)
	r = line.split("\t")
	return r

    def __del__(self):
        self.ifile.close()

class Sheet_Analysis:

    def __init__(self, filename, sep):
	self.filename = filename
	self.sep = sep
	self.ifile = open(filename, 'r')
	self.counter = 0
	self.max_items = 0
	self.blank = ""
	flag = True
	while flag == True:
	    line = self.ifile.readline()
	    if line != "":
		r = line.split(self.sep)
		if self.max_items < len(r):
		    self.max_items = len(r)
	    else:
		flag = False

	self.ifile.seek(0)

    def ret_counter(self):
	return self.counter

    def ret_max_items(self):
	return self.max_items

    def __del__(self):
        self.ifile.close()

    def readlines(self):
	flag = True
	while flag == True:
	    line = self.ifile.readline()
	    if line != "":
		line = string.rstrip(line)
		r = line.split(self.sep)
		if len(r) < self.ret_max_items():
		    for i in range(len(r),self.ret_max_items()):
			r.append(self.blank)
		self.analyze(r)
		self.counter += 1
	    else:
		flag = False

    def analyze(self, r):
	print self.ret_counter(), r


if __name__ == "__main__":

    import TmpFile

    tmp_obj = TmpFile.TmpFile_III("""

Sun Nichi a
Mon Getsu b irregular
Tue Kayob c
Wed Suiyo d


""")

    sf = Sheet_tab(tmp_obj.filename())
    while True:
	r = sf.read_line()
	if not r: break
	print r[1]

    sf2 = Sheet_Analysis(tmp_obj.filename(), "\t")
    sf2.readlines()
    print sf2.ret_max_items()
