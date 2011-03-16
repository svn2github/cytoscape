#!/usr/bin/env python

class Cuboid:
    def __init__(self, l, w, d):
        self.length = l
        self.width  = w
        self.depth  = d
        
    def volume(self):
        return self.length * self.width * self.depth
    
    
if __name__ == "__main__":
    cuboid = Cuboid(10, 20, 30)
    print cuboid.volume()
    