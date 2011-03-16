#!/usr/bin/env python

import re

comment_line3 = re.compile(r"(\n\s*)#.*|(\n[^#\n]+) #.*")
comment_line3_1 = re.compile(r"\n\s*#.*")
comment_line3_2 = re.compile(r"(\n[^#\n]+) #.*")

del_comment1 = comment_line3_1.sub(r"\nX", """
This is a first line (excluding the first blank line).
The comment will follow. # Here is a comment.
   ## Solely comment.
Not comment.
## Comment again.
DNA sequence catgctag#catgcat which contain mutation(s)
Bye!
Bye!!
""")

del_comment2 = comment_line3_2.sub(r"\1Y", del_comment1) # raw string symbol r is essential.

print del_comment2



