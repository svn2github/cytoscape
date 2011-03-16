#!/usr/bin/env python

# See also MultiDimDict class.

class Count1:
    def __init__(self):
        self.counter = 0
    def count_up(self):
        self.counter += 1
    def add_count(self, val):
        self.counter += val
    def get_counter(self):
        return self.counter

class Count2:
    def __init__(self):
        self.counter = {}     # Counting up more than 1 allowed.
        self.counter_one = {} # Counting only one by one.

    def increment(self, elem):
        if self.counter_one.has_key(elem):
            self.counter_one[ elem ] += 1
        else:
            self.counter_one[ elem ] = 1

    def count_up(self, elem): # Usually use this one.
        self.increment(elem)
        if self.counter.has_key(elem):
            self.counter[ elem ] += 1
        else:
            self.counter[ elem ] = 1
    
    def count_up_list(self, elems):
        map(self.count_up, elems)

    def add_count(self, elem, val):
        self.increment(elem)
        if self.counter.has_key(elem):
            self.counter[ elem ] += val
        else:
            self.counter[ elem ] = val


    def add_count_dict(self, idict):
        for key in idict:
            self.add_count(key, idict[key])

    def get_counter(self, elem):
        if self.counter.has_key(elem):
            return self.counter[ elem ]
        else:
            return 0

    def get_counter_one(self, elem):
        if self.counter_one.has_key(elem):
            return self.counter_one[ elem ]
        else:
            return 0

    def get_average(self, elem):
        if self.counter.has_key(elem):
            return 1.0 * self.counter[ elem ] / self.counter_one[ elem ]
        else:
            return False

    def get_elems(self):
        return self.counter.keys()

    def __len__(self):
        return len(self.get_elems())

    def get_max_count(self):
        if self.get_elems():
            return max(self.counter.values())
        else:
            return 0

    def get_max_key(self):
        ret = []
        max_c = 0
        for elem in self.get_elems():
            if self.get_counter(elem) > max_c:
                max_c = self.get_counter(elem)
                ret = [ elem ]
            elif self.get_counter(elem) == max_c:
                ret.append(elem)
        return ret


if __name__ == "__main__":
    ct1 = Count1()
    ct1.count_up()
    ct1.add_count(1.5)
    print ct1.get_counter()

    ct2 = Count2()
    ct2.count_up("Rin")
    ct2.add_count("Rin", 2.5)
    ct2.add_count("Gen", 2.5)
    ct2.count_up("Gen")
    print ct2.get_counter("Rin")
    print ct2.get_counter("Gen")
    print ct2.get_elems()

    add = { "Rin": 100, "Gen": 200 }
    ct2.add_count_dict(add)
    ct2.add_count("Rin", 1000)
    print ct2.get_counter("Rin")
    print ct2.get_counter("Gen")
    print ct2.get_average("Rin")

    ct3 = Count2()
    ct3.count_up_list(("Rin", "Gen", "Rin", "Rin", "Ryo", 
                       "Gen", "Gen", "Tetsu", "Tetsu", "Tetsu",
                       "Tetsu", "Tetsu"))
    print ct3.counter
    print ct3.get_max_count()
    print ct3.get_max_key()
    print len(ct3)
