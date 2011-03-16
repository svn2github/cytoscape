#!/usr/bin/env python

class Hash_recorder:
    def __init__(self, func):
        self.func = func
        self.h = {}
        
    def get(self, *params):
        return self.__getitem__(params)
    
    def __getitem__(self, params):
        """ In principle, params must be a tuple. """
        if params in self.h:
            # print "Recorded"
            return self.h[ params ]
        
        self.h[ params ] = apply(self.func, params)
        return self.h[ params ]
    
if __name__ == "__main__":
    def plus(x, y):
        return x + y
    
    hr = Hash_recorder(plus)
    print hr[ (3, 2) ]
    print hr[ (3, 2) ] 
        