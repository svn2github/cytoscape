#!/usr/bin/env python

import Singleton

class Obj_Factory(Singleton.Singleton3):

    def __init__(self):
        if 'name2obj' not in vars(self):
            self.name2obj = {}
            self.set_classobj()

    def set_classobj(self):
        self.classobj = Klass
        # Class Klass must have appropriate constructor.

    def make(self, iname):
        if iname not in self.name2obj:
            self.name2obj[ iname ] = self.classobj(iname)
            # print iname, "newly produced as", self.name2obj[iname]
        return self.name2obj[ iname ]

class Klass:
    def __init__(self, iname): # Required constructor
        self.iname = iname
        # iname must be unique ID.

    def get_name(self):
        return self.iname


class Obj_Factory2(Singleton.Singleton3):

    def __init__(self):
        if 'name2obj' not in vars(self):
            self.name2obj = {}
            self.set_classobj()

    def set_classobj(self):
        self.classobj = Klass2
        # Class Klass must have appropriate constructor.

    def make(self, iname1, iname2):
        iname = (iname1, iname2)
        if iname not in self.name2obj:
            self.name2obj[ iname ] = self.classobj(iname1, iname2)
            # print iname, "newly produced as", self.name2obj[iname]
        return self.name2obj[ iname ]

class Klass2:
    def __init__(self, iname1, iname2): # Required constructor
        self.iname1 = iname1
        self.iname2 = iname2
        # iname must be unique ID.

    def get_name(self):
        return self.iname1 + "\t" + self.iname2

if __name__ == "__main__":

    factory1 = Obj_Factory()
    product1 = factory1.make("Galant")
    product2 = factory1.make("Lancer")
    product3 = factory1.make("GTO")
    print id(product1), product1.get_name() 
    print id(product2), product2.get_name() 
    print id(product3), product3.get_name() 

    factory2 = Obj_Factory()
    product1 = factory2.make("Galant")
    product2 = factory2.make("Lancer")
    product3 = factory2.make("GTO")
    print id(product1), product1.get_name() 
    print id(product2), product2.get_name() 
    print id(product3), product3.get_name() 

    print "#####"

    class Obj_Factory_test(Obj_Factory):
        def set_classobj(self):
            self.classobj = Klass_test

    class Klass_test(Klass):
        pass
   
    factory3 = Obj_Factory_test()
    product4 = factory3.make("Galant")
    product5 = factory3.make("Lancer")
    product6 = factory3.make("GTO")
    print id(product4), product4.get_name() 
    print id(product5), product5.get_name() 
    print id(product6), product6.get_name() 

    factory4 = Obj_Factory_test()
    product4 = factory4.make("Galant")
    product5 = factory4.make("Lancer")
    product6 = factory4.make("GTO")
    print id(product4), product4.get_name() 
    print id(product5), product5.get_name() 
    print id(product6), product6.get_name() 

    print "#####"

    factory5 = Obj_Factory2()
    product7 = factory5.make("A", "B")
    product8 = factory5.make("A", "B")
    product9 = factory5.make("B", "A")
    print id(product7), product7.get_name()
    print id(product8), product8.get_name()
    print id(product9), product9.get_name()

    factory6 = Obj_Factory2()
    product7 = factory6.make("A", "B")
    product8 = factory6.make("A", "B")
    product9 = factory6.make("B", "A")
    print id(product7), product7.get_name()
    print id(product8), product8.get_name()
    print id(product9), product9.get_name()
