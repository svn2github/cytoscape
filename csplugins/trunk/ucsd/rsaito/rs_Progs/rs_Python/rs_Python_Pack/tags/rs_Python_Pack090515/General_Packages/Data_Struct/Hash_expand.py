#!/usr/bin/env python

import Usefuls1

def Hash_expand1(ilist, ihashes):

    current_list = Usefuls1.list_to_dict(ilist)

    for ihash in ihashes:
        new_list = {}
        for elem in current_list.keys():
            if elem in ihash:
                conv_elem = ihash[ elem ]
                new_list[ conv_elem ] = ""
        current_list = new_list
    
    return new_list.keys()


if __name__ == "__main__":

    h1 = { "A" : "a",
           "B" : "b",
           "C" : "c",
           "D" : "d",
           "E" : "e",
           "F" : "f" }
    h2 = { "a" : 1,
           "b" : 2,
           "c" : 3,
           "d" : 4,
           "e" : 5,
           "f" : 6 }
    h3 = { 1: "x",
           2: "xx",
           3: "xxx",
           5: "xxxxx",
           6: "xxxxxx" }
    
    

    l = Hash_expand1(("B", "C", "D"), (h1, h2, h3))
    print l
    


