#!/usr/bin/env python

import re

comment_line1 = re.compile(r"(\n[^#\n]*) #.*")
comment_line2 = re.compile(r"(.*) is a (.*)")

after = comment_line2.sub(r"\1 are \2", """
This is a pen.
Hello---, ###
   # Hi!
This is a pen again.
## Here!
Bye!
Bye!!
""")

print after