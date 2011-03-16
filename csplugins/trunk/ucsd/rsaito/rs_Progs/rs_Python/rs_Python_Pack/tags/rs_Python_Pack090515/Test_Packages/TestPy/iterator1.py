#!/usr/bin/env python

class Foo:
    def __init__(self):
        self.count = 0
    def __iter__(self):
        return self
    def next(self):
        self.count += 1
        if self.count > 5:
            raise StopIteration
        return self.count

class ArrayTest:
    def __init__(self, ilist):
        self.ilist = ilist

    def __getitem__(self, i):
        return self.ilist[i]

class ArrayTest2:
    def __init__(self, ilist):
        self.ilist = ilist

    def __iter__(self):
        return self.ilist.__iter__()

foo = Foo()
for x in foo:
    print x
for x in foo:
    print x

foo2 = ArrayTest(["A", "B", "C"])
for x in foo2:
    print x
for x in foo2:
    print x

foo2 = ArrayTest2(["A", "B", "C"])
for x in foo2:
    print x
for x in foo2:
    print x

