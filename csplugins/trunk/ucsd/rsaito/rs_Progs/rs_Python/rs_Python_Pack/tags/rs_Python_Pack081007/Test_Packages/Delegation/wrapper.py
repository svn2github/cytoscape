#!/usr/bin/env python

class wrapper:
    def __init__(self, object):
        self.wrapped = object
    def __getattr__(self, attrname):
        print "Trace:", attrname
        print "Attribute:", getattr(self.wrapped, attrname)
        return getattr(self.wrapped, attrname)
    
if __name__ == "__main__":
    x = wrapper([1,2,3])
    x.append(4)
    print x.wrapped
    
    x = wrapper({"a":1, "b":2})
    print x.keys()
    print x["b"]
    x["c"] = 3
    