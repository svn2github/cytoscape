#!/usr/bin/env python

import os

os.execlp("ls", "-l")

print "This line will not be executed."
