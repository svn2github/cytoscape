#!/usr/bin/env python

class Revolver:
    def __init__(self, array):
	# array must be a list, not tuple.
	self.content = array
	self.pointer = -1

    def current(self):
        return self.content[ self.pointer ]

    def forward(self):
	self.pointer += 1
	if self.pointer >= len(self.content):
	    self.pointer = 0
	return self.current()

    def backward(self):
	self.pointer -= 1
	if self.pointer < 0:
	    self.pointer = len(self.content) - 1
	return self.current()

    def get_content(self):
	return self.content

    def search(self, item):
	try:
	    pointer = self.get_content().index(item)
	    self.pointer = pointer
	    return self.current()
	except ValueError:
	    return False

if __name__ == "__main__":
    r = Revolver(["Aoyagi", "Ikeda", "Ueda", "Saito"])
    print r.forward()
    print r.forward()
    print r.forward()
    print r.forward()
    print r.forward()
    print r.forward()
    print r.backward()
    print r.backward()
    print r.backward()
    print r.backward()
    print r.backward()
    print r.backward()
    print r.search("Ikeda")
    print r.search("Rintaro")
