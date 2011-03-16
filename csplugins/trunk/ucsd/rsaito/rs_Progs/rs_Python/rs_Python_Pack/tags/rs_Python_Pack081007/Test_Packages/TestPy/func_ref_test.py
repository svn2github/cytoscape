#!/usr/bin/env python

def print_test(arg):
    print arg

func_ref = print_test

if __name__ == '__main__':

    func_ref('Hello, world!')

