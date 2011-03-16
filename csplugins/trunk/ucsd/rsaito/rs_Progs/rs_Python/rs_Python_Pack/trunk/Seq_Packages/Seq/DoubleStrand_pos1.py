#!/usr/bin/env python

class dsDNA_pos:
    """ Starts from position 0 """
    def __init__(self, size):
        self.size = size
    
    def set_plus_pos(self, pos_p):
        self.pos_p = pos_p
        
    def set_minus_pos(self, pos_m):
        self.pos_p = self.size - pos_m - 1
    
    def posp(self):
        return self.pos_p
    
    def posm(self):
        return self.size - self.pos_p - 1
    
class dsDNA_range:
    """ Range (a, b) --> a to b - 1 """
    def __init__(self, size):
        self.dsDNA_pos1 = dsDNA_pos(size)
        self.dsDNA_pos2 = dsDNA_pos(size)
        
    def set_plus_range(self, pos_p1, pos_p2):
        """ pos_p1 < pos_p2 """
        self.dsDNA_pos1.set_plus_pos(pos_p1)
        self.dsDNA_pos2.set_plus_pos(pos_p2-1)
        
    def set_minus_range(self, pos_m1, pos_m2):
        """ pos_m1 < pos_m2 """
        self.dsDNA_pos1.set_minus_pos(pos_m2-1)
        self.dsDNA_pos2.set_minus_pos(pos_m1)     
    
    def rangep(self):
        return (self.dsDNA_pos1.posp(),
                self.dsDNA_pos2.posp() + 1)
        
    def rangem(self):
        return (self.dsDNA_pos2.posm(),
                self.dsDNA_pos1.posm() + 1)
    
    
if __name__ == "__main__":
    dsdnapos = dsDNA_pos(10)
    dsdnapos.set_plus_pos(3)
    print dsdnapos.posp()
    print dsdnapos.posm()    
    
    dsdnarng =dsDNA_range(10)
    dsdnarng.set_plus_range(3, 5)
    print dsdnarng.rangep()
    dsdnarng.set_plus_range(3, 8)
    print dsdnarng.rangem()
    dsdnarng.set_minus_range(2, 7)
    print dsdnarng.rangep()
    