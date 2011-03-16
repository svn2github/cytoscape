#!/usr/bin/python

import string

class Table_row:
    def __init__(self):
        self.a = {}
        self.order = []
        self.count = 0
    
    def append(self, key, val):
        if not key in self.a:
            self.order.append(key)
        self.a[key] = val
                
    def return_header(self):
        return self.order

    def return_row(self):
        row = []
        for key in self.order:
            row.append(self.a[key])
        return row

    def output(self, sep, limit = False):
        if self.count == 0:
            print string.join(self.return_header(), sep)
	if limit == False:
	    print string.join(self.return_row(), sep)
	else:
	    limit_col = self.return_header().index(limit)
	    print string.join(self.return_row()[:limit_col+1], sep)
	    
        self.count += 1

    def clear(self):
        for each_key in self.a:
            self.a[ each_key ] = ""

if __name__ == "__main__":
    
    tb = Table_row()

    tb.append("Name", "Rin")
    tb.append("Age", "33")
    tb.append("Birth", "Nov. 5, 1972")
    tb.output("\t")
    
    tb.append("Name", "Gen")
    tb.append("Age", "30")
    tb.append("Birth", "Nov. 10, 1975")
    tb.output("\t", "Age")
    tb.output("\t")

    tb.clear()
    print "Cleared"
    tb.output("\t")
