#!/usr/bin/env python

import subprocess

p1 = subprocess.Popen(["echo", "Hello"], stdout = open("/tmp/tmphello1", "w"))
p2 = subprocess.Popen(["echo Hello"], shell = True, stdout = open("/tmp/tmphello2", "w"))
p3 = subprocess.Popen(["sleep", "5"])

print "Wait until all processes terminates."

# In Eclipse environment, this process waits until all sub-processes terminate.
