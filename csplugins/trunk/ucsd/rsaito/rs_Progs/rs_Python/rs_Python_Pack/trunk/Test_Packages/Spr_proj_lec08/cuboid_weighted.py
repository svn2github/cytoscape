#!/usr/bin/env python

import cuboid

class Cuboid_weighted(cuboid.Cuboid):
    def __init__(self, l, w, d, weight):
        cuboid.Cuboid.__init__(self, l, w, d)
        self.weight = weight
        
    def density(self):
        volume = self.volume()
        return 1.0 * self.weight / volume
    
if __name__ == "__main__":
    cuboid_weighted = Cuboid_weighted(10, 20, 30, 1200)
    print cuboid_weighted.volume()
    print cuboid_weighted.density()

