#!/usr/bin/env python

class Redund_level:
    def __init__(self, ilist):

        self.ilist = ilist

    def _redund_check(self, item1, item2):

        if item1 == item2:
            return 1
        else:
            return 0

    def redund_level(self):
        
        redund_count = 0
        for item1 in self.ilist:
            for item2 in self.ilist:
                redund_count += self._redund_check(item1, item2)
            
        return 1.0*redund_count / len(self.ilist)

if __name__ == "__main__":
    rl = Redund_level(["A", "A", "A", "B", "A" ])
    print rl.redund_level()
