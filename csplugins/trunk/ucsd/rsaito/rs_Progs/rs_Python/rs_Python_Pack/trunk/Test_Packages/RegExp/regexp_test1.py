#!/usr/bin/env python

import re

comment_line1_2          = re.compile(r"\n\s*#.*")

comment_line2_1          = re.compile(r"\n\s*#.*")
comment_line2_2          = re.compile(r"(\n[^#\n]*) #.*")

wash_seq_match1          = re.compile(r"[^a-zA-Z]")
wash_seq_match2_1        = re.compile(r"\n([^>].*)[^a-zA-Z]")

test_match               = re.compile(r">.*zzz.*")
test_match2              = re.compile(r"(>.*)zzz(.*)")

after1_1 = comment_line2_1.sub("\nX", """
This is a pen.
Hello---, ###
   # Hi!
This is a pen again.
## Here!
Bye!
Bye!!
""")

print after1_1

after1_2 = comment_line2_2.sub(r"\1Y", after1_1)

print after1_2

after2 = wash_seq_match1.sub("X", """
cagtagctg12gycgaygy32
cahcahy21wa
""")

print after2

print "------"

after3 = wash_seq_match2_1.sub(r"\1XXX", """
ca acgta acgt23ea
""")

print after3

print "*****"

after4 = test_match.sub(r"AAA", """
cagcagtgzzzcagtcgta
cagtzzzgd
>cagtgczzzahyavhy
acgtacgtzzzgcatgt
""")

print after4

after5 = test_match2.sub(r"\1AAA\2", """
cagcagtgzzzcagtcgta
cagtzzzgd
>cagtgczzzahyzzzavhy
acgtacgtzzzgcatgt
""")

print after5
