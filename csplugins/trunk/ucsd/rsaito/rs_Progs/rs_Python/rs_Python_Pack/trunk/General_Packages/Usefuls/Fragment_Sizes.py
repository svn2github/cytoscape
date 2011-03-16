#!/usr/bin/env python

class Fragment_Sizes:
    def __init__(self, full_length, frag_length, extension_mode = None, begin = 0):
        """ extension_mode should be one of "First" or "Last" """
        
        if extension_mode not in (None, "First", "Last"):
            raise "Extension mode error (%s)" % (extension_mode)
        
        self.full_length = full_length
        self.frag_length = frag_length
        self.extension_mode = extension_mode
        self._begin = begin

    def __iter__(self):
        return self

    def next(self):
        if self._begin < self.full_length:
            self._end = self._begin + self.frag_length
            if (self.extension_mode == "Last" and 
                self._end + self.frag_length > self.full_length):
                self._end = self.full_length
            if self.extension_mode == "First":
                self._end += (self.full_length - self._begin) % self.frag_length
            if self._end > self.full_length:
                self._end = self.full_length
            ret = self._begin, self._end
            self._begin = self._end
            return ret
        else:
            raise StopIteration

    
if __name__ == "__main__":
    for r in Fragment_Sizes(100, 10, "Last", 10):
        print r
        