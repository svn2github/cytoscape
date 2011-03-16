#!/usr/bin/env python

class TestClass:
    def recur(self, x):
        if x == 1:
            ret = 1;
        else:
            ret =  x * self.recur(x-1)
        
        print "x =", x, "ret=", ret

        return ret

if __name__ == "__main__":
    print TestClass().recur(4)
    