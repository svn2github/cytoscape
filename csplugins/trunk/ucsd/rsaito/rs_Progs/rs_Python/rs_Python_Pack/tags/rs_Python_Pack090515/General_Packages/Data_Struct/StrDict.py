#!/usr/bin/env python

class StrDict:
    def __init__(self):
        self.h = {}

    def append_dict(self, ikey, ielem):
        if ikey in self.h:
            self.h[ ikey ] += ielem
        else:
            self.h[ ikey ] = ielem

    def store_dict(self, ikey, ielem):
        self.h[ ikey ] = ielem

    def get_elem(self, ikey):
        if ikey in self.h:
            return self.h[ ikey ]
        else:
            return False

    def get_all(self):
        return self.h

    def delete(self, ikey):
        del self.h[ikey]

if __name__ == "__main__":
    str1 = StrDict()
    str1.append_dict("A", "Rin")
    str1.append_dict("AA", "Rintaro\n")
    str1.append_dict("AA", "Saito")
    # str1.store_dict("AA", "RS")

    print str1.get_elem("AA")
    # str1.delete("AA")
    print str1.get_all()
    
