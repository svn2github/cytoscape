#!/usr/bin/env python

import socket

print socket.gethostname()

import datetime

curr = datetime.datetime.today()

print curr

print curr.year, curr.month, curr.day
print curr.hour, curr.minute, curr.second, curr.microsecond

import os

print os.getpid()