#!/usr/bin/env python

import re

rslash_search = re.compile(r'//+')

def sdp(dirpath):
    """ Simplifies directory path """
    return rslash_search.sub('/', dirpath)

if __name__ == "__main__":
    print sdp("/a//b//c///")
    print sdp("/a/b///c/d")
    print sdp("a/b/c")