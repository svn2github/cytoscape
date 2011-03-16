#!/usr/bin/env python

import SAT_Packages.SAT44K_Expr.file44k as file44k

f = file44k.Files()

f_all = f.get()
f_sub1 = f.kwget(['Mouse', 'Heart'])

for elem in f_sub1:
    print elem, elem.get_sample(), elem.get_file_name(), elem.get_priming(), \
          elem.get_tissue()
    
f_sub2 = f.kwget(['Human', 'Cancer'])

for elem in f_sub2:
    print elem, elem.get_sample(), elem.get_file_name(), elem.get_priming(), \
          elem.get_tissue_type()


    
    
