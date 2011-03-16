#!/usr/bin/env python

class Person:
    def __init__(self, name):
        self.name = name

    def get_name(self):
        return self.name

if __name__ == "__main__":
    person1 = Person("Rintaro")
    print person1.get_name()

    h = {}
    h[ person1 ] = "Saito"
    print h[ person1 ]
    print id(person1)

    person1.__init__("Rin")
    print person1.get_name()
    print h[ person1 ]
    print id(person1)
