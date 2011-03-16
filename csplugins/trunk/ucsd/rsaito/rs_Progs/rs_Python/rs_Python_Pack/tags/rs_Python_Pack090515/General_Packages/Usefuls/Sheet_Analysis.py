#!/usr/bin/python

import sys
import string

class Sheet_tab:

    def __init__(self, filename):
	self.filename = filename
	self.ifile = open(filename, 'r')
        self.line_counter = 0

    def read_line(self):
	line = self.ifile.readline()
	if line == "": return False
        self.line_counter += 1
	line = string.rstrip(line)
	r = line.split("\t")
	return r

    def get_line_counter(self):
        return self.line_counter

    def __del__(self):
        self.ifile.close()

class Sheet_tab_header:

    def __init__(self, filename):
	self.filename = filename
	self.__ifile = open(filename, 'r')
        header = self.readline()
        self.hdlabel_to_hdno = {}
        for i in range(len(header)):
            label = header[i]
            self.hdlabel_to_hdno[label] = i
        self.current = None

    def readline(self): # This must be only the method to readline.
	line = self.__ifile.readline()
	if line == "": return False
	line = string.rstrip(line)
	r = line.split("\t")
        self.current = r
	return r

    def get_items_accord_hd(self, *hdls):
        ret = []
        for hdlabel in hdls:
            nth = self.hdlabel_to_hdno[hdlabel]
            if nth < len(self.current):
                ret.append(self.current[nth])
            else:
                ret.append(None)
        return ret

    def __del__(self):
        self.__ifile.close()


class Sheet_Analysis:

    def __init__(self, filename, sep, numerize_flag = False):
	self.filename = filename
	self.sep = sep
        self.numerize_flag = numerize_flag
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
                if self.numerize_flag:
                    tmp_r = []
                    for num in r:
                        if num == self.blank:
                            tmp_r.append(None)
                        else:
                            tmp_r.append(float(num))
                    r = tmp_r
		self.analyze(r)
		self.counter += 1
	    else:
		flag = False

    def analyze(self, r): # Implement your analysis method here.
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


    tmp_obj_num = TmpFile.TmpFile_III("""

1.2 -0.5 1.2 5.3
2.1 1.3 1.2
3.2 -1.2 0.9

""")

    sf3 = Sheet_Analysis(tmp_obj_num.filename(), "\t", True)
    sf3.readlines()
    
    
    tmp_obj2 = TmpFile.TmpFile_III("""

ENG JPN ALPHA COMMENT
Sun Nichi a
Mon Getsu b irregular
Tue Kayob c
Wed Suiyo d


""")

    sf3 = Sheet_tab_header(tmp_obj2.filename())
    while True:
        if sf3.readline():
            print sf3.get_items_accord_hd("ENG", "JPN", "COMMENT")
        else:
            break

