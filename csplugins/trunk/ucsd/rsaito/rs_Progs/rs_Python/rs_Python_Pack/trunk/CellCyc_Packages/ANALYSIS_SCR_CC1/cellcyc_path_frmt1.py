#!/usr/bin/env python

from Usefuls.Sheet_Analysis import Sheet_tab_header
import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

ccpath = Sheet_tab_header(rsc.Cell_cyc_path_mammal_descr)
ct = {}

while True:
    if ccpath.readline():
        substrate = ccpath.get_items_accord_hd(
            "Substrate 1",
            "Substrate 2")
        product = ccpath.get_items_accord_hd(
            "Product 1",
            "Product 2",
            "Product 3")
        substance = substrate + product

        for each in substance:
            if not each:
                continue

            for elem in each.split("_"):
                if elem in ("p",
                            "p1",
                            "p2",
                            "p3"):
                    continue
                if elem == "Chk1/2":
                    elems = [ "Chk1", "Chk2" ]
                elif elem == "Cdk4/6":
                    elems = [ "Cdk4", "Cdk6" ]
                elif elem == "p16/p15":
                    elems = [ "p16", "p15" ]
                elif elem == "p21/p27":
                    elems = [ "p21", "p27" ]
                else:
                    elems = [ elem ]

                
                for elem in elems:
                    ct[ elem ] = ct.get(elem, 0) + 1
        
    else:
        break
    
for elem in ct:
    print elem, ct[elem]
