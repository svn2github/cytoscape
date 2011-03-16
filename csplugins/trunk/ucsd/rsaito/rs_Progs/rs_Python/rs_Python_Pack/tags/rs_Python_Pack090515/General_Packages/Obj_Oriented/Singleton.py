#!/usr/bin/env python

class Singleton1:
    __single_instance = None
    def __init__( self ):
        if Singleton1.__single_instance:
            raise Singleton1.__single_instance
        Singleton1.__single_instance = self    


class Singleton2:
    """ A python singleton.
This implementation hides the singleton interface in an inner class
and creates exactly one instance of the inner class. The outer class
is a handle to the inner class and delegates any requests to it. While
the id() of the handle objects changes, the id() of the inner class
which implements the singleton behaviour is constant.

Of course, the inner class is not REALLY hidden, like anything in
Python. But you have to invest extra effort to break into the
singleton.

This is related to the "Automatic delegation as an alternative to
inheritance" recipe.

[1] Gamma, Helm, et al, "Design Patterns - Elements of Reusable
Object-Oriented Software". Addison-Wesley, 1995, ISBN 0-201-63361-2.
"""
    class __impl:
        """ Implementation of the singleton interface """

        def spam(self):
            """ Test method, return singleton id """
            return id(self)

    # storage for the instance reference
    __instance = None

    def __init__(self):
        """ Create singleton instance """
        # Check whether we already have an instance
        if Singleton2.__instance is None:
            # Create and remember instance
            Singleton2.__instance = Singleton2.__impl()

        # Store instance reference as the only member in the handle
        # Remember that "self._Singleton2__instance = Singleton2.__instance"
        # will be intercepted by __setattr__
        self.__dict__['_Singleton2__instance'] = Singleton2.__instance
        # self.name = "Rintaro"
        # print self.__dict__
        # print Singleton2.__instance.__dict__

    def __getattr__(self, attr):
        """ Delegate access to implementation """
        return getattr(self.__instance, attr)

    def __setattr__(self, attr, value):
        """ Delegate access to implementation """
        return setattr(self.__instance, attr, value)


class Singleton3(object):
    """ A Pythonic Singleton
    Inheritance from "object" is new feature. Function "super"
    can only be used with instance inherited from "object".
    """
    def __new__(cls, *args, **kwargs):
        if '_inst' not in vars(cls):
            # print "Instance initialization ..."
            cls._inst = super(Singleton3, cls).__new__(cls, *args, **kwargs)
            # Above is particularly useful when inheritance occurs
            # cls._inst = object.__new__(cls, *args, **kwargs)
        return cls._inst


if __name__ == "__main__":
    
    ### Test Code for Singleton1 ###
    single1 = Singleton1()
    print "Instance 1 made."
    # single2 = Singleton1()
    # print "Instance 2 made."


    ### Test Code for Singleton2 ###
    # Test it
    s1 = Singleton2()
    print id(s1), s1.spam()

    s2 = Singleton2()
    print id(s2), s2.spam()

# Sample output, the second (inner) id is constant:
# 8172684 8176268
# 8168588 8176268

    ### Test Code for Singleton3 ###
    
    class SingleTest(Singleton3):
        def __init__(self, s):
            print "Instance initialization in derived."
            self.s = s
        def __str__(self):
            return self.s

    s1 = SingleTest("ABC")
    print id(s1), s1
    s2 = SingleTest("DEF")
    print id(s2), s2
    print id(s1), s1


