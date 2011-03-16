#!/usr/bin/python

class Hello:
    def __init__(self):
	print "Hello"
    def display(self):
	print "Hi!!"
    def display2(self):
	self.display()

if __name__ == "__main__":
    obj = Hello()
    obj.display2()

