#!/usr/bin/env python

def ListList_Pick(listlist, pos):
    ret = []
    for ilist in listlist:
        try:
            ret.append(ilist[pos])
        except IndexError:
            pass

    return ret

if __name__ == "__main__":
    
    listlist = [
        ["A", "B", "C"],
        ["D", "E"],
        ["F", "G"],
        ["H", "I", "J", "K" ]
        ]

    print ListList_Pick(listlist, -3)
