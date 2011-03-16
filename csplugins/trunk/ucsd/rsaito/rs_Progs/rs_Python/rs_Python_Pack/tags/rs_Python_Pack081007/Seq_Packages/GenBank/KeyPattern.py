#!/usr/bin/env python

import re

nucs_search  = re.compile(r'[^a-z]')
fea21_search = re.compile(r'^     (.+\S) +')
both_ends_d_search_simple = re.compile(r'^(\d+)\.\.(\d+)')
both_ends_c_search_simple = re.compile(r'^complement\((\d+)\.\.(\d+)\)')

key_locus  = "LOCUS"
key_acc    = "ACCESSION"
feastart   = "FEATURES             Location/Qualifiers"
feaspace5  = "     "
feaspace21 = "                     "
basecount  = "BASE COUNT"
origin     = "ORIGIN"
entry_end  = "//"
