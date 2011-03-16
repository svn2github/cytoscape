#!/usr/bin/env python

import NonRedSet1

class Array_regist:
    def __init__(self, num):
        self.regist = []
        for i in range(num):
            self.regist.append(NonRedSet1.NonRedSet())

    def __len__(self):
        return len(self.regist)

    def register(self, elem, pos):
        self.regist[pos].append(elem)

    def register_range(self, elem, pos1, pos2):
        for i in range(pos1, pos2 + 1):
            self.regist[i].append(elem)

    def get(self, pos):
        return self.regist[pos].ret_set()

    def display_all(self):
        for i in range(len(self.regist)):
            print i, self.regist[i].ret_set()

if __name__ == "__main__":

    reg = Array_regist(10)
    reg.register("Rintaro", 3)
    reg.register_range("Rintaro", 2, 6)
    reg.register_range("Rintaro", 3, 7)
    reg.register_range("Saito", 3, 7)
    reg.display_all()
    print reg.get(4)
