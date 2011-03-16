#!/usr/bin/python

class Table_row:
    def __init__(self, filename = None):
        self.a = {}
        self.order = []
        self.count = 0
        if filename:
            self.fh = open(filename, "w")
        else:
            self.fh = None
    
    def append(self, key, val = None):
        if not key in self.a:
            self.order.append(key)
        self.a[key] = val
                
    def return_header(self):
        return self.order

    def get_header_idx(self, idx):
        return self.return_header().index(idx)

    def return_row(self):
        row = []
        for key in self.order:
            row.append(self.a[key])
        return row

    def output(self, sep, limit = False):
        if self.count == 0:
            self.out(sep.join(self.return_header()))
        if limit is False:
            self.out(sep.join(self.return_row()))
        else:
            limit_col = self.return_header().index(limit)
            self.out(sep.join(self.return_row()[:limit_col+1]))
    
        self.count += 1
        
    def record(self, limit = False):
        """ What to do with this record may be defined in
        subclasses. """

        if 'rec' not in vars(self):
            self.rec = []
        
        if limit is False:
            self.rec.append(self.return_row())
        else:
            limit_col = self.return_header().index(limit)
            self.rec.append(self.return_row()[:limit_col+1])                  
    
    def get_record(self):
        return self.rec

    def get_record_str(self, sep = "\t", limit = False):

        rt = []

        if limit is False:
            rt.append(sep.join(self.return_header()))
            for rec in self.rec:
                rt.append(sep.join(rec))
        else:
            limit_col = self.return_header().index(limit)
            rt.append(sep.join(self.return_header()[:limit_col+1]))
            for rec in self.rec:
                rt.append(sep.join(rec[:limit_col+1]))        

        return "\n".join(rt)

    
    def output_record(self, sep):
        self.out(sep.join(self.return_header()))
        for row in self.rec:
            self.out(sep.join(row))
        
    def out(self, o_string):
        if self.fh:
            self.fh.write(o_string + "\n")
        else:
            print o_string

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
    print tb.get_header_idx("Age")
    print tb.get_header_idx("Birth")

    tb.clear()
    print "Cleared"
    tb.output("\t")
    
    tb = Table_row("/tmp/testout")
    tb.append("Name", "Rin")
    tb.append("Age", "33")
    tb.append("Birth", "Nov. 5, 1972")
    tb.output("\t")
    tb.append("Name", "Gen")
    tb.append("Age", "30")
    tb.append("Birth", "Nov. 10, 1975")
    tb.output("\t", "Age")
    tb.output("\t")
    
    tb = Table_row()

    tb.append("Name", "Rin")
    tb.append("Age", "33")
    tb.append("Birth", "Nov. 5, 1972")
    tb.record(limit = "Age")
    
    tb.append("Name", "Gen")
    tb.append("Age", "30")
    tb.append("Birth", "Nov. 10, 1975")
    tb.record()
    
    print tb.get_header_idx("Age")
    print tb.get_header_idx("Birth")
    print tb.rec
    print "---"
    print tb.get_record_str("\t")
    print "***"
    print tb.get_record_str("\t", "Age")
