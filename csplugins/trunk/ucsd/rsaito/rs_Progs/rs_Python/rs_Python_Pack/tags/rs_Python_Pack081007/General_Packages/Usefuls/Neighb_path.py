#!/usr/bin/env python

import os

def upper_dir(n):
    cwd = os.getcwd()
    if cwd[-1] == "/":
        cwd = cwd[:-1]
    dirs = cwd.split("/")
    if n == 0:
        n = -len(dirs)
    return dirs[:-n]

if __name__ == "__main__":
    print upper_dir(0)
    print upper_dir(1)
    print upper_dir(2)
