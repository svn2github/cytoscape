#!/usr/bin/python

import sys

""" Save the following code as module CONST.py on some directory
on your Python sys.path: """

class _const(object):
    
    class ConstError(TypeError): pass
    
    def __setattr__(self, name, value):
        
        if name in self.__dict__:
            raise self.ConstError, "Can't rebind CONST (%s)" % name
        self.__dict__[name] = value
        
    def __delattr(self, name):
        
        if name in self.__dict__:
            raise self.ConstError, "Can't unbind CONST (%s)" % name
        raise NameError, name

if __name__ != "__main__":
    sys.modules[__name__] = _const()

"""
Now, any client code can import CONST, then bind an attribute
on the CONST module just once, as follows:

import Dir.CONST
# where Dir is a directory name.
# Dir.CONST becomes an instance by sys.modules[__name__] = _const()

Dir.CONST.magic = 23

Once the attribute is bound, the program cannot accidentally
rebind or unbind it:

Dir.CONST.magic = 88 # raises CONST.ConstError
del Dir.CONST.magic  # raises CONST.ConstError

"""

if __name__ == "__main__":
    CONST = _const()
    CONST.magic = 23

    print CONST.magic
    CONST.magic = 88

