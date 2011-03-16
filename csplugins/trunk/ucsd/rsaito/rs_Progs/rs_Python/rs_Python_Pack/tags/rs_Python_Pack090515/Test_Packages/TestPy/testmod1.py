#!/usr/bin/env python

def add_exclamation(istr):
    return istr + "!"

class TestClass:
    def set_hello(self):
        self.str1 = "Hello"
    def strout(self):
        return self.str1
    def add_exclamation(self):
        self.str1 = add_exclamation(self.str1)

if __name__ == "__main__":
    tc = TestClass()
    tc.set_hello()
    tc.add_exclamation()
    tc.add_exclamation()
    tc.add_exclamation()
    print tc.strout()

