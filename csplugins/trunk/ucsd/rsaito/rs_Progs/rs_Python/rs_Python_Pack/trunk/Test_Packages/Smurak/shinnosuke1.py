#!/usr/bin/env python

import sys
import re

cds_cou = 0
aaa = 0
bbb = 0
ccc = 0
m = 0

match_cds = re.compile('^     CDS             (.*)')
match_brc = re.compile(r'\(([^()]+)\)')

try: # open(FILE0, $ARGV[0]) || die "Cannot open \"$ARGV[0]\": $!\n";
    FILE0 = open(sys.argv[1], "r")
except IOError :
    raise IOError, 'Cannot open "'+ sys.argv[1] + '"';

while True: # while(<FILE0>)
    line_r = FILE0.readline()
    if line_r == "": break
    line = line_r.rstrip() # chomp

    match_result = match_cds.match(line)
    if match_result:
        cds_cou = cds_cou +1
        cds_a = match_result.group(1)
        while '(' in cds_a and cds_a[-1] != ')':
            cds_a += FILE0.readline().rstrip()[21:]
        print cds_a
        match_result2 = match_brc.search(cds_a)
        if match_result2:
            cds = match_result2.group(1) 
        else:
            cds = cds_a
        exon_set = cds.split(",")

        for cds2 in exon_set:
            if ".." not in cds2: continue
            cds_start_pos, cds_stop_pos = cds2.split("..")
            if '<' in cds_start_pos or '>' in cds_start_pos: continue
            if '<' in cds_stop_pos or '>' in cds_stop_pos: continue
            cds_start, cds_stop = int(cds_start_pos), int(cds_stop_pos)
        
            if cds_start < cds_stop:
                cds_cal_a = cds_stop - cds_start +1

            if cds_start > cds_stop:
                cds_cal_a = cds_start - cds_stop +1

            cds_cal = cds_cal_a % 3;
            if cds_cal == 0:
                aaa = aaa +1

            if cds_cal == 1:
                bbb = bbb +1

            if cds_cal == 2:
                ccc = ccc +1

            if cds_cal_a < 0:
                m = m +1
        
print "cds = ", cds_cou
print "3n = ",  aaa
print "3n+1= ", bbb
print "3n+2= ", ccc

