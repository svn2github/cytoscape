#!/usr/bin/env python

import string

class DictKey_Iterator:
    def __init__(self, i_dict):
        
        self.i_dict = i_dict
        self.stock = False

    def current_key(self):
        """ [[ "a1", "a2" ], [ "b1", "b2", "b3" ], [ "c1", "c2" ]]
        ---> [ "a1", "b1", "c1" ] """
        
        return map(lambda l: l[0], self.stock)

    def current_value(self):
        """
        h = { "A": { "a1": 1, "a2": 2, "a3":{ "aa1": 3, "aa2": 4 }},
        "B": { "b1": 5 },
        "C": { "c1": 6, "c2": { "cc1":7, "cc2": { "ccc1": 8 }}}}

        self.stock is [ "A", "a1" ] ---> 1
        self.stock is [ "C", "c2" ] ---> { "cc1":7, "cc2": { "ccc1": 8 }}
        """

        cur_key = self.current_key()
        p = self.i_dict
        for k in cur_key:
            p = p[k]
        return p

    def calc_stock(self, input_dict):
        """
        h = { "A": { "a1": 1, "a2": 2, "a3":{ "aa1": 3, "aa2": 4 }},
        "B": { "b1": 5 },
        "C": { "c1": 6, "c2": { "cc1":7, "cc2": { "ccc1": 8 }}}}

        input_dict is h["A"] -- (order depends on dictionary) -->
        [["a3", "a1", "a2" ], [ "aa1", "aa2" ]]
        
        """
        
        p = input_dict
        stock = []
        while type(p) == dict:
            k = p.keys()
            stock.append(k)
            p = p[ k[0] ]

        return stock

    def next(self):

        if self.stock == False:
            self.stock = self.calc_stock(self.i_dict)
            return True

        stock_p = len(self.stock) - 1

        while stock_p >= 0:
            self.stock[ stock_p ] = self.stock[ stock_p ][1:]
            if self.stock[ stock_p ] <> []:
                break
            self.stock = self.stock[:-1]
            stock_p -= 1

        if stock_p < 0:
            return False

        cur_p = self.current_value()
        self.stock += self.calc_stock(cur_p)

        return True

    def Squash_Dict(self):
	self.stock = False
	h_squash = {}
	while self.next():
	    k = self.current_key()
	    v = self.current_value()
	    k_squash = string.join(k, "\t")
	    h_squash[ k_squash ] = v
	return h_squash

if __name__ == "__main__":

    h = { "A": { "a1": 1, "a2": 2, "a3":{ "aa1": 3, "aa2": 4 }},
          "B": { "b1": 5 },
          "C": { "c1": 6, "c2": { "cc1":7, "cc2": { "ccc1": 8 }}}}


    h = { "A": "aaa",
          "B": "bbb",
          "C": "ccc" }

    dki = DictKey_Iterator(h)

    while dki.next():
        print dki.current_key(), dki.current_value()

    print dki.Squash_Dict()
