#!/usr/bin/env python

class Combi_iterator:
    def __init__(self, ilists):
        self.ilists = ilists
        self.reset()

    def reset(self):
        self.pointer = []
        for i in range(len(self.ilists)):
            self.pointer.append(0)
        self.pointer[0] = -1

    def current(self):
        ret = []
        for i in range(len(self.ilists)):
            ret.append(self.ilists[i][ self.pointer[i] ])
        return ret

    def next(self):
        i = 0
        while i < len(self.ilists):
            self.pointer[i] += 1
            if self.pointer[i] < len(self.ilists[i]):
                break
            self.pointer[i] = 0
            i += 1
        if i == len(self.ilists):
            self.reset()
            return False
        return True
            
            
if __name__ == "__main__":
    ci = Combi_iterator([["a", "b"], [1,2,3], ["A", "B"]])
    
    while ci.next():
        print ci.current()

    print "Second time ..."
    while ci.next():
        print ci.current()

    print "Manual mode ..."
    ci.next()
    print ci.current()
    ci.next()
    print ci.current()
    ci.next()
    print ci.current()
    print "Resetting ..."
    ci.reset()
    ci.next()
    print ci.current()
    ci.next()
    print ci.current()
    ci.next()
    print ci.current()

