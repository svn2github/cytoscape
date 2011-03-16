#!/usr/bin/env python

import sys

rec = {}
rec_max_match = {}

for filename in sys.argv[1:]:
    for line in open(filename):
        if not line[0].isdigit():
            continue
        r = line.rstrip().split('\t')
        id = r[9]
        match = int(r[0])
        if id not in rec or rec_max_match[id] < match:
            rec[ id ] = line.rstrip()
            rec_max_match[ id ] = match

for id in rec:
    print rec[id]
    