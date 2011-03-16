#!/usr/bin/env python

# Simple inheritance case
class super1 (object):
    def __new__(typ, *args, **kwargs):
        print "Class initialization:", typ, args, kwargs
        obj = object.__new__(typ, *args, **kwargs)
        obj.attr1 = []
        return obj # Without this, instances will always be "None".

class derived1(super1):
    def __init__(self, arg4, **kwargs):
        self.attr4 = arg4
        self.attr5 = kwargs['arg5']

if '__main__'==__name__:
    d1 = derived1(222, arg5=333)
    d1.attr1.append(111)
    print d1.attr1
    print d1.attr4, d1.attr5,
    print isinstance(d1, super1)
