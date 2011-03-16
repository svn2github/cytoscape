#!/usr/bin/python

import string

class Table_row:
    def __init__(self):
        self.a = []
        self.count = 0
    
    def append(self, key, val):
        self.a.append([key, val])

    def return_header(self):
        header = []
        for elem in self.a:
            header.append(elem[0])
        return header

    def return_row(self):
        row = []
        for elem in self.a:
            row.append(elem[1])
        return row

    def clear(self):
        self.a = []

    def output(self, sep):
        if self.count == 0:
            print string.join(self.return_header(), sep)
        print string.join(self.return_row(), sep)
        self.count += 1
        
        

if __name__ == "__main__":
    
    tb = Table_row()

    tb.append("Name", "Rin")
    tb.append("Age", "33")
    tb.append("Birth", "Nov. 5, 1972")
    tb.output("\t")
    
    tb.clear()
    
    tb.append("Name", "Gen")
    tb.append("Age", "30")
    tb.append("Birth", "Nov. 10, 1975")
    tb.output("\t")
    
